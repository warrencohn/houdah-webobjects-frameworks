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

package com.houdah.eocontrol.changenotification;


import com.webobjects.eocontrol.EOEnterpriseObject;

/**
 * Convenience class for implementing ObserverInterface.<br/>
 * 
 * The recommended way of registering for change notificications is to
 * create an inner class extending this one.
 */
public abstract class Observer implements ObserverInterface
{
	/**
	 * Method to be called by subclasses when the create a cached value that
	 * depends on the attributes of a given object.<br/>
	 * 
	 * Upon registration the object is watched and the clearCachedValues()
	 * is called once it changes, thus providing an oppurtunity to
	 * invalidate the cached value.<br/>
	 * 
	 * You need to register for each object your locally cached values
	 * depend on. E.g. if you build a cached value from attributes of EOs in
	 * a too-many relationship, your cache depends on each of the objects in
	 * the relationship as well as on the source of the realtionship.<br/>
	 * 
	 * N.B. Extend the dispose() method to unregister any dependancy wou
	 * might be registered for.
	 * 
	 * @param object
	 *            the object to watch
	 * @see #unregisterCacheDependancy
	 * @see #clearCachedValues
	 */
	protected void registerCacheDependancy(EOEnterpriseObject object)
	{
		ChangeNotificationCenter.defaultCenter().registerCacheDependancy(this, object);
	}
	
	
	
	/**
	 * Method to be called by subclasses when they abandon or clear a cached
	 * value that depended on attributes of a given object.<br/>
	 * 
	 * @param object
	 *            the object to watch
	 * @see #registerCacheDependancy
	 * @see #clearCachedValues
	 */
	protected void unregisterCacheDependancy(EOEnterpriseObject object)
	{
		ChangeNotificationCenter.defaultCenter().unregisterCacheDependancy(this, object);
	}
	
	
	
	/**
	 * Unregisters all of the callers dependancies. To be used sparingly as
	 * it may break unknown but required dependancies. Usually called when
	 * disposing the calling object.<br/>
	 * 
	 * @see #registerCacheDependancy
	 * @see #unregisterCacheDependancy
	 */
	public void unregisterCacheDependancies()
	{
		ChangeNotificationCenter.defaultCenter().unregisterCacheDependancies(this);
	}
	
	
	
	/**
	 * Overridden to unregister all cache dependancies
	 */
	public void finalize() throws Throwable
	{
		ChangeNotificationCenter.defaultCenter().unregisterCacheDependancies(this);
		
		super.finalize();
	}
}