package github.openautonomousconnection.protocol.versions.v1_0_0.classic;

import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

class Classic_DomainUtils extends DefaultMethodsOverrider {

    public static String getTopLevelDomain(String url) throws MalformedURLException {
        URL uri = null;
        String tldString = null;

        if (url.startsWith(Classic_SiteType.PUBLIC.name + "://")) url = url.substring((Classic_SiteType.PUBLIC.name + "://").length());
        if (url.startsWith(Classic_SiteType.CLIENT.name + "://")) url = url.substring((Classic_SiteType.CLIENT.name + "://").length());
        if (url.startsWith(Classic_SiteType.SERVER.name + "://")) url = url.substring((Classic_SiteType.SERVER.name + "://").length());
        if (url.startsWith(Classic_SiteType.PROTOCOL.name + "://")) url = url.substring((Classic_SiteType.PROTOCOL.name + "://").length());
        if (url.startsWith(Classic_SiteType.LOCAL.name + "://")) url = url.substring((Classic_SiteType.LOCAL.name + "://").length());

        if (!url.startsWith("https://") && !url.startsWith("http://")) url = "https://" + url;

        uri = new URL(url);
        String[] domainNameParts = uri.getHost().split("\\.");
        tldString = domainNameParts[domainNameParts.length - 1];

        return tldString;
    }

    public static String getDomainName(String url) throws URISyntaxException, MalformedURLException {
        if (url.startsWith(Classic_SiteType.PUBLIC.name + "://")) url = url.substring((Classic_SiteType.PUBLIC.name + "://").length());
        if (url.startsWith(Classic_SiteType.CLIENT.name + "://")) url = url.substring((Classic_SiteType.CLIENT.name + "://").length());
        if (url.startsWith(Classic_SiteType.SERVER.name + "://")) url = url.substring((Classic_SiteType.SERVER.name + "://").length());
        if (url.startsWith(Classic_SiteType.PROTOCOL.name + "://")) url = url.substring((Classic_SiteType.PROTOCOL.name + "://").length());
        if (url.startsWith(Classic_SiteType.LOCAL.name + "://")) url = url.substring((Classic_SiteType.LOCAL.name + "://").length());

        if (!url.startsWith("https://") && !url.startsWith("http://")) url = "https://" + url;

        URI uri = new URI(url);
        String domain = uri.getHost().replace("." + getTopLevelDomain(url), "");
        return domain;
    }


    public static String getPath(String url) {
        if (!url.startsWith(Classic_SiteType.PUBLIC.name + "://") && !url.startsWith(Classic_SiteType.CLIENT.name + "://") &&
                !url.startsWith(Classic_SiteType.SERVER.name + "://") && !url.startsWith(Classic_SiteType.PROTOCOL.name + "://") &&
                !url.startsWith(Classic_SiteType.LOCAL.name + "://") && !url.startsWith("http") && !url.startsWith("https")) {
            url = Classic_SiteType.PUBLIC.name + "://" + url;
        }

        String[] split = url.split("/");
        if (split.length <= 3) return "";

        StringBuilder path = new StringBuilder();

        for (int i = 3; i < split.length; i++) path.append(split[i]).append("/");

        String pathStr = path.toString();
        if (pathStr.startsWith("/")) pathStr = pathStr.substring("/".length());
        if (pathStr.endsWith("/")) pathStr = pathStr.substring(0, pathStr.length() - "/".length());

        return pathStr;
    }
}