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

/**
 * Class containing validation problem information about which field and description
 * of the problem.
 *
 * Created by Philip Vendil.
 */
public class ValidationProblems {

    private String messageType;
    private String field;
    private String descriptionResourceKey;
    private Object[] resourceParameters=null;
    private String description;

    /**
     * Main constructor of a new Validation Problem.
     *
     * @param messageType the type of message that contained the validation problem.
     * @param field the field in the message that contained the validation problem.
     * @param descriptionResourceKey the message resource key to get a translatable description.
     * @param description a description of the validation problem found.
     */
    public ValidationProblems(String messageType, String field, String descriptionResourceKey, String description) {
        this.messageType = messageType;
        this.field = field;
        this.descriptionResourceKey = descriptionResourceKey;
        this.description = description;
    }

    /**
     * Main constructor of a new Validation Problem.
     *
     * @param messageType the type of message that contained the validation problem.
     * @param field the field in the message that contained the validation problem.
     * @param descriptionResourceKey the message resource key to get a translatable description.
     * @param resourceParameters an array of resource parameters used for locale substitutions
     * in message bundles.
     * @param description a description of the validation problem found.
     */
    public ValidationProblems(String messageType, String field, String descriptionResourceKey, Object[] resourceParameters, String description) {
        this.messageType = messageType;
        this.field = field;
        this.descriptionResourceKey = descriptionResourceKey;
        this.resourceParameters = resourceParameters;
        this.description = description;
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
     * @return the field in the message that contained the validation problem.
     */
    public String getField() {
        return field;
    }

    /**
     *
     * @return the message resource key to get a translatable description.
     */
    public String getDescriptionResourceKey() {
        return descriptionResourceKey;
    }

    /**
     *
     * @return a description of the validation problem found.
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @return returns an array of resource parameters used for locale substitutions
     * in message bundles.
     */
    public Object[] getResourceParameters() {
        return resourceParameters;
    }


}
