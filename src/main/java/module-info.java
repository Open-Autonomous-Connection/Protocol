module me.openautonomousconnection.protocol {
    requires json.simple;
    requires org.checkerframework.checker.qual;
    requires antlr4.runtime;
    requires unlegitlibrary;
    requires java.sql;
    exports me.openautonomousconnection.protocol.domain;
    exports me.openautonomousconnection.protocol;
    exports me.openautonomousconnection.protocol.utils;
    exports me.openautonomousconnection.protocol.side;
}