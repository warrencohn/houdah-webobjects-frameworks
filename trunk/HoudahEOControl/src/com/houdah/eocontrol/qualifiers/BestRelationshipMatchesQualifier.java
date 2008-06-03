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

import com.houdah.foundation.KVCUtility;
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
 * Qualifier to find objects where a related object with a given attribute as
 * close as possible to a specified value matches a provided qualifier.<br/>
 * 
 * The qualifier is initialized with the key path to the value to approximate,
 * the target value and a qualifierOperator specifying how to approximate the
 * limit. The value to approximate is an attribute of a related entity. The
 * keyPath thus has to traverse at least one relationship. The qualifier is also
 * set up with 2 optional qualifiers. The first one restricts the related object
 * among which to search for the best value. The second one is to be applied to
 * the found object. If that qualifier finds a match the parent/root object is
 * said to match this the BestRelationshipMatchesQualifier.
 * 
 * @author bernard
 */
public class BestRelationshipMatchesQualifier extends Qualifier implements
		Cloneable, NSCoding, EOKeyValueArchiving
{
	// Private class constants
	
	private static final long		serialVersionUID	= -565573475498534756L;
	
	
	
	// Public class constants
	
	/**
	 * Key value for the one argument clone method
	 */
	public static final String		KEY_PATH			= "keyPath",
			LIMIT = "limit", QUALIFIER_OPERATOR = "qualifierOperator",
			SUB_QUALIFIER = "subQualifier", MATCH_QUALIFIER = "matchQualifier";
	
	
	
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
	 * Qualifier applied to the related objects holding the values which
	 * approximate the limit best
	 */
	protected EOQualifier			matchQualifier;
	
	
	
	
	// Constructor
	
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
	 * <br/>
	 * 
	 * N.B: Both the subQualifier and the matchQualifier are relative to the
	 * entity holding the limit value.
	 * 
	 * @param keyPath
	 *            Path to the value to maximize below the given limit
	 * @param limit
	 *            Upper limit for the value at the given keyPath
	 * @param qualifierOperator
	 *            qualifierOperator used to approximate the limit
	 * @param subQualifier
	 *            Qualifier used to limit the list of accepatble values
	 * @param matchQualifier
	 *            Qualifier applied to the related objects holding the values
	 *            which approximate the limit best
	 */
	public BestRelationshipMatchesQualifier(String keyPath, Object limit,
			NSSelector qualifierOperator, EOQualifier subQualifier,
			EOQualifier matchQualifier)
	{
		this.keyPath = keyPath;
		this.limit = limit;
		this.qualifierOperator = qualifierOperator;
		this.subQualifier = subQualifier;
		this.matchQualifier = matchQualifier;
		
		if ((this.keyPath == null) || (this.keyPath.indexOf('.') < 0)) {
			throw new IllegalArgumentException("The keyPath must be a path");
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
	
	
	public EOQualifier matchQualifier()
	{
		return this.matchQualifier;
	}
	
	
	public boolean evaluateWithObject(Object object)
	{
		String myKeyPath = keyPath();
		int dotIndex = myKeyPath.lastIndexOf('.');
		String key = myKeyPath.substring(dotIndex + 1);
		String path = myKeyPath.substring(0, dotIndex);
		BestMatchQualifier bmQualifier = new BestMatchQualifier(key, limit(),
				qualifierOperator(), subQualifier());
		Object values = NSKeyValueCodingAdditions.Utility.valueForKeyPath(
				object, path);
		
		if (!(values instanceof NSArray)) {
			values = new NSArray(values);
		}
		
		bmQualifier.prepareWithArray((NSArray) values);
		
		NSArray bestMatch = EOQualifier.filteredArrayWithQualifier(
				(NSArray) values, bmQualifier);
		NSArray filteredMatch = EOQualifier.filteredArrayWithQualifier(
				bestMatch, matchQualifier());
		
		return (filteredMatch.count() != 0);
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
				BestRelationshipMatchesQualifier clone = (BestRelationshipMatchesQualifier) clone();
				
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
		
		String path = KVCUtility.sharedInstance().allButLastPathComponent(keyPath());
		EOClassDescription subDescription = classDescription
				.classDescriptionForKeyPath(path);
		
		if (subQualifier() != null) {
			subQualifier().validateKeysWithRootClassDescription(subDescription);
		}
		
		if (matchQualifier() != null) {
			matchQualifier().validateKeysWithRootClassDescription(
					subDescription);
		}
	}
	
	
	public void addQualifierKeysToSet(NSMutableSet keySet)
	{
		NSMutableSet subKeySet = new NSMutableSet();
		
		keySet.addObject(keyPath());
		
		if (subQualifier() != null) {
			subQualifier().addQualifierKeysToSet(subKeySet);
		}
		
		if (matchQualifier() != null) {
			matchQualifier().addQualifierKeysToSet(subKeySet);
		}
		
		Enumeration subKeys = subKeySet.objectEnumerator();
		
		if (subKeys.hasMoreElements()) {
			String prefix = keyPath()
					+ NSKeyValueCodingAdditions.KeyPathSeparator;
			
			while (subKeys.hasMoreElements()) {
				keySet.addObject(prefix + subKeys.nextElement());
			}
		}
	}
	
	
	public boolean equals(Object object)
	{
		if (object instanceof BestRelationshipMatchesQualifier) {
			BestRelationshipMatchesQualifier other = (BestRelationshipMatchesQualifier) object;
			
			return ((this.keyPath.equals(other.keyPath))
					&& (this.limit.equals(other.limit))
					&& (this.qualifierOperator.equals(other.qualifierOperator))
					&& (((this.subQualifier == null) && (this.subQualifier == other.subQualifier)) || (this.subQualifier
							.equals(other.subQualifier))) && (((this.matchQualifier == null) && (this.matchQualifier == other.matchQualifier)) || (this.matchQualifier
					.equals(other.matchQualifier))));
		}
		
		return false;
	}
	
	
	public int hashCode()
	{
		return this.keyPath.hashCode();
	}
	
	
	public Object clone()
	{
		BestRelationshipMatchesQualifier clone = new BestRelationshipMatchesQualifier(
				keyPath(), limit(), qualifierOperator(), subQualifier(),
				matchQualifier());
		
		return clone;
	}
	
	
	public BestRelationshipMatchesQualifier clone(NSDictionary newValues)
	{
		BestRelationshipMatchesQualifier clone = (BestRelationshipMatchesQualifier) clone();
		
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
		String value;
		
		if (this.limit == NSKeyValueCoding.NullValue) {
			value = "null";
		} else if (this.limit instanceof Number) {
			value = this.limit.toString();
		} else if (this.limit instanceof EOQualifierVariable) {
			value = "$" + ((EOQualifierVariable) this.limit).key();
		} else {
			value = "'" + this.limit + "'";
		}
		
		return "("
				+ this.keyPath
				+ " = "
				+ ((this.qualifierOperator.equals(QualifierOperatorLessThan) || (this.qualifierOperator
						.equals(QualifierOperatorLessThanOrEqualTo))) ? "MAX("
						: "MIN(")
				+ this.keyPath
				+ ") WHERE "
				+ this.keyPath
				+ ((this.qualifierOperator.equals(QualifierOperatorLessThan) || (this.qualifierOperator
						.equals(QualifierOperatorLessThanOrEqualTo))) ? " <"
						: " >")
				+ ((this.qualifierOperator
						.equals(QualifierOperatorLessThanOrEqualTo) || (this.qualifierOperator
						.equals(QualifierOperatorGreaterThanOrEqualTo))) ? "= "
						: " ")
				+ value
				+ ((this.subQualifier != null) ? (" AND " + this.subQualifier
						.toString()) : "")
				+ ")"
				+ ((this.matchQualifier != null) ? (" AND " + this.matchQualifier
						.toString())
						: "");
	}
	
	
	
	// Conformance with NSCoding
	
	public Class classForCoder()
	{
		return getClass();
	}
	
	
	public static Object decodeObject(NSCoder coder)
	{
		return new BestRelationshipMatchesQualifier((String) coder
				.decodeObject(), coder.decodeObject(),
				EOQualifier.operatorSelectorForSelectorNamed((String) coder
						.decodeObject()), (EOQualifier) coder.decodeObject(),
				(EOQualifier) coder.decodeObject());
	}
	
	
	public void encodeWithCoder(NSCoder coder)
	{
		coder.encodeObject(keyPath());
		coder.encodeObject(limit());
		coder.encodeObject(qualifierOperator().name());
		coder.encodeObject(subQualifier());
		coder.encodeObject(matchQualifier());
	}
	
	
	
	// Conformance with KeyValueCodingArchiving
	
	public void encodeWithKeyValueArchiver(EOKeyValueArchiver keyValueArchiver)
	{
		keyValueArchiver.encodeObject(keyPath(), KEY_PATH);
		keyValueArchiver.encodeObject(limit(), LIMIT);
		keyValueArchiver.encodeObject(qualifierOperator().name(),
				QUALIFIER_OPERATOR);
		keyValueArchiver.encodeObject(subQualifier(), SUB_QUALIFIER);
		keyValueArchiver.encodeObject(matchQualifier(), MATCH_QUALIFIER);
	}
	
	
	public static Object decodeWithKeyValueUnarchiver(
			EOKeyValueUnarchiver keyValueUnarchiver)
	{
		return new BestRelationshipMatchesQualifier((String) keyValueUnarchiver
				.decodeObjectForKey(KEY_PATH), keyValueUnarchiver
				.decodeObjectForKey(LIMIT), EOQualifier
				.operatorSelectorForSelectorNamed((String) keyValueUnarchiver
						.decodeObjectForKey(QUALIFIER_OPERATOR)),
				(EOQualifier) keyValueUnarchiver
						.decodeObjectForKey(SUB_QUALIFIER),
				(EOQualifier) keyValueUnarchiver
						.decodeObjectForKey(MATCH_QUALIFIER));
	}
	
	
	
	// Private instance methods
	
	private void setLimit(Object limit)
	{
		this.limit = limit;
	}
}