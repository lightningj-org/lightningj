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

import com.github.nitram509.jmacaroons.Macaroon;
import com.github.nitram509.jmacaroons.MacaroonsBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * MacaroonContext that reads a static file during construction
 * and returns the macaroon content for each calls.
 *
 * @see org.lightningj.lnd.wrapper.MacaroonContext
 *
 * Created by Philip Vendil on 2018-02-04.
 */
public class StaticFileMacaroonContext implements MacaroonContext{

    Macaroon currentMacaroon;
    /**
     * Constructor to read a specified macaroon path.
     *
     * @param macaroonPath the path to the macaroon to be used.
     * @throws ClientSideException if problem occurs during reading or parsing of macaroon data. status is null.
     */
    public StaticFileMacaroonContext(File macaroonPath) throws ClientSideException{
        try {
            FileInputStream fis = new FileInputStream(macaroonPath);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            fis.close();

            currentMacaroon = MacaroonsBuilder.deserialize(com.github.nitram509.jmacaroons.util.Base64.encodeUrlSafeToString(data));
        }catch(Exception e){
            throw new ClientSideException("Error reading macaroon from path '" + macaroonPath + "', message: " + e.getMessage(),null,e);
        }
    }

    /**
     * Method that should return the macaroon that should be used in header for calls towards server node.
     *
     * @return the current macaroon or null of no valid macaroon is available.
     */
    @Override
    public Macaroon getCurrentMacaroon() {
        return currentMacaroon;
    }
}
