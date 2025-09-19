package github.openautonomousconnection.protocol.packets.v1_0_0.classic;

import github.openautonomousconnection.protocol.ProtocolBridge;
import github.openautonomousconnection.protocol.ProtocolVersion;
import github.openautonomousconnection.protocol.classic.Classic_Domain;
import github.openautonomousconnection.protocol.classic.Classic_PingPacketReceivedEvent;
import github.openautonomousconnection.protocol.classic.Classic_ProtocolVersion;
import github.openautonomousconnection.protocol.classic.Classic_RequestDomain;
import github.openautonomousconnection.protocol.packets.OACPacket;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;

public class PingPacket extends OACPacket {
    private Classic_RequestDomain requestDomain;
    private Classic_Domain domain;
    private int clientID;
    private boolean reachable;
    private Classic_ProtocolVersion protocolVersion;

    public PingPacket(ProtocolBridge protocolBridge, Classic_RequestDomain requestDomain, Classic_Domain domain, boolean reachable) {
        super(1, ProtocolVersion.ProtocolType.CLASSIC, protocolBridge);

        this.requestDomain = requestDomain;
        this.domain = domain;
        this.reachable = reachable;
        this.protocolVersion = Classic_ProtocolVersion.PV_1_0_0;
    }

    @Override
    public void write(PacketHandler packetHandler, ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        objectOutputStream.writeInt(clientID);
        objectOutputStream.writeObject(requestDomain);
        objectOutputStream.writeObject(domain);
        objectOutputStream.writeBoolean(reachable);
        objectOutputStream.writeObject(protocolVersion);
    }

    @Override
    public void read(PacketHandler packetHandler, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        clientID = objectInputStream.readInt();
        requestDomain = (Classic_RequestDomain) objectInputStream.readObject();
        protocolVersion = (Classic_ProtocolVersion) objectInputStream.readObject();

        try {
            domain = getProtocolBridge().getClassicHandlerServer().ping(requestDomain);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        reachable = domain != null;
        getProtocolBridge().getProtocolServer().getNetworkServer().getEventManager().executeEvent(new Classic_PingPacketReceivedEvent(protocolVersion, domain, requestDomain, reachable, clientID));
        getProtocolBridge().getProtocolServer().getNetworkServer().getConnectionHandlerByID(clientID).sendPacket(new PingPacket(getProtocolBridge(), requestDomain, domain, reachable));
    }
}
