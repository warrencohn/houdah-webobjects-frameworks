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

import com.houdah.foundation.utilities.TimestampUtilities;
import com.houdah.queryBlessing.BlessingContext;
import com.houdah.queryBlessing.BlessingErrorCause;
import com.houdah.queryBlessing.BlessingStatus;

import com.webobjects.eocontrol.EOKeyValueArchiver;
import com.webobjects.eocontrol.EOKeyValueUnarchiver;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSCoder;
import com.webobjects.foundation.NSSelector;
import com.webobjects.foundation.NSTimestamp;

/**
 * Condition applying to an EOQualifier.<br/>
 * 
 * Concrete subclasse of KeyCondition. Requires that the qualifier provides both
 * a lower and an upper boundary for a date attribute and that these boundaries
 * are no more than a specified number of months and days apart.<br/>
 */
public class TimeIntervalCondition extends KeyCondition
{
	// Protected class constants
	
	protected static final String	MIN_MONTHS_KEY		= "minMonths";
	
	
	protected static final String	MIN_DAYS_KEY		= "minDays";
	
	protected static final String	DEFAULT_UPPER_KEY	= "defaultUpper";
	
	
	
	// Protected instance variables
	
	/**
	 * The maximum timespan allowed between the lower and upper boundaries is
	 * minMonth months plus minDays days.
	 */
	protected int					minMonths, minDays;
	
	
	
	/**
	 * Flag that determines if the current date is to be used as default upper
	 * boundary.
	 */
	protected boolean				defaultUpper;
	
	
	
	
	// Constructor
	
	/**
	 * Public constructor.
	 * 
	 * @param errorMessage
	 *            the errorMessage to refer to when a qualifier fails
	 *            verification
	 * @param conditionKey
	 *            the name of the attribute this condition applies to
	 * @param minMonths
	 *            the minimum number of months between the lower and upper
	 *            boundaries. Summed with minDays
	 * @param minDays
	 *            the minimum number of days between the lower and upper
	 *            boundaries. Summed with minMonths
	 * @param defaultUpper
	 *            flag that determines if the current date is to be used as
	 *            default upper boundary
	 */
	public TimeIntervalCondition(String errorMessage, String conditionKey, int minMonths,
			int minDays, boolean defaultUpper)
	{
		super(errorMessage, conditionKey);
		
		this.minMonths = minMonths;
		this.minDays = minDays;
		this.defaultUpper = defaultUpper;
	}
	
	
	
	/**
	 * Public constructor.
	 * 
	 * @param conditionKey
	 *            the name of the attribute this condition applies to
	 * @param minMonths
	 *            the minimum number of months between the lower and upper
	 *            boundaries. Summed wiith minDays
	 * @param minDays
	 *            the minimum number of days between the lower and upper
	 *            boundaries. Summed wiith minMonths
	 * @param defaultUpper
	 *            flag that determines if the current date is to be used as
	 *            default upper boundary
	 */
	public TimeIntervalCondition(String conditionKey, int minMonths, int minDays,
			boolean defaultUpper)
	{
		this(null, conditionKey, minMonths, minDays, defaultUpper);
	}
	
	
	
	// Public instance methods
	
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
		if (conditionKey.equals(key) && (value instanceof NSTimestamp)) {
			TimeIntervalStatus tiStatus = (TimeIntervalStatus) context.status();
			boolean isGreater = (selector == EOQualifier.QualifierOperatorGreaterThan)
					|| (selector == EOQualifier.QualifierOperatorGreaterThanOrEqualTo);
			boolean isEqual = (selector == EOQualifier.QualifierOperatorEqual);
			boolean isNotEqual = (selector == EOQualifier.QualifierOperatorNotEqual);
			boolean isLess = (selector == EOQualifier.QualifierOperatorLessThan)
					|| (selector == EOQualifier.QualifierOperatorLessThanOrEqualTo);
			boolean negate = tiStatus.negate();
			NSTimestamp upperBoundary = tiStatus.getUpperBoundary();
			NSTimestamp lowerBoundary = tiStatus.getLowerBoundary();
			NSTimestamp timestamp = (NSTimestamp) value;
			
			if (negate) {
				boolean tmp;
				
				tmp = isGreater;
				isGreater = isLess;
				isLess = tmp;
				
				tmp = isEqual;
				isEqual = isNotEqual;
				isNotEqual = tmp;
			}
			
			if (isEqual) {
				return true;
			} else if (isGreater) {
				lowerBoundary = (lowerBoundary == null) ? timestamp : (timestamp
						.after(lowerBoundary) ? timestamp : lowerBoundary);
			} else if (isLess) {
				upperBoundary = (upperBoundary == null) ? timestamp : (timestamp
						.before(upperBoundary) ? timestamp : upperBoundary);
			}
			
			tiStatus.setUpperBoundary(upperBoundary);
			tiStatus.setLowerBoundary(lowerBoundary);
			
			if ((upperBoundary != null) && (lowerBoundary != null)) {
				lowerBoundary = TimestampUtilities.timestampByAddingGregorianUnits(lowerBoundary,
						0, minMonths, minDays, 0, 0, 0);
				if (!lowerBoundary.before(upperBoundary))
					return true;
			}
		}
		
		context.setErrorCause(new BlessingErrorCause(this.errorMessage, key, value, selector));
		
		return false;
	}
	
	
	
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
		TimeIntervalStatus status = new TimeIntervalStatus();
		
		if (defaultUpper) {
			status.setUpperBoundary(new NSTimestamp());
		}
		
		return status;
	}
	
	
	
	// Conformance with NSCoding
	
	public Class classForCoder()
	{
		return getClass();
	}
	
	
	public static Object decodeObject(NSCoder coder)
	{
		return new TimeIntervalCondition((String) coder.decodeObject(), (String) coder
				.decodeObject(), coder.decodeInt(), coder.decodeInt(), coder.decodeBoolean());
	}
	
	
	public void encodeWithCoder(NSCoder coder)
	{
		coder.encodeObject(this.errorMessage);
		coder.encodeObject(this.conditionKey);
		coder.encodeInt(this.minMonths);
		coder.encodeInt(this.minDays);
		coder.encodeBoolean(this.defaultUpper);
	}
	
	
	
	// Conformance with KeyValueCodingArchiving
	
	public void encodeWithKeyValueArchiver(EOKeyValueArchiver keyValueArchiver)
	{
		keyValueArchiver.encodeObject(this.errorMessage, ERROR_MESSAGE_KEY);
		keyValueArchiver.encodeObject(this.conditionKey, CONDITION_KEY_KEY);
		keyValueArchiver.encodeInt(this.minMonths, MIN_MONTHS_KEY);
		keyValueArchiver.encodeInt(this.minDays, MIN_DAYS_KEY);
		keyValueArchiver.encodeBool(this.defaultUpper, DEFAULT_UPPER_KEY);
	}
	
	
	public static Object decodeWithKeyValueUnarchiver(EOKeyValueUnarchiver keyValueUnarchiver)
	{
		return new TimeIntervalCondition((String) keyValueUnarchiver
				.decodeObjectForKey(ERROR_MESSAGE_KEY), (String) keyValueUnarchiver
				.decodeObjectForKey(CONDITION_KEY_KEY), keyValueUnarchiver
				.decodeIntForKey(MIN_MONTHS_KEY), keyValueUnarchiver.decodeIntForKey(MIN_DAYS_KEY),
				keyValueUnarchiver.decodeBoolForKey(DEFAULT_UPPER_KEY));
	}
	
	
	
	
	// Inner class
	
	/**
	 * Status class used for propagating state while verifying compliance of a
	 * qualifier with a Condition.<br/>
	 * 
	 * Extends the default status class to include information needed by the
	 * TimeIntervalCondition.
	 */
	public static class TimeIntervalStatus extends BlessingStatus
	{
		// Protected instance variables
		
		/**
		 * Boundary values encountered so far.
		 */
		protected NSTimestamp	lowerBoundary, upperBoundary;
		
		
		
		
		// Constructor
		
		/**
		 * Single protected constructor.
		 * 
		 * Should not be instantiated form outside this package.
		 */
		protected TimeIntervalStatus()
		{
			this.lowerBoundary = null;
			this.upperBoundary = null;
		}
		
		
		
		// Public instance methods
		
		/**
		 * Returns the highest lower boundary value found so far in the current
		 * branch.
		 * 
		 * @return the boundary, null if none was found so far
		 */
		public NSTimestamp getLowerBoundary()
		{
			return lowerBoundary;
		}
		
		
		
		/**
		 * Sets lower boundary value. Called by
		 * TimeCondition.evaluate(EOKeyValueQualifier). The caller ensures that
		 * the value is the highest lower boundary found in the current branch.
		 * 
		 * @param lowerBoundary
		 *            the boundary, null if none was found so far
		 */
		public void setLowerBoundary(NSTimestamp lowerBoundary)
		{
			this.lowerBoundary = lowerBoundary;
		}
		
		
		
		/**
		 * Returns the lowest upper boundary value found so far in the current
		 * branch.
		 * 
		 * @return the boundary, null if none was found so far
		 */
		public NSTimestamp getUpperBoundary()
		{
			return upperBoundary;
		}
		
		
		
		/**
		 * Sets upper boundary value. Called by
		 * TimeCondition.evaluate(EOKeyValueQualifier). The caller ensures that
		 * the value is the lowest upper boundary found in the current branch.
		 * 
		 * @param upperBoundary
		 *            the boundary, null if none was found so far
		 */
		public void setUpperBoundary(NSTimestamp upperBoundary)
		{
			this.upperBoundary = upperBoundary;
		}
		
		
		
		/**
		 * For debugging purposes
		 * 
		 * @return a human readable represenation of the status object
		 */
		public String toString()
		{
			return "TimeIntervalStatus <negate=" + negate() + ", lowerBoundary=" + lowerBoundary
					+ ", upperBoundary=" + upperBoundary + ">";
		}
	}
}