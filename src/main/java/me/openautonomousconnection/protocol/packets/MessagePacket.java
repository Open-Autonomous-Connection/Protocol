/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.protocol.packets;

import me.finn.libraries.eventsystem.EventManager;
import me.finn.libraries.eventsystem.events.Event;
import me.finn.libraries.networksystem.packets.Packet;
import org.antlr.v4.runtime.misc.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class MessagePacket extends Packet implements Serializable {

    private String message;
    private int id;

    public MessagePacket() {
        super(4);
    }

    public MessagePacket(@NotNull int id, @NotNull String message) {
        this();

        this.message = message;
        this.id = id;
    }

    @Override
    public void write(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(id);
        objectOutputStream.writeUTF(message);
    }

    @Override
    public void read(ObjectInputStream objectInputStream) throws IOException {
        id = objectInputStream.readInt();
        message = objectInputStream.readUTF();
        EventManager.executeEvent(new MessagePacketReceiveEvent(id, message));
    }

    public class MessagePacketReceiveEvent extends Event {
        public final String message;
        public final int id;

        public MessagePacketReceiveEvent(int id, String message) {
            this.message = message;
            this.id = id;
        }
    }
}
