package github.openautonomousconnection.protocol.packets.v1_0_0.beta;

import github.openautonomousconnection.protocol.ProtocolBridge;
import github.openautonomousconnection.protocol.packets.OACPacket;
import github.openautonomousconnection.protocol.versions.ProtocolVersion;
import github.openautonomousconnection.protocol.versions.v1_0_0.beta.DNSResponseCode;
import github.openautonomousconnection.protocol.versions.v1_0_0.beta.Domain;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.function.DoubleBinaryOperator;

public class GetDestinationPacket extends OACPacket {
    private Domain domain;
    private int clientID;
    private DNSResponseCode validationResponse;
    private String destination;

    public GetDestinationPacket(Domain domain, DNSResponseCode validationResponse, String destination) {
        this();
        this.domain = domain;
        this.validationResponse = validationResponse;
        this.destination = destination;
    }

    public GetDestinationPacket() {
        super(6, ProtocolVersion.PV_1_0_0_BETA);
    }

    @Override
    public void onWrite(PacketHandler packetHandler, ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        if (ProtocolBridge.getInstance().isRunningAsClient()) {
            if (validationResponse != DNSResponseCode.RESPONSE_DOMAIN_FULLY_EXIST) return;

            objectOutputStream.writeInt(ProtocolBridge.getInstance().getProtocolClient().getNetworkClient().getClientID());
            objectOutputStream.writeObject(domain);
            objectOutputStream.writeObject(validationResponse);
        } else {
            objectOutputStream.writeObject(domain);
            objectOutputStream.writeObject(validationResponse);
            objectOutputStream.writeUTF(destination);
        }
    }

    @Override
    public void onRead(PacketHandler packetHandler, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        if (ProtocolBridge.getInstance().isRunningAsServer()) {
            clientID = objectInputStream.readInt();
            domain = (Domain) objectInputStream.readObject();
            validationResponse = (DNSResponseCode) objectInputStream.readObject();
        } else {
            domain = (Domain) objectInputStream.readObject();
            validationResponse = (DNSResponseCode) objectInputStream.readObject();
            destination = objectInputStream.readUTF();

            ProtocolBridge.getInstance().getProtocolClient().getDestinationCompleted(domain, destination, validationResponse);
        }
    }

    @Override
    protected void onResponseCodeRead(PacketHandler packetHandler, ObjectInputStream objectInputStream) {
        super.onResponseCodeRead(packetHandler, objectInputStream);

        if (ProtocolBridge.getInstance().isRunningAsServer()) {
            if (validationResponse != DNSResponseCode.RESPONSE_DOMAIN_FULLY_EXIST) return;
            destination = domain.getDestination();
            try {
                ProtocolBridge.getInstance().getProtocolServer().getClientByID(clientID).getConnectionHandler().sendPacket(new GetDestinationPacket(domain, validationResponse, destination));
            } catch (IOException | ClassNotFoundException exception) {
                ProtocolBridge.getInstance().getProtocolServer().getDomainDestinationFailed(ProtocolBridge.getInstance().getProtocolServer().getClientByID(clientID), domain, validationResponse, exception);
            }
        }
    }
}
