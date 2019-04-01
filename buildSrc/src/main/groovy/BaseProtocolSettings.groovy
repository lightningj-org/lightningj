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
 * Base Class containing common methods that doesn't change for each protocol.
 *
 * Created by Philip Vendil on 2019-03-30.
 *
 * @see ProtocolSettings
 */
abstract class BaseProtocolSettings {

    String protocol

    abstract String getAPIClassPath()

    String getMessageOutputDir(){
        if(protocol == "lnrpc"){
            return "build/generated/source/wrapper/main/java/org/lightningj/lnd/wrapper/message/"
        }
        return "build/generated/source/wrapper/main/java/org/lightningj/lnd/wrapper/${protocol}/message/"
    }

    String getCallerOutputDir(){
        if(protocol == "lnrpc"){
            return "build/generated/source/wrapper/main/java/org/lightningj/lnd/wrapper/"
        }
        return "build/generated/source/wrapper/main/java/org/lightningj/lnd/wrapper/${protocol}/"
    }

    String getResourcesOutputDir(){
        if(protocol == "lnrpc") {
            return "build/resources/main/org/lightningj/lnd/wrapper/message/"
        }
        return "build/resources/main/org/lightningj/lnd/wrapper/${protocol}/message/"
    }

    String getWrapperBasePackageName(){
        if(protocol == "lnrpc") {
            return "org.lightningj.lnd.wrapper"
        }
        return "org.lightningj.lnd.wrapper.${protocol}"
    }

    String getAPIPackage(){
        if(protocol == "lnrpc"){
            return "org.lightningj.lnd.proto"
        }
        return "org.lightningj.lnd.${protocol}.proto"
    }

    String getJAXBIndexResouceLocation(){
        if(protocol == "lnrpc"){
            return "org/lightningj/lnd/wrapper/message/jaxb.index"
        }
        return "org/lightningj/lnd/wrapper/${protocol}/message/jaxb.index"
    }

    String getXMLNameSpace(){
        if(protocol == "lnrpc"){
            return "http://lightningj.org/xsd/lndjapi_1_0"
        }
        return "http://lightningj.org/xsd/lndjapi/${protocol}_1_0"
    }

    String getAPIClassName(){
        String[] values = getAPIClassPath().split("\\.")
        return values[values.length-1]
    }

    String getXSDName(){
        if(protocol == "lnrpc"){
            return "lnd_v1.xsd"
        }
        return protocol + "_v1.xsd"
    }

    String getJaxbSrcDirectory(){
        if(protocol == "lnrpc"){
            return "org.lightningj.lnd.wrapper.message"
        }
        return "org.lightningj.lnd.wrapper.${protocol}.message"
    }
}