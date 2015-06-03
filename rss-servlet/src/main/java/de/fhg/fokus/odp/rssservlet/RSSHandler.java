/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS
 *
 * This file is part of Open Data Platform.
 *
 * Open Data Platform is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Open Data Plaform is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with Open Data Platform.  If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

package de.fhg.fokus.odp.rssservlet;

// imports
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

// TODO: Auto-generated Javadoc
/**
 * The class takes care of adapting the RSS feeds coming from CKAN.
 * 
 * @author Nikolay Tcholtchev, nikolay.tcholtchev@fokus.fraunhofer.de
 * 
 */
public class RSSHandler {

    /** The basic URL for a short link. */
    private static final String HREF_SHORT_LINK = "/web/guest/daten/-/details/";

    /** The title for the custom query. */
    private static final String TITLE = "govData.de: Suchanfrage";

    /** The subtitle for the custom query. */
    private static final String SUBTITLE = "govData.de: Suchanfrage";

    /** The local instance for the singleton pattern. */
    private static RSSHandler instance = null;

    /** The log. */
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    /**
     * The getInstance method for the class.
     * 
     * @return the singleton instance.
     */
    public static RSSHandler getInstance() {
        if (instance == null) {
            instance = new RSSHandler();
        }

        return instance;
    }

    /** The constructor for the singleton pattern. */
    private RSSHandler() {
    }

    /**
     * The method converts the CKAN rss feed to an OD platform one.
     * 
     * @param ckanRSSFeed
     *            the text of the RSS feed.
     * 
     * @return the converted text for the RSS feed or <b>null</b> if anything has gone wrong.
     */
    public String convertAtomFeed(String ckanRSSFeed) {

        // check the input
        if (ckanRSSFeed == null || ckanRSSFeed.matches("^\\s*$")) {
            return null;
        }

        // the variable to return
        String toreturn = null;

        // parse the xml text
        Document domDocument = null;
        try {
            domDocument = buildDocument(ckanRSSFeed);
        }
        // catch the exception thrown during parsing
        catch (ParserConfigurationException e) {
            LOG.error(e.getMessage());
            return null;
        } catch (SAXException e) {
            LOG.error(e.getMessage());
            return null;
        } catch (IOException e) {
            LOG.error(e.getMessage());
            return null;
        }

        // check the result of the parsing
        if (domDocument == null) {
            return null;
        }

        // get the root node of the document
        Node node = domDocument.getDocumentElement();
        if (node == null) {
            return null;
        }

        // get the node list
        NodeList nodeList = node.getChildNodes();
        if (nodeList == null) {
            return null;
        }

        // move over the top layer elements
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {

                if (currentNode.getNodeName().toLowerCase().trim().equals("entry")) {
                    // process an entry
                    processEntry(currentNode);
                }

                if (currentNode.getNodeName().toLowerCase().trim().equals("link")) {
                    // process a link on top level
                    processTopLevelLink(currentNode);
                }

                if (currentNode.getNodeName().toLowerCase().trim().equals("title")) {
                    // process title on top level
                    currentNode.setTextContent(TITLE);
                }

                if (currentNode.getNodeName().toLowerCase().trim().equals("subtitle")) {
                    // process subtitle on top level
                    currentNode.setTextContent(SUBTITLE);
                }

            }
        }

        // convert the DOM Document to XML String
        toreturn = convertDOMDocumentToXMLString(domDocument);

        return toreturn;
    }

    /**
     * This method converts a DOM document to an XML string.
     * 
     * @param domDocument
     *            the document to convert.
     * 
     * @return the resulting XML string.
     */
    private String convertDOMDocumentToXMLString(Document domDocument) {

        // the variable to return
        String toreturn = null;

        // the transformer objects
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = null;

        // setup the transformation and issue it
        try {
            trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(domDocument);
            trans.transform(source, result);
            toreturn = sw.toString();

        } catch (TransformerConfigurationException e) {
            LOG.error(e.getMessage());
            return null;
        } catch (TransformerException e) {
            LOG.error(e.getMessage());
            return null;
        }

        return toreturn;
    }

    /**
     * The method processes and modifies links on the top level of the feed.
     * 
     * @param linkNode
     *            the link node to process.
     */
    private void processTopLevelLink(Node linkNode) {

        // check the input parameter
        if (linkNode == null) {
            return;
        }

        // get the attributes
        NamedNodeMap attr = linkNode.getAttributes();
        if (attr == null) {
            return;
        }

        // get the href node
        Node href = attr.getNamedItem("href");
        if (href == null) {
            return;
        }

        // get the href node value
        String hrefNodeValue = href.getNodeValue();
        if (hrefNodeValue == null) {
            return;
        }

        // remove links to the search on CKAN
        if (hrefNodeValue.trim().contains("/ckan/dataset?")) {
            linkNode.getParentNode().removeChild(linkNode);
            return;
        }

        // remove links to the RSS on CKAN
        if (hrefNodeValue.trim().contains("/ckan/feeds/custom.atom?")) {
            hrefNodeValue = hrefNodeValue.replace("/ckan/feeds/custom.atom?", "/rss-servlet/webresources/rssservice?");
            // TODO: the upper link should be made configurable over a
            // properties file
            href.setNodeValue(hrefNodeValue);
        }
    }

    /**
     * The function processes an entry node.
     * 
     * @param entryNode
     *            the entry node to process.
     */
    private void processEntry(Node entryNode) {

        // check the input parameter
        if (entryNode == null) {
            return;
        }

        // get the node list
        NodeList nodeList = entryNode.getChildNodes();
        if (nodeList == null) {
            return;
        }

        // the node to change
        Node nodeToChange = null;

        // the value to change
        String hrefValueToReplaceWith = null;

        // move over the entry sub elements
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {

                if (currentNode.getNodeName().toLowerCase().trim().equals("link")) {
                    // proceed the links in an entry

                    // get the attributes
                    NamedNodeMap attr = currentNode.getAttributes();
                    if (attr == null) {
                        continue;
                    }

                    // pick the href attribute value first

                    // get the href node
                    Node href = attr.getNamedItem("href");
                    if (href == null) {
                        continue;
                    }

                    // get the href value
                    String hrefNodeValue = href.getNodeValue();
                    if (hrefNodeValue == null) {
                        continue;
                    }

                    // pick the rel attribute value second

                    // get the href node
                    Node rel = attr.getNamedItem("rel");
                    if (rel == null) {
                        continue;
                    }

                    // get the rel value
                    String relNodeValue = rel.getNodeValue();
                    if (relNodeValue == null) {
                        continue;
                    }

                    // extract the value to set into the node
                    if (relNodeValue.contains("enclosure")) {
                        if (hrefNodeValue.contains("/")) {
                            String name = hrefNodeValue.substring(hrefNodeValue.lastIndexOf("/") + 1);
                            hrefValueToReplaceWith = HREF_SHORT_LINK + name;
                        }
                    }

                    // set the node to change
                    if (relNodeValue.contains("alternate")) {
                        nodeToChange = currentNode;
                    }
                }
            }
        }

        // if no link node to be changed was found --> just leave
        if (nodeToChange == null) {
            return;
        }

        // if no suitable value to modify the present value with --> remove the
        // link node to change
        if (hrefValueToReplaceWith == null) {
            entryNode.removeChild(nodeToChange);
            return;
        }

        // change the value in question
        Node hrefToModify = nodeToChange.getAttributes().getNamedItem("href");
        if (hrefToModify != null) {
            hrefToModify.setNodeValue(hrefValueToReplaceWith);
        }

    }

    /**
     * 
     * The method prepares a DOM document for the submitted XML.
     * 
     * @param xmldata
     *            the xml data to parse.
     * 
     * @return DOM document with the parsed XML.
     * 
     * @throws ParserConfigurationException
     *             a configuration exception for the parser.
     * @throws SAXException
     *             a SAX exception thrown during the parsing of the xml text.
     * @throws IOException
     *             an exception potentially thrown by byte array stream.
     */
    private Document buildDocument(String xmldata) throws ParserConfigurationException, SAXException, IOException {

        // check the argument
        if (xmldata == null || xmldata.matches("^\\s*$")) {
            return null;
        }

        // prepare the factory and the document builder
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

        // Parse the XML file and build the Document object in RAM
        Document doc = docBuilder.parse(new ByteArrayInputStream(xmldata.getBytes()));
        if (doc == null) {
            return null;
        }

        // Normalize text representation.
        // Collapses adjacent text nodes into one node.
        doc.getDocumentElement().normalize();

        return doc;
    }
}
