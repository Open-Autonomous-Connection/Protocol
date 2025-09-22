package github.openautonomousconnection.protocol.side.server;

import github.openautonomousconnection.protocol.ProtocolBridge;
import github.openautonomousconnection.protocol.versions.v1_0_0.beta.DNSResponseCode;
import github.openautonomousconnection.protocol.versions.v1_0_0.beta.Domain;
import lombok.Getter;
import me.finn.unlegitlibrary.file.ConfigurationManager;
import me.finn.unlegitlibrary.network.system.server.NetworkServer;
import me.finn.unlegitlibrary.network.utils.NetworkUtils;
import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

public abstract class ProtocolServer extends DefaultMethodsOverrider {
    public final class ServerCertificateFolderStructure {
        public final File certificatesFolder;

        public final File publicFolder;
        public final File privateFolder;

        public final File privateCAFolder;
        public final File privateServerFolder;

        public final File publicCAFolder;
        public final File publicServerFolder;

        public ServerCertificateFolderStructure() {
            certificatesFolder = new File("certificates");

            publicFolder = new File(certificatesFolder, "public");
            privateFolder = new File(certificatesFolder, "private");

            privateCAFolder = new File(privateFolder, "ca");
            privateServerFolder = new File(privateFolder, "server");

            publicCAFolder = new File(publicFolder, "ca");
            publicServerFolder = new File(publicFolder, "server");

            if (!certificatesFolder.exists()) certificatesFolder.mkdirs();

            if (!publicFolder.exists()) publicFolder.mkdirs();
            if (!privateFolder.exists()) privateFolder.mkdirs();

            if (!privateCAFolder.exists()) privateCAFolder.mkdirs();
            if (!privateServerFolder.exists()) privateServerFolder.mkdirs();

            if (!publicCAFolder.exists()) publicCAFolder.mkdirs();
            if (!publicServerFolder.exists()) publicServerFolder.mkdirs();
        }

        public final String caPrefix = "ca_dns_";
        public final String certPrefix = "cert_dns_";
    }

    @Getter
    private final NetworkServer networkServer;

    @Getter
    private List<ConnectedProtocolClient> clients;

    @Getter
    private ServerCertificateFolderStructure folderStructure;

    private final ConfigurationManager configurationManager;

    public ProtocolServer(File configFile) throws IOException, CertificateException {
        if (!configFile.exists()) configFile.createNewFile();

        configurationManager = new ConfigurationManager(configFile);
        configurationManager.loadProperties();

        if (!configurationManager.isSet("server.site.info")) {
            configurationManager.set("server.site.info", "DNS-SERVER INFO SITE IP");
            configurationManager.saveProperties();
        }

        if (!configurationManager.isSet("server.site.register")) {
            configurationManager.set("server.site.register", "SERVER IP TO DNS-FRONTENT WEBSITE");
            configurationManager.saveProperties();
        }

        folderStructure = new ServerCertificateFolderStructure();

        checkFileExists(folderStructure.publicCAFolder, folderStructure.caPrefix, ".pem");
        checkFileExists(folderStructure.publicCAFolder, folderStructure.caPrefix, ".srl");
        checkFileExists(folderStructure.privateCAFolder, folderStructure.caPrefix, ".key");

        checkFileExists(folderStructure.publicServerFolder, folderStructure.certPrefix, ".crt");
        checkFileExists(folderStructure.privateServerFolder, folderStructure.certPrefix, ".key");

        File certFile = new File(folderStructure.publicServerFolder, folderStructure.certPrefix + NetworkUtils.getPublicIPAddress() + ".crt");
        File keyFile = new File(folderStructure.privateServerFolder, folderStructure.certPrefix + NetworkUtils.getPublicIPAddress() + ".key");

        ProtocolBridge protocolBridge = ProtocolBridge.getInstance();
        this.clients = new ArrayList<>();

        this.networkServer = new NetworkServer.ServerBuilder().setLogger(protocolBridge.getLogger()).
                setEventManager(protocolBridge.getProtocolSettings().eventManager).
                setPacketHandler(protocolBridge.getProtocolSettings().packetHandler).
                setPort(protocolBridge.getProtocolSettings().port).
                setRequireClientCertificate(false).setRootCAFolder(folderStructure.publicCAFolder).setServerCertificate(certFile, keyFile).
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

    public final ConnectedProtocolClient getClientByID(int clientID) {
        for (ConnectedProtocolClient client : clients)
            if (client.getConnectionHandler().getClientID() == clientID) return client;
        return null;
    }

    public final String getDNSInfoSite() {
        return configurationManager.getString("server.site.info");
    }

    public final String getDNSRegisterSite() {
        return configurationManager.getString("server.site.register");
    }

    public abstract List<Domain> getDomains();

    public abstract String getDomainDestination(Domain domain);

    public abstract String getSubnameDestination(Domain domain, String subname);

    public abstract String getTLNInfoSite(String topLevelName);

    public abstract DNSResponseCode validateDomain(Domain requestedDomain);

    public abstract void validationFailed(Domain domain, ConnectedProtocolClient client, Exception exception);

    public abstract void getDomainDestinationFailed(ConnectedProtocolClient client, Domain domain, DNSResponseCode validationResponse, Exception exception);
}
