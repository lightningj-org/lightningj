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
 * Unit tests for ValidationResult.
 *
 * Created by Philip Vendil.
 */
class ValidationResultSpec extends Specification {

    def "Verify constructor and getters handles fields properly"(){
        when:
        ValidationResult vr = new ValidationResult("Invoice")
        then:
        vr.getMessageType() == "Invoice"
        vr.getMessageErrors().size() == 0
        vr.getSubMessageResults().size() == 0
    }

    def "Verify that isValid return true if no validation errors were found otherwise false."(){
        when:
        ValidationResult vr = new ValidationResult("Invoice")
        then:
        vr.isValid()

        when:
        vr.messageErrors.add(new ValidationProblems("Invoice","receipt","someDescriptionResourceKey","Some Error1"))
        then:
        !vr.isValid()

        when:
        vr = new ValidationResult("Invoice")
        vr.subMessageResults.add(new ValidationResult("SubInvoice"))
        vr.subMessageResults[0].messageErrors.add(new ValidationProblems("SubInvoice","someField","someDescriptionResourceKey","Some Error2"))
        then:
        !vr.isValid()
    }

    def "Verify that getAggregatedValidationErrors returns a complete list of validation errors"(){
        setup:
        ValidationResult vr = createValidationResult()

        when:
        List<ValidationProblems> ve = vr.getAggregatedValidationErrors()

        then:
        ve.size() == 4
        ve[0].messageType == "Invoice"
        ve[0].field == "receipt"
        ve[1].messageType == "Invoice"
        ve[1].field == "value"
        ve[2].messageType == "SubInvoice"
        ve[2].field == "someField1"
        ve[3].messageType == "SubInvoice"
        ve[3].field == "someField2"

    }

    def "Verify that toString returns a valid json representation of the validation errors"(){
        setup:
        ValidationResult vr = createValidationResult()
        expect:
        vr.toString() == """
[
    {
        "receipt": "Some Error1"
    },
    {
        "value": "Some Error2"
    },
    {
        "SubInvoice": [
            {
                "someField1": "Some Error3"
            }
        ]
    },
    {
        "SubInvoice": [
            {
                "someField2": "Some Error4"
            }
        ]
    }
]"""
    }

    private ValidationResult createValidationResult(){
        ValidationResult vr = new ValidationResult("Invoice")
        vr.messageErrors.add(new ValidationProblems("Invoice","receipt","someDescriptionResourceKey","Some Error1"))
        vr.messageErrors.add(new ValidationProblems("Invoice","value","someDescriptionResourceKey","Some Error2"))
        vr.subMessageResults.add(new ValidationResult("SubInvoice"))
        vr.subMessageResults.add(new ValidationResult("SubInvoice"))
        vr.subMessageResults[0].messageErrors.add(new ValidationProblems("SubInvoice","someField1","someDescriptionResourceKey","Some Error3"))
        vr.subMessageResults[1].messageErrors.add(new ValidationProblems("SubInvoice","someField2","someDescriptionResourceKey","Some Error4"))
        return vr
    }
}
