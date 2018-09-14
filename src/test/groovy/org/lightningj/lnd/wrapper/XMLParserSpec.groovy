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
package org.lightningj.lnd.wrapper

import org.lightningj.lnd.wrapper.message.Invoice
import org.lightningj.lnd.wrapper.message.ListInvoiceResponse
import org.lightningj.lnd.wrapper.message.NewAddressRequest
import org.lightningj.lnd.wrapper.message.SendManyRequest
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
        nar.type == NewAddressRequest.AddressType.NESTED_PUBKEY_HASH

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

    def "Verify that getSchemaData() returns correct schema data"(){
        when:
        String schemaData = new String(parser.getSchemaData(),"UTF-8")
        then:
        schemaData =~ "</xs:schema>"
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
        newAddressRequest.setType(NewAddressRequest.AddressType.NESTED_PUBKEY_HASH)
        return newAddressRequest
    }


    def invoiceXML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?><ListInvoiceResponse xmlns="http://lightningj.org/xsd/lndjapi_1_0"><invoices><Invoice><memo>memo1</memo><receipt>U29tZVJlY2VpcHQ=</receipt><RPreimage>U29tZVJQcmVpbWFnZQ==</RPreimage><RHash>U29tZVJIYXNo</RHash><value>12345</value><settled>false</settled><creationDate>87637234234</creationDate><settleDate>0</settleDate><paymentRequest></paymentRequest><descriptionHash></descriptionHash><expiry>0</expiry><fallbackAddr></fallbackAddr><cltvExpiry>0</cltvExpiry><route_hints/><private>false</private><addIndex>0</addIndex><settleIndex>0</settleIndex><amtPaid>0</amtPaid><amtPaidSat>0</amtPaidSat><amtPaidMsat>0</amtPaidMsat></Invoice><Invoice><memo>memo2</memo><receipt>U29tZVJlY2VpcHQ=</receipt><RPreimage>U29tZVJQcmVpbWFnZQ==</RPreimage><RHash>U29tZVJIYXNo</RHash><value>12345</value><settled>false</settled><creationDate>87637234234</creationDate><settleDate>0</settleDate><paymentRequest></paymentRequest><descriptionHash></descriptionHash><expiry>0</expiry><fallbackAddr></fallbackAddr><cltvExpiry>0</cltvExpiry><route_hints/><private>false</private><addIndex>0</addIndex><settleIndex>0</settleIndex><amtPaid>0</amtPaid><amtPaidSat>0</amtPaidSat><amtPaidMsat>0</amtPaidMsat></Invoice><Invoice><memo>memo3</memo><receipt>U29tZVJlY2VpcHQ=</receipt><RPreimage>U29tZVJQcmVpbWFnZQ==</RPreimage><RHash>U29tZVJIYXNo</RHash><value>12345</value><settled>false</settled><creationDate>87637234234</creationDate><settleDate>0</settleDate><paymentRequest></paymentRequest><descriptionHash></descriptionHash><expiry>0</expiry><fallbackAddr></fallbackAddr><cltvExpiry>0</cltvExpiry><route_hints/><private>false</private><addIndex>0</addIndex><settleIndex>0</settleIndex><amtPaid>0</amtPaid><amtPaidSat>0</amtPaidSat><amtPaidMsat>0</amtPaidMsat></Invoice></invoices><lastIndexOffset>0</lastIndexOffset><firstIndexOffset>0</firstIndexOffset></ListInvoiceResponse>"""

    def prettyPrintedInvoiceXML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ListInvoiceResponse xmlns="http://lightningj.org/xsd/lndjapi_1_0">
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
        </Invoice>
    </invoices>
    <lastIndexOffset>0</lastIndexOffset>
    <firstIndexOffset>0</firstIndexOffset>
</ListInvoiceResponse>
"""

    def sendManyRequestXML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<SendManyRequest xmlns="http://lightningj.org/xsd/lndjapi_1_0">
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
<NewAddressRequest xmlns="http://lightningj.org/xsd/lndjapi_1_0">
    <type>NESTED_PUBKEY_HASH</type>
</NewAddressRequest>
"""

    def invalidNewAddressRequestXML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<NewAddressRequest xmlns="http://lightningj.org/xsd/lndjapi_1_0">
    <type>INVALID_PUBKEY_HASH</type>
</NewAddressRequest>
"""

    def badxml = "<sd>"
}
