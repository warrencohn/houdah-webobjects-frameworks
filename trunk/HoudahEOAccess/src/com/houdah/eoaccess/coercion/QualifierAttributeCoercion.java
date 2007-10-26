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

import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EONotQualifier;
import com.webobjects.eocontrol.EOOrQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * Qualifier attribute value coercion ensures that values referenced by
 * qualifiers match the data type specified in the model. E.g. a numeral string
 * may be replaced by a number.
 * 
 * @author bernard
 */
public class QualifierAttributeCoercion
{
	// Static initializer
	
	static {
		// Register support for standard qualifiers
		// Support for custom qualifiers is typically registered by the
		// principal class
		QualifierAttributeCoercion.registerSupportForClass(
				new EOKeyValueQualifierCoercionSupport(), EOKeyValueQualifier.class);
		QualifierAttributeCoercion.registerSupportForClass(new EONotQualifierCoercionSupport(),
				EONotQualifier.class);
		QualifierAttributeCoercion.registerSupportForClass(new EOAndQualifierCoercionSupport(),
				EOAndQualifier.class);
		QualifierAttributeCoercion.registerSupportForClass(new EOOrQualifierCoercionSupport(),
				EOOrQualifier.class);
	}
	
	
	
	// Proteced class variables
	
	protected static NSMutableDictionary	coercionSupportForQualifierClasses;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 */
	private QualifierAttributeCoercion()
	{
		throw new IllegalStateException("Do not instantiate this utility class");
	}
	
	
	
	// Public class methods
	
	/**
	 * Register a support class for a given qualifier class.
	 * 
	 * @param support
	 *            the support class instance
	 * @param qualifierClass
	 *            the class of qualifiers to register support for
	 */
	public static void registerSupportForClass(Support support, Class qualifierClass)
	{
		coercionSupportForQualifierClasses().setObjectForKey(support, qualifierClass);
	}
	
	
	
	/**
	 * Applies value coercion to values passed to qualifiers referenced by the
	 * fetch specification.<br/>
	 * 
	 * The fetch specification is MODIFIED by method.
	 * 
	 * @param fetchSpec
	 *            the fetch spec to validate
	 * @param the
	 *            MODIFIED fetch specification
	 */
	public static EOFetchSpecification coerceFetchSpecificationAttributes(
			final EOFetchSpecification fetchSpec)
	{
		EOQualifier qualifier = fetchSpec.qualifier();
		
		if (qualifier != null) {
			EOEntity entity = EOModelGroup.defaultGroup().entityNamed(fetchSpec.entityName());
			
			fetchSpec.setQualifier(coerceQualifierAttributes(qualifier, entity));
		}
		
		return fetchSpec;
	}
	
	
	
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
	public static EOQualifier coerceQualifierAttributes(final EOQualifier qualifier,
			final EOEntity entity)
	{
		Support support = (Support) coercionSupportForQualifierClasses().objectForKey(
				qualifier.getClass());
		
		if (support != null) {
			return support.coerceQualifierAttributes(qualifier, entity);
		} else {
			return qualifier;
		}
	}
	
	
	
	// Protected class methods
	
	protected static synchronized NSMutableDictionary coercionSupportForQualifierClasses()
	{
		if (QualifierAttributeCoercion.coercionSupportForQualifierClasses == null) {
			QualifierAttributeCoercion.coercionSupportForQualifierClasses = new NSMutableDictionary();
		}
		
		return QualifierAttributeCoercion.coercionSupportForQualifierClasses;
	}
	
	
	
	
	// Public inner interfaces
	
	/**
	 * Support class to apply qualifier attribute coercion to a given qualifier.
	 */
	public static interface Support
	{
		/**
		 * Applies value coercion to values passed to the supplied qualifier.<br/>
		 * 
		 * @param qualifier
		 *            the qualifier to validate
		 * @param entity
		 *            the entity this qualifier is to be applied to
		 * @return the updated qualifier - may share branches with the original
		 *         qualifier
		 */
		EOQualifier coerceQualifierAttributes(EOQualifier qualifier, EOEntity entity);
	}
	
	
	
	// Protected inner classes
	
	protected static class EOKeyValueQualifierCoercionSupport implements Support
	{
		public EOQualifier coerceQualifierAttributes(EOQualifier qualifier, EOEntity entity)
		{
			EOKeyValueQualifier kvQualifier = (EOKeyValueQualifier) qualifier;
			String keyPath = kvQualifier.key();
			EOAttribute attribute = null;
			
			if (keyPath.indexOf(NSKeyValueCodingAdditions.KeyPathSeparator) > -1) {
				NSArray keyArray = NSArray.componentsSeparatedByString(keyPath,
						NSKeyValueCodingAdditions.KeyPathSeparator);
				int limit = keyArray.count() - 1;
				EOEntity tmpEntity = entity;
				
				for (int i = 0; i < limit; i++) {
					EORelationship relationship = tmpEntity.anyRelationshipNamed((String) keyArray
							.objectAtIndex(i));
					
					if (relationship != null) {
						tmpEntity = relationship.destinationEntity();
					} else {
						tmpEntity = null;
						break;
					}
				}
				
				if (tmpEntity != null) {
					attribute = entity.attributeNamed((String) keyArray.objectAtIndex(limit));
				}
			} else {
				attribute = entity.attributeNamed(keyPath);
			}
			
			if (attribute != null) {
				return new EOKeyValueQualifier(keyPath, kvQualifier.selector(), attribute
						.validateValue(kvQualifier.value()));
			} else {
				return qualifier;
			}
		}
	}
	
	
	protected static class EONotQualifierCoercionSupport implements Support
	{
		public EOQualifier coerceQualifierAttributes(EOQualifier qualifier, EOEntity entity)
		{
			EONotQualifier notQualifier = (EONotQualifier) qualifier;
			
			return new EONotQualifier(QualifierAttributeCoercion.coerceQualifierAttributes(
					notQualifier.qualifier(), entity));
		}
	}
	
	
	protected static class EOOrQualifierCoercionSupport implements Support
	{
		public EOQualifier coerceQualifierAttributes(EOQualifier qualifier, EOEntity entity)
		{
			NSArray qualifierBranch = ((EOOrQualifier) qualifier).qualifiers();
			int count = qualifierBranch.count();
			NSMutableArray newBranch = new NSMutableArray(count);
			
			for (int i = 0; i < count; i++) {
				newBranch.addObject(QualifierAttributeCoercion.coerceQualifierAttributes(
						(EOQualifier) qualifierBranch.objectAtIndex(i), entity));
			}
			
			return new EOOrQualifier(newBranch);
		}
	}
	
	
	protected static class EOAndQualifierCoercionSupport implements Support
	{
		public EOQualifier coerceQualifierAttributes(EOQualifier qualifier, EOEntity entity)
		{
			NSArray qualifierBranch = ((EOAndQualifier) qualifier).qualifiers();
			int count = qualifierBranch.count();
			NSMutableArray newBranch = new NSMutableArray(count);
			
			for (int i = 0; i < count; i++) {
				newBranch.addObject(QualifierAttributeCoercion.coerceQualifierAttributes(
						(EOQualifier) qualifierBranch.objectAtIndex(i), entity));
			}
			
			return new EOAndQualifier(newBranch);
		}
	}
}