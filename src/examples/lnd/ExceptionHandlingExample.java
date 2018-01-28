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
package lnd;

import io.grpc.stub.StreamObserver;
import org.lightningj.lnd.wrapper.*;
import org.lightningj.lnd.wrapper.message.ChannelBalanceResponse;

import java.rmi.ServerException;

import static lnd.ExampleUtils.*;

/**
 * Example of Exception Handling
 */
public class ExceptionHandlingExample {

    public static void main(String[] args) throws Exception{

        // Get API
        SynchronousLndAPI synchronousLndAPI = getSynchronousLndAPI();

        try{
            // Perform a call
            synchronousLndAPI.channelBalance();
        }catch(ValidationException ve){
            // Thrown if request or response contained invalid data
        }catch(StatusException se){
            // Thrown if GRPC related exception happened.
        }

        // Example of more fine grained exception handling.
        try{
            synchronousLndAPI.channelBalance();
        }catch(ValidationException ve){
            // Thrown if request or response contained invalid data
        }catch(ClientSideException cse){
            // Thrown if there is some problem on the client side such as invalid request data.
        }catch(ServerSideException sse){
            // Thrown if there is some problem on the server side that might persist for some time.
        }catch(CommunicationException ce){
            // Thrown if communication problems occurred such as  timeout or dropped package and request can be retried.
        }

        AsynchronousLndAPI asynchronousLndAPI = getAsynchronousLndAPI();

        asynchronousLndAPI.channelBalance(new StreamObserver<ChannelBalanceResponse>() {
            @Override
            public void onNext(ChannelBalanceResponse value) {
                // Handle ok resonses
            }

            @Override
            public void onError(Throwable t) {
                // Here is exceptions sent of same type as thrown by synchronous API.
            }

            @Override
            public void onCompleted() {
                // Call completed
            }
        });
    }
}
