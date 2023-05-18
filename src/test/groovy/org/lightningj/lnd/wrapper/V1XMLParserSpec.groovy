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

    import spock.lang.Specification

    /**
     * Unit tests for V1XMLParser.
     *
     * Created by Philip Vendil.
     */
    class V1XMLParserSpec extends Specification {

        def "Verify that abstract method returns correct values"(){
            setup:
            V1XMLParser p = new V1XMLParser()
            expect:
            p.getVersion() == "1.0"
            p.getSchemaLocations() == [ "/lnd_v1.xsd",
                                        "/autopilot_v1.xsd",
                                        "/chainnotifier_v1.xsd",
                                        "/chainkit_v1.xsd",
                                        "/invoices_v1.xsd",
                                        "/router_v1.xsd",
                                        "/signer_v1.xsd",
                                        "/walletkit_v1.xsd",
                                        "/watchtower_v1.xsd",
                                        "/wtclient_v1.xsd",
                                        "/verrpc_v1.xsd",
                                        "/walletunlocker_v1.xsd",
                                        "/stateservice_v1.xsd",
                                        "/dev_v1.xsd",
                                        "/neutrino_v1.xsd",
                                        "/peers_v1.xsd"] as String[]
            p.getJAXBClassPath() == "org.lightningj.lnd.wrapper.message:" +
                    "org.lightningj.lnd.wrapper.autopilot.message:"+
                    "org.lightningj.lnd.wrapper.chainnotifier.message:"+
                    "org.lightningj.lnd.wrapper.chainkit.message:"+
                    "org.lightningj.lnd.wrapper.invoices.message:"+
                    "org.lightningj.lnd.wrapper.router.message:"+
                    "org.lightningj.lnd.wrapper.signer.message:"+
                    "org.lightningj.lnd.wrapper.walletkit.message:"+
                    "org.lightningj.lnd.wrapper.watchtower.message:" +
                    "org.lightningj.lnd.wrapper.wtclient.message:" +
                    "org.lightningj.lnd.wrapper.verrpc.message:" +
                    "org.lightningj.lnd.wrapper.walletunlocker.message:" +
                    "org.lightningj.lnd.wrapper.stateservice.message:" +
                    "org.lightningj.lnd.wrapper.dev.message:" +
                    "org.lightningj.lnd.wrapper.neutrino.message:" +
                    "org.lightningj.lnd.wrapper.peers.message"
        }
    }
