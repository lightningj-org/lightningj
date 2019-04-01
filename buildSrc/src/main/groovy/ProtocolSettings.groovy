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
        }
    }

    String getSpecialAPIImports(){
        switch (protocol){
            case "lnrpc":
                return """import org.lightningj.lnd.wrapper.message.Invoice.InvoiceState;"""
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
            default:
                return []
        }
    }

}