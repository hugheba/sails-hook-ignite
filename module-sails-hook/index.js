
class IgniteBridge {

    constructor(config) {
        this.config = config;
        this.caches = {};
        this.connect();
        this.initCaches();
    }

    connect() {
        const IB = Java.type('com.hugheba.sails.hook.ignite.IgniteBridge');
        this.javaBridge = new IB(JSON.stringify(this.config));
    }

    initCaches() {
        var me = this;
        try {
            var configCaches = this.config.caches;
            configCaches.forEach( function(cache) {
                if (cache.name) {
                    me.caches[cache.name] = me.javaBridge.getOrCreateCache(cache.name);
                }
            });
        } catch(e) {
            console.error('Unable to load Ignite caches!', e);
        }
    }

    getIgnite() {
        return this.javaBridge.ignite;
    }

    getCaches() {
        return this.caches;
    }

    subscribe(topic, callback) {
        this.javaBridge.subscribe(topic, callback);
    }

    unsubscribe(topic, callback) {
        this.javaBridge.unsubscribe(topic, callback);
    }

    broadcast(topic, message) {
        this.javaBridge.broadcast(topic, JSON.stringify(message));
    }
}

module.exports = function ignite (sails) {

    var igniteBridge;

    return {
        defaults: {
            ignite: {
                connection: {
                    ipFinder: 'TcpDiscoveryMulticastIpFinder', // See https://apacheignite.readme.io/docs/tcpip-discovery
                    addresses: ['127.0.0.1:47500..47509'],
                    multicastGroup: '228.10.10.157', // Used only for discovery: 'TcpDiscoveryMulticastIpFinder'
                },
                caches: [
                    {
                        name: 'default',
                        cacheMode: 'PARTITIONED' // Options are: LOCAL, PARTITIONED or REPLICATED, see https://apacheignite.readme.io/docs/cache-modes
                    },
                ]
            }
        },
        initialize: function(cb) {
            sails.log.debug('Initializing Apache Ignite Bridge...');
            var config = sails.config.ignite;
            sails.log.debug(`Ignite config : ${JSON.stringify(config)}`);
            igniteBridge = new IgniteBridge(config);
            return cb();
        },
        getIgnite: function () { return igniteBridge.getIgnite(); },
        getCaches: function () { return igniteBridge.getCaches(); },
        subscribe: function (topic, callback) {
            if (igniteBridge && topic && callback) {
                igniteBridge.subscribe(`${topic}`, callback);
            }
        },
        unsubscribe: function (topic, callback) {
            if (igniteBridge && topic && callback) {
                igniteBridge.unsubscribe(`${topic}`, callback);
            }
        },
        broadcast: function (topic, message) {
            if (igniteBridge && topic && message) {
                igniteBridge.broadcast(`${topic}`, message);
            }
        }
    }

};
