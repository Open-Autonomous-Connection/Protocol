package github.openautonomousconnection.protocol;

import github.openautonomousconnection.protocol.handle.ClassicHandler;
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
    private ClassicHandler classicHandler;

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
