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

import com.houdah.eocontrol.qualifiers.BestRelationshipMatchesQualifier;
import com.houdah.eocontrol.qualifiers.Qualifier;

import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOJoin;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eoaccess.EOSQLExpression;
import com.webobjects.eoaccess.EOQualifierSQLGeneration.Support;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOQualifierVariable;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * Support class to handle SQL generation for the
 * BestRelationshipMatchesQualifier instances.
 * 
 * @author bernard
 */
public class BestRelationshipMatchesQualifierSupport extends
		QualifierGenerationSupport
{
	// Public instance methods
	
	public String sqlStringForSQLExpression(EOQualifier qualifier,
			EOSQLExpression expression)
	{
		BestRelationshipMatchesQualifier brmQualifier = (BestRelationshipMatchesQualifier) qualifier;
		String keyPath = brmQualifier.keyPath();
		EOEntity entity = expression.entity();
		EOAttribute attribute = attributeForPath(entity, keyPath);
		String path = Qualifier.allButLastPathComponent(keyPath);
		String key = Qualifier.lastPathComponent(keyPath);
		EORelationship relationship = relationshipForPath(entity, path);
		
		if (relationship == null) {
			throw new IllegalStateException(
					"sqlStringForKeyValueQualifier: attempt to generate SQL for "
							+ brmQualifier
							+ " failed because relationship identified by key path '"
							+ path + "' was not reachable from from entity '"
							+ entity.name() + "'");
		}
		
		EOEntity subEntity = relationship.destinationEntity();
		
		Object limit = brmQualifier.limit();
		
		if (limit instanceof EOQualifierVariable) {
			throw new IllegalStateException(
					"sqlStringForKeyValueQualifier: attempt to generate SQL for "
							+ brmQualifier.getClass().getName() + " "
							+ brmQualifier
							+ " failed because the qualifier variable '$"
							+ ((EOQualifierVariable) limit).key()
							+ "' is unbound.");
		}
		
		StringBuffer buffer = new StringBuffer();
		StringBuffer wrapperBuffer = new StringBuffer();
		NSArray joins = relationship.joins();
		int joinCount = joins.count();
		EOSQLExpression wrapperExpression = expressionForEntity(subEntity);
		EOSQLExpression subExpression = expressionForEntity(subEntity);
		EOQualifier limitQualifier = new EOKeyValueQualifier(key, brmQualifier
				.qualifierOperator(), limit);
		EOQualifier subQualifier = (brmQualifier.subQualifier() != null) ? new EOAndQualifier(
				new NSArray(new EOQualifier[] { brmQualifier.subQualifier(),
						limitQualifier }))
				: limitQualifier;
		EOFetchSpecification subFetch = new EOFetchSpecification(subEntity
				.name(), subQualifier, null);
		EOQualifier wrapperQualifier = (brmQualifier.subQualifier() != null) ? new EOAndQualifier(
				new NSArray(new EOQualifier[] { brmQualifier.subQualifier(),
						brmQualifier.matchQualifier() }))
				: brmQualifier.matchQualifier();
		EOFetchSpecification wrapperFetch = new EOFetchSpecification(subEntity
				.name(), wrapperQualifier, null);
		
		buffer.append(" (");
		
		NSMutableArray joinAttributes = new NSMutableArray(joinCount);
		
		for (int i = 0; i < joinCount; i++) {
			EOJoin join = (EOJoin) joins.objectAtIndex(i);
			EOAttribute destinationAttribute = join.destinationAttribute();
			
			joinAttributes.addObject(destinationAttribute);
			
			if (i > 0) {
				buffer.append(", ");
			}
			
			buffer.append(expression._aliasForRelatedAttributeRelationshipPath(
					destinationAttribute, path));
		}
		
		if (joinCount > 0) {
			buffer.append(", ");
		}
		
		buffer.append(expression._aliasForRelatedAttributeRelationshipPath(
				attribute, path));
		buffer.append(") IN (");
		
		wrapperExpression.prepareSelectExpressionWithAttributes(joinAttributes
				.arrayByAddingObject(attribute), false, wrapperFetch);
		
		subExpression.prepareSelectExpressionWithAttributes(new NSArray(
				attribute), false, subFetch);
		
		wrapperBuffer.append("SELECT ");
		wrapperBuffer.append(wrapperExpression.listString());
		wrapperBuffer.append(" FROM ");
		wrapperBuffer.append(wrapperExpression
				.tableListWithRootEntity(subEntity));
		
		String wrapperExpressionAttributeString = wrapperExpression
				.sqlStringForAttributeNamed(key);
		
		if (wrapperExpressionAttributeString == null) {
			throw new IllegalStateException(
					"sqlStringForKeyValueQualifier: attempt to generate SQL for "
							+ brmQualifier
							+ " failed because attribute identified by key '"
							+ key + "' was not reachable from from entity '"
							+ subEntity + "'");
		}
		
		wrapperBuffer.append(" WHERE ");
		wrapperBuffer.append(wrapperExpressionAttributeString);
		wrapperBuffer.append(" = (");
		wrapperBuffer.append(subSqlStringForSQLExpression(brmQualifier,
				wrapperExpression, joins, subExpression));
		wrapperBuffer.append(") ");
		
		String wrapperWhereClause = wrapperExpression.whereClauseString();
		
		if ((wrapperWhereClause != null) && (wrapperWhereClause.length() > 0)) {
			wrapperBuffer.append(" AND ");
			wrapperBuffer.append(wrapperWhereClause);
		}
		
		if ((wrapperExpression.joinClauseString() != null)
				&& (wrapperExpression.joinClauseString().length() > 0)) {
			wrapperBuffer.append(" AND ");
			wrapperBuffer.append(wrapperExpression.joinClauseString());
		}
		
		replaceTableAliasesInExpressionBuffer(wrapperBuffer, wrapperExpression);
		
		buffer.append(wrapperBuffer);
		buffer.append(")");
		
		Enumeration subBindVariables = subExpression.bindVariableDictionaries()
				.objectEnumerator();
		
		while (subBindVariables.hasMoreElements()) {
			expression
					.addBindVariableDictionary((NSDictionary) subBindVariables
							.nextElement());
		}
		
		Enumeration wrapperBindVariables = wrapperExpression
				.bindVariableDictionaries().objectEnumerator();
		
		while (wrapperBindVariables.hasMoreElements()) {
			expression
					.addBindVariableDictionary((NSDictionary) wrapperBindVariables
							.nextElement());
		}
		
		return buffer.toString();
	}
	
	
	public EOQualifier schemaBasedQualifierWithRootEntity(
			EOQualifier qualifier, EOEntity entity)
	{
		BestRelationshipMatchesQualifier brmQualifier = (BestRelationshipMatchesQualifier) qualifier;
		String keyPath = brmQualifier.keyPath();
		
		if (relationshipForPath(entity, keyPath) == null) {
			NSMutableDictionary substitutions = new NSMutableDictionary(2);
			String key = brmQualifier.keyPath();
			String path = Qualifier.allButLastPathComponent(key);
			EORelationship relationship = relationshipForPath(entity, path);
			EOEntity subEntity = (relationship != null) ? relationship
					.destinationEntity() : entity;
			EOQualifier subQualifier = brmQualifier.subQualifier();
			EOQualifier matchQualifier = brmQualifier.matchQualifier();
			
			if (subQualifier != null) {
				Support support = supportForClass(subQualifier.getClass());
				
				substitutions.setObjectForKey(support
						.schemaBasedQualifierWithRootEntity(subQualifier,
								subEntity),
						BestRelationshipMatchesQualifier.SUB_QUALIFIER);
			}
			
			if (matchQualifier != null) {
				Support support = supportForClass(matchQualifier.getClass());
				
				substitutions.setObjectForKey(support
						.schemaBasedQualifierWithRootEntity(matchQualifier,
								subEntity),
						BestRelationshipMatchesQualifier.MATCH_QUALIFIER);
			}
			
			return brmQualifier.clone(substitutions);
		} else {
			throw new IllegalStateException(
					"The key path may not lead to a relationship. Comparison on joins makes no sense");
		}
	}
	
	
	public EOQualifier qualifierMigratedFromEntityRelationshipPath(
			EOQualifier qualifier, EOEntity entity, String relationshipPath)
	{
		BestRelationshipMatchesQualifier brmQualifier = (BestRelationshipMatchesQualifier) qualifier;
		
		return brmQualifier.clone(new NSDictionary(
				translateKeyAcrossRelationshipPath(brmQualifier.keyPath(),
						relationshipPath, entity),
				BestRelationshipMatchesQualifier.KEY_PATH));
	}
	
	
	
	// Protected instance methods
	
	protected String subSqlStringForSQLExpression(
			BestRelationshipMatchesQualifier brmQualifier,
			EOSQLExpression wrapperExpression, NSArray joins,
			EOSQLExpression subExpression)
	{
		EOEntity subEntity = subExpression.entity();
		StringBuffer subBuffer = new StringBuffer();
		
		if (brmQualifier.qualifierOperator().equals(
				EOQualifier.QualifierOperatorLessThan)
				|| brmQualifier.qualifierOperator().equals(
						EOQualifier.QualifierOperatorLessThanOrEqualTo)) {
			subBuffer.append("SELECT MAX");
		} else {
			subBuffer.append("SELECT MIN");
		}
		
		subBuffer.append("(");
		subBuffer.append(subExpression.listString());
		subBuffer.append(") FROM ");
		subBuffer.append(subExpression.tableListWithRootEntity(subEntity));
		
		boolean hasWhereClause = (subExpression.whereClauseString() != null)
				&& (subExpression.whereClauseString().length() > 0);
		
		if (hasWhereClause) {
			subBuffer.append(" WHERE ");
			subBuffer.append(subExpression.whereClauseString());
		}
		
		if ((subExpression.joinClauseString() != null)
				&& (subExpression.joinClauseString().length() > 0)) {
			if (hasWhereClause) {
				subBuffer.append(" AND ");
			}
			
			subBuffer.append(subExpression.joinClauseString());
		}
		
		replaceTableAliasesInExpressionBuffer(subBuffer, subExpression);
		
		int joinCount = joins.count();
		for (int j = 0; j < joinCount; j++) {
			EOJoin join = (EOJoin) joins.objectAtIndex(j);
			EOAttribute destinationAttribute = join.destinationAttribute();
			
			subBuffer.append(" AND ");
			subBuffer.append(wrapperExpression
					._aliasForRelatedAttributeRelationshipPath(
							destinationAttribute, ""));
			subBuffer.append(" = ");
			subBuffer.append(subExpression
					._aliasForRelatedAttributeRelationshipPath(
							destinationAttribute, ""));
		}
		
		return subBuffer.toString();
	}
}