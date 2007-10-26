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

import com.houdah.eocontrol.qualifiers.InSubqueryQualifier;
import com.houdah.eocontrol.qualifiers.Qualifier;

import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOJoin;
import com.webobjects.eoaccess.EOQualifierSQLGeneration;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eoaccess.EOSQLExpression;
import com.webobjects.eocontrol.EOClassDescription;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableSet;

/**
 * Support class to handle SQL generation for InSubqueryQualifier instances.<br/>
 * 
 * @author bernard
 */
public class InSubqueryQualifierSupport extends QualifierGenerationSupport
{
	// Static initializer
	
	static {
		setSupportForClass(new ArrayInSubqueryQualifierSupport(),
				ArrayInSubqueryQualifier.class);
	}
	
	
	
	
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
		InSubqueryQualifier isQualifier = (InSubqueryQualifier) qualifier;
		ArrayInSubqueryQualifier aisQualifier = new ArrayInSubqueryQualifier(
				new NSArray(isQualifier.keyPath()), isQualifier.entityName(),
				new NSArray(isQualifier.attributePath()), isQualifier
						.subQualifier());
		EOQualifierSQLGeneration.Support support = supportForClass(ArrayInSubqueryQualifier.class);
		
		return support.sqlStringForSQLExpression(aisQualifier, expression);
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
		InSubqueryQualifier isQualifier = (InSubqueryQualifier) qualifier;
		String keyPath = isQualifier.keyPath();
		
		if (isQualifier.entityName() == null) {
			EORelationship relationship = relationshipForPath(entity, keyPath);
			
			if (relationship.isFlattened()) {
				relationship = (EORelationship) relationship
						.componentRelationships().lastObject();
			}
			
			EOEntity destEntity = relationship.destinationEntity();
			NSArray joins = relationship.joins();
			int joinCount = joins.count();
			NSMutableArray srcAttributes = new NSMutableArray(joinCount);
			NSMutableArray destAttrNames = new NSMutableArray(joinCount);
			String path = Qualifier.allButLastPathComponent(keyPath);
			
			for (int i = joinCount - 1; i >= 0; i--) {
				EOJoin join = (EOJoin) joins.objectAtIndex(i);
				String srcAttributeName = join.sourceAttribute().name();
				String destAttributeName = join.destinationAttribute().name();
				
				srcAttributes.addObject(optimizeQualifierKeyPath(entity, path,
						srcAttributeName));
				destAttrNames.addObject(destAttributeName);
			}
			
			EOQualifier subQualifier = isQualifier.subQualifier();
			EOQualifierSQLGeneration.Support support = supportForClass(subQualifier
					.getClass());
			EOQualifier schemaBasedSubQualifier = support
					.schemaBasedQualifierWithRootEntity(subQualifier,
							destEntity);
			
			return new ArrayInSubqueryQualifier(srcAttributes, destEntity
					.name(), destAttrNames, schemaBasedSubQualifier);
		} else {
			EOEntity destEntity = entity.model().modelGroup().entityNamed(
					isQualifier.entityName());
			String attributePath = isQualifier.attributePath();
			
			EOQualifier subQualifier = isQualifier.subQualifier();
			EOQualifierSQLGeneration.Support support = supportForClass(subQualifier
					.getClass());
			EOQualifier schemaBasedSubQualifier = support
					.schemaBasedQualifierWithRootEntity(subQualifier,
							destEntity);
			
			return new ArrayInSubqueryQualifier(new NSArray(
					optimizeQualifierKeyPath(entity, Qualifier
							.allButLastPathComponent(keyPath), Qualifier
							.lastPathComponent(keyPath))), isQualifier
					.entityName(), new NSArray(optimizeQualifierKeyPath(
					destEntity, Qualifier
							.allButLastPathComponent(attributePath), Qualifier
							.lastPathComponent(attributePath))),
					schemaBasedSubQualifier);
		}
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
		InSubqueryQualifier isQualifier = (InSubqueryQualifier) qualifier;
		
		return new InSubqueryQualifier(translateKeyAcrossRelationshipPath(
				isQualifier.keyPath(), relationshipPath, entity), isQualifier
				.entityName(), isQualifier.attributePath(), isQualifier
				.subQualifier());
	}
	
	
	
	
	// Inner class
	
	protected static class ArrayInSubqueryQualifier extends Qualifier
	{
		// Private class constants
		
		private static final long	serialVersionUID	= -3169293747997906907L;
		
		
		
		// Private instance variables
		
		private NSArray				keyPathArray;
		
		
		private String				entityName;
		
		
		private NSArray				attributePaths;
		
		
		private EOQualifier			subQualifier;
		
		
		
		
		// Constructor
		
		public ArrayInSubqueryQualifier(NSArray keyPathArray,
				String entityName, NSArray attributePaths,
				EOQualifier subQualifier)
		{
			setKeyPathArray(keyPathArray);
			setEntityName(entityName);
			setAttributePaths(attributePaths);
			setSubQualifier(subQualifier);
		}
		
		
		
		// Public instance methods
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see com.webobjects.eocontrol.EOQualifier#qualifierWithBindings(com.webobjects.foundation.NSDictionary,
		 *      boolean)
		 */
		public EOQualifier qualifierWithBindings(NSDictionary bindings,
				boolean requiresAll)
		{
			EOQualifier qualifier = subQualifier();
			
			if (qualifier != null) {
				EOQualifier boundQualifier = qualifier.qualifierWithBindings(
						bindings, requiresAll);
				
				if (qualifier != boundQualifier) {
					return new ArrayInSubqueryQualifier(keyPathArray(),
							entityName(), attributePaths(), boundQualifier);
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
			Enumeration keys = keyPathArray().objectEnumerator();
			
			while (keys.hasMoreElements()) {
				Qualifier.validateKeyPathWithRootClassDescription((String) keys
						.nextElement(), classDescription);
			}
			
			if (subQualifier() != null) {
				EOClassDescription subDescription = EOClassDescription
						.classDescriptionForEntityName(entityName());
				
				subQualifier().validateKeysWithRootClassDescription(
						subDescription);
			}
		}
		
		
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see com.webobjects.eocontrol.EOQualifier#addQualifierKeysToSet(com.webobjects.foundation.NSMutableSet)
		 */
		public void addQualifierKeysToSet(NSMutableSet keySet)
		{
			keySet.addObjectsFromArray(keyPathArray());
			
			if (subQualifier() != null) {
				subQualifier().addQualifierKeysToSet(keySet);
			}
		}
		
		
		
		// Protected instance methods
		
		protected NSArray keyPathArray()
		{
			return this.keyPathArray;
		}
		
		
		protected void setKeyPathArray(NSArray keyPathArray)
		{
			this.keyPathArray = keyPathArray;
		}
		
		
		protected String entityName()
		{
			return this.entityName;
		}
		
		
		protected void setEntityName(String entityName)
		{
			this.entityName = entityName;
		}
		
		
		protected NSArray attributePaths()
		{
			return this.attributePaths;
		}
		
		
		protected void setAttributePaths(NSArray attributePaths)
		{
			this.attributePaths = attributePaths;
		}
		
		
		protected EOQualifier subQualifier()
		{
			return this.subQualifier;
		}
		
		
		protected void setSubQualifier(EOQualifier subQualifier)
		{
			this.subQualifier = subQualifier;
		}
	}
	
	
	public static class ArrayInSubqueryQualifierSupport extends
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
			ArrayInSubqueryQualifier aisQualifier = (ArrayInSubqueryQualifier) qualifier;
			EOEntity entity = expression.entity();
			NSArray keyPathArray = aisQualifier.keyPathArray();
			int keyPathCount = keyPathArray.count();
			boolean hasMultipleElements = (keyPathCount > 1);
			StringBuffer buffer = new StringBuffer();
			
			if (hasMultipleElements) {
				buffer.append("(");
			}
			
			for (int i = 0; i < keyPathCount; i++) {
				String keyPath = (String) keyPathArray.objectAtIndex(i);
				String attributeString = expression
						.sqlStringForAttributeNamed(keyPath);
				
				if (attributeString == null) {
					throw new IllegalStateException(
							"sqlStringForKeyValueQualifier: attempt to generate SQL for "
									+ aisQualifier.getClass().getName()
									+ " "
									+ aisQualifier
									+ " failed because attribute identified by key '"
									+ keyPath
									+ "' was not reachable from from entity '"
									+ entity.name() + "'");
				}
				
				if (i > 0) {
					buffer.append(", ");
				}
				
				buffer.append(expression.formatSQLString(attributeString,
						attributeForPath(entity, keyPath).readFormat()));
			}
			
			if (hasMultipleElements) {
				buffer.append(")");
			}
			
			buffer.append(" IN (");
			
			EOEntity subEntity = entity.model().modelGroup().entityNamed(
					aisQualifier.entityName());
			EOSQLExpression subExpression = expressionForEntity(subEntity);
			EOFetchSpecification subFetch = new EOFetchSpecification(subEntity
					.name(), aisQualifier.subQualifier(), null);
			NSArray attributePaths = aisQualifier.attributePaths();
			int aCount = attributePaths.count();
			NSMutableArray attributes = new NSMutableArray(aCount);
			
			for (int a = 0; a < aCount; a++) {
				attributes.addObject(attributeForPath(subEntity,
						(String) attributePaths.objectAtIndex(a)));
			}
			
			StringBuffer subBuffer = new StringBuffer();
			
			subExpression.prepareSelectExpressionWithAttributes(attributes,
					false, subFetch);
			
			subBuffer.append("SELECT ");
			subBuffer.append(subExpression.listString());
			subBuffer.append(" FROM ");
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
			buffer.append(subBuffer);
			buffer.append(")");
			Enumeration bindVariables = subExpression
					.bindVariableDictionaries().objectEnumerator();
			while (bindVariables.hasMoreElements()) {
				expression
						.addBindVariableDictionary((NSDictionary) bindVariables
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
			ArrayInSubqueryQualifier aisQualifier = (ArrayInSubqueryQualifier) qualifier;
			NSArray keyPathArray = aisQualifier.keyPathArray();
			int keyPathCount = keyPathArray.count();
			NSMutableArray migratedKeyPathArray = new NSMutableArray(
					keyPathCount);
			for (int i = 0; i < keyPathCount; i++) {
				String keyPath = (String) keyPathArray.objectAtIndex(i);
				migratedKeyPathArray
						.addObject(translateKeyAcrossRelationshipPath(keyPath,
								relationshipPath, entity));
			}
			
			return new ArrayInSubqueryQualifier(migratedKeyPathArray,
					aisQualifier.entityName(), aisQualifier.attributePaths(),
					aisQualifier.subQualifier());
		}
	}
}