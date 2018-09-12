module.exports = function(sails) {

    var hook = this;
    var IB = Java.type('com.test.IgniteBridge');
    var igniteBridge;
    var caches = {};

    var defaults = {
        connection: {
            addresses: '127.0.0.1:10800'
        },
        caches: [
            {
                name: 'default'
            }
        ]
    };

    function connect() {
        hook.igniteBridge = new IB({addresses: addresses});
    }

    function initCaches() {
        try {
            for (cacheCfg in sales.config.ignite.caches) {
                if (cacheCfg.name) {
                    var cache = hook.igniteBridge.getOrCreateCache(cacheCfg.name)
                }
            }
        } catch(e) {
            sails.log.error()
        }
    }

    function subscribe(topic, callack) {}

    function unsubscribe(topic) {}

    return {
        defaults: hook.defaults,
        initialize: function(cb) {
            sails.log.debug('Initializing Apache Ignite Bridge...');
            hook.connect();
            hook.initCaches();
            return cb();
        },
        ignite: hook.ignite,
        subscribe: subscribe,
        unsubscribe: unsubscribe
    }


};