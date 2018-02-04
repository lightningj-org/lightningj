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
 * Unit tests for StaticFileMacaroonContext.
 *
 * Created by Philip Vendil on 2018-02-04.
 */
class StaticFileMacaroonContextSpec extends Specification {

    def "Verify that the constructor is reads and parses the macaroon correctly and getCurrentMacaroon() returns the parsed macaroon"(){
        setup:
        File macaroonPath = new File(this.getClass().getResource("/admin.macaroon").path)
        when:
        StaticFileMacaroonContext macaroonContext = new StaticFileMacaroonContext(macaroonPath)
        then:
        macaroonContext.currentMacaroon.inspect() =~ """location lnd
identifier 0-5fc1b14ade4344d889a38696e4ac6680
signature c706ada85cea02671ed5b862e51ac79d34029fd9a9a4a02eb8ce86a9328ed84e
"""
    }

    def "Verify that ClientSideException is thrown if macaroon file is not found"(){
        when:
        new StaticFileMacaroonContext(new File("notexists.macaroon"))
        then:
        def e = thrown ClientSideException
        e.message == "Error reading macaroon from path 'notexists.macaroon', message: notexists.macaroon (No such file or directory)"
        e.status == null
    }


    def "Verify that ClientSideException is thrown if macaroon was unparsable"(){
        setup:
        File macaroonPath = new File(this.getClass().getResource("/invalid.macaroon").path)
        when:
        new StaticFileMacaroonContext(macaroonPath)
        then:
        def e = thrown ClientSideException
        e.message =~ "invalid.macaroon', message: Not enough data bytes available. Needed 21773 bytes, but was only 134"
        e.status == null
    }


}
