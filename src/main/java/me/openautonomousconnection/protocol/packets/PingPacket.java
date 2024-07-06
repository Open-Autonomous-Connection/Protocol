package me.openautonomousconnection.protocol.packets;

import me.finn.libraries.eventsystem.EventManager;
import me.finn.libraries.eventsystem.events.Event;
import me.finn.libraries.networksystem.packets.Packet;
import me.openautonomousconnection.protocol.APIInformation;
import me.openautonomousconnection.protocol.domain.Domain;
import me.openautonomousconnection.protocol.domain.RequestDomain;
import org.antlr.v4.runtime.misc.NotNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class PingPacket extends Packet implements Serializable {
    private boolean isRequest;
    private boolean isReachable;
    private RequestDomain requestDomain;
    private Domain responseDomain;
    private APIInformation apiInformation;
    private int id;

    public PingPacket() {
        super(1);
    }

    public PingPacket(@NotNull int id, @NotNull boolean isRequest, @Nullable boolean isReachable, @Nullable RequestDomain requestDomain, @Nullable Domain responseDomain, @NotNull APIInformation apiInformation) {
        this();

        this.id = id;
        this.isRequest = isRequest;
        this.isReachable = isReachable;
        this.requestDomain = requestDomain;
        this.responseDomain = responseDomain;
        this.apiInformation = apiInformation;
    }


    @Override
    public void write(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(id);
        objectOutputStream.writeBoolean(isRequest);
        objectOutputStream.writeBoolean(isReachable);
        objectOutputStream.writeObject(isRequest ? requestDomain : responseDomain);
        objectOutputStream.writeObject(apiInformation);
    }

    @Override
    public void read(ObjectInputStream objectInputStream) throws IOException {
        id = objectInputStream.readInt();
        isRequest = objectInputStream.readBoolean();
        isReachable = objectInputStream.readBoolean();

        try {
            if (isRequest) requestDomain = (RequestDomain) objectInputStream.readObject();
            else responseDomain = (Domain) objectInputStream.readObject();

            apiInformation = (APIInformation) objectInputStream.readObject();
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
            return;
        }

        EventManager.executeEvent(new PingPacketReceiveEvent(id, isRequest, isReachable, requestDomain, responseDomain, apiInformation));
    }

    public class PingPacketReceiveEvent extends Event {
        public final boolean isRequest;
        public final boolean isReachable;
        public final RequestDomain requestDomain;
        public final Domain responseDomain;
        public final APIInformation apiInformation;
        public final int id;

        public PingPacketReceiveEvent(int id, boolean isRequest, boolean isReachable, RequestDomain requestDomain, Domain responseDomain, APIInformation apiInformation) {
            this.isRequest = isRequest;
            this.id = id;
            this.isReachable = isReachable;
            this.requestDomain = requestDomain;
            this.responseDomain = responseDomain;
            this.apiInformation = apiInformation;
        }
    }
}
