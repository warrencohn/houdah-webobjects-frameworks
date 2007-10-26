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

import com.houdah.eocontrol.qualifiers.ExistsInRelationshipQualifier;
import com.houdah.eocontrol.qualifiers.InSubqueryQualifier;

import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOJoin;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eoaccess.EOSQLExpression;
import com.webobjects.eoaccess.EOQualifierSQLGeneration.Support;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * Support class to handle SQL generation for the ExistsInRelationshipQualifier
 * instances.
 * 
 * @author bernard
 */
public class ExistsInRelationshipQualifierSupport extends
		QualifierGenerationSupport
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
		ExistsInRelationshipQualifier eirQualifier = (ExistsInRelationshipQualifier) qualifier;
		String keyPath = eirQualifier.keyPath();
		EOEntity entity = expression.entity();
		EORelationship relationship = relationshipForPath(entity, keyPath);
		NSArray joins = relationship.joins();
		int joinCount = joins.count();
		EOEntity subEntity = relationship.destinationEntity();
		EOQualifier subQualifier = eirQualifier.qualifier();
		StringBuffer subBuffer = new StringBuffer();
		EOSQLExpression subExpression = expressionForEntity(subEntity);
		EOFetchSpecification subFetch = new EOFetchSpecification(subEntity
				.name(), subQualifier, null);
		StringBuffer buffer = new StringBuffer();
		int minCount = eirQualifier.minCount();
		
		if (minCount > 1) {
			buffer.append(" ");
			buffer.append(eirQualifier.minCount());
			buffer.append(" <= ");
			
			subBuffer.append(" (SELECT count(*) FROM ");
		} else {
			buffer.append(" EXISTS (");
			
			subBuffer.append("SELECT 1 FROM ");
		}
		
		subExpression.prepareSelectExpressionWithAttributes(subEntity
				.primaryKeyAttributes(), false, subFetch);
		
		subBuffer.append(subExpression.tableListWithRootEntity(subEntity));
		subBuffer.append(" WHERE ");
		subBuffer.append(subExpression.whereClauseString());
		
		if ((subExpression.joinClauseString() != null)
				&& (subExpression.joinClauseString().length() > 0)) {
			subBuffer.append(" AND ");
			subBuffer.append(subExpression.joinClauseString());
		}
		
		replaceTableAliasesInExpressionBuffer(subBuffer, subExpression);
		
		if (joinCount > 0) {
			for (int j = 0; j < joinCount; j++) {
				EOJoin join = (EOJoin) joins.objectAtIndex(j);
				EOAttribute sourceAttribute = join.sourceAttribute();
				EOAttribute destinationAttribute = join.destinationAttribute();
				
				subBuffer.append(" AND ");
				subBuffer.append(expression
						._aliasForRelatedAttributeRelationshipPath(
								sourceAttribute, ""));
				subBuffer.append(" = ");
				subBuffer.append(subExpression
						._aliasForRelatedAttributeRelationshipPath(
								destinationAttribute, ""));
			}
		}
		
		buffer.append(subBuffer);
		buffer.append(")");
		
		Enumeration bindVariables = subExpression.bindVariableDictionaries()
				.objectEnumerator();
		
		while (bindVariables.hasMoreElements()) {
			expression.addBindVariableDictionary((NSDictionary) bindVariables
					.nextElement());
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
		NSMutableDictionary substitutions = new NSMutableDictionary(1);
		ExistsInRelationshipQualifier eirQualifier = (ExistsInRelationshipQualifier) qualifier;
		String keyPath = eirQualifier.keyPath();
		EORelationship relationship = relationshipForPath(entity, keyPath);
		
		if ((relationship != null) && relationship.isFlattened()) {
            NSArray componentRelationships = relationship.componentRelationships();
			int crCount = componentRelationships.count();

            int relationshipIndex = keyPath.indexOf(relationship.name());
            String prefix = keyPath.substring(0, relationshipIndex - 1);
            StringBuffer pathToPivot = new StringBuffer(prefix);
			StringBuffer pathFromPivot = new StringBuffer();
			
			StringBuffer path = pathToPivot;
			EOEntity subEntity = null;
			
			for (int cr = 0; cr < crCount; cr++) {
				EORelationship component = (EORelationship) componentRelationships
						.objectAtIndex(cr);
				
				if (path.length() > 0) {
					path.append(NSKeyValueCodingAdditions.KeyPathSeparator);
				}
				
				path.append(component.name());
				
				if ((subEntity == null) && component.isToMany()) {
					path = pathFromPivot;
					subEntity = component.destinationEntity();
				}
			}
			
			substitutions.setObjectForKey(pathToPivot.toString(),
					ExistsInRelationshipQualifier.KEY_PATH);
			
			InSubqueryQualifier isQualifier = new InSubqueryQualifier(
					pathFromPivot.toString(), eirQualifier.qualifier());
			Support support = supportForClass(isQualifier.getClass());
			
			if (support == null) {
				throw new IllegalArgumentException("Qualifier " + isQualifier
						+ " has no support class");
			} else {
				substitutions.setObjectForKey(support
						.schemaBasedQualifierWithRootEntity(isQualifier,
								subEntity),
						ExistsInRelationshipQualifier.QUALIFIER);
			}
		} else {
			EOEntity subEntity = (relationship != null) ? relationship
					.destinationEntity() : entity;
			EOQualifier subQualifier = eirQualifier.qualifier();
			
			if (subQualifier != null) {
				Support support = supportForClass(subQualifier.getClass());
				
				substitutions.setObjectForKey(support
						.schemaBasedQualifierWithRootEntity(subQualifier,
								subEntity),
						ExistsInRelationshipQualifier.QUALIFIER);
			}
		}
		
		return eirQualifier.clone(substitutions);
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
		ExistsInRelationshipQualifier eirQualifier = (ExistsInRelationshipQualifier) qualifier;
		NSMutableDictionary substitutions = new NSMutableDictionary(1);
		
		substitutions.setObjectForKey(translateKeyAcrossRelationshipPath(
				eirQualifier.keyPath(), relationshipPath, entity),
				ExistsInRelationshipQualifier.KEY_PATH);
		
		return eirQualifier.clone(substitutions);
	}
}