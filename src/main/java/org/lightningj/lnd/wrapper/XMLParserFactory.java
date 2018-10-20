/************************************************************************
 *                                                                       *
 *  LightningJ                                                           *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU Lesser General Public License   *
 *  (LGPL-3.0-or-later)                                                  *
 *  License as published by the Free Software Foundation; either         *
 *  version 3 of the License, or any later version.                      *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/
package org.lightningj.lnd.wrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Factory class to retrieve Singleton XML Parser within the factory instance.
 *
 * Created by Philip Vendil.
 */
public class XMLParserFactory {


    private Map<String, XMLParser> xmlParsers = new HashMap<>();

    /**
     * Constructor for new XMLParser that initiates all supported versions of XML Parsers.
     *
     */
    public XMLParserFactory(){
        XMLParser v1Parser = new V1XMLParser();
        xmlParsers.put(v1Parser.getVersion(),v1Parser);
    }

    /**
     * Method to retrieve the XMLParser for a given version.
     *
     * @param version the version to retrieve.
     * @return the corresponding XMLParser.
     * @throws IllegalArgumentException if unsupported version found.
     */
    public XMLParser getXMLParser(String version) throws IllegalArgumentException{
        XMLParser retval = xmlParsers.get(version);
        if(retval == null){
            throw new IllegalArgumentException("Error no XML Parser with version " + version + " supported.");
        }
        return retval;
    }

    /**
     * Returns a set of supported XML Parser versions.
     *
     * @return a set of supported XML Parser versions.
     */
    public Set<String> getSupportedVersions(){
        return xmlParsers.keySet();
    }
}
