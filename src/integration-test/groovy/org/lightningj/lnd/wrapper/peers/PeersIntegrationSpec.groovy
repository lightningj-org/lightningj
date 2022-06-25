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
package org.lightningj.lnd.wrapper.peers


import org.lightningj.lnd.wrapper.neutrino.AsynchronousNeutrinoAPI
import org.lightningj.lnd.wrapper.neutrino.SynchronousNeutrinoAPI
import org.lightningj.lnd.wrapper.peers.message.UpdateAddressAction
import org.lightningj.lnd.wrapper.peers.message.UpdateFeatureAction
import spock.lang.Shared
import spock.lang.Specification

import java.security.SecureRandom

/**
 * Integration tests running the nautrino APIs against a live test-net LND node.
 */
class PeersIntegrationSpec extends Specification{

    @Shared String lndHost
    @Shared int lndPort
    @Shared File tlsCertPath
    @Shared File macaroonPath

    SynchronousPeersAPI synchronousAPI
    AsynchronousPeersAPI asynchronousAPI

    def setupSpec(){
        lndHost = System.getProperty("lightningj.integration.test.lnd.host")
        lndPort = Integer.parseInt(System.getProperty("lightningj.integration.test.lnd.port"))
        tlsCertPath = new File(System.getProperty("lightningj.integration.test.lnd.tlscertpath"))
        macaroonPath = new File(System.getProperty("lightningj.integration.test.lnd.macaroonpath"))
    }

    def setup(){
        asynchronousAPI = new AsynchronousPeersAPI(lndHost,lndPort,tlsCertPath, macaroonPath)
        synchronousAPI = new SynchronousPeersAPI(lndHost,lndPort,tlsCertPath, macaroonPath)
    }

    //@Ignore // Wait until proper instructions on how to enable to API.
    def "Test to verify peers api is available"(){
        expect:
        synchronousAPI.updateNodeAnnouncement([],null,randomString(),[])
    }

    static final char[] AliasCharset = "abcdefghijklmnopqrstqvst".toCharArray()

    String randomString(){
        char[] retval = new char[10]
        for(int i=0;i<10;i++){
            retval[i] = AliasCharset[SecureRandom.getInstanceStrong().nextInt(AliasCharset.length-1)]
        }
        return new String(retval)
    }


}
