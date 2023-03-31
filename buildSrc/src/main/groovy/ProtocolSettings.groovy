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
            case "watchtower":
                return "org.lightningj.lnd.watchtower.proto.WatchtowerOuterClass"
            case "wtclient":
                return "org.lightningj.lnd.wtclient.proto.Wtclient"
            case "verrpc":
                return "org.lightningj.lnd.verrpc.proto.Verrpc"
            case "walletunlocker":
                return "org.lightningj.lnd.walletunlocker.proto.Walletunlocker"
            case "stateservice":
                return "org.lightningj.lnd.stateservice.proto.Stateservice"
            case "dev":
                return "org.lightningj.lnd.dev.proto.DevOuterClass"
            case "peers":
                return "org.lightningj.lnd.peers.proto.PeersOuterClass"
            case "neutrino":
                return "org.lightningj.lnd.neutrino.proto.Neutrino"
        }
    }

    String getSpecialAPIImports(){
        switch (protocol){
            case "lnrpc":
                return """import org.lightningj.lnd.wrapper.message.Invoice.InvoiceState;"""
            case "invoices":
                return """import org.lightningj.lnd.proto.LightningApi;
import org.lightningj.lnd.wrapper.message.Invoice;
import org.lightningj.lnd.wrapper.message.RouteHint;
import org.lightningj.lnd.wrapper.message.PaymentHash;
"""
            case "walletkit":
                return """import org.lightningj.lnd.wrapper.message.OutPoint;
import org.lightningj.lnd.wrapper.signer.message.TxOut;
import org.lightningj.lnd.wrapper.signer.message.KeyLocator;
import org.lightningj.lnd.wrapper.signer.message.KeyDescriptor;
import org.lightningj.lnd.signer.proto.SignerOuterClass;
import org.lightningj.lnd.wrapper.message.Utxo;
import org.lightningj.lnd.wrapper.message.TransactionDetails;
"""
            case "router":
                return """import org.lightningj.lnd.proto.LightningApi;
import org.lightningj.lnd.wrapper.message.Route;
import org.lightningj.lnd.wrapper.message.RouteHint;
import org.lightningj.lnd.wrapper.message.HTLCAttempt;
import org.lightningj.lnd.wrapper.message.FeatureBit;
import org.lightningj.lnd.wrapper.message.Failure;
import org.lightningj.lnd.wrapper.message.Failure.FailureCode;
import org.lightningj.lnd.wrapper.message.Payment;
import org.lightningj.lnd.wrapper.message.ChannelPoint;
"""
            case "walletunlocker":
                return """
import org.lightningj.lnd.wrapper.message.ChanBackupSnapshot;
"""
            case "dev":
                return """
import org.lightningj.lnd.wrapper.message.LightningNode;
import org.lightningj.lnd.wrapper.message.ChannelEdge;
import org.lightningj.lnd.wrapper.message.ChannelGraph;
"""
            case "peers":
                return """
import org.lightningj.lnd.wrapper.message.Op;
import org.lightningj.lnd.wrapper.message.FeatureBit;
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
            case "watchtower":
                return [
                        new ApiSettings(
                                baseGrpcClassPath:'org.lightningj.lnd.watchtower.proto.WatchtowerGrpc$Watchtower',
                                grpcClassName: 'WatchtowerGrpc',
                                baseApiClassName: 'WatchtowerAPI',
                                baseProtoClassPath: 'org.lightningj.lnd.watchtower.proto.WatchtowerOuterClass',
                                baseStubClass: 'Watchtower',
                                baseFileName: 'WatchtowerAPI.java'
                        )
                ]
            case "wtclient":
                return [
                        new ApiSettings(
                                baseGrpcClassPath:'org.lightningj.lnd.wtclient.proto.WatchtowerClientGrpc$WatchtowerClient',
                                grpcClassName: 'WatchtowerClientGrpc',
                                baseApiClassName: 'WatchtowerClientAPI',
                                baseProtoClassPath: 'org.lightningj.lnd.wtclient.proto.Wtclient',
                                baseStubClass: 'WatchtowerClient',
                                baseFileName: 'WatchtowerClientAPI.java'
                        )
                ]
            case "verrpc":
                return [
                        new ApiSettings(
                                baseGrpcClassPath:'org.lightningj.lnd.verrpc.proto.VersionerGrpc$Versioner',
                                grpcClassName: 'VersionerGrpc',
                                baseApiClassName: 'VersionerAPI',
                                baseProtoClassPath: 'org.lightningj.lnd.verrpc.proto.Verrpc',
                                baseStubClass: 'Versioner',
                                baseFileName: 'VersionerAPI.java'
                        )
                ]
            case "walletunlocker":
                return [
                        new ApiSettings(
                                baseGrpcClassPath:'org.lightningj.lnd.walletunlocker.proto.WalletUnlockerGrpc$WalletUnlocker',
                                grpcClassName: 'WalletUnlockerGrpc',
                                baseApiClassName: 'WalletUnlockerAPI',
                                baseProtoClassPath: 'org.lightningj.lnd.walletunlocker.proto.Walletunlocker',
                                baseStubClass: 'WalletUnlocker',
                                baseFileName: 'WalletUnlockerAPI.java'
                        )
                ]
            case "stateservice":
                return [
                        new ApiSettings(
                                baseGrpcClassPath:'org.lightningj.lnd.stateservice.proto.StateGrpc$State',
                                grpcClassName: 'StateGrpc',
                                baseApiClassName: 'StateServiceAPI',
                                baseProtoClassPath: 'org.lightningj.lnd.stateservice.proto.Stateservice',
                                baseStubClass: 'State',
                                baseFileName: 'StateServiceAPI.java'
                        )
                ]
            case "dev":
                return [
                        new ApiSettings(
                                baseGrpcClassPath:'org.lightningj.lnd.dev.proto.DevGrpc$Dev',
                                grpcClassName: 'DevGrpc',
                                baseApiClassName: 'DevAPI',
                                baseProtoClassPath: 'org.lightningj.lnd.dev.proto.DevOuterClass',
                                baseStubClass: 'Dev',
                                baseFileName: 'DevAPI.java'
                        )
                ]
            case "peers":
                return [
                        new ApiSettings(
                                baseGrpcClassPath:'org.lightningj.lnd.peers.proto.PeersGrpc$Peers',
                                grpcClassName: 'PeersGrpc',
                                baseApiClassName: 'PeersAPI',
                                baseProtoClassPath: 'org.lightningj.lnd.peers.proto.PeersOuterClass',
                                baseStubClass: 'Peers',
                                baseFileName: 'PeersAPI.java'
                        )
                ]
            case "neutrino":
                return [
                        new ApiSettings(
                                baseGrpcClassPath:'org.lightningj.lnd.neutrino.proto.NeutrinoKitGrpc$NeutrinoKit',
                                grpcClassName: 'NeutrinoKitGrpc',
                                baseApiClassName: 'NeutrinoAPI',
                                baseProtoClassPath: 'org.lightningj.lnd.neutrino.proto.Neutrino',
                                baseStubClass: 'NeutrinoKit',
                                baseFileName: 'NeutrinoAPI.java'
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
            case "dev":
                return [
                        "ChannelGraph" : "org.lightningj.lnd.proto.LightningApi"
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
                break
            case "invoices":
                if(methodName == "subscribeSingleInvoice" || methodName == "lookupInvoiceV2"){
                    return "LightningApi"
                }
                break
            case "router":
                if(methodName == "sendPaymentV2" || methodName == "trackPaymentV2" || methodName == "sendToRouteV2" || methodName == "trackPayments"){
                    return "LightningApi"
                }
            case "dev":
                if(methodName == "importGraph"){
                    return "DevOuterClass"
                }
        }
        return null
    }

    String getApiTypeName(String type){
        return "LightningApi." + type
    }

    String getExternalNamespaces(){
        return """
          @jakarta.xml.bind.annotation.XmlNs(namespaceURI = "http://lightningj.org/xsd/lndjapi_1_0", prefix = ""),
          @jakarta.xml.bind.annotation.XmlNs(namespaceURI = "http://lightningj.org/xsd/autopilot_1_0", prefix = "autopilot"),
          @jakarta.xml.bind.annotation.XmlNs(namespaceURI = "http://lightningj.org/xsd/chainnotifier_1_0", prefix = "chainnotifier"),
          @jakarta.xml.bind.annotation.XmlNs(namespaceURI = "http://lightningj.org/xsd/invoices_1_0", prefix = "invoices"),
          @jakarta.xml.bind.annotation.XmlNs(namespaceURI = "http://lightningj.org/xsd/router_1_0", prefix = "router"),
          @jakarta.xml.bind.annotation.XmlNs(namespaceURI = "http://lightningj.org/xsd/signer_1_0", prefix = "signer"),
          @jakarta.xml.bind.annotation.XmlNs(namespaceURI = "http://lightningj.org/xsd/walletkit_1_0", prefix = "walletkit"),
          @jakarta.xml.bind.annotation.XmlNs(namespaceURI = "http://lightningj.org/xsd/watchtower_1_0", prefix = "watchtower"),
          @jakarta.xml.bind.annotation.XmlNs(namespaceURI = "http://lightningj.org/xsd/wtclient_1_0", prefix = "wtclient"),
          @jakarta.xml.bind.annotation.XmlNs(namespaceURI = "http://lightningj.org/xsd/verrpc_1_0", prefix = "verrpc"),
          @jakarta.xml.bind.annotation.XmlNs(namespaceURI = "http://lightningj.org/xsd/walletunlocker_1_0", prefix = "walletunlocker"),
          @jakarta.xml.bind.annotation.XmlNs(namespaceURI = "http://lightningj.org/xsd/stateservice_1_0", prefix = "stateservice"),
          @jakarta.xml.bind.annotation.XmlNs(namespaceURI = "http://lightningj.org/xsd/dev_1_0", prefix = "dev"),
          @jakarta.xml.bind.annotation.XmlNs(namespaceURI = "http://lightningj.org/xsd/neutrino_1_0", prefix = "neutrino"),
          @jakarta.xml.bind.annotation.XmlNs(namespaceURI = "http://lightningj.org/xsd/peers_1_0", prefix = "peers")
"""
    }

}