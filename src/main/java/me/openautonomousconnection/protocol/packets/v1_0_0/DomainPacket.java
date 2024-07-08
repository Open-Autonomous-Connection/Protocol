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
    private ProtocolBridge protocolBridge;
    private ProtocolVersion protocolVersion;

    public DomainPacket(ProtocolBridge protocolBridge, RequestDomain requestDomain, Domain domain) {
        this();

        this.requestDomain = requestDomain;
        this.domain = domain;
        this.protocolBridge = protocolBridge;
    }

    public DomainPacket() {
        super(2);
    }

    @Override
    public void write(ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        protocolVersion = protocolBridge.getProtocolVersion();

        if (protocolBridge.isRunningAsServer()) {
            objectOutputStream.writeObject(domain);
            objectOutputStream.writeInt(clientID);
            objectOutputStream.writeObject(requestDomain);
        } else {
            clientID = protocolBridge.getProtocolClient().getClient().getClientID();
            objectOutputStream.writeInt(clientID);
            objectOutputStream.writeObject(requestDomain);
        }

        objectOutputStream.writeObject(protocolVersion);
    }

    @Override
    public void read(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        if (protocolBridge.isRunningAsServer()) {
            clientID = objectInputStream.readInt();
            requestDomain = (RequestDomain) objectInputStream.readObject();
            protocolVersion = (ProtocolVersion) objectInputStream.readObject();

            try {
                domain = protocolBridge.getProtocolServer().getDomain(requestDomain);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            protocolBridge.getProtocolServer().getServer().getClientHandlerByID(clientID).sendPacket(new DomainPacket(protocolBridge, requestDomain, domain));
        } else {
            clientID = objectInputStream.readInt();
            requestDomain = (RequestDomain) objectInputStream.readObject();
            domain = (Domain) objectInputStream.readObject();
            protocolVersion = (ProtocolVersion) objectInputStream.readObject();

            if (clientID != protocolBridge.getProtocolClient().getClient().getClientID()) return;
            protocolBridge.getProtocolClient().getClient().getEventManager().executeEvent(new DomainPacketReceivedEvent(protocolBridge, protocolVersion, domain, requestDomain));
        }
    }
}