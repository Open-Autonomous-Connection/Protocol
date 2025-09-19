package github.openautonomousconnection.protocol.side;

import github.openautonomousconnection.protocol.ProtocolBridge;
import lombok.Getter;
import me.finn.unlegitlibrary.network.system.server.NetworkServer;

public abstract class ProtocolServer {
    @Getter
    private final ProtocolBridge protocolBridge;

    @Getter
    private NetworkServer networkServer;

    public ProtocolServer(ProtocolBridge protocolBridge) {
        this.protocolBridge = protocolBridge;

        this.networkServer = new NetworkServer.ServerBuilder().
                setEventManager(protocolBridge.getProtocolSettings().eventManager).
                setPacketHandler(protocolBridge.getProtocolSettings().packetHandler).
                setPort(protocolBridge.getProtocolSettings().port).
                build();
    }
}
