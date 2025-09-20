package github.openautonomousconnection.protocol.side.server.events;

import github.openautonomousconnection.protocol.side.server.ConnectedProtocolClient;
import lombok.Getter;
import me.finn.unlegitlibrary.event.impl.Event;

public class ProtocolClientConnected extends Event {

    @Getter
    private final ConnectedProtocolClient protocolClient;

    public ProtocolClientConnected(ConnectedProtocolClient protocolClient) {
        this.protocolClient = protocolClient;
    }
}
