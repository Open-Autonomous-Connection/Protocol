package github.openautonomousconnection.protocol.packets.v1_0_0.classic;

import github.openautonomousconnection.protocol.ProtocolBridge;
import github.openautonomousconnection.protocol.packets.v1_0_0.beta.UnsupportedClassicPacket;
import github.openautonomousconnection.protocol.versions.ProtocolVersion;
import github.openautonomousconnection.protocol.versions.v1_0_0.beta.DNSResponseCode;
import github.openautonomousconnection.protocol.versions.v1_0_0.classic.Classic_Domain;
import github.openautonomousconnection.protocol.versions.v1_0_0.classic.Classic_PingPacketReceivedEvent;
import github.openautonomousconnection.protocol.versions.v1_0_0.classic.Classic_ProtocolVersion;
import github.openautonomousconnection.protocol.versions.v1_0_0.classic.Classic_RequestDomain;
import github.openautonomousconnection.protocol.packets.OACPacket;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;

public class Classic_PingPacket extends OACPacket {
    private Classic_RequestDomain requestDomain;
    private Classic_Domain domain;
    private int clientID;
    private boolean reachable;
    private Classic_ProtocolVersion protocolVersion;

    public Classic_PingPacket(Classic_RequestDomain requestDomain, Classic_Domain domain, boolean reachable) {
        this();

        this.requestDomain = requestDomain;
        this.domain = domain;
        this.reachable = reachable;
        this.protocolVersion = Classic_ProtocolVersion.PV_1_0_0;
    }

    public Classic_PingPacket() {
        super(1, ProtocolVersion.PV_1_0_0_CLASSIC);
    }

    @Override
    public void onWrite(PacketHandler packetHandler, ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        if (ProtocolBridge.getInstance().isRunningAsServer()) {
            objectOutputStream.writeInt(clientID);
            objectOutputStream.writeObject(requestDomain);
            objectOutputStream.writeObject(domain);
            objectOutputStream.writeBoolean(reachable);
        } else {
            clientID = ProtocolBridge.getInstance().getProtocolClient().getNetworkClient().getClientID();
            objectOutputStream.writeInt(clientID);
            objectOutputStream.writeObject(requestDomain);
        }

        objectOutputStream.writeObject(protocolVersion);
    }

    @Override
    public void onRead(PacketHandler packetHandler, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        if (ProtocolBridge.getInstance().isRunningAsServer()) {
            clientID = objectInputStream.readInt();
            requestDomain = (Classic_RequestDomain) objectInputStream.readObject();
            protocolVersion = (Classic_ProtocolVersion) objectInputStream.readObject();

            try {
                domain = ProtocolBridge.getInstance().getClassicHandlerServer().ping(requestDomain);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            reachable = domain != null;
            ProtocolBridge.getInstance().getProtocolServer().getNetworkServer().getEventManager().executeEvent(new Classic_PingPacketReceivedEvent(protocolVersion, domain, requestDomain, reachable, clientID));
            if (ProtocolBridge.getInstance().getProtocolServer().getClientByID(clientID).clientSupportClassic())
                ProtocolBridge.getInstance().getProtocolServer().getNetworkServer().getConnectionHandlerByID(clientID).sendPacket(new Classic_PingPacket(requestDomain, domain, reachable));
            else
                ProtocolBridge.getInstance().getProtocolServer().getNetworkServer().getConnectionHandlerByID(clientID).sendPacket(new UnsupportedClassicPacket(Classic_PingPacket.class, new Object[]{requestDomain, domain, reachable}));
        } else {
            clientID = objectInputStream.readInt();
            requestDomain = (Classic_RequestDomain) objectInputStream.readObject();
            domain = (Classic_Domain) objectInputStream.readObject();
            boolean reachable = objectInputStream.readBoolean();
            Classic_ProtocolVersion protocolVersion = (Classic_ProtocolVersion) objectInputStream.readObject();

            ProtocolBridge.getInstance().getProtocolClient().getNetworkClient().getEventManager().executeEvent(new Classic_PingPacketReceivedEvent(protocolVersion, domain, requestDomain, reachable, clientID));
        }
    }
}
