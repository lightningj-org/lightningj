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

import spock.lang.Specification
import spock.lang.Unroll

import static io.grpc.Status.*
/**
 * Unit tests for StatusExceptionWrapper.
 *
 * Created by Philip Vendil.
 */
class StatusExceptionWrapperSpec extends Specification {
    StatusExceptionWrapper exceptionWrapper = new StatusExceptionWrapper()

    @Unroll
    def "Verify that wrap converts to grpc StatusException with Code #code into exception with type #type"(){

        when:
        StatusException e = exceptionWrapper.wrap(new io.grpc.StatusException(code))
        then:
        e.getClass().getSimpleName() == type
        e.getStatus() == code

        where:
        code                | type
        CANCELLED           | "ClientSideException"
        UNKNOWN             | "ServerSideException"
        INVALID_ARGUMENT    | "ClientSideException"
        DEADLINE_EXCEEDED   | "CommunicationException"
        NOT_FOUND           | "ClientSideException"
        ALREADY_EXISTS      | "ClientSideException"
        PERMISSION_DENIED   | "ClientSideException"
        RESOURCE_EXHAUSTED  | "ServerSideException"
        FAILED_PRECONDITION | "ServerSideException"
        ABORTED             | "ServerSideException"
        OUT_OF_RANGE        | "ClientSideException"
        UNIMPLEMENTED       | "ServerSideException"
        INTERNAL            | "ServerSideException"
        UNAVAILABLE         | "CommunicationException"
        DATA_LOSS           | "ServerSideException"
        UNAUTHENTICATED     | "ClientSideException"

    }

    def "Verify that both io.grpc.StatusException and io.grpc.StatusRuntimeException is wrapped"(){
        when:
        StatusException e = exceptionWrapper.wrap(new io.grpc.StatusRuntimeException(ABORTED))
        then:
        e.getClass().getSimpleName() == "ServerSideException"
        e.getStatus() == ABORTED
    }

    def "Verify that exceptions that isn't io.grpc.StatusException nor io.grpc.StatusRuntimeException generates ServerSideException"(){
        when:
        StatusException e = exceptionWrapper.wrap(new IOException())
        then:
        e.getClass().getSimpleName() == "ServerSideException"
        e.getStatus() == null
        e.message == "Internal Error, cannot convert exception of type: IOException"
    }

    def "Verify that io.grpc.StatusException with status OK generates assertion error"(){
        when:
        exceptionWrapper.wrap(new io.grpc.StatusRuntimeException(OK))
        then:
        thrown(java.lang.AssertionError)
    }

}
