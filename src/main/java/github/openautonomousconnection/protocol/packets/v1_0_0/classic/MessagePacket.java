package github.openautonomousconnection.protocol.packets.v1_0_0.classic;

import github.openautonomousconnection.protocol.ProtocolBridge;
import github.openautonomousconnection.protocol.ProtocolVersion;
import github.openautonomousconnection.protocol.classic.Classic_ProtocolVersion;
import github.openautonomousconnection.protocol.packets.OACPacket;
import github.openautonomousconnection.protocol.side.ProtocolClient;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// ProtocolVersion 1.0.0-CLASSIC is ProtocolSide Server only
public class MessagePacket extends OACPacket {
    private final String message;
    private Classic_ProtocolVersion protocolVersion;
    private final int clientID;

    public MessagePacket(String message, int toClient, ProtocolBridge protocolBridge) {
        super(3, ProtocolVersion.ProtocolType.CLASSIC, protocolBridge);
        this.message = message;
        this.clientID = toClient;
    }

    @Override
    public void write(PacketHandler packetHandler, ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        objectOutputStream.writeInt(clientID);

        objectOutputStream.writeUTF(message);
        objectOutputStream.writeObject(Classic_ProtocolVersion.PV_1_0_0);
    }

    @Override
    public void read(PacketHandler packetHandler, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        int clientID = objectInputStream.readInt();
        String message = objectInputStream.readUTF();
        protocolVersion = (Classic_ProtocolVersion) objectInputStream.readObject();

        getProtocolBridge().getClassicHandler().handleMessage(getProtocolBridge().getProtocolServer().getNetworkServer().getConnectionHandlerByID(clientID), message);
    }
}
