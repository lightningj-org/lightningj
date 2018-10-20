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
     * @return the resource location of the related schema.
     */
    @Override
    protected String getSchemaLocation() {
        return "/lnd_v1.xsd";
    }

    /**
     * @return the JAXB class path used for JAXBContext separated with ':'
     */
    @Override
    protected String getJAXBClassPath() {
        return "org.lightningj.lnd.wrapper.message";
    }
}
