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

import io.grpc.ManagedChannel
import spock.lang.Specification

import java.util.logging.Logger

/**
 * Unit tests for SynchronousAPI methods.
 *
 * Created by Philip Vendil.
 */
class AsynchronousAPISpec extends Specification {

    AsynchronousLndAPI api = new AsynchronousLndAPI(Mock(ManagedChannel))

    def setup(){
        api.log = Mock(Logger)
    }

    def "AsynchronousLndAPI initializes constructors properly."(){
        when: // This constructor
        SynchronousLndAPI api1 = new SynchronousLndAPI("localhost",8080,new File("src/test/resources/cert.pem"))
        then:
        api1.channel != null
    }

}
