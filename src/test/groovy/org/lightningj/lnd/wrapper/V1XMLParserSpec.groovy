/************************************************************************
 *                                                                       *
 *  LightningJ                                                           *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU General Public License          *
 *  License as published by the Free Software Foundation; either         *
 *  version 3 of the License, or any later version.                      *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/
package org.lightningj.lnd.wrapper

import spock.lang.Specification

/**
 * Unit tests for V1XMLParser.
 *
 * Created by Philip Vendil.
 */
class V1XMLParserSpec extends Specification {

    def "Verify that abstract method returns correct values"(){
        setup:
        V1XMLParser p = new V1XMLParser()
        expect:
        p.getVersion() == "1.0"
        p.getSchemaLocation() == "/lnd_v1.xsd"
        p.getJAXBClassPath() == "org.lightningj.lnd.wrapper.message"
    }
}
