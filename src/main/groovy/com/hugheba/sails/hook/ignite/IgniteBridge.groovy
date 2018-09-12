package com.hugheba.sails.hook.ignite

import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache
import org.apache.ignite.IgniteMessaging
import org.apache.ignite.Ignition
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.lang.IgniteBiPredicate
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder
import org.graalvm.polyglot.Value


class IgniteBridge {

    Ignite ignite
    IgniteMessaging eb
    Value jsEventListener

    class EBEventListener implements IgniteBiPredicate<UUID, String> {
        @Override
        boolean apply(UUID uuid, String msg) {
            passEvent(msg)

            true
        }
    }

    EBEventListener myEventListener = new EBEventListener()

    void passEvent(String msg) {
        if (jsEventListener!=null) (jsEventListener).executeVoid(msg)
    }

    IgniteBridge(String addresses) {
        IgniteConfiguration cfg = new IgniteConfiguration(
                discoSpi: new TcpDiscoverySpi(
                        ipFinder: new TcpDiscoveryVmIpFinder(
                                addresses: ["127.0.0.1:47500..47509"]
                        )
                )
        )
        ignite = Ignition.start(cfg)

        eb = ignite.message()
    }

    IgniteCache getOrCreateCache(String cacheName) {
        ignite.getOrCreateCache(cacheName)
    }

    void listen(String topic, Value jsEventListener) {
        setJsEventListener(jsEventListener)
        eb.localListen(topic, myEventListener)
    }

    void broadcast(String topic, String message) {
        eb.sendOrdered(topic, message, 1000)
    }


}
