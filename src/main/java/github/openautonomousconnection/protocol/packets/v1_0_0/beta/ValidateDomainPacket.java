package github.openautonomousconnection.protocol.packets.v1_0_0.beta;

import github.openautonomousconnection.protocol.ProtocolBridge;
import github.openautonomousconnection.protocol.packets.OACPacket;
import github.openautonomousconnection.protocol.versions.ProtocolVersion;
import github.openautonomousconnection.protocol.versions.v1_0_0.beta.Domain;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ValidateDomainPacket extends OACPacket {
    private Domain domain;
    private int clientID;

    public ValidateDomainPacket(Domain domain) {
        this();
        this.domain = domain;
    }

    public ValidateDomainPacket() {
        super(6, ProtocolVersion.PV_1_0_0_BETA);
    }

    @Override
    public void onWrite(PacketHandler packetHandler, ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        if (ProtocolBridge.getInstance().isRunningAsClient())
            objectOutputStream.writeInt(ProtocolBridge.getInstance().getProtocolClient().getNetworkClient().getClientID());
        else setResponseCode(ProtocolBridge.getInstance().getProtocolServer().validateDomain(domain));

        objectOutputStream.writeObject(domain);
    }

    @Override
    public void onRead(PacketHandler packetHandler, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        if (ProtocolBridge.getInstance().isRunningAsServer()) clientID = objectInputStream.readInt();
        domain = (Domain) objectInputStream.readObject();
    }

    @Override
    protected void onResponseCodeRead(PacketHandler packetHandler, ObjectInputStream objectInputStream) {
        super.onResponseCodeRead(packetHandler, objectInputStream);

        if (ProtocolBridge.getInstance().isRunningAsServer()) {
            try {
                ProtocolBridge.getInstance().getProtocolServer().getClientByID(clientID).getConnectionHandler().sendPacket(new ValidateDomainPacket(domain));
            } catch (IOException | ClassNotFoundException e) {
                ProtocolBridge.getInstance().getProtocolServer().validationFailed(domain, ProtocolBridge.getInstance().getProtocolServer().getClientByID(clientID), e);
            }

            return;
        }

        ProtocolBridge.getInstance().getProtocolClient().validationCompleted(domain, getResponseCode());
    }
}
