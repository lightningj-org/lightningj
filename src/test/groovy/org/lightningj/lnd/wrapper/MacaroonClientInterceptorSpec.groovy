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

import com.github.nitram509.jmacaroons.Macaroon
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.Metadata
import spock.lang.Specification

/**
 * Unit tests for MacaroonClientInterceptor.
 *
 * Created by Philip Vendil on 2018-02-04.
 */
class MacaroonClientInterceptorSpec extends Specification {

    MacaroonClientInterceptor interceptor
    MacaroonContext mockedMacaroonContext
    Macaroon macaroon
    ClientCall delegate
    Channel channel

    def setup(){
        mockedMacaroonContext = Mock(MacaroonContext)
        interceptor = new MacaroonClientInterceptor(mockedMacaroonContext)
        macaroon = Mock(Macaroon)
        delegate = Mock(ClientCall)
        channel = Mock(Channel)
        channel.newCall(_,_) >> delegate
    }

    def "Verify that interceptCall inserts a macaroon in call header if macaroon context is set and returns a macaroon"(){
        setup:
        Metadata headers = new Metadata()
        macaroon.serialize() >> "abc"
        when:
        interceptor.interceptCall(null,null,channel).start(null, headers)
        then:
        1 * mockedMacaroonContext.currentMacaroon >> macaroon
        headers.get(MacaroonClientInterceptor.MACAROON_METADATA_KEY) == "69b7"
        1 * delegate.start(null, headers)
    }

    def "Verify that interceptCall doesn't throw exception if macaroonContext is null"(){
        setup:
        Metadata headers = new Metadata()
        interceptor.macaroonContext = null
        when:
        interceptor.interceptCall(null,null,channel).start(null, headers)
        then:
        0 * mockedMacaroonContext.currentMacaroon
        headers.get(MacaroonClientInterceptor.MACAROON_METADATA_KEY) == null
        1 * delegate.start(null, headers)
    }

    def "Verify that interceptCall doesn't throw exception if macaroonContext.getCurrentMacaroon() return null"(){
        setup:
        Metadata headers = new Metadata()
        when:
        interceptor.interceptCall(null,null,channel).start(null, headers)
        then:
        1 * mockedMacaroonContext.currentMacaroon >> null
        headers.get(MacaroonClientInterceptor.MACAROON_METADATA_KEY) == null
        1 * delegate.start(null, headers)
    }
}
