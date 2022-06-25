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
package org.lightningj.lnd.wrapper.dev


import org.lightningj.lnd.wrapper.peers.AsynchronousPeersAPI
import org.lightningj.lnd.wrapper.peers.SynchronousPeersAPI
import spock.lang.Shared
import spock.lang.Specification

/**
 * Integration tests running the nautrino APIs against a live test-net LND node.
 */
class DevIntegrationSpec extends Specification{

    @Shared String lndHost
    @Shared int lndPort
    @Shared File tlsCertPath
    @Shared File macaroonPath

    SynchronousDevAPI synchronousAPI
    AsynchronousDevAPI asynchronousAPI

    def setupSpec(){
        lndHost = System.getProperty("lightningj.integration.test.lnd.host")
        lndPort = Integer.parseInt(System.getProperty("lightningj.integration.test.lnd.port"))
        tlsCertPath = new File(System.getProperty("lightningj.integration.test.lnd.tlscertpath"))
        macaroonPath = new File(System.getProperty("lightningj.integration.test.lnd.macaroonpath"))
    }

    def setup(){
        asynchronousAPI = new AsynchronousDevAPI(lndHost,lndPort,tlsCertPath, macaroonPath)
        synchronousAPI = new SynchronousDevAPI(lndHost,lndPort,tlsCertPath, macaroonPath)
    }

    def "Test to verify dev api is available"(){
        expect:
        synchronousAPI.importGraph([],[])
    }


}
