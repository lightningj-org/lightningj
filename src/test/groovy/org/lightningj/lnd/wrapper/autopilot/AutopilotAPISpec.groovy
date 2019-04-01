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
package org.lightningj.lnd.wrapper.autopilot

import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import org.lightningj.lnd.proto.LightningApi
import org.lightningj.lnd.wrapper.*
import org.lightningj.lnd.wrapper.message.WalletBalanceRequest
import spock.lang.Specification

import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Unit tests for Autopilot API classes.
 * <p>
 * This class just verifies that the API classes have been generated.
 * Functional tests is in the integration tests.
 * </p>
 *
 * Created by Philip Vendil.
 */
class AutopilotAPISpec extends Specification {

    // Initialization is tested in SynchronousAPISpec

    AsynchronousAutopilotAPI asyncApi = new AsynchronousAutopilotAPI(Mock(ManagedChannel))
    SynchronousAutopilotAPI syncApi = new SynchronousAutopilotAPI(Mock(ManagedChannel))

    def "Verify that apis have been created."(){
        expect:
        asyncApi != null
        syncApi != null
    }
}
