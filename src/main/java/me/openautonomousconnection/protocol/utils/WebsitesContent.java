/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.protocol.utils;

import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

public class WebsitesContent extends DefaultMethodsOverrider {

    public static final String DOMAIN_NOT_FOUND = """
            <html>
            <head>
                <title>404 - Domain not found</title>
                <meta content="UTF-8" name="charset"/>
                <meta content="Open Autonomous Connection" name="author"/>
                <meta content="Domain not found" name="description"/>
            </head>
            <body>
            <h1>404 - This domain was not found</h1>
            </body>
            </html>
            """;

    public static final String FILE_NOT_FOUND = """
            <html>
            <head>
                <title>404 - File not found</title>
                <meta content="UTF-8" name="charset"/>
                <meta content="Open Autonomous Connection" name="author"/>
                <meta content="File not found" name="description"/>
            </head>
            <body>
            <h1>404 - This file was not found</h1>
            </body>
            </html>
            """;

    public static final String DOMAIN_NOT_REACHABLE = """
            <html>
            <head>
                <title>504 - Site not reachable</title>
                <meta content="UTF-8" name="charset"/>
                <meta content="Open Autonomous Connection" name="author"/>
                <meta content="Site not reached" name="description"/>
            </head>
            <body>
            <h1>504 - This site is currently not reachable</h1>
            </body>
            </html>
            """;

    public static String ERROR_OCCURRED(String errorDetails) {
        return """
                <html>
                <head>
                    <title>500 - Error occurred</title>
                    <meta content="UTF-8" name="charset"/>
                    <meta content="Open Autonomous Connection" name="author"/>
                    <meta content="Site not reached" name="description"/>
                </head>
                <body>
                <h1>500 - Error occured while resolving domain!</h1>
                <h4>Details:</h2>
                <h5>""" + errorDetails + "</h5>" + """
                </body>
                </html>
                """;
    }

    public static String ERROR_OCCURRED = ERROR_OCCURRED("No specified details!");
}
