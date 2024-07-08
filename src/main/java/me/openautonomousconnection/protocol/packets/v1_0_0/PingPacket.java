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
import me.openautonomousconnection.protocol.events.v1_0_0.PingPacketReceivedEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;

public class PingPacket extends Packet {
    private RequestDomain requestDomain;
    private Domain domain;
    private int clientID;
    private boolean reachable;
    private ProtocolVersion protocolVersion;

    public PingPacket(RequestDomain requestDomain, Domain domain, boolean reachable) {
        this();

        this.requestDomain = requestDomain;
        this.domain = domain;
        this.reachable = reachable;
    }

    public PingPacket() {
        super(1);
    }

    @Override
    public void write(ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        protocolVersion = ProtocolBridge.getInstance().getProtocolVersion();

        if (ProtocolBridge.getInstance().isRunningAsServer()) {
            objectOutputStream.writeInt(clientID);
            objectOutputStream.writeObject(requestDomain);
            objectOutputStream.writeObject(domain);
            objectOutputStream.writeBoolean(reachable);
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
                domain = ProtocolBridge.getInstance().getProtocolServer().ping(requestDomain);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            reachable = domain != null;
            ProtocolBridge.getInstance().getProtocolServer().getServer().getClientHandlerByID(clientID).sendPacket(new PingPacket(requestDomain, domain, reachable));
        } else {
            clientID = objectInputStream.readInt();
            requestDomain = (RequestDomain) objectInputStream.readObject();
            domain = (Domain) objectInputStream.readObject();
            boolean reachable = objectInputStream.readBoolean();
            protocolVersion = (ProtocolVersion) objectInputStream.readObject();

            if (clientID != ProtocolBridge.getInstance().getProtocolClient().getClient().getClientID()) return;
            ProtocolBridge.getInstance().getProtocolClient().getClient().getEventManager().executeEvent(new PingPacketReceivedEvent(protocolVersion, domain, requestDomain, reachable));
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
