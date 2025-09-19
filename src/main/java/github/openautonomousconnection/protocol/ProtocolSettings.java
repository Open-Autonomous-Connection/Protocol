package github.openautonomousconnection.protocol;

import me.finn.unlegitlibrary.event.EventManager;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;
import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

public class ProtocolSettings extends DefaultMethodsOverrider {

    public String host;
    public int port;
    public PacketHandler packetHandler;
    public EventManager eventManager;

}
