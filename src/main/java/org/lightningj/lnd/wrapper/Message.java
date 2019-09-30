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

import com.google.protobuf.GeneratedMessageV3.Builder;
import org.lightningj.lnd.util.JsonGenUtils;
import org.lightningj.lnd.util.ValidationUtils;

import javax.json.JsonException;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Base Wrapper Message class containing the base functionality for
 * all LND Messages.
 * <p>
 *     Contains methods for:
 *     <li>Convert to underlying API Object</li>
 *     <li>Convert to and from Json</li>
 *     <li>Validation</li>
 * </p>
 *
 * Created by Philip Vendil.
 */
@XmlTransient
public abstract class Message<T> {

    protected WrapperFactory wrapperFactory = WrapperFactory.getInstance();

    protected Builder builder;

    /**
     * Base constructor used when reading from JSON.
     * @param jsonReader the json object to parse and set all related fields.
     * @param builder the API Object builder set by inheriting message object.
     * @throws JsonException if problems occurred parsing the JSON data.
     */
    public Message(JsonReader jsonReader, Builder builder) throws JsonException {
        this.builder = builder;
        try {
            JsonGenUtils.jsonToMessage(jsonReader.readObject(), builder, builder.getDescriptorForType());
        }catch(Exception e){
            throw new JsonException("Error converting JSON to Message" + (e.getMessage() != null ? ": " + e.getMessage() : ""),e);
        }
    }

    /**
     * Base constructor for creating a new message.
     *
     * @param builder the API Object builder set by inheriting message object.
     */
    public Message(Builder builder){
        this.builder = builder;
    }


    /**
     * Method to convert the Message into JSON Representation
     * @return JsonObjectBuilder version of the message.
     * @throws JsonException if problems occurred converting to json.
     */
    public JsonObjectBuilder toJson() throws JsonException{
        try {
            populateRepeatedFields();
            return JsonGenUtils.messageToJson(builder, builder.getDescriptorForType());
        }catch(Exception e){
            throw new JsonException("Error converting Message to JSON" + (e.getMessage() != null ? ": " + e.getMessage() : ""),e);
        }
    }


    /**
     * Method to convert Message into a json string.
     * @param prettyPrint if json data should be pretty printed with newlines and indentation.
     * @return string json data representation of message.
     */
    public String toJsonAsString(boolean prettyPrint){
        return JsonGenUtils.jsonToString(toJson(),prettyPrint);
    }


    /**
     * Method to return the underlying API object in GRPC format.
     * @return the underlying API object in GRPC format.
     */
    public T getApiObject(){
        populateRepeatedFields();
        return (T)  builder.build();
    }

    /**
     * Method to be overrided by inherited messages that contains repeatable or mapped fields.
     */
    protected void populateRepeatedFields(){}

    /**
     *
     * @return the name of the underling message.
     */
    public String getMessageName() {
        return builder.getDescriptorForType().getName();
    }

    /**
     * Returns a string representation i JSON format of all data in this message.
     * @return returns a string representation i JSON format of all data in this message.
     */
    public String toString(){
        return getMessageName() + ": " + toJsonAsString(true);
    }


    /**
     * Method to check if another object equals this object.
     * <p>
     *     Compares the underlying API object data so can be relative resource intensive.
     * </p>
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message<?> that = (Message<?>) o;
        return getApiObject().equals(that.getApiObject());
    }

    /**
     *
     * @return the underlying API Object hash code, can be relative resource intensive to calculate.
     */
    @Override
    public int hashCode() {
        return getApiObject().hashCode();
    }

    /**
     * Method to validate the data in the message against proto specification and
     * returns a ValidationReport containing all validation errors found.
     *
     * @return a ValidationReport containing all validation error found in message.
     */
    public ValidationResult validate()  {
        return ValidationUtils.validateMessage(builder,builder.getDescriptorForType());
    }
}
