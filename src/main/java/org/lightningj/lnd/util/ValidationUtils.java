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
package org.lightningj.lnd.util;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLiteOrBuilder;
import org.lightningj.lnd.wrapper.ValidationProblems;
import org.lightningj.lnd.wrapper.ValidationResult;

import java.util.List;

/**
 * Helper class with methods to perform validation of Message objects.
 *
 * Created by Philip Vendil.
 */
public class ValidationUtils {


    /**
     * Method to validate a message and generate a validation result.
     * <p>
     * <b>This method hasn't been tested properly since there currently are no validation requirements
     * in the proto specification.</b>
     * </p>
     * @param messageOrBuilder the message or builder to validate.
     * @param messageDescriptor the related message descriptor.
     * @return a ValidationResult containing all found validation errors.
     */
    public static ValidationResult validateMessage(MessageLiteOrBuilder messageOrBuilder, Descriptors.Descriptor messageDescriptor){
        ValidationResult retval = new ValidationResult(messageDescriptor.getName());
        for(Descriptors.FieldDescriptor fieldDescriptor : messageDescriptor.getFields()){
            if(fieldDescriptor.isRequired()){
                Object field=null;
                if(messageOrBuilder instanceof Message.Builder){
                    field = ((Message.Builder) messageOrBuilder).getField(fieldDescriptor);
                }
                if(messageOrBuilder instanceof Message){
                    field = ((Message) messageOrBuilder).getField(fieldDescriptor);
                }
               if(field == null){
                   ValidationProblems ve = new ValidationProblems(messageDescriptor.getName(),fieldDescriptor.getName(),"lightningj.validation.fieldisrequired", new Object[]{fieldDescriptor.getName()},"Field " + fieldDescriptor.getName() + " is required.");
                   retval.getMessageErrors().add(ve);
               }else {
                   if (fieldDescriptor.isRepeated() && ((List) field).size() == 0){
                       ValidationProblems ve = new ValidationProblems(messageDescriptor.getName(),fieldDescriptor.getName(),"lightningj.validation.fieldisrequired", new Object[]{fieldDescriptor.getName()},"Field " + fieldDescriptor.getName() + " is required.");
                       retval.getMessageErrors().add(ve);
                   }
               }

               if(fieldDescriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE){
                   ValidationResult subMessageResult = validateMessage((Message) field, fieldDescriptor.getMessageType());
                   if(!subMessageResult.isValid()){
                       retval.getSubMessageResults().add(subMessageResult);
                   }
               }
            }
        }
        return retval;
    }



}
