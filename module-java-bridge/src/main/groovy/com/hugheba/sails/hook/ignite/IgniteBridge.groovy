package com.hugheba.sails.hook.ignite

import com.google.gson.Gson
import com.hugheba.sails.hook.ignite.exception.IgniteBridgeConfigurationException
import com.hugheba.sails.hook.ignite.model.IgniteBridgeConfiguration
import com.hugheba.sails.hook.ignite.model.IgniteBridgeConnectionConfig
import com.hugheba.sails.hook.ignite.model.IgniteBridgeTcpDiscoveryIpFinder
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache
import org.apache.ignite.IgniteMessaging
import org.apache.ignite.Ignition
import org.apache.ignite.configuration.CacheConfiguration
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.lang.IgniteBiPredicate
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinder
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder
import org.graalvm.polyglot.Value


class IgniteBridge {

    IgniteBridgeConfiguration config
    Ignite ignite
    IgniteMessaging eb
    Value jsEventListener
    Map<String, EBEventListener> listeners = [:]

    class EBEventListener implements IgniteBiPredicate<UUID, String> {

        Value jsEventListener

        EBEventListener(Value jsEventListener) {
            setJsEventListener(jsEventListener)
        }

        @Override
        boolean apply(UUID uuid, String msg) {
            if (jsEventListener!=null) (jsEventListener).executeVoid(msg)

            true
        }
    }

    IgniteBridge(Value config) {
        this.config = new Gson().fromJson(config.as(String) as String, IgniteBridgeConfiguration) as IgniteBridgeConfiguration
        IgniteConfiguration cfg = new IgniteConfiguration(
                discoverySpi: new TcpDiscoverySpi(ipFinder: buildIpFinder()),
                cacheCfg: buildCaches()
        )
        ignite = Ignition.start(cfg)

        eb = ignite.message()
    }

    private TcpDiscoveryIpFinder buildIpFinder() {
        TcpDiscoveryIpFinder ipFinder
        IgniteBridgeConnectionConfig connCfg = this.config.connection
        switch(connCfg.ipFinder) {
            case {it == IgniteBridgeTcpDiscoveryIpFinder.TcpDiscoveryVmIpFinder}:
                if (!connCfg.addresses || !connCfg.multicastGroup) {
                    throw new IgniteBridgeConfigurationException(
                            "Missing config.connection.addresses for ${IgniteBridgeTcpDiscoveryIpFinder.TcpDiscoveryVmIpFinder.name()}"
                    )
                }
                ipFinder = new TcpDiscoveryVmIpFinder(addresses: connCfg.addresses)
                break
//            case {it == IgniteBridgeTcpDiscoveryIpFinder.TcpDiscoveryMulticastIpFinder}:
            default:
                if (!connCfg.addresses || !connCfg.multicastGroup) {
                    throw new IgniteBridgeConfigurationException(
                            "Missing either config.connection.addresses or config.connection.multicastGroup for ${IgniteBridgeTcpDiscoveryIpFinder.TcpDiscoveryMulticastIpFinder.name()}"
                    )
                }
                ipFinder = new TcpDiscoveryMulticastIpFinder()
                if (connCfg.multicastGroup) ipFinder.multicastGroup = connCfg.multicastGroup
                if (connCfg.addresses) ipFinder.addresses = connCfg.addresses
                break
        }

        ipFinder
    }

    private CacheConfiguration[] buildCaches() {
        this.config.caches.collect {
            def cache = new CacheConfiguration<String, String>(name: it.name, cacheMode: it.cacheMode)
        } as CacheConfiguration[]
    }

    IgniteCache getOrCreateCache(String cacheName) {
        ignite.getOrCreateCache(cacheName)
    }

    void subscribe(String topic, Value jsEventListener) {
        listeners[topic] = new EBEventListener(jsEventListener)
        eb.localListen(topic, listeners[topic])
    }

    void unsubscribe(String topic, Value jsEventListener) {
        eb.stopLocalListen(topic, listeners[topic])
    }

    void broadcast(String topic, String message) {
        eb.sendOrdered(topic, message, 1000)
    }


}
