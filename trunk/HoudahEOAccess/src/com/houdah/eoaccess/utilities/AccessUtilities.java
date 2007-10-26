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

import com.houdah.eocontrol.qualifiers.FalseQualifier;

import com.webobjects.eoaccess.EOAdaptorChannel;
import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EODatabaseContext;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOGlobalID;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOObjectStoreCoordinator;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;

/**
 * Sister class of EOUtilities. Repository of tools for fetching objects.<br/>
 * 
 * @author Bernard
 */
public class AccessUtilities
{
	// Constructor
	
	/**
	 * Designated constructor
	 */
	private AccessUtilities()
	{
		throw new IllegalStateException("Do not instantiate this utility class");
	}
	
	
	
	// Public class methods
	
	/**
	 * Fetches single objects using a bound fetch specification. <br/>
	 * 
	 * @param editingContext
	 *            the context to fetch into
	 * @param fetchSpecification
	 *            the specification to use
	 * @return the first of the found objects, null if none is found
	 */
	public static EOEnterpriseObject objectWithBoundFetchSpecification(
			EOEditingContext editingContext,
			EOFetchSpecification fetchSpecification)
	{
		NSArray fetchResult;
		EOFetchSpecification fetch = (EOFetchSpecification) fetchSpecification
				.clone();
		
		fetch.setFetchLimit(1);
		fetchResult = editingContext.objectsWithFetchSpecification(fetch);
		
		if (fetchResult.count() > 0) {
			return (EOEnterpriseObject) fetchResult.objectAtIndex(0);
		} else {
			return null;
		}
	}
	
	
	
	/**
	 * Fetches and returns the enterprise object identified by the specified
	 * primary key value.<br/>
	 * For use only with enterprise objects that have non-compound primary keys.<br/>
	 * 
	 * Returns null if no matching object is found. I.e. no dummy fault is
	 * created.>BR>
	 * 
	 * This method is essentially meant to optimize performance in cases where
	 * the object is likely to be in cache. You would usually call this method
	 * on entities that are set to cache their objects. Access to known objects
	 * will not hit the database. A query for an object that is not yet known
	 * will however cause a database access.<br/>
	 * 
	 * The responsibility of locking the editing context is left to the caller.
	 * 
	 * @param editingContext
	 *            the context to fetch into
	 * @param entityName
	 *            the name of the object's entity
	 * @param pkValue
	 *            the primary key value to look up
	 * @return the requested object or fault, null if none is found
	 */
	public static EOEnterpriseObject faultForSupposedPrimaryKeyValue(
			EOEditingContext editingContext, String entityName, Object pkValue)
	{
		EOEntity entity = EOModelGroup.defaultGroup().entityNamed(entityName);
		
		return faultForSupposedPrimaryKeyValue(editingContext, entity, pkValue);
	}
	
	
	
	/**
	 * Fetches and returns the enterprise object identified by the specified
	 * primary key value.<br/>
	 * For use only with enterprise objects that have non-compound primary keys.<br/>
	 * 
	 * Returns null if no matching object is found. I.e. no dummy fault is
	 * created.>BR>
	 * 
	 * This method is essentially meant to optimize performance in cases where
	 * the object is likely to be in cache. You would usually call this method
	 * on entities that are set to cache their objects. Access to known objects
	 * will not hit the database. A query for an object that is not yet known
	 * will however cause a database access.<br/>
	 * 
	 * The responsibility of locking the editing context is left to the caller.
	 * 
	 * @param editingContext
	 *            the context to fetch into
	 * @param entity
	 *            the object's entity
	 * @param pkValue
	 *            the primary key value to look up
	 * @return the requested object or fault, null if none is found
	 */
	public static EOEnterpriseObject faultForSupposedPrimaryKeyValue(
			EOEditingContext editingContext, EOEntity entity, Object pkValue)
	{
		NSArray pkAttributes = entity.primaryKeyAttributes();
		
		if (pkAttributes.count() != 1) {
			throw new IllegalArgumentException(
					"faultForSupposedPrimaryKeyValue: Does not support entities with compound primary keys");
		} else {
			String primaryKey = ((EOAttribute) pkAttributes.objectAtIndex(0))
					.name();
			EOFetchSpecification fetchSpec = new EOFetchSpecification(entity
					.name(), null, null);
			EOGlobalID globalId = entity.globalIDForRow(new NSDictionary(
					pkValue, primaryKey));
			EOObjectStoreCoordinator coordinator = (EOObjectStoreCoordinator) editingContext
					.rootObjectStore();
			EODatabaseContext dbContext = (EODatabaseContext) coordinator
					.objectStoreForFetchSpecification(fetchSpec);
			NSDictionary snapshot = dbContext.snapshotForGlobalID(globalId);
			
			
			// If a snapshot exits we can safely construct a fault
			if (snapshot != null) {
				return editingContext
						.faultForGlobalID(globalId, editingContext);
			} else {
				EOQualifier qualifier = new EOKeyValueQualifier(primaryKey,
						EOQualifier.QualifierOperatorEqual, pkValue);
				
				
				// Qualify the fetch specification
				fetchSpec.setQualifier(qualifier);
				
				return objectWithBoundFetchSpecification(editingContext,
						fetchSpec);
			}
		}
	}
	
	
	
	/**
	 * Creates a fetch specification to fetch objects at the end of an object's
	 * relationship.
	 * 
	 * @param enterpriseObject
	 *            the source object
	 * @param key
	 *            the relationship name
	 * @return a fetch specification that may be used to retrieve the
	 *         relationship's values
	 */
	public static EOFetchSpecification fetchSpecificationForRelationshipWithKey(
			EOEnterpriseObject enterpriseObject, String key)
	{
		EOEntity entity = EOUtilities.entityForObject(enterpriseObject
				.editingContext(), enterpriseObject);
		EORelationship relationship = entity.relationshipNamed(key);
		EOEditingContext editingContext = enterpriseObject.editingContext();
		EOGlobalID globalID = editingContext
				.globalIDForObject(enterpriseObject);
		String aModelName = entity.model().name();
		EODatabaseContext databaseContext = EOUtilities
				.databaseContextForModelNamed(editingContext, aModelName);
		databaseContext.lock();
		
		NSDictionary row = null;
		
		try {
			row = databaseContext.snapshotForGlobalID(globalID);
		} finally {
			databaseContext.unlock();
		}
		
		EOQualifier qualifier = (row != null) ? relationship
				.qualifierWithSourceRow(row) : new FalseQualifier();
		EOEntity destinationEntity = relationship.destinationEntity();
		EOFetchSpecification fetchSpecification = new EOFetchSpecification(
				destinationEntity.name(), qualifier, null);
		
		fetchSpecification.setIsDeep(destinationEntity.isAbstractEntity());
		
		return fetchSpecification;
	}
	
	
	
	/**
	 * Find an available adaptor channel to handle a given fetch specification.
	 * 
	 * @param editingContext
	 *            the editing context identifying an EOF stack
	 * @param fetchSpecification
	 *            the fetch specification for which to find a channel
	 * @return an adaptor channel
	 */
	public static EOAdaptorChannel adaptorChannel(
			EOEditingContext editingContext,
			EOFetchSpecification fetchSpecification)
	{
		EOObjectStoreCoordinator osc = (EOObjectStoreCoordinator) editingContext
				.rootObjectStore();
		
		osc.lock();
		
		try {
			EODatabaseContext databaseContext = (EODatabaseContext) osc
					.objectStoreForFetchSpecification(fetchSpecification);
			EOAdaptorChannel adaptorChannel = databaseContext
					.availableChannel().adaptorChannel();
			
			return adaptorChannel;
		} finally {
			osc.unlock();
		}
	}
	
	
	
	/**
	 * Fetches values for a given set of attributes.
	 * 
	 * @param objectStoreCoordinator
	 *            the object store coordinator to use
	 * @param attributes
	 *            the list of EOAttributes
	 * @param fetchSpecification
	 *            the specification of the rows to match
	 * @param adaptorChannelDelegate
	 *            an optional delegate to use on the EOAdaptorChannel
	 * @return an array of NSDictionary indexed by attribute names
	 */
	public static NSArray fetchAttributes(
			EOObjectStoreCoordinator objectStoreCoordinator,
			NSArray attributes, EOFetchSpecification fetchSpecification,
			Object adaptorChannelDelegate)
	{
		objectStoreCoordinator.lock();
		
		try {
			EOModelGroup modelGroup = EOModelGroup
					.modelGroupForObjectStoreCoordinator(objectStoreCoordinator);
			EOEntity entity = modelGroup.entityNamed(fetchSpecification
					.entityName());
			EOQualifier qualifier = fetchSpecification.qualifier();
			EOQualifier schemaBasedQualifier = (qualifier != null) ? entity
					.schemaBasedQualifier(qualifier) : null;
			EOFetchSpecification schemaBasedFetchSpecification = new EOFetchSpecification(
					entity.name(), schemaBasedQualifier, fetchSpecification
							.sortOrderings());
			
			schemaBasedFetchSpecification.setFetchesRawRows(true);
			schemaBasedFetchSpecification.setFetchLimit(fetchSpecification
					.fetchLimit());
			schemaBasedFetchSpecification.setHints(fetchSpecification.hints());
			schemaBasedFetchSpecification
					.setIsDeep(fetchSpecification.isDeep());
			schemaBasedFetchSpecification.setUsesDistinct(fetchSpecification
					.usesDistinct());
			
			EODatabaseContext databaseContext = (EODatabaseContext) objectStoreCoordinator
					.objectStoreForFetchSpecification(fetchSpecification);
			EOAdaptorChannel adaptorChannel = databaseContext
					.availableChannel().adaptorChannel();
			Object savedDelegate = adaptorChannel.delegate();
			
			if (adaptorChannelDelegate != null) {
				adaptorChannel.setDelegate(adaptorChannelDelegate);
			}
			
			try {
				NSMutableArray rows = new NSMutableArray();
				NSDictionary currentRow;
				
				adaptorChannel.selectAttributes(attributes,
						schemaBasedFetchSpecification, false, entity);
				
				while ((currentRow = adaptorChannel.fetchRow()) != null) {
					rows.addObject(currentRow);
				}
				
				return rows;
			} finally {
				adaptorChannel.setDelegate(savedDelegate);
				adaptorChannel.cancelFetch();
			}
		} finally {
			objectStoreCoordinator.unlock();
		}
	}
}