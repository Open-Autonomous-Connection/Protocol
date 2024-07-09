/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.protocol.events.v1_0_0;

import me.finn.unlegitlibrary.event.impl.Event;
import me.finn.unlegitlibrary.network.system.packets.Packet;
import me.openautonomousconnection.protocol.ProtocolBridge;
import me.openautonomousconnection.protocol.ProtocolVersion;
import me.openautonomousconnection.protocol.domain.Domain;
import me.openautonomousconnection.protocol.domain.RequestDomain;
import me.openautonomousconnection.protocol.packets.v1_0_0.DomainPacket;

public class DomainPacketReceivedEvent extends Event {

    public final ProtocolVersion protocolVersion;
    public final Domain domain;
    public final RequestDomain requestDomain;
    public final int clientID;

    public DomainPacketReceivedEvent(ProtocolVersion protocolVersion, Domain domain, RequestDomain requestDomain, int clientID) {
        this.protocolVersion = protocolVersion;
        this.domain = domain;
        this.requestDomain = requestDomain;
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
