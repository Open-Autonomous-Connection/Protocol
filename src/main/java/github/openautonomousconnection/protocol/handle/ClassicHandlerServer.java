package github.openautonomousconnection.protocol.handle;

import github.openautonomousconnection.protocol.classic.Classic_Domain;
import github.openautonomousconnection.protocol.classic.Classic_RequestDomain;
import me.finn.unlegitlibrary.network.system.server.ConnectionHandler;

import java.sql.SQLException;

public abstract class ClassicHandlerServer {
    public abstract void handleMessage(ConnectionHandler connectionHandler, String message);
    public abstract Classic_Domain getDomain(Classic_RequestDomain requestDomain) throws SQLException;
    public abstract Classic_Domain ping(Classic_RequestDomain requestDomain) throws SQLException;
}
