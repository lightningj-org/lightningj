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
package org.lightningj.lnd.util

import com.google.protobuf.Descriptors
import org.lightningj.lnd.proto.LightningApi
import spock.lang.Specification

/**
 * Unit tests for ValidationUtils.
 *
 * Created by Philip Vendil.
 */
class ValidationUtilsSpec extends Specification {

    def "Verify that no fields in LightningApi is required, and propert testing cannot be performed"(){

        when:
        boolean foundRequired = false
        LightningApi.getDescriptor().messageTypes.each{
            it.fields.each{ Descriptors.FieldDescriptor fieldDescriptor ->
                if(fieldDescriptor.required){
                    foundRequired = true
                }

            }
        }
        then:
        !foundRequired
    }
}
