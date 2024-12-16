// Copyright 2024 The casbin Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.casbin.util;

import org.casbin.Client;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * Utility class for retrieving version-related information.
 */
public class VersionUtil {

    /***
     * Calculates the new version based on the current version and the type of commit.
     *
     * @param currentVersion The current version string in the format "vX.Y.Z".
     * @param commitType The type of commit that caused the version change (e.g., "fix", "feat", "breaking change").
     * @return The calculated new version string in the format "vX.Y.Z".
     */
    public static String calculateNewVersion(String currentVersion, String commitType) {
        String[] versionParts = currentVersion.substring(1).split("\\.");
        int major = Integer.parseInt(versionParts[0]);
        int minor = Integer.parseInt(versionParts[1]);
        int patch = Integer.parseInt(versionParts[2]);

        switch (commitType.toLowerCase()) {
            case "fix":
                patch++;
                break;
            case "feat":
                minor++;
                patch = 0;
                break;
            case "breaking change":
                major++;
                minor = 0;
                patch = 0;
                break;
            default:
                return currentVersion;
        }
        return "v" + major + "." + minor + "." + patch;
    }

    /**
     * Retrieves Cli version information.
     *
     * @return The Cli version info as a string (commit ID or tag name).
     * @throws IOException If an error occurs while reading the git.properties file.
     */
    public static String getCliVersion() throws IOException {
        Properties properties = new Properties();
        InputStream input = Client.class.getResourceAsStream("/META-INF/git.properties");
        if (input != null) {
            properties.load(input);
        }

        if (properties.isEmpty()) {
            throw new IOException("Git properties not found!");
        }

        String commitId = properties.getProperty("git.commit.id.abbrev", "Unknown");
        String tag = properties.getProperty("git.closest.tag.name", "Unknown");
        String commitCount = properties.getProperty("git.closest.tag.commit.count", "Unknown");

        String commitMessage = properties.getProperty("git.commit.message.full", "Unknown");
        Pattern pattern = Pattern.compile("^(fix|feat|chore|docs|style|refactor|test|perf|build|ci):");
        Matcher matcher = pattern.matcher(commitMessage);

        if(tag.isEmpty() || tag.equals("Unknown")) {
            tag = properties.getProperty("git.tags", "Unknown");
            if(tag.isEmpty() || tag.equals("Unknown")) {
                InputStream versionInputStream = Client.class.getResourceAsStream("/META-INF/lastCli.version");
                if (versionInputStream != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(versionInputStream, StandardCharsets.UTF_8));
                    String version =  reader.readLine();
                    if (version != null && matcher.find()) {
                        tag = calculateNewVersion(version, matcher.group(1));
                    }
                }
            }
        }else if(!commitCount.isEmpty() && !commitCount.equals("0") && !commitCount.equals("Unknown") && matcher.find()) {
            tag = calculateNewVersion(tag, matcher.group(1));
            commitCount = "0";
        }

        if ((commitCount.isEmpty() || commitCount.equals("0") || commitCount.equals("Unknown"))
                && (!tag.isEmpty() && !tag.equals("Unknown"))) {
            return tag;
        }

        return commitId;
    }

    /**
     * Retrieves Casbin version information.
     *
     * @param groupId The group ID of the dependency to search for.
     * @param artifactId The artifact ID of the dependency to search for.
     * @return The version of the specified dependency, or "Unknown" if not found.
     * @throws ParserConfigurationException If a configuration error occurs during parsing.
     * @throws SAXException If a SAX error occurs during parsing.
     * @throws IOException If an error occurs while reading the POM file.
     */
    public static String getCasbinVersion(String groupId, String artifactId) throws ParserConfigurationException, SAXException, IOException {
        InputStream inputStream = Client.class.getResourceAsStream("/META-INF/pom.xml");
        if (inputStream == null) {
            throw new IOException("POM file not found!");
        }

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        DependencyHandler handler = new DependencyHandler(groupId, artifactId);
        parser.parse(inputStream, handler);

        return handler.getDependencyVersion();
    }
}
