/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.protocol.packets.v1_0_0;

import me.finn.unlegitlibrary.network.system.packets.Packet;
import me.openautonomousconnection.protocol.ProtocolBridge;
import me.openautonomousconnection.protocol.ProtocolVersion;
import me.openautonomousconnection.protocol.domain.Domain;
import me.openautonomousconnection.protocol.domain.RequestDomain;
import me.openautonomousconnection.protocol.events.v1_0_0.DomainPacketReceivedEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;

public class DomainPacket extends Packet {
    private int clientID;
    private RequestDomain requestDomain;
    private Domain domain;
    private ProtocolVersion protocolVersion;

    public DomainPacket(RequestDomain requestDomain, Domain domain) {
        this();

        this.requestDomain = requestDomain;
        this.domain = domain;
    }

    public DomainPacket() {
        super(2);
    }

    @Override
    public void write(ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        protocolVersion = ProtocolBridge.getInstance().getProtocolVersion();

        if (ProtocolBridge.getInstance().isRunningAsServer()) {
            objectOutputStream.writeInt(clientID);
            objectOutputStream.writeObject(requestDomain);
            objectOutputStream.writeObject(domain);
        } else {
            clientID = ProtocolBridge.getInstance().getProtocolClient().getClient().getClientID();
            objectOutputStream.writeInt(clientID);
            objectOutputStream.writeObject(requestDomain);
        }

        objectOutputStream.writeObject(protocolVersion);
    }

    @Override
    public void read(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        if (ProtocolBridge.getInstance().isRunningAsServer()) {
            clientID = objectInputStream.readInt();
            requestDomain = (RequestDomain) objectInputStream.readObject();
            protocolVersion = (ProtocolVersion) objectInputStream.readObject();

            try {
                domain = ProtocolBridge.getInstance().getProtocolServer().getDomain(requestDomain);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            ProtocolBridge.getInstance().getProtocolServer().getServer().getEventManager().executeEvent(new DomainPacketReceivedEvent(protocolVersion, domain, requestDomain, clientID));
            ProtocolBridge.getInstance().getProtocolServer().getServer().getClientHandlerByID(clientID).sendPacket(new DomainPacket(requestDomain, domain));
        } else {
            clientID = objectInputStream.readInt();
            requestDomain = (RequestDomain) objectInputStream.readObject();
            domain = (Domain) objectInputStream.readObject();
            protocolVersion = (ProtocolVersion) objectInputStream.readObject();

            ProtocolBridge.getInstance().getProtocolClient().getClient().getEventManager().executeEvent(new DomainPacketReceivedEvent(protocolVersion, domain, requestDomain, clientID));
        }
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
