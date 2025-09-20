package github.openautonomousconnection.protocol.packets.v1_0_0.beta;

import github.openautonomousconnection.protocol.ProtocolBridge;
import github.openautonomousconnection.protocol.packets.OACPacket;
import github.openautonomousconnection.protocol.side.client.events.ConnectedToProtocolServer;
import github.openautonomousconnection.protocol.side.server.ConnectedProtocolClient;
import github.openautonomousconnection.protocol.side.server.events.ProtocolClientConnected;
import github.openautonomousconnection.protocol.versions.ProtocolVersion;
import github.openautonomousconnection.protocol.versions.v1_0_0.beta.DNSResponseCode;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;
import me.finn.unlegitlibrary.network.system.server.ConnectionHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AuthPacket extends OACPacket {

    public AuthPacket() {
        super(4, ProtocolVersion.PV_1_0_0_BETA);
    }

    @Override
    public void onWrite(PacketHandler packetHandler, ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        if (ProtocolBridge.getInstance().isRunningAsServer()) objectOutputStream.writeObject(ProtocolBridge.getInstance().getProtocolVersion());
        else {
            objectOutputStream.writeInt(ProtocolBridge.getInstance().getProtocolClient().getNetworkClient().getClientID());
            objectOutputStream.writeObject(ProtocolBridge.getInstance().getProtocolVersion());
        }
    }

    @Override
    public void onRead(PacketHandler packetHandler, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        if (ProtocolBridge.getInstance().isRunningAsServer()) {
            int clientID = objectInputStream.readInt();
            ProtocolVersion clientVersion = (ProtocolVersion) objectInputStream.readObject();
            ConnectionHandler connectionHandler = ProtocolBridge.getInstance().getProtocolServer().getNetworkServer().getConnectionHandlerByID(clientID);

            if (!ProtocolBridge.getInstance().isVersionSupported(clientVersion)) {
                setResponseCode(DNSResponseCode.RESPONSE_AUTH_FAILED);
                connectionHandler.disconnect();
            } else {
                setResponseCode(DNSResponseCode.RESPONSE_AUTH_SUCCESS);
                ConnectedProtocolClient client = ProtocolBridge.getInstance().getProtocolServer().getClientByID(clientID);
                client.setClientVersion(clientVersion);
                ProtocolBridge.getInstance().getProtocolSettings().eventManager.executeEvent(new ProtocolClientConnected(client));
            }
        } else {
            ProtocolVersion serverVersion = (ProtocolVersion) objectInputStream.readObject();

            if (!ProtocolBridge.getInstance().isVersionSupported(serverVersion)) {
                setResponseCode(DNSResponseCode.RESPONSE_AUTH_FAILED);
                ProtocolBridge.getInstance().getProtocolClient().getNetworkClient().disconnect();
            } else {
                setResponseCode(DNSResponseCode.RESPONSE_AUTH_SUCCESS);
                ProtocolBridge.getInstance().getProtocolClient().setServerVersion(serverVersion);
                ProtocolBridge.getInstance().getProtocolSettings().eventManager.executeEvent(new ConnectedToProtocolServer());
            }
        }
    }
}
