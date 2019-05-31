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
package org.lightningj.lnd.wrapper;

import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Status;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper factory converting a GRPC Message into it's wrapper object.
 *
 * Created by Philip Vendil.
 */
public class WrapperFactory {

    private static final Map<String,String> wrapperPackages = new HashMap<>();
    static {
        wrapperPackages.put("org.lightningj.lnd.proto.LightningApi$","org.lightningj.lnd.wrapper.message.");
        wrapperPackages.put("org.lightningj.lnd.autopilot.proto.AutopilotOuterClass$","org.lightningj.lnd.wrapper.autopilot.message.");
        wrapperPackages.put("org.lightningj.lnd.chainnotifier.proto.ChainNotifierOuterClass$","org.lightningj.lnd.wrapper.chainnotifier.message.");
        wrapperPackages.put("org.lightningj.lnd.invoices.proto.InvoicesOuterClass$"," org.lightningj.lnd.wrapper.invoices.message.");
        wrapperPackages.put("org.lightningj.lnd.router.proto.RouterOuterClass$","org.lightningj.lnd.wrapper.router.message.");
        wrapperPackages.put("org.lightningj.lnd.signer.proto.SignerOuterClass$","org.lightningj.lnd.wrapper.signer.message.");
        wrapperPackages.put("org.lightningj.lnd.walletkit.proto.WalletKitOuterClass$","org.lightningj.lnd.wrapper.walletkit.message.");
    }


    private static final WrapperFactory instance = new WrapperFactory();

    /**
     *
     * @return returns a singleton instance of the WrapperFactory.
     */
    public static WrapperFactory getInstance(){
        return instance;
    }


    /**
     * Method to wrap an GRPC object to it's wrapped object.
     *
     * @param apiObject the GRPC object to wrap
     * @return the wrapped variant of the GRPC object.
     * @throws ClientSideException if problems occurred constructing the wrapped object.
     */
    public Message wrap(GeneratedMessageV3 apiObject) throws ClientSideException{
        Class c;
        try {
            String sourceName = apiObject.getClass().getName();
            String className = null;
            for(String sourcePackage : wrapperPackages.keySet())
            if(sourceName.startsWith(sourcePackage)){
                sourceName = sourceName.substring(sourcePackage.length());
                String targetBasePackage = wrapperPackages.get(sourcePackage);
                className = targetBasePackage + sourceName;
            }
            if(className == null){
                throw new ClientSideException("Error looking up wrapper class, verify that wrapper class for API class: " + apiObject.getClass().getName() + " exists.", Status.INTERNAL);
            }
            c = WrapperFactory.class.getClassLoader().loadClass(className);
        }catch(Exception e){
            throw new ClientSideException("Error converting GRPC object " + apiObject.getClass().getSimpleName() + " to wrapped object, message: " + e.getMessage(),null,e);
        }
        try {
            Constructor constructor = c.getConstructor(apiObject.getClass());
            return (Message) constructor.newInstance(apiObject);
        }catch(Exception e){
            throw new  ClientSideException("Error constructing wrapper for GRPC object " + apiObject.getClass().getSimpleName()  + ", message: " + e.getMessage(),null,e);
        }
    }


}
