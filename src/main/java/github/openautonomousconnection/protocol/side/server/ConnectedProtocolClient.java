package github.openautonomousconnection.protocol.side.server;

import github.openautonomousconnection.protocol.packets.OACPacket;
import github.openautonomousconnection.protocol.versions.ProtocolVersion;
import lombok.Getter;
import me.finn.unlegitlibrary.network.system.server.ConnectionHandler;

public class ConnectedProtocolClient {

    @Getter
    private final ConnectionHandler connectionHandler;

    private ProtocolVersion clientVersion = null;

    public ConnectedProtocolClient(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public ProtocolVersion getClientVersion() {
        return clientVersion == null ? ProtocolVersion.PV_1_0_0_CLASSIC : clientVersion;
    }

    public void setClientVersion(ProtocolVersion clientVersion) {
        if (clientVersion == null) this.clientVersion = clientVersion;
    }

    public boolean isStableClient() {
        return !isBetaClient() && !isClassicClient();
    }

    public boolean clientSupportStable() {
        boolean yes = false;
        for (ProtocolVersion compatibleVersion : getClientVersion().getCompatibleVersions()) {
            yes = compatibleVersion.getProtocolType() == ProtocolVersion.ProtocolType.STABLE;
            if (yes) break;
        }

        return isStableClient() || yes;
    }

    public boolean isBetaClient() {
        return getClientVersion().getProtocolType() == ProtocolVersion.ProtocolType.BETA;
    }

    public boolean clientSupportBeta() {
        boolean yes = false;
        for (ProtocolVersion compatibleVersion : getClientVersion().getCompatibleVersions()) {
            yes = compatibleVersion.getProtocolType() == ProtocolVersion.ProtocolType.BETA;
            if (yes) break;
        }

        return isBetaClient() || yes;
    }

    public boolean isClassicClient() {
        return getClientVersion().getProtocolType() == ProtocolVersion.ProtocolType.CLASSIC;
    }

    public boolean clientSupportClassic() {
        boolean yes = false;
        for (ProtocolVersion compatibleVersion : getClientVersion().getCompatibleVersions()) {
            yes = compatibleVersion.getProtocolType() == ProtocolVersion.ProtocolType.CLASSIC;
            if (yes) break;
        }

        return isClassicClient() || yes;
    }

    public boolean isPacketSupported(OACPacket packet) {
        return isVersionSupported(packet.getProtocolVersion());
    }

    public boolean isVersionSupported(ProtocolVersion targetVersion) {
        return getClientVersion() == targetVersion || getClientVersion().getCompatibleVersions().contains(targetVersion);
    }
}
