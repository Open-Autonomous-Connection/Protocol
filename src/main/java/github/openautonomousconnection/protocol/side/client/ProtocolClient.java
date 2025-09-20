package github.openautonomousconnection.protocol.side.client;

import github.openautonomousconnection.protocol.ProtocolBridge;
import github.openautonomousconnection.protocol.packets.OACPacket;
import github.openautonomousconnection.protocol.versions.ProtocolVersion;
import lombok.Getter;
import me.finn.unlegitlibrary.network.system.client.NetworkClient;
import me.finn.unlegitlibrary.network.system.client.events.ClientDisconnectedEvent;
import me.finn.unlegitlibrary.network.system.server.NetworkServer;
import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import java.io.File;

public class ProtocolClient extends DefaultMethodsOverrider {
    private ProtocolVersion serverVersion = null;

    @Getter
    private final NetworkClient networkClient;

    public ProtocolVersion getServerVersion() {
        return serverVersion == null ? ProtocolVersion.PV_1_0_0_CLASSIC : serverVersion;
    }

    public void setServerVersion(ProtocolVersion serverVersion) {
        if (serverVersion == null) this.serverVersion = serverVersion;
    }

    public final void onDisconnect(ClientDisconnectedEvent event) {
        serverVersion = null;
    }

    public boolean isStableServer() {
        return !isBetaServer() && !isClassicServer();
    }

    public boolean serverSupportStable() {
        boolean yes = false;
        for (ProtocolVersion compatibleVersion : getServerVersion().getCompatibleVersions()) {
            yes = compatibleVersion.getProtocolType() == ProtocolVersion.ProtocolType.STABLE;
            if (yes) break;
        }

        return isStableServer() || yes;
    }

    public boolean isBetaServer() {
        return getServerVersion().getProtocolType() == ProtocolVersion.ProtocolType.BETA;
    }

    public boolean serverSupportBeta() {
        boolean yes = false;
        for (ProtocolVersion compatibleVersion : getServerVersion().getCompatibleVersions()) {
            yes = compatibleVersion.getProtocolType() == ProtocolVersion.ProtocolType.BETA;
            if (yes) break;
        }

        return isBetaServer() || yes;
    }

    public boolean isClassicServer() {
        return getServerVersion().getProtocolType() == ProtocolVersion.ProtocolType.CLASSIC;
    }

    public boolean serverSupportClassic() {
        boolean yes = false;
        for (ProtocolVersion compatibleVersion : getServerVersion().getCompatibleVersions()) {
            yes = compatibleVersion.getProtocolType() == ProtocolVersion.ProtocolType.CLASSIC;
            if (yes) break;
        }

        return isClassicServer() || yes;
    }

    public boolean isPacketSupported(OACPacket packet) {
        return isVersionSupported(packet.getProtocolVersion());
    }

    public boolean isVersionSupported(ProtocolVersion targetVersion) {
        return getServerVersion() == targetVersion || getServerVersion().getCompatibleVersions().contains(targetVersion);
    }

    public ProtocolClient(File caFolder, File certificatesClientFolder, File certificatesKeyFolder) {
        networkClient = new NetworkClient.ClientBuilder().setLogger(ProtocolBridge.getInstance().getLogger()).
                setHost(ProtocolBridge.getInstance().getProtocolSettings().host).setPort(ProtocolBridge.getInstance().getProtocolSettings().port).
                setPacketHandler(ProtocolBridge.getInstance().getProtocolSettings().packetHandler).setEventManager(ProtocolBridge.getInstance().getProtocolSettings().eventManager).
                setRootCAFolder(caFolder).setClientCertificatesFolder(certificatesClientFolder, certificatesKeyFolder).
                build();
    }
}
