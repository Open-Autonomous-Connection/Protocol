package github.openautonomousconnection.protocol.packets;

import github.openautonomousconnection.protocol.ProtocolBridge;
import github.openautonomousconnection.protocol.ProtocolVersion;
import lombok.Getter;
import me.finn.unlegitlibrary.network.system.packets.Packet;

public abstract class OACPacket extends Packet {
    @Getter
    private final ProtocolVersion.ProtocolType packetType;

    public OACPacket(int id, ProtocolVersion.ProtocolType packetType) {
        super(id);
        this.packetType = packetType;
    }
}
