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

import com.houdah.eocontrol.utilities.ControlUtilities;
import com.houdah.web.view.form.values.Value;
import com.houdah.web.view.form.values.ValueContainer;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSSet;

public class Toolbox
{
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 */
	private Toolbox()
	{
		throw new IllegalStateException("Do not instantiate this utility class");
	}
	
	
	
	// Public class methods
	
	/**
	 * Binds a fetch specification using a value container.
	 * 
	 * EOEnterpriseObjects are translated into the target editing context if
	 * provided.
	 * 
	 * @param template
	 *            an unbound fetch specification
	 * @param values
	 *            a value container
	 * @param editingContext
	 *            target editing context, optional
	 * @return the bound fetch specification
	 */
	public static EOFetchSpecification bindFetchSpecification(EOFetchSpecification template,
			ValueContainer values, EOEditingContext editingContext)
	{
		NSDictionary dictionary = values.dictionary();
		NSMutableDictionary bindings = new NSMutableDictionary(dictionary.count());
		Enumeration keys = dictionary.allKeys().objectEnumerator();
		
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Value value = (Value) dictionary.objectForKey(key);
			Object object = value.value();
			
			if (object != null) {
				if (object instanceof EOEnterpriseObject) {
					if (editingContext != null) {
						EOEnterpriseObject eo = (EOEnterpriseObject) object;
						
						object = ControlUtilities.localInstanceOfObject(editingContext, eo);
					}
				} else if (object instanceof NSSet) {
					NSSet set = (NSSet) object;
					
					if (set.count() == 0) {
						continue;
					} else if (set.count() == 1) {
						object = set.anyObject();
					}
				}
				
				bindings.setObjectForKey(object, key);
			}
		}
		
		return ControlUtilities.bindFetchSpecification(template, bindings);
	}
}
