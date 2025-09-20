package github.openautonomousconnection.protocol.listeners;

import github.openautonomousconnection.protocol.ProtocolBridge;
import github.openautonomousconnection.protocol.packets.v1_0_0.beta.AuthPacket;
import me.finn.unlegitlibrary.event.EventListener;
import me.finn.unlegitlibrary.event.Listener;
import me.finn.unlegitlibrary.network.system.client.events.ClientConnectedEvent;
import me.finn.unlegitlibrary.network.system.client.events.ClientDisconnectedEvent;

import java.io.IOException;

public class ClientListener extends EventListener {

    @Listener
    public void onConnect(ClientConnectedEvent event) {
        try {
            event.client.sendPacket(new AuthPacket());
        } catch (IOException | ClassNotFoundException exception) {
            ProtocolBridge.getInstance().getLogger().exception("Failed to send auth packet", exception);
            event.client.disconnect();
        }
    }

    @Listener
    public void onDisconnect(ClientDisconnectedEvent event) {
        ProtocolBridge.getInstance().getProtocolClient().onDisconnect(event);
    }

}
