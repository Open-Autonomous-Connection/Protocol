package me.openautonomousconnection.protocol.packets;

import me.finn.libraries.eventsystem.EventManager;
import me.finn.libraries.eventsystem.events.Event;
import me.finn.libraries.networksystem.packets.Packet;
import me.openautonomousconnection.protocol.APIInformation;
import me.openautonomousconnection.protocol.RequestType;
import org.antlr.v4.runtime.misc.NotNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class TopLevelDomainPacket extends Packet implements Serializable {
    private RequestType requestType;
    private String topLevelDomain;
    private String newTopLevelDomain;
    private String accessKey;
    private APIInformation apiInformation;
    private int id;

    public TopLevelDomainPacket() {
        super(2);
    }

    public TopLevelDomainPacket(@NotNull int id, @NotNull RequestType requestType, @NotNull String topLevelDomain, @Nullable String newTopLevelDomain, @Nullable String accessKey, @NotNull APIInformation apiInformation) {
        this();

        this.id = id;
        this.requestType = requestType;
        this.topLevelDomain = topLevelDomain;
        this.accessKey = accessKey;
        this.apiInformation = apiInformation;
    }


    @Override
    public void write(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(id);
        objectOutputStream.writeObject(requestType);

        if (requestType == RequestType.REMOVE || requestType == RequestType.REGISTER) {
            objectOutputStream.writeObject(topLevelDomain);
            objectOutputStream.writeObject(accessKey);
            objectOutputStream.writeObject(apiInformation);
        } else if (requestType == RequestType.UPDATE) {
            new TopLevelDomainPacket(id, RequestType.REMOVE, topLevelDomain, newTopLevelDomain, accessKey, apiInformation);
            new TopLevelDomainPacket(id, RequestType.REGISTER, newTopLevelDomain, topLevelDomain, accessKey, apiInformation);
            return;
        } else if (requestType == RequestType.EXISTS || requestType == RequestType.INFO) {
            objectOutputStream.writeObject(topLevelDomain);
            objectOutputStream.writeObject(apiInformation);
        }
    }

    @Override
    public void read(ObjectInputStream objectInputStream) throws IOException {

        try {
            id = objectInputStream.readInt();
            requestType = (RequestType) objectInputStream.readObject();

            if (requestType == RequestType.REMOVE || requestType == RequestType.REGISTER) {
                topLevelDomain = objectInputStream.readUTF();
                accessKey = objectInputStream.readUTF();
                apiInformation = (APIInformation) objectInputStream.readObject();
            } else if (requestType == RequestType.EXISTS) {
                topLevelDomain = objectInputStream.readUTF();
                apiInformation = (APIInformation) objectInputStream.readObject();
            }
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
            return;
        }

        EventManager.executeEvent(new TopLevelDomainPacketReceiveEvent(id, requestType, topLevelDomain, newTopLevelDomain, accessKey, apiInformation));
    }

    public class TopLevelDomainPacketReceiveEvent extends Event {
        public final RequestType requestType;
        public final String topLevelDomain;
        public final String newTopLevelDomain;
        public final String accessKey;
        public final APIInformation apiInformation;
        public final int id;

        public TopLevelDomainPacketReceiveEvent(int id, RequestType requestType, String topLevelDomain, String newTopLevelDomain, String accessKey, APIInformation apiInformation) {
            this.requestType = requestType;
            this.id = id;
            this.topLevelDomain = topLevelDomain;
            this.newTopLevelDomain = newTopLevelDomain;
            this.accessKey = accessKey;
            this.apiInformation = apiInformation;
        }
    }
}
