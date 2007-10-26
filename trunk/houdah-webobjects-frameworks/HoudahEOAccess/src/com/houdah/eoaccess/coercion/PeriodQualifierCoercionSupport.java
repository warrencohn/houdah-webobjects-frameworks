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

import com.houdah.eocontrol.qualifiers.PeriodQualifier;

import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOQualifierVariable;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCodingAdditions;

/**
 * Support class to apply qualifier attribute coercion to instances of
 * PeriodQualifier.
 * 
 * @author bernard
 */
public class PeriodQualifierCoercionSupport implements
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
		PeriodQualifier pQualifier = (PeriodQualifier) qualifier;
		String keyPath = pQualifier.keyPath();
		EOEntity periodEntity = entity;
		
		if (keyPath != null) {
			NSArray keyArray = NSArray.componentsSeparatedByString(keyPath,
					NSKeyValueCodingAdditions.KeyPathSeparator);
			
			int limit = keyArray.count() - 1;
			
			for (int i = 0; i < limit; i++) {
				EORelationship relationship = periodEntity
						.anyRelationshipNamed((String) keyArray
								.objectAtIndex(i));
				
				if (relationship != null) {
					entity = relationship.destinationEntity();
				} else {
					entity = null;
					break;
				}
			}
		}
		
		if (periodEntity != null) {
			EOAttribute yearAttribute = entity.attributeNamed(pQualifier
					.yearKey());
			Object yearValue = pQualifier.yearValue();
			
			if (!(yearValue instanceof EOQualifierVariable)) {
				yearValue = yearAttribute.validateValue(yearValue);
			}
			
			EOAttribute monthAttribute = entity.attributeNamed(pQualifier
					.monthKey());
			Object monthValue = pQualifier.monthValue();
			
			if (!(monthValue instanceof EOQualifierVariable)) {
				monthValue = monthAttribute.validateValue(monthValue);
			}
			
			return new PeriodQualifier(pQualifier.keyPath(), pQualifier
					.yearKey(), pQualifier.monthKey(), pQualifier
					.qualifierOperator(), yearValue, monthValue);
		}
		
		return qualifier;
	}
}