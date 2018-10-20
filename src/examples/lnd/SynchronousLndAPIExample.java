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

import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.message.ListPeersResponse;
import org.lightningj.lnd.wrapper.message.OpenChannelRequest;
import org.lightningj.lnd.wrapper.message.OpenStatusUpdate;

import java.io.File;
import java.util.Iterator;

/**
 * Example of using the Synchronous API.
 *
 * Created by Philip Vendil.
 */
public class SynchronousLndAPIExample {

    public static void main(String[] args) throws Exception{

        // To create a synchronousAPI there are three constructors available
        // One simple with host,port and certificate to trust, last file is the file path to the macaroon, use null if no macaroons are used.
        SynchronousLndAPI synchronousLndAPI = new SynchronousLndAPI("localhost",10001,
                new File(System.getProperty("user.home") + "/Library/Application Support/Lnd/tls.cert"),
                new File(System.getProperty("user.home")+ "/Library/Application Support/Lnd/admin.macaroon"));
        // A second with host,port and a custom SSL Context for more advanced SSL Context and Macaroon Context settings.
        //SynchronousLndAPI synchronousLndAPI = new SynchronousLndAPI("localhost",10001,sSLContext, macaroonContext);
        // The third that takes a ManagedChannel, with full customization capabilities of underlying API
        // See GRPC Java documentation for details.
        //SynchronousLndAPI synchronousLndAPI = new SynchronousLndAPI(managedChannel);

        // By default is validation performed on all inbound and outbound messages, to turn of validation:
        //synchronousLndAPI.setPerformValidation(false);

        // Example call to get channel balance and write output as JSON (pretty printed)
        System.out.println(synchronousLndAPI.channelBalance().toJsonAsString(true));

        // Calls returns a wrapped response or Iterator of wrapped responses.
        // Example to get a response:
        ListPeersResponse listPeersResponse = synchronousLndAPI.listPeers();
        // The response can be converted to XML or JSON or just parsed.


        // A more advanced call returning an iterator is for example openChannel().

        // To generate a request call, there are two ways to generate a request.
        // Either build up a request object like below:
        OpenChannelRequest openChannelRequest = new OpenChannelRequest();
        openChannelRequest.setNodePubkeyString("02ad1fddad0c572ec3e886cbea31bbafa30b5f7e745da7e936ed9d1471116cdc02");
        openChannelRequest.setLocalFundingAmount(40000);
        openChannelRequest.setPushSat(25000);
        openChannelRequest.setSatPerByte(0);

        // Alternatively it is possible to specify the parameters directly without having to create a request.
        // Iterator<OpenStatusUpdate> result = synchronousLndAPI.openChannel(1,null,"02ad1fddad0c572ec3e886cbea31bbafa30b5f7e745da7e936ed9d1471116cdc02", 40000L,25000L,null,0L,null,null);

        // Perform the call using alternative 1
        Iterator<OpenStatusUpdate> result = synchronousLndAPI.openChannel(openChannelRequest);

        // This call will wait for a the channel has opened, which means confirmation block must
        // generated in btc. If simnet is used you can manually generate blocks with
        // 'btcctl --simnet --rpcuser=kek --rpcpass=kek generate 3'

        while(result.hasNext()){
            System.out.println("Received Update: " + result.next().toJsonAsString(true));
        }

        // To close the api use the method
        synchronousLndAPI.close();
    }
}
