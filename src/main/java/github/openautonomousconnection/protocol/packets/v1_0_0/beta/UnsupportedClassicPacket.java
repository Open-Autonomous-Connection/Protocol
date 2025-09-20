package github.openautonomousconnection.protocol.packets.v1_0_0.beta;

import github.openautonomousconnection.protocol.ProtocolBridge;
import github.openautonomousconnection.protocol.packets.OACPacket;
import github.openautonomousconnection.protocol.versions.ProtocolVersion;
import github.openautonomousconnection.protocol.versions.v1_0_0.beta.DNSResponseCode;
import me.finn.unlegitlibrary.network.system.packets.Packet;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class UnsupportedClassicPacket extends OACPacket {
    private Class<? extends OACPacket> unsupportedClassicPacket;
    private Object[] content;

    public UnsupportedClassicPacket(Class<? extends OACPacket> unsupportedClassicPacket, Object[] content) {
        this();
        this.unsupportedClassicPacket = unsupportedClassicPacket;
        this.content = content;
    }

    public UnsupportedClassicPacket() {
        super(5, ProtocolVersion.PV_1_0_0_BETA);
    }

    @Override
    public void onWrite(PacketHandler packetHandler, ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        objectOutputStream.writeUTF(unsupportedClassicPacket.getName());
        objectOutputStream.writeInt(content.length);
        for (Object o : content) objectOutputStream.writeObject(o);
        setResponseCode(DNSResponseCode.RESPONSE_NOT_REQUIRED);
    }

    @Override
    public void onRead(PacketHandler packetHandler, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        String className = objectInputStream.readUTF();
        int size = objectInputStream.readInt();
        content = new Object[size];

        for (int i = 0; i < size; i++) {
            content[i] = objectInputStream.readObject();
        }

        ProtocolBridge.getInstance().getClassicHandlerClient().unsupportedClassicPacket(className, content);
    }
}
