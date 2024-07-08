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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MessagePacket extends Packet {
    private ProtocolBridge protocolBridge;
    private ProtocolVersion protocolVersion;
    private String message;
    private int clientID;

    public MessagePacket(ProtocolBridge protocolBridge, int id, String message) {
        super(id);

        this.protocolBridge = protocolBridge;
        this.message = message;
    }

    public MessagePacket() {
        super(3);
    }

    @Override
    public void write(ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        protocolVersion = protocolBridge.getProtocolVersion();

        if (protocolBridge.isRunningAsServer()) objectOutputStream.writeInt(clientID);
        else {
            clientID = protocolBridge.getProtocolClient().getClient().getClientID();
            objectOutputStream.writeInt(clientID);
        }

        objectOutputStream.writeUTF(message);
        objectOutputStream.writeObject(protocolVersion);
    }

    @Override
    public void read(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        if (protocolBridge.isRunningAsServer()) {
            int clientID = objectInputStream.readInt();
            String message = objectInputStream.readUTF();
            protocolVersion = (ProtocolVersion) objectInputStream.readObject();

            protocolBridge.getProtocolServer().handleMessage(clientID, message);
        } else {
            int clientID = objectInputStream.readInt();
            String message = objectInputStream.readUTF();
            protocolVersion = (ProtocolVersion) objectInputStream.readObject();

            if (clientID != protocolBridge.getProtocolClient().getClient().getClientID()) return;
            protocolBridge.getProtocolClient().handleMessage(message);
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
