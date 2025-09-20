package github.openautonomousconnection.protocol.versions.v1_0_0.beta;

import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Domain implements Serializable {
    @Getter
    private final String subname;
    @Getter
    private final String name;
    @Getter
    private final String topLevelName;
    @Getter
    private final String path;
    @Getter
    private final String query;
    @Getter
    private final String fragment;
    @Getter
    private final String protocol;

    public Domain(String fullDomain) {
        // Remove protocol
        String domainWithPath = fullDomain.contains("://") ? fullDomain.split("://", 2)[1] : fullDomain;
        this.protocol = fullDomain.contains("://") ? fullDomain.split("://", 2)[0] : "";

        // Cut path
        String[] domainPartsAndPath = domainWithPath.split("/", 2);
        String host = domainPartsAndPath[0];                  // z.B. hello.world.com
        String fullPath = domainPartsAndPath.length > 1 ? "/" + domainPartsAndPath[1] : "";

        // Split domain in labels
        List<String> labels = Arrays.asList(host.split("\\."));
        if (labels.size() < 2) throw new IllegalArgumentException("Invalid domain: " + host);

        this.topLevelName = labels.get(labels.size() - 1);
        this.name = labels.get(labels.size() - 2);
        this.subname = labels.size() > 2 ? String.join(".", labels.subList(0, labels.size() - 2)) : "";

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
    }

    public String getDestination() {
        return "404";
    }

}
