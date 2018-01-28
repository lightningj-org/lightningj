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

import io.grpc.Status
import spock.lang.Specification

/**
 * Unit tests CommunicationException.
 *
 * Created by Philip Vendil.
 */
class CommunicationExceptionSpec extends Specification {

    def "Verify that constructor populated fields properly"(){
        when:
        CommunicationException e = new CommunicationException("SomeMessage", Status.ABORTED)
        then:
        e.message == "SomeMessage"
        e.status == Status.ABORTED
        when:
        e = new CommunicationException("SomeMessage", Status.ABORTED, new IOException())
        then:
        e.message == "SomeMessage"
        e.status == Status.ABORTED
        e.cause instanceof IOException
    }
}
