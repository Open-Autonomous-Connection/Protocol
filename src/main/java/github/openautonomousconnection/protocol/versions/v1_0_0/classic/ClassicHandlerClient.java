package github.openautonomousconnection.protocol.versions.v1_0_0.classic;

public abstract class ClassicHandlerClient {

    public abstract void unsupportedClassicPacket(String classicPacketClassName, Object[] content);

    public abstract void handleHTMLContent(Classic_SiteType siteType, Classic_Domain domain, String html);

    public abstract void handleMessage(String message);
}
