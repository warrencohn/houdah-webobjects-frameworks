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

package com.houdah.eoaccess.qualifiers;

import java.util.Enumeration;
import java.util.StringTokenizer;

import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOOrQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSMutableArray;

/**
 * Repository class for utility methods relative to EOQualifiers
 * 
 * @author bernard
 */
public class QualifierUtilities
{
	// Constructor
	
	/**
	 * Designated constructor
	 */
	private QualifierUtilities()
	{
		throw new IllegalStateException("Do not instantiate this utility class");
	}
	
	
	
	// Public class methods
	
	/**
	 * @param qualifier
	 * @return NSArray of EOQualifiers which are contained in the given
	 *         qualifier.
	 */
	public static NSArray andQualifiers(EOQualifier qualifier)
	{
		return andQualifiers(qualifier, new NSMutableArray());
	}
	
	
	
	/**
	 * @param qualifier
	 * @return NSArray of EOQualifiers which are contained in the given
	 *         qualifier.
	 */
	public static NSArray allQualifiers(EOQualifier qualifier)
	{
		return allQualifiers(qualifier, new NSMutableArray());
	}
	
	
	public static EORelationship relationshipForPath(EOEntity entity,
			String keyPath)
	{
		if (keyPath != null) {
			StringTokenizer tokenizer = new StringTokenizer(keyPath,
					NSKeyValueCodingAdditions.KeyPathSeparator);
			EORelationship relationship = null;
			
			while (tokenizer.hasMoreElements()) {
				String key = tokenizer.nextToken();
				
				relationship = entity.anyRelationshipNamed(key);
				
				if (relationship != null) {
					entity = relationship.destinationEntity();
				} else {
					return null;
				}
			}
			
			return relationship;
		}
		
		return null;
	}
	
	
	
	// Private class methods
	
	private static NSMutableArray andQualifiers(EOQualifier qualifier,
			NSMutableArray quals)
	{
		if (qualifier instanceof EOAndQualifier) {
			EOAndQualifier andQual = (EOAndQualifier) qualifier;
			for (Enumeration e = andQual.qualifiers().objectEnumerator(); e
					.hasMoreElements();) {
				EOQualifier qual = (EOQualifier) e.nextElement();
				andQualifiers(qual, quals);
			}
		} else {
			quals.addObject(qualifier);
		}
		
		return quals;
	}
	
	
	private static NSMutableArray allQualifiers(EOQualifier qualifier,
			NSMutableArray quals)
	{
		if (qualifier instanceof EOAndQualifier
				|| qualifier instanceof EOOrQualifier) {
			NSArray subQuals;
			
			if (qualifier instanceof EOAndQualifier) {
				subQuals = ((EOAndQualifier) qualifier).qualifiers();
			} else {
				subQuals = ((EOOrQualifier) qualifier).qualifiers();
			}
			
			for (Enumeration e = subQuals.objectEnumerator(); e
					.hasMoreElements();) {
				EOQualifier qual = (EOQualifier) e.nextElement();
				allQualifiers(qual, quals);
			}
		} else {
			quals.addObject(qualifier);
		}
		
		return quals;
	}
}