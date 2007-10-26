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

package com.houdah.web.view.table.cells;



import com.houdah.web.view.actions.Action;
import com.houdah.web.view.table.descriptors.TableColumnRowActionDescriptor;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

public class HVCTableColumnRowActionCell extends HVCTableColumnRowTextCell
{
	// Private class constants
	
	private static final long	serialVersionUID	= -3542407762811550035L;
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param context
	 *            the context in which this component is instantiated
	 */
	public HVCTableColumnRowActionCell(WOContext context)
	{
		super(context);
	}
	
	
	
	// Protected instance methods
	
	protected Action action()
	{
		TableColumnRowActionDescriptor descriptor = (TableColumnRowActionDescriptor) cellDescriptor();
		
		return descriptor.controllerAction();
	}
	
	
	protected boolean isEnabled()
	{
		Action action = action();
		Object object = displayedObject();
		
		return ((action != null) && (action.isEnabledFor(object)));
	}
	
	
	protected String actionClass()
	{
		Action action = action();
		String actionClass = (action != null) ? action.cssClass() : null;
		
		return (actionClass != null) ? actionClass : "";
	}
	
	
	
	/**
	 * Builds the String used to display the target object in this cell
	 * 
	 * @return the formatted display String
	 */
	protected String displayString()
	{
		Action action = action();
		
		if (action != null) {
			String displayString = action.label();
			
			if (displayString != null) {
				return displayString;
			}
		}
		
		String displayString = super.displayString();
		
		if ((action != null)
				&& ((displayString == null) || (displayString.length() == 0))) {
			displayString = "&#216;";
		}
		
		return displayString;
	}
	
	
	protected WOActionResults performAction()
	{
		return this.performControllerAction(action().actionName());
	}
	
	
	// Private instance methods
	
}