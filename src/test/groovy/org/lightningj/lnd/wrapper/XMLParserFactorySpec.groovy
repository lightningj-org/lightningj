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
package org.lightningj.lnd.wrapper

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Unit test for XMLParserFactory.
 *
 * Created by Philip Vendil
 */
class XMLParserFactorySpec extends Specification {

    XMLParserFactory factory = new XMLParserFactory()

    def "Verify that getSupportedVersions returns all supported versions"(){
        expect:
        factory.supportedVersions.contains("1.0")
    }

    @Unroll
    def "Verify that getXMLParser returns correct XMLParser for version #version"(){
        expect:
        factory.getXMLParser(version).class == expectedClass
        where:
        version       | expectedClass
        "1.0"         | V1XMLParser.class

    }

    def "Verify that getXMLParser throws IllegalArgumentException for unsupported version"(){
        when:
        factory.getXMLParser("0.0")
        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Error no XML Parser with version 0.0 supported."
    }
}
