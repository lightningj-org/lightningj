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

import com.google.protobuf.GeneratedMessageV3
import org.lightningj.lnd.proto.LightningApi
import org.lightningj.lnd.wrapper.message.WalletBalanceRequest
import org.lightningj.lnd.wrapper.message.WalletBalanceResponse
import spock.lang.Specification

/**
 * Unit tests for WrapperFactory.
 *
 * Created by Philip Vendil.
 */
class WrapperFactorySpec extends Specification {

    WrapperFactory factory = new WrapperFactory()

    def "Verify that convertToWrapper converts API message to wrapper message correctly"(){
        when:
        WalletBalanceRequest o = factory.wrap(LightningApi.WalletBalanceRequest.getDefaultInstance())
        then:
        o.getApiObject() == LightningApi.WalletBalanceRequest.getDefaultInstance()

        when:
        WalletBalanceResponse o2 = factory.wrap(LightningApi.WalletBalanceResponse.newBuilder().setTotalBalance(123L).build())
        then:
        o2.totalBalance == 123L
    }

    def "Verify that ClientSideException it throws for invalid objects"(){
        when:
        factory.wrap(Mock(GeneratedMessageV3))
        then:
        def e = thrown ClientSideException
        e.message =~ 'Error converting GRPC object GeneratedMessageV3'
    }
}
