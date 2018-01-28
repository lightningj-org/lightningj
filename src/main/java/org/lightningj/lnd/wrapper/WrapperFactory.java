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
package org.lightningj.lnd.wrapper;

import com.google.protobuf.GeneratedMessageV3;

import java.lang.reflect.Constructor;

/**
 * Wrapper factory converting a GRPC Message into it's wrapper object.
 *
 * Created by Philip Vendil.
 */
public class WrapperFactory {

    private static final String BASE_PACKAGE = "org.lightningj.lnd.wrapper.message.";

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
            c = WrapperFactory.class.getClassLoader().loadClass(BASE_PACKAGE + apiObject.getClass().getSimpleName());
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
