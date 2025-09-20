package github.openautonomousconnection.protocol.packets;

import github.openautonomousconnection.protocol.versions.ProtocolVersion;
import lombok.Getter;
import me.finn.unlegitlibrary.network.system.packets.Packet;

public abstract class OACPacket extends Packet {
    @Getter
    private final ProtocolVersion protocolVersion;

    public OACPacket(int id, ProtocolVersion protocolVersion) {
        super(id);
        this.protocolVersion = protocolVersion;
    }
}
