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

import java.util.Enumeration;
import java.util.HashMap;

import com.webobjects.eocontrol.EOClassDescription;
import com.webobjects.eocontrol.EOKeyValueArchiver;
import com.webobjects.eocontrol.EOKeyValueArchiving;
import com.webobjects.eocontrol.EOKeyValueUnarchiver;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOQualifierVariable;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSCoder;
import com.webobjects.foundation.NSCoding;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSMutableSet;
import com.webobjects.foundation.NSSelector;
import com.webobjects.foundation.NSSet;

/**
 * Qualifier to find an object with a given attribute as close as possible to a
 * specified value.<br/>
 * 
 * The qualifier is initialized with the key path to the value to approximate,
 * the target value and a qualifierOperator specifying how to approximate the
 * limit. Usually this qualifier will match at most one object. Thus, in most
 * cases, sensible results are obtained only if the key path represents an
 * attribute or a to-one relationship. In cases where a to-many relationship is
 * involved, the BestRelationshipMatchesQualifier is likely to be most
 * appropriate for the task.<br/>
 * 
 * An alternate way of using the qualifier is to specify an optional array of
 * valuing key paths. This allows for handling cases where the objects to be
 * fetched are valued by relationships in a way that the best match is different
 * for each value in the relationship. The qualifier will thus match as many
 * objects as there are distinct value combinations reachable by theses key
 * paths.<br/>
 * 
 * For database qualification the best matching entry is searched for among all
 * commited instances of that entity. The list may actually be restricted by
 * using a qualifier. For in-memory evaluation such a list must be made
 * available to the qualifier. This is done by calling either prepareWithArray()
 * or qualifierPreparedWithArray().<br/>
 * 
 * A null (or NSKeyValueCoding.NullValue) limit allows for matching of the
 * object with largest or lowest value at keyPath.
 * 
 * @author bernard
 */
public class BestMatchQualifier extends Qualifier implements Cloneable,
		NSCoding, EOKeyValueArchiving
{
	// Private class constants
	
	private static final long		serialVersionUID	= 9135931883620237884L;
	
	
	
	// Public class constants
	
	/**
	 * Key value for the one argument clone method
	 */
	public static final String		KEY_PATH			= "keyPath",
			LIMIT = "limit", QUALIFIER_OPERATOR = "qualifierOperator",
			SUB_QUALIFIER = "subQualifier", UNIQUING_PATHS = "uniquingPaths";
	
	
	
	// Protected class constants
	
	/**
	 * List of allowed qualifierOperators
	 */
	protected static final NSSet	AVAILABLE_SELECTORS	= new NSSet(
																new NSSelector[] {
			QualifierOperatorLessThan, QualifierOperatorLessThanOrEqualTo,
			QualifierOperatorGreaterThanOrEqualTo, QualifierOperatorGreaterThan });
	
	
	
	// Protected instance variables
	
	/**
	 * Path to the value to maximize below the given limt
	 */
	protected String				keyPath;
	
	
	
	/**
	 * Upper limit for the value at the given keyPath
	 */
	protected Object				limit;
	
	
	
	/**
	 * Qualifier operator used to approximate the limit
	 */
	protected NSSelector			qualifierOperator;
	
	
	
	/**
	 * Qualifier used to limit the list of accepatble values
	 */
	protected EOQualifier			subQualifier;
	
	
	
	/**
	 * Best matching values from an in-memory array
	 */
	protected Object				bestValues			= null;
	
	
	
	/**
	 * List of key paths to the valuing attributes
	 */
	protected NSArray				uniquingPaths;
	
	
	
	
	// Constructor
	
	/**
	 * Constructor. No restricting qualifier<br/>
	 * <br/>
	 * 
	 * The applicable qualifierOperators are:
	 * <ul>
	 * <li>EOQualifier.QualifierOperatorLessThan
	 * <li>EOQualifier.QualifierOperatorLessThanOrEqualTo
	 * <li>EOQualifier.QualifierOperatorGreaterThanOrEqualTo
	 * <li>EOQualifier.QualifierOperatorGreaterThan
	 * </ul>
	 * 
	 * @param keyPath
	 *            Path to the value to maximize below the given limit
	 * @param limit
	 *            Upper limit for the value at the given keyPath
	 * @param qualifierOperator
	 *            Qualifier operator used to approximate the limit
	 */
	public BestMatchQualifier(String keyPath, Object limit,
			NSSelector qualifierOperator)
	{
		this(keyPath, limit, qualifierOperator, null, null);
	}
	
	
	
	/**
	 * Constructor<br/>
	 * <br/>
	 * 
	 * The applicable qualifierOperators are:
	 * <ul>
	 * <li>EOQualifier.QualifierOperatorLessThan
	 * <li>EOQualifier.QualifierOperatorLessThanOrEqualTo
	 * <li>EOQualifier.QualifierOperatorGreaterThanOrEqualTo
	 * <li>EOQualifier.QualifierOperatorGreaterThan
	 * </ul>
	 * 
	 * @param keyPath
	 *            Path to the value to maximize below the given limit
	 * @param limit
	 *            Upper limit for the value at the given keyPath
	 * @param qualifierOperator
	 *            qualifierOperator used to approximate the limit
	 * @param subQualifier
	 *            qualifier used to limit the list of accepatble values
	 */
	public BestMatchQualifier(String keyPath, Object limit,
			NSSelector qualifierOperator, EOQualifier subQualifier)
	{
		this(keyPath, limit, qualifierOperator, subQualifier, null);
	}
	
	
	
	/**
	 * Constructor<br/>
	 * <br/>
	 * 
	 * The applicable qualifierOperators are:
	 * <ul>
	 * <li>EOQualifier.QualifierOperatorLessThan
	 * <li>EOQualifier.QualifierOperatorLessThanOrEqualTo
	 * <li>EOQualifier.QualifierOperatorGreaterThanOrEqualTo
	 * <li>EOQualifier.QualifierOperatorGreaterThan
	 * </ul>
	 * 
	 * @param keyPath
	 *            Path to the value to maximize below the given limit
	 * @param limit
	 *            Upper limit for the value at the given keyPath
	 * @param qualifierOperator
	 *            qualifierOperator used to approximate the limit
	 * @param subQualifier
	 *            qualifier used to limit the list of accepatble values
	 * @param uniquingPaths
	 *            list of key paths to the valuing attributes
	 */
	public BestMatchQualifier(String keyPath, Object limit,
			NSSelector qualifierOperator, EOQualifier subQualifier,
			NSArray uniquingPaths)
	{
		this.keyPath = keyPath;
		this.limit = limit;
		this.qualifierOperator = qualifierOperator;
		this.subQualifier = subQualifier;
		this.uniquingPaths = (uniquingPaths != null) ? uniquingPaths
				: NSArray.EmptyArray;
		
		if (this.keyPath == null) {
			throw new IllegalArgumentException("The keyPath may not be null");
		}
		
		if (!AVAILABLE_SELECTORS.containsObject(this.qualifierOperator)) {
			throw new IllegalArgumentException(
					"qualifierOperator not applicable for this qualifier");
		}
		
		if (this.limit == null) {
			this.limit = NSKeyValueCoding.NullValue;
		}
	}
	
	
	
	// Public instance methods
	
	public String keyPath()
	{
		return this.keyPath;
	}
	
	
	public Object limit()
	{
		return this.limit;
	}
	
	
	public NSSelector qualifierOperator()
	{
		return this.qualifierOperator;
	}
	
	
	public EOQualifier subQualifier()
	{
		return this.subQualifier;
	}
	
	
	public NSArray uniquingPaths()
	{
		return this.uniquingPaths;
	}
	
	
	
	/**
	 * Prepares the qualifier for in-memory qualifying.<br/>
	 * <br/>
	 * 
	 * The best matching values (usually one) are picked from the array. Later
	 * evaluations need only to determine if the object to evaluate matches the
	 * found value.
	 * 
	 * @param array
	 *            the array from which to pick values
	 */
	public void prepareWithArray(NSArray array)
	{
		resetBestValues();
		
		if (array != null) {
			Enumeration en = array.objectEnumerator();
			
			while (en.hasMoreElements()) {
				Object object = en.nextElement();
				ValueHolder bestValueHolder = bestValueHolder(object, true);
				Object bestValue = bestValueHolder.value();
				
				if ((subQualifier() == null)
						|| (subQualifier().evaluateWithObject(object))) {
					Object value = NSKeyValueCodingAdditions.Utility
							.valueForKeyPath(object, keyPath());
					Object myLimit = limit();
					boolean resultOne = (myLimit == NSKeyValueCoding.NullValue)
							|| EOQualifier.ComparisonSupport.compareValues(
									value, myLimit, qualifierOperator());
					
					if (resultOne) {
						boolean resultTwo = (bestValue == null)
								|| EOQualifier.ComparisonSupport.compareValues(
										bestValue, value, qualifierOperator());
						
						if (resultTwo) {
							bestValue = value;
						}
					}
				}
				
				bestValueHolder.setValue(bestValue);
			}
		}
	}
	
	
	public BestMatchQualifier qualifierPreparedWithArray(NSArray array)
	{
		BestMatchQualifier clone = (BestMatchQualifier) clone();
		
		clone.prepareWithArray(array);
		
		return clone;
	}
	
	
	public boolean evaluateWithObject(Object object)
	{
		if (this.bestValues != null) {
			ValueHolder bestValueHolder = bestValueHolder(object, false);
			Object bestValue = (bestValueHolder != null) ? bestValueHolder
					.value() : null;
			Object value = NSKeyValueCodingAdditions.Utility.valueForKeyPath(
					object, keyPath());
			
			return ((bestValue != null) && (bestValue.equals(value)) && ((subQualifier() == null) || (subQualifier()
					.evaluateWithObject(object))));
		} else {
			throw new IllegalStateException(
					"The qualifier needs to be prepared first by passing it a value array");
		}
	}
	
	
	public EOQualifier qualifierWithBindings(NSDictionary bindings,
			boolean requiresAll)
	{
		if (this.limit instanceof EOQualifierVariable) {
			Object value = null;
			
			if (bindings != null) {
				value = bindings.valueForKeyPath(((EOQualifierVariable) limit)
						.key());
			}
			
			if (value != null) {
				BestMatchQualifier clone = (BestMatchQualifier) clone();
				
				clone.setLimit(value);
				
				return clone;
			}
			
			if (requiresAll) {
				throw new EOQualifier.QualifierVariableSubstitutionException(
						"Error in variable substitution: value for variable "
								+ this.limit + " not found");
			} else {
				return null;
			}
		} else {
			return this;
		}
	}
	
	
	public void validateKeysWithRootClassDescription(
			EOClassDescription classDescription)
	{
		Qualifier.validateKeyPathWithRootClassDescription(keyPath(),
				classDescription);
		
		if (subQualifier() != null) {
			String path = Qualifier.allButLastPathComponent(keyPath());
			EOClassDescription subDescription = classDescription
					.classDescriptionForKeyPath(path);
			
			subQualifier().validateKeysWithRootClassDescription(subDescription);
		}
	}
	
	
	public void addQualifierKeysToSet(NSMutableSet keySet)
	{
		keySet.addObject(keyPath());
		
		if (subQualifier() != null) {
			NSMutableSet subKeySet = new NSMutableSet();
			
			subQualifier().addQualifierKeysToSet(subKeySet);
			
			Enumeration subKeys = subKeySet.objectEnumerator();
			String prefix = keyPath()
					+ NSKeyValueCodingAdditions.KeyPathSeparator;
			
			while (subKeys.hasMoreElements()) {
				keySet.addObject(prefix + subKeys.nextElement());
			}
		}
		
		keySet.addObjectsFromArray(uniquingPaths());
	}
	
	
	public boolean equals(Object object)
	{
		if (object instanceof BestMatchQualifier) {
			BestMatchQualifier other = (BestMatchQualifier) object;
			
			return ((this.keyPath.equals(other.keyPath))
					&& (this.limit.equals(other.limit))
					&& (this.qualifierOperator.equals(other.qualifierOperator))
					&& (((this.subQualifier == null) && (this.subQualifier == other.subQualifier)) || (this.subQualifier
							.equals(other.subQualifier))) && (this.uniquingPaths
					.equals(other.uniquingPaths)));
		}
		
		return false;
	}
	
	
	public int hashCode()
	{
		return this.keyPath.hashCode();
	}
	
	
	public Object clone()
	{
		BestMatchQualifier clone = new BestMatchQualifier(keyPath(), limit(),
				qualifierOperator(), subQualifier(), uniquingPaths());
		
		return clone;
	}
	
	
	public BestMatchQualifier clone(NSDictionary newValues)
	{
		BestMatchQualifier clone = (BestMatchQualifier) clone();
		
		if (newValues != null) {
			Enumeration en = newValues.keyEnumerator();
			
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				Object value = newValues.objectForKey(key);
				
				NSKeyValueCoding.Utility.takeValueForKey(clone, value, key);
			}
		}
		
		return clone;
	}
	
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("(");
		buffer.append(this.keyPath);
		buffer.append(" = ");
		buffer
				.append(((this.qualifierOperator
						.equals(QualifierOperatorLessThan) || (this.qualifierOperator
						.equals(QualifierOperatorLessThanOrEqualTo))) ? "MAX("
						: "MIN("));
		buffer.append(this.keyPath);
		buffer.append(")");
		
		if (this.limit != NSKeyValueCoding.NullValue) {
			String value;
			
			if (this.limit instanceof Number) {
				value = this.limit.toString();
			} else if (this.limit instanceof EOQualifierVariable) {
				value = "$" + ((EOQualifierVariable) this.limit).key();
			} else {
				value = "'" + this.limit + "'";
			}
			buffer.append(" WHERE ");
			buffer.append(this.keyPath);
			buffer
					.append(((this.qualifierOperator
							.equals(QualifierOperatorLessThan) || (this.qualifierOperator
							.equals(QualifierOperatorLessThanOrEqualTo))) ? " <"
							: " >"));
			buffer
					.append(((this.qualifierOperator
							.equals(QualifierOperatorLessThanOrEqualTo) || (this.qualifierOperator
							.equals(QualifierOperatorGreaterThanOrEqualTo))) ? "= "
							: " "));
			buffer.append(value);
		}
		
		if (this.subQualifier != null) {
			if (this.limit != NSKeyValueCoding.NullValue) {
				buffer.append(" AND ");
			} else {
				buffer.append(" WHERE ");
			}
		}
		
		buffer.append(this.subQualifier.toString());
		
		if (this.uniquingPaths != null) {
			buffer.append(") FOR EACH ");
			buffer.append(this.uniquingPaths);
		} else {
			buffer.append(")");
		}
		
		return buffer.toString();
	}
	
	
	
	// Protected instance methods
	
	protected void resetBestValues()
	{
		int count = uniquingPaths.count();
		
		if (count > 0) {
			this.bestValues = new HashMap();
		} else {
			this.bestValues = new ValueHolder();
		}
	}
	
	
	protected ValueHolder bestValueHolder(Object object, boolean mayExpand)
	{
		int count = uniquingPaths.count();
		Object holder = this.bestValues;
		
		for (int i = 0; i < count; i++) {
			String path = (String) uniquingPaths.objectAtIndex(i);
			Object key = NSKeyValueCodingAdditions.Utility.valueForKeyPath(
					object, path);
			HashMap hashMap = (HashMap) holder;
			
			holder = hashMap.get(key);
			
			if (holder == null) {
				if (mayExpand) {
					if (i < (count - 1)) {
						holder = new HashMap();
					} else {
						holder = new ValueHolder();
					}
					
					hashMap.put(key, holder);
				} else {
					return null;
				}
			}
		}
		
		return (ValueHolder) holder;
	}
	
	
	
	// Conformance with NSCoding
	
	public Class classForCoder()
	{
		return getClass();
	}
	
	
	public static Object decodeObject(NSCoder coder)
	{
		return new BestMatchQualifier((String) coder.decodeObject(), coder
				.decodeObject(),
				EOQualifier.operatorSelectorForSelectorNamed((String) coder
						.decodeObject()), (EOQualifier) coder.decodeObject(),
				(NSArray) coder.decodeObject());
	}
	
	
	public void encodeWithCoder(NSCoder coder)
	{
		coder.encodeObject(keyPath());
		coder.encodeObject(limit());
		coder.encodeObject(qualifierOperator().name());
		coder.encodeObject(subQualifier());
		coder.encodeObject(uniquingPaths());
	}
	
	
	
	// Conformance with KeyValueCodingArchiving
	
	public void encodeWithKeyValueArchiver(EOKeyValueArchiver keyValueArchiver)
	{
		keyValueArchiver.encodeObject(keyPath(), KEY_PATH);
		keyValueArchiver.encodeObject(limit(), LIMIT);
		keyValueArchiver.encodeObject(qualifierOperator().name(),
				QUALIFIER_OPERATOR);
		keyValueArchiver.encodeObject(subQualifier(), SUB_QUALIFIER);
		keyValueArchiver.encodeObject(uniquingPaths(), UNIQUING_PATHS);
	}
	
	
	public static Object decodeWithKeyValueUnarchiver(
			EOKeyValueUnarchiver keyValueUnarchiver)
	{
		return new BestMatchQualifier((String) keyValueUnarchiver
				.decodeObjectForKey(KEY_PATH), keyValueUnarchiver
				.decodeObjectForKey(LIMIT), EOQualifier
				.operatorSelectorForSelectorNamed((String) keyValueUnarchiver
						.decodeObjectForKey(QUALIFIER_OPERATOR)),
				(EOQualifier) keyValueUnarchiver
						.decodeObjectForKey(SUB_QUALIFIER),
				(NSArray) keyValueUnarchiver.decodeObjectForKey(UNIQUING_PATHS));
	}
	
	
	
	// Private instance methods
	
	private void setLimit(Object limit)
	{
		this.limit = limit;
	}
	
	
	
	
	// Inner classes
	
	protected static class ValueHolder
	{
		// Protected instance variables
		
		protected Object	value;
		
		
		
		
		// Public instance methods
		
		public Object value()
		{
			return this.value;
		}
		
		
		public void setValue(Object value)
		{
			this.value = value;
		}
	}
}