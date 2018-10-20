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

import org.lightningj.lnd.proto.LightningApi
import org.lightningj.lnd.wrapper.message.OpenChannelRequest
import org.lightningj.lnd.wrapper.message.OpenStatusUpdate
import org.lightningj.lnd.wrapper.message.WalletBalanceRequest
import spock.lang.Specification

import javax.json.Json
import javax.json.JsonObject
import javax.json.JsonObjectBuilder
import javax.json.JsonReader

/**
 * Unit test for Message Wrapper abstract base class.
 *
 * Created by Philip Vendil.
 */
class MessageSpec extends Specification {

    def "Verify that JSON Parsing constructor populates fields properly"(){
        setup:
        String jsonData = "{\"node_pubkey\":\"\",\"node_pubkey_string\":\"02ad1fddad0c572ec3e886cbea31bbafa30b5f7e745da7e936ed9d1471116cdc02\",\"local_funding_amount\":40000,\"push_sat\":25000,\"targetConf\":0,\"satPerByte\":0,\"private\":false,\"min_htlc_msat\":0}"
        JsonReader jsonReader = Json.createReader(new StringReader(jsonData))
        when:
        OpenChannelRequest openChannelRequest = new OpenChannelRequest(jsonReader)

        then:
        openChannelRequest.getLocalFundingAmount() == 40000L
    }

    def "Verify that default constructor initializes and empty builder"(){
        when:
        new OpenChannelRequest().setTargetConf(1)
        then:
        true // If underlying builder is null should null pointer be thrown.
    }

    def "Verify that toJson converts to JSON Data"(){
        when:
        JsonObjectBuilder jsonObjectBuilder = genOpenChannelRequest().toJson()
        JsonObject jsonObject = jsonObjectBuilder.build()
        then:
        jsonObject.getString("node_pubkey_string") == "02ad1fddad0c572ec3e886cbea31bbafa30b5f7e745da7e936ed9d1471116cdc02"
    }

    def "Verify that toJsonAsString returns json in string format"(){
        expect:
        genOpenChannelRequest().toJsonAsString(false) == '{"node_pubkey":"","node_pubkey_string":"02ad1fddad0c572ec3e886cbea31bbafa30b5f7e745da7e936ed9d1471116cdc02","local_funding_amount":40000,"push_sat":25000,"targetConf":0,"satPerByte":0,"private":false,"min_htlc_msat":0,"remote_csv_delay":0,"min_confs":0}'
        genOpenChannelRequest().toJsonAsString(true) == """
{
    "node_pubkey": "",
    "node_pubkey_string": "02ad1fddad0c572ec3e886cbea31bbafa30b5f7e745da7e936ed9d1471116cdc02",
    "local_funding_amount": 40000,
    "push_sat": 25000,
    "targetConf": 0,
    "satPerByte": 0,
    "private": false,
    "min_htlc_msat": 0,
    "remote_csv_delay": 0,
    "min_confs": 0
}"""
    }

    def "Verify that getApiObject() return low level API object"(){
        when:
        LightningApi.OpenChannelRequest apiObject = genOpenChannelRequest().getApiObject()
        then:
        apiObject.getLocalFundingAmount() == 40000
    }

    def "Verify that getMessageName return the name of the underlying message"(){
        expect:
        genOpenChannelRequest().getMessageName() == "OpenChannelRequest"
        new OpenStatusUpdate().getMessageName() == "OpenStatusUpdate"
        new WalletBalanceRequest().getMessageName() == "WalletBalanceRequest"
    }

    def "Verify that toString() returns the underlying data in String representation"(){
        expect:
        genOpenChannelRequest().toString() == """OpenChannelRequest: 
{
    "node_pubkey": "",
    "node_pubkey_string": "02ad1fddad0c572ec3e886cbea31bbafa30b5f7e745da7e936ed9d1471116cdc02",
    "local_funding_amount": 40000,
    "push_sat": 25000,
    "targetConf": 0,
    "satPerByte": 0,
    "private": false,
    "min_htlc_msat": 0,
    "remote_csv_delay": 0,
    "min_confs": 0
}"""
    }

    def "Verify that equals and hashcode works"(){
        setup:
        OpenChannelRequest o1 = genOpenChannelRequest()
        OpenChannelRequest o2 = genOpenChannelRequest()
        OpenChannelRequest o3 = genOpenChannelRequest(5000)
        WalletBalanceRequest o4 = new WalletBalanceRequest()
        expect:
        o1 == o2
        o1 != o3
        o1 != o4

        o1.hashCode() == o2.hashCode()
        o1.hashCode() != o3.hashCode()
        o1.hashCode() != o4.hashCode()
    }

    def "Verify that validate generates a validation report"(){
        expect:
        genOpenChannelRequest().validate() instanceof ValidationResult
        genOpenChannelRequest().validate().isValid()
    }

    private OpenChannelRequest genOpenChannelRequest(long localAmount=40000){
        OpenChannelRequest openChannelRequest = new OpenChannelRequest()
        openChannelRequest.setNodePubkeyString("02ad1fddad0c572ec3e886cbea31bbafa30b5f7e745da7e936ed9d1471116cdc02")
        openChannelRequest.setLocalFundingAmount(localAmount)
        openChannelRequest.setPushSat(25000)
        openChannelRequest.setSatPerByte(0)

        return openChannelRequest
    }
}
