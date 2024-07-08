# Open Autonomous Connection Protocol

This is the Protocol for our Open Autonomous Connection project.<br />
You can easily implement this Protocol via Maven.<br />
Feel free to join our Discord.
<br />
<br />
Discord: https://discord.gg/ef8cyW4t7p

# Bugs/Problems
# In progress
- Cleanup Code
# TODO
- Subdomains
- Path
- Fragments
- Query
- PHP & JavaScript support

# Maven
### pom.xml
```
<dependency>
    <groupId>me.openautonomousconnection.protocol</groupId>
    <artifactId>Protocol</artifactId>
    <version>1.0.0</version>
    <scope>compile</scope>
</dependency>
```

### Repository:
```
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/open-autonomous-connection/protocol</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

# Examples
#### Note: These examples are very basic
### Server
```java
import me.openautonomousconnection.protocol.ProtocolBridge;
import me.openautonomousconnection.protocol.ProtocolSettings;
import me.openautonomousconnection.protocol.ProtocolVersion;
import me.openautonomousconnection.protocol.side.ProtocolServer;

public class Server extends ProtocolServer {

    public Server() throws IOException, InterruptedException {
        super(10);
    }

    public static void main(String[] args) {
        try {
            ProtocolBridge protocolBridge = new ProtocolBridge(ProtocolVersion.PV_1_0_0, new ProtocolSettings(), new Server());
            protocolBridge.getProtocolServer().setProtocolBridge(protocolBridge);
            protocolBridge.getProtocolServer().startServer();
        } catch (IOException | InterruptedException | InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchMethodException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public List<Domain> getDomains() throws SQLException {
        return List.of(); // Your method here to get all registered domains
    }

    @Override
    public void handleMessage(int clientID, String message) {
        System.out.println("Received message: " + message + " from client: " + clientID);
    }
}
```
### Client

```java
import me.openautonomousconnection.protocol.ProtocolBridge;
import me.openautonomousconnection.protocol.ProtocolSettings;
import me.openautonomousconnection.protocol.ProtocolVersion;
import me.openautonomousconnection.protocol.side.ProtocolClient;

public class Client extends ProtocolClient {

    public static void main(String[] args) {
        try {
            ProtocolBridge protocolBridge = new ProtocolBridge(ProtocolVersion.PV_1_0_0, new ProtocolSettings(), new Client());
            protocolBridge.getProtocolClient().setProtocolBridge(protocolBridge);
            protocolBridge.getProtocolServer().startClient();
        } catch (IOException | InterruptedException | InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchMethodException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void handleHTMLContent(String htmlContent) {
        System.out.println("Website html content received:");
        System.out.println(htmlContent); // Render content in a webview for example
    }

    @Override
    public void handleMessage(String message) {
        System.out.println("Received message: " + message);
    }
}
```