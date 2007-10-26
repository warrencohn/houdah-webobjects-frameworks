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

package com.houdah.eoaccess.utilities;

import java.io.InputStream;
import java.math.BigDecimal;

import com.houdah.foundation.utilities.PropertyListUtilities;

import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOModel;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eoaccess.EOProperty;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCodingAdditions;

/**
 * Repository of utility methods for manipulating EOModel, EOEntity,
 * EOAttribute, ... objects.<br/>
 * 
 * @author bernard
 */
public class ModelUtilities
{
	// Constructor
	
	/**
	 * Designated constructor
	 */
	private ModelUtilities()
	{
		throw new IllegalStateException("Do not instantiate this utility class");
	}
	
	
	
	// Public class methods
	
	/**
	 * Determine is a given keyPath identifies a to-many relationship.
	 * 
	 * @param rootEntity
	 *            root entity for the keyPath
	 * @param keyPath
	 *            a relationship keyPath
	 * @return true if keyPath identifies a to-many relationship
	 * @throws IllegalArgumentException
	 *             if keyPath does not identify a valid relationship
	 */
	public static boolean relationshipPathIsToMany(final EOEntity rootEntity, final String keyPath)
	{
		EOEntity entity = rootEntity;
		NSArray elements = NSArray.componentsSeparatedByString(keyPath,
				NSKeyValueCodingAdditions.KeyPathSeparator);
		int eCount = elements.count();
		
		for (int e = 0; e < eCount; e++) {
			String relationshipName = (String) elements.objectAtIndex(e);
			EORelationship relationship = entity.anyRelationshipNamed(relationshipName);
			
			if (relationship == null) {
				throw new IllegalArgumentException("'" + relationshipName
						+ "' is not a valid relationship path on the entity :" + entity.name());
			}
			
			if (relationship.isToMany()) {
				return true;
			}
			
			entity = relationship.destinationEntity();
		}
		
		return false;
	}
	
	
	
	/**
	 * Follows a keyPath to find an attribute or a relationship.
	 * 
	 * @param rootEntity
	 *            root entity for the keyPath
	 * @param keyPath
	 *            a keyPath originating at rootEntity
	 * @return an instance of EOAttribute or EORelationship; null if keyPath is
	 *         not valid
	 */
	public static EOProperty propertyAtPath(final EOEntity rootEntity, final String keyPath)
	{
		EOEntity entity = rootEntity;
		NSArray elements = NSArray.componentsSeparatedByString(keyPath,
				NSKeyValueCodingAdditions.KeyPathSeparator);
		int eCount = elements.count();
		int eLast = eCount - 1;
		
		for (int e = 0; e < eCount; e++) {
			String propertyName = (String) elements.objectAtIndex(e);
			
			if (e < eLast) {
				EORelationship relationship = entity.anyRelationshipNamed(propertyName);
				
				if (relationship == null) {
					return null;
				}
				
				entity = relationship.destinationEntity();
			} else {
				EORelationship relationship = entity.anyRelationshipNamed(propertyName);
				
				if (relationship == null) {
					EOAttribute attribute = entity.anyAttributeNamed(propertyName);
					
					return attribute;
				} else {
					return relationship;
				}
			}
		}
		
		return null;
	}
	
	
	public static Number numberValueForAttribute(String value, EOAttribute attribute)
	{
		String valueType = attribute.valueType();
		
		if ((valueType != null) && (valueType.length() > 0)) {
			char valueTypeChar = valueType.charAt(0);
			
			switch (valueTypeChar) {
				
				case 'B':
					return new BigDecimal(value);
				case 'c':
					return Integer.valueOf(value);
				case 'd':
					return Double.valueOf(value);
				case 'f':
					return Float.valueOf(value);
				case 'i':
					return Integer.valueOf(value);
				case 'l':
					return Long.valueOf(value);
				case 's':
					return Short.valueOf(value);
			}
		}
		
		throw new IllegalArgumentException("numberValueForAttribute: unknown value type ("
				+ valueType + ")");
	}
	
	
	
	/**
	 * Attempts to set the connection dictionary for all available models to the
	 * one provided by an external source, e.g. a file.<br/>
	 * 
	 * @param dictionary
	 *            a dictionary in <code>.plist</code> format
	 * @return true if the dictionary was successfully loaded, false otherwise
	 * @see BFPropertyListTools
	 */
	public static boolean loadConnectionDictionary(NSDictionary dictionary)
	{
		EOModelGroup modelGroup;
		EOModel model;
		NSArray models;
		
		if (dictionary == null)
			return false;
		
		modelGroup = EOModelGroup.defaultGroup();
		models = modelGroup.models();
		
		if (models != null) {
			for (int i = 0; i < models.count(); i++) {
				model = (EOModel) models.objectAtIndex(i);
				model.setConnectionDictionary(dictionary);
			}
		}
		
		return true;
	}
	
	
	
	/**
	 * Attempts to set the connection dictionary for all available models to the
	 * one provided in the specified file.<br/>
	 * 
	 * @param inputStream
	 *            typically a stream that maps a <code>.plist</code> file
	 * @return true if the dictionary was successfully loaded, false otherwise
	 */
	public static boolean loadConnectionDictionaryFromInputStream(InputStream inputStream)
	{
		return loadConnectionDictionary(PropertyListUtilities
				.dictionaryFromInputStream(inputStream));
	}
}