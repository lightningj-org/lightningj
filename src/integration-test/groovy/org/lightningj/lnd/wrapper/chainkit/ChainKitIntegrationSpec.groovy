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
package org.lightningj.lnd.wrapper.chainkit


import spock.lang.Shared
import spock.lang.Specification

/**
 * Integration tests running the chainkit client APIs against a live test-net LND node.
 */
class ChainKitIntegrationSpec extends Specification{


    @Shared String lndHost
    @Shared int lndPort
    @Shared File tlsCertPath
    @Shared File macaroonPath

    SynchronousChainKitAPI synchronousAPI
    AsynchronousChainKitAPI asynchronousAPI

    def setupSpec(){
        lndHost = System.getProperty("lightningj.integration.test.lnd.host")
        lndPort = Integer.parseInt(System.getProperty("lightningj.integration.test.lnd.port"))
        tlsCertPath = new File(System.getProperty("lightningj.integration.test.lnd.tlscertpath"))
        macaroonPath = new File(System.getProperty("lightningj.integration.test.lnd.macaroonpath"))
    }

    def setup(){
        asynchronousAPI = new AsynchronousChainKitAPI(lndHost,lndPort,tlsCertPath, macaroonPath)
        synchronousAPI = new SynchronousChainKitAPI(lndHost,lndPort,tlsCertPath, macaroonPath)
    }

    def "Test to verify chainkit api is available"(){
        expect:
        synchronousAPI.getBlockHash(123L) != null
    }


}
