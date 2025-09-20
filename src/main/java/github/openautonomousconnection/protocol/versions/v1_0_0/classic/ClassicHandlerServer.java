package github.openautonomousconnection.protocol.versions.v1_0_0.classic;

import github.openautonomousconnection.protocol.side.server.ConnectedProtocolClient;
import me.finn.unlegitlibrary.network.system.server.ConnectionHandler;

import java.sql.SQLException;

public abstract class ClassicHandlerServer {
    public abstract void handleMessage(ConnectedProtocolClient client, String message, Classic_ProtocolVersion protocolVersion);
    public abstract Classic_Domain getDomain(Classic_RequestDomain requestDomain) throws SQLException;
    public abstract Classic_Domain ping(Classic_RequestDomain requestDomain) throws SQLException;
    public abstract void unsupportedClassicPacket(String className, Object[] content, ConnectedProtocolClient client);
}
