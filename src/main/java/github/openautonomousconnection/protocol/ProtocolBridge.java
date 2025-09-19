package github.openautonomousconnection.protocol;

import github.openautonomousconnection.protocol.handle.ClassicHandlerServer;
import github.openautonomousconnection.protocol.packets.v1_0_0.classic.Classic_DomainPacket;
import github.openautonomousconnection.protocol.packets.v1_0_0.classic.Classic_MessagePacket;
import github.openautonomousconnection.protocol.packets.v1_0_0.classic.Classic_PingPacket;
import github.openautonomousconnection.protocol.side.ProtocolClient;
import github.openautonomousconnection.protocol.side.ProtocolServer;
import lombok.Getter;
import lombok.Setter;
import me.finn.unlegitlibrary.utils.Logger;

import java.io.File;
import java.io.IOException;

public class ProtocolBridge {

    @Getter
    private final ProtocolSettings protocolSettings;

    @Getter
    private final ProtocolVersion protocolVersion;

    @Getter
    private ProtocolServer protocolServer;

    @Getter
    private ProtocolClient protocolClient;

    @Getter
    private final Logger logger;

    @Getter @Setter
    private ClassicHandlerServer classicHandlerServer;

    @Getter
    private static ProtocolBridge instance;

    public ProtocolBridge(ProtocolServer protocolServer, ProtocolSettings protocolSettings, ProtocolVersion protocolVersion, File logFolder) {
        this.protocolServer = protocolServer;
        this.protocolSettings = protocolSettings;
        this.protocolVersion = protocolVersion;

        Logger tmpLogger = null;
        try {
            tmpLogger = new Logger(logFolder, false, true);
        } catch (IOException | NoSuchFieldException | IllegalAccessException exception) {
            exception.printStackTrace();
            tmpLogger = null;
            System.exit(1);
        }

        this.logger = tmpLogger;

        if (!validateProtocolVersion()) {
            this.logger.error("Invalid protocol version '" + protocolVersion.toString() + "'!");
            System.exit(1);
        }

        instance = this;
    }

    public ProtocolBridge(ProtocolClient protocolClient, ProtocolSettings protocolSettings, ProtocolVersion protocolVersion, File logFolder) {
        this.protocolClient = protocolClient;
        this.protocolSettings = protocolSettings;
        this.protocolVersion = protocolVersion;

        Logger tmpLogger = null;
        try {
            tmpLogger = new Logger(logFolder, false, true);
        } catch (IOException | NoSuchFieldException | IllegalAccessException exception) {
            exception.printStackTrace();
            tmpLogger = null;
            System.exit(1);
        }

        this.logger = tmpLogger;

        if (!validateProtocolVersion()) {
            this.logger.error("Invalid protocol version '" + protocolVersion.toString() + "'!");
            System.exit(1);
        }

        if (isClassicSupported()) {
            protocolSettings.packetHandler.registerPacket(new Classic_DomainPacket());
            protocolSettings.packetHandler.registerPacket(new Classic_MessagePacket());
            protocolSettings.packetHandler.registerPacket(new Classic_PingPacket());
        }

        instance = this;
    }

    public boolean isClassicSupported() {
        boolean yes = false;
        for (ProtocolVersion compatibleVersion : protocolVersion.getCompatibleVersions()) {
            yes = compatibleVersion.getProtocolType() == ProtocolVersion.ProtocolType.CLASSIC;
            if (yes) break;
        }

        return protocolVersion.getProtocolType() == ProtocolVersion.ProtocolType.CLASSIC || yes;
    }

    public boolean isRunningAsServer() {
        return protocolServer != null;
    }

    public boolean isRunningAsClient() {
        return protocolClient != null;
    }

    private boolean validateProtocolVersion() {
        return (isRunningAsServer() && protocolVersion.getProtocolSide() != ProtocolVersion.ProtocolSide.CLIENT) ||
                (isRunningAsClient() && protocolVersion.getProtocolSide() != ProtocolVersion.ProtocolSide.SERVER);
    }

    public boolean validateProtocolVersion(ProtocolVersion targetVersion) {
        return protocolVersion == targetVersion || protocolVersion.getCompatibleVersions().contains(targetVersion);
    }
}
