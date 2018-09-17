package com.hugheba.sails.hook.ignite

import org.graalvm.polyglot.Value

class ListenerContainer {
    IgniteBridge.EBEventListener ebEventListener
    Value jsEventListener
}
