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
package lnd;

import org.lightningj.lnd.wrapper.AsynchronousLndAPI;
import org.lightningj.lnd.wrapper.ClientSideException;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.message.OpenChannelRequest;

import javax.net.ssl.SSLException;
import java.io.File;

/**
 * Class that contains utility methods to simplify the example code.
 *
 * Created by Philip Vendil
 */
public class ExampleUtils {

    static SynchronousLndAPI getSynchronousLndAPI() throws SSLException, ClientSideException {
        return new SynchronousLndAPI("localhost",10001,new File(System.getProperty("user.home") + "/Library/Application Support/Lnd/tls.cert"),null);
    }

    static AsynchronousLndAPI getAsynchronousLndAPI() throws SSLException, ClientSideException {
        return new AsynchronousLndAPI("localhost",10001,new File(System.getProperty("user.home") + "/Library/Application Support/Lnd/tls.cert"),null);
    }

    static OpenChannelRequest genOpenChannelRequest() {
        OpenChannelRequest openChannelRequest = new OpenChannelRequest();
        openChannelRequest.setNodePubkeyString("02ad1fddad0c572ec3e886cbea31bbafa30b5f7e745da7e936ed9d1471116cdc02");
        openChannelRequest.setLocalFundingAmount(40000);
        openChannelRequest.setPushSat(25000);
        openChannelRequest.setSatPerByte(0);
        return openChannelRequest;
    }
}
