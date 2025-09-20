package github.openautonomousconnection.protocol.side.server;

import github.openautonomousconnection.protocol.ProtocolBridge;
import github.openautonomousconnection.protocol.versions.v1_0_0.beta.DNSResponseCode;
import github.openautonomousconnection.protocol.versions.v1_0_0.beta.Domain;
import lombok.Getter;
import me.finn.unlegitlibrary.file.ConfigurationManager;
import me.finn.unlegitlibrary.network.system.server.NetworkServer;
import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class ProtocolServer extends DefaultMethodsOverrider {
    @Getter
    private final NetworkServer networkServer;

    @Getter
    private List<ConnectedProtocolClient> clients;

    private final ConfigurationManager configurationManager;

    public ProtocolServer(File caFolder, File certFile, File keyFile, File configFile) throws IOException {
        if (!caFolder.exists()) caFolder.mkdirs();
        if (!certFile.exists() || !keyFile.exists()) throw new FileNotFoundException("Certificate or Key is missing!");

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

        ProtocolBridge protocolBridge = ProtocolBridge.getInstance();
        this.clients = new ArrayList<>();

        this.networkServer = new NetworkServer.ServerBuilder().setLogger(protocolBridge.getLogger()).
                setEventManager(protocolBridge.getProtocolSettings().eventManager).
                setPacketHandler(protocolBridge.getProtocolSettings().packetHandler).
                setPort(protocolBridge.getProtocolSettings().port).
                setRequireClientCertificate(false).setRootCAFolder(caFolder).setServerCertificate(certFile, keyFile).
                build();
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
