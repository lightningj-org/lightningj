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

/**
 * Class containing settings for a specific protocol to be generated.
 *
 * Update this class to relect all settings for a specific protocol.
 *
 * Created by philip on 2019-03-30.
 */
class ProtocolSettings extends BaseProtocolSettings{

    String getAPIClassPath(){
        switch(protocol){
            case "lnrpc":
                return "org.lightningj.lnd.proto.LightningApi"
            case "autopilot":
                return "org.lightningj.lnd.autopilot.proto.AutopilotOuterClass"
            case "chainnotifier":
                return "org.lightningj.lnd.chainnotifier.proto.ChainNotifierOuterClass"
            case "invoices":
                return "org.lightningj.lnd.invoices.proto.InvoicesOuterClass"
            case "router":
                return "org.lightningj.lnd.router.proto.RouterOuterClass"
            case "signer":
                return "org.lightningj.lnd.signer.proto.SignerOuterClass"
            case "walletkit":
                return "org.lightningj.lnd.walletkit.proto.WalletKitOuterClass"
        }
    }

    String getSpecialAPIImports(){
        switch (protocol){
            case "lnrpc":
                return """import org.lightningj.lnd.wrapper.message.Invoice.InvoiceState;"""
            case "invoices":
                return """import org.lightningj.lnd.wrapper.message.Invoice;
import org.lightningj.lnd.wrapper.message.RouteHint;
import org.lightningj.lnd.wrapper.message.PaymentHash;
"""
            case "walletkit":
                return """import org.lightningj.lnd.wrapper.signer.message.TxOut;
import org.lightningj.lnd.wrapper.signer.message.KeyLocator;
import org.lightningj.lnd.wrapper.signer.message.KeyDescriptor;
import org.lightningj.lnd.signer.proto.SignerOuterClass;
"""
            default:
                return ""
        }
    }

    List<ApiSettings> getApiSettings(){
        switch (protocol){
            case "lnrpc":
                return [
                        new ApiSettings(
                                baseGrpcClassPath:'org.lightningj.lnd.proto.LightningGrpc$Lightning',
                                grpcClassName: 'LightningGrpc',
                                baseApiClassName: 'LndAPI',
                                baseProtoClassPath: 'org.lightningj.lnd.proto.LightningApi',
                                baseStubClass: 'Lightning',
                                baseFileName: 'LndAPI.java'
                        ),
                        new ApiSettings(
                                baseGrpcClassPath:'org.lightningj.lnd.proto.WalletUnlockerGrpc$WalletUnlocker',
                                grpcClassName: 'WalletUnlockerGrpc',
                                baseApiClassName: 'WalletUnlockerAPI',
                                baseProtoClassPath: 'org.lightningj.lnd.proto.LightningApi',
                                baseStubClass: 'WalletUnlocker',
                                baseFileName: 'WalletUnlockerAPI.java'
                        )
                ]
            case "autopilot":
                return [
                        new ApiSettings(
                                baseGrpcClassPath:'org.lightningj.lnd.autopilot.proto.AutopilotGrpc$Autopilot',
                                grpcClassName: 'AutopilotGrpc',
                                baseApiClassName: 'AutopilotAPI',
                                baseProtoClassPath: 'org.lightningj.lnd.autopilot.proto.AutopilotOuterClass',
                                baseStubClass: 'Autopilot',
                                baseFileName: 'AutopilotAPI.java'
                        )
                ]
            case "chainnotifier":
                return [
                        new ApiSettings(
                                baseGrpcClassPath:'org.lightningj.lnd.chainnotifier.proto.ChainNotifierGrpc$ChainNotifier',
                                grpcClassName: 'ChainNotifierGrpc',
                                baseApiClassName: 'ChainNotifierAPI',
                                baseProtoClassPath: 'org.lightningj.lnd.chainnotifier.proto.ChainNotifierOuterClass',
                                baseStubClass: 'ChainNotifier',
                                baseFileName: 'ChainNotifierAPI.java'
                        )
                ]
            case "invoices":
                return [
                        new ApiSettings(
                                baseGrpcClassPath:'org.lightningj.lnd.invoices.proto.InvoicesGrpc$Invoices',
                                grpcClassName: 'InvoicesGrpc',
                                baseApiClassName: 'InvoicesAPI',
                                baseProtoClassPath: 'org.lightningj.lnd.invoices.proto.InvoicesOuterClass',
                                baseStubClass: 'Invoices',
                                baseFileName: 'InvoicesAPI.java'
                        )
                ]
            case "router":
                return [
                        new ApiSettings(
                                baseGrpcClassPath:'org.lightningj.lnd.router.proto.RouterGrpc$Router',
                                grpcClassName: 'RouterGrpc',
                                baseApiClassName: 'RouterAPI',
                                baseProtoClassPath: 'org.lightningj.lnd.router.proto.RouterOuterClass',
                                baseStubClass: 'Router',
                                baseFileName: 'RouterAPI.java'
                        )
                ]
            case "signer":
                return [
                        new ApiSettings(
                                baseGrpcClassPath:'org.lightningj.lnd.signer.proto.SignerGrpc$Signer',
                                grpcClassName: 'SignerGrpc',
                                baseApiClassName: 'SignerAPI',
                                baseProtoClassPath: 'org.lightningj.lnd.signer.proto.SignerOuterClass',
                                baseStubClass: 'Signer',
                                baseFileName: 'SignerAPI.java'
                        )
                ]
            case "walletkit":
                return [
                        new ApiSettings(
                                baseGrpcClassPath:'org.lightningj.lnd.walletkit.proto.WalletKitGrpc$WalletKit',
                                grpcClassName: 'WalletKitGrpc',
                                baseApiClassName: 'WalletKitAPI',
                                baseProtoClassPath: 'org.lightningj.lnd.walletkit.proto.WalletKitOuterClass',
                                baseStubClass: 'WalletKit',
                                baseFileName: 'WalletKitAPI.java'
                        )
                ]
            default:
                return []
        }
    }

    Map getImportedRequestApiClassPathes(){
        switch (protocol){
            case "invoices":
                return [
                        "PaymentHash" : "org.lightningj.lnd.proto.LightningApi",
                        "RouteHint" : "org.lightningj.lnd.proto.LightningApi",
                        "Invoice" : "org.lightningj.lnd.proto.LightningApi"
                ]
            case "walletkit":
                return [
                        "KeyDescriptor" : "org.lightningj.lnd.signer.proto.SignerOuterClass",
                        "KeyLocator" : "org.lightningj.lnd.signer.proto.SignerOuterClass",
                        "TxOut" : "org.lightningj.lnd.signer.proto.SignerOuterClass"
                ]
            default:
                return [:]
        }
    }

    String getSpecialApiResponseClassName(String methodName, String requestName){
        switch (protocol){
            case "walletkit":
                if(methodName == "deriveKey" || methodName == "deriveNextKey"){
                    return "SignerOuterClass"
                }
        }
        return null
    }

    String getExternalNamespaces(){
        return """
          @javax.xml.bind.annotation.XmlNs(namespaceURI = "http://lightningj.org/xsd/lndjapi_1_0", prefix = ""),
          @javax.xml.bind.annotation.XmlNs(namespaceURI = "http://lightningj.org/xsd/autopilot_1_0", prefix = "autopilot"),
          @javax.xml.bind.annotation.XmlNs(namespaceURI = "http://lightningj.org/xsd/chainnotifier_1_0", prefix = "chainnotifier"),
          @javax.xml.bind.annotation.XmlNs(namespaceURI = "http://lightningj.org/xsd/invoices_1_0", prefix = "invoices"),
          @javax.xml.bind.annotation.XmlNs(namespaceURI = "http://lightningj.org/xsd/router_1_0", prefix = "router"),
          @javax.xml.bind.annotation.XmlNs(namespaceURI = "http://lightningj.org/xsd/signer_1_0", prefix = "signer"),
          @javax.xml.bind.annotation.XmlNs(namespaceURI = "http://lightningj.org/xsd/walletkit_1_0", prefix = "walletkit")
"""
    }

}