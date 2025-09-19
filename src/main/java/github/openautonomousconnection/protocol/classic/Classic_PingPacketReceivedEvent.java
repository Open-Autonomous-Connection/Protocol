package github.openautonomousconnection.protocol.classic;

import me.finn.unlegitlibrary.event.impl.Event;

public class Classic_PingPacketReceivedEvent extends Event {

    public final Classic_ProtocolVersion protocolVersion;
    public final Classic_Domain domain;
    public final Classic_RequestDomain requestDomain;
    public final boolean reachable;
    public final int clientID;

    public Classic_PingPacketReceivedEvent(Classic_ProtocolVersion protocolVersion, Classic_Domain domain, Classic_RequestDomain requestDomain, boolean reachable, int clientID) {
        this.protocolVersion = protocolVersion;
        this.domain = domain;
        this.requestDomain = requestDomain;
        this.reachable = reachable;
        this.clientID = clientID;
    }

    @Override
    protected final Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public final String toString() {
        return super.toString();
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }
}