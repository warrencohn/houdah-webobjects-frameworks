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

package com.houdah.web.view.actions;

public class Action
{
	// Private instance variables
	
	private String	actionName;
	
	
	private String	label;
	
	
	private String	title;
	
	
	private String	cssClass;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param actionName
	 *            name of the controller action to perform
	 * @param label
	 *            link text to display
	 * @param title
	 *            title to attach to the action cell
	 * @param cssClass
	 *            CSS class to assign to the action cell
	 */
	public Action(String actionName, String label, String title, String cssClass)
	{
		this.actionName = actionName;
		this.label = label;
		this.title = title;
		this.cssClass = cssClass;
	}
	
	/**
	 * Simplified constructor.
	 * 
	 * @param actionName
	 *            name of the controller action to perform
	 */
	public Action(String actionName)
	{
		this(actionName, null, null, null);
	}
	
	
	
	// Public instance methods
	
	/**
	 * Conditionally enable this actions. May be overridden by subclasses.
	 * 
	 * @return true
	 */
	public boolean isEnabledFor(Object object)
	{
		return true;
	}
	
	
	
	/**
	 * @return Returns the label.
	 */
	public final String label()
	{
		return this.label;
	}
	
	
	
	/**
	 * @return Returns the actionName.
	 */
	public final String actionName()
	{
		return this.actionName;
	}
	
	
	
	/**
	 * @return Returns the title.
	 */
	public final String title()
	{
		return this.title;
	}
	
	
	
	/**
	 * @return Returns the cssClass.
	 */
	public final String cssClass()
	{
		return this.cssClass;
	}
}