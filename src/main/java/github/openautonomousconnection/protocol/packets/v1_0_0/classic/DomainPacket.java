package github.openautonomousconnection.protocol.packets.v1_0_0.classic;

import github.openautonomousconnection.protocol.ProtocolBridge;
import github.openautonomousconnection.protocol.ProtocolVersion;
import github.openautonomousconnection.protocol.classic.Classic_Domain;
import github.openautonomousconnection.protocol.classic.Classic_DomainPacketReceivedEvent;
import github.openautonomousconnection.protocol.classic.Classic_ProtocolVersion;
import github.openautonomousconnection.protocol.classic.Classic_RequestDomain;
import github.openautonomousconnection.protocol.packets.OACPacket;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;

// ProtocolVersion 1.0.0-CLASSIC is ProtocolSide Server only
public class DomainPacket extends OACPacket {
    private Classic_RequestDomain requestDomain;
    private Classic_Domain domain;
    private int clientID;

    public DomainPacket(ProtocolBridge protocolBridge, int toClient, Classic_RequestDomain requestDomain, Classic_Domain domain) {
        super(2, ProtocolVersion.ProtocolType.CLASSIC, protocolBridge);
        this.clientID = toClient;
        this.requestDomain = requestDomain;
        this.domain = domain;
    }

    @Override
    public void write(PacketHandler packetHandler, ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        objectOutputStream.writeInt(clientID);
        objectOutputStream.writeObject(requestDomain);
        objectOutputStream.writeObject(domain);

        objectOutputStream.writeObject(Classic_ProtocolVersion.PV_1_0_0);
    }

    @Override
    public void read(PacketHandler packetHandler, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        clientID = objectInputStream.readInt();
        requestDomain = (Classic_RequestDomain) objectInputStream.readObject();
        Classic_ProtocolVersion protocolVersion = (Classic_ProtocolVersion) objectInputStream.readObject();

        try {
            domain = getProtocolBridge().getClassicHandlerServer().getDomain(requestDomain);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        getProtocolBridge().getProtocolServer().getNetworkServer().getEventManager().executeEvent(new Classic_DomainPacketReceivedEvent(protocolVersion, domain, requestDomain, clientID));
        getProtocolBridge().getProtocolServer().getNetworkServer().getConnectionHandlerByID(clientID).sendPacket(new DomainPacket(getProtocolBridge(), clientID, requestDomain, domain));
    }
}
