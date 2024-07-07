/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.protocol;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Utils {

    public static String getTopLevelDomain(String url) throws MalformedURLException {
        URL uri = null;
        String tldString = null;

        if (url.startsWith("oac://")) url = url.substring(6);
        if (!url.startsWith("https://")) url = "https://" + url;

        uri = new URL(url);
        String[] domainNameParts = uri.getHost().split("\\.");
        tldString = domainNameParts[domainNameParts.length - 1];

        return tldString;
    }

    public static String getDomainName(String url) throws URISyntaxException, MalformedURLException {
        if (url.startsWith("oac://")) url = url.substring(6);
        if (!url.startsWith("https://")) url = "https://" + url;

        URI uri = new URI(url);
        String domain = uri.getHost().replace("." + getTopLevelDomain(url), "");
        return domain;
    }


    public static String getPath(String url) {
        if (!url.startsWith("oac://") && !url.startsWith("http://") && !url.startsWith("https://"))
            url = "oac://" + url;

        String[] split = url.split("/");
        if (split.length <= 3) return null;

        StringBuilder path = new StringBuilder();

        for (int i = 3; i < split.length; i++) path.append(split[i]).append("/");

        return path.toString();
    }
}