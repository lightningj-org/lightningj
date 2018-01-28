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
 * Exception indicating an error occurred on client side which
 * indicate there is some problem on the client side such
 * as invalid request data.
 *
 * Created by Philip Vendil.
 */
public class ClientSideException extends StatusException {

    /**
     * Exception indicating an error occurred on client side which
     * indicate there is some problem on the client side such
     * as invalid request data.
     *
     * @param message detail message of the exception.
     * @param status The underlying GRPC status code, might be null.
     */
    public ClientSideException(String message, Status status) {
        super(message, status);
    }

    /**
     * Exception indicating an error occurred on client side which
     * indicate there is some problem on the client side such
     * as invalid request data.
     *
     * @param message detail message of the exception.
     * @param status The underlying GRPC status code, might be null.
     * @param cause the underlying exception.
     */
    public ClientSideException(String message, Status status, Throwable cause) {
        super(message, status, cause);
    }

}
