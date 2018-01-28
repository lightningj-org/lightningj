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

import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for Wrapping API implementations.
 *
 * Created by Philip Vendil.
 */
abstract public class API {

    protected static Logger log =
            Logger.getLogger(API.class.getName());

    protected WrapperFactory wrapperFactory = WrapperFactory.getInstance();
    protected StatusExceptionWrapper statusExceptionWrapper = StatusExceptionWrapper.getInstance();

    protected boolean performValidation=true;
    protected ManagedChannel channel;


    /**
     * Minimal constructor for setting up a connection with LND Application.
     *
     * @param host the hostname of ldn application
     * @param port the port of the application.
     * @param trustedServerCertificate a link of the SSL certificate used by the LND Application.
     * @throws SSLException if problems occurred setting up the SSL Connection.
     */
    protected API(String host, int port, File trustedServerCertificate) throws SSLException {
        this(host,port, GrpcSslContexts.configure(SslContextBuilder.forClient(), SslProvider.OPENSSL)
                .trustManager(trustedServerCertificate)
                .build());
    }

    /**
     * Constructor for setting up a connection with LND Application with more flexible
     * SSL context parameters.
     *
     * @param host the hostname of ldn application
     * @param port the port of the application.
     * @param sslContext the SSL Context used when connecting the LND Application.
     */
    protected API(String host, int port, SslContext sslContext){
        this(NettyChannelBuilder.forAddress(host, port)
                .sslContext(sslContext)
                .build());
    }

    /**
     * Constructor used for setting up a connection using a GRPC managed channel that
     * can be customized.
     *
     * @param channel the managed channel to use.
     */
    protected API(ManagedChannel channel){
        this.channel = channel;
    }

    /**
     * Method to validate all fields in related message and throws a validation exception with
     * a validation report with all found validation problems.
     *
     * @param message the message to validate.
     * @throws ValidationException exception containing a validation report with all validation
     * problems found.
     */
    protected void validate(Message message) throws ValidationException{
        if(performValidation){
            ValidationResult validationResult = message.validate();
            if(!validationResult.isValid()){
                throw new ValidationException("Validation problems in message " + validationResult.getMessageType(),validationResult);
            }
        }
    }

    /**
     * Method to close underlying channel and free resources.
     *
     * @throws StatusException if problems occurred in underlying GRPC call. Can be of one of three sub exceptions
     * <li>ClientSideException: if problems was found in the request data, such as invalid or unexpected data.
     * <li>ServerSideException: if server side problems was detected when processing the request.
     * <li>CommunicationException: if communication related problems occurred during the call.
     */
    public void close() throws StatusException {
        String name = this.getClass().getSimpleName();
        log.fine("Closing " + name + " Channel...");
        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            log.fine(name +" Channel Closed.");
        }catch(io.grpc.StatusRuntimeException e){
            log.severe("Error occurred closing " + name + " Channel: " + e.getMessage() + ".");
            log.log(Level.FINE, "Stacktrace: ",e);
            throw statusExceptionWrapper.wrap(e);
        } catch (InterruptedException e) {
            log.log(Level.FINE, "Thread interupted: " + e.getMessage(),e);
        }
    }

    /**
     * Method to convert and validate (if validation is enabled) a request message to and underlying apiObject sent
     * to LDN Server.
     *
     * @param requestMessage the request message to convert
     * @return a converted GRPC message.
     * @throws ValidationException exception containing a validation report with all validation
     * problems found, if validation is used.
     */
    protected Object processRequest(Message requestMessage) throws ValidationException{
        log.fine("Sending request message: " + requestMessage.toString());
        // TODO add tracetimes
        validate(requestMessage);
        return requestMessage.getApiObject();
    }

    /**
     *
     * @return returns true if send and received messages is validated.
     */
    public boolean isPerformValidation() {
        return performValidation;
    }

    /**
     *
     * @param performValidation set to true if messages should be validated before sent or received.
     */
    public void setPerformValidation(boolean performValidation) {
        this.performValidation = performValidation;
    }
}
