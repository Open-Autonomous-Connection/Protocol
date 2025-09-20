package github.openautonomousconnection.protocol.packets.v1_0_0.classic;

import github.openautonomousconnection.protocol.ProtocolBridge;
import github.openautonomousconnection.protocol.packets.v1_0_0.beta.UnsupportedClassicPacket;
import github.openautonomousconnection.protocol.versions.ProtocolVersion;
import github.openautonomousconnection.protocol.versions.v1_0_0.classic.Classic_Domain;
import github.openautonomousconnection.protocol.versions.v1_0_0.classic.Classic_DomainPacketReceivedEvent;
import github.openautonomousconnection.protocol.versions.v1_0_0.classic.Classic_ProtocolVersion;
import github.openautonomousconnection.protocol.versions.v1_0_0.classic.Classic_RequestDomain;
import github.openautonomousconnection.protocol.packets.OACPacket;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;

// ProtocolVersion 1.0.0-CLASSIC is ProtocolSide Server only
public class Classic_DomainPacket extends OACPacket {
    private Classic_RequestDomain requestDomain;
    private Classic_Domain domain;
    private int clientID;

    public Classic_DomainPacket(int toClient, Classic_RequestDomain requestDomain, Classic_Domain domain) {
        this();
        this.clientID = toClient;

        this.requestDomain = requestDomain;
        this.domain = domain;
    }

    public Classic_DomainPacket() {
        super(2, ProtocolVersion.PV_1_0_0_CLASSIC);
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
            domain = ProtocolBridge.getInstance().getClassicHandlerServer().getDomain(requestDomain);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        ProtocolBridge.getInstance().getProtocolServer().getNetworkServer().getEventManager().executeEvent(new Classic_DomainPacketReceivedEvent(protocolVersion, domain, requestDomain, clientID));

        if (ProtocolBridge.getInstance().getProtocolServer().getClientByID(clientID).clientSupportClassic()) ProtocolBridge.getInstance().getProtocolServer().getNetworkServer().getConnectionHandlerByID(clientID).sendPacket(new Classic_DomainPacket(clientID, requestDomain, domain));
        else ProtocolBridge.getInstance().getProtocolServer().getNetworkServer().getConnectionHandlerByID(clientID).sendPacket(new UnsupportedClassicPacket(Classic_PingPacket.class, new Object[] {clientID, requestDomain, domain}));
    }
}
