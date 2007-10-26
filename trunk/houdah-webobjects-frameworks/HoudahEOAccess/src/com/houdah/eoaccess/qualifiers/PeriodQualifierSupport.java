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

import com.houdah.eocontrol.qualifiers.PeriodQualifier;

import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOSQLExpression;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * Support class to handle SQL generation for the PeriodQualifier instances.
 * 
 * @author bernard
 */
public class PeriodQualifierSupport extends QualifierGenerationSupport
{
	// Public instance methods
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eoaccess.EOQualifierSQLGeneration.Support#sqlStringForSQLExpression(com.webobjects.eocontrol.EOQualifier,
	 *      com.webobjects.eoaccess.EOSQLExpression)
	 */
	public String sqlStringForSQLExpression(EOQualifier qualifier,
			EOSQLExpression expression)
	{
		PeriodQualifier pQualifier = (PeriodQualifier) qualifier;
		
		Object rhs = pQualifier.period();
		
		EOEntity entity = expression.entity();
		String yearPath = pQualifier.yearPath();
		String sqlStringForYear = expression
				.sqlStringForAttributeNamed(yearPath);
		
		if (sqlStringForYear == null) {
			
			throw new IllegalStateException(
					"sqlStringForKeyValueQualifier: attempt to generate SQL for "
							+ pQualifier.getClass().getName() + " "
							+ pQualifier
							+ " failed because attribute identified by key '"
							+ yearPath
							+ "' was not reachable from from entity '"
							+ entity.name() + "'");
		}
		
		String monthPath = pQualifier.monthPath();
		String sqlStringForMonth = expression
				.sqlStringForAttributeNamed(monthPath);
		
		if (sqlStringForMonth == null) {
			throw new IllegalStateException(
					"sqlStringForKeyValueQualifier: attempt to generate SQL for "
							+ pQualifier.getClass().getName() + " "
							+ pQualifier
							+ " failed because attribute identified by key '"
							+ monthPath
							+ "' was not reachable from from entity '"
							+ entity.name() + "'");
		}
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(expression.formatSQLString(sqlStringForYear,
				attributeForPath(entity, yearPath).readFormat()));
		buffer.append(" * 100 + ");
		buffer.append(expression.formatSQLString(sqlStringForMonth,
				attributeForPath(entity, monthPath).readFormat()));
		buffer.append(" ");
		buffer.append(expression.sqlStringForSelector(pQualifier
				.qualifierOperator(), rhs));
		buffer.append(" ");
		
		
		// buffer.append((rhs != NSKeyValueCoding.NullValue) ? rhs : null);
		
		EOAttribute periodAttribute = new EOAttribute();
		
		periodAttribute.setName("period");
		periodAttribute.setClassName("java.lang.Number");
		periodAttribute.setValueType("i");
		
		if (expression.useBindVariables()) {
			NSMutableDictionary bindVariableDictionary = expression
					.bindVariableDictionaryForAttribute(periodAttribute, rhs);
			
			expression.addBindVariableDictionary(bindVariableDictionary);
			
			buffer.append(bindVariableDictionary
					.objectForKey(EOSQLExpression.BindVariablePlaceHolderKey));
		} else {
			buffer.append(expression.formatValueForAttribute(rhs,
					periodAttribute));
		}
		
		return buffer.toString();
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eoaccess.EOQualifierSQLGeneration.Support#schemaBasedQualifierWithRootEntity(com.webobjects.eocontrol.EOQualifier,
	 *      com.webobjects.eoaccess.EOEntity)
	 */
	public EOQualifier schemaBasedQualifierWithRootEntity(
			EOQualifier qualifier, EOEntity entity)
	{
		return qualifier;
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eoaccess.EOQualifierSQLGeneration.Support#qualifierMigratedFromEntityRelationshipPath(com.webobjects.eocontrol.EOQualifier,
	 *      com.webobjects.eoaccess.EOEntity, java.lang.String)
	 */
	public EOQualifier qualifierMigratedFromEntityRelationshipPath(
			EOQualifier qualifier, EOEntity entity, String relationshipPath)
	{
		PeriodQualifier pQualifier = (PeriodQualifier) qualifier;
		String yearPath = pQualifier.yearPath();
		String translatedYearPath = translateKeyAcrossRelationshipPath(
				yearPath, relationshipPath, entity);
		String keyPath = translatedYearPath.substring(0, translatedYearPath
				.lastIndexOf(NSKeyValueCodingAdditions.KeyPathSeparator));
		
		return new PeriodQualifier(keyPath, pQualifier.yearKey(), pQualifier
				.monthKey(), pQualifier.qualifierOperator(), pQualifier
				.yearValue(), pQualifier.monthValue());
	}
}