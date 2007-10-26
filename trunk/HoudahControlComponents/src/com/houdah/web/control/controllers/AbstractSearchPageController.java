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

import com.houdah.foundation.formatters.KeyFormatterFormatter;
import com.houdah.web.control.application.Application;
import com.houdah.web.control.components.HCCSearchComponent;
import com.houdah.web.control.components.ControllerPage;
import com.houdah.web.view.actions.Action;
import com.houdah.web.view.form.descriptors.FieldsetDescriptor;
import com.houdah.web.view.form.descriptors.FormActionFieldDescriptor;
import com.houdah.web.view.form.values.ValueContainer;
import com.houdah.web.view.simplelist.descriptors.SimpleListDescriptor;
import com.houdah.web.view.simplelist.descriptors.SimpleListItemTextDescriptor;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.appserver.WORequest;
import com.webobjects.eoaccess.EODatabaseDataSource;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

public abstract class AbstractSearchPageController extends AbstractFormPageController
{
	// Public class constants
	
	public static final String					SEARCH_ACTION			= "searchAction";
	
	
	public static final String					SEARCH_ACTION_CSS_CLASS	= "searchAction";
	
	
	
	// Private class constants
	
	private static final NSArray				PERSISTENT_KEYS			= new NSArray(
																				new String[] { "values" });
	
	
	
	// Private class variables
	
	private static FormActionFieldDescriptor	searchActionField		= null;
	
	
	
	// Protected instance variables
	
	protected NSMutableArray					errorMessages			= null;
	
	
	
	// Private instance variables
	
	private NSArray								searchFieldsets			= null;
	
	
	private FieldsetDescriptor					fieldsetDescriptor		= null;
	
	
	private SimpleListDescriptor				errorListDescriptor		= null;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param entityName
	 *            name of the entity to work on
	 * @param task
	 *            name of the task to perform
	 */
	public AbstractSearchPageController(String entityName, String task)
	{
		super(entityName, task);
	}
	
	
	
	// Public instance methods
	
	
	// Page controller methods
	
	public void willInitializePage()
	{
		this.searchFieldsets = generateMultipleSearchFieldsets().immutableClone();
		this.values = new ValueContainer(this);
		this.errorMessages = new NSMutableArray();
		
		if (this.errorListDescriptor == null) {
			this.errorListDescriptor = generateErrorListDescriptor();
		}
		
		super.willInitializePage();
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
	
	
	
	// Configuration methods
	
	public String controllerComponentName()
	{
		return HCCSearchComponent.class.getName();
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
	
	
	protected NSArray searchFieldsets()
	{
		return this.searchFieldsets;
	}
	
	
	
	/**
	 * Search query validation.
	 * 
	 * This call-back method gives subclasses the oportunity to validate and
	 * veto searches in the current context.
	 * 
	 * It is considered good form to post a warning to the message server when
	 * returning false from this method.
	 * 
	 * @param the
	 *            fetch specification to run. May be modified
	 * @return true in the default implementation
	 */
	protected boolean preFlightChecks(EOFetchSpecification fetchSpecification)
	{
		return true;
	}
	
	
	
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
	
	
	protected void clearErrors()
	{
		this.errorMessages.removeAllObjects();
	}
	
	
	
	// Action methods
	
	protected WOActionResults searchAction()
	{
		String entityName = entityName();
		ControllerPage nextPage = session().pageWithEntityAndTask(entityName,
				Application.LIST_TASK, page().context());
		AbstractListPageController controller = (AbstractListPageController) nextPage.controller();
		
		EOFetchSpecification template = fetchSpecificationTemplate();
		EOEditingContext targetContext = controller.createEditingContext();
		
		EOFetchSpecification fetchSpecification = bindFetchSpecification(template, targetContext);
		
		if (preFlightChecks(fetchSpecification)) {
			controller.setEditingContext(targetContext);
			
			WODisplayGroup displayGroup = controller.displayGroup();
			EODatabaseDataSource dataSource = new EODatabaseDataSource(targetContext, entityName);
			
			dataSource.setFetchSpecification(fetchSpecification);
			displayGroup.setDataSource(dataSource);
			
			return nextPage;
		}
		
		sessionController().dispatchErrorMessage(
				sessionController().messageFactory().message("SEARCH_PRE_FLIGHT", null));
		
		return page().context().page();
	}
	
	
	
	// Initialization
	
	/**
	 * Generate the descriptor for the component's fieldsets. Called during
	 * initialization.
	 * 
	 * Subclasses should override EITHER generateMultipleSearchFieldsets() or
	 * generateSingleSearchFieldset()
	 * 
	 * @see generateSingleSearchFieldset()
	 * @return the descriptors
	 */
	protected NSArray generateMultipleSearchFieldsets()
	{
		return new NSArray(new FieldsetDescriptor[] { generateSingleSearchFieldset() });
	}
	
	
	
	/**
	 * Creates the descriptor for the search form. Called during component
	 * initialization
	 * 
	 * @return an immutable descriptor
	 */
	protected FieldsetDescriptor generateSingleSearchFieldset()
	{
		throw new RuntimeException("Subclasses should override EITHER "
				+ "generateMultipleSearchFieldsets() or generateSingleSearchFieldset()");
	}
	
	
	protected FormActionFieldDescriptor searchActionField()
	{
		if (AbstractSearchPageController.searchActionField == null) {
			Action action = new Action(AbstractSearchPageController.SEARCH_ACTION, "Search",
					Application.SEARCH_TASK, AbstractSearchPageController.SEARCH_ACTION_CSS_CLASS);
			
			AbstractSearchPageController.searchActionField = new FormActionFieldDescriptor(action);
		}
		
		return AbstractSearchPageController.searchActionField;
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
}