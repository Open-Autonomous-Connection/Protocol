package github.openautonomousconnection.protocol.versions.v1_0_0.beta;

import github.openautonomousconnection.protocol.ProtocolBridge;
import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Domain implements Serializable {
    public static class DefaultDomains {
        public static final Domain DNS_INFO_SITE = new Domain("oac://about.oac/");
        public static final Domain DNS_REGISTER_SITE = new Domain("oac://register.oac/");

        public static Domain TLN_INFO_SITE(String topLevelName) {
            return new Domain("oac://about." + topLevelName + "/");
        }
    }

    @Getter
    private final String subname;
    @Getter
    private final String name;
    @Getter
    private final String topLevelName;
    @Getter
    private String path;
    @Getter
    private String query;
    @Getter
    private String fragment;
    @Getter
    private String protocol;

    public Domain(String fullDomain) {
        // Remove protocol
        String domainWithPath = fullDomain.contains("://") ? fullDomain.split("://", 2)[1] : fullDomain;
        this.protocol = fullDomain.contains("://") ? fullDomain.split("://", 2)[0] : "";
        if (this.protocol.endsWith("://")) this.protocol = this.protocol.substring(0, this.protocol.length() - "://".length());

        // Cut path
        String[] domainPartsAndPath = domainWithPath.split("/", 2);
        String host = domainPartsAndPath[0];                  // z.B. hello.world.com
        String fullPath = domainPartsAndPath.length > 1 ? "/" + domainPartsAndPath[1] : "";

        // Split domain in labels
        List<String> labels = Arrays.asList(host.split("\\."));
        if (labels.size() < 2) throw new IllegalArgumentException("Invalid domain: " + host);

        this.topLevelName = labels.getLast();
        this.name = labels.get(labels.size() - 2);
        this.subname = labels.size() > 2 ? String.join(".", labels.subList(0, labels.size() - 2)) : null;

        if (fullPath.contains("#")) {
            this.fragment = "#" + Arrays.stream(fullPath.split("#")).toList().getLast();
            fullPath = fullPath.substring(0, fullPath.length() - ("#" + fragment).length());
        } else this.fragment = "";

        // Split path and query
        if (fullPath.contains("?")) {
            String[] parts = fullPath.split("\\?", 2);
            this.path = parts[0];
            this.query = parts[1];
        } else {
            this.path = fullPath;
            this.query = "";
        }

        if (this.path.startsWith("/")) this.path = this.path.substring(1);
        if (this.path.endsWith("/")) this.path = this.path.substring(0, this.path.length() - 1);

        if (this.query.startsWith("?")) this.query = this.query.substring(1);
        if (this.fragment.startsWith("#")) this.fragment = this.fragment.substring(1);
    }

    public final boolean hasSubname() {
        return subname != null;
    }

    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof Domain domain)) return false;

        return domain.getSubname().equalsIgnoreCase(this.subname) && domain.getName().equalsIgnoreCase(this.name) &&
                domain.getTopLevelName().equalsIgnoreCase(this.topLevelName) && domain.getProtocol().equalsIgnoreCase(this.protocol);
    }

    public final String getDestination() {
        if (this.equals(DefaultDomains.DNS_INFO_SITE)) return ProtocolBridge.getInstance().getProtocolServer().getDNSInfoSite();
        if (this.equals(DefaultDomains.DNS_REGISTER_SITE)) return ProtocolBridge.getInstance().getProtocolServer().getDNSRegisterSite();
        if (this.name.equalsIgnoreCase("about") && this.protocol.equalsIgnoreCase("oac")) return ProtocolBridge.getInstance().getProtocolServer().getTLNInfoSite(topLevelName);

        return !hasSubname() ? ProtocolBridge.getInstance().getProtocolServer().getDomainDestination(this) : ProtocolBridge.getInstance().getProtocolServer().getSubnameDestination(this, subname);
    }

}
