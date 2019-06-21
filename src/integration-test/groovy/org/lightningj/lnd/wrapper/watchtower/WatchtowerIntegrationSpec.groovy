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
package org.lightningj.lnd.wrapper.watchtower


import org.lightningj.lnd.wrapper.autopilot.AsynchronousAutopilotAPI
import org.lightningj.lnd.wrapper.autopilot.SynchronousAutopilotAPI
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

/**
 * Integration tests running the watchtower APIs against a live test-net LND node.
 */
@Ignore // Calling watchtower api seems to crash LND.
class WatchtowerIntegrationSpec extends Specification{

    @Shared String lndHost
    @Shared int lndPort
    @Shared File tlsCertPath
    @Shared File macaroonPath

    SynchronousWatchtowerAPI synchronousAPI
    AsynchronousWatchtowerAPI asynchronousAPI

    def setupSpec(){
        lndHost = System.getProperty("lightningj.integration.test.lnd.host")
        lndPort = Integer.parseInt(System.getProperty("lightningj.integration.test.lnd.port"))
        tlsCertPath = new File(System.getProperty("lightningj.integration.test.lnd.tlscertpath"))
        macaroonPath = new File(System.getProperty("lightningj.integration.test.lnd.macaroonpath"))
    }

    def setup(){
        asynchronousAPI = new AsynchronousWatchtowerAPI(lndHost,lndPort,tlsCertPath, macaroonPath)
        synchronousAPI = new SynchronousWatchtowerAPI(lndHost,lndPort,tlsCertPath, macaroonPath)
    }

    def "Test to verify watchtower api is available"(){
        expect:
        synchronousAPI.getInfo() != null
    }


}
