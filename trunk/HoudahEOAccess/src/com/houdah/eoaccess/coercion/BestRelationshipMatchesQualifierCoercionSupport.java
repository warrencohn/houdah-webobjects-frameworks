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

import com.houdah.eoaccess.qualifiers.QualifierUtilities;
import com.houdah.eocontrol.qualifiers.BestRelationshipMatchesQualifier;
import com.houdah.eocontrol.qualifiers.Qualifier;

import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * Support class to apply qualifier attribute coercion to instances of
 * BestRelationshipMatchesQualifier.
 * 
 * @author bernard
 */
public class BestRelationshipMatchesQualifierCoercionSupport implements
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
		BestRelationshipMatchesQualifier brmQualifier = (BestRelationshipMatchesQualifier) qualifier;
		EOQualifier subQualifier = brmQualifier.subQualifier();
		EOQualifier matchQualifier = brmQualifier.matchQualifier();
		String path = Qualifier.allButLastPathComponent(brmQualifier.keyPath());
		EOEntity destinationEntity = entity;
		
		if (path.length() > 0) {
			EORelationship relationship = QualifierUtilities
					.relationshipForPath(entity, path);
			
			destinationEntity = relationship.destinationEntity();
		}
		
		if (destinationEntity != null) {
			NSMutableDictionary substitutions = new NSMutableDictionary(2);
			
			substitutions.setObjectForKey(
					QualifierAttributeCoercion.coerceQualifierAttributes(
							subQualifier, destinationEntity),
					BestRelationshipMatchesQualifier.SUB_QUALIFIER);
			substitutions.setObjectForKey(QualifierAttributeCoercion
					.coerceQualifierAttributes(matchQualifier,
							destinationEntity),
					BestRelationshipMatchesQualifier.MATCH_QUALIFIER);
			
			return brmQualifier.clone(substitutions);
		}
		
		return brmQualifier;
	}
}

