package github.openautonomousconnection.protocol.side.server;

import github.openautonomousconnection.protocol.ProtocolBridge;
import lombok.Getter;
import me.finn.unlegitlibrary.network.system.server.NetworkServer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class ProtocolServer {
    @Getter
    private final NetworkServer networkServer;

    @Getter
    private List<ConnectedProtocolClient> clients;

    public ConnectedProtocolClient getClientByID(int clientID) {
        for (ConnectedProtocolClient client : clients) if (client.getConnectionHandler().getClientID() == clientID) return client;
        return null;
    }

    public ProtocolServer(File caFolder, File certFile, File keyFile) {
        ProtocolBridge protocolBridge = ProtocolBridge.getInstance();
        this.clients = new ArrayList<>();

        this.networkServer = new NetworkServer.ServerBuilder().setLogger(protocolBridge.getLogger()).
                setEventManager(protocolBridge.getProtocolSettings().eventManager).
                setPacketHandler(protocolBridge.getProtocolSettings().packetHandler).
                setPort(protocolBridge.getProtocolSettings().port).
                setRequireClientCertificate(false).setRootCAFolder(caFolder).setServerCertificate(certFile, keyFile).
                build();
    }
}
