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

import java.net.URL;

/**
 * LND Version 1 implementation of XML Api
 * Created by Philip Vendil.
 */
public class V1XMLParser extends XMLParser {

    /**
     * @return return the version of the related Lnd API.
     */
    @Override
    protected String getVersion() {
        return "1.0";
    }

    /**
     * @return the resource location of the related schemas.
     */
    @Override
    protected String[] getSchemaLocations() {
        return new String[]{
                "/lnd_v1.xsd",
                "/autopilot_v1.xsd",
                "/chainnotifier_v1.xsd",
                "/invoices_v1.xsd",
                "/router_v1.xsd",
                "/signer_v1.xsd",
                "/walletkit_v1.xsd",
                "/watchtower_v1.xsd"
        };
    }

    /**
     * @return the JAXB class path used for JAXBContext separated with ':'
     */
    @Override
    protected String getJAXBClassPath() {
        return  "org.lightningj.lnd.wrapper.message:" +
                "org.lightningj.lnd.wrapper.autopilot.message:"+
                "org.lightningj.lnd.wrapper.chainnotifier.message:"+
                "org.lightningj.lnd.wrapper.invoices.message:"+
                "org.lightningj.lnd.wrapper.router.message:"+
                "org.lightningj.lnd.wrapper.signer.message:"+
                "org.lightningj.lnd.wrapper.walletkit.message:" +
                "org.lightningj.lnd.wrapper.watchtower.message";
    }
}
