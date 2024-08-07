/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.protocol.packets.v1_0_0;

import me.finn.unlegitlibrary.network.system.packets.Packet;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;
import me.openautonomousconnection.protocol.ProtocolBridge;
import me.openautonomousconnection.protocol.ProtocolVersion;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MessagePacket extends Packet {
    private ProtocolVersion protocolVersion;
    private String message;
    private int clientID;

    public MessagePacket(int id, String message) {
        super(id);

        this.message = message;
    }

    public MessagePacket() {
        super(3);
    }

    @Override
    public void write(PacketHandler packetHandler, ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        protocolVersion = ProtocolBridge.getInstance().getProtocolVersion();

        if (ProtocolBridge.getInstance().isRunningAsServer()) objectOutputStream.writeInt(clientID);
        else {
            clientID = ProtocolBridge.getInstance().getProtocolClient().getClient().getClientID();
            objectOutputStream.writeInt(clientID);
        }

        objectOutputStream.writeUTF(message);
        objectOutputStream.writeObject(protocolVersion);
    }

    @Override
    public void read(PacketHandler packetHandler, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        if (ProtocolBridge.getInstance().isRunningAsServer()) {
            int clientID = objectInputStream.readInt();
            String message = objectInputStream.readUTF();
            protocolVersion = (ProtocolVersion) objectInputStream.readObject();

            ProtocolBridge.getInstance().getProtocolServer().handleMessage(clientID, message);
        } else {
            int clientID = objectInputStream.readInt();
            String message = objectInputStream.readUTF();
            protocolVersion = (ProtocolVersion) objectInputStream.readObject();

            ProtocolBridge.getInstance().getProtocolClient().handleMessage(message);
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
