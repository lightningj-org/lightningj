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
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * StreamObserverWrapper is converts received api object to wrapped objects.
 * <p>
 * Implements the StreamObserver interface containing three methods for
 * receiving messages asynchronously:
 * <li>onNext : the next object in stream</li>
 * <li>onError : if exception occurred recieving the stream</li>
 * <li>onCompleted : called when no more messages is expected on the response stream.</li>
 * </p>
 *
 * Created by Philip Vendil.
 */
public class StreamObserverWrapper<T> implements StreamObserver<T> {

    protected static Logger log =
            Logger.getLogger(API.class.getName());

    protected StreamObserver wrappedObserver;
    protected boolean performValidation;
    protected String messageType;
    protected WrapperFactory wrapperFactory = WrapperFactory.getInstance();
    protected StatusExceptionWrapper exceptionWrapper = StatusExceptionWrapper.getInstance();

    public StreamObserverWrapper(StreamObserver<?> wrappedObserver,
                                 boolean performValidation,
                                 String messageType){
      assert wrappedObserver != null;
      this.wrappedObserver=wrappedObserver;
      this.performValidation=performValidation;
      this.messageType = messageType;
    }
    /**
     * Receives a value from the stream.
     * <p>
     * <p>Can be called many times but is never called after {@link #onError(Throwable)} or {@link
     * #onCompleted()} are called.
     * <p>
     * <p>Unary calls must invoke onNext at most once.  Clients may invoke onNext at most once for
     * server streaming calls, but may receive many onNext callbacks.  Servers may invoke onNext at
     * most once for client streaming calls, but may receive many onNext callbacks.
     * <p>
     * <p>If an exception is thrown by an implementation the caller is expected to terminate the
     * stream by calling {@link #onError(Throwable)} with the caught exception prior to
     * propagating it.
     *
     * @param value the value passed to the stream
     */
    @Override
    public void onNext(Object value) {
        assert value instanceof GeneratedMessageV3;
        try {
            Message message = wrapperFactory.wrap((GeneratedMessageV3) value);
            log.fine("Received streamed message: " + message.toString());
            if(!performValidation){
                wrappedObserver.onNext(message);
            }else {
                ValidationResult validationResult = message.validate();
                if (validationResult.isValid()) {
                    wrappedObserver.onNext(message);
                } else {
                    onError(new ValidationException("Streamed message " + message.getMessageName() + " wasn't valid.", validationResult));
                }
            }
        }catch(Exception e){
            onError(e);
        }
    }

    /**
     * Receives a terminating error from the stream.
     * <p>
     * <p>May only be called once and if called it must be the last method called. In particular if an
     * exception is thrown by an implementation of {@code onError} no further calls to any method are
     * allowed.
     * <p>
     * <p>{@code t} should be a {@link StatusException} or {@link
     * StatusRuntimeException}, but other {@code Throwable} types are possible. Callers should
     * generally convert from a {@link Status} via {@link Status#asException()} or
     * {@link Status#asRuntimeException()}. Implementations should generally convert to a
     * {@code Status} via {@link Status#fromThrowable(Throwable)}.
     *
     * @param t the error occurred on the stream
     */
    @Override
    public void onError(Throwable t) {
        if(t instanceof StatusException || t instanceof StatusRuntimeException){
           t = exceptionWrapper.wrap((Exception) t);
        }
        log.log(Level.FINE,"Error processing streamed message of type " + messageType + ": " + t.getMessage(),t);
        wrappedObserver.onError(t);


    }

    /**
     * Receives a notification of successful stream completion.
     * <p>
     * <p>May only be called once and if called it must be the last method called. In particular if an
     * exception is thrown by an implementation of {@code onCompleted} no further calls to any method
     * are allowed.
     */
    @Override
    public void onCompleted() {
        log.fine("Stream of " + messageType + " messages is complete.");
        wrappedObserver.onCompleted();
    }
}
