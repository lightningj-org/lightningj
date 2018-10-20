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
 * Interface for MacaroonContext used to manage which Macaroon that should be used
 * for API calls.
 *
 * @see org.lightningj.lnd.wrapper.StaticFileMacaroonContext
 *
 * Created by Philip Vendil on 2018-02-04.
 */
public interface MacaroonContext {

    /**
     * Method that should return the macaroon in serialized (hex encoded form) that should be used in header
     * for calls towards server node.
     *
     * @return the current macaroon or null of no valid macaroon is available.
     */
    String getCurrentMacaroonAsHex();
}
