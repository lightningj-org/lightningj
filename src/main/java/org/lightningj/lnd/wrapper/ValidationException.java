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

/**
 * Exception indicating an error occurred validating a message.
 * Contains an attached validation result with details of message and
 * field that failed validation.
 *
 *
 * Created by Philip Vendil.
 */
public class ValidationException extends Exception {

    private ValidationResult validationResult;

    /**
     * Exception indicating an error occurred validating a message.
     * Contains an attached validation result with details of message and
     * field that failed validation.
     *
     * @param message detail message of the exception.
     * @param validationResult the validation result containing all validation problems found.
     */
    public ValidationException(String message, ValidationResult validationResult) {
        super(message);
        this.validationResult = validationResult;
    }

    /**
     * Exception indicating an error occurred validating a message.
     * Contains an attached validation result with details of message and
     * field that failed validation.
     *
     * @param message detail message of the exception.
     * @param validationResult the validation result containing all validation problems found.
     * @param cause the underlying exception.
     */
    public ValidationException(String message, ValidationResult validationResult, Throwable cause) {
        super(message, cause);
        this.validationResult = validationResult;
    }

    /**
     * @return attached validation result containing details of message and
     * field that failed validation.
     */
    public ValidationResult getValidationResult(){
        return validationResult;
    }


}
