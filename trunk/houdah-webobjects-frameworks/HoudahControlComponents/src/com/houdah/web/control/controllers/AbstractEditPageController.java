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

package com.houdah.web.control.controllers;

import java.util.Enumeration;
import java.util.Hashtable;

import com.houdah.appserver.support.ApplicationException;
import com.houdah.eoaccess.utilities.AdaptorExceptionUtilities;
import com.houdah.eocontrol.GenericRecord;
import com.houdah.eocontrol.utilities.ControlUtilities;
import com.houdah.eovalidation.control.HEVValidationException;
import com.houdah.foundation.formatters.KeyFormatterFormatter;
import com.houdah.web.control.application.Application;
import com.houdah.web.control.components.HCCEditComponent;
import com.houdah.web.control.components.ControllerPage;
import com.houdah.web.view.actions.Action;
import com.houdah.web.view.form.descriptors.FieldsetDescriptor;
import com.houdah.web.view.form.descriptors.FieldsetRowDescriptor;
import com.houdah.web.view.form.descriptors.FormActionFieldDescriptor;
import com.houdah.web.view.form.descriptors.FormFieldDescriptor;
import com.houdah.web.view.form.descriptors.FormValueFieldDescriptor;
import com.houdah.web.view.form.values.Value;
import com.houdah.web.view.form.values.ValueContainer;
import com.houdah.web.view.simplelist.descriptors.SimpleListDescriptor;
import com.houdah.web.view.simplelist.descriptors.SimpleListItemTextDescriptor;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.appserver.WORequest;
import com.webobjects.eoaccess.EODatabaseDataSource;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOGeneralAdaptorException;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EODataSource;
import com.webobjects.eocontrol.EODetailDataSource;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOGlobalID;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EORelationshipManipulation;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSMutableSet;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;
import com.webobjects.foundation.NSSet;
import com.webobjects.foundation.NSValidation;
import com.webobjects.foundation.NSValidation.ValidationException;

public abstract class AbstractEditPageController extends AbstractFormPageController
{
	// Public class constants
	
	public static final String					CANCEL_ACTION			= "cancelAction";
	
	
	public static final String					SAVE_ACTION				= "saveAction";
	
	
	public static final String					CANCEL_ACTION_CSS_CLASS	= "cancelAction";
	
	
	public static final String					SAVE_ACTION_CSS_CLASS	= "saveAction";
	
	
	
	// Protected class constants
	
	/**
	 * Key for the page's userInfo dictionary.
	 */
	public static final String					WRITING_CONTEXT_KEY		= "writingContext";
	
	
	
	// Private class variables
	
	private static FormActionFieldDescriptor	cancelActionField;
	
	
	private static FormActionFieldDescriptor	saveActionField;
	
	
	
	// Protected instance variables
	
	protected NSMutableArray					errorMessages			= null;
	
	
	
	// Private instance variables
	
	private WODisplayGroup						displayGroup			= null;
	
	
	private EOGlobalID							globalID				= null;
	
	
	private EOEnterpriseObject					object;
	
	
	private NSArray								editFieldsets			= null;
	
	
	private FormFieldDescriptor					cellDescriptor			= null;
	
	
	private FieldsetDescriptor					fieldsetDescriptor		= null;
	
	
	private SimpleListDescriptor				errorListDescriptor		= null;
	
	
	private NSMutableSet						readOnlyOnEditFields	= null;
	
	
	private NSMutableSet						readOnlyFields			= null;
	
	
	private NSArray								orderedDescriptors		= null;
	
	
	private boolean								needsToInitialize		= true;
	
	
	private boolean								isDeletion				= false;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param entityName
	 *            name of the entity to work on
	 * @param task
	 *            name of the task to perform
	 */
	public AbstractEditPageController(String entityName, String task)
	{
		super(entityName, task);
	}
	
	
	
	// Public instance methods
	
	public EOEditingContext writingContext()
	{
		NSMutableDictionary userInfo = page().userInfo();
		EOEditingContext writingContext = (EOEditingContext) userInfo
				.valueForKey(WRITING_CONTEXT_KEY);
		
		if (writingContext == null) {
			writingContext = setWritingContext(createWritingContext());
		}
		
		return writingContext;
	}
	
	
	public EOEditingContext setWritingContext(EOEditingContext writingContext)
	{
		NSMutableDictionary userInfo = page().userInfo();
		
		if (writingContext != null) {
			session().lockEditingContext(writingContext);
			
			userInfo.takeValueForKey(writingContext, WRITING_CONTEXT_KEY);
		} else {
			userInfo.removeObjectForKey(WRITING_CONTEXT_KEY);
		}
		
		writingContext.setDelegate(new EOContextLockingDelegate());
		
		this.globalID = null;
		this.displayGroup = null;
		this.object = null;
		this.needsToInitialize = true;
		
		return writingContext;
	}
	
	
	
	/**
	 * Get the editing context used for current operations.<br/>
	 * 
	 * Subclassed using child or peer editing contexts mut override this method
	 * to always return the currently used editing context.
	 * 
	 * @return the currently used editing context
	 */
	public EOEditingContext activeEditingContext()
	{
		return writingContext();
	}
	
	
	public void setObject(EOGlobalID globalID, WODisplayGroup displayGroup)
	{
		// Prime writing context as doing so may otherwise override the below
		// values
		writingContext().revert();
		
		this.globalID = globalID;
		this.displayGroup = displayGroup;
		this.object = null;
		this.needsToInitialize = true;
	}
	
	
	public void valueChangedNotification(NSNotification notification)
	{
		Value value = (Value) notification.object();
		FormValueFieldDescriptor valueFieldDescriptor = (FormValueFieldDescriptor) value.userInfo()
				.objectForKey(AbstractFormPageController.DESCRIPTOR_KEY);
		String key = valueFieldDescriptor.key();
		
		String oldErrorMessage = value.errorMessage();
		
		if (oldErrorMessage != null) {
			this.errorMessages.removeObject(oldErrorMessage);
			
			value.setErrorMessage(null);
			value.removeCssClass("error");
		}
		
		Object objectValue = value.value();
		
		if (value.errorMessage() == null) {
			if (objectValue instanceof NSSet) {
				NSMutableSet missing = ((NSSet) objectValue).mutableClone();
				Object existing = this.object.valueForKeyPath(key);
				
				if (existing instanceof NSSet) {
					NSMutableSet existingSet = ((NSSet) existing).mutableClone();
					Enumeration iterator = ((NSSet) existing).objectEnumerator();
					
					while (iterator.hasMoreElements()) {
						Object element = iterator.nextElement();
						
						if (missing.containsObject(element)) {
							missing.removeObject(element);
						} else {
							existingSet.removeObject(element);
						}
					}
					
					Enumeration enumeration = missing.objectEnumerator();
					
					while (enumeration.hasMoreElements()) {
						Object element = enumeration.nextElement();
						
						existingSet.removeObject(element);
					}
				} else if (missing.count() == 1) {
					Object missingObject = missing.anyObject();
					if (missingObject == NSKeyValueCoding.NullValue) {
						missingObject = null;
					}
					this.object.takeValueForKeyPath(missingObject, key);
				}
			} else {
				try {
					Object validatedValue = this.object.validateTakeValueForKeyPath(objectValue,
							key);
					
					value.setValue(validatedValue);
				} catch (ValidationException ve) {
					value.exceptionHandler().handleException(value, ve);
				}
			}
		}
		
		String errorMessage = value.errorMessage();
		
		if (errorMessage != null) {
			this.errorMessages.addObject(errorMessage);
			value.addCssClass("error");
		}
	}
	
	
	
	// Delegate methods
	
	public Boolean finishInitialization(FormValueFieldDescriptor cellDescriptor, Value value)
	{
		if (super.finishInitialization(cellDescriptor, value).booleanValue()) {
			if (this.readOnlyFields.containsObject(cellDescriptor)) {
				value.setReadOnly(true);
			} else if ((!isNew()) && (this.readOnlyOnEditFields.containsObject(cellDescriptor))) {
				value.setReadOnly(true);
			}
			return Boolean.TRUE;
		}
		
		return Boolean.FALSE;
	}
	
	
	
	// Page controller methods
	
	public void willInitializePage()
	{
		if (this.object == null) {
			_setObject();
		}
		
		initializeController();
		
		super.willInitializePage();
	}
	
	
	
	// Configuration methods
	
	public String controllerComponentName()
	{
		return HCCEditComponent.class.getName();
	}
	
	
	
	// Backtrack detection
	
	/**
	 * Returns the keys to the values that need to be persisted.<br/>
	 * 
	 * A null return value means that backtracking is prohibited.<br/> A
	 * NSArray.EmptyArray return value means that backtracking need not be
	 * handled.<br/>
	 * 
	 * The above 2 special cases are however best served by directly subclassing
	 * BackTrackComponent.
	 * 
	 * @see #needsStatePersistence
	 */
	public NSArray getPersistentKeys()
	{
		return null;
	}
	
	
	
	// Protected instance methods
	
	protected NSArray editFieldsets()
	{
		return this.editFieldsets;
	}
	
	
	protected FormFieldDescriptor cellDescriptor()
	{
		return this.cellDescriptor;
	}
	
	
	protected void setCellDescriptor(FormFieldDescriptor cellDescriptor)
	{
		this.cellDescriptor = cellDescriptor;
	}
	
	
	protected FieldsetDescriptor fieldsetDescriptor()
	{
		return this.fieldsetDescriptor;
	}
	
	
	protected void setFieldsetDescriptor(FieldsetDescriptor fieldsetDescriptor)
	{
		this.fieldsetDescriptor = fieldsetDescriptor;
	}
	
	
	protected SimpleListDescriptor errorListDescriptor()
	{
		return this.errorListDescriptor;
	}
	
	
	protected void setErrorListDescriptor(SimpleListDescriptor errorListDescriptor)
	{
		this.errorListDescriptor = errorListDescriptor;
	}
	
	
	protected void setReadOnlyOnEdit(FormValueFieldDescriptor cellDescriptor)
	{
		this.readOnlyOnEditFields.addObject(cellDescriptor);
		
		Value value = this.values.value(cellDescriptor);
		
		if ((value.valueDelegate() == null) || (value.valueDelegate().delegateObject() != this)) {
			throw new IllegalArgumentException("Must be delegate of the read-only value");
		}
	}
	
	
	protected void setReadOnly(FormValueFieldDescriptor cellDescriptor)
	{
		this.readOnlyFields.addObject(cellDescriptor);
		
		Value value = this.values.value(cellDescriptor);
		
		if ((value.valueDelegate() == null) || (value.valueDelegate().delegateObject() != this)) {
			throw new IllegalArgumentException("Must be delegate of the read-only value");
		}
	}
	
	
	protected void initializeController()
	{
		if (this.needsToInitialize) {
			this.needsToInitialize = false;
			
			this.values = new ValueContainer(this);
			this.errorMessages = new NSMutableArray();
			
			if (this.editFieldsets == null) {
				this.readOnlyOnEditFields = new NSMutableSet();
				this.readOnlyFields = new NSMutableSet();
				this.editFieldsets = generateEditFieldsets();
				this.orderedDescriptors = generateOrderedValueDescriptors(this.editFieldsets);
			}
			
			if (this.errorListDescriptor == null) {
				this.errorListDescriptor = generateErrorListDescriptor();
			}
			
			if (this.isDeletion) {
				int dCount = this.orderedDescriptors.count();
				
				for (int d = 0; d < dCount; d++) {
					FormValueFieldDescriptor descriptor = (FormValueFieldDescriptor) this.orderedDescriptors
							.objectAtIndex(d);
					
					setReadOnly(descriptor);
				}
			}
		}
	}
	
	
	protected EOEditingContext createWritingContext()
	{
		EOEditingContext editingContext = editingContext();
		
		
		// Create a peer editing context
		return sessionController().createEditingContext(editingContext.parentObjectStore());
	}
	
	
	protected boolean isNew()
	{
		return this.globalID == null;
	}
	
	
	
	// Action methods
	
	public WOActionResults deleteObject()
	{
		EOEditingContext writingContext = writingContext();
		
		writingContext.revert();
		
		if (this.object != null) {
			writingContext.deleteObject(this.object);
		} else if (this.globalID == null) {
			return page().previousPage();
		}
		
		this.isDeletion = true;
		this.needsToInitialize = true;
		
		initializeController();
		
		return page();
	}
	
	
	public WOActionResults saveAction()
	{
		EOEditingContext writingContext = writingContext();
		
		if (((EOContextLockingDelegate) writingContext.delegate()).hasConcurrentEdit()) {
			EOGlobalID editedGlobalID = writingContext.globalIDForObject(this.object);
			
			writingContext.invalidateObjectsWithGlobalIDs(new NSArray(editedGlobalID));
			_revert();
			
			sessionController().dispatchErrorMessage(
					sessionController().messageFactory().message("EDIT_CONCURRENT", null));
			
			return page().context().page();
		}
		
		boolean hasErrors = false;
		int dCount = this.orderedDescriptors.count();
		
		this.errorMessages.removeAllObjects();
		
		for (int d = 0; d < dCount; d++) {
			FormValueFieldDescriptor descriptor = (FormValueFieldDescriptor) this.orderedDescriptors
					.objectAtIndex(d);
			Value value = this.values.value(descriptor);
			String errorMessage = value.errorMessage();
			
			if (errorMessage != null) {
				hasErrors = true;
				
				this.errorMessages.addObject(errorMessage);
				value.addCssClass("error");
			}
		}
		
		if (hasErrors) {
			sessionController().dispatchErrorMessage(
					sessionController().messageFactory().message("EDIT_VALIDATION", null));
			
			return page().context().page();
		}
		
		boolean success = false;
		
		try {
			writingContext.saveChanges();
			
			success = true;
		} catch (NSValidation.ValidationException ve) {
			NSDictionary values = values().dictionary();
			NSArray keys = null;
			
			if (ve instanceof HEVValidationException) {
				HEVValidationException validationException = (HEVValidationException) ve;
				
				keys = validationException.allKeys();
			} else {
				keys = new NSArray(ve.key());
			}
			
			int kCount = keys.count();
			
			for (int k = 0; k < kCount; k++) {
				String key = (String) keys.objectAtIndex(k);
				Value value = (Value) values.objectForKey(key);
				
				if (value != null) {
					value.addCssClass("error");
				}
			}
			
			this.errorMessages.addObjectsFromArray(sessionController().messageFactory()
					.messages(ve));
			
			sessionController().dispatchErrorMessage(
					sessionController().messageFactory().message("EDIT_VALIDATION", null));
		} catch (EOGeneralAdaptorException gae) {
			int errorType = AdaptorExceptionUtilities.typeOfException(gae);
			EOGlobalID affectedGlobalID = AdaptorExceptionUtilities.affectedGlobalID(gae);
			
			switch (errorType) {
				case AdaptorExceptionUtilities.OPTIMISTIC_LOCK_FAILURE:
					writingContext.invalidateObjectsWithGlobalIDs(new NSArray(affectedGlobalID));
					_revert();
					
					sessionController().dispatchErrorMessage(
							sessionController().messageFactory().message("EDIT_CONCURRENT", null));
					
					break;
				
				case AdaptorExceptionUtilities.UNIQUE_CONSTRAINT_VIOLATED:
					EOGlobalID editedGlobalID = writingContext.globalIDForObject(this.object);
					
					if (editedGlobalID.equals(affectedGlobalID)) {
						writingContext
								.invalidateObjectsWithGlobalIDs(new NSArray(affectedGlobalID));
						_swapObject();
						
						sessionController().dispatchErrorMessage(
								sessionController().messageFactory().message("EDIT_UNIQUE", null));
					} else {
						throw new ApplicationException(gae, ApplicationException.SESSION_ALIVE);
					}
					break;
				
				default:
					throw new ApplicationException(gae, ApplicationException.SESSION_ALIVE);
			}
		} catch (Exception e) {
			throw new ApplicationException(e, ApplicationException.SESSION_ALIVE);
		}
		
		if (success) {
			return nextPage();
		}
		
		return page().context().page();
	}
	
	
	protected WOActionResults nextPage()
	{
		ControllerPage nextPage;
		
        if ((this.globalID != null) && !((GenericRecord) this.object).isDeleted()) { 
			nextPage = session().pageWithEntityAndTask(entityName(), Application.DETAIL_TASK, 
					page().context()); 
                
        } else { 
			nextPage = session().pageWithEntityAndTask(entityName(), Application.LIST_TASK, 
					page().context()); 
        } 

		AbstractPageController controller = nextPage.controller();
		
		controller.setEditingContext(null);
		
		EOEditingContext destinationContext = controller.editingContext();
		
        if (controller instanceof DisplayGroupController) { 
			DisplayGroupController displayGroupController = (DisplayGroupController) controller; 
			EOEnterpriseObject targetObject = null; 
			
			if (!((GenericRecord) this.object).isDeleted()) { 
				targetObject = ControlUtilities.localInstanceOfObject(destinationContext, this.object); 
			}

			EODataSource destinationDataSource = null;
			NSArray sortOrderings = null;
			
			if ((this.displayGroup != null) && (this.displayGroup.dataSource() != null)
					&& (targetObject != null)) {
				destinationDataSource = localDataSource(destinationContext, this.displayGroup
						.dataSource());
				sortOrderings = this.displayGroup.sortOrderings();
				
				NSArray objects = destinationDataSource.fetchObjects();
				
				if (!objects.containsObject(targetObject)) {
					destinationDataSource = null;
				}
			}
			
			if (destinationDataSource == null) {
				EODatabaseDataSource databaseDataSource = new EODatabaseDataSource(
						destinationContext, this.object.classDescription().entityName());
				EOEntity entity = databaseDataSource.entity();
				EOQualifier qualifier = entity.qualifierForPrimaryKey(EOUtilities
						.primaryKeyForObject(writingContext(), this.object));
				EOFetchSpecification fetchspecification = new EOFetchSpecification(entity.name(),
						qualifier, null);
				
				fetchspecification.setRefreshesRefetchedObjects(true);
				
				databaseDataSource.setFetchSpecification(fetchspecification);
				
				destinationDataSource = databaseDataSource;
			}
			
			WODisplayGroup destinationDisplayGroup = sessionController().createDisplayGroup();
			
			destinationDisplayGroup.setDataSource(destinationDataSource);
			destinationDisplayGroup.setSortOrderings(sortOrderings);
			destinationDisplayGroup.fetch();
			
			destinationDisplayGroup.setSelectedObject(targetObject);
			destinationDisplayGroup.displayBatchContainingSelectedObject();
			
			displayGroupController.setDisplayGroup(destinationDisplayGroup);
		}
		
		nextPage.setPreviousPage(null);
		
		return nextPage;
	}
	
	
	public WOActionResults cancelAction()
	{
		_revert();
		
		if (this.isDeletion) {
			return page().previousPage();
		} else {
			return page().context().page();
		}
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.houdah.web.control.controllers.AbstractPageController#willTakeValuesFromRequest(com.webobjects.appserver.WORequest,
	 *      com.webobjects.appserver.WOContext)
	 */
	public void willTakeValuesFromRequest(WORequest request, WOContext context)
	{
		super.willTakeValuesFromRequest(request, context);
		
		clearErrors();
	}
	
	
	
	// Initialization
	
	
	/**
	 * Generate the descriptor for the component's fieldset. Called during
	 * initialization.
	 * 
	 * @param values
	 *            the values dictionary
	 * @return the descriptors
	 */
	protected NSArray generateEditFieldsets()
	{
		return new NSArray(new FieldsetDescriptor[] { generateEditFieldset(), controlFieldset() });
	}
	
	
	
	/**
	 * Generate the descriptor for the component's fieldset. Called during
	 * initialization.
	 * 
	 * @param values
	 *            the values dictionary
	 * @return the descriptors
	 */
	protected FieldsetDescriptor generateEditFieldset()
	{
		throw new RuntimeException("Subclasses should override EITHER "
				+ "generateEditFieldsets() or generateEditFieldset()");
	}
	
	
	
	/**
	 * Generate the descriptor for the error messages list. Called during
	 * initialization.
	 * 
	 * @return the descriptor
	 */
	protected SimpleListDescriptor generateErrorListDescriptor()
	{
		return new SimpleListDescriptor(new SimpleListItemTextDescriptor(new KeyFormatterFormatter(
				null, null), false));
	}
	
	
	
	/**
	 * Create a fresh values container
	 * 
	 * @param fieldsetDescriptor
	 *            the deacriptor
	 * @return values the values container
	 */
	protected ValueContainer prepareValues(NSArray fieldsetDescriptors)
	{
		ValueContainer values = new ValueContainer(this);
		
		return values;
	}
	
	
	protected void _setObject()
	{
		EOEditingContext writingContext = writingContext();
		
		if (this.globalID != null) {
			writingContext.setFetchTimestamp(System.currentTimeMillis());
			writingContext.invalidateObjectsWithGlobalIDs(new NSArray(this.globalID));
			
			EOEnterpriseObject object = writingContext.faultForGlobalID(this.globalID,
					writingContext);
			
			if ((this.displayGroup != null) && (this.displayGroup.dataSource() != null)) {
				EODataSource dataSource = localDataSource(writingContext, this.displayGroup
						.dataSource());
				
				dataSource.insertObject(object);
			}
			
			setObject(object);
		} else if ((this.displayGroup != null) && (this.displayGroup.dataSource() != null)) {
			EODataSource dataSource = localDataSource(writingContext, this.displayGroup
					.dataSource());
			EOEnterpriseObject object = (EOEnterpriseObject) dataSource.createObject();
			
			
			// Allow for object initialization or substitution
			object = prepareNewObject(object);
			
			dataSource.insertObject(object);
			
			setObject(object);
		} else {
			EOEnterpriseObject object = EOUtilities.createAndInsertInstance(writingContext,
					entityName());
			
			setObject(prepareNewObject(object));
		}
		
		if (this.isDeletion) {
			writingContext.deleteObject(this.object);
		}
	}
	
	
	protected void _swapObject()
	{
		if (this.object != null) {
			EOEnterpriseObject tmpObject;
			
			if (isNew()) {
				tmpObject = prepareNewObject(this.object);
			} else {
				tmpObject = this.object;
			}
			
			Enumeration fieldSets = this.editFieldsets.objectEnumerator();
			
			while (fieldSets.hasMoreElements()) {
				FieldsetDescriptor fieldSet = (FieldsetDescriptor) fieldSets.nextElement();
				NSArray rows = fieldSet.rows();
				int rCount = rows.count();
				
				for (int r = 0; r < rCount; r++) {
					FieldsetRowDescriptor row = (FieldsetRowDescriptor) rows.objectAtIndex(r);
					NSArray fields = row.fields();
					int fCount = fields.count();
					
					for (int f = 0; f < fCount; f++) {
						FormFieldDescriptor descriptor = (FormFieldDescriptor) fields
								.objectAtIndex(f);
						
						if (descriptor instanceof FormValueFieldDescriptor) {
							FormValueFieldDescriptor valueFieldDescriptor = (FormValueFieldDescriptor) descriptor;
							Value value = this.values.value(valueFieldDescriptor);
							
							if (!value.readOnly()) {
								
								String key = valueFieldDescriptor.key();
								Object objectValue = value.value();
								
								if (objectValue instanceof NSSet) {
									NSMutableSet missing = ((NSSet) objectValue).mutableClone();
									Object existing = tmpObject.valueForKeyPath(key);
									
									if (existing instanceof NSArray) {
										Enumeration existingEnumeration = ((NSArray) existing)
												.immutableClone().objectEnumerator();
										
										while (existingEnumeration.hasMoreElements()) {
											EORelationshipManipulation element = (EORelationshipManipulation) existingEnumeration
													.nextElement();
											
											if (missing.containsObject(element)) {
												missing.removeObject(element);
											} else {
												tmpObject
														.removeObjectFromBothSidesOfRelationshipWithKey(
																element, key);
											}
										}
										
										Enumeration missingEnumeration = missing.objectEnumerator();
										
										while (missingEnumeration.hasMoreElements()) {
											EORelationshipManipulation element = (EORelationshipManipulation) missingEnumeration
													.nextElement();
											
											tmpObject.addObjectToBothSidesOfRelationshipWithKey(
													element, key);
										}
									} else if (missing.count() == 1) {
										tmpObject.takeValueForKeyPath(missing.anyObject(), key);
									}
								} else {
									tmpObject.takeValueForKeyPath(objectValue, key);
								}
							}
						}
					}
				}
				
			}
			this.object = tmpObject;
		}
	}
	
	
	protected void _revert()
	{
		writingContext().revert();
		_setObject();
	}
	
	
	protected void clearErrors()
	{
		int dCount = this.orderedDescriptors.count();
		
		for (int d = 0; d < dCount; d++) {
			FormValueFieldDescriptor descriptor = (FormValueFieldDescriptor) this.orderedDescriptors
					.objectAtIndex(d);
			Value value = this.values.value(descriptor);
			String errorMessage = value.errorMessage();
			
			if (errorMessage == null) {
				value.removeCssClass("error");
			}
		}
		
		this.errorMessages.removeAllObjects();
	}
	
	
	
	/**
	 * Create a local 'clone' of the data source. The clone must allow for
	 * creation of objects in the local editing context.
	 * 
	 * Subclasses may override this to support additional types of data sources.
	 * 
	 * @param editingContext
	 *            local / target editing context
	 * @param dataSource
	 *            data source to replicate
	 * @return a data source set in the local editing context
	 */
	protected EODataSource localDataSource(EOEditingContext editingContext, EODataSource dataSource)
	{
		if (dataSource instanceof EODatabaseDataSource) {
			EODatabaseDataSource databaseDataSource = (EODatabaseDataSource) dataSource;
			EODatabaseDataSource localDataSource = new EODatabaseDataSource(editingContext,
					databaseDataSource.entity().name());
			EOFetchSpecification fetchSpecification = databaseDataSource
					.fetchSpecificationForFetch();
			
			fetchSpecification.setSortOrderings(databaseDataSource.fetchSpecification()
					.sortOrderings());
			
			localDataSource.setFetchSpecification(fetchSpecification);
			
			return localDataSource;
		} else if (dataSource instanceof EODetailDataSource) {
			EODetailDataSource detailDataSource = (EODetailDataSource) dataSource;
			EODetailDataSource localDataSource = new EODetailDataSource(detailDataSource
					.masterClassDescription(), detailDataSource.detailKey());
			EOEnterpriseObject masterObject = (EOEnterpriseObject) detailDataSource.masterObject();
			EOEnterpriseObject localMasterObject = EOUtilities.localInstanceOfObject(
					editingContext, masterObject);
			
			localDataSource.qualifyWithRelationshipKey(detailDataSource.detailKey(),
					localMasterObject);
			
			return localDataSource;
		} else {
			throw new IllegalArgumentException(
					"Cannot create local instance of unknown data source: " + dataSource);
		}
	}
	
	
	protected void setObject(EOEnterpriseObject object)
	{
		this.object = object;
		this.needsToInitialize = true;
		
		initializeController();
		
		this.values = prepareValues(this.editFieldsets);
		
		NSNotificationCenter notificationCenter = NSNotificationCenter.defaultCenter();
		
		notificationCenter.removeObserver(this, Value.VALUE_CHANGED_NOTIFICATION, null);
		
		Enumeration fieldsets = this.editFieldsets.objectEnumerator();
		
		while (fieldsets.hasMoreElements()) {
			FieldsetDescriptor fieldset = (FieldsetDescriptor) fieldsets.nextElement();
			NSArray rows = fieldset.rows();
			int rCount = rows.count();
			NSSelector selector = new NSSelector(
					AbstractEditPageController.VALUE_CHANGED_NOTIFICATION,
					new Class[] { NSNotification.class });
			
			for (int r = 0; r < rCount; r++) {
				FieldsetRowDescriptor row = (FieldsetRowDescriptor) rows.objectAtIndex(r);
				NSArray fields = row.fields();
				int fCount = fields.count();
				
				for (int f = 0; f < fCount; f++) {
					FormFieldDescriptor descriptor = (FormFieldDescriptor) fields.objectAtIndex(f);
					
					if (descriptor instanceof FormValueFieldDescriptor) {
						FormValueFieldDescriptor valueFieldDescriptor = (FormValueFieldDescriptor) descriptor;
						Value value = this.values.value(valueFieldDescriptor);
						Object objectValue = NSKeyValueCodingAdditions.Utility.valueForKeyPath(
								this.object, valueFieldDescriptor.key());
						
						value.setValue(objectValue);
						value.userInfo().setObjectForKey(valueFieldDescriptor,
								AbstractEditPageController.DESCRIPTOR_KEY);
						
						notificationCenter.addObserver(this, selector,
								Value.VALUE_CHANGED_NOTIFICATION, value);
					}
				}
			}
		}
	}
	
	
	
	/**
	 * Call back method allowing subclasses to initialize a newly created
	 * object.
	 * 
	 * An alternative object may be substituted provided the object passed in as
	 * an argument is correctly disposed of.
	 * 
	 * @param object
	 *            newly created object
	 * @return fully initialized obbject
	 */
	protected EOEnterpriseObject prepareNewObject(EOEnterpriseObject object)
	{
		return object;
	}
	
	
	protected FormActionFieldDescriptor saveActionField()
	{
		if (AbstractEditPageController.saveActionField == null) {
			Action action = new Action(AbstractEditPageController.SAVE_ACTION, "Save", "save",
					AbstractEditPageController.SAVE_ACTION_CSS_CLASS);
			
			AbstractEditPageController.saveActionField = new FormActionFieldDescriptor(action);
		}
		
		return AbstractEditPageController.saveActionField;
	}
	
	
	protected FormActionFieldDescriptor cancelActionField()
	{
		if (AbstractEditPageController.cancelActionField == null) {
			Action action = new Action(AbstractEditPageController.CANCEL_ACTION, "Revert",
					"cancel", AbstractEditPageController.CANCEL_ACTION_CSS_CLASS);
			
			AbstractEditPageController.cancelActionField = new FormActionFieldDescriptor(action);
		}
		
		return AbstractEditPageController.cancelActionField;
	}
	
	
	protected FieldsetDescriptor controlFieldset()
	{
		FieldsetRowDescriptor rowDescriptor = new FieldsetRowDescriptor(new NSArray(
				new FormFieldDescriptor[] { cancelActionField(), saveActionField() }));
		
		return new FieldsetDescriptor(new NSArray(rowDescriptor), null, null, null);
		
	}
	
	
	
	// Protected instance methods
	
	
	// Private instance methods
	
	private NSArray generateOrderedValueDescriptors(NSArray fieldsets)
	{
		NSMutableArray orderedValueDescriptors = new NSMutableArray();
		int sCount = fieldsets.count();
		
		for (int s = 0; s < sCount; s++) {
			FieldsetDescriptor fieldsetDescriptor = (FieldsetDescriptor) fieldsets.objectAtIndex(s);
			NSArray rows = fieldsetDescriptor.rows();
			int rCount = rows.count();
			
			for (int r = 0; r < rCount; r++) {
				FieldsetRowDescriptor rowDescriptor = (FieldsetRowDescriptor) rows.objectAtIndex(r);
				NSArray fields = rowDescriptor.fields();
				int fCount = fields.count();
				
				for (int f = 0; f < fCount; f++) {
					FormFieldDescriptor descriptor = (FormFieldDescriptor) fields.objectAtIndex(f);
					
					if (descriptor instanceof FormValueFieldDescriptor) {
						orderedValueDescriptors.addObject(descriptor);
					}
				}
			}
		}
		
		return orderedValueDescriptors.immutableClone();
	}
	
	
	
	
	// Inner class
	
	/**
	 * Delegate to the editing contexts used in the edit components. Used to
	 * detec concurrent modifications whithin the same EOF stack.<br/>
	 * 
	 * TBD: This implementation does not exactely mimick the behavior of
	 * optimistic locking. Thus concurrent modifications whithin the same stack
	 * are not detected based on the same criteria as external modifications.<br/>
	 * 
	 * The idea is to record a 'snapshot' of an object shortly before external
	 * changes are merged in. Once the changes were applied, I verify the object
	 * against the recorded snapshot. The big catch here is that what I get as a
	 * snapshot from the editing context is more like a dump of the object into
	 * an NSDictionary: it contains only the class properties and there are
	 * relationships rather than foreign keys.<br/>
	 * 
	 * In order to handle optimistic locking correctly, I would need to get
	 * access to a raw data snapshot: a dump of an entry in a table. Indeed, to
	 * get the same optimistic locking behavior as I get with external changes,
	 * I would need a raw snapshot and restrict it to only the keys selected for
	 * locking in EOModeler. The keys mapped to attributes or relationships are
	 * however a different set: I lock on a few attributes to many as I lock on
	 * relationships even if they map a foreign key not set for locking. I may
	 * also miss a few attributes as not all attributes need to be mapped into
	 * the EO.<br/>
	 * 
	 * Yes, I tried getting a raw snapshot from the database context.
	 * Unfortunately, by the time I get the notification that snapshot has
	 * already been updated.<br/>
	 * 
	 * <B>CAVEAT:</B> The delegate may not be shared between different editing
	 * contexts.
	 */
	protected static class EOContextLockingDelegate
	{
		// Protected instance variables
		
		protected Hashtable	snapshots;
		
		
		protected boolean	hasConcurrentEdit;
		
		
		
		
		// Constructor
		
		protected EOContextLockingDelegate()
		{
			this.snapshots = new Hashtable();
			this.hasConcurrentEdit = false;
		}
		
		
		
		// Public instance methods
		
		/**
		 * EOEditingContext.Delegate implementation used to spot concurrent edit
		 * operations within the same EOF stack. Called for each object that has
		 * updates in the current editing context and that was modified
		 * externally.
		 */
		public boolean editingContextShouldMergeChangesForObject(EOEditingContext editingContext,
				EOEnterpriseObject object)
		{
			EOGlobalID globalID = editingContext.globalIDForObject(object);
			NSDictionary snapshot = object.snapshot();
			
			
			/*
			 * // Actually we should use the snapshot from the database context
			 * level and filter it according to
			 * entity.attributesUsedForLocking(). // Unfortunately that snapshot
			 * has already been updated with the new values when this delegate
			 * is called. So this approach is possible only // by subclassing
			 * EOEditingContext. NSDictionary contextSnapshot; EOEntity entity =
			 * EOUtilities.entityForObject(editingContext, object);
			 * EODatabaseContext context =
			 * EODatabaseContext.registeredDatabaseContextForModel(entity.model(),
			 * editingContext);
			 * 
			 * context.lock(); contextSnapshot =
			 * context.snapshotForGlobalID(globalID)); context.unlock();
			 */

			synchronized (this) {
				this.snapshots.put(globalID, snapshot);
			}
			
			return true;
		}
		
		
		
		/**
		 * EOEditingContext.Delegate implementation used to spot concurrent edit
		 * operations within the same EOF stack. Called once all changes have
		 * been merged into the current context.
		 */
		public void editingContextDidMergeChanges(EOEditingContext editingContext)
		{
			synchronized (this) {
				Enumeration ids = this.snapshots.keys();
				
				while (ids.hasMoreElements() && (!this.hasConcurrentEdit)) {
					EOGlobalID globalID = (EOGlobalID) ids.nextElement();
					EOEnterpriseObject object = editingContext.faultForGlobalID(globalID,
							editingContext);
					NSDictionary changes = object.changesFromSnapshot((NSDictionary) this.snapshots
							.get(globalID));
					
					if (changes.count() > 0) {
						this.hasConcurrentEdit = true;
					}
					
					object.willChange();
				}
				
				this.snapshots.clear();
			}
		}
		
		
		
		// Protected instance methods
		
		protected synchronized boolean hasConcurrentEdit()
		{
			return this.hasConcurrentEdit;
		}
		
		
		protected synchronized void setHasConcurrentEdit(boolean hasConcurrentEdit)
		{
			this.hasConcurrentEdit = hasConcurrentEdit;
		}
	}
}