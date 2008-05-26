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

import com.houdah.web.control.application.Application;
import com.houdah.web.control.components.HCCListComponent;
import com.houdah.web.control.components.ControllerPage;
import com.houdah.web.view.table.descriptors.TableColumnDescriptor;
import com.houdah.web.view.table.descriptors.TableColumnHeaderDescriptor;
import com.houdah.web.view.table.descriptors.TableColumnRowTextDescriptor;
import com.houdah.web.view.table.descriptors.TableDescriptor;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.eoaccess.EODatabaseDataSource;
import com.webobjects.eocontrol.EOClassDescription;
import com.webobjects.eocontrol.EODataSource;
import com.webobjects.eocontrol.EODetailDataSource;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableSet;

public abstract class AbstractListPageController extends AbstractPageController implements
		DisplayGroupController
{
	// Public class constants
	
	public static final String		SORT_ACTION		= "sortAction";
	
	
	public static final String		DETAIL_ACTION	= "detailAction";
	
	
	public static final String		EDIT_ACTION		= "editAction";
	
	
	public static final String		CREATE_ACTION	= "createAction";
	
	
	
	// Private class constants
	
	private static final NSArray	PERSISTENT_KEYS	= new NSArray(new String[] { "displayGroup",
			"displayGroup.currentBatchIndex"		});
	
	
	
	// Private instance variables
	
	private WODisplayGroup			displayGroup	= null;
	
	
	private TableDescriptor			listTable;
	
	
	private TableColumnDescriptor	sortColumn		= null;
	
	
	private Boolean					sortAscending	= Boolean.TRUE;
	
	
	private NSMutableSet			selectedObjects	= null;
	
	
	
	
	// Constructor
	
	
	/**
	 * Designated constructor
	 * 
	 * @param entityName
	 *            name of the entity to work on
	 * @param task
	 *            name of the task to perform
	 */
	public AbstractListPageController(String entityName, String task)
	{
		super(entityName, task);
	}
	
	
	
	// Public instance methods
	
	public WODisplayGroup displayGroup()
	{
		if (this.displayGroup == null) {
			setDisplayGroup(sessionController().createDisplayGroup());
		}
		
		return this.displayGroup;
	}
	
	
	public void setDisplayGroup(WODisplayGroup displayGroup)
	{
		this.displayGroup = displayGroup;
	}
	
	
	
	// Page controller methods
	
	public void willInitializePage()
	{
		this.listTable = generateListTable();
		if (this.sortColumn == null) {
			this.sortColumn = defaultSortColumn(this.listTable);
		}
		this.selectedObjects = new NSMutableSet();
		
		WODisplayGroup myDisplayGroup = displayGroup();
		
		initDisplayGroup(myDisplayGroup);
		
		myDisplayGroup.fetch();
		
		super.willInitializePage();
	}
	
	
	
	// Configuration methods
	
	public String controllerComponentName()
	{
		return HCCListComponent.class.getName();
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
	
	
	
	// Delegate methods
	
	public WOActionResults commitSelection(Object object, Object delegateContext)
	{
		ControllerPage nextPage = session().pageWithEntityAndTask(entityName(),
				Application.EDIT_TASK, page().context());
		AbstractEditPageController controller = (AbstractEditPageController) nextPage.controller();
		WODisplayGroup myDisplayGroup = displayGroup();
		EOEnterpriseObject enterpriseObject = (EOEnterpriseObject) object;
		EOEditingContext editingContext = enterpriseObject.editingContext();
		
		controller.setObject(editingContext.globalIDForObject(enterpriseObject), myDisplayGroup);
		
		
		// Avoid returning on quickSearch.
		nextPage.setPreviousPage(page());
		
		return nextPage;
	}
	
	
	
	// Protected instance methods
	
	protected TableDescriptor listTable()
	{
		return this.listTable;
	}
	
	
	protected TableColumnDescriptor sortColumn()
	{
		return this.sortColumn;
	}
	
	
	protected void setSortColumn(TableColumnDescriptor sortColumn)
	{
		this.sortColumn = sortColumn;
	}
	
	
	protected Boolean sortAscending()
	{
		return this.sortAscending;
	}
	
	
	protected void setSortAscending(Boolean sortAscending)
	{
		this.sortAscending = sortAscending;
	}
	
	
	protected NSMutableSet selectedObjects()
	{
		return this.selectedObjects;
	}
	
	
	protected boolean mayCreate()
	{
		return canCreate();
	}
	
	
	protected final boolean canCreate()
	{
		EODataSource dataSource = displayGroup().dataSource();
		
		if (dataSource instanceof EODatabaseDataSource) {
			EODatabaseDataSource databaseDataSource = (EODatabaseDataSource) dataSource;
			
			return !databaseDataSource.entity().isAbstractEntity();
		} else if (dataSource instanceof EODetailDataSource) {
			EODetailDataSource detailDataSource = (EODetailDataSource) dataSource;
			EOClassDescription classDescription = detailDataSource.masterClassDescription();
			String detailKey = detailDataSource.detailKey();
			boolean isToMany = classDescription.toManyRelationshipKeys().containsObject(detailKey);
			boolean ownsDestination = classDescription
					.ownsDestinationObjectsForRelationshipKey(detailKey);
			
			return isToMany && ownsDestination;
		}
		
		return false;
	}
	
	
	protected boolean mayAdd()
	{
		return canAdd();
	}
	
	
	protected final boolean canAdd()
	{
		EODataSource dataSource = displayGroup().dataSource();
		
		if (dataSource instanceof EODatabaseDataSource) {
			return false;
		} else if (dataSource instanceof EODetailDataSource) {
			EODetailDataSource detailDataSource = (EODetailDataSource) dataSource;
			EOClassDescription classDescription = detailDataSource.masterClassDescription();
			String detailKey = detailDataSource.detailKey();
			boolean isToMany = classDescription.toManyRelationshipKeys().containsObject(detailKey);
			boolean ownsDestination = classDescription
					.ownsDestinationObjectsForRelationshipKey(detailKey);
			
			return isToMany && (!ownsDestination);
		}
		
		return false;
	}
	
	
	protected String createActionLabel()
	{
		return "Create";
	}
	
	
	protected String addActionLabel()
	{
		return "Add";
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
	
	protected WOActionResults sortAction()
	{
		TableColumnRowTextDescriptor cellDescriptor = (TableColumnRowTextDescriptor) this.sortColumn
				.rowDescriptor();
		EOSortOrdering sortOrdering = new EOSortOrdering(cellDescriptor.keyPath(),
				this.sortAscending.booleanValue() ? EOSortOrdering.CompareCaseInsensitiveAscending
						: EOSortOrdering.CompareCaseInsensitiveDescending);
		WODisplayGroup myDisplayGroup = displayGroup();
		
		myDisplayGroup.setSortOrderings(new NSArray(sortOrdering));
		myDisplayGroup.fetch();
		
		return page().context().page();
	}
	
	
	protected WOActionResults detailAction()
	{
		ControllerPage nextPage = session().pageWithEntityAndTask(entityName(),
				Application.DETAIL_TASK, page().context());
		AbstractDetailPageController controller = (AbstractDetailPageController) nextPage
				.controller();
		
		controller.setDisplayGroup(displayGroup());
		
		return nextPage;
	}
	
	
	protected WOActionResults editAction()
	{
		ControllerPage nextPage = session().pageWithEntityAndTask(entityName(),
				Application.EDIT_TASK, page().context());
		AbstractEditPageController controller = (AbstractEditPageController) nextPage.controller();
		WODisplayGroup myDisplayGroup = displayGroup();
		EOEnterpriseObject object = (EOEnterpriseObject) myDisplayGroup.selectedObject();
		EOEditingContext editingContext = object.editingContext();
		
		controller.setObject(editingContext.globalIDForObject(object), myDisplayGroup);
		
		return nextPage;
	}
	
	
	protected WOActionResults createAction()
	{
		ControllerPage nextPage = session().pageWithEntityAndTask(entityName(),
				Application.EDIT_TASK, page().context());
		AbstractEditPageController controller = (AbstractEditPageController) nextPage.controller();
		
		controller.setObject(null, displayGroup);
		
		return nextPage;
	}
	
	
	protected WOActionResults addAction()
	{
		ControllerPage nextPage = session().pageWithEntityAndTask(entityName(),
				Application.QUICK_SEARCH_TASK, page().context());
		AbstractQuickSearchController controller = (AbstractQuickSearchController) nextPage
				.controller();
		
		controller.setDelegate(this);
		
		return controller.page();
	}
	
	
	
	// Initialization
	
	/**
	 * Creates the descriptor for the data table. Called during component
	 * initialization
	 * 
	 * @return an immutable descriptor
	 */
	protected abstract TableDescriptor generateListTable();
	
	
	
	/**
	 * Picks the default sort column from the table descriptor. Called during
	 * component initialization
	 * 
	 * @return a column descriptor, or null
	 */
	protected TableColumnDescriptor defaultSortColumn(TableDescriptor tableDescriptor)
	{
		NSArray columns = tableDescriptor.columnDescriptors();
		
		for (int i = 0; i < columns.count(); i++) {
			TableColumnDescriptor tableColumnDescriptor = (TableColumnDescriptor) columns
					.objectAtIndex(i);
			
			TableColumnHeaderDescriptor headerDescriptor = tableColumnDescriptor.headerDescriptor();
			
			if (headerDescriptor.supportsSorting()) {
				return tableColumnDescriptor;
			}
		}
		
		return null;
	}
	
	
	
	/**
	 * Initializes a displayGroup. - Sets the dataSource. - Sets the
	 * displayGroup's sort orderings avoiding to override sort orderings
	 * specified by fecthSpecification.
	 * 
	 * @param aDisplayGroup
	 *            The displayGroup to initialize
	 * 
	 */
	protected void initDisplayGroup(WODisplayGroup aDisplayGroup)
	{
		if (aDisplayGroup.dataSource() == null) {
			EODatabaseDataSource dataSource = new EODatabaseDataSource(editingContext(),
					entityName());
			
			aDisplayGroup.setDataSource(dataSource);
		}
		
		if (aDisplayGroup.sortOrderings() == null
				&& aDisplayGroup.dataSource() instanceof EODatabaseDataSource) {
			EODatabaseDataSource dataSource = (EODatabaseDataSource) aDisplayGroup.dataSource();
			if (dataSource.fetchSpecification() != null) {
				NSArray sortOrderings = dataSource.fetchSpecification().sortOrderings();
				if (sortOrderings != null && sortOrderings != NSArray.EmptyArray) {
					aDisplayGroup.setSortOrderings(sortOrderings);
					// Avoid column header descriptor providing information on
					// sorting.
					this.sortColumn = null;
				}
			}
		}
		
		if ((aDisplayGroup.sortOrderings() == null) && (this.sortColumn != null)
				&& (this.sortColumn.rowDescriptor() instanceof TableColumnRowTextDescriptor)) {
			TableColumnRowTextDescriptor cellDescriptor = (TableColumnRowTextDescriptor) this.sortColumn
					.rowDescriptor();
			EOSortOrdering sortOrdering = new EOSortOrdering(
					cellDescriptor.keyPath(),
					this.sortAscending.booleanValue() ? EOSortOrdering.CompareCaseInsensitiveAscending
							: EOSortOrdering.CompareCaseInsensitiveDescending);
			
			aDisplayGroup.setSortOrderings(new NSArray(sortOrdering));
		}
	}
}