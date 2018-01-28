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
package lnd;

import org.lightningj.lnd.wrapper.*;
import org.lightningj.lnd.wrapper.message.OpenChannelRequest;

import java.util.List;

import static lnd.ExampleUtils.*;

/**
 * Example of validation API.
 */
public class ValidationExample {

    public static void main(String[] args) throws Exception{

        // Validation is features to validate that in and outgoing messages fulfills
        // requirements in defined Proto Specification.

        // Get API
        SynchronousLndAPI synchronousLndAPI = getSynchronousLndAPI();

        // To manually validate a wrapped Message it is possible to call the validate() method.
        OpenChannelRequest openChannelRequest = genOpenChannelRequest();
        // To validate call validate() and it will return ValidationResult
        ValidationResult validationResult = openChannelRequest.validate();
        // The ValidationResult.isValid() returns true if the message was valud.
        validationResult.isValid();
        // If there is problems it is possible to retrieve the problems found either
        // in a single aggregated list for all sub-messages.
        List<ValidationProblems> allProblems= validationResult.getAggregatedValidationErrors();
        // Or as a tree structure with all problems in this message in:
        validationResult.getMessageErrors();
        // and all sub messages as their own report.
        validationResult.getSubMessageResults();


        try{
            // Each call might throw a ValidationException
            synchronousLndAPI.channelBalance();
        }catch(ValidationException ve){
            // A ValidationException has the faulty messages ValidationReport as a field.
            ValidationResult vr = ve.getValidationResult();
        }catch(StatusException se){
            //...
        }


    }
}
