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
        macaroonContext.currentMacaroonAsHex == """303031316C6F636174696F6E206C6E640A303033326964656E74696669657220302D35666331623134616465343334346438383961333836393665346163363638300A303032667369676E617475726520C706ADA85CEA02671ED5B862E51AC79D34029FD9A9A4A02EB8CE86A9328ED84E0A"""
    }

    def "Verify that ClientSideException is thrown if macaroon file is not found"(){
        when:
        new StaticFileMacaroonContext(new File("notexists.macaroon"))
        then:
        def e = thrown ClientSideException
        e.message == "Error reading macaroon from path 'notexists.macaroon', message: notexists.macaroon (No such file or directory)"
        e.status == null
    }


}
