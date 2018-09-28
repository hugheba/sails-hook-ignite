var IgniteBridge = require('hugheba-graaljs-ignite');

module.exports = function ignite (sails) {

    var igniteBridge;

    return {
        defaults: {
            ignite: {
                connection: {
                    ipFinder: 'TcpDiscoveryMulticastIpFinder',
                    addresses: ['127.0.0.1:47500..47509'],
                    multicastGroup: '228.10.10.157',
                },
                caches: {'default' : {cacheMode: 'PARTITIONED' }},
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
        getEventBus: function () { return igniteBridge.getEventBus() },
        getCache: function (cacheName) { return igniteBridge.getCache(cacheName); },
        getRecord: function (recordName) { return igniteBridge.getRecord(recordName); },
        getCounter: function (counterName) { return igniteBridge.getCounter(counterName); },
    }

};
