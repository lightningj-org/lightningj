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

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;

import javax.net.ssl.SSLException;
import java.io.File;

/**
 * Base call for all Asynchronous API implementations.
 *
 * Created by Philip Vendil.
 */
abstract public class AsynchronousAPI extends API{

    /**
     * Minimal constructor for setting up a connection with LND Application.
     *
     * @param host                     the hostname of ldn application
     * @param port                     the port of the application.
     * @param trustedServerCertificate a link of the SSL certificate used by the LND Application.
     * @param macaroonFile the file pointing to the macaroon to use, or null if no macaroons are used.
     * @throws SSLException if problems occurred setting up the SSL Connection.
     * @throws ClientSideException if problems occurred reading the macaroon file.
     */
    protected AsynchronousAPI(String host, int port, File trustedServerCertificate, File macaroonFile) throws SSLException, ClientSideException {
        super(host, port, trustedServerCertificate, macaroonFile);
    }

    /**
     * Constructor for setting up a connection with LND Application with more flexible
     * SSL context parameters.
     *
     * @param host       the hostname of ldn application
     * @param port       the port of the application.
     * @param sslContext the SSL Context used when connecting the LND Application.
     * @param macaroonContext the macaroon context to use.
     */
    protected AsynchronousAPI(String host, int port, SslContext sslContext, MacaroonContext macaroonContext) {
        super(host, port, sslContext, macaroonContext);
    }

    /**
     * Constructor used for setting up a connection using a GRPC managed channel that
     * can be customized.
     *
     * @param channel the managed channel to use.
     */
    protected AsynchronousAPI(ManagedChannel channel) {
        super(channel);
    }


    /**
     * Method to convert and validate (if validation is enabled) a response from LDN Server to a wrapped object.
     *
     * @param responseMessage the response message to convert
     * @return a wrapped response message
     * @throws ValidationException exception containing a validation report with all validation
     * problems found, if validation is used.
     */
    protected Message processResponse(Message responseMessage) throws ValidationException{
        log.fine("Received response message: " + responseMessage.toString());
        // TODO add tracetimes
        validate(responseMessage);
        return responseMessage;
    }




}
