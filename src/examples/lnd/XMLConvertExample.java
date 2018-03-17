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
import org.lightningj.lnd.wrapper.XMLParser;
import org.lightningj.lnd.wrapper.XMLParserFactory;
import org.lightningj.lnd.wrapper.message.OpenChannelRequest;
import org.lightningj.lnd.wrapper.message.OpenStatusUpdate;

import java.util.Iterator;

import static lnd.ExampleUtils.getSynchronousLndAPI;

/**
 * Example on how to convert a wrapped object to XML.
 *
 * Created by Philip Vendil
 */
public class XMLConvertExample {

    public static void main(String[] args) throws Exception{

        // Get API
        SynchronousLndAPI synchronousLndAPI = getSynchronousLndAPI();

        // Create a XMLParserFactory
        XMLParserFactory xmlParserFactory = new XMLParserFactory();

        // Retrieve XML Parser for a given XML version schema. (Currently "1.0")
        XMLParser xmlParser = xmlParserFactory.getXMLParser("1.0");

        byte[] xmlRequestData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><OpenChannelRequest xmlns=\"http://lightningj.org/xsd/lndjapi_1_0\"><nodePubkey></nodePubkey><nodePubkeyString>02ad1fddad0c572ec3e886cbea31bbafa30b5f7e745da7e936ed9d1471116cdc02</nodePubkeyString><localFundingAmount>40000</localFundingAmount><pushSat>25000</pushSat><targetConf>0</targetConf><satPerByte>0</satPerByte><private>false</private><minHtlcMsat>0</minHtlcMsat></OpenChannelRequest>".getBytes("UTF-8");

        // Convert to a wrapped high level message object.
        OpenChannelRequest openChannelRequest = (OpenChannelRequest) xmlParser.unmarshall(xmlRequestData);

        // Perform the call.
        Iterator<OpenStatusUpdate> result = synchronousLndAPI.openChannel(openChannelRequest);

        // This call will wait for a the channel has opened, which means confirmation block must
        // generated in btc. If simnet is used you can manually generate blocks with
        // 'btcctl --simnet --rpcuser=kek --rpcpass=kek generate 3'

        while(result.hasNext()){
            // To generate XML from a response do the following:
            OpenStatusUpdate next = result.next();
            // To get XML as byte[]
            byte[] responseData = xmlParser.marshall(next);
            System.out.println("XML Response data: " + new String(responseData,"UTF-8"));
            // To get XML pretty printed
            byte[] responseDataPrettyPrinted = xmlParser.marshall(next,true);
            System.out.println("Pretty Printed XML Response data: " + new String(responseDataPrettyPrinted,"UTF-8"));
        }
    }
}
