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

import org.lightningj.lnd.wrapper.autopilot.message.StatusResponse
import org.lightningj.lnd.wrapper.chainnotifier.message.BlockEpoch
import org.lightningj.lnd.wrapper.chainkit.message.GetBlockRequest
import org.lightningj.lnd.wrapper.invoices.message.AddHoldInvoiceRequest
import org.lightningj.lnd.wrapper.message.AddressType
import org.lightningj.lnd.wrapper.message.HopHint
import org.lightningj.lnd.wrapper.message.Invoice
import org.lightningj.lnd.wrapper.message.ListInvoiceResponse
import org.lightningj.lnd.wrapper.message.NewAddressRequest
import org.lightningj.lnd.wrapper.message.RouteHint
import org.lightningj.lnd.wrapper.message.SendManyRequest
import org.lightningj.lnd.wrapper.neutrino.message.AddPeerRequest
import org.lightningj.lnd.wrapper.peers.message.UpdateAction
import org.lightningj.lnd.wrapper.peers.message.UpdateAddressAction
import org.lightningj.lnd.wrapper.router.message.RouteFeeRequest
import org.lightningj.lnd.wrapper.signer.message.SignReq
import org.lightningj.lnd.wrapper.signer.message.TxOut
import org.lightningj.lnd.wrapper.verrpc.message.VersionRequest
import org.lightningj.lnd.wrapper.walletkit.message.SendOutputsRequest
import org.lightningj.lnd.wrapper.walletunlocker.message.GenSeedRequest
import org.lightningj.lnd.wrapper.watchtower.message.GetInfoRequest
import org.lightningj.lnd.wrapper.wtclient.message.ListTowersRequest
import org.lightningj.lnd.wrapper.stateservice.message.GetStateRequest
import org.lightningj.lnd.wrapper.dev.message.ImportGraphResponse
import spock.lang.Specification

import jakarta.xml.bind.UnmarshalException
import javax.xml.validation.Schema

/**
 * Unit tests for the base XMLParser.
 *
 * Created by Philip Vendil.
 */
class XMLParserSpec extends Specification {

    XMLParser parser = new V1XMLParser()


    def "Verify that marshall returns a valid XML variant containing List"(){
        when:
        byte[] result = parser.marshall(createListInvoiceResponse())
        String xmlData = new String(result,"UTF-8")
        then:
        xmlData == invoiceXML
        when: // Verify pretty printed version
        result = parser.marshall(createListInvoiceResponse(), true)
        xmlData = new String(result,"UTF-8")
        then:
        xmlData == prettyPrintedInvoiceXML
    }

    def "Verify that unmarshall returns a valid Message Object for XML containing List"(){
        when:
        ListInvoiceResponse lir = parser.unmarshall(invoiceXML.getBytes("UTF-8"))
        then:
        lir.getInvoices().size() == 3
        lir.getApiObject().getInvoicesCount() == 3
    }


    def "Verify that marshall returns a valid XML variant containing Map"(){
        when:
        byte[] result = parser.marshall(createSendManyRequests(), true)
        String xmlData = new String(result,"UTF-8")
        then:
        xmlData == sendManyRequestXML
    }

    def "Verify that it is possible to marshall and unmarshall an autoenroll message"(){
        when:
        byte[] result = parser.marshall(createAutopilotStatusResponse(),true)
        String xmlData = new String(result,"UTF-8")
        then:
        xmlData == autoEnrollStatusResponse

        when:
        StatusResponse statusResponse = parser.unmarshall(result)
        then:
        statusResponse.active
    }

    def "Verify that it is possible to marshall and unmarshall a chainnotifier message"(){
        when:
        byte[] result = parser.marshall(createChainNotifierBlockEpoch(),true)
        String xmlData = new String(result,"UTF-8")
        then:
        xmlData == chainNotifierBlockEpoch

        when:
        BlockEpoch blockEpoch = parser.unmarshall(result)
        then:
        blockEpoch.hash == "abc".getBytes()
        blockEpoch.height == 123
    }

    def "Verify that it is possible to marshall and unmarshall a chainkit message"(){
        when:
        byte[] result = parser.marshall(createGetBlockRequest(),true)
        String xmlData = new String(result,"UTF-8")
        println xmlData
        then:

        xmlData == chainKitGetBlockRequest

        when:
        GetBlockRequest getBlockRequest = parser.unmarshall(result)
        then:
        getBlockRequest.blockHash == "1234".getBytes()

    }


    def "Verify that it is possible to marshall and unmarshall an invoices message"(){
        when:
        byte[] result = parser.marshall(createInvoicesAddHoldInvoiceRequest(),true)
        String xmlData = new String(result,"UTF-8")
        then:
        xmlData == invoicesAddHoldInvoiceRequest

        when:
        AddHoldInvoiceRequest request = parser.unmarshall(result)
        then:
        request.value == 123
        request.routeHints.size() == 1
        request.routeHints[0].hopHints[0].chanId == 222
        request.routeHints[0].hopHints[1].chanId == 333
    }

    def "Verify that it is possible to marshall and unmarshall a router message"(){
        when:
        byte[] result = parser.marshall(createRouteFeeRequest(),true)
        String xmlData = new String(result,"UTF-8")
        then:
        xmlData == routerRouteFeeRequest

        when:
        RouteFeeRequest request = parser.unmarshall(result)
        then:
        request.dest == "abc".getBytes()
        request.amtSat == 1000
    }

    def "Verify that it is possible to marshall and unmarshall a signer message"(){
        when:
        byte[] result = parser.marshall(createSignReq(),true)
        String xmlData = new String(result,"UTF-8")
        then:
        xmlData == signerSignReq

        when:
        SignReq request = parser.unmarshall(result)
        then:
        request.rawTxBytes == "abc".getBytes()
    }

    def "Verify that it is possible to marshall and unmarshall a walletkit message"(){
        when:
        byte[] result = parser.marshall(createWalletKitSendOutputsRequest(),true)
        String xmlData = new String(result,"UTF-8")
        println xmlData
        then:
        xmlData == walletkitSendOutputsRequest

        when:
        SendOutputsRequest request = parser.unmarshall(result)
        then:
        request.satPerKw == 10
        request.outputs.size() == 2
        request.outputs[0].value == 100
        request.outputs[1].value == 200
    }

    def "Verify that it is possible to marshall and unmarshall a watchtower message"(){
        when:
        byte[] result = parser.marshall(createWatchtowerRequest(),true)
        String xmlData = new String(result,"UTF-8")
        println xmlData
        then:
        xmlData == watchtowerRequest

        when:
        GetInfoRequest request = parser.unmarshall(result)
        then:
        request != null
    }

    def "Verify that it is possible to marshall and unmarshall a wtclient message"(){
        when:
        byte[] result = parser.marshall(createListTowersRequest(),true)
        String xmlData = new String(result,"UTF-8")
        println xmlData
        then:
        xmlData == wtClientRequest

        when:
        ListTowersRequest request = parser.unmarshall(result)
        then:
        request != null
    }

    def "Verify that it is possible to marshall and unmarshall a verrpc message"(){
        when:
        byte[] result = parser.marshall(createVersionRequest(),true)
        String xmlData = new String(result,"UTF-8")
        println xmlData
        then:
        xmlData == verRpcRequest

        when:
        VersionRequest request = parser.unmarshall(result)
        then:
        request != null
    }

    def "Verify that it is possible to marshall and unmarshall a walletunlocker message"(){
        when:
        byte[] result = parser.marshall(createGenSeedRequest(),true)
        String xmlData = new String(result,"UTF-8")
        println xmlData
        then:
        xmlData == genSeedRequest

        when:
        GenSeedRequest request = parser.unmarshall(result)
        then:
        request != null
        new String(request.aezeedPassphrase,"UTF-8") == "foo123"
        new String(request.seedEntropy,"UTF-8") == "abc"

    }

    def "Verify that it possible to marshall and unmarshall a stateservice message"(){
        when:
        byte[] result = parser.marshall(new GetStateRequest(),true)
        String xmlData = new String(result,"UTF-8")
        println xmlData
        then:
        xmlData == getStateRequest

        when:
        GetStateRequest request = parser.unmarshall(result)
        then:
        request != null
    }

    def "Verify that it possible to marshall and unmarshall a dev message"(){
        when:
        byte[] result = parser.marshall(new ImportGraphResponse(),true)
        String xmlData = new String(result,"UTF-8")
        println xmlData
        then:
        xmlData == importGraphResponse

        when:
        ImportGraphResponse request = parser.unmarshall(result)
        then:
        request != null
    }

    def "Verify that it possible to marshall and unmarshall a peers message"(){
        when:
        def uaa = new UpdateAddressAction()
        uaa.setAction(UpdateAction.ADD)
        uaa.setAddress("someAddress")
        byte[] result = parser.marshall(uaa,true)
        String xmlData = new String(result,"UTF-8")
        println xmlData
        then:
        xmlData == updateAddressAction

        when:
        UpdateAddressAction request = parser.unmarshall(result)
        then:
        request.getAction() == UpdateAction.ADD
    }

    def "Verify that it possible to marshall and unmarshall a neutrino message"(){
        when:
        def apr = new AddPeerRequest()
        apr.setPeerAddrs("someAddress")
        byte[] result = parser.marshall(apr,true)
        String xmlData = new String(result,"UTF-8")
        println xmlData
        then:
        xmlData == addPeerRequest

        when:
        AddPeerRequest request = parser.unmarshall(result)
        then:
        request.getPeerAddrs() == "someAddress"
    }

    def "Verify that unmarshall returns a valid Message Object for XML containing Map"(){
        when:
        SendManyRequest smr = parser.unmarshall(sendManyRequestXML.getBytes("UTF-8"))
        then:
        smr.getAddrToAmountAsDetachedMap().size() == 2
        smr.getApiObject().getAddrToAmountMap().size() == 2
    }

    def "Verify that marshall of enum is done correctly"(){
        when:
        byte[] result = parser.marshall(createNewAddressRequest(), true)
        String xmlData = new String(result,"UTF-8")
        then:
        xmlData == newAddressRequestXML
    }

    def "Verify unmarshall returns a valid Message Object for XML containing enum"(){
        when:
        NewAddressRequest nar = parser.unmarshall(newAddressRequestXML.getBytes("UTF-8"))
        then:
        nar.type == AddressType.NESTED_PUBKEY_HASH

    }

    def "Verify that schema is checked when unmarshalling"(){
        when:
        parser.unmarshall(invalidNewAddressRequestXML.getBytes("UTF-8"))
        then:
        def e = thrown UnmarshalException
        e.linkedException.message =~ "INVALID_PUBKEY_HASH"
    }

    def "Verify that invalid XML throws JAXBException"(){
        when:
        parser.unmarshall(badxml.getBytes("UTF-8"))
        then:
        thrown UnmarshalException
    }

    def "Verify that invalid mashalling data throws JAXBException"(){
        when:
        parser.marshall(null)
        then:
        thrown IllegalArgumentException
    }


    def "Verify that getSchema() returns a Schema"(){
        expect:
        parser.getSchema() instanceof Schema
    }




    private Message createListInvoiceResponse(){
        ListInvoiceResponse retval = new ListInvoiceResponse()

        List<Invoice> invoices = [createInvoice("memo1"),createInvoice("memo2"),createInvoice("memo3")]
        retval.setInvoices(invoices)

        return retval
    }


    private Invoice createInvoice(String memo){
        Invoice invoice = new Invoice()
        invoice.memo = memo
        invoice.RPreimage = "SomeRPreimage".getBytes("UTF-8")
        invoice.RHash = "SomeRHash".getBytes("UTF-8")
        invoice.value = 12345L
        invoice.setSettled(false)
        invoice.setCreationDate(87637234234L)

        return invoice
    }


    private Message createSendManyRequests(){
        SendManyRequest sendManyRequest = new SendManyRequest()

        sendManyRequest.setAddrToAmount(["SomeKey":1234L, "SomeKey2": 12345L])

        return sendManyRequest
    }

    private Message createNewAddressRequest(){
        NewAddressRequest newAddressRequest = new NewAddressRequest()
        newAddressRequest.setType(AddressType.NESTED_PUBKEY_HASH)
        return newAddressRequest
    }

    private Message createAutopilotStatusResponse(){
        StatusResponse statusResponse = new StatusResponse()
        statusResponse.setActive(true)
        return statusResponse
    }

    private Message createChainNotifierBlockEpoch(){
        BlockEpoch blockEpoch = new BlockEpoch()
        blockEpoch.setHash("abc".getBytes())
        blockEpoch.setHeight(123)
        return blockEpoch
    }

    private Message createGetBlockRequest(){
        GetBlockRequest getBlockRequest = new GetBlockRequest()
        getBlockRequest.setBlockHash("1234".bytes)
        return getBlockRequest
    }

    private Message createInvoicesAddHoldInvoiceRequest(){
        AddHoldInvoiceRequest retval = new AddHoldInvoiceRequest()
        retval.value = 123L
        HopHint hopHint1 = new HopHint()
        hopHint1.setChanId(222)
        HopHint hopHint2 = new HopHint()
        hopHint2.setChanId(333)
        RouteHint routeHint1 = new RouteHint()
        routeHint1.setHopHints([hopHint1, hopHint2])
        retval.setRouteHints([routeHint1])
        return retval
    }

    private Message createRouteFeeRequest(){
        RouteFeeRequest retval = new RouteFeeRequest()
        retval.dest = "abc".getBytes()
        retval.amtSat = 1000
        return retval
    }

    private Message createSignReq(){
        SignReq retval = new SignReq()
        retval.rawTxBytes = "abc".getBytes()
        return retval
    }

    private Message createWalletKitSendOutputsRequest(){
        SendOutputsRequest retval = new SendOutputsRequest()
        retval.satPerKw = 10
        TxOut txOut1 = new TxOut()
        txOut1.value = 100
        TxOut txOut2 = new TxOut()
        txOut2.value = 200
        retval.setOutputs([txOut1,txOut2])
        return retval
    }

    private Message createWatchtowerRequest(){
        GetInfoRequest infoRequest = new GetInfoRequest()
        return infoRequest
    }

    private Message createListTowersRequest(){
        ListTowersRequest retval = new ListTowersRequest()
        retval.setIncludeSessions(true)
        return retval
    }

    private Message createVersionRequest(){
        VersionRequest retval = new VersionRequest()
        return retval
    }

    private Message createGenSeedRequest(){
        GenSeedRequest retval = new GenSeedRequest()
        retval.setAezeedPassphrase("foo123".bytes)
        retval.setSeedEntropy("abc".bytes)
        return retval
    }

    def invoiceXML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?><ListInvoiceResponse xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:chainkit="http://lightningj.org/xsd/chainkit_1_0" xmlns:dev="http://lightningj.org/xsd/dev_1_0" xmlns:neutrino="http://lightningj.org/xsd/neutrino_1_0" xmlns:stateservice="http://lightningj.org/xsd/stateservice_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:verrpc="http://lightningj.org/xsd/verrpc_1_0" xmlns:walletunlocker="http://lightningj.org/xsd/walletunlocker_1_0" xmlns:peers="http://lightningj.org/xsd/peers_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0"><invoices><Invoice><memo>memo1</memo><RPreimage>U29tZVJQcmVpbWFnZQ==</RPreimage><RHash>U29tZVJIYXNo</RHash><value>12345</value><valueMsat>0</valueMsat><settled>false</settled><creationDate>87637234234</creationDate><settleDate>0</settleDate><paymentRequest></paymentRequest><descriptionHash></descriptionHash><expiry>0</expiry><fallbackAddr></fallbackAddr><cltvExpiry>0</cltvExpiry><route_hints/><private>false</private><addIndex>0</addIndex><settleIndex>0</settleIndex><amtPaid>0</amtPaid><amtPaidSat>0</amtPaidSat><amtPaidMsat>0</amtPaidMsat><state>OPEN</state><htlcs/><featuresEntries/><isKeysend>false</isKeysend><paymentAddr></paymentAddr><isAmp>false</isAmp><ampInvoiceStateEntries/></Invoice><Invoice><memo>memo2</memo><RPreimage>U29tZVJQcmVpbWFnZQ==</RPreimage><RHash>U29tZVJIYXNo</RHash><value>12345</value><valueMsat>0</valueMsat><settled>false</settled><creationDate>87637234234</creationDate><settleDate>0</settleDate><paymentRequest></paymentRequest><descriptionHash></descriptionHash><expiry>0</expiry><fallbackAddr></fallbackAddr><cltvExpiry>0</cltvExpiry><route_hints/><private>false</private><addIndex>0</addIndex><settleIndex>0</settleIndex><amtPaid>0</amtPaid><amtPaidSat>0</amtPaidSat><amtPaidMsat>0</amtPaidMsat><state>OPEN</state><htlcs/><featuresEntries/><isKeysend>false</isKeysend><paymentAddr></paymentAddr><isAmp>false</isAmp><ampInvoiceStateEntries/></Invoice><Invoice><memo>memo3</memo><RPreimage>U29tZVJQcmVpbWFnZQ==</RPreimage><RHash>U29tZVJIYXNo</RHash><value>12345</value><valueMsat>0</valueMsat><settled>false</settled><creationDate>87637234234</creationDate><settleDate>0</settleDate><paymentRequest></paymentRequest><descriptionHash></descriptionHash><expiry>0</expiry><fallbackAddr></fallbackAddr><cltvExpiry>0</cltvExpiry><route_hints/><private>false</private><addIndex>0</addIndex><settleIndex>0</settleIndex><amtPaid>0</amtPaid><amtPaidSat>0</amtPaidSat><amtPaidMsat>0</amtPaidMsat><state>OPEN</state><htlcs/><featuresEntries/><isKeysend>false</isKeysend><paymentAddr></paymentAddr><isAmp>false</isAmp><ampInvoiceStateEntries/></Invoice></invoices><lastIndexOffset>0</lastIndexOffset><firstIndexOffset>0</firstIndexOffset></ListInvoiceResponse>"""

    def prettyPrintedInvoiceXML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ListInvoiceResponse xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:chainkit="http://lightningj.org/xsd/chainkit_1_0" xmlns:dev="http://lightningj.org/xsd/dev_1_0" xmlns:neutrino="http://lightningj.org/xsd/neutrino_1_0" xmlns:stateservice="http://lightningj.org/xsd/stateservice_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:verrpc="http://lightningj.org/xsd/verrpc_1_0" xmlns:walletunlocker="http://lightningj.org/xsd/walletunlocker_1_0" xmlns:peers="http://lightningj.org/xsd/peers_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
    <invoices>
        <Invoice>
            <memo>memo1</memo>
            <RPreimage>U29tZVJQcmVpbWFnZQ==</RPreimage>
            <RHash>U29tZVJIYXNo</RHash>
            <value>12345</value>
            <valueMsat>0</valueMsat>
            <settled>false</settled>
            <creationDate>87637234234</creationDate>
            <settleDate>0</settleDate>
            <paymentRequest></paymentRequest>
            <descriptionHash></descriptionHash>
            <expiry>0</expiry>
            <fallbackAddr></fallbackAddr>
            <cltvExpiry>0</cltvExpiry>
            <route_hints/>
            <private>false</private>
            <addIndex>0</addIndex>
            <settleIndex>0</settleIndex>
            <amtPaid>0</amtPaid>
            <amtPaidSat>0</amtPaidSat>
            <amtPaidMsat>0</amtPaidMsat>
            <state>OPEN</state>
            <htlcs/>
            <featuresEntries/>
            <isKeysend>false</isKeysend>
            <paymentAddr></paymentAddr>
            <isAmp>false</isAmp>
            <ampInvoiceStateEntries/>
        </Invoice>
        <Invoice>
            <memo>memo2</memo>
            <RPreimage>U29tZVJQcmVpbWFnZQ==</RPreimage>
            <RHash>U29tZVJIYXNo</RHash>
            <value>12345</value>
            <valueMsat>0</valueMsat>
            <settled>false</settled>
            <creationDate>87637234234</creationDate>
            <settleDate>0</settleDate>
            <paymentRequest></paymentRequest>
            <descriptionHash></descriptionHash>
            <expiry>0</expiry>
            <fallbackAddr></fallbackAddr>
            <cltvExpiry>0</cltvExpiry>
            <route_hints/>
            <private>false</private>
            <addIndex>0</addIndex>
            <settleIndex>0</settleIndex>
            <amtPaid>0</amtPaid>
            <amtPaidSat>0</amtPaidSat>
            <amtPaidMsat>0</amtPaidMsat>
            <state>OPEN</state>
            <htlcs/>
            <featuresEntries/>
            <isKeysend>false</isKeysend>
            <paymentAddr></paymentAddr>
            <isAmp>false</isAmp>
            <ampInvoiceStateEntries/>
        </Invoice>
        <Invoice>
            <memo>memo3</memo>
            <RPreimage>U29tZVJQcmVpbWFnZQ==</RPreimage>
            <RHash>U29tZVJIYXNo</RHash>
            <value>12345</value>
            <valueMsat>0</valueMsat>
            <settled>false</settled>
            <creationDate>87637234234</creationDate>
            <settleDate>0</settleDate>
            <paymentRequest></paymentRequest>
            <descriptionHash></descriptionHash>
            <expiry>0</expiry>
            <fallbackAddr></fallbackAddr>
            <cltvExpiry>0</cltvExpiry>
            <route_hints/>
            <private>false</private>
            <addIndex>0</addIndex>
            <settleIndex>0</settleIndex>
            <amtPaid>0</amtPaid>
            <amtPaidSat>0</amtPaidSat>
            <amtPaidMsat>0</amtPaidMsat>
            <state>OPEN</state>
            <htlcs/>
            <featuresEntries/>
            <isKeysend>false</isKeysend>
            <paymentAddr></paymentAddr>
            <isAmp>false</isAmp>
            <ampInvoiceStateEntries/>
        </Invoice>
    </invoices>
    <lastIndexOffset>0</lastIndexOffset>
    <firstIndexOffset>0</firstIndexOffset>
</ListInvoiceResponse>
"""

    def sendManyRequestXML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<SendManyRequest xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:chainkit="http://lightningj.org/xsd/chainkit_1_0" xmlns:dev="http://lightningj.org/xsd/dev_1_0" xmlns:neutrino="http://lightningj.org/xsd/neutrino_1_0" xmlns:stateservice="http://lightningj.org/xsd/stateservice_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:verrpc="http://lightningj.org/xsd/verrpc_1_0" xmlns:walletunlocker="http://lightningj.org/xsd/walletunlocker_1_0" xmlns:peers="http://lightningj.org/xsd/peers_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
    <addrToAmountEntries>
        <entry>
            <key>SomeKey</key>
            <value>1234</value>
        </entry>
        <entry>
            <key>SomeKey2</key>
            <value>12345</value>
        </entry>
    </addrToAmountEntries>
    <targetConf>0</targetConf>
    <satPerVbyte>0</satPerVbyte>
    <satPerByte>0</satPerByte>
    <label></label>
    <minConfs>0</minConfs>
    <spendUnconfirmed>false</spendUnconfirmed>
</SendManyRequest>
"""
    def newAddressRequestXML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<NewAddressRequest xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:chainkit="http://lightningj.org/xsd/chainkit_1_0" xmlns:dev="http://lightningj.org/xsd/dev_1_0" xmlns:neutrino="http://lightningj.org/xsd/neutrino_1_0" xmlns:stateservice="http://lightningj.org/xsd/stateservice_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:verrpc="http://lightningj.org/xsd/verrpc_1_0" xmlns:walletunlocker="http://lightningj.org/xsd/walletunlocker_1_0" xmlns:peers="http://lightningj.org/xsd/peers_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
    <type>NESTED_PUBKEY_HASH</type>
    <account></account>
</NewAddressRequest>
"""

    def invalidNewAddressRequestXML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<NewAddressRequest xmlns="http://lightningj.org/xsd/lndjapi_1_0">
    <type>INVALID_PUBKEY_HASH</type>
</NewAddressRequest>
"""

    def badxml = "<sd>"

    def autoEnrollStatusResponse = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<autopilot:StatusResponse xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:chainkit="http://lightningj.org/xsd/chainkit_1_0" xmlns:dev="http://lightningj.org/xsd/dev_1_0" xmlns:neutrino="http://lightningj.org/xsd/neutrino_1_0" xmlns:stateservice="http://lightningj.org/xsd/stateservice_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:verrpc="http://lightningj.org/xsd/verrpc_1_0" xmlns:walletunlocker="http://lightningj.org/xsd/walletunlocker_1_0" xmlns:peers="http://lightningj.org/xsd/peers_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
    <autopilot:active>true</autopilot:active>
</autopilot:StatusResponse>
"""

    def chainNotifierBlockEpoch = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<chainnotifier:BlockEpoch xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:chainkit="http://lightningj.org/xsd/chainkit_1_0" xmlns:dev="http://lightningj.org/xsd/dev_1_0" xmlns:neutrino="http://lightningj.org/xsd/neutrino_1_0" xmlns:stateservice="http://lightningj.org/xsd/stateservice_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:verrpc="http://lightningj.org/xsd/verrpc_1_0" xmlns:walletunlocker="http://lightningj.org/xsd/walletunlocker_1_0" xmlns:peers="http://lightningj.org/xsd/peers_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
    <chainnotifier:hash>YWJj</chainnotifier:hash>
    <chainnotifier:height>123</chainnotifier:height>
</chainnotifier:BlockEpoch>
"""

    def chainKitGetBlockRequest = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<chainkit:GetBlockRequest xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:chainkit="http://lightningj.org/xsd/chainkit_1_0" xmlns:dev="http://lightningj.org/xsd/dev_1_0" xmlns:neutrino="http://lightningj.org/xsd/neutrino_1_0" xmlns:stateservice="http://lightningj.org/xsd/stateservice_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:verrpc="http://lightningj.org/xsd/verrpc_1_0" xmlns:walletunlocker="http://lightningj.org/xsd/walletunlocker_1_0" xmlns:peers="http://lightningj.org/xsd/peers_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
    <chainkit:blockHash>MTIzNA==</chainkit:blockHash>
</chainkit:GetBlockRequest>
"""

    def invoicesAddHoldInvoiceRequest = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<invoices:AddHoldInvoiceRequest xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:chainkit="http://lightningj.org/xsd/chainkit_1_0" xmlns:dev="http://lightningj.org/xsd/dev_1_0" xmlns:neutrino="http://lightningj.org/xsd/neutrino_1_0" xmlns:stateservice="http://lightningj.org/xsd/stateservice_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:verrpc="http://lightningj.org/xsd/verrpc_1_0" xmlns:walletunlocker="http://lightningj.org/xsd/walletunlocker_1_0" xmlns:peers="http://lightningj.org/xsd/peers_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
    <invoices:memo></invoices:memo>
    <invoices:hash></invoices:hash>
    <invoices:value>123</invoices:value>
    <invoices:valueMsat>0</invoices:valueMsat>
    <invoices:descriptionHash></invoices:descriptionHash>
    <invoices:expiry>0</invoices:expiry>
    <invoices:fallbackAddr></invoices:fallbackAddr>
    <invoices:cltvExpiry>0</invoices:cltvExpiry>
    <invoices:route_hints>
        <invoices:RouteHint>
            <hop_hints>
                <HopHint>
                    <nodeId></nodeId>
                    <chanId>222</chanId>
                    <feeBaseMsat>0</feeBaseMsat>
                    <feeProportionalMillionths>0</feeProportionalMillionths>
                    <cltvExpiryDelta>0</cltvExpiryDelta>
                </HopHint>
                <HopHint>
                    <nodeId></nodeId>
                    <chanId>333</chanId>
                    <feeBaseMsat>0</feeBaseMsat>
                    <feeProportionalMillionths>0</feeProportionalMillionths>
                    <cltvExpiryDelta>0</cltvExpiryDelta>
                </HopHint>
            </hop_hints>
        </invoices:RouteHint>
    </invoices:route_hints>
    <invoices:private>false</invoices:private>
</invoices:AddHoldInvoiceRequest>
"""

    def routerRouteFeeRequest = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<router:RouteFeeRequest xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:chainkit="http://lightningj.org/xsd/chainkit_1_0" xmlns:dev="http://lightningj.org/xsd/dev_1_0" xmlns:neutrino="http://lightningj.org/xsd/neutrino_1_0" xmlns:stateservice="http://lightningj.org/xsd/stateservice_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:verrpc="http://lightningj.org/xsd/verrpc_1_0" xmlns:walletunlocker="http://lightningj.org/xsd/walletunlocker_1_0" xmlns:peers="http://lightningj.org/xsd/peers_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
    <router:dest>YWJj</router:dest>
    <router:amtSat>1000</router:amtSat>
</router:RouteFeeRequest>
"""

    def signerSignReq = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<signer:SignReq xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:chainkit="http://lightningj.org/xsd/chainkit_1_0" xmlns:dev="http://lightningj.org/xsd/dev_1_0" xmlns:neutrino="http://lightningj.org/xsd/neutrino_1_0" xmlns:stateservice="http://lightningj.org/xsd/stateservice_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:verrpc="http://lightningj.org/xsd/verrpc_1_0" xmlns:walletunlocker="http://lightningj.org/xsd/walletunlocker_1_0" xmlns:peers="http://lightningj.org/xsd/peers_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
    <signer:rawTxBytes>YWJj</signer:rawTxBytes>
    <signer:sign_descs/>
    <signer:prev_outputs/>
</signer:SignReq>
"""

    def walletkitSendOutputsRequest = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<walletkit:SendOutputsRequest xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:chainkit="http://lightningj.org/xsd/chainkit_1_0" xmlns:dev="http://lightningj.org/xsd/dev_1_0" xmlns:neutrino="http://lightningj.org/xsd/neutrino_1_0" xmlns:stateservice="http://lightningj.org/xsd/stateservice_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:verrpc="http://lightningj.org/xsd/verrpc_1_0" xmlns:walletunlocker="http://lightningj.org/xsd/walletunlocker_1_0" xmlns:peers="http://lightningj.org/xsd/peers_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
    <walletkit:satPerKw>10</walletkit:satPerKw>
    <walletkit:outputs>
        <walletkit:TxOut>
            <signer:value>100</signer:value>
            <signer:pkScript></signer:pkScript>
        </walletkit:TxOut>
        <walletkit:TxOut>
            <signer:value>200</signer:value>
            <signer:pkScript></signer:pkScript>
        </walletkit:TxOut>
    </walletkit:outputs>
    <walletkit:label></walletkit:label>
    <walletkit:minConfs>0</walletkit:minConfs>
    <walletkit:spendUnconfirmed>false</walletkit:spendUnconfirmed>
</walletkit:SendOutputsRequest>
"""

    def watchtowerRequest = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<watchtower:GetInfoRequest xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:chainkit="http://lightningj.org/xsd/chainkit_1_0" xmlns:dev="http://lightningj.org/xsd/dev_1_0" xmlns:neutrino="http://lightningj.org/xsd/neutrino_1_0" xmlns:stateservice="http://lightningj.org/xsd/stateservice_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:verrpc="http://lightningj.org/xsd/verrpc_1_0" xmlns:walletunlocker="http://lightningj.org/xsd/walletunlocker_1_0" xmlns:peers="http://lightningj.org/xsd/peers_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0"/>
"""

    def wtClientRequest = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<wtclient:ListTowersRequest xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:chainkit="http://lightningj.org/xsd/chainkit_1_0" xmlns:dev="http://lightningj.org/xsd/dev_1_0" xmlns:neutrino="http://lightningj.org/xsd/neutrino_1_0" xmlns:stateservice="http://lightningj.org/xsd/stateservice_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:verrpc="http://lightningj.org/xsd/verrpc_1_0" xmlns:walletunlocker="http://lightningj.org/xsd/walletunlocker_1_0" xmlns:peers="http://lightningj.org/xsd/peers_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
    <wtclient:includeSessions>true</wtclient:includeSessions>
    <wtclient:excludeExhaustedSessions>false</wtclient:excludeExhaustedSessions>
</wtclient:ListTowersRequest>
"""

    def verRpcRequest = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<verrpc:VersionRequest xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:chainkit="http://lightningj.org/xsd/chainkit_1_0" xmlns:dev="http://lightningj.org/xsd/dev_1_0" xmlns:neutrino="http://lightningj.org/xsd/neutrino_1_0" xmlns:stateservice="http://lightningj.org/xsd/stateservice_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:verrpc="http://lightningj.org/xsd/verrpc_1_0" xmlns:walletunlocker="http://lightningj.org/xsd/walletunlocker_1_0" xmlns:peers="http://lightningj.org/xsd/peers_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0"/>
"""

    def genSeedRequest = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<walletunlocker:GenSeedRequest xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:chainkit="http://lightningj.org/xsd/chainkit_1_0" xmlns:dev="http://lightningj.org/xsd/dev_1_0" xmlns:neutrino="http://lightningj.org/xsd/neutrino_1_0" xmlns:stateservice="http://lightningj.org/xsd/stateservice_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:verrpc="http://lightningj.org/xsd/verrpc_1_0" xmlns:walletunlocker="http://lightningj.org/xsd/walletunlocker_1_0" xmlns:peers="http://lightningj.org/xsd/peers_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
    <walletunlocker:aezeedPassphrase>Zm9vMTIz</walletunlocker:aezeedPassphrase>
    <walletunlocker:seedEntropy>YWJj</walletunlocker:seedEntropy>
</walletunlocker:GenSeedRequest>
"""

    def getStateRequest = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<stateservice:GetStateRequest xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:chainkit="http://lightningj.org/xsd/chainkit_1_0" xmlns:dev="http://lightningj.org/xsd/dev_1_0" xmlns:neutrino="http://lightningj.org/xsd/neutrino_1_0" xmlns:stateservice="http://lightningj.org/xsd/stateservice_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:verrpc="http://lightningj.org/xsd/verrpc_1_0" xmlns:walletunlocker="http://lightningj.org/xsd/walletunlocker_1_0" xmlns:peers="http://lightningj.org/xsd/peers_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0"/>
"""

    def importGraphResponse = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<dev:ImportGraphResponse xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:chainkit="http://lightningj.org/xsd/chainkit_1_0" xmlns:dev="http://lightningj.org/xsd/dev_1_0" xmlns:neutrino="http://lightningj.org/xsd/neutrino_1_0" xmlns:stateservice="http://lightningj.org/xsd/stateservice_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:verrpc="http://lightningj.org/xsd/verrpc_1_0" xmlns:walletunlocker="http://lightningj.org/xsd/walletunlocker_1_0" xmlns:peers="http://lightningj.org/xsd/peers_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0"/>
"""

    def updateAddressAction = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<peers:UpdateAddressAction xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:chainkit="http://lightningj.org/xsd/chainkit_1_0" xmlns:dev="http://lightningj.org/xsd/dev_1_0" xmlns:neutrino="http://lightningj.org/xsd/neutrino_1_0" xmlns:stateservice="http://lightningj.org/xsd/stateservice_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:verrpc="http://lightningj.org/xsd/verrpc_1_0" xmlns:walletunlocker="http://lightningj.org/xsd/walletunlocker_1_0" xmlns:peers="http://lightningj.org/xsd/peers_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
    <peers:action>ADD</peers:action>
    <peers:address>someAddress</peers:address>
</peers:UpdateAddressAction>
"""

    def addPeerRequest = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<neutrino:AddPeerRequest xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:chainkit="http://lightningj.org/xsd/chainkit_1_0" xmlns:dev="http://lightningj.org/xsd/dev_1_0" xmlns:neutrino="http://lightningj.org/xsd/neutrino_1_0" xmlns:stateservice="http://lightningj.org/xsd/stateservice_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:verrpc="http://lightningj.org/xsd/verrpc_1_0" xmlns:walletunlocker="http://lightningj.org/xsd/walletunlocker_1_0" xmlns:peers="http://lightningj.org/xsd/peers_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
    <neutrino:peerAddrs>someAddress</neutrino:peerAddrs>
</neutrino:AddPeerRequest>
"""
}
