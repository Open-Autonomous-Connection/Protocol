# Open Autonomous Connection Protocol

This is the Protocol for our Open Autonomous Connection project.<br />
You can easily implement this Protocol via Maven.<br />
Feel free to join our Discord.
<br />

## License Notice

This project (OAC) is licensed under the [Open Autonomous Public License (OAPL)](https://github.com/Open-Autonomous-Connection/OAPL/blob/main/LICENSE).

**Third-party components:**
- *UnlegitLibrary* is authored by the same copyright holder and is used here under a special agreement:  
  While UnlegitLibrary is generally distributed under the [GNU GPLv3](https://github.com/UnlegitDqrk/UnlegitLibrary/blob/master/LICENSE),  
  it is additionally licensed under OAPL **exclusively for the OAC project**.  
  Therefore, within OAC, the OAPL terms apply to UnlegitLibrary as well.

# Bugs/Problems
# In progress
# TODO

# Maven
### pom.xml
```
<dependency>
    <groupId>github.openautonomousconnection</groupId>
    <artifactId>protocol</artifactId>
    <version>VERSION</version>
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
import me.finn.unlegitlibrary.network.system.server.ConnectionHandler;
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
    public void handleMessage(ConnectionHandler connectionHandler, String message) {
        System.out.println("Received message: " + message + " from client: " + connectionHandler.getClientID());
    }
}
```
### Client

```java
import me.openautonomousconnection.protocol.ProtocolBridge;
import me.openautonomousconnection.protocol.ProtocolSettings;
import me.openautonomousconnection.protocol.ProtocolVersion;
import me.openautonomousconnection.protocol.domain.Domain;
import me.openautonomousconnection.protocol.side.ProtocolClient;
import me.openautonomousconnection.protocol.utils.SiteType;

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
    public void handleHTMLContent(SiteType siteType, Domain domain, String htmlContent) {
        System.out.println("Website html content received. This site is " + siteType.name);
        System.out.println(htmlContent); // Render content in a webview for example
    }

    @Override
    public void handleMessage(String message) {
        System.out.println("Received message: " + message);
    }
}
```
