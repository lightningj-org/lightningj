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

import io.grpc.stub.StreamObserver
import org.lightningj.lnd.proto.LightningApi
import org.lightningj.lnd.wrapper.message.WalletBalanceResponse
import spock.lang.Specification

import java.util.logging.Level
import java.util.logging.Logger

import static io.grpc.Status.ABORTED
import static io.grpc.Status.CANCELLED

/**
 * Unit tests for StreamObserverWrapper.
 *
 * Created by Philip Vendil.
 */
class StreamObserverWrapperSpec extends Specification {

    def streamObserverMock = Mock(StreamObserver)
    StreamObserverWrapper sow = new StreamObserverWrapper(streamObserverMock,true,"WalletBalanceResponse")

    def setup(){
        StreamObserverWrapper.log = Mock(Logger)
    }

    def "Verify that constructor initializes properly"(){
        expect:
        sow.messageType == "WalletBalanceResponse"
        sow.wrappedObserver == streamObserverMock
        sow.performValidation
        sow.exceptionWrapper instanceof StatusExceptionWrapper
        sow.wrapperFactory instanceof WrapperFactory
    }


   def "Verify that when calling onNext is Api Object is converted, logged and the wrapped object forwarded."(){
       setup:
       LightningApi.WalletBalanceResponseOrBuilder apiObject = LightningApi.WalletBalanceResponse.newBuilder()
       apiObject.setConfirmedBalance(123L)

       when:
       sow.onNext(apiObject.build())
       then:
       1 * streamObserverMock.onNext(_ as WalletBalanceResponse) >> {WalletBalanceResponse w ->
           assert w.getConfirmedBalance() ==  123L}
       1 * sow.log.fine('Received streamed message: WalletBalanceResponse: \n{\n    "totalBalance": 0,\n    "confirmedBalance": 123,\n    "unconfirmedBalance": 0,\n    "accountBalance": [\n    ]\n}')
   }

   def "Verify that if performValidation is false is not validate() called on converted message"(){
       setup:
       sow.performValidation = false

       sow.wrapperFactory = Mock(WrapperFactory)
       def mockedMessage =  Mock(Message)
       sow.wrapperFactory.wrap(_) >> { return mockedMessage}
       when:
       sow.onNext(LightningApi.WalletBalanceResponse.newBuilder().build())
       then:
       1 * streamObserverMock.onNext(_ as Message)
       0 * mockedMessage.validate()
   }

    def "Verify that if performValidation is true and message does not validate is ValidationException thrown instead."(){
        setup:
        sow.wrapperFactory = Mock(WrapperFactory)
        def mockedMessage =  Mock(Message)
        sow.wrapperFactory.wrap(_) >> { return mockedMessage}
        when:
        sow.onNext(LightningApi.WalletBalanceResponse.newBuilder().build())
        then:
        1 * streamObserverMock.onError(_ as ValidationException)
        1 * mockedMessage.validate()>> {
            def vr = new ValidationResult("WalletBalanceResponse")
            vr.messageErrors << new ValidationProblems("WalletBalanceResponse", "total_balance","somekey","Some description")
            return vr
        }
    }

    def "Verify that if internal error occurred during onNext() convertion is onError called instead."(){
        setup:
        setup:
        sow.wrapperFactory = Mock(WrapperFactory)
        sow.wrapperFactory.wrap(_) >> { throw new ClientSideException("SomeMessage", ABORTED)}
        when:
        sow.onNext(LightningApi.WalletBalanceResponse.newBuilder().build())
        then:
        1 * streamObserverMock.onError(_ as ClientSideException)

    }


    /*

           sow.wrapperFactory = Mock(WrapperFactory)
       def mockedMessage =  Mock(Message)
       mockedMessage.validate() >> { def vr = new ValidationResult("WalletBalanceResponse")
                                     vr.messageErrors << new ValidationProblems("WalletBalanceResponse", "total_balance","somekey","Some description")
                                     return vr
       }
       sow.wrapperFactory.wrap(_) >> { return mockedMessage}
     */
    def "Verify that onError logs error and converts StatusExceptions before forwarding them."(){
        when:
        sow.onError(new io.grpc.StatusException(CANCELLED))
        then:
        1 * streamObserverMock.onError(_ as  ClientSideException)
        1 * sow.log.log(Level.FINE, "Error processing streamed message of type WalletBalanceResponse: CANCELLED", _ as ClientSideException)
    }

    def "Verify that onError logs error and forwards non StatusExceptions without being converted."(){
        when:
        sow.onError(new ValidationException("Some message", new ValidationResult()))
        then:
        1 * streamObserverMock.onError(_ as  ValidationException)
        1 * sow.log.log(Level.FINE, "Error processing streamed message of type WalletBalanceResponse: Some message", _ as ValidationException)

    }

    def "Verify that onCompleted calls wrappedObserver and performs logging"(){
        when:
        sow.onCompleted()
        then:
        1 * streamObserverMock.onCompleted()
        1 * sow.log.fine("Stream of WalletBalanceResponse messages is complete.")

    }




}
