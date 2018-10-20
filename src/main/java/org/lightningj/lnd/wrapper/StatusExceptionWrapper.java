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

import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;


/**
 * Class wrapping a GRPC status exception.
 *
 * Created by Philip Vendil.
 */
public class StatusExceptionWrapper {

    private static final StatusExceptionWrapper instance = new StatusExceptionWrapper();

    /**
     *
     * @return returns a singleton instance of the StatusExceptionWrapper.
     */
    public static StatusExceptionWrapper getInstance(){
        return instance;
    }

    /**
     * Method that will wrap a StatusRuntimeException or StatusException to either
     * a ClientSideException/CommunicationException/ServerSideException (which all are a StatusException).
     *
     *
     * @param e the exception to wrap, should be either StatusRuntimeException or StatusException.
     * @throws org.lightningj.lnd.wrapper.StatusException the converted exception. Either a ClientSideException,
     * CommunicationException or ServerSideException.
     * @see  org.lightningj.lnd.wrapper.ClientSideException
     * @see  org.lightningj.lnd.wrapper.CommunicationException
     * @see  org.lightningj.lnd.wrapper.ServerSideException
     */
    public org.lightningj.lnd.wrapper.StatusException wrap(Exception e)  {
        if(e instanceof StatusRuntimeException || e instanceof StatusException){
            Status status = null;
            if(e instanceof StatusRuntimeException){
                status = ((StatusRuntimeException) e).getStatus();
            }
            if(e instanceof StatusException){
                status = ((StatusException) e).getStatus();
            }
            if(status != null) {
                assert status != Status.OK;

                switch (status.getCode()) {
                    case CANCELLED:
                    case INVALID_ARGUMENT:
                    case NOT_FOUND:
                    case ALREADY_EXISTS:
                    case PERMISSION_DENIED:
                    case OUT_OF_RANGE:
                    case UNAUTHENTICATED:
                        return new ClientSideException(e.getMessage(),status,e);
                    case DEADLINE_EXCEEDED:
                    case UNAVAILABLE:
                        return new CommunicationException(e.getMessage(),status,e);
                    default:
                    return  new ServerSideException(e.getMessage(),status,e);
                }
            }
            return new ServerSideException("Internal Error, couldn't determine status in GRPC call.",null,e);
        }
        return new ServerSideException("Internal Error, cannot convert exception of type: " +e.getClass().getSimpleName(), null,e);
    }




}
