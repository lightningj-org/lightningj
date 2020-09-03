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
package org.lightningj.lnd.util

import com.google.protobuf.ByteString
import com.google.protobuf.Descriptors
import org.lightningj.lnd.proto.LightningApi
import org.lightningj.lnd.proto.LightningApi.Invoice
import org.lightningj.lnd.proto.LightningApi.Route
import org.lightningj.lnd.wrapper.message.InvoiceHTLC
import spock.lang.Specification
import spock.lang.Unroll

import javax.json.Json
import javax.json.JsonArray
import javax.json.JsonObject
import javax.json.JsonObjectBuilder
import com.google.protobuf.GeneratedMessageV3.Builder

import javax.json.JsonReader

import static com.google.protobuf.Descriptors.FieldDescriptor.JavaType.BOOLEAN
import static com.google.protobuf.Descriptors.FieldDescriptor.JavaType.BYTE_STRING
import static com.google.protobuf.Descriptors.FieldDescriptor.JavaType.DOUBLE
import static com.google.protobuf.Descriptors.FieldDescriptor.JavaType.INT
import static com.google.protobuf.Descriptors.FieldDescriptor.JavaType.LONG
import static com.google.protobuf.Descriptors.FieldDescriptor.JavaType.MESSAGE
import static com.google.protobuf.Descriptors.FieldDescriptor.JavaType.STRING
import static com.google.protobuf.Descriptors.FieldDescriptor.JavaType.ENUM
import static org.lightningj.lnd.proto.LightningApi.AddressType.WITNESS_PUBKEY_HASH

/**
 * Unit test for class JsonGenUtils
 *
 * Created by Philip Vendil.
 */
class JsonGenUtilsSpec extends Specification {

    static ByteString byteString = ByteString.copyFrom("SomeByteString".getBytes())
    static String base64EncodedByteString = Base64.getEncoder().encodeToString("SomeByteString".getBytes())

    def "Verify that jsonToString converts json to string properly"(){
        setup:
        JsonObjectBuilder jsonObjectBuilder1 = JsonGenUtils.messageToJson(createInvoice(),Invoice.descriptor)
        JsonObjectBuilder jsonObjectBuilder2 = JsonGenUtils.messageToJson(createInvoice(),Invoice.descriptor)
        expect:
        JsonGenUtils.jsonToString(jsonObjectBuilder1,false) == '{"memo":"SomeMemo","rPreimage":"","rHash":"","value":0,"valueMsat":0,"settled":false,"creationDate":5432123,"settleDate":0,"paymentRequest":"","descriptionHash":"VGVzdA==","expiry":5432343,"fallbackAddr":"","cltvExpiry":12345,"routeHints":[],"private":false,"addIndex":0,"settleIndex":0,"amtPaid":0,"amtPaidSat":0,"amtPaidMsat":0,"state":"OPEN","htlcs":[],"features":[],"isKeysend":false}'
        JsonGenUtils.jsonToString(jsonObjectBuilder2,true) == """
{
    "memo": "SomeMemo",
    "rPreimage": "",
    "rHash": "",
    "value": 0,
    "valueMsat": 0,
    "settled": false,
    "creationDate": 5432123,
    "settleDate": 0,
    "paymentRequest": "",
    "descriptionHash": "VGVzdA==",
    "expiry": 5432343,
    "fallbackAddr": "",
    "cltvExpiry": 12345,
    "routeHints": [
    ],
    "private": false,
    "addIndex": 0,
    "settleIndex": 0,
    "amtPaid": 0,
    "amtPaidSat": 0,
    "amtPaidMsat": 0,
    "state": "OPEN",
    "htlcs": [
    ],
    "features": [
    ],
    "isKeysend": false
}"""
    }

    @Unroll
    def "Verify that setSingleValue set correct json value for a field of type #javaType"(){
        setup:
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder()
        Builder builder = instance.toBuilder()
        Descriptors.FieldDescriptor fieldDescriptor = instance.descriptor.fields.find {it.name == field}
        if(javaType != ENUM) {
            builder.setField(fieldDescriptor, value)
        }else{
            builder.setType(value)
        }


        when:
        JsonGenUtils.setSingleValue(jsonObjectBuilder,builder,fieldDescriptor)


        then:
        jsonObjectBuilder.build().toString() == expected

        where:
        javaType          | instance                                     | field                | value                       | expected
        STRING            | LightningApi.Invoice.newInstance()           | "memo"               | "SomeString"                | '{"memo":"SomeString"}'
        INT               | LightningApi.Hop.newInstance()               | "expiry"             | 123                         | '{"expiry":123}'
        DOUBLE            | LightningApi.NetworkInfo.newInstance()       | "avg_out_degree"     | (double) 1928127.00001      | '{"avgOutDegree":1928127.00001}'
        LONG              | LightningApi.Payment.newInstance()           | "fee"                | 1292817282L                 | '{"fee":1292817282}'
        BOOLEAN           | LightningApi.Invoice.newInstance()           | "settled"            | true                        | '{"settled":true}'
        BYTE_STRING       | LightningApi.Invoice.newInstance()           | "r_preimage"         | byteString                  | '{"rPreimage":"' + base64EncodedByteString + '"}'
        ENUM              | LightningApi.NewAddressRequest.newInstance() | "type"               | WITNESS_PUBKEY_HASH                 | '{"type":"WITNESS_PUBKEY_HASH"}'
        MESSAGE           | LightningApi.SendResponse.newInstance()      | "payment_route"      | createRoute().build()       | '{"paymentRoute":{"totalTimeLock":0,"totalFees":543234,"totalAmt":123,"hops":[],"totalFeesMsat":0,"totalAmtMsat":0}}'
    }

    @Unroll
    def "Verify that readSingleValue retrieves correct json value for a field of type #javaType"(){
        setup:
        JsonReader jsonReader = Json.createReader(new StringReader(jsonData))
        Builder builder = instance.toBuilder()
        Descriptors.FieldDescriptor fieldDescriptor = instance.descriptor.fields.find {it.name == field}

        when:
        JsonGenUtils.readSingleValue(jsonReader.readObject(),builder,fieldDescriptor)

        then:
        if(javaType != ENUM) {
          assert builder.getField(fieldDescriptor) == expected
        }else{
          assert builder.getType() == expected
        }


        where:
        javaType          | instance                                     | field                | expected                    | jsonData
        STRING            | LightningApi.Invoice.newInstance()           | "memo"               | "SomeString"                | '{"memo":"SomeString"}'
        INT               | LightningApi.Hop.newInstance()               | "expiry"             | 123                         | '{"expiry":123}'
        DOUBLE            | LightningApi.NetworkInfo.newInstance()       | "avg_out_degree"     | (double) 1928127.00001      | '{"avgOutDegree":1928127.00001}'
        LONG              | LightningApi.Payment.newInstance()           | "fee"                | 1292817282L                 | '{"fee":1292817282}'
        BOOLEAN           | LightningApi.Invoice.newInstance()           | "settled"            | true                        | '{"settled":true}'
        BYTE_STRING       | LightningApi.Invoice.newInstance()           | "r_preimage"         | byteString                  | '{"rPreimage":"' + base64EncodedByteString + '"}'
        ENUM              | LightningApi.NewAddressRequest.newInstance() | "type"               | WITNESS_PUBKEY_HASH                 | '{"type":"WITNESS_PUBKEY_HASH"}'
        MESSAGE           | LightningApi.SendResponse.newInstance()      | "payment_route"      | createRoute().build()       | '{"paymentRoute":{"totalTimeLock":0,"totalFees":543234,"totalAmt":123,"hops":[],"totalFeesMsat":0,"totalAmtMsat":0}}'

    }

    @Unroll
    def "Verify that setRepeatedValue creates Json Array correctly for type #javaType"(){
        setup:
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder()
        def builder = instance.toBuilder()
        Descriptors.FieldDescriptor fieldDescriptor = instance.descriptor.fields.find {it.name == field}
        values.each {
            builder.addRepeatedField(fieldDescriptor, it)
        }
        when:
        JsonGenUtils.setRepeatedValue(jsonObjectBuilder,builder,fieldDescriptor)
        then:
        jsonObjectBuilder.build().toString() == jsonData
        where:
        javaType          | instance                                       | field                | values                                                     | jsonData
        STRING            | LightningApi.GetInfoResponse.newInstance()     | "uris"             | ["SomeString1","SomeString2"]                              | '{"uris":["SomeString1","SomeString2"]}'
        MESSAGE           | LightningApi.QueryRoutesResponse.newInstance() | "routes"             | [createRoute().build(),createRoute(321L).build()] | '{"routes":[{"totalTimeLock":0,"totalFees":543234,"totalAmt":123,"hops":[],"totalFeesMsat":0,"totalAmtMsat":0},{"totalTimeLock":0,"totalFees":543234,"totalAmt":321,"hops":[],"totalFeesMsat":0,"totalAmtMsat":0}]}'
    }

    @Unroll
    def "Verify that readRepeatedValue sets values from JsonArray correctly for type #javaType"(){
        setup:
        JsonReader jsonReader = Json.createReader(new StringReader(jsonData))
        Builder builder = instance.toBuilder()
        Descriptors.FieldDescriptor fieldDescriptor = instance.descriptor.fields.find {it.name == field}

        JsonArray jsonArray = jsonReader.readObject().getJsonArray(field)
        when:
        JsonGenUtils.readRepeatedValue(jsonArray,builder,fieldDescriptor)
        then:
        builder.getRepeatedFieldCount(fieldDescriptor) == values.size()
        for(int i=0;i<values.size();i++){
            builder.getRepeatedField(fieldDescriptor,i) == values.get(i)
        }

        where:
        javaType          | instance                                       | field                | values                                                     | jsonData
        STRING            | LightningApi.GetInfoResponse.newInstance()     | "uris"             | ["SomeString1","SomeString2"]                              | '{"uris":["SomeString1","SomeString2"]}'
        MESSAGE           | LightningApi.QueryRoutesResponse.newInstance() | "routes"             | [createRoute().build(),createRoute(321L).build()] | '{"routes":[{"total_time_lock":0,"total_fees":543234,"total_amt":123,"hops":[]},{"total_time_lock":0,"total_fees":543234,"total_amt":321,"hops":[]}]}'
    }


    def "Verify that mapped fields are converted properly to json"(){
        setup:
        def instance = LightningApi.SendManyRequest.newInstance()
        def builder = instance.toBuilder()

        builder.putAddrToAmount("SomeKey1",1234L)
        builder.putAddrToAmount("SomeKey2",2345L)

        when:
        JsonObjectBuilder jsonObjectBuilder = JsonGenUtils.messageToJson(builder,instance.descriptor)

        then:
        jsonObjectBuilder.build().toString() == '{"AddrToAmount":[{"key":"SomeKey1","value":1234},{"key":"SomeKey2","value":2345}],"targetConf":0,"satPerByte":0,"label":""}'
    }

    def "Verify that json containing mapped fields are converted properly from json"(){
        setup:
        String jsonData = '{"AddrToAmount":[{"key":"SomeKey1","value":1234},{"key":"SomeKey2","value":2345}]}'
        JsonReader jsonReader = Json.createReader(new StringReader(jsonData))
        def instance = LightningApi.SendManyRequest.newInstance()

        when:
        LightningApi.SendManyRequest.Builder builder = JsonGenUtils.jsonToMessage(jsonReader.readObject(),instance.descriptor)

        then:
        builder.getAddrToAmountMap()["SomeKey1"] == 1234L
        builder.getAddrToAmountMap()["SomeKey2"] == 2345L
    }


    def "Verify that messageToJson converts a message fields to json that contain single values"(){
        when:
        JsonObjectBuilder result = JsonGenUtils.messageToJson(createInvoice(),LightningApi.Invoice.descriptor)
        then:
        result.build().toString() == '{"memo":"SomeMemo","rPreimage":"","rHash":"","value":0,"valueMsat":0,"settled":false,"creationDate":5432123,"settleDate":0,"paymentRequest":"","descriptionHash":"VGVzdA==","expiry":5432343,"fallbackAddr":"","cltvExpiry":12345,"routeHints":[],"private":false,"addIndex":0,"settleIndex":0,"amtPaid":0,"amtPaidSat":0,"amtPaidMsat":0,"state":"OPEN","htlcs":[],"features":[],"isKeysend":false}'
    }

    def "Verify that jsonToMessage converts json to a message fields that contain single values"(){
        setup:
        JsonObject jsonObject = Json.createReader(new StringReader('{"memo":"SomeMemo","rPreimage":"","rHash":"","value":0,"valueMsat":0,"settled":false,"creationDate":5432123,"settleDate":0,"paymentRequest":"","descriptionHash":"VGVzdA==","expiry":5432343,"fallbackAddr":"","cltvExpiry":12345,"routeHints":[],"private":false,"addIndex":0,"settleIndex":0,"amtPaid":0,"amtPaidSat":0,"amtPaidMsat":0,"state":"OPEN","htlcs":[],"features":[],"isKeysend":false}')).readObject()
        when:
        Invoice result = JsonGenUtils.jsonToMessage(jsonObject,LightningApi.Invoice.descriptor).build()
        then:
        result.getMemo() == "SomeMemo"
        result.getState() == Invoice.InvoiceState.OPEN
        result.getCreationDate() == 5432123L

        when: // Verify that json data with not all fields set doesn't throw exception
        jsonObject = Json.createReader(new StringReader('{"memo":"SomeMemo","receipt":"","r_preimage":"","r_hash":"","value":0}')).readObject()
        result = JsonGenUtils.jsonToMessage(jsonObject,LightningApi.Invoice.descriptor).build()
        then:
        result.getMemo() == "SomeMemo"
        result.getState() == Invoice.InvoiceState.OPEN
        result.getCreationDate() == 0L

    }

    private static Invoice.Builder createInvoice(){
        LightningApi.Invoice.Builder builder = LightningApi.Invoice.newBuilder()
        builder.setMemo("SomeMemo")
        builder.setCltvExpiry(12345L)
        builder.setCreationDate(5432123L)
        builder.setDescriptionHash(ByteString.copyFrom("Test".getBytes()))
        builder.setExpiry(5432343L)
        return builder
    }

    private static Route.Builder createRoute(long totalAmt=123L){
        LightningApi.Route.Builder builder = LightningApi.Route.newBuilder()
        builder.setTotalAmt(totalAmt)
        builder.setTotalFees(543234L)
        return builder
    }

    private static LightningApi.SendResponse.Builder createSendResponse(){
        LightningApi.SendResponse.Builder builder = LightningApi.SendResponse.newBuilder()
        builder.setPaymentRoute(createRoute().build())
        return builder

    }

}
