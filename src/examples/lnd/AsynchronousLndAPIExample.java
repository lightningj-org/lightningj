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
import org.lightningj.lnd.wrapper.AsynchronousLndAPI;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.message.Invoice;
import org.lightningj.lnd.wrapper.message.WalletBalanceResponse;

import java.io.File;


/**
 * Example of using the AsynchronousLndAPI for non-blocking calls.
 *
 * Created by Philip Vendil
 */
public class AsynchronousLndAPIExample {

    public static void main(String[] args) throws Exception{

        // Create  API, using the most simple constructor. There are alternatives
        // where it is possible to specify custom SSLContext or just a managed channel.
        // See SynchronousLndAPIExample for details.
        AsynchronousLndAPI asynchronousLndAPI = new AsynchronousLndAPI("localhost",10001,new File(System.getProperty("user.home") + "/Library/Application Support/Lnd/tls.cert"), null);

        try {
            // Example of a simple asynchronous call.
            System.out.println("Sending WalletBalance request...");
            asynchronousLndAPI.walletBalance(new StreamObserver<WalletBalanceResponse>() {

                // Each response is sent in a onNext call.
                @Override
                public void onNext(WalletBalanceResponse value) {
                    System.out.println("Received WalletBalance response: " + value.toJsonAsString(true));
                }

                // Errors during the stream is showed here.
                @Override
                public void onError(Throwable t) {
                    System.err.println("Error occurred during WalletBalance call: " + t.getMessage());
                    t.printStackTrace(System.err);
                }

                // When the stream have finished is onCompleted called.
                @Override
                public void onCompleted() {
                    System.out.println("WalletBalance call closed.");
                }
            });

            Thread.sleep(1000);

            // Call to subscribe for invoices.
            // To recieve invoices you can use the lncli to send payment of an invoice to your LND node.
            // and it will show up here.
            System.out.println("Subscribing to invoices call...");
            asynchronousLndAPI.subscribeInvoices(null,null,new StreamObserver<Invoice>() {
                @Override
                public void onNext(Invoice value) {
                    System.out.println("Received Invoice: " + value.toJsonAsString(true));
                }

                @Override
                public void onError(Throwable t) {
                    System.err.println("Error occurred during subscribeInvoices call: " + t.getMessage());
                    t.printStackTrace(System.err);
                }

                @Override
                public void onCompleted() {
                    System.out.println("subscribeInvoices call closed.");
                }
            });

            System.out.println("Press Ctrl-C to stop listening for invoices");
            while (true) {
                Thread.sleep(1000);
            }


        }finally {
            // To close the api use the method
            asynchronousLndAPI.close();
        }

    }
}
