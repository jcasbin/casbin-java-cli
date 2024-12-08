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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/***
 * A custom SAX handler for parsing Maven POM files to retrieve the version
 * of a specific dependency based on its groupId and artifactId.
 */
public class DependencyHandler extends DefaultHandler {
    private final String targetGroupId;
    private final String targetArtifactId;
    private String currentElement = "";
    private String currentGroupId = null;
    private String currentArtifactId = null;
    private String version = null;
    private boolean isDependency = false;

    /**
     * Constructs a DependencyHandler instance.
     *
     * @param groupId    the groupId of the dependency to search for.
     * @param artifactId the artifactId of the dependency to search for.
     */
    public DependencyHandler(String groupId, String artifactId) {
        this.targetGroupId = groupId;
        this.targetArtifactId = artifactId;
    }

    /**
     * Gets the version of the target dependency.
     *
     * @return the version of the dependency if found, or null if not found.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Called when the start of an XML element is encountered.
     *
     * @param uri       the Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed.
     * @param localName the local name (without prefix), or the empty string if Namespace processing is not being performed.
     * @param qName     the qualified name (with prefix, if present).
     * @param attributes the attributes attached to the element. If there are no attributes, it shall be an empty Attributes object.
     * @throws SAXException if a SAX error occurs.
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currentElement = qName;

        if ("dependency".equals(qName)) {
            isDependency = true;
        }
    }

    /**
     * Called when the end of an XML element is encountered.
     *
     * @param uri       the Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed.
     * @param localName the local name (without prefix), or the empty string if Namespace processing is not being performed.
     * @param qName     the qualified name (with prefix, if present).
     * @throws SAXException if a SAX error occurs.
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("dependency".equals(qName)) {
            if (targetGroupId.equals(currentGroupId) && targetArtifactId.equals(currentArtifactId)) {
                isDependency = false;
            }

            currentGroupId = null;
            currentArtifactId = null;
        }
        currentElement = "";
    }

    /**
     * Called when character data inside an XML element is encountered.
     *
     * @param ch     the characters.
     * @param start  the start position in the character array.
     * @param length the number of characters to read from the array.
     * @throws SAXException if a SAX error occurs.
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (isDependency) {
            String content = new String(ch, start, length).trim();

            switch (currentElement) {
                case "groupId":
                    currentGroupId = content;
                    break;
                case "artifactId":
                    currentArtifactId = content;
                    break;
                case "version":
                    if (targetGroupId.equals(currentGroupId) && targetArtifactId.equals(currentArtifactId)) {
                        version = content;
                    }
                    break;
            }
        }
    }
}
