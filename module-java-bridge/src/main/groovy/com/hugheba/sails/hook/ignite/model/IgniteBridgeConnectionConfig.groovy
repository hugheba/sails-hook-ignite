package com.hugheba.sails.hook.ignite.model

class IgniteBridgeConnectionConfig {
    IgniteBridgeTcpDiscoveryIpFinder ipFinder
    List<String> addresses
    String multicastGroup
}

enum IgniteBridgeTcpDiscoveryIpFinder {
    TcpDiscoveryMulticastIpFinder, TcpDiscoveryVmIpFinder
}

