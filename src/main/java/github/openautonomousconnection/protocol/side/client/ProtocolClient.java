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
import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import java.io.File;
import java.io.IOException;

public abstract class ProtocolClient extends DefaultMethodsOverrider {
    @Getter
    private final NetworkClient networkClient;
    private ProtocolVersion serverVersion = null;

    public ProtocolClient(File caFolder, File certificatesClientFolder, File certificatesKeyFolder) {
        if (!caFolder.exists()) caFolder.mkdirs();
        if (!certificatesClientFolder.exists()) certificatesClientFolder.mkdirs();
        if (!certificatesKeyFolder.exists()) certificatesKeyFolder.mkdirs();

        networkClient = new NetworkClient.ClientBuilder().setLogger(ProtocolBridge.getInstance().getLogger()).
                setHost(ProtocolBridge.getInstance().getProtocolSettings().host).setPort(ProtocolBridge.getInstance().getProtocolSettings().port).
                setPacketHandler(ProtocolBridge.getInstance().getProtocolSettings().packetHandler).setEventManager(ProtocolBridge.getInstance().getProtocolSettings().eventManager).
                setRootCAFolder(caFolder).setClientCertificatesFolder(certificatesClientFolder, certificatesKeyFolder).
                build();
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
