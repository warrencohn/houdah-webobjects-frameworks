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
import java.util.StringTokenizer;

import com.webobjects.eocontrol.EOClassDescription;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOQualifierEvaluation;
import com.webobjects.eocontrol.EOQualifierVariable;

import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSMutableSet;
import com.webobjects.foundation.NSSet;

/**
 * Qualifier to match objects according to the values in a provided list.<br/>
 * <br/>
 * 
 * For an object to match, the value obtained by following the provided key path
 * must match at least on of the provided values.
 * 
 * @author bernard
 */
public class InSetQualifier extends Qualifier implements Cloneable,
		EOQualifierEvaluation
{
	// Private class constants
	
	private static final long	serialVersionUID	= -557032633819654230L;
	
	
	
	// Protected instance variables
	
	protected String			keyPath;
	
	protected NSSet				values;
	
	
	
	
	// Constructors
	
	/**
	 * Constructor.<br/>
	 * <br/>
	 * 
	 * @param keyPath
	 *            passed to the setKeyPath method
	 * @param values
	 *            passed to the the setValues method
	 * @throws IllegalArgumentException
	 *             as defined by the setter methods
	 * @see #setKeyPath(String)
	 * @see #setValues(NSSet)
	 */
	public InSetQualifier(String keyPath, NSSet values)
	{
		setKeyPath(keyPath);
		setValues(values);
	}
	
	
	
	// Public instance methods
	
	/**
	 * The path to the value to match against the specified set.
	 * 
	 * @return the key path as specified by the matching setter method
	 */
	public String keyPath()
	{
		return this.keyPath;
	}
	
	
	
	/**
	 * Sets the path to the value to match against the specified set.
	 * 
	 * @param keyPath
	 *            the path. May not be null.
	 * @throws IllegalArgumentException
	 *             if the path is null
	 */
	public void setKeyPath(String keyPath)
	{
		if (keyPath == null) {
			throw new IllegalArgumentException("The key path must not be null");
		}
		
		this.keyPath = keyPath;
	}
	
	
	
	/**
	 * The values to match against.
	 * 
	 * @return the set of values as specified using the setter method
	 */
	public NSSet values()
	{
		return this.values;
	}
	
	
	
	/**
	 * Sets the values to match against.
	 * 
	 * @param values
	 *            the values to match against. May not be null. May not contain
	 *            enterprise objects
	 * @throws IllegalArgumentException
	 *             if the argument is null or contains enterprise objects
	 */
	public void setValues(NSSet values)
	{
		if (values == null) {
			throw new IllegalArgumentException(
					"The values array must not be null");
		}
		
		this.values = values;
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eocontrol.EOQualifierEvaluation#evaluateWithObject(java.lang.Object)
	 */
	public boolean evaluateWithObject(Object object)
	{
		Object value = NSKeyValueCodingAdditions.Utility.valueForKeyPath(
				object, keyPath());
		
		if (value == null) {
			value = NSKeyValueCoding.NullValue;
		}
		
		return values().containsObject(value);
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
		Enumeration e = values().objectEnumerator();
		boolean didSubstitute = false;
		NSMutableSet valueSet = new NSMutableSet(values().count());
		
		while (e.hasMoreElements()) {
			Object object = e.nextElement();
			
			if (object instanceof EOQualifierVariable) {
				Object value = null;
				
				if (bindings != null) {
					value = bindings
							.valueForKeyPath(((EOQualifierVariable) object)
									.key());
				}
				
				if (value != null) {
					valueSet.addObject(value);
					didSubstitute = true;
					
					continue;
				}
				
				if (requiresAll) {
					throw new EOQualifier.QualifierVariableSubstitutionException(
							"Error in variable substitution: value for variable "
									+ object + " not found");
				}
			} else {
				valueSet.addObject(object);
			}
		}
		
		if (didSubstitute) {
			InSetQualifier clone = (InSetQualifier) clone();
			
			clone.setValues(valueSet);
			
			return clone;
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
			EOClassDescription classDescription)
	{
		StringTokenizer tokenizer = new StringTokenizer(keyPath(),
				KEY_PATH_SEPARATOR);
		
		while (tokenizer.hasMoreElements()) {
			String key = tokenizer.nextToken();
			
			if (tokenizer.hasMoreElements()) {
				classDescription = classDescription
						.classDescriptionForDestinationKey(key);
				
				if (classDescription == null) {
					throw new IllegalStateException("Invalid key '" + key
							+ "' found");
				}
			} else {
				if (!classDescription.attributeKeys().containsObject(key)) {
					throw new IllegalStateException("Invalid key '" + key
							+ "' found");
				}
			}
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
	}
	
	
	public Object clone()
	{
		return new InSetQualifier(keyPath(), values());
	}
	
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		Enumeration e = values().objectEnumerator();
		
		buffer.append("(");
		buffer.append(keyPath());
		buffer.append(" IN (");
		
		while (e.hasMoreElements()) {
			Object object = e.nextElement();
			
			if (object == NSKeyValueCoding.NullValue) {
				buffer.append("null");
			} else if (object instanceof Number) {
				buffer.append(object);
			} else if (object instanceof EOQualifierVariable) {
				buffer.append("$");
				buffer.append(((EOQualifierVariable) object).key());
			} else {
				buffer.append("'");
				buffer.append(object);
				buffer.append("'");
			}
			
			if (e.hasMoreElements()) {
				buffer.append(", ");
			}
		}
		
		buffer.append("))");
		
		return buffer.toString();
	}
}