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
package org.lightningj.lnd.wrapper.wtclient

import io.grpc.ManagedChannel
import org.lightningj.lnd.wrapper.walletkit.AsynchronousWalletKitAPI
import org.lightningj.lnd.wrapper.walletkit.SynchronousWalletKitAPI
import spock.lang.Specification

/**
 * Unit tests for WTClient API classes.
 * <p>
 * This class just verifies that the API classes have been generated.
 * Functional tests is in the integration tests.
 * </p>
 *
 * Created by Philip Vendil.
 */
class WtclientSpec extends Specification {

    // Initialization is tested in SynchronousAPISpec

    def asyncApi = new AsynchronousWatchtowerClientAPI(Mock(ManagedChannel))
    def syncApi = new SynchronousWatchtowerClientAPI(Mock(ManagedChannel))

    def "Verify that apis have been created."(){
        expect:
        asyncApi != null
        syncApi != null
    }
}
