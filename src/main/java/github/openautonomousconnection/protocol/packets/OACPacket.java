package github.openautonomousconnection.protocol.packets;

import github.openautonomousconnection.protocol.versions.ProtocolVersion;
import github.openautonomousconnection.protocol.versions.v1_0_0.beta.DNSResponseCode;
import lombok.Getter;
import me.finn.unlegitlibrary.network.system.packets.Packet;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class OACPacket extends Packet {

    @Getter
    private final ProtocolVersion protocolVersion;

    private DNSResponseCode responseCode = DNSResponseCode.RESPONSE_NOT_REQUIRED;

    public OACPacket(int id, ProtocolVersion protocolVersion) {
        super(id);
        this.protocolVersion = protocolVersion;
    }

    protected final DNSResponseCode getResponseCode() {
        return responseCode;
    }

    protected final void setResponseCode(DNSResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    @Override
    public final void write(PacketHandler packetHandler, ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        onWrite(packetHandler, objectOutputStream);
        if (protocolVersion != ProtocolVersion.PV_1_0_0_CLASSIC) objectOutputStream.writeObject(responseCode);
    }

    @Override
    public final void read(PacketHandler packetHandler, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        onRead(packetHandler, objectInputStream);
        if (protocolVersion != ProtocolVersion.PV_1_0_0_CLASSIC) {
            responseCode = (DNSResponseCode) objectInputStream.readObject();
            onResponseCodeRead(packetHandler, objectInputStream);
        }
    }

    public abstract void onWrite(PacketHandler packetHandler, ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException;

    public abstract void onRead(PacketHandler packetHandler, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException;

    protected void onResponseCodeRead(PacketHandler packetHandler, ObjectInputStream objectInputStream) {
    }
}
