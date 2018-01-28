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
import io.grpc.ManagedChannel;
import io.netty.handler.ssl.SslContext;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Base call for all Synchronous API implementations.
 *
 * Created by Philip Vendil.
 */
abstract public class SynchronousAPI extends API{

    /**
     * Minimal constructor for setting up a connection with LND Application.
     *
     * @param host                     the hostname of ldn application
     * @param port                     the port of the application.
     * @param trustedServerCertificate a link of the SSL certificate used by the LND Application.
     * @throws SSLException if problems occurred setting up the SSL Connection.
     */
    protected SynchronousAPI(String host, int port, File trustedServerCertificate) throws SSLException {
        super(host, port, trustedServerCertificate);
    }

    /**
     * Constructor for setting up a connection with LND Application with more flexible
     * SSL context parameters.
     *
     * @param host       the hostname of ldn application
     * @param port       the port of the application.
     * @param sslContext the SSL Context used when connecting the LND Application.
     */
    protected SynchronousAPI(String host, int port, SslContext sslContext) {
        super(host, port, sslContext);
    }

    /**
     * Constructor used for setting up a connection using a GRPC managed channel that
     * can be customized.
     *
     * @param channel the managed channel to use.
     */
    protected SynchronousAPI(ManagedChannel channel) {
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

    /**
     * Method to convert and validate (if validation is enabled) a iterable response from LDN Server to a wrapped object.
     *
     * @param responses the iterable collection of response message to convert
     * @return a wrapped List of response message
     * @throws ValidationException exception containing a validation report with all validation
     * problems found, if validation is used.
     */
    protected Iterator<?> processRepeatableResponse(Iterator<?> responses) throws ValidationException, ClientSideException {
        ArrayList<Message> retval = new ArrayList<>();
        while(responses.hasNext()){
            Message message = wrapperFactory.wrap((GeneratedMessageV3) responses.next());
            // TODO fix better logging
            log.fine("Received response message: " + message.toString());
            validate(message);
            retval.add(message);
        }
        return retval.iterator();
    }


}
