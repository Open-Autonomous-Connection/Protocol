package github.openautonomousconnection.protocol.packets.v1_0_0.beta;

import github.openautonomousconnection.protocol.ProtocolBridge;
import github.openautonomousconnection.protocol.packets.OACPacket;
import github.openautonomousconnection.protocol.side.client.events.ConnectedToProtocolServer;
import github.openautonomousconnection.protocol.side.server.ConnectedProtocolClient;
import github.openautonomousconnection.protocol.side.server.events.ProtocolClientConnected;
import github.openautonomousconnection.protocol.versions.ProtocolVersion;
import github.openautonomousconnection.protocol.versions.v1_0_0.beta.DNSResponseCode;
import me.finn.unlegitlibrary.file.FileUtils;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;
import me.finn.unlegitlibrary.network.system.server.ConnectionHandler;
import me.finn.unlegitlibrary.network.utils.NetworkUtils;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AuthPacket extends OACPacket {

    public AuthPacket() {
        super(4, ProtocolVersion.PV_1_0_0_BETA);
    }

    File certificatesFolder = new File("certificates");

    File publicFolder = new File(certificatesFolder, "public");
    File privateFolder = new File(certificatesFolder, "private");

    File privateCAFolder = new File(privateFolder, "ca");
    File privateServerFolder = new File(privateFolder, "server");

    File publicCAFolder = new File(publicFolder, "ca");
    File publicServerFolder = new File(publicFolder, "server");

    @Override
    public void onWrite(PacketHandler packetHandler, ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        if (ProtocolBridge.getInstance().isRunningAsServer()) {
            objectOutputStream.writeObject(ProtocolBridge.getInstance().getProtocolVersion());

            // Read ca files
            String caKey = "N/A";
            String caPem = "N/A";
            String caSrl = "N/A";
            try {
                objectOutputStream.writeUTF(ProtocolBridge.getInstance().getProtocolServer().getFolderStructure().caPrefix + NetworkUtils.getPublicIPAddress());

                caKey = FileUtils.readFileFull(new File(
                        ProtocolBridge.getInstance().getProtocolServer().getFolderStructure().privateCAFolder,
                        ProtocolBridge.getInstance().getProtocolServer().getFolderStructure().caPrefix + NetworkUtils.getPublicIPAddress() + ".key"));

                caPem = FileUtils.readFileFull(new File(
                        ProtocolBridge.getInstance().getProtocolServer().getFolderStructure().publicCAFolder,
                        ProtocolBridge.getInstance().getProtocolServer().getFolderStructure().caPrefix + NetworkUtils.getPublicIPAddress() + ".pem"));

                caSrl = FileUtils.readFileFull(new File(
                        ProtocolBridge.getInstance().getProtocolServer().getFolderStructure().publicCAFolder,
                        ProtocolBridge.getInstance().getProtocolServer().getFolderStructure().caPrefix + NetworkUtils.getPublicIPAddress() + ".srl"));
            } catch (Exception exception) {
                ProtocolBridge.getInstance().getLogger().exception("Failed to read ca-files", exception);
                setResponseCode(DNSResponseCode.RESPONSE_AUTH_FAILED);
            }

            // Send ca data
            objectOutputStream.writeUTF(caKey);
            objectOutputStream.writeUTF(caPem);
            objectOutputStream.writeUTF(caSrl);
        } else {
            objectOutputStream.writeInt(ProtocolBridge.getInstance().getProtocolClient().getNetworkClient().getClientID());
            objectOutputStream.writeObject(ProtocolBridge.getInstance().getProtocolVersion());
        }
    }

    @Override
    public void onRead(PacketHandler packetHandler, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        if (ProtocolBridge.getInstance().isRunningAsServer()) {
            int clientID = objectInputStream.readInt();
            ProtocolVersion clientVersion = (ProtocolVersion) objectInputStream.readObject();
            ConnectionHandler connectionHandler = ProtocolBridge.getInstance().getProtocolServer().getNetworkServer().getConnectionHandlerByID(clientID);

            if (!ProtocolBridge.getInstance().isVersionSupported(clientVersion)) {
                setResponseCode(DNSResponseCode.RESPONSE_AUTH_FAILED);
                connectionHandler.disconnect();
                return;
            } else setResponseCode(DNSResponseCode.RESPONSE_AUTH_SUCCESS);


            ConnectedProtocolClient client = ProtocolBridge.getInstance().getProtocolServer().getClientByID(clientID);
            client.setClientVersion(clientVersion);
            ProtocolBridge.getInstance().getProtocolSettings().eventManager.executeEvent(new ProtocolClientConnected(client));
        } else {
            ProtocolVersion serverVersion = (ProtocolVersion) objectInputStream.readObject();

            if (!ProtocolBridge.getInstance().isVersionSupported(serverVersion)) {
                setResponseCode(DNSResponseCode.RESPONSE_AUTH_FAILED);
                ProtocolBridge.getInstance().getProtocolClient().getNetworkClient().disconnect();
                return;
            } else setResponseCode(DNSResponseCode.RESPONSE_AUTH_SUCCESS);

            String caPrefix = objectInputStream.readUTF();

            String caKey = objectInputStream.readUTF();
            String caPem = objectInputStream.readUTF();
            String caSrl = objectInputStream.readUTF();

            if (caKey.equalsIgnoreCase("N/A") || caPem.equalsIgnoreCase("N/A") || caSrl.equalsIgnoreCase("N/A"))
                setResponseCode(DNSResponseCode.RESPONSE_AUTH_FAILED);
            else {

                File caPemFile = new File(ProtocolBridge.getInstance().getProtocolClient().getFolderStructure().publicCAFolder, caPrefix + ".pem");
                File caSrlFile = new File(ProtocolBridge.getInstance().getProtocolClient().getFolderStructure().publicCAFolder, caPrefix + ".srl");
                File caKeyFile = new File(ProtocolBridge.getInstance().getProtocolClient().getFolderStructure().privateCAFolder, caPrefix + ".key");

                try {
                    if (!caPemFile.exists()) caPemFile.createNewFile();
                    if (!caSrlFile.exists()) caSrlFile.createNewFile();
                    if (!caKeyFile.exists()) caKeyFile.createNewFile();

                    FileUtils.writeFile(caPemFile, caPem);
                    FileUtils.writeFile(caSrlFile, caKey);
                    FileUtils.writeFile(caKeyFile, caSrl);
                } catch (Exception exception) {
                    ProtocolBridge.getInstance().getLogger().exception("Failed to create/save ca-files", exception);
                    setResponseCode(DNSResponseCode.RESPONSE_AUTH_FAILED);
                }
            }

            ProtocolBridge.getInstance().getProtocolClient().setServerVersion(serverVersion);
            ProtocolBridge.getInstance().getProtocolSettings().eventManager.executeEvent(new ConnectedToProtocolServer());
        }
    }
}
