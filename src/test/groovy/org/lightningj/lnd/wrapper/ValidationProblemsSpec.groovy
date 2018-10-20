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

import java.text.MessageFormat

/**
 * Unit tests for ValidationProblems.
 *
 * Created by Philip Vendil.
 */
class ValidationProblemsSpec extends Specification {

    def "Verify that constructor and getters retrives correct valuds"(){
        when:
        ValidationProblems ve = new ValidationProblems("Invoice", "receipt","some.recource.key", "Some description")
        then:
        ve.getMessageType() == "Invoice"
        ve.getField() == "receipt"
        ve.getDescription() == "Some description"
        ve.getDescriptionResourceKey() == "some.recource.key"
    }

    def "Verify that resource bundle loading works properly"(){
        setup:
        ValidationProblems ve = new ValidationProblems("Invoice", "receipt","some.recource.key", ["someField"] as Object[], "Some description")
        ResourceBundle br = ResourceBundle.getBundle("lightningj_messages")
        ResourceBundle br_sv = ResourceBundle.getBundle("lightningj_messages",  new Locale("sv", "SE"))

        when: "Verify that one with parameters is working for default locale"
        String text = MessageFormat.format(br.getString("lightningj.validation.fieldisrequired"),ve.getResourceParameters())
        then:
        text == "Field someField is required."

        when: "Verify that one with parameters is working for swedish locale"
        text = MessageFormat.format(br_sv.getString("lightningj.validation.fieldisrequired"),ve.getResourceParameters())
        then:
        text == "Fält someField är obligatoriskt."

        when: "Verify that null resourceParameters doesn't throw exception"
        ve = new ValidationProblems("Invoice", "receipt","some.recource.key", "Some description")
        text = MessageFormat.format(br.getString("lightningj.validation.fieldisrequired"),ve.getResourceParameters())
        then:
        text == "Field {0} is required."
    }
}
