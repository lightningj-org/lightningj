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

/**
 * Exception indicating an error occurred on server side which
 * indicate there is some problem on the server side that might
 * persist for some time.
 *
 * Created by Philip Vendil.
 */
public class ServerSideException extends StatusException {

    /**
     * Exception indicating an error occurred on server side which
     * indicate there is some problem on the server side that might
     * persist for some time.
     *
     * @param message detail message of the exception.
     * @param status The underlying GRPC status code, might be null.
     */
    public ServerSideException(String message, Status status) {
        super(message, status);
    }

    /**
     * Exception indicating an error occurred on server side which
     * indicate there is some problem on the server side that might
     * persist for some time.
     *
     * @param message detail message of the exception.
     * @param status The underlying GRPC status code, might be null.
     * @param cause the underlying exception.
     */
    public ServerSideException(String message, Status status, Throwable cause) {
        super(message, status, cause);
    }
}
