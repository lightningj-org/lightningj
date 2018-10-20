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

import io.grpc.Status
import spock.lang.Specification

/**
 * Unit tests for ServerSideException.
 *
 * Created by Philip Vendil.
 */
class ServerSideExceptionSpec extends Specification {

    def "Verify that constructor populated fields properly"(){
        when:
        ServerSideException e = new ServerSideException("SomeMessage", Status.ABORTED)
        then:
        e.message == "SomeMessage"
        e.status == Status.ABORTED
        when:
        e = new ServerSideException("SomeMessage", Status.ABORTED, new IOException())
        then:
        e.message == "SomeMessage"
        e.status == Status.ABORTED
        e.cause instanceof IOException
    }
}
