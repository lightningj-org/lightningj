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
package org.lightningj.lnd.wrapper

import io.grpc.stub.StreamObserver
import org.lightningj.lnd.wrapper.message.AddInvoiceResponse
import org.lightningj.lnd.wrapper.message.ChannelGraph
import org.lightningj.lnd.wrapper.message.Invoice
import spock.lang.Shared
import spock.lang.Specification

/**
 * Integration tests running the APIs against a live test-net LND node.
 */
class LNDIntegrationSpec extends Specification{

    @Shared String lndHost
    @Shared int lndPort
    @Shared File tlsCertPath
    @Shared File macaroonPath

    SynchronousLndAPI synchronousLndAPI
    AsynchronousLndAPI asynchronousLndAPI

    def setupSpec(){
        lndHost = System.getProperty("lightningj.integration.test.lnd.host")
        lndPort = Integer.parseInt(System.getProperty("lightningj.integration.test.lnd.port"))
        tlsCertPath = new File(System.getProperty("lightningj.integration.test.lnd.tlscertpath"))
        macaroonPath = new File(System.getProperty("lightningj.integration.test.lnd.macaroonpath"))
    }

    def setup(){
        asynchronousLndAPI = new AsynchronousLndAPI(lndHost,lndPort,tlsCertPath, macaroonPath)
        synchronousLndAPI = new SynchronousLndAPI(lndHost,lndPort,tlsCertPath, macaroonPath)
    }

    def "Test to register an invoice synchronously and wait for it asynchronously"(){
        setup:
        Invoice recievedInvoice = null
        asynchronousLndAPI.subscribeInvoices(null,null,new StreamObserver<Invoice>() {
            @Override
            void onNext(Invoice value) {
                recievedInvoice = value
            }

            @Override
            void onError(Throwable t) {
                assert false
            }

            @Override
            void onCompleted() {
                 assert false
            }
        })
        when:
        Invoice invoice = new Invoice()
        invoice.value = 10
        invoice.memo = "Some description"
        AddInvoiceResponse response = synchronousLndAPI.addInvoice(invoice)
        then:
        response.paymentRequest != null
        when:
        int times = 0
        while(recievedInvoice == null && times < 20){
            Thread.sleep(500)
        }
        then:
        recievedInvoice.paymentRequest != null
    }

    def "Verify describeGraph fetches the entire graph"(){
        when:
        ChannelGraph channelGraph = synchronousLndAPI.describeGraph(true)
        then:
        channelGraph != null
    }

}
