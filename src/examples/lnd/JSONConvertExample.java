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
package lnd;


import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.message.OpenChannelRequest;
import org.lightningj.lnd.wrapper.message.OpenStatusUpdate;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.Iterator;

import static lnd.ExampleUtils.getSynchronousLndAPI;
/**
 * This example shows how to parse and generate JSON data from high level wrapper objects.
 *
 * Created by Philip Vendil.
 */
public class JSONConvertExample {

    public static void main(String[] args) throws Exception{

        // Get API
        SynchronousLndAPI synchronousLndAPI = getSynchronousLndAPI();

        // To convert JSON request data to a wrapped request object (High level)
        // Do the following
        String jsonData = "{\"target_peer_id\":1,\"node_pubkey\":\"\",\"node_pubkey_string\":\"02ad1fddad0c572ec3e886cbea31bbafa30b5f7e745da7e936ed9d1471116cdc02\",\"local_funding_amount\":40000,\"push_sat\":25000,\"targetConf\":0,\"satPerByte\":0,\"private\":false,\"min_htlc_msat\":0}";

        // The library uses the javax.json-api 1.0 (JSR 374) API to parse and generate JSON.
        // To parse a JSON String, start by creating a JsonReader
        JsonReader jsonReader = Json.createReader(new StringReader(jsonData));

        // Then parse by creating a Wrapped Message object.
        OpenChannelRequest openChannelRequest = new OpenChannelRequest(jsonReader);

        // Perform the call.
        Iterator<OpenStatusUpdate> result = synchronousLndAPI.openChannel(openChannelRequest);

        // This call will wait for a the channel has opened, which means confirmation block must
        // generated in btc. If simnet is used you can manually generate blocks with
        // 'btcctl --simnet --rpcuser=kek --rpcpass=kek generate 3'

        while(result.hasNext()){
            // To generate JSON from a response there are three possiblities, either
            OpenStatusUpdate next = result.next();
            // To get JSON as String
            System.out.println("Received Update: " + next.toJsonAsString(false));
            // To have the result more human readable set pretty print to true
            System.out.println("Received Update: " + next.toJsonAsString(true));
            // It is also possible to get the JSON as a populated JsonObjectBuilder
            JsonObjectBuilder jsonObjectBuilder = next.toJson();
        }

    }
}
