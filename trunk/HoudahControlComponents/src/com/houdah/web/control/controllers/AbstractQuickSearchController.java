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

import com.houdah.foundation.Delegate;
import com.houdah.web.control.application.Application;
import com.houdah.web.control.components.HCCQuickSearchComponent;
import com.houdah.web.view.actions.Action;
import com.houdah.web.view.form.descriptors.FieldsetDescriptor;
import com.houdah.web.view.form.descriptors.FormActionFieldDescriptor;
import com.houdah.web.view.form.values.ValueContainer;
import com.houdah.web.view.simplelist.descriptors.SimpleListDescriptor;
import com.houdah.web.view.simplelist.descriptors.SimpleListItemActionDescriptor;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.foundation.NSArray;

public abstract class AbstractQuickSearchController extends AbstractFormPageController
{
	// Public class constants
	
	public static final String					QUICK_SEARCH_ACTION				= "searchAction";
	
	
	public static final String					QUICK_SELECT_ACTION				= "selectAction";
	
	
	public static final String					QUICK_SEARCH_ACTION_CSS_CLASS	= "quickSearchAction";
	
	
	public static final String					QUICK_SELECT_ACTION_CSS_CLASS	= "quickSelectAction";
	
	
	
	// Private class constants
	
	private static final NSArray				PERSISTENT_KEYS					= new NSArray(
																						"values");
	
	
	
	// Private instance variables
	
	private FieldsetDescriptor					fieldsetDescriptor				= null;
	
	
	private SimpleListDescriptor				simpleListDescriptor			= null;
	
	
	private Delegate							delegate						= null;
	
	
	private Object								delegateContext					= null;
	
	
	private NSArray								results							= null;
	
	
	private Object								currentObject					= null;
	
	
	
	// Private class variables
	
	private static FormActionFieldDescriptor	quickSearchActionField			= null;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param entityName
	 *            name of the entity to work on
	 * @param task
	 *            name of the task to perform
	 */
	public AbstractQuickSearchController(String entityName, String task)
	{
		super(entityName, task);
	}
	
	
	
	// Public instance methods
	
	// Page controller methods
	
	public void willInitializePage()
	{
		this.fieldsetDescriptor = generateQuickSearchFieldsets();
		this.simpleListDescriptor = generateQuickSearchSimpleListDescriptor();
		this.values = new ValueContainer(this);
		
		page().setShowNavigationControls(false);
		
		super.willInitializePage();
	}
	
	
	
	// Accessors
	
	public Delegate delegate()
	{
		return this.delegate;
	}
	
	
	public void setDelegate(Object delegate)
	{
		this.delegate = new Delegate(AbstractQuickSearchController.QuickSearchDelegate.class,
				delegate);
	}
	
	
	public Object delegateContext()
	{
		return this.delegateContext;
	}
	
	
	public void setDelegateContext(Object delegateContext)
	{
		this.delegateContext = delegateContext;
	}
	
	
	protected ValueContainer values()
	{
		return this.values;
	}
	
	
	protected NSArray results()
	{
		return this.results;
	}
	
	
	protected Object currentObject()
	{
		return this.currentObject;
	}
	
	
	protected void setCurrentObject(Object currentObject)
	{
		this.currentObject = currentObject;
	}
	
	
	
	// Configuration methods
	
	public FieldsetDescriptor fieldsetDescriptor()
	{
		return this.fieldsetDescriptor;
	}
	
	
	public SimpleListDescriptor simpleListDescriptor()
	{
		return this.simpleListDescriptor;
	}
	
	
	public String controllerComponentName()
	{
		return HCCQuickSearchComponent.class.getName();
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
	
	
	
	// Protected instance methods
	
	protected FormActionFieldDescriptor quickSearchActionField()
	{
		if (AbstractQuickSearchController.quickSearchActionField == null) {
			Action action = new Action(AbstractQuickSearchController.QUICK_SEARCH_ACTION, "Search",
					Application.QUICK_SEARCH_TASK,
					AbstractQuickSearchController.QUICK_SEARCH_ACTION_CSS_CLASS);
			
			AbstractQuickSearchController.quickSearchActionField = new FormActionFieldDescriptor(
					action);
		}
		
		return AbstractQuickSearchController.quickSearchActionField;
	}
	
	
	
	// Action methods
	
	protected WOActionResults searchAction()
	{
		EOEditingContext editingContext = editingContext();
		EOFetchSpecification template = fetchSpecificationTemplate();
		EOFetchSpecification fetchSpecification = bindFetchSpecification(template, editingContext);
		
		this.results = editingContext.objectsWithFetchSpecification(fetchSpecification);
		
		return page().context().page();
	}
	
	
	protected WOActionResults selectAction()
	{
		Delegate delegateProxy = delegate();
		
		if ((delegateProxy != null)
				&& delegateProxy
						.respondsTo(AbstractQuickSearchController.QuickSearchDelegate.COMMIT_SELECTION)) {
			return (WOActionResults) delegateProxy.perform(
					AbstractQuickSearchController.QuickSearchDelegate.COMMIT_SELECTION,
					currentObject(), delegateContext());
		}
		
		return page().context().page();
	}
	
	protected void checkAccessRights()
	{
		// Don't perform the check!
	}
	
	
	// Initialization
	
	
	/**
	 * Produces a template fetch specification for searches.
	 * 
	 * The fetch specification may still have dangling bindings.
	 * 
	 * @return a fetch specification for the current entity
	 */
	protected abstract EOFetchSpecification fetchSpecificationTemplate();
	
	
	protected EOFetchSpecification bindFetchSpecification(EOFetchSpecification template,
			EOEditingContext targetContext)
	{
		return Toolbox.bindFetchSpecification(template, values(), targetContext);
	}
	
	
	
	/**
	 * Creates the descriptor for the quickSearch form.
	 * 
	 * @return an immutable descriptor
	 */
	protected abstract FieldsetDescriptor generateQuickSearchFieldsets();
	
	
	
	/**
	 * Creates the descriptor for the quickSearch result list.
	 * 
	 * @return an immutable descriptor
	 */
	protected SimpleListDescriptor generateQuickSearchSimpleListDescriptor()
	{
		Action action = new Action(AbstractQuickSearchController.QUICK_SELECT_ACTION, null, null,
				AbstractQuickSearchController.QUICK_SELECT_ACTION_CSS_CLASS);
		
		return new SimpleListDescriptor(new SimpleListItemActionDescriptor(action, Application
				.sharedInstance().entityDescriber(entityName()), false));
	}
	
	
	
	
	// Inner interface
	
	public interface QuickSearchDelegate
	{
		// Class constants
		
		String	COMMIT_SELECTION	= "commitSelection";
		
		
		
		
		// Public instance methods
		
		WOActionResults commitSelection(Object object, Object delegateContext);
	}
}