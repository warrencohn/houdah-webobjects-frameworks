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

import java.util.Collection;

import com.houdah.foundation.KVCUtility;

/**
 * Basic conditional action.<br/>
 * 
 * Enabled if pointed to a method on the business object returning a value which
 * may be interpreted as true (boolean true, positive integer, collection with
 * at least one element)
 * 

 * 
 */
public class BasicConditionalAction extends Action
{
	// Private instance variables
	
	private String	enabledKeyPath;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param actionName
	 *            name of the controller action to perform
	 * @param label
	 *            link text to display
	 * @param title
	 *            title to attach to the hyperlink
	 * @param enabledKeyPath
	 *            key path to the EO property determining if this action is
	 *            enabled or not
	 * @param cssClass
	 *            CSS class to assign to the hyperlink
	 */
	public BasicConditionalAction(String actionName, String label,
			String title, String enabledKeyPath, String cssClass)
	{
		super(actionName, label, title, cssClass);
		
		this.enabledKeyPath = enabledKeyPath;
	}
	
	
	
	// Public instance methods
	
	/**
	 * Return true if the enabledKeyPath leads us to a true boolean, a non empty
	 * collection or a positive number.
	 * 
	 */
	public boolean isEnabledFor(Object object)
	{
		String hotKeyPath = this.enabledKeyPath;
		
		if (hotKeyPath != null) {
			if (object == null) {
				return false;
			} else {
				Object value = KVCUtility.sharedInstance().valueForKeyPath(
						object, hotKeyPath);
				
				if (value == null) {
					return false;
				} else if (value instanceof Number) {
					return (((Number) value).intValue() > 0);
				} else if (value instanceof Collection) {
					return !((Collection) value).isEmpty();
				} else {
					return (!Boolean.FALSE.equals(value));
				}
			}
		} else {
			return true;
		}
	}
}
