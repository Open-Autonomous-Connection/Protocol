package github.openautonomousconnection.protocol.packets.v1_0_0.classic;

import github.openautonomousconnection.protocol.ProtocolBridge;
import github.openautonomousconnection.protocol.versions.ProtocolVersion;
import github.openautonomousconnection.protocol.versions.v1_0_0.classic.Classic_ProtocolVersion;
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
        super(3, ProtocolVersion.PV_1_0_0_CLASSIC);
    }

    @Override
    public void onWrite(PacketHandler packetHandler, ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        if (ProtocolBridge.getInstance().isRunningAsServer()) objectOutputStream.writeInt(clientID);
        else {
            clientID = ProtocolBridge.getInstance().getProtocolClient().getNetworkClient().getClientID();
            objectOutputStream.writeInt(clientID);
        }

        objectOutputStream.writeUTF(message);
        objectOutputStream.writeObject(Classic_ProtocolVersion.PV_1_0_0);
    }

    @Override
    public void onRead(PacketHandler packetHandler, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        if (ProtocolBridge.getInstance().isRunningAsServer()) {
            clientID = objectInputStream.readInt();
            String message = objectInputStream.readUTF();
            Classic_ProtocolVersion protocolVersion = (Classic_ProtocolVersion) objectInputStream.readObject();

            ProtocolBridge.getInstance().getClassicHandlerServer().handleMessage(ProtocolBridge.getInstance().getProtocolServer().getNetworkServer().getConnectionHandlerByID(clientID), message, protocolVersion);
        } else {
            clientID = objectInputStream.readInt();
            String message = objectInputStream.readUTF();
            Classic_ProtocolVersion protocolVersion = (Classic_ProtocolVersion) objectInputStream.readObject();

            ProtocolBridge.getInstance().getClassicHandlerClient().handleMessage(message);
        }
    }
}
