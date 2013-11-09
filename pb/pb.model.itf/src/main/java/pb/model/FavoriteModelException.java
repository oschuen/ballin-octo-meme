/**
 * Copyright (C) 2013 Oliver Schünemann
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, 
 * Boston, MA 02110, USA 
 * 
 * @since 12.10.2013
 * @version 1.0
 * @author oliver
 */
package pb.model;

/**
 * Exception used to return Malfunction Reason from the Model to the user.
 * 
 * @author oliver
 */
public class FavoriteModelException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 409786154067154429L;
	protected final Cause cause;

	public static enum Cause {
		UNKNOWN, NOT_ENOUGH_CREDIT, SERVER_NOT_AVAILABLE, ALLREADY_PAIRED
	}

	/**
	 * Make super class constructors unaccessible because they don't set the
	 * cause
	 */
	protected FavoriteModelException() {
		super();
		this.cause = Cause.UNKNOWN;
	}

	/**
	 * Make super class constructors unaccessible because they don't set the
	 * cause
	 */
	protected FavoriteModelException(final String message, final Throwable cause,
			final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.cause = Cause.UNKNOWN;
	}

	/**
	 * Make super class constructors unaccessible because they don't set the
	 * cause
	 */
	protected FavoriteModelException(final String message, final Throwable cause) {
		super(message, cause);
		this.cause = Cause.UNKNOWN;
	}

	/**
	 * Make super class constructors unaccessible because they don't set the
	 * cause
	 */
	protected FavoriteModelException(final String message) {
		super(message);
		this.cause = Cause.UNKNOWN;
	}

	/**
	 * Make super class constructors unaccessible because they don't set the
	 * cause
	 */
	protected FavoriteModelException(final Throwable cause) {
		super(cause);
		this.cause = Cause.UNKNOWN;
	}

	/**
	 * Standard Constructor setting the cause for the exception
	 * 
	 * @param cause
	 */
	public FavoriteModelException(final Cause cause) {
		super();
		this.cause = cause;
	}

	/**
	 * Standard Constructor setting the cause for the exception and adds a more
	 * detailed message to it-
	 * 
	 * @param cause
	 */
	protected FavoriteModelException(final Cause cause, final String message) {
		super(message);
		this.cause = cause;
	}

	/**
	 * @return the cause
	 */
	public Cause getModelCause() {
		return cause;
	}
}
