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
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/***
 * Utility class for retrieving version-related information.
 */
public class VersionUtil {

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

        if(tag.isEmpty()) {
            tag = properties.getProperty("git.tags", "Unknown");
        }
        if (commitCount.isEmpty()) {
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
