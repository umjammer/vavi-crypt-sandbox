/*
 * @(#) $Id: CADatabaseException.java,v 1.1.1.1 2003/10/05 18:39:14 pankaj_kumar Exp $
 *
 * Copyright (c) 2002-03 by Pankaj Kumar (http://www.pankaj-k.net).
 * All rights reserved.
 *
 * The license governing the use of this file can be found in the
 * root directory of the containing software.
 */

package org.jstk.cert.ca;

public class CADatabaseException extends Exception {
    public CADatabaseException() {
        super();
    }

    public CADatabaseException(String message) {
        super(message);
    }

    public CADatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public CADatabaseException(Throwable cause) {
        super(cause);
    }
}
