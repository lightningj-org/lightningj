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


import io.grpc.*;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

/**
 * Macaroon Client Call Interceptor used to inject current macaroon into each GRPC call.
 *
 * @see MacaroonContext
 * Created by Philip Vendil on 2018-02-03.
 */
public class MacaroonClientInterceptor implements io.grpc.ClientInterceptor {

    private static final Metadata.Key<String> MACAROON_METADATA_KEY = Metadata.Key.of("macaroon", ASCII_STRING_MARSHALLER);

    private MacaroonContext macaroonContext;


    public MacaroonClientInterceptor(MacaroonContext macaroonContext) {
        this.macaroonContext = macaroonContext;

    }

    /**
     * Intercept {@link ClientCall} creation by the {@code next} {@link Channel}.
     * <p>
     * <p>Many variations of interception are possible. Complex implementations may return a wrapper
     * around the result of {@code next.newCall()}, whereas a simpler implementation may just modify
     * the header metadata prior to returning the result of {@code next.newCall()}.
     * <p>
     * <p>{@code next.newCall()} <strong>must not</strong> be called under a different {@link Context}
     * other than the current {@code Context}. The outcome of such usage is undefined and may cause
     * memory leak due to unbounded chain of {@code Context}s.
     *
     * @param method      the remote method to be called.
     * @param callOptions the runtime options to be applied to this call.
     * @param next        the channel which is being intercepted.
     * @return the call object for the remote operation, never {@code null}.
     */
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                String macaroonData = null;
                if(macaroonContext != null){
                    macaroonData = macaroonContext.getCurrentMacaroonAsHex();
                }
                if(macaroonData != null) {
                    headers.put(MACAROON_METADATA_KEY, macaroonData);
                }
                super.start(responseListener, headers);
            }
        };
    }

}
