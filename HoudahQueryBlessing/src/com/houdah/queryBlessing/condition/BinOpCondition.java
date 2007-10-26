/*
 * Modified MIT License
 * 
 * Copyright (c) 2006-2007 Houdah Software s.à r.l.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * Except as contained in this notice, the name(s) of the above copyright holders
 * shall not be used in advertising or otherwise to promote the sale, use or other 
 * dealings in this Software without prior written authorization.
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
**/

package com.houdah.queryBlessing.condition;

import com.houdah.queryBlessing.BlessingContext;
import com.houdah.queryBlessing.BlessingStatus;

import com.webobjects.eocontrol.EOKeyValueArchiver;
import com.webobjects.foundation.NSCoder;
import com.webobjects.foundation.NSSelector;

/**
 * Condition applying to an EOQualifier.<br/>
 * 
 * Abstract subclass of Condition. Parent class to conditions composed of two
 * other conditions.<br/>
 */
public abstract class BinOpCondition extends Condition
{
	// Protected class constants
	
	protected static final String	ERROR_MESSAGE_KEY		= "errorMessage";
	
	
	protected static final String	FIRST_CONDITION_KEY		= "firstCondition";
	
	
	protected static final String	SECOND_CONDITION_KEY	= "secondCondition";
	
	
	
	// Protected instance variables
	
	protected Condition				firstCondition, secondCondition;
	
	
	protected String				errorMessage;
	
	
	
	
	// Constructor
	
	/**
	 * Single public constructor.
	 * 
	 * @param errorMessage
	 *            the errorMessage to refer to when a qualifier fails
	 *            verification
	 * @param firstCondition
	 *            the first of the two conditions that is to be verified
	 * @param secondCondition
	 *            the second of the two conditions that is to be verified
	 */
	public BinOpCondition(String errorMessage, Condition firstCondition, Condition secondCondition)
	{
		this.errorMessage = errorMessage;
		this.firstCondition = firstCondition;
		this.secondCondition = secondCondition;
	}
	
	
	
	// Public instance methods
	
	/**
	 * Creates a new status object for the current condition.<br/>
	 * 
	 * Called by the bless() method to create a status object representing the
	 * initial context.
	 * 
	 * @return a new status object
	 */
	public BlessingStatus newStatus()
	{
		return new BinOpStatus(firstCondition.newStatus(), secondCondition.newStatus());
	}
	
	
	
	/**
	 * Evaluates the condition.<br/>
	 * 
	 * Called upon by support classes when a qualifier's key and value are
	 * known.<br/>
	 * 
	 * Needs to be implemented by concrete subclasses.
	 * 
	 * @param key
	 *            for which a qualifier provided a value
	 * @param value
	 *            the value provided. Null if no value is available. NullValue
	 *            if key is compared to a NullValue
	 * @param selector
	 *            the relationship between key and value. A qualifier operator
	 * @param context
	 *            the current context. May be modified by this method
	 * @return true if the condition is verified
	 */
	public boolean evaluate(String key, Object value, NSSelector selector, BlessingContext context)
	{
		BinOpStatus binOpStatus = (BinOpStatus) context.status();
		boolean firstVerified = binOpStatus.isFirstVerified();
		boolean secondVerified = binOpStatus.isSecondVerified();
		
		if (!firstVerified) {
			context.setStatus(binOpStatus.firstStatus());
			firstVerified = firstCondition.evaluate(key, value, selector, context);
			binOpStatus.setFirstVerified(firstVerified);
		}
		
		if (!secondVerified) {
			context.setStatus(binOpStatus.secondStatus());
			secondVerified = secondCondition.evaluate(key, value, selector, context);
			binOpStatus.setSecondVerified(secondVerified);
		}
		
		context.setStatus(binOpStatus);
		
		return computeResult(firstVerified, secondVerified);
	}
	
	
	
	/**
	 * Returns the error message to quote if this condition is not verified.<br/>
	 * 
	 * Called by the QualifierBlessing.bless() method when blessing is denied.
	 * 
	 * @return the error message, or null to use the default message
	 */
	public String errorMessage(BlessingStatus status)
	{
		BinOpStatus binOpStatus = (BinOpStatus) status;
		String message = this.errorMessage;
		
		if ((message == null) && (!binOpStatus.isFirstVerified())) {
			message = firstCondition.errorMessage(binOpStatus.firstStatus());
		}
		
		if ((message == null) && (!binOpStatus.isSecondVerified())) {
			message = secondCondition.errorMessage(binOpStatus.secondStatus());
		}
		
		return message;
	}
	
	
	
	/**
	 * Determines the result ot return based on the result returned by the two
	 * child conditions.
	 * 
	 * @param firstResult
	 *            the result returned by the first condition
	 * @param secondResult
	 *            the result returned by the second condition
	 * @param the
	 *            result to return by the combined condition
	 */
	protected abstract boolean computeResult(boolean firstResult, boolean secondResult);
	
	
	
	// Conformance with NSCoding
	
	public Class classForCoder()
	{
		return getClass();
	}
	
	
	public void encodeWithCoder(NSCoder coder)
	{
		coder.encodeObject(this.errorMessage);
		coder.encodeObject(this.firstCondition);
		coder.encodeObject(this.secondCondition);
	}
	
	
	
	// Conformance with KeyValueCodingArchiving
	
	public void encodeWithKeyValueArchiver(EOKeyValueArchiver keyValueArchiver)
	{
		keyValueArchiver.encodeObject(this.errorMessage, ERROR_MESSAGE_KEY);
		keyValueArchiver.encodeObject(this.firstCondition, FIRST_CONDITION_KEY);
		keyValueArchiver.encodeObject(this.secondCondition, SECOND_CONDITION_KEY);
	}
	
	
	
	
	// Inner class
	
	/**
	 * Status class used for propagating state while verifying compliance of a
	 * qualifier with a Condition.<br/>
	 * 
	 * Extends the default status class to include information needed by the
	 * TimeIntervalCondition.
	 */
	public static class BinOpStatus extends BlessingStatus
	{
		// Protected instance variables
		
		/**
		 * Status objects for the sub-conditions.
		 */
		protected BlessingStatus	firstStatus, secondStatus;
		
		
		
		/**
		 * Flags to tell which condition is verified in the current context
		 */
		protected boolean			firstVerified, secondVerified;
		
		
		
		
		// Constructor
		
		/**
		 * Single protected constructor.
		 * 
		 * @param firstStatus
		 *            status object for the first sub-condition.
		 * @param secondStatus
		 *            status object for the second sub-condition.
		 */
		protected BinOpStatus(BlessingStatus firstStatus, BlessingStatus secondStatus)
		{
			this.firstStatus = firstStatus;
			this.secondStatus = secondStatus;
			this.firstVerified = false;
			this.secondVerified = false;
		}
		
		
		
		// Public instance methods
		
		/**
		 * Returns the status object for the first sub-condition.
		 * 
		 * @return the status object for the first sub-condition
		 */
		public BlessingStatus firstStatus()
		{
			return firstStatus;
		}
		
		
		
		/**
		 * Returns the status object for the first sub-condition.
		 * 
		 * @return the status object for the first sub-condition
		 */
		public BlessingStatus secondStatus()
		{
			return secondStatus;
		}
		
		
		
		/**
		 * Returns true if the first condition is verified in the current
		 * branch.
		 * 
		 * @return true if the first condition is verified in the current branch
		 */
		public boolean isFirstVerified()
		{
			return firstVerified;
		}
		
		
		
		/**
		 * Sets the flag determining if the first condition is verified in the
		 * current branch.
		 * 
		 * @param firstVerified
		 *            new value for the flag
		 */
		public void setFirstVerified(boolean firstVerified)
		{
			this.firstVerified = firstVerified;
		}
		
		
		
		/**
		 * Returns true if the second condition is verified in the current
		 * branch.
		 * 
		 * @return true if the second condition is verified in the current
		 *         branch
		 */
		public boolean isSecondVerified()
		{
			return secondVerified;
		}
		
		
		
		/**
		 * Sets the flag determining if the second condition is verified in the
		 * current branch.
		 * 
		 * @param secondVerified
		 *            new value for the flag
		 */
		public void setSecondVerified(boolean secondVerified)
		{
			this.secondVerified = secondVerified;
		}
		
		
		
		/**
		 * Sets the flag determining if the current qualifier branch is negated,
		 * i.e contains an EONotQualifier at a higher level.
		 * 
		 * @param the
		 *            new negate flag
		 */
		public void setNegate(boolean negate)
		{
			super.setNegate(negate);
			
			firstStatus.setNegate(negate);
			secondStatus.setNegate(negate);
		}
		
		
		
		/**
		 * For debugging purposes
		 * 
		 * @return a human readable represenation of the status object
		 */
		public String toString()
		{
			return "BinOpStatus <negate=" + negate() + ", firstStatus=" + firstStatus
					+ ", firstStatus=" + firstStatus + ", secondStatus=" + secondStatus
					+ ", secondVerified=" + secondVerified + ">";
		}
		
		
		
		// Protected instance methods
		
		/**
		 * Clones the current status for use in a new branch of the qualifier.
		 * 
		 * @return a copy of this status object
		 */
		public BlessingStatus newStatus()
		{
			try {
				BinOpStatus status = (BinOpStatus) clone();
				
				status.firstStatus = firstStatus.newStatus();
				status.secondStatus = secondStatus.newStatus();
				
				return status;
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException("Wow, I did not expect that one!");
			}
		}
	}
}