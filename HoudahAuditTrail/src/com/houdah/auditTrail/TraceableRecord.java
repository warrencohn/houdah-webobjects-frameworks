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

package com.houdah.auditTrail;

import com.houdah.eocontrol.utilities.ControlUtilities;

import com.webobjects.eocontrol.EOClassDescription;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFaulting;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSSet;

public abstract class TraceableRecord extends ChangeableRecord
{
	// Private class constants
	
	private static final long	serialVersionUID	= -1864063473989639532L;
	
	
	
	// Public class constants
	
	public static final String	COMMENT				= "comment";
	
	
	public static final String	VERSION				= "version";
	
	
	public static final Integer	ZERO_VERSION		= new Integer(0);
	
	
	
	// Private instance variables
	
	private String				comment				= null;
	
	
	private NSDictionary		originalValues		= null;
	
	
	
	
	// Constructors
	
	public TraceableRecord()
	{
	}
	
	
	public TraceableRecord(EOClassDescription classDescription)
	{
		super(classDescription);
	}
	
	
	
	// Public instance methods
	
	/**
	 * Provides defaults values for a newly inserted object.
	 * 
	 * Sets the version number to 0.
	 */
	public void awakeFromInsertion(EOEditingContext editingContext)
	{
		if (needsHistoricObject() && (version() == null)) {
			setVersion(ZERO_VERSION);
		}
		
		super.awakeFromInsertion(editingContext);
	}
	
	
	
	/**
	 * Retrieves the immediate ancessor of an historic object.
	 * 
	 * @return the former version of the object, null if none exists, null if
	 *         the current object is itself an historic object
	 */
	public TraceableRecord latestHistoricObject()
	{
		String toHistoricRelationshipName = historicRelationshipName();
		
		if (toHistoricRelationshipName == null) {
			throw new RuntimeException(getClass().getName()
					+ ".toHistoricRelationshipName() returned null");
		} else {
			NSArray history = (NSArray) valueForKey(toHistoricRelationshipName);
			int hCount = history.count();
			
			if (hCount > 0) {
				if (hCount > 1) {
					EOSortOrdering versionOrdering = new EOSortOrdering(TraceableRecord.AUDIT_DATE,
							EOSortOrdering.CompareDescending);
					NSArray sortedHistory = EOSortOrdering.sortedArrayUsingKeyOrderArray(history,
							new NSArray(versionOrdering));
					
					return (TraceableRecord) sortedHistory.objectAtIndex(0);
				} else {
					return (TraceableRecord) history.objectAtIndex(0);
				}
			}
			
			return null;
		}
	}
	
	
	
	// Public abstract instance methods
	
	public abstract Number version();
	
	
	public abstract void setVersion(Number aValue);
	
	
	
	/**
	 * Tells TraceableRecord whether it needs to set an initial version number
	 * and create history objects for the current class.<br/>
	 * 
	 * @return true if TraceableRecord's services are needed, false if not (e.g.
	 *         on history objects)
	 */
	public abstract boolean needsHistoricObject();
	
	
	
	/**
	 * Tells TraceableRecord the name of the relationship to the history array.<br/>
	 * 
	 * @return something like TO_HISTORY_ARRAY, null if there is no link to the
	 *         history array
	 */
	public abstract String historicRelationshipName();
	
	
	
	// Public instance methods
	
	/**
	 * Method allowing to a subclass to tell which is the inverse relationship
	 * when this relationship isn't defined on the historic class it self (eg:
	 * when the relationship is defined in a superclass of the historic class).<br/>
	 * This method provides the default implemtation returning null
	 * 
	 * @return the name of a relationship
	 */
	public String customInverseHistoricRelationshipName()
	{
		return null;
	}
	
	
	
	/**
	 * Tells TraceableRecord what Entity to use for history objects.<br/>
	 * 
	 * @return something like TheHistoryClass.entityName, null if
	 *         needsHistoricObject() returns false
	 */
	public String historicEntityName()
	{
		String toHistoricRelationshipName = historicRelationshipName();
		
		if (toHistoricRelationshipName != null) {
			EOClassDescription classDescription = classDescription();
			EOClassDescription historicClassDescription = classDescription
					.classDescriptionForDestinationKey(toHistoricRelationshipName);
			
			return historicClassDescription.entityName();
		}
		
		return null;
	}
	
	
	
	/**
	 * Retrieves the comment from the immediately preceeding former version of
	 * the object. Applies only to current (i.e. non historic objects).<br/>
	 * 
	 * Historic objects should override this method by declaring a comment
	 * attribute in their model.
	 * 
	 * @return the comment, if possible, null otherwise
	 * @see #latestHistoricObject
	 */
	public String comment()
	{
		if (needsHistoricObject() && (this.comment != null)) {
			return this.comment;
		} else {
			TraceableRecord ancessor = (TraceableRecord) latestHistoricObject();
			
			return (ancessor != null) ? ancessor.comment() : null;
		}
	}
	
	
	
	/**
	 * Sets the comment on the historic object created since the last save.<br/>
	 * 
	 * Historic objects should override this method by declaring a comment
	 * attribute in their model.
	 * 
	 * @param comment
	 *            the non null comment to set
	 */
	public synchronized void setComment(String comment)
	{
		this.comment = comment;
	}
	
	
	
	// Protected instance methods related to change management
	
	
	/**
	 * Handles the event received from the AuditingEditingContext.<br/>
	 * 
	 * @see AuditingEditingContext#objectWillChange
	 */
	protected synchronized void objectWillChange()
	{
		super.objectWillChange();
		
		if ((this.originalValues == null) && needsHistoricObject()) {
			this.originalValues = valuesForKeys(allPropertyKeys());
		}
	}
	
	
	
	/**
	 * Called by the AuditingEditor to inform the object that we are about to
	 * commit.
	 */
	protected synchronized void commitAuditTrail()
	{
		super.commitAuditTrail();
		
		createHistoricData();
	}
	
	
	
	/**
	 * Called by the AuditingEditingContext after accepting previously
	 * historized changes from a child context.
	 */
	protected synchronized void purgeAuditTrail()
	{
		super.purgeAuditTrail();
		
		this.originalValues = null;
	}
	
	
	
	// Private instance methods
	
	
	/**
	 * Creates an history object for the current object.<br/>
	 * 
	 * Relies on the subclasses' implementations of the historicDataEntityName()
	 * and toHistoricDataRelationshipName() methods.
	 */
	private void createHistoricData()
	{
		if (this.originalValues != null) {
			if (!isNew()) {
				// Fetch the name of the history entity to be instantiated
				String historicEntityName = historicEntityName();
				
				if (historicEntityName == null) {
					throw new RuntimeException(getClass().getName()
							+ ".historicEntityName() returned null");
				} else {
					TraceableRecord latestHistoricObject = latestHistoricObject();
					
					if (!((latestHistoricObject != null) && latestHistoricObject.isNew())) {
						TraceableRecord historicObject = (TraceableRecord) ControlUtilities
								.createAndInsertInstance(editingContext(), historicEntityName);
						NSArray historicAttributeKeys = historicObject.attributeKeys();
						NSArray historiyToOneKeys = historicObject.toOneRelationshipKeys();
						NSArray historicPropertyKeys = historicAttributeKeys
								.arrayByAddingObjectsFromArray(historiyToOneKeys);
						NSArray atributeKeys = attributeKeys();
						NSArray toOneKeys = toOneRelationshipKeys();
						NSArray propertyKeys = atributeKeys
								.arrayByAddingObjectsFromArray(toOneKeys);
						NSSet propertyKeySet = new NSSet(propertyKeys);
						
						
						// Copy the properties
						for (int i = 0; i < historicPropertyKeys.count(); i++) {
							String key = (String) historicPropertyKeys.objectAtIndex(i);
							
							if (propertyKeySet.containsObject(key)) {
								Object value = this.originalValues.objectForKey(key);
								
								historicObject.takeStoredValueForKey(value, key);
							}
						}
						
						
						// Hand off the comment
						historicObject.setComment(this.comment);
						this.comment = null;
						
						
						// Update the version number
						setVersion(new Integer(version().intValue() + 1));
						
						
						// Update the relationship to the historic data, if
						// required
						String toHistoricRelationshipName = historicRelationshipName();
						
						if (toHistoricRelationshipName != null) {
							Object historicArray = valueForKey(toHistoricRelationshipName);
							String inverseForRelationshipName = customInverseHistoricRelationshipName();
							
							if (inverseForRelationshipName == null) {
								inverseForRelationshipName = inverseForRelationshipKey(toHistoricRelationshipName);
							}
							
							if (inverseForRelationshipName != null) {
								historicObject.takeValueForKey(this, inverseForRelationshipName);
							}
							
							
							// If the array fault has not been resolved yet,
							// don't fire it now
							boolean addToRelationship = true;
							
							if (historicArray instanceof EOFaulting) {
								EOFaulting historicArrayFault = (EOFaulting) historicArray;
								
								addToRelationship = !historicArrayFault.isFault();
							}
							
							if (addToRelationship) {
								this.addObjectToPropertyWithKey(historicObject,
										toHistoricRelationshipName);
							}
						}
						
						
						// Purge audit trail on historic object
						historicObject.purgeAuditTrail();
					}
				}
			}
			
			this.originalValues = null;
		}
	}
}