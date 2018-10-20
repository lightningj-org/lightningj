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
 * Exception indicating an error occurred when communicating
 * with server. This could indicate timeout or dropped package
 * and request can be retried.
 *
 * Created by Philip Vendil.
 */
public class CommunicationException extends StatusException {

    /**
     * Exception indicating an error occurred when communicating
     * with server. This could indicate timeout or dropped package
     * and request can be retried.
     *
     * @param message detail message of the exception.
     * @param status The underlying GRPC status code, might be null.
     */
    public CommunicationException(String message, Status status) {
        super(message, status);
    }

    /**
     * Exception indicating an error occurred when communicating
     * with server. This could indicate timeout or dropped package
     * and request can be retried.
     *
     * @param message detail message of the exception.
     * @param status The underlying GRPC status code, might be null.
     * @param cause the underlying exception.
     */
    public CommunicationException(String message, Status status, Throwable cause) {
        super(message, status, cause);
    }

}
