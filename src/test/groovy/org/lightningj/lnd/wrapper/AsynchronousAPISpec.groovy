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

import io.grpc.ManagedChannel
import spock.lang.Specification

import java.util.logging.Logger

/**
 * Unit tests for AsynchronousAPI methods.
 *
 * Created by Philip Vendil.
 */
class AsynchronousAPISpec extends Specification {

    AsynchronousLndAPI api = new AsynchronousLndAPI(Mock(ManagedChannel))

    def setup(){
        api.log = Mock(Logger)
    }

    def "AsynchronousLndAPI initializes constructors properly."(){
        setup:
        File macaroonPath = new File(this.getClass().getResource("/admin.macaroon").path)
        when: // This constructor
        AsynchronousLndAPI api1 = new AsynchronousLndAPI("localhost",8080,new File("src/test/resources/cert.pem"), macaroonPath)
        then:
        api1.channel != null
    }

}
