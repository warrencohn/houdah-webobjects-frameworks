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

package com.houdah.eocontrol.qualifiers;

import com.webobjects.eocontrol.EOClassDescription;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOQualifierEvaluation;
import com.webobjects.eocontrol.EOQualifierVariable;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSMutableSet;
import com.webobjects.foundation.NSSelector;

/**
 * Qualifier to operate on month precision dates (a.k.a. periods) stored as year
 * and month fields.
 * 
 * @author bernard
 */
public class PeriodQualifier extends Qualifier implements EOQualifierEvaluation
{
	// Private class constants
	
	private static final long	serialVersionUID	= 2515432338922498957L;
	
	
	
	// Protected instance variables
	
	protected String			keyPath;
	
	protected String			yearKey;
	
	protected String			monthKey;
	
	protected NSSelector		qualifierOperator;
	
	protected Object			yearValue;
	
	protected Object			monthValue;
	
	protected Object			period;
	
	
	
	
	// Constructor
	
	/**
	 * Constructs a qualifier to match a period EO.
	 * 
	 * @param keyPath
	 *            the path to the period entity
	 * @param yearKey
	 *            key to the period entity attribute holding the year value, not
	 *            null
	 * @param monthKey
	 *            key to the period entity attribute holding the month value,
	 *            not null
	 * @param qualifierOperator
	 *            comparison operator, not null
	 * @param yearValue
	 *            reference value for year
	 * @param monthValue
	 *            reference value for month
	 */
	public PeriodQualifier(String keyPath, String yearKey, String monthKey,
			NSSelector qualifierOperator, Object yearValue, Object monthValue)
	{
		if ((yearKey == null)
				|| (yearKey.indexOf(NSKeyValueCodingAdditions.KeyPathSeparator) > -1)) {
			throw new IllegalArgumentException("yearKey must be a simple key");
		}
		
		if ((monthKey == null)
				|| (monthKey
						.indexOf(NSKeyValueCodingAdditions.KeyPathSeparator) > -1)) {
			throw new IllegalArgumentException("monthKey must be a simple key");
		}
		
		if (qualifierOperator == null) {
			throw new IllegalArgumentException(
					"qualifierOperator must not be null");
		}
		
		if (!((yearValue == NSKeyValueCoding.NullValue) || (yearValue == null) || (yearValue instanceof EOQualifierVariable))) {
			int intValue = ((Number) yearValue).intValue();
			
			if ((intValue < 1900) || (intValue > 3000)) {
				throw new IllegalArgumentException(
						"yearValue must be between 1900 and 3000");
			}
		}
		
		if (!((monthValue == NSKeyValueCoding.NullValue)
				|| (monthValue == null) || (monthValue instanceof EOQualifierVariable))) {
			int intValue = ((Number) monthValue).intValue();
			
			if ((intValue < 1) || (intValue > 12)) {
				throw new IllegalArgumentException(
						"monthValue must be between 1 and 12");
			}
		}
		
		this.keyPath = "".equals(keyPath) ? null : keyPath;
		this.yearKey = yearKey;
		this.monthKey = monthKey;
		this.qualifierOperator = qualifierOperator;
		this.yearValue = yearValue;
		this.monthValue = monthValue;
		this.period = null;
	}
	
	
	
	// Public instance methods
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eocontrol.EOQualifierEvaluation#evaluateWithObject(java.lang.Object)
	 */
	public boolean evaluateWithObject(Object object)
	{
		Object rhs = period();
		String myKeyPath = keyPath();
		Object target = (myKeyPath != null) ? NSKeyValueCodingAdditions.Utility
				.valueForKeyPath(object, keyPath()) : object;
		
		Object year = NSKeyValueCoding.Utility.valueForKey(target, yearKey());
		Object month = NSKeyValueCoding.Utility.valueForKey(target, monthKey());
		
		Object lhs = null;
		
		if ((year == null) || (month == null)) {
			lhs = NSKeyValueCoding.NullValue;
		} else {
			lhs = new Integer(((Number) year).intValue() * 100
					+ ((Number) month).intValue());
		}
		
		return EOQualifier.ComparisonSupport.compareValues(lhs, rhs,
				qualifierOperator());
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eocontrol.EOQualifier#qualifierWithBindings(com.webobjects.foundation.NSDictionary,
	 *      boolean)
	 */
	public EOQualifier qualifierWithBindings(NSDictionary bindings,
			boolean requiresAll)
	{
		Object myYearValue = yearValue();
		Object myMonthValue = monthValue();
		boolean hasBindings = false;
		if (myYearValue instanceof EOQualifierVariable) {
			Object value = null;
			if (bindings != null) {
				value = bindings
						.valueForKeyPath(((EOQualifierVariable) myYearValue)
								.key());
			}
			
			if (value != null) {
				myYearValue = value;
				hasBindings = true;
			} else {
				if (requiresAll) {
					throw new EOQualifier.QualifierVariableSubstitutionException(
							"Error in variable substitution: value for variable "
									+ myYearValue + " not found");
				} else {
					return null;
				}
			}
		}
		
		if (myMonthValue instanceof EOQualifierVariable) {
			Object value = null;
			if (bindings != null) {
				value = bindings
						.valueForKeyPath(((EOQualifierVariable) myMonthValue)
								.key());
			}
			
			if (value != null) {
				myMonthValue = value;
				hasBindings = true;
			} else {
				if (requiresAll) {
					throw new EOQualifier.QualifierVariableSubstitutionException(
							"Error in variable substitution: value for variable "
									+ myMonthValue + " not found");
				} else {
					return null;
				}
			}
		}
		
		if (hasBindings) {
			return new PeriodQualifier(keyPath(), yearKey(), monthKey(),
					qualifierOperator(), myYearValue, myMonthValue);
		} else {
			return this;
		}
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eocontrol.EOQualifier#validateKeysWithRootClassDescription(com.webobjects.eocontrol.EOClassDescription)
	 */
	public void validateKeysWithRootClassDescription(
			EOClassDescription rootClassDescription)
	{
		Qualifier.validateKeyPathWithRootClassDescription(yearPath(),
				rootClassDescription);
		Qualifier.validateKeyPathWithRootClassDescription(monthPath(),
				rootClassDescription);
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eocontrol.EOQualifier#addQualifierKeysToSet(com.webobjects.foundation.NSMutableSet)
	 */
	public void addQualifierKeysToSet(NSMutableSet keySet)
	{
		keySet.addObject(yearPath());
		keySet.addObject(monthPath());
	}
	
	
	
	// Getters
	
	public String keyPath()
	{
		return this.keyPath;
	}
	
	
	public String yearKey()
	{
		return this.yearKey;
	}
	
	
	public String monthKey()
	{
		return this.monthKey;
	}
	
	
	public NSSelector qualifierOperator()
	{
		return this.qualifierOperator;
	}
	
	
	public Object yearValue()
	{
		return this.yearValue;
	}
	
	
	public Object monthValue()
	{
		return this.monthValue;
	}
	
	
	public String yearPath()
	{
		String yearPath = yearKey();
		String myKeyPath = keyPath();
		
		if (myKeyPath != null) {
			yearPath = myKeyPath + NSKeyValueCodingAdditions.KeyPathSeparator
					+ yearPath;
		}
		
		return yearPath;
	}
	
	
	public String monthPath()
	{
		
		String monthPath = monthKey();
		String myKeyPath = keyPath();
		
		if (myKeyPath != null) {
			monthPath = myKeyPath + NSKeyValueCodingAdditions.KeyPathSeparator
					+ monthPath;
		}
		
		return monthPath;
	}
	
	
	public Object period()
	{
		if (this.period == null) {
			Object myYearValue = yearValue();
			Object myMonthValue = monthValue();
			
			if (myYearValue instanceof EOQualifierVariable) {
				throw new IllegalStateException(
						"Value must be substitued for yearValue variable."
								+ " The qualifier variable '$"
								+ ((EOQualifierVariable) myYearValue).key()
								+ "' is unbound.");
			}
			
			if (myMonthValue instanceof EOQualifierVariable) {
				throw new IllegalStateException(
						"Value must be substitued for monthValue variable."
								+ " The qualifier variable '$"
								+ ((EOQualifierVariable) myMonthValue).key()
								+ "' is unbound.");
			}
			
			if ((myYearValue == null) || (myMonthValue == null)) {
				this.period = NSKeyValueCoding.NullValue;
			} else {
				this.period = new Integer(((Number) myYearValue).intValue() * 100
						+ ((Number) myMonthValue).intValue());
			}
		}
		
		return period;
	}
}