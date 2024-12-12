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

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;

/***
 * A custom handler for parsing XML dependencies (POM file format) using SAX (Simple API for XML).
 * This handler looks for a specific dependency identified by the group ID and artifact ID,
 * and extracts its version information.
 */
public class DependencyHandler extends DefaultHandler {
    private final String targetGroupId;
    private final String targetArtifactId;
    private String currentElement;
    private String currentGroupId;
    private String currentArtifactId;
    private String currentVersion;
    private String dependencyVersion;

    /**
     * Constructor to initialize the handler with the target groupId and artifactId.
     *
     * @param groupId The groupId of the dependency to search for.
     * @param artifactId The artifactId of the dependency to search for.
     */
    public DependencyHandler(String groupId, String artifactId) {
        this.targetGroupId = groupId;
        this.targetArtifactId = artifactId;
    }

    /**
     * Called when a new element is encountered during the XML parsing.
     *
     * @param uri The namespace URI (if any).
     * @param localName The local name of the element.
     * @param qName The qualified name of the element.
     * @param attributes The attributes of the element.
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        currentElement = qName;
        if ("dependency".equals(currentElement)) {
            currentGroupId = null;
            currentArtifactId = null;
            currentVersion = null;
        }
    }

    /**
     * Called when the end of an element is reached during XML parsing.
     *
     * @param uri The namespace URI (if any).
     * @param localName The local name of the element.
     * @param qName The qualified name of the element.
     */
    @Override
    public void endElement(String uri, String localName, String qName) {
        if ("dependency".equals(qName)) {
            if (targetGroupId.equals(currentGroupId) && targetArtifactId.equals(currentArtifactId)) {
                dependencyVersion = currentVersion;
            }
        }
        currentElement = null;
    }

    /**
     * Called to process the character data inside an element during XML parsing.
     *
     * @param ch The character array containing the text.
     * @param start The start index of the text.
     * @param length The length of the text.
     */
    @Override
    public void characters(char[] ch, int start, int length) {
        String content = new String(ch, start, length).trim();
        if (content.isEmpty()) {
            return;
        }

        if ("groupId".equals(currentElement)) {
            currentGroupId = content;
        } else if ("artifactId".equals(currentElement)) {
            currentArtifactId = content;
        } else if ("version".equals(currentElement)) {
            currentVersion = content;
        }
    }

    /**
     * Returns the version of the dependency if found, otherwise returns "Unknown".
     *
     * @return The version of the target dependency.
     */
    public String getDependencyVersion() {
        return dependencyVersion != null ? dependencyVersion : "Unknown";
    }
}
