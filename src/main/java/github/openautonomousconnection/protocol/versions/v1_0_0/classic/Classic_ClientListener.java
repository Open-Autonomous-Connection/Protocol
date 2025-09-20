package github.openautonomousconnection.protocol.versions.v1_0_0.classic;

import github.openautonomousconnection.protocol.ProtocolBridge;
import github.openautonomousconnection.protocol.packets.v1_0_0.classic.Classic_PingPacket;
import me.finn.unlegitlibrary.event.EventListener;
import me.finn.unlegitlibrary.event.Listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Classic_ClientListener extends EventListener {

    @Listener
    public void onDomain(Classic_DomainPacketReceivedEvent event) {
        boolean exists = event.domain != null;

        if (exists) {
            try {
                if (!ProtocolBridge.getInstance().getProtocolClient().getNetworkClient().sendPacket(new Classic_PingPacket(event.requestDomain, event.domain, false))) {
                    ProtocolBridge.getInstance().getClassicHandlerClient().handleHTMLContent(Classic_SiteType.PROTOCOL, new Classic_LocalDomain("error-occurred", "html", ""),
                            Classic_WebsitesContent.ERROR_OCCURRED(event.domain + "/" + event.domain.path));
                }
            } catch (IOException | ClassNotFoundException e) {
                ProtocolBridge.getInstance().getClassicHandlerClient().handleHTMLContent(Classic_SiteType.PROTOCOL, new Classic_LocalDomain("error-occurred", "html", ""),
                        Classic_WebsitesContent.ERROR_OCCURRED(event.domain + "/" + event.domain.path + ":\n" + e.getMessage()));
            }
        } else
            ProtocolBridge.getInstance().getClassicHandlerClient().handleHTMLContent(Classic_SiteType.PROTOCOL, new Classic_LocalDomain("domain-not-found", "html", ""), Classic_WebsitesContent.DOMAIN_NOT_FOUND);
    }

    @Listener
    public void onPing(Classic_PingPacketReceivedEvent event) {
        if (event.reachable) {
            String destination = event.domain.getDomain().getDestination();

            try {
                URL url = new URL(destination);
                HttpURLConnection connection2 = (HttpURLConnection) url.openConnection();
                connection2.setRequestMethod("GET");

                StringBuilder content = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection2.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) content.append(line);
                }

                ProtocolBridge.getInstance().getClassicHandlerClient().handleHTMLContent(Classic_SiteType.PUBLIC, event.domain, content.toString());
            } catch (IOException exception) {
                ProtocolBridge.getInstance().getClassicHandlerClient().handleHTMLContent(Classic_SiteType.PROTOCOL, new Classic_LocalDomain("error-occurred", "html", ""),
                        Classic_WebsitesContent.ERROR_OCCURRED(exception.getMessage().replace(event.domain.getDomain().getDestination(), event.domain + "/" + event.domain.path)));
            }
        } else
            ProtocolBridge.getInstance().getClassicHandlerClient().handleHTMLContent(Classic_SiteType.PROTOCOL, new Classic_LocalDomain("error-not-reached", "html", ""), Classic_WebsitesContent.DOMAIN_NOT_REACHABLE);
    }

    @Override
    protected final Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public final String toString() {
        return super.toString();
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }
}