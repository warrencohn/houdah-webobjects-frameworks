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

import com.webobjects.eocontrol.EOClassDescription;
import com.webobjects.eocontrol.EOKeyValueArchiver;
import com.webobjects.eocontrol.EOKeyValueArchiving;
import com.webobjects.eocontrol.EOKeyValueUnarchiver;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOQualifierEvaluation;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSCoder;
import com.webobjects.foundation.NSCoding;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSMutableSet;

/**
 * Qualifier to check for the presence of objects matching a given qualifier in
 * a to-many relationship.<br/>
 * 
 * While in-memory qualification is supported, its use is not adviseable over
 * performance concers.
 * 
 * @author bernard
 */
public class ExistsInRelationshipQualifier extends Qualifier implements
		EOQualifierEvaluation, NSCoding, EOKeyValueArchiving, Cloneable
{
	// Private class constants
	
	private static final long	serialVersionUID	= 4708006332866881810L;
	
	
	
	// Public class constants
	
	/**
	 * Key value for the one argument clone method
	 */
	public static final String	KEY_PATH			= "keyPath",
			QUALIFIER = "qualifier", MIN_COUNT = "minCount";
	
	
	
	// Protected instance variables
	
	/**
	 * Path to the to-many relationship to search
	 */
	protected String			keyPath;
	
	
	
	/**
	 * Qualifier to narrow down on elements of the relationship to check for
	 */
	protected EOQualifier		qualifier;
	
	
	
	/**
	 * Minimum number of matches in the to-many for this qualifier to match
	 */
	protected int				minCount;
	
	
	
	
	// Constructors
	
	/**
	 * Constructor
	 * 
	 * @param keyPath
	 *            Path to the to-many relationship to search. Not null
	 * @param qualifier
	 *            Qualifier to narrow down on elements of the relationship to
	 *            check for
	 */
	public ExistsInRelationshipQualifier(String keyPath, EOQualifier qualifier)
	{
		this(keyPath, qualifier, 1);
	}
	
	
	
	/**
	 * Constructor
	 * 
	 * @param keyPath
	 *            Path to the to-many relationship to search. Not null
	 * @param qualifier
	 *            Qualifier to narrow down on elements of the relationship to
	 *            check for
	 * @param minCount
	 *            Minimum number ( > 0 )of matches in the to-many for this
	 *            qualifier to match
	 */
	public ExistsInRelationshipQualifier(String keyPath, EOQualifier qualifier,
			int minCount)
	{
		this.keyPath = keyPath;
		this.qualifier = qualifier;
		this.minCount = (minCount < 1) ? 1 : minCount;
	}
	
	
	
	// Public instance qualifier
	
	/**
	 * Path to the to-many relationship to search
	 * 
	 * @return the value as passed to the constructor
	 */
	public String keyPath()
	{
		return this.keyPath;
	}
	
	
	
	/**
	 * Qualifier to narrow down on elements of the relationship to check for
	 * 
	 * @return the value as passed to the constructor
	 */
	public EOQualifier qualifier()
	{
		return this.qualifier;
	}
	
	
	
	/**
	 * Minimum number of matches in the to-many for this qualifier to match
	 * 
	 * @return the value as passed to the constructor
	 */
	public int minCount()
	{
		return this.minCount;
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
		EOQualifier qualifier = qualifier();
		
		if (qualifier != null) {
			EOQualifier boundQualifier = qualifier.qualifierWithBindings(
					bindings, requiresAll);
			
			if (qualifier != boundQualifier) {
				NSDictionary substitutions = new NSDictionary(boundQualifier,
						QUALIFIER);
				
				return (ExistsInRelationshipQualifier) clone(substitutions);
			}
		}
		
		return this;
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eocontrol.EOQualifier#validateKeysWithRootClassDescription(com.webobjects.eocontrol.EOClassDescription)
	 */
	public void validateKeysWithRootClassDescription(
			EOClassDescription classDescription)
	{
		Qualifier.validateKeyPathWithRootClassDescription(keyPath(),
				classDescription);
		
		if (qualifier() != null) {
			String path = Qualifier.allButLastPathComponent(keyPath());
			EOClassDescription subDescription = classDescription
					.classDescriptionForKeyPath(path);
			
			qualifier().validateKeysWithRootClassDescription(subDescription);
		}
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eocontrol.EOQualifier#addQualifierKeysToSet(com.webobjects.foundation.NSMutableSet)
	 */
	public void addQualifierKeysToSet(NSMutableSet keySet)
	{
		keySet.addObject(keyPath());
		
		if (qualifier() != null) {
			NSMutableSet subKeySet = new NSMutableSet();
			
			qualifier().addQualifierKeysToSet(subKeySet);
			
			Enumeration subKeys = subKeySet.objectEnumerator();
			String prefix = keyPath()
					+ NSKeyValueCodingAdditions.KeyPathSeparator;
			
			while (subKeys.hasMoreElements()) {
				keySet.addObject(prefix + subKeys.nextElement());
			}
		}
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eocontrol.EOQualifierEvaluation#evaluateWithObject(java.lang.Object)
	 */
	public boolean evaluateWithObject(Object object)
	{
		NSArray values = (NSArray) NSKeyValueCodingAdditions.Utility
				.valueForKeyPath(object, keyPath());
		
		return EOQualifier.filteredArrayWithQualifier(values, qualifier())
				.count() >= this.minCount;
	}
	
	
	public Object clone()
	{
		ExistsInRelationshipQualifier clone = new ExistsInRelationshipQualifier(
				keyPath(), qualifier(), minCount());
		
		return clone;
	}
	
	
	public ExistsInRelationshipQualifier clone(NSDictionary newValues)
	{
		ExistsInRelationshipQualifier clone = (ExistsInRelationshipQualifier) clone();
		
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
		return "("
				+ keyPath()
				+ " CONTAINS at least "
				+ this.minCount
				+ ((qualifier() != null) ? (" match(es) for ("
						+ qualifier().toString() + ")") : " elements");
	}
	
	
	
	// Conformance with NSCoding
	
	public Class classForCoder()
	{
		return getClass();
	}
	
	
	public static Object decodeObject(NSCoder coder)
	{
		String keyPath = (String) coder.decodeObject();
		EOQualifier qualifier = (EOQualifier) coder.decodeObject();
		
		try {
			int minCount = coder.decodeInt();
			
			return new ExistsInRelationshipQualifier(keyPath, qualifier,
					minCount);
		} catch (Exception e) {
			return new ExistsInRelationshipQualifier(keyPath, qualifier);
		}
	}
	
	
	public void encodeWithCoder(NSCoder coder)
	{
		coder.encodeObject(keyPath());
		coder.encodeObject(qualifier());
		coder.encodeInt(minCount());
	}
	
	
	
	// Conformance with KeyValueCodingArchiving
	
	public void encodeWithKeyValueArchiver(EOKeyValueArchiver keyValueArchiver)
	{
		keyValueArchiver.encodeObject(keyPath(), KEY_PATH);
		keyValueArchiver.encodeObject(qualifier(), QUALIFIER);
		keyValueArchiver.encodeInt(minCount(), MIN_COUNT);
	}
	
	
	public static Object decodeWithKeyValueUnarchiver(
			EOKeyValueUnarchiver keyValueUnarchiver)
	{
		return new ExistsInRelationshipQualifier((String) keyValueUnarchiver
				.decodeObjectForKey(KEY_PATH), (EOQualifier) keyValueUnarchiver
				.decodeObjectForKey(QUALIFIER), (int) keyValueUnarchiver
				.decodeIntForKey(MIN_COUNT));
	}
}