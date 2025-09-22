package github.openautonomousconnection.protocol.side.client;

import github.openautonomousconnection.protocol.ProtocolBridge;
import github.openautonomousconnection.protocol.packets.OACPacket;
import github.openautonomousconnection.protocol.packets.v1_0_0.beta.ValidateDomainPacket;
import github.openautonomousconnection.protocol.versions.ProtocolVersion;
import github.openautonomousconnection.protocol.versions.v1_0_0.beta.DNSResponseCode;
import github.openautonomousconnection.protocol.versions.v1_0_0.beta.Domain;
import lombok.Getter;
import me.finn.unlegitlibrary.network.system.client.NetworkClient;
import me.finn.unlegitlibrary.network.system.client.events.ClientDisconnectedEvent;
import me.finn.unlegitlibrary.network.utils.NetworkUtils;
import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CertificateException;

public abstract class ProtocolClient extends DefaultMethodsOverrider {
    public final class ClientCertificateFolderStructure {
        public final File certificatesFolder;

        public final File publicFolder;
        public final File privateFolder;

        public final File privateCAFolder;
        public final File privateClientFolder;

        public final File publicCAFolder;
        public final File publicClientFolder;

        public ClientCertificateFolderStructure() {
            certificatesFolder = new File("certificates");

            publicFolder = new File(certificatesFolder, "public");
            privateFolder = new File(certificatesFolder, "private");

            privateCAFolder = new File(privateFolder, "ca");
            privateClientFolder = new File(privateFolder, "client");

            publicCAFolder = new File(publicFolder, "ca");
            publicClientFolder = new File(publicFolder, "client");

            if (!certificatesFolder.exists()) certificatesFolder.mkdirs();

            if (!publicFolder.exists()) publicFolder.mkdirs();
            if (!privateFolder.exists()) privateFolder.mkdirs();

            if (!privateCAFolder.exists()) privateCAFolder.mkdirs();
            if (!privateClientFolder.exists()) privateClientFolder.mkdirs();

            if (!publicCAFolder.exists()) publicCAFolder.mkdirs();
            if (!publicClientFolder.exists()) publicClientFolder.mkdirs();
        }
    }

    @Getter
    private final NetworkClient networkClient;
    private ProtocolVersion serverVersion = null;
    @Getter
    private final ClientCertificateFolderStructure folderStructure;

    public ProtocolClient() throws CertificateException, IOException {
        folderStructure = new ClientCertificateFolderStructure();

        networkClient = new NetworkClient.ClientBuilder().setLogger(ProtocolBridge.getInstance().getLogger()).
                setHost(ProtocolBridge.getInstance().getProtocolSettings().host).setPort(ProtocolBridge.getInstance().getProtocolSettings().port).
                setPacketHandler(ProtocolBridge.getInstance().getProtocolSettings().packetHandler).setEventManager(ProtocolBridge.getInstance().getProtocolSettings().eventManager).
                setRootCAFolder(folderStructure.publicCAFolder).setClientCertificatesFolder(folderStructure.publicClientFolder, folderStructure.privateClientFolder).
                build();
    }

    private final void checkFileExists(File folder, String prefix, String extension) throws CertificateException, IOException {
        boolean found = false;
        if (folder == null) throw new FileNotFoundException("Folder does not exist");

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) throw new FileNotFoundException("Folder " + folder.getAbsolutePath() + " is empty");

        for (File file : files) {
            if (!file.getName().startsWith(prefix) || !file.getName().endsWith(extension)) throw new CertificateException(file.getAbsolutePath() + " is not valid");
            if (!found) found = file.getName().equalsIgnoreCase(prefix + NetworkUtils.getPublicIPAddress() + extension);
        }

        if (!found) throw new CertificateException("Missing " + prefix + NetworkUtils.getPublicIPAddress() + extension);
    }

    public final ProtocolVersion getServerVersion() {
        return serverVersion == null ? ProtocolVersion.PV_1_0_0_CLASSIC : serverVersion;
    }

    public final void setServerVersion(ProtocolVersion serverVersion) {
        if (serverVersion == null) this.serverVersion = serverVersion;
    }

    public final void onDisconnect(ClientDisconnectedEvent event) {
        serverVersion = null;
    }

    public final boolean isStableServer() {
        return !isBetaServer() && !isClassicServer();
    }

    public final boolean serverSupportStable() {
        boolean yes = false;
        for (ProtocolVersion compatibleVersion : getServerVersion().getCompatibleVersions()) {
            yes = compatibleVersion.getProtocolType() == ProtocolVersion.ProtocolType.STABLE;
            if (yes) break;
        }

        return isStableServer() || yes;
    }

    public final boolean isBetaServer() {
        return getServerVersion().getProtocolType() == ProtocolVersion.ProtocolType.BETA;
    }

    public final boolean serverSupportBeta() {
        boolean yes = false;
        for (ProtocolVersion compatibleVersion : getServerVersion().getCompatibleVersions()) {
            yes = compatibleVersion.getProtocolType() == ProtocolVersion.ProtocolType.BETA;
            if (yes) break;
        }

        return isBetaServer() || yes;
    }

    public final boolean isClassicServer() {
        return getServerVersion().getProtocolType() == ProtocolVersion.ProtocolType.CLASSIC;
    }

    public final boolean serverSupportClassic() {
        boolean yes = false;
        for (ProtocolVersion compatibleVersion : getServerVersion().getCompatibleVersions()) {
            yes = compatibleVersion.getProtocolType() == ProtocolVersion.ProtocolType.CLASSIC;
            if (yes) break;
        }

        return isClassicServer() || yes;
    }

    public final boolean isPacketSupported(OACPacket packet) {
        return isVersionSupported(packet.getProtocolVersion());
    }

    public final boolean isVersionSupported(ProtocolVersion targetVersion) {
        return getServerVersion() == targetVersion || getServerVersion().getCompatibleVersions().contains(targetVersion);
    }

    public final void validateDomain(Domain domain) throws IOException, ClassNotFoundException {
        networkClient.sendPacket(new ValidateDomainPacket(domain));
    }

    public abstract void validationCompleted(Domain domain, DNSResponseCode responseCode);

    public abstract void getDestinationCompleted(Domain domain, String destination, DNSResponseCode validationResponse);
}
