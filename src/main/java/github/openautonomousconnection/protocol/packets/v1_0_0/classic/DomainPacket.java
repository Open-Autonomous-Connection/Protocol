package github.openautonomousconnection.protocol.packets.v1_0_0.classic;

import github.openautonomousconnection.protocol.ProtocolBridge;
import github.openautonomousconnection.protocol.ProtocolVersion;
import github.openautonomousconnection.protocol.classic.Classic_Domain;
import github.openautonomousconnection.protocol.classic.Classic_ProtocolVersion;
import github.openautonomousconnection.protocol.classic.Classic_RequestDomain;
import github.openautonomousconnection.protocol.packets.OACPacket;
import me.finn.unlegitlibrary.network.system.packets.Packet;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// ProtocolVersion 1.0.0-CLASSIC is ProtocolSide Server only
public class DomainPacket extends OACPacket {
    private final Classic_RequestDomain requestDomain;
    private final Classic_Domain domain;
    private final int clientID;

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

    }
}
