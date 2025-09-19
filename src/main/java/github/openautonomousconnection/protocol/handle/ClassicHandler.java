package github.openautonomousconnection.protocol.handle;

import me.finn.unlegitlibrary.network.system.server.ConnectionHandler;

public abstract class ClassicHandler {

    public abstract void handleMessage(ConnectionHandler connectionHandler, String message);

}
