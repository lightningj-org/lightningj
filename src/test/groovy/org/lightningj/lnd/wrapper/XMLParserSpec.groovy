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
import org.lightningj.lnd.wrapper.invoices.message.AddHoldInvoiceRequest
import org.lightningj.lnd.wrapper.message.AddressType
import org.lightningj.lnd.wrapper.message.HopHint
import org.lightningj.lnd.wrapper.message.Invoice
import org.lightningj.lnd.wrapper.message.ListInvoiceResponse
import org.lightningj.lnd.wrapper.message.NewAddressRequest
import org.lightningj.lnd.wrapper.message.RouteHint
import org.lightningj.lnd.wrapper.message.SendManyRequest
import org.lightningj.lnd.wrapper.router.message.RouteFeeRequest
import org.lightningj.lnd.wrapper.signer.message.SignReq
import org.lightningj.lnd.wrapper.signer.message.TxOut
import org.lightningj.lnd.wrapper.walletkit.message.SendOutputsRequest
import org.lightningj.lnd.wrapper.watchtower.message.GetInfoRequest
import org.lightningj.lnd.wrapper.wtclient.message.ListTowersRequest
import spock.lang.Specification

import javax.xml.bind.UnmarshalException
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
        invoice.receipt = "SomeReceipt".getBytes("UTF-8")
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

    def invoiceXML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?><ListInvoiceResponse xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0"><invoices><Invoice><memo>memo1</memo><receipt>U29tZVJlY2VpcHQ=</receipt><RPreimage>U29tZVJQcmVpbWFnZQ==</RPreimage><RHash>U29tZVJIYXNo</RHash><value>12345</value><settled>false</settled><creationDate>87637234234</creationDate><settleDate>0</settleDate><paymentRequest></paymentRequest><descriptionHash></descriptionHash><expiry>0</expiry><fallbackAddr></fallbackAddr><cltvExpiry>0</cltvExpiry><route_hints/><private>false</private><addIndex>0</addIndex><settleIndex>0</settleIndex><amtPaid>0</amtPaid><amtPaidSat>0</amtPaidSat><amtPaidMsat>0</amtPaidMsat><state>OPEN</state><htlcs/></Invoice><Invoice><memo>memo2</memo><receipt>U29tZVJlY2VpcHQ=</receipt><RPreimage>U29tZVJQcmVpbWFnZQ==</RPreimage><RHash>U29tZVJIYXNo</RHash><value>12345</value><settled>false</settled><creationDate>87637234234</creationDate><settleDate>0</settleDate><paymentRequest></paymentRequest><descriptionHash></descriptionHash><expiry>0</expiry><fallbackAddr></fallbackAddr><cltvExpiry>0</cltvExpiry><route_hints/><private>false</private><addIndex>0</addIndex><settleIndex>0</settleIndex><amtPaid>0</amtPaid><amtPaidSat>0</amtPaidSat><amtPaidMsat>0</amtPaidMsat><state>OPEN</state><htlcs/></Invoice><Invoice><memo>memo3</memo><receipt>U29tZVJlY2VpcHQ=</receipt><RPreimage>U29tZVJQcmVpbWFnZQ==</RPreimage><RHash>U29tZVJIYXNo</RHash><value>12345</value><settled>false</settled><creationDate>87637234234</creationDate><settleDate>0</settleDate><paymentRequest></paymentRequest><descriptionHash></descriptionHash><expiry>0</expiry><fallbackAddr></fallbackAddr><cltvExpiry>0</cltvExpiry><route_hints/><private>false</private><addIndex>0</addIndex><settleIndex>0</settleIndex><amtPaid>0</amtPaid><amtPaidSat>0</amtPaidSat><amtPaidMsat>0</amtPaidMsat><state>OPEN</state><htlcs/></Invoice></invoices><lastIndexOffset>0</lastIndexOffset><firstIndexOffset>0</firstIndexOffset></ListInvoiceResponse>"""

    def prettyPrintedInvoiceXML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ListInvoiceResponse xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
    <invoices>
        <Invoice>
            <memo>memo1</memo>
            <receipt>U29tZVJlY2VpcHQ=</receipt>
            <RPreimage>U29tZVJQcmVpbWFnZQ==</RPreimage>
            <RHash>U29tZVJIYXNo</RHash>
            <value>12345</value>
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
        </Invoice>
        <Invoice>
            <memo>memo2</memo>
            <receipt>U29tZVJlY2VpcHQ=</receipt>
            <RPreimage>U29tZVJQcmVpbWFnZQ==</RPreimage>
            <RHash>U29tZVJIYXNo</RHash>
            <value>12345</value>
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
        </Invoice>
        <Invoice>
            <memo>memo3</memo>
            <receipt>U29tZVJlY2VpcHQ=</receipt>
            <RPreimage>U29tZVJQcmVpbWFnZQ==</RPreimage>
            <RHash>U29tZVJIYXNo</RHash>
            <value>12345</value>
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
        </Invoice>
    </invoices>
    <lastIndexOffset>0</lastIndexOffset>
    <firstIndexOffset>0</firstIndexOffset>
</ListInvoiceResponse>
"""

    def sendManyRequestXML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<SendManyRequest xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
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
    <satPerByte>0</satPerByte>
</SendManyRequest>
"""
    def newAddressRequestXML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<NewAddressRequest xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
    <type>NESTED_PUBKEY_HASH</type>
</NewAddressRequest>
"""

    def invalidNewAddressRequestXML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<NewAddressRequest xmlns="http://lightningj.org/xsd/lndjapi_1_0">
    <type>INVALID_PUBKEY_HASH</type>
</NewAddressRequest>
"""

    def badxml = "<sd>"

    def autoEnrollStatusResponse = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<autopilot:StatusResponse xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
    <autopilot:active>true</autopilot:active>
</autopilot:StatusResponse>
"""

    def chainNotifierBlockEpoch = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<chainnotifier:BlockEpoch xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
    <chainnotifier:hash>YWJj</chainnotifier:hash>
    <chainnotifier:height>123</chainnotifier:height>
</chainnotifier:BlockEpoch>
"""

    def invoicesAddHoldInvoiceRequest = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<invoices:AddHoldInvoiceRequest xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
    <invoices:memo></invoices:memo>
    <invoices:hash></invoices:hash>
    <invoices:value>123</invoices:value>
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
<router:RouteFeeRequest xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
    <router:dest>YWJj</router:dest>
    <router:amtSat>1000</router:amtSat>
</router:RouteFeeRequest>
"""

    def signerSignReq = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<signer:SignReq xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
    <signer:rawTxBytes>YWJj</signer:rawTxBytes>
    <signer:sign_descs/>
</signer:SignReq>
"""

    def walletkitSendOutputsRequest = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<walletkit:SendOutputsRequest xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
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
</walletkit:SendOutputsRequest>
"""

    def watchtowerRequest = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<watchtower:GetInfoRequest xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0"/>
"""

    def wtClientRequest = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<wtclient:ListTowersRequest xmlns:chainnotifier="http://lightningj.org/xsd/chainnotifier_1_0" xmlns:invoices="http://lightningj.org/xsd/invoices_1_0" xmlns:autopilot="http://lightningj.org/xsd/autopilot_1_0" xmlns:wtclient="http://lightningj.org/xsd/wtclient_1_0" xmlns:router="http://lightningj.org/xsd/router_1_0" xmlns:watchtower="http://lightningj.org/xsd/watchtower_1_0" xmlns="http://lightningj.org/xsd/lndjapi_1_0" xmlns:signer="http://lightningj.org/xsd/signer_1_0" xmlns:walletkit="http://lightningj.org/xsd/walletkit_1_0">
    <wtclient:includeSessions>true</wtclient:includeSessions>
</wtclient:ListTowersRequest>
"""
}
