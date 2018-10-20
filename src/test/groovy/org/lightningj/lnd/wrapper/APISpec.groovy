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
import io.grpc.Status
import io.grpc.StatusRuntimeException
import org.lightningj.lnd.proto.LightningApi
import org.lightningj.lnd.wrapper.message.WalletBalanceRequest
import spock.lang.Specification

import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Unit tests for Base API class.
 *
 * Created by Philip Vendil.
 */
class APISpec extends Specification {

    // Initialization is tested in SynchronousAPISpec

    SynchronousLndAPI api = new SynchronousLndAPI(Mock(ManagedChannel))

    def setup(){
        api.log = Mock(Logger)
    }

    def "Verify that validate() checks a message if performValidation is set true"(){
        setup:
        Message m = Mock(Message)
        when:
        api.validate(m)
        then:
        1 * m.validate() >> { getValidValidationResult()}
    }

    def "Verify that validate() throws ValidationProblems if performValidation is set true and validation problems were found."(){
        setup:
        Message m = Mock(Message)
        when:
        api.validate(m)
        then:
        def e = thrown ValidationException
        e.message == "Validation problems in message SomeMessage"
        1 * m.validate() >> { getInvalidValidationResult()}
    }

    def "Verify that validate() doesnt validate if performValidation is set to false"(){
        setup:
        api.performValidation = false
        Message m = Mock(Message)
        when:
        api.validate(m)
        then:
        0 * m.validate()
    }

    def "Verify that close() closes underlying channel properly and  performs expected logging"(){
        setup:
        ManagedChannel managedChannel = Mock(ManagedChannel)
        when:
        api.close()
        then:
        1 * api.channel.shutdown() >> managedChannel
        1 * managedChannel.awaitTermination(5,TimeUnit.SECONDS)
        1 * api.log.fine("Closing SynchronousLndAPI Channel...")
        1 * api.log.fine("SynchronousLndAPI Channel Closed.")
    }

    def "Verify that if problems occurred during close() is correct exception thrown and error logging is done."(){
        when:
        api.close()
        then:
        def e = thrown ClientSideException
        e.message == "CANCELLED"
        1 * api.channel.shutdown() >> { throw new StatusRuntimeException(Status.CANCELLED)}
        1 * api.log.fine("Closing SynchronousLndAPI Channel...")
        1 * api.log.severe("Error occurred closing SynchronousLndAPI Channel: CANCELLED.")
        1 * api.log.log(Level.FINE,"Stacktrace: ",_ as StatusRuntimeException)
    }

    def "Verify that processRequest logs, validates and returns apiObject"(){
        setup:
        Message m = Mock(Message)

        when:
        api.processRequest(m)
        then: // Check that validates gets called
        1 * m.validate() >> { getValidValidationResult()}

        when: // verify cat log and return value is correct
        WalletBalanceRequest walletBalanceRequest = new WalletBalanceRequest()
        LightningApi.WalletBalanceRequest result = api.processRequest(walletBalanceRequest)
        then:
        result != null
        1 * api.log.fine('Sending request message: WalletBalanceRequest: \n{\n}')
    }

    def "Verify getter and setter for perform validation"(){
        when:
        api.setPerformValidation(false)
        then:
        !api.isPerformValidation()
    }


    private static ValidationResult getValidValidationResult(){
        return new ValidationResult("SomeMessage")
    }
    private static ValidationResult getInvalidValidationResult(){
        ValidationResult vr = new ValidationResult("SomeMessage")
        vr.messageErrors.add(new ValidationProblems("SomeMessage","somefield","SomeDescriptionResourceKey","SomeDesc"))
        return vr
    }
}
