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

package com.houdah.eocontrol.utilities;

import java.util.Collection;

import com.houdah.eocontrol.qualifiers.InSetQualifier;

import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOClassDescription;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOGlobalID;
import com.webobjects.eocontrol.EOKeyComparisonQualifier;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EONotQualifier;
import com.webobjects.eocontrol.EOOrQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSSelector;
import com.webobjects.foundation.NSSet;

/**
 * Sibiling class to EOUtilities. Repository of utility methods related to
 * EOControl. <br/>
 * 
 * This class provides a subset of the methods provided by EOUtilities, but has
 * the advantage of not relying on EOAccess. It is thus safe for use in client
 * applications. <br/>
 * 
 * It also provides additional, but related, utility methods. <br/>
 */
public class ControlUtilities
{
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 */
	private ControlUtilities()
	{
		throw new IllegalStateException("Do not instantiate this utility class");
	}
	
	
	
	// Public class methods
	
	/**
	 * Binds a fetch specification.
	 * 
	 * This method specifically takes care of multiple (aka. Collection) bindings.
	 * 
	 * @param fetchSpecification
	 *            the unbound fetch specification
	 * @param bindings
	 *            a dictionary of bindings
	 * @return a bound and pruned fetch specification
	 */
	public static EOFetchSpecification bindFetchSpecification(
			EOFetchSpecification fetchSpecification, NSDictionary bindings)
	{
		EOFetchSpecification boundFetchSpecification = fetchSpecification
				.fetchSpecificationWithQualifierBindings(bindings);
		
		if (boundFetchSpecification == fetchSpecification) {
			boundFetchSpecification = (EOFetchSpecification) fetchSpecification.clone();
		}
		
		EOQualifier qualifier = boundFetchSpecification.qualifier();
		
		if (qualifier != null) {
			EOQualifier qualifierWithBranches = qualifierWithBranches(qualifier);
			
			boundFetchSpecification.setQualifier(qualifierWithBranches);
		}
		
		return boundFetchSpecification;
	}
	
	
	
	/**
	 * Builds an EOQualifier allowing all provided values for the provided key.
	 * 
	 * @param key
	 *            the key to match against
	 * @param values
	 *            the values to match
	 * @return an EOOrQualifier with one branch for each value
	 */
	public static EOQualifier qualifierToMatchAnyValue(String key, NSArray values)
	{
		int count = values.count();
		NSMutableArray qualifiers = new NSMutableArray(count);
		
		for (int i = 0; i < count; i++) {
			EOQualifier qualifier = new EOKeyValueQualifier(key,
					EOQualifier.QualifierOperatorEqual, values.objectAtIndex(i));
			
			qualifiers.addObject(qualifier);
		}
		
		return new EOOrQualifier(qualifiers);
	}
	
	
	
	/**
	 * Searches both in the database and in memory.
	 * 
	 * CAVEAT: Does not yet implement inheritance: does not perform deep fetches !
	 * 
	 * @see com.webobjects.eoaccess.EOUtilities#objectsMatchingValues
	 */
	public static NSSet allObjectsMatchingValues(EOEditingContext editingContext,
			String entityName, NSDictionary values)
	{
		EOQualifier qualifier = EOQualifier.qualifierToMatchAllValues(values);
		EOFetchSpecification fetchSpecification = new EOFetchSpecification(entityName, qualifier,
				null);
		
		fetchSpecification.setIsDeep(false);
		
		NSArray inDatabase = editingContext.objectsWithFetchSpecification(fetchSpecification);
		NSSet filtered = new NSSet(EOQualifier.filteredArrayWithQualifier(inDatabase, qualifier));
		
		EOQualifier entityNameQualifier = new EOKeyValueQualifier("entityName",
				EOQualifier.QualifierOperatorEqual, entityName);
		EOQualifier memoryQualifier = new EOAndQualifier(new NSArray(new EOQualifier[] {
				entityNameQualifier, qualifier }));
		
		NSSet inserted = new NSSet(EOQualifier.filteredArrayWithQualifier(editingContext
				.insertedObjects(), memoryQualifier));
		NSSet updated = new NSSet(EOQualifier.filteredArrayWithQualifier(editingContext
				.updatedObjects(), memoryQualifier));
		NSSet inMemory = inserted.setByUnioningSet(updated);
		
		NSSet deleted = new NSSet(editingContext.deletedObjects());
		NSSet result = filtered.setByUnioningSet(inMemory).setBySubtractingSet(deleted);
		
		return result;
	}
	
	
	
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
			EOEditingContext editingContext, EOFetchSpecification fetchSpecification)
	{
		return objectWithBoundFetchSpecification(editingContext, fetchSpecification, true);
	}
	
	
	
	/**
	 * Fetches single objects using a fetch specification and a provided
	 * dictionary of bindings. <br/>
	 * 
	 * @param editingContext
	 *            the context to fetch into
	 * @param fetchSpecification
	 *            the specification to use
	 * @param bindings
	 *            qualifer bindings to use with qualifier variables
	 * @return the first of the found objects, null if none is found
	 */
	public static EOEnterpriseObject objectWithFetchSpecificationAndBindings(
			EOEditingContext editingContext, EOFetchSpecification fetchSpecification,
			NSDictionary bindings)
	{
		EOFetchSpecification boundFetchSpec = fetchSpecification
				.fetchSpecificationWithQualifierBindings(bindings);
		
		return objectWithBoundFetchSpecification(editingContext, boundFetchSpec,
				(boundFetchSpec == fetchSpecification));
	}
	
	
	
	// EOUtilities re-implementation
	
	/**
	 * @see com.webobjects.eoaccess.EOUtilities#objectsForEntityNamed
	 */
	public static NSArray objectsForEntityNamed(EOEditingContext editingContext, String entityName)
	{
		EOFetchSpecification fetchSpecification = new EOFetchSpecification(entityName, null, null);
		NSArray objectsForEntityNamed = editingContext
				.objectsWithFetchSpecification(fetchSpecification);
		
		return objectsForEntityNamed;
	}
	
	
	
	/**
	 * @see com.webobjects.eoaccess.EOUtilities#objectsWithQualifierFormat
	 */
	public static NSArray objectsWithQualifierFormat(EOEditingContext editingContext,
			String entityName, String qualifierFormat, NSArray arguments)
	{
		EOQualifier qualifier = EOQualifier
				.qualifierWithQualifierFormat(qualifierFormat, arguments);
		EOFetchSpecification fetchSpecification = new EOFetchSpecification(entityName, qualifier,
				null);
		NSArray objectsWithQualifierFormat = editingContext
				.objectsWithFetchSpecification(fetchSpecification);
		
		return objectsWithQualifierFormat;
	}
	
	
	
	/**
	 * @see com.webobjects.eoaccess.EOUtilities#objectsMatchingKeyAndValue
	 */
	public static NSArray objectsMatchingKeyAndValue(EOEditingContext editingContext,
			String entityName, String key, Object value)
	{
		NSDictionary values = new NSDictionary(value, key);
		NSArray objectsMatchingKeyAndValue = objectsMatchingValues(editingContext, entityName,
				values);
		
		return objectsMatchingKeyAndValue;
	}
	
	
	
	/**
	 * @see com.webobjects.eoaccess.EOUtilities#objectsMatchingValues
	 */
	public static NSArray objectsMatchingValues(EOEditingContext editingContext, String entityName,
			NSDictionary values)
	{
		EOQualifier qualifier = EOQualifier.qualifierToMatchAllValues(values);
		EOFetchSpecification fetchSpecification = new EOFetchSpecification(entityName, qualifier,
				null);
		NSArray objectsMatchingValues = editingContext
				.objectsWithFetchSpecification(fetchSpecification);
		
		return objectsMatchingValues;
	}
	
	
	
	/**
	 * @see com.webobjects.eoaccess.EOUtilities#objectWithQualifierFormat
	 */
	public static EOEnterpriseObject objectWithQualifierFormat(EOEditingContext editingContext,
			String entityName, String qualifierFormat, NSArray arguments)
	{
		EOQualifier qualifier = EOQualifier
				.qualifierWithQualifierFormat(qualifierFormat, arguments);
		EOFetchSpecification fetchSpecification = new EOFetchSpecification(entityName, qualifier,
				null);
		NSArray objects = editingContext.objectsWithFetchSpecification(fetchSpecification);
		
		switch (objects.count()) {
			case 1:
				return (EOEnterpriseObject) objects.objectAtIndex(0);
				
			case 0:
				throw new ObjectNotAvailableException(
						"objectWithQualifierFormat: No item selected for entity " + entityName
								+ " qualified by " + qualifier);
			default:
				throw new MoreThanOneException(
						"objectWithQualifierFormat: Selected more than one item for entity "
								+ entityName + " qualified by " + qualifier);
		}
	}
	
	
	
	/**
	 * @see com.webobjects.eoaccess.EOUtilities#objectMatchingKeyAndValue
	 */
	public static EOEnterpriseObject objectMatchingKeyAndValue(EOEditingContext editingContext,
			String entityName, String key, Object value) throws ObjectNotAvailableException,
			MoreThanOneException
	{
		NSArray objects = objectsMatchingKeyAndValue(editingContext, entityName, key, value);
		int count = objects.count();
		
		if (count == 0) {
			throw new ObjectNotAvailableException("objectMatchingValueForKeyEntityNamed: No '"
					+ entityName + "' found with key '" + key + "' matching '" + value + "'");
		} else if (count > 1) {
			throw new MoreThanOneException(
					"objectMatchingValueForKeyEntityNamed: Selected more than one " + entityName
							+ " with key " + key + " matching " + value);
		} else {
			return (EOEnterpriseObject) objects.objectAtIndex(0);
		}
	}
	
	
	
	/**
	 * @see com.webobjects.eoaccess.EOUtilities#objectMatchingValues
	 */
	public static EOEnterpriseObject objectMatchingValues(EOEditingContext editingContext,
			String entityName, NSDictionary values)
	{
		NSArray objects = objectsMatchingValues(editingContext, entityName, values);
		int count = objects.count();
		
		if (count == 0) {
			throw new ObjectNotAvailableException("objectMatchingValuesEntityNamed: No "
					+ entityName + " found matching " + values);
		} else if (count > 1) {
			throw new MoreThanOneException(
					"objectMatchingValuesEntityNamed: Selected more than one " + entityName
							+ " matching " + values);
		} else {
			return (EOEnterpriseObject) objects.objectAtIndex(0);
		}
	}
	
	
	
	/**
	 * @see com.webobjects.eoaccess.EOUtilities#rawRowsWithQualifierFormat
	 */
	public static NSArray rawRowsWithQualifierFormat(EOEditingContext editingContext,
			String entityName, String qualifierFormat, NSArray arguments)
	{
		EOQualifier qualifier = EOQualifier
				.qualifierWithQualifierFormat(qualifierFormat, arguments);
		EOFetchSpecification fetchSpecification = new EOFetchSpecification(entityName, qualifier,
				null);
		
		fetchSpecification.setFetchesRawRows(true);
		
		return editingContext.objectsWithFetchSpecification(fetchSpecification);
	}
	
	
	
	/**
	 * @see com.webobjects.eoaccess.EOUtilities#rawRowsMatchingKeyAndValue
	 */
	public static NSArray rawRowsMatchingKeyAndValue(EOEditingContext editingContext,
			String entityName, String key, Object value)
	{
		NSDictionary values = new NSDictionary(value, key);
		
		return rawRowsMatchingValues(editingContext, entityName, values);
	}
	
	
	public static NSArray rawRowsMatchingValues(EOEditingContext editingContext, String entityName,
			NSDictionary values)
	{
		EOQualifier qualifier = EOQualifier.qualifierToMatchAllValues(values);
		EOFetchSpecification fetchSpecification = new EOFetchSpecification(entityName, qualifier,
				null);
		
		fetchSpecification.setFetchesRawRows(true);
		
		return editingContext.objectsWithFetchSpecification(fetchSpecification);
	}
	
	
	
	/**
	 * @see com.webobjects.eoaccess.EOUtilities#createAndInsertInstance
	 */
	public static EOEnterpriseObject createAndInsertInstance(EOEditingContext editingContext,
			String entityName)
	{
		EOClassDescription description = EOClassDescription
				.classDescriptionForEntityName(entityName);
		
		if (description == null) {
			throw new IllegalArgumentException(
					"Could not find EOClassDescription for entity name '" + entityName + "' !");
		} else {
			return ControlUtilities.createAndInsertInstance(editingContext, description);
		}
	}
	
	
	
	/**
	 * @see com.webobjects.eoaccess.EOUtilities#createAndInsertInstance
	 */
	public static EOEnterpriseObject createAndInsertInstance(EOEditingContext editingContext,
			EOClassDescription eoclassdescription)
	{
		EOEnterpriseObject enterpriseObject = eoclassdescription.createInstanceWithEditingContext(
				editingContext, null);
		
		editingContext.insertObject(enterpriseObject);
		
		return enterpriseObject;
	}
	
	
	
	/**
	 * @see com.webobjects.eoaccess.EOUtilities#localInstanceOfObject
	 */
	public static EOEnterpriseObject localInstanceOfObject(EOEditingContext editingContext,
			EOEnterpriseObject enterpriseObject)
	{
		
		if (enterpriseObject == null) {
			return null;
		} else {
			EOEditingContext objectEditingContext = enterpriseObject.editingContext();
			
			if (objectEditingContext == null) {
				throw new IllegalArgumentException("The EOEnterpriseObject " + enterpriseObject
						+ " is not in an EOEditingContext");
			}
			
			EOGlobalID globalId = objectEditingContext.globalIDForObject(enterpriseObject);
			return editingContext.faultForGlobalID(globalId, editingContext);
		}
	}
	
	
	
	/**
	 * @see com.webobjects.eoaccess.EOUtilities#localInstancesOfObjects
	 */
	public static NSArray localInstancesOfObjects(EOEditingContext editingContext, NSArray objects)
	{
		if (objects == null) {
			return NSArray.EmptyArray;
		} else {
			int count = objects.count();
			NSMutableArray localInstancesOfObjects = new NSMutableArray(count);
			
			for (int i = 0; i < count; i++) {
				EOEnterpriseObject enterpriseObject = localInstanceOfObject(editingContext,
						(EOEnterpriseObject) objects.objectAtIndex(i));
				if (enterpriseObject != null) {
					localInstancesOfObjects.addObject(enterpriseObject);
				} else {
					throw new IllegalArgumentException(
							"Unable to create a local instance of the EO at index " + i
									+ " in the destination EOEditingContext.");
				}
			}
			
			return localInstancesOfObjects;
		}
	}
	
	
	
	// Private class methods
	
	/**
	 * Fetches single objects using a bound fetch specification. <br/>
	 * 
	 * @param editingContext
	 *            the context to fetch into
	 * @param fetchSpecification
	 *            the specification to use
	 * @param clone
	 *            true if the fetch spec should be cloned before setting a fetch
	 *            limit
	 * @return the first of the found objects, null if none is found
	 */
	private static EOEnterpriseObject objectWithBoundFetchSpecification(
			EOEditingContext editingContext, EOFetchSpecification fetchSpecification, boolean clone)
	{
		NSArray fetchResult;
		EOFetchSpecification fetch = (EOFetchSpecification) (clone ? fetchSpecification.clone()
				: fetchSpecification);
		
		fetch.setFetchLimit(1);
		fetchResult = editingContext.objectsWithFetchSpecification(fetch);
		
		if (fetchResult.count() > 0) {
			return (EOEnterpriseObject) fetchResult.objectAtIndex(0);
		} else {
			return null;
		}
	}
	
	
	
	/**
	 * Creates a new EOQualifier based on a fully bound qualifier it receives.<br/>
	 * 
	 * The new qualifier is identical to the old one, except that it fixes
	 * situations where a EOKeyValueQualifier binds a key to an array of values.
	 * In the arborescence of qualifiers such a node is replaced by an
	 * EOOrQualifier node with an EOKeyValueQualifier child for each of the
	 * values in the array.<br/>
	 * 
	 * Recursively calls itself on nodes of type EONotQualifier, EOAndQualifier
	 * and EOOrQualifier. Calls branchForQualifier to handle terminal leaves of
	 * type EOKeyValueQualifier. Other terminal leaves are qualifiers of type
	 * EOKeyValueQualifier.
	 * 
	 * @param qualifier
	 *            a fully bound qualifier
	 * @return a new fully bound qualifier with added branches as described
	 *         above
	 * @see #branchForQualifier
	 */
	private static EOQualifier qualifierWithBranches(EOQualifier qualifier)
	{
		NSMutableArray newQualifierBranch;
		NSArray qualifierBranch = null;
		
		if (qualifier instanceof EOKeyValueQualifier) {
			return branchForQualifier((EOKeyValueQualifier) qualifier);
		} else if (qualifier instanceof EOKeyComparisonQualifier) {
			return qualifier;
		} else if (qualifier instanceof EONotQualifier) {
			return new EONotQualifier(qualifierWithBranches(qualifier));
		} else if (qualifier instanceof EOOrQualifier) {
			qualifierBranch = ((EOOrQualifier) qualifier).qualifiers();
			newQualifierBranch = new NSMutableArray();
			
			for (int i = 0; i < qualifierBranch.count(); i++) {
				newQualifierBranch.addObject(qualifierWithBranches((EOQualifier) qualifierBranch
						.objectAtIndex(i)));
			}
			
			return new EOOrQualifier(newQualifierBranch);
		} else if (qualifier instanceof EOAndQualifier) {
			qualifierBranch = ((EOAndQualifier) qualifier).qualifiers();
			newQualifierBranch = new NSMutableArray();
			
			for (int i = 0; i < qualifierBranch.count(); i++) {
				newQualifierBranch.addObject(qualifierWithBranches((EOQualifier) qualifierBranch
						.objectAtIndex(i)));
			}
			
			return new EOAndQualifier(newQualifierBranch);
		} else {
			throw new RuntimeException(
					"ControlUtilities - qualifierWithBranches: unknown qualifier type: "
							+ qualifier.getClass().getName());
		}
	}
	
	
	
	/**
	 * Creates a new EOQualifier based on a EOKeyValueQualifier it receives.<br/>
	 * 
	 * The new qualifier is a copy of the old one if the old one binds a key to
	 * a simple value.<br/>
	 * 
	 * However, if the old one binds its key to an array of values. The new
	 * qualifier is an EOOrQualifier with an EOKeyValueQualifier child for each
	 * of the values in the array.<br/>
	 * 
	 * @param qualifier
	 *            a EOKeyValueQualifier
	 * @return either a copy of the qualifier, or an EOOrQualifier build as
	 *         described above
	 * @see #qualifierWithBranches
	 */
	private static EOQualifier branchForQualifier(EOKeyValueQualifier qualifier)
	{
		String key = qualifier.key();
		NSSelector selector = qualifier.selector();
		Object value = qualifier.value();
		
		if (selector.equals(EOQualifier.QualifierOperatorEqual)) {
			if (value instanceof NSArray) {
				return new InSetQualifier(key, new NSSet((NSArray) value));
			} else if (value instanceof NSSet) {
				return new InSetQualifier(key, (NSSet) value);
			} else if (value instanceof Collection) {
				Collection collection = (Collection) value;
				
				return new InSetQualifier(key, new NSSet(collection.toArray()));
			}
		} else {
			NSArray valueArray = null;
			
			if (value instanceof NSArray) {
				valueArray = (NSArray) value;
			} else if (value instanceof NSSet) {
				valueArray = ((NSSet) value).allObjects();
			} else if (value instanceof Collection) {
				Collection collection = (Collection) value;
				
				valueArray = new NSArray(collection.toArray());
			}
			
			if (valueArray != null) {
				NSMutableArray qualifierBranch = new NSMutableArray();
				
				for (int j = 0; j < valueArray.count(); j++) {
					qualifierBranch.addObject(new EOKeyValueQualifier(key, selector, valueArray
							.objectAtIndex(j)));
				}
				
				return new EOOrQualifier(qualifierBranch);
			}
		}
		
		return new EOKeyValueQualifier(key, selector, value);
	}
}