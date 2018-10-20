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
package lnd;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.SslContext;
import org.lightningj.lnd.proto.LightningApi;
import org.lightningj.lnd.proto.LightningGrpc;


import java.io.File;


/**
 * Example on how to use the low level GRPC-Java API
 */
public class LowLevelAPIExample {

    public static void main(String[] args) throws Exception{

        File trustedServerCertificate = new File(System.getProperty("user.home") + "/Library/Application Support/Lnd/tls.cert");
        // Method to create SSL Context, trusting a specified LND node TLS certificate.
        // It is possible to customize the SSL setting by supplying a javax.net.ssl.SSLContext as well
        SslContext sslContext = GrpcSslContexts.configure(SslContextBuilder.forClient(), SslProvider.OPENSSL)
                .trustManager(trustedServerCertificate)
                .build();

        // Then create a managed communication channed
        ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", 10001)
                .sslContext(sslContext)
                .build();

        // Then create the low level API by calling.
        LightningGrpc.LightningBlockingStub stub = LightningGrpc.newBlockingStub(channel);
        // To create asynchronous API us LightningGrpc.newStub(channel)

        // Create a request object using messages in "org.lightningj.lnd.proto.LightningApi"
        LightningApi.WalletBalanceRequest.Builder walletBalanceRequest = LightningApi.WalletBalanceRequest.newBuilder();
        try{
            LightningApi.WalletBalanceResponse response = stub.walletBalance(walletBalanceRequest.build());
            System.out.println("Wallet Balance: " + response.getTotalBalance());
        }catch(StatusRuntimeException sre){
            // Handle exceptions a with status code in sre.getStatus()
        }
    }
}
