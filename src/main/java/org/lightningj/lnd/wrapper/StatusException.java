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

import io.grpc.Status;

/**
 * Base exception that is wrapped around GRPC status.
 *
 * Created by Philip Vendil.
 */
public abstract class StatusException extends Exception{

    private Status status;

    public StatusException(String message, Status status){
        super(message);
        this.status = status;
    }

    public StatusException(String message, Status status, Throwable cause){
        super(message, cause);
        this.status=status;
    }

    /**
     * @return the underlying GRPC status code, might be null
     * if no related status code could be found.
     */
    public Status getStatus(){
        return status;
    }

}
