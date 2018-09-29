var IgniteBridge = require('hugheba-graaljs-ignite');

module.exports = function ignite (sails) {

    var igniteBridge;

    return {
        defaults: {
            __configKey__: {
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
        get: function() {
            return igniteBridge;
        }
    }

};
