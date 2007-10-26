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

import com.houdah.web.control.actions.NavigationAction;
import com.houdah.web.control.application.Application;
import com.houdah.web.control.components.HCCDetailComponent;
import com.houdah.web.control.components.ControllerPage;
import com.houdah.web.view.actions.Action;
import com.houdah.web.view.list.descriptors.ListDescriptor;
import com.houdah.web.view.list.descriptors.ListPropertyDescriptor;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.eocontrol.EOClassDescription;
import com.webobjects.eocontrol.EODetailDataSource;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.foundation.NSArray;

public abstract class AbstractDetailPageController extends AbstractPageController implements
		DisplayGroupController
{
	// Public class constants
	
	public static final String		NAVIGATE_ACTION_PREFIX	= "navigateAction_";
	
	
	public static final String		EDIT_ACTION				= "editAction";
	
	public static final String		DELETE_ACTION				= "deleteAction";
	
	
	
	// Private class constants
	
	private static final NSArray	PERSISTENT_KEYS			= new NSArray(new String[] {
			"displayGroup", "displayGroup.selectedObject"	});
	
	
	
	// Private class variables
	
	// Private instance variables
	
	private WODisplayGroup			displayGroup			= null;
	
	
	private ListDescriptor			listDescriptor			= null;
	
	
	private ListPropertyDescriptor	property				= null;
	
	
	private boolean					displayGroupNeedsInit	= true;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param entityName
	 *            name of the entity to work on
	 * @param task
	 *            name of the task to perform
	 */
	public AbstractDetailPageController(String entityName, String task)
	{
		super(entityName, task);
	}
	
	
	
	// Public instance methods
	
	public WODisplayGroup displayGroup()
	{
		return this.displayGroup;
	}
	
	
	public void setDisplayGroup(WODisplayGroup displayGroup)
	{
		this.displayGroup = displayGroup;
		this.displayGroupNeedsInit = true;
	}
	
	
	
	// Page controller methods
	
	public void willInitializePage()
	{
		this.listDescriptor = generateListDescriptor();
		
		super.willInitializePage();
	}
	
	
	public void willAwake()
	{
		if (this.displayGroupNeedsInit) {
			WODisplayGroup displayGroup = displayGroup();
			
			if (displayGroup == null) {
				throw new IllegalStateException("A display group is needed");
			} else {
				displayGroup.fetch();
				
				if (displayGroup.selectedObject() == null) {
					displayGroup.setSelectionIndexes(new NSArray(new Integer(0)));
				}
			}
			
			this.displayGroupNeedsInit = false;
		}
		
		super.willAwake();
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#handleQueryWithUnboundKey(java.lang.String)
	 */
	public Object handleQueryWithUnboundKey(String key)
	{
		if (key.startsWith(NAVIGATE_ACTION_PREFIX)) {
			return navigateAction(key.substring(NAVIGATE_ACTION_PREFIX.length()));
		} else {
			return super.handleQueryWithUnboundKey(key);
		}
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
		return PERSISTENT_KEYS;
	}
	
	
	
	// Configuration methods
	
	public String controllerComponentName()
	{
		return HCCDetailComponent.class.getName();
	}
	
	
	
	// Protected instance methods
	
	protected ListDescriptor listDescriptor()
	{
		return this.listDescriptor;
	}
	
	
	protected void setProperty(ListPropertyDescriptor property)
	{
		this.property = property;
	}
	
	
	protected ListPropertyDescriptor property()
	{
		return this.property;
	}
	
	
	protected boolean mayEdit()
	{
		return canEdit();
	}
	
	
	protected final boolean canEdit()
	{
		return !entity().isReadOnly();
	}
	
	protected boolean mayDelete()
	{
		return canDelete();
	}
	
	
	protected final boolean canDelete()
	{
		return !entity().isReadOnly();
	}
	
	
	protected String editActionLabel()
	{
		return "Edit";
	}
	
	
	protected String deleteActionLabel()
	{
		return "Delete";
	}
	
	
	protected String previousActionLabel()
	{
		return "previous";
	}
	
	
	protected String nextActionLabel()
	{
		return "next";
	}
	
	
	
	// Action methods
	
	protected WOActionResults editAction()
	{
		ControllerPage nextPage = session().pageWithEntityAndTask(entityName(),
				Application.EDIT_TASK, page().context());
		AbstractEditPageController controller = (AbstractEditPageController) nextPage.controller();
		WODisplayGroup displayGroup = displayGroup();
		EOEnterpriseObject object = (EOEnterpriseObject) displayGroup.selectedObject();
		EOEditingContext editingContext = object.editingContext();
		
		controller.setObject(editingContext.globalIDForObject(object), displayGroup);
		
		return nextPage;
	}
	
	
	protected WOActionResults deleteAction()
	{
		ControllerPage nextPage = session().pageWithEntityAndTask(entityName(),
				Application.EDIT_TASK, page().context());
		AbstractEditPageController controller = (AbstractEditPageController) nextPage.controller();
		WODisplayGroup displayGroup = displayGroup();
		EOEnterpriseObject object = (EOEnterpriseObject) displayGroup.selectedObject();
		EOEditingContext editingContext = object.editingContext();
		
		controller.setObject(editingContext.globalIDForObject(object), displayGroup);
		
		return controller.deleteObject();
	}
	
	
	
	/**
	 * Navigate a to-one or to-many relationship.
	 * 
	 * @param keyPath
	 *            path to follow, required
	 * @return configured page
	 */
	protected WOActionResults navigateAction(String keyPath)
	{
		WODisplayGroup displayGroup = displayGroup();
		EOEnterpriseObject object = (EOEnterpriseObject) displayGroup.selectedObject();
		
		if (keyPath.startsWith("@")) {
			String propertyPath = keyPath.substring(1);
			
			keyPath = (String) object.valueForKeyPath(propertyPath);
		}
		
		Object destination = object.valueForKeyPath(keyPath);
		
		if (destination instanceof NSArray) {
			EOClassDescription masterClassDescription = object.classDescription();
			EOClassDescription destinationClassDescription = masterClassDescription
					.classDescriptionForKeyPath(keyPath);
			
			ControllerPage nextPage = session().pageWithEntityAndTask(
					destinationClassDescription.entityName(), Application.LIST_TASK,
					page().context());
			AbstractListPageController controller = (AbstractListPageController) nextPage
					.controller();
			
			WODisplayGroup destinationDisplayGroup = controller.displayGroup();
			EODetailDataSource destinationDataSource = (EODetailDataSource) displayGroup
					.dataSource().dataSourceQualifiedByKey(keyPath);
			
			destinationDataSource.qualifyWithRelationshipKey(keyPath, object);
			
			destinationDisplayGroup.setDataSource(destinationDataSource);
			destinationDisplayGroup.fetch();
			
			controller.setDisplayGroup(destinationDisplayGroup);
			
			return nextPage;
		} else if (destination instanceof EOEnterpriseObject) {
			EOEnterpriseObject destinationObject = (EOEnterpriseObject) destination;
			WODisplayGroup destinationDisplayGroup = sessionController().createDisplayGroup();
			EODetailDataSource destinationDataSource = (EODetailDataSource) displayGroup
					.dataSource().dataSourceQualifiedByKey(keyPath);
			
			destinationDataSource.setMasterClassDescription(object.classDescription());
			destinationDataSource.qualifyWithRelationshipKey(keyPath, object);
			
			ControllerPage nextPage = session().pageWithEntityAndTask(
					destinationObject.entityName(), Application.DETAIL_TASK, page().context());
			AbstractDetailPageController controller = (AbstractDetailPageController) nextPage
					.controller();
			
			destinationDisplayGroup.setDataSource(destinationDataSource);
			destinationDisplayGroup.fetch();
			destinationDisplayGroup.setSelectedObject(destinationObject);
			
			controller.setDisplayGroup(destinationDisplayGroup);
			
			return nextPage;
		} else {
			throw new IllegalArgumentException("Navigate keyPath is not a relationship");
		}
	}
	
	
	
	// Initialization
	
	/**
	 * Creates the descriptor for the list. Called during component
	 * initialization
	 * 
	 * @return an immutable descriptor
	 */
	protected abstract ListDescriptor generateListDescriptor();
	
	
	protected Action createNavigateAction(String keyPath, String label, String title,
			String cssClass)
	{
		return new NavigationAction(keyPath, label, title, cssClass);
	}
}