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
package org.lightningj

import javax.json.Json
import javax.json.JsonReader

/**
 * Class containing static test util methods.
 *
 * Created by Philip Vendil.
 */
class TestUtils {

    /**
     * Help method that creates a Json Reader with given input.
     * @param jsonData string representation of JSON data
     * @return a newly created JsonReader
     */
    static JsonReader jsonRead(String jsonData){
        return Json.createReader(new StringReader(jsonData))
    }
}
