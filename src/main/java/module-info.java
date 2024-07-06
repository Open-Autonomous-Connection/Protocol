module me.openautonomousconnection.protocol {
    requires json.simple;
    requires networksystem;
    requires eventsystem;
    requires org.checkerframework.checker.qual;
    requires antlr4.runtime;
    exports me.openautonomousconnection.protocol.domain;
    exports me.openautonomousconnection.protocol;
    exports me.openautonomousconnection.protocol.packets;
}