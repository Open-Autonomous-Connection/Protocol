package github.openautonomousconnection.protocol.packets.v1_0_0.classic;

import github.openautonomousconnection.protocol.ProtocolBridge;
import github.openautonomousconnection.protocol.ProtocolVersion;
import github.openautonomousconnection.protocol.classic.Classic_ProtocolVersion;
import github.openautonomousconnection.protocol.packets.OACPacket;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// ProtocolVersion 1.0.0-CLASSIC is ProtocolSide Server only
public class Classic_MessagePacket extends OACPacket {
    private String message;
    private int clientID;

    public Classic_MessagePacket(String message, int toClient) {
        this();
        this.message = message;
        this.clientID = toClient;
    }

    public Classic_MessagePacket() {
        super(3, ProtocolVersion.ProtocolType.CLASSIC);
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
        Classic_ProtocolVersion protocolVersion = (Classic_ProtocolVersion) objectInputStream.readObject();

        ProtocolBridge.getInstance().getClassicHandlerServer().handleMessage(ProtocolBridge.getInstance().getProtocolServer().getNetworkServer().getConnectionHandlerByID(clientID), message);
    }
}
