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

import java.util.Enumeration;

import com.houdah.eocontrol.qualifiers.InSetQualifier;

import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSRange;
import com.webobjects.foundation.NSSet;

/**
 * Utility class to iterate over the results of a given fetch specification.<br/>
 * <br/>
 * 
 * The database is queried once to build the list of rows to retrieve. On each
 * iteration only a batch objects rows is retrieved to be instantiated as
 * objects. This allows for memory efficient operations on large sets of
 * objects.<br/>
 * <br/>
 * 
 * CAVEAT: This does NOT work on objects whose entity has a compound primary
 * key.
 * 
 * @author bernard
 */
public class FetchSpecBatchIterator implements Enumeration
{
	// Public class constants
	
	public static final int			DEFAULT_BATCH_SIZE	= 200;
	
	
	
	// Protected instance variables
	
	protected EOFetchSpecification	fetchSpecification;
	
	
	protected EOEditingContext		editingContext;
	
	
	protected int					batchSize;
	
	
	protected boolean				useFaults;
	
	
	protected NSArray				prefetchingRelationshipKeyPaths;
	
	
	protected NSArray				primaryKeys			= null;
	
	
	protected int					batchIndex;
	
	
	protected int					batchCount;
	
	
	
	
	// Constructors
	
	/**
	 * Constructor. <br/>
	 * 
	 * Once constructed, data is retrieved in a lazy manner from the database.
	 * 
	 * @param the
	 *            fetch specification specifying the objects to retrieve
	 * @param the
	 *            editing context to fetch into
	 * @param the
	 *            maximum size of the batches to create
	 */
	public FetchSpecBatchIterator(EOFetchSpecification fetchSpecification,
			EOEditingContext editingContext)
	{
		this(fetchSpecification, editingContext, DEFAULT_BATCH_SIZE);
	}
	
	
	
	/**
	 * Designated Constructor. <br/>
	 * 
	 * Once constructed, data is retrieved in a lazy manner from the database.
	 * 
	 * @param the
	 *            fetch specification specifying the objects to retrieve
	 * @param the
	 *            editing context to fetch into
	 * @param the
	 *            maximum size of the batches to create
	 */
	public FetchSpecBatchIterator(EOFetchSpecification fetchSpecification,
			EOEditingContext editingContext, int batchSize)
	{
		if (fetchSpecification == null) {
			throw new IllegalArgumentException(
					"fetchSpecification may not be null");
		}
		
		if (editingContext == null) {
			throw new IllegalArgumentException("editingContext may not be null");
		}
		
		if (batchSize <= 0) {
			throw new IllegalArgumentException(
					"batchSize must greater than zero");
		}
		
		this.fetchSpecification = pkFetchSpec(editingContext,
				fetchSpecification);
		this.editingContext = editingContext;
		this.useFaults = false;
		this.batchSize = batchSize;
		this.prefetchingRelationshipKeyPaths = fetchSpecification
				.prefetchingRelationshipKeyPaths();
	}
	
	
	
	// Public instance methods
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Enumeration#hasMoreElements()
	 */
	public boolean hasMoreElements()
	{
		return hasMoreBatches();
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Enumeration#nextElement()
	 */
	public Object nextElement()
	{
		return nextBatch();
	}
	
	
	public NSArray nextBatch()
	{
		if (hasMoreBatches()) {
			NSArray batch = null;
			NSArray primaryKeys = primaryKeys();
			int pkCount = primaryKeys.count();
			int fetchCount = this.batchIndex * this.batchSize;
			int length = (pkCount - fetchCount > this.batchSize) ? this.batchSize
					: (pkCount - fetchCount);
			NSRange range = new NSRange(fetchCount, length);
			NSArray primaryKeysToFetch = primaryKeys.subarrayWithRange(range);
			String pkAttributeName = (String) this.fetchSpecification
					.rawRowKeyPaths().objectAtIndex(0);
			
			if (useFaults()) {
				NSMutableArray mutableBatch = new NSMutableArray(length);
				
				for (int i = 0; i < length; i++) {
					mutableBatch.addObject(EOUtilities.faultWithPrimaryKey(
							editingContext(), this.fetchSpecification
									.entityName(), new NSDictionary(
									primaryKeysToFetch.objectAtIndex(i),
									pkAttributeName)));
				}
				
				batch = mutableBatch.immutableClone();
			} else {
				InSetQualifier qualifier = new InSetQualifier(pkAttributeName,
						new NSSet(primaryKeysToFetch));
				EOFetchSpecification fetchSpec = new EOFetchSpecification(
						this.fetchSpecification.entityName(), qualifier,
						this.fetchSpecification.sortOrderings());
				
				fetchSpec
						.setPrefetchingRelationshipKeyPaths(this.prefetchingRelationshipKeyPaths);
				batch = editingContext().objectsWithFetchSpecification(
						fetchSpec);
			}
			this.batchIndex += 1;
			
			return batch;
		}
		
		throw new IllegalStateException("No more batches");
	}
	
	
	public boolean hasMoreBatches()
	{
		if (!hasFetched()) {
			fetchPrimaryKeys();
		}
		
		return this.batchIndex < this.batchCount;
	}
	
	
	public EOEditingContext editingContext()
	{
		return this.editingContext;
	}
	
	
	public boolean useFaults()
	{
		return this.useFaults;
	}
	
	
	
	/**
	 * Defines if the iterator should retrieve faults rather than fullfledged
	 * objects.<br/>
	 * 
	 * This is of advantage in those rare situations where the values of the
	 * objects attributes are not needed. Here it saves both and IO time and
	 * memory space.<br/>
	 * 
	 * CAVEAT: This feature should not be used when the rows retrieved are to be
	 * transformed into full blown enterprise objects later on. Faulting objects
	 * one at a time would create a significant performance hit. Object deletion
	 * unfortunately does fire the fault in order to perform validation!
	 * 
	 * @param useFauls
	 *            true to enable this feature, off by default
	 */
	public void setUseFaults(boolean useFaults)
	{
		this.useFaults = useFaults;
	}
	
	
	
	// Protected instance methods
	
	protected NSArray primaryKeys()
	{
		if (!hasFetched()) {
			fetchPrimaryKeys();
		}
		
		return this.primaryKeys;
	}
	
	
	protected boolean hasFetched()
	{
		return (this.primaryKeys != null);
	}
	
	
	protected void fetchPrimaryKeys()
	{
		EOEntity entity = EOUtilities.entityNamed(editingContext(),
				this.fetchSpecification.entityName());
		NSArray primaryKeyAttributeNames = entity.primaryKeyAttributeNames();
		String pkAttributeName = (String) primaryKeyAttributeNames
				.objectAtIndex(0);
		NSArray primaryKeyDictionaries = editingContext()
				.objectsWithFetchSpecification(this.fetchSpecification);
		
		this.primaryKeys = (NSArray) primaryKeyDictionaries
				.valueForKey(pkAttributeName);
		this.batchIndex = 0;
		this.batchCount = (int) Math.ceil((this.primaryKeys.count() * 1.0)
				/ (this.batchSize * 1.0));
	}
	
	
	
	// Protected class methods
	
	protected static EOFetchSpecification pkFetchSpec(
			EOEditingContext editingContext,
			EOFetchSpecification fetchSpecification)
	{
		EOEntity entity = EOUtilities.entityNamed(editingContext,
				fetchSpecification.entityName());
		EOFetchSpecification pkFetchSpec = (EOFetchSpecification) fetchSpecification
				.clone();
		
		pkFetchSpec.setFetchesRawRows(true);
		pkFetchSpec.setRawRowKeyPaths(entity.primaryKeyAttributeNames());
		pkFetchSpec.setFetchLimit(fetchSpecification.fetchLimit());
		
		if (entity.primaryKeyAttributes().count() != 1) {
			throw new IllegalArgumentException("The entity " + entity.name()
					+ " has a compound primary key. Not supported.");
		}
		
		return pkFetchSpec;
	}
}