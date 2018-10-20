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
 * Unit tests of ValidationException.
 *
 * Created by Philip Vendil.
 */
class ValidationExceptionSpec extends Specification {

    def "Verify that constructor populated fields properly"(){
        setup:
        ValidationResult validationResult = new ValidationResult()
        when:
        ValidationException e = new ValidationException("SomeMessage", validationResult)
        then:
        e.message == "SomeMessage"
        e.getValidationResult() == validationResult
        when:
        e = new ValidationException("SomeMessage", validationResult, new IOException())
        then:
        e.message == "SomeMessage"
        e.getValidationResult() == validationResult
        e.cause instanceof IOException
    }
}
