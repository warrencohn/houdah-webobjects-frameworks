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

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOObjectStore;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;

/**
 * Utility class. Provides for a way to watch for changes in EOs. The main use
 * of this would be to be able to safely keep cached/computed/derived values
 * that get cleared once out of sync.
 * 
 */
public class ChangeNotificationCenter
{
	// Public class constants
	
	/**
	 * Name of the posted notification
	 */
	public static final String				OBJECT_CHANGED					= "CNC_ObjectChanged";
	
	
	
	/**
	 * Possible type of change a notification is posted for
	 */
	public static final String				INVALIDATION					= "Invalidation";
	
	
	
	/**
	 * Possible type of change a notification is posted for
	 */
	public static final String				DELETION						= "Deletion";
	
	
	
	/**
	 * Possible type of change a notification is posted for
	 */
	public static final String				UPDATE							= "Update";
	
	
	
	// Private class constants
	
	/**
	 * Key in the posted notification's userInfo dictionary.
	 */
	private static final String				OBJECT							= "CNC_Object";
	
	
	
	/**
	 * Key in the posted notification's userInfo dictionary.
	 */
	private static final String				TYPE							= "CNC_Type";
	
	
	
	/**
	 * Selector used for calling the watcher object upon a change
	 */
	private static final NSSelector			watchedObjectChangedSelector	= new NSSelector(
																					"watchedObjectChanged",
																					new Class[] { NSNotification.class });
	
	
	
	// Private class variables
	
	/**
	 * Reference holding the shared singleton instance.
	 */
	private static ChangeNotificationCenter	defaultCenter					= null;
	
	
	
	
	// Constructor
	
	/**
	 * Singleton class
	 */
	private ChangeNotificationCenter()
	{
		super();
		
		Class[] notificationArray = new Class[] { NSNotification.class };
		NSSelector objectsChangedIInEditingContextSelector = new NSSelector(
				"objectsChangedInEditingContext", notificationArray);
		
		NSNotificationCenter.defaultCenter().addObserver(this,
				objectsChangedIInEditingContextSelector,
				EOEditingContext.ObjectsChangedInEditingContextNotification, null);
	}
	
	
	
	// Public insatnce methods
	
	/**
	 * Method called when a change notification is received.
	 * 
	 * The notification is split and redispatched for all updated objects.
	 */
	public void objectsChangedInEditingContext(NSNotification notification)
	{
		NSArray updated = (NSArray) notification.userInfo().objectForKey(EOObjectStore.UpdatedKey);
		
		if (updated != null) {
			int count = updated.count();
			
			for (int i = 0; i < count; i++) {
				Object object = updated.objectAtIndex(i);
				NSDictionary userInfo = new NSDictionary(new Object[] { object, UPDATE },
						new Object[] { OBJECT, TYPE });
				NSNotificationCenter.defaultCenter().postNotification(OBJECT_CHANGED, object,
						userInfo);
			}
		}
		
		NSArray invalidated = (NSArray) notification.userInfo().objectForKey(
				EOObjectStore.InvalidatedKey);
		
		if (invalidated != null) {
			int count = invalidated.count();
			
			for (int i = 0; i < count; i++) {
				Object object = invalidated.objectAtIndex(i);
				NSDictionary userInfo = new NSDictionary(new Object[] { object, INVALIDATION },
						new Object[] { OBJECT, TYPE });
				NSNotificationCenter.defaultCenter().postNotification(OBJECT_CHANGED, object,
						userInfo);
			}
		}
		
		NSArray deleted = (NSArray) notification.userInfo().objectForKey(EOObjectStore.DeletedKey);
		
		if (deleted != null) {
			int count = deleted.count();
			
			for (int i = 0; i < count; i++) {
				Object object = deleted.objectAtIndex(i);
				NSDictionary userInfo = new NSDictionary(new Object[] { object, DELETION },
						new Object[] { OBJECT, TYPE });
				NSNotificationCenter.defaultCenter().postNotification(OBJECT_CHANGED, object,
						userInfo);
			}
		}
	}
	
	
	
	/**
	 * Method to be called when one creates a cached value that depends on the
	 * attributes of a given object.<br/>
	 * 
	 * Upon registration the object is watched and the watchedObjectChanged() is
	 * called once it changes, thus providing an oppurtunity to invalidate the
	 * cached value.<br/>
	 * 
	 * @param watcher
	 *            the watching object which should unregister before disposing
	 * @param object
	 *            the object to watch
	 * @see #unregisterCacheDependancy
	 * @see #unregisterCacheDependancies
	 */
	public void registerCacheDependancy(ObserverInterface watcher, EOEnterpriseObject object)
	{
		NSNotificationCenter.defaultCenter().addObserver(watcher, watchedObjectChangedSelector,
				OBJECT_CHANGED, object);
	}
	
	
	
	/**
	 * Method to be called when one abandons or clears a cached value that
	 * depended on attributes of a given object.<br/>
	 * 
	 * @param watcher
	 *            the watching object
	 * @param object
	 *            the object to watch
	 * @see #registerCacheDependancy
	 */
	public void unregisterCacheDependancy(ObserverInterface watcher, Object object)
	{
		NSNotificationCenter.defaultCenter().removeObserver(watcher, OBJECT_CHANGED, object);
	}
	
	
	
	/**
	 * Unregisters all of the callers dependancies. To be used sparingly as it
	 * may break unknown but required dependancies. Usually called when
	 * disposing the calling object.<br/>
	 * 
	 * @param watcher
	 *            the watching object
	 * @see #registerCacheDependancy
	 * @see #unregisterCacheDependancy
	 */
	public void unregisterCacheDependancies(ObserverInterface watcher)
	{
		NSNotificationCenter.defaultCenter().removeObserver(watcher, OBJECT_CHANGED, null);
	}
	
	
	
	// Public class methods
	
	/**
	 * Gets or lazily instantiates the shared instance of the
	 * ChangeNotificationCenter
	 * 
	 * @return the unique instance
	 */
	public static ChangeNotificationCenter defaultCenter()
	{
		if (defaultCenter == null) {
			createDefaultCenter();
		}
		
		return defaultCenter;
	}
	
	
	
	/**
	 * Utility method. Allows for retrieving the changed object from a
	 * notification that results of registering interest in a change.
	 * 
	 * @param notification
	 *            the received notification
	 * @return the object that triggered the notification
	 */
	public static EOEnterpriseObject objectFromNotification(NSNotification notification)
	{
		return (EOEnterpriseObject) notification.userInfo().objectForKey(OBJECT);
	}
	
	
	
	/**
	 * Utility method. Allows for retrieving the type of change that ocurred
	 * from a notification that results of registering interest in a change.
	 * 
	 * @param notification
	 *            the received notification
	 * @return the type as one of the class constants defined in
	 *         ChangeNotificationCenter
	 */
	public static String typeFromNotification(NSNotification notification)
	{
		return (String) notification.userInfo().objectForKey(TYPE);
	}
	
	
	
	// Private class methods
	
	/**
	 * Creates the singleton instance ensuring there is no existing instance.
	 */
	private static synchronized void createDefaultCenter()
	{
		if (defaultCenter == null) {
			defaultCenter = new ChangeNotificationCenter();
		}
	}
}