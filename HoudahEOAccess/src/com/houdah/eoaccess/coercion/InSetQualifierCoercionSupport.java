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

package com.houdah.eoaccess.coercion;

import java.util.Enumeration;

import com.houdah.eocontrol.qualifiers.InSetQualifier;

import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSMutableSet;

/**
 * Support class to apply qualifier attribute coercion to instances of
 * InSetQualifier.
 * 
 * @author bernard
 */
public class InSetQualifierCoercionSupport implements
		QualifierAttributeCoercion.Support
{
	/**
	 * Applies value coercion to values passed to the supplied qualifier.<br/>
	 * 
	 * @param qualifier
	 *            the qualifier to validate
	 * @param entity
	 *            the entity this qualifier is to be applied to
	 * @param the
	 *            updated qualifier - may share branches with the original
	 *            qualifier
	 */
	public EOQualifier coerceQualifierAttributes(EOQualifier qualifier,
			EOEntity entity)
	{
		InSetQualifier isQualifier = (InSetQualifier) qualifier;
		String keyPath = isQualifier.keyPath();
		EOAttribute attribute = null;
		
		if (keyPath.indexOf(NSKeyValueCodingAdditions.KeyPathSeparator) > -1) {
			NSArray keyArray = NSArray.componentsSeparatedByString(keyPath,
					NSKeyValueCodingAdditions.KeyPathSeparator);
			int limit = keyArray.count() - 1;
			EOEntity tmpEntity = entity;
			
			for (int i = 0; i < limit; i++) {
				EORelationship relationship = tmpEntity
						.anyRelationshipNamed((String) keyArray
								.objectAtIndex(i));
				
				if (relationship != null) {
					tmpEntity = relationship.destinationEntity();
				} else {
					tmpEntity = null;
					break;
				}
			}
			
			if (tmpEntity != null) {
				attribute = entity.attributeNamed((String) keyArray
						.objectAtIndex(limit));
			}
		} else {
			attribute = entity.attributeNamed(keyPath);
		}
		
		if (attribute != null) {
			Enumeration values = isQualifier.values().objectEnumerator();
			NSMutableSet coercedValues = new NSMutableSet();
			
			while (values.hasMoreElements()) {
				coercedValues.addObject(attribute.validateValue(values
						.nextElement()));
			}
			
			return new InSetQualifier(keyPath, coercedValues);
		} else {
			return qualifier;
		}
	}
}
