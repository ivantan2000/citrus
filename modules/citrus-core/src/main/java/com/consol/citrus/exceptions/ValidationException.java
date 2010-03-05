/*
 * Copyright 2006-2010 ConSol* Software GmbH.
 * 
 * This file is part of Citrus.
 * 
 * Citrus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Citrus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Citrus. If not, see <http://www.gnu.org/licenses/>.
 */

package com.consol.citrus.exceptions;

/**
 * This exception marks errors during validation in Citrus. Basically this exception is thrown
 * during message validation, exception assertion, SOAP fault and attachment validation and so on.
 * 
 * @author Christoph Deppisch
 */
public class ValidationException extends CitrusRuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor using fields.
     * @param message
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Constructor using fields.
     * @param message
     * @param cause
     */
    public ValidationException(String message, Throwable cause) {
        super(message + " " +  cause.getLocalizedMessage(), cause);
    }
}
