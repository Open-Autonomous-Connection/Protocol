package me.openautonomousconnection.protocol.packets;

import me.finn.libraries.eventsystem.EventManager;
import me.finn.libraries.eventsystem.events.Event;
import me.finn.libraries.networksystem.packets.Packet;
import me.openautonomousconnection.protocol.APIInformation;
import me.openautonomousconnection.protocol.RequestType;
import me.openautonomousconnection.protocol.domain.Domain;
import me.openautonomousconnection.protocol.domain.RequestDomain;
import org.antlr.v4.runtime.misc.NotNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class DomainPacket extends Packet implements Serializable {
    private RequestType requestType;
    private Domain domain;
    private RequestDomain requestDomain;
    private String accessKey;
    private APIInformation apiInformation;
    private int id;

    public DomainPacket() {
        super(3);
    }

    public DomainPacket(@NotNull int id, @NotNull RequestType requestType, @Nullable Domain domain, @Nullable RequestDomain requestDomain, @Nullable String accessKey, @NotNull APIInformation apiInformation) {
        this();

        this.requestType = requestType;
        this.domain = domain;
        this.requestDomain = requestDomain;
        this.accessKey = accessKey;
        this.apiInformation = apiInformation;
        this.id = id;
    }


    @Override
    public void write(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(requestType);
        objectOutputStream.writeInt(id);

        if (requestType == RequestType.REMOVE || requestType == RequestType.REGISTER) {
            objectOutputStream.writeObject(domain);
            objectOutputStream.writeObject(accessKey);
            objectOutputStream.writeObject(apiInformation);
        } else if (requestType == RequestType.UPDATE) {
            new DomainPacket(id, RequestType.REMOVE, domain, requestDomain, accessKey, apiInformation);
            new DomainPacket(id, RequestType.REGISTER, domain, requestDomain, accessKey, apiInformation);
        } else if (requestType == RequestType.INFO) {
            new PingPacket(id, true, false, requestDomain, domain, apiInformation).write(objectOutputStream);
            return;
        } else if (requestType == RequestType.EXISTS) {
            objectOutputStream.writeObject(requestDomain);
            objectOutputStream.writeObject(domain);
            objectOutputStream.writeObject(apiInformation);
        }
    }

    @Override
    public void read(ObjectInputStream objectInputStream) throws IOException {
        try {
            requestType = (RequestType) objectInputStream.readObject();
            id = objectInputStream.readInt();

            if (requestType == RequestType.REMOVE || requestType == RequestType.REGISTER) {
                domain = (Domain) objectInputStream.readObject();
                accessKey = objectInputStream.readUTF();
                apiInformation = (APIInformation) objectInputStream.readObject();
            } else if (requestType == RequestType.EXISTS) {
                requestDomain = (RequestDomain) objectInputStream.readObject();
                domain = (Domain) objectInputStream.readObject();
                apiInformation = (APIInformation) objectInputStream.readObject();
            }
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
            return;
        }

        EventManager.executeEvent(new DomainPacketReceiveEvent(id, requestType, domain, requestDomain, accessKey, apiInformation));
    }

    public class DomainPacketReceiveEvent extends Event {
        public final RequestType requestType;
        public final Domain domain;
        public final RequestDomain requestDomain;
        public final String accessKey;
        public final APIInformation apiInformation;
        public final int id;

        public DomainPacketReceiveEvent(int id, RequestType requestType, Domain domain, RequestDomain requestDomain, String accessKey, APIInformation apiInformation) {
            this.id = id;
            this.requestType = requestType;
            this.domain = domain;
            this.requestDomain = requestDomain;
            this.accessKey = accessKey;
            this.apiInformation = apiInformation;
        }
    }
}
