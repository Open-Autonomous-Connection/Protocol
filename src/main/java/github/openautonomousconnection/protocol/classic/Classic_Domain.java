package github.openautonomousconnection.protocol.classic;

import me.finn.unlegitlibrary.string.StringUtils;

import java.io.Serializable;

public class Classic_Domain implements Serializable {
    public final String name;
    public final String topLevelDomain;
    private final String destination;
    private final String path;

    public Classic_Domain(String name, String topLevelDomain, String destination, String path) {
        if (path == null) path = "";

        this.name = name;
        this.topLevelDomain = topLevelDomain;
        this.destination = destination;
        this.path = path;
    }

    public final String realDestination() {
        String tmpDestination = destination.endsWith("/") ? destination : destination + "/";
        String tmpPath = getPath();

        if (tmpPath == null) tmpPath = "";
        if (tmpPath.startsWith("/")) tmpPath = tmpPath.substring("/".length());
        if (tmpPath.endsWith("/")) tmpPath = tmpPath.substring(0, tmpPath.length() - "/".length());

        return tmpDestination + tmpPath;
    }

    public final String getPath() {
        if (path.endsWith("/")) return path.substring(0, path.length() - "/".length());
        if (path.startsWith("/")) return path.substring("/".length());
        return path;
    }

    public final String parsedDestination() {
        if (destination.toLowerCase().startsWith("https://github.com/")) {
            String base = "https://raw.githubusercontent.com/";
            String username = Classic_DomainUtils.getPath(destination).split("/")[0];
            String site = Classic_DomainUtils.getPath(destination).split("/")[1];

            String tmpPath = getPath();
            if (tmpPath == null || StringUtils.isEmptyString(tmpPath)) tmpPath = "index.html";
            if (tmpPath.startsWith("/")) tmpPath = tmpPath.substring("/".length());
            if (tmpPath.endsWith("/")) tmpPath = tmpPath.substring(0, tmpPath.length() - "/".length());

            base = base + username + "/" + site + "/main/" + tmpPath;
            return base;
        }

        return realDestination();
    }

    @Override
    protected final Object clone() throws CloneNotSupportedException {
        return new Classic_Domain(name, topLevelDomain, destination, path);
    }

    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof Classic_Domain other)) return false;
        return other.name.equalsIgnoreCase(name) && other.topLevelDomain.equalsIgnoreCase(topLevelDomain);
    }

    @Override
    public final String toString() {
        return "{parsed='" + parsedDestination() + "';real='" + realDestination() + "'}";
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }
}
