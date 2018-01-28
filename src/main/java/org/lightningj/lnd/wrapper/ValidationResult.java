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

import org.lightningj.lnd.util.JsonGenUtils;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Message.validate() containing a result of the validation.
 * <p>
 *     Contains method isValid() that returns true if no validation problems found. Otherwise
 *     a complete list of validation problems can be retrieved with getAggregatedValidationErrors().
 * </p>
 *
 * Created by Philip Vendil.
 */
public class ValidationResult {

    String messageType;
    List<ValidationProblems> messageErrors = new ArrayList<>();
    List<ValidationResult> subMessageResults = new ArrayList<>();

    /**
     * Default constructor.
     *
     * @param messageType the name of the related message.
     */
    public ValidationResult(String messageType){
        this.messageType = messageType;
    }

    /**
     *
     * @return true of no validation errors found in this message or any sub-messages.
     */
    public boolean isValid(){
        return messageErrors.size() == 0 && subMessageResults.size() == 0;
    }

    /**
     *
     * @return Method that returns a list of all found validation errors, in this
     * message and all sub-messages.
     */
    public List<ValidationProblems> getAggregatedValidationErrors(){
        List<ValidationProblems> retval = new ArrayList<>();
        retval.addAll(messageErrors);
        for(ValidationResult subMessageResult: subMessageResults){
            retval.addAll(subMessageResult.getAggregatedValidationErrors());
        }
        return retval;
    }

    /**
     *
     * @return the type of message that contained the validation problem.
     */
    public String getMessageType() {
        return messageType;
    }

    /**
     *
     * @return this message validation errors. Sub-Message errors is
     * not included.
     */
    public List<ValidationProblems> getMessageErrors(){
        return this.messageErrors;
    }

    /**
     *
     * @return a list of validation results of the sub-messages to this message.
     */
    public List<ValidationResult> getSubMessageResults() {
        return subMessageResults;
    }

    /**
     * Help method to convert validation result data into a json object.
     * @return json object representation of the validation result.
     */
    protected JsonArrayBuilder toJson(){
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();


        for(ValidationProblems validationProblems : messageErrors){
            JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
            jsonObjectBuilder.add(validationProblems.getField(), validationProblems.getDescription());
            jsonArrayBuilder.add(jsonObjectBuilder);
        }
        if(subMessageResults.size() > 0) {

            for (ValidationResult subMessageResult : subMessageResults) {
                JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
                jsonObjectBuilder.add(subMessageResult.getMessageType(), subMessageResult.toJson());
                jsonArrayBuilder.add(jsonObjectBuilder);
            }
        }

        return jsonArrayBuilder;
    }

    /**
     *
     * @return the validation result in json string format.
     */
    @Override
    public String toString(){
        return JsonGenUtils.jsonToString(toJson(),true);
    }

}
