package github.openautonomousconnection.protocol.listeners;

import github.openautonomousconnection.protocol.ProtocolBridge;
import github.openautonomousconnection.protocol.side.server.ConnectedProtocolClient;
import me.finn.unlegitlibrary.event.EventListener;
import me.finn.unlegitlibrary.event.Listener;
import me.finn.unlegitlibrary.network.system.server.events.ConnectionHandlerConnectedEvent;
import me.finn.unlegitlibrary.network.system.server.events.ConnectionHandlerDisconnectedEvent;

import java.util.ArrayList;

public class ServerListener extends EventListener {

    @Listener
    public void onConnect(ConnectionHandlerConnectedEvent event) {
        ProtocolBridge.getInstance().getProtocolServer().getClients().add(new ConnectedProtocolClient(event.connectionHandler));
    }

    @Listener
    public void onDisconnect(ConnectionHandlerDisconnectedEvent event) {
        ProtocolBridge.getInstance().getProtocolServer().getClients().removeIf(client -> client.getConnectionHandler().getClientID() == -1);
    }

}
