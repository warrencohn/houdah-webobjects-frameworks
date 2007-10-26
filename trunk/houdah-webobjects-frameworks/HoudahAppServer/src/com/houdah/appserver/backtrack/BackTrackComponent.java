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

package com.houdah.appserver.backtrack;

import com.houdah.appserver.support.ApplicationException;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSMutableSet;

/**
 * Subclass of BackButtonComponent to work around problems caused by client-side
 * backtracking. <br/>
 * 
 * The problems I currently run into are:
 * 
 * <ul>
 * <li>Wrong items being selected from WORepetitions after the user backtracked
 * <li>A component being asked to perform an action that is inappropriate in
 * the current state, e.g. cancel an edit after committing it
 * </ul>
 * 
 * The first problem is in fact caused by a more general problem: a component
 * always exits with only one state, i.e. the latest one. The user may however
 * backtrack to a cached page that matches a previous state: showing components
 * that should be hidden, displaying an earlier batch of a display group,... .<br/>
 * 
 * The first problem is closely related, but differs by the fact that the former
 * state of the component is permanently lost in that the moving to the new
 * state has non reversible effects like deleting from a database. <br/>
 * 
 * Another problem is that some actions modifying a display group may get
 * repeated, causing unwanted behaviour like deleting the wrong item. This
 * situation fits the 2nd problem: the state change is so dramatic that
 * backtracking is not tolerable. <br/>
 * 
 * The workaround I suggest tries to address these two problems. I tried to come
 * up with a mecanism that is very general and should apply to many situations.
 * Most notably, the business decision of what to do when the user has
 * backtracked is left to the afffected component. There will however be
 * situations that can not be addressed by my approach. This fix does not
 * address problems with users reloading a page. <br/>
 * 
 * The role of the BackTrackComponent is to detect client-side backtracking. It
 * offers hooks for its subclasses to handle the back tracking. A
 * DefaultImplementation offers state persistence allowing subclasses to recover
 * from simple backtracking situations.
 * 
 * @author bernard
 */
public abstract class BackTrackComponent extends BackButtonComponent
{
	// Private class constants
	
	private static final char	DOT				= '.';
	
	
	
	// Private instance variables
	
	private String				_lastKnownCID	= null;
	
	
	private NSMutableSet		_knownCIDs		= null;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param context
	 *            calling context
	 */
	public BackTrackComponent(WOContext context)
	{
		super(context);
		
		if (needsBackTrackDetection()) {
			_knownCIDs = new NSMutableSet();
		}
	}
	
	
	
	// Public instance methods
	
	/**
	 * First we need to detect if the user has backtracked. <br/>
	 * In order to do so we store the context ID of the request-response loop
	 * that created the current state of the component in an instance variable
	 * during sleep.
	 */
	public void sleep()
	{
		if (needsBackTrackDetection()) {
			String contextID;
			
			contextID = context().contextID();
			_lastKnownCID = contextID;
			_knownCIDs.addObject(_lastKnownCID);
			
			sleepInContext(contextID);
		}
		
		super.sleep();
	}
	
	
	
	/**
	 * At the biginning of the next request-response loop, we determine from
	 * which version of the component the request stems.
	 */
	public void awake()
	{
		super.awake();
		
		if (needsBackTrackDetection()) {
			NSArray requestHandlerPathArray;
			String lastElement;
			String contextID;
			
			requestHandlerPathArray = context().request()
					.requestHandlerPathArray();
			lastElement = ((String) requestHandlerPathArray
					.objectAtIndex(requestHandlerPathArray.count() - 1));
			contextID = lastElement.substring(0, lastElement.indexOf(DOT));
			
			if ((_lastKnownCID != null) && (!_lastKnownCID.equals(contextID))
					&& (_knownCIDs.containsObject(contextID))) {
				awakeFromContext(contextID);
			}
		}
	}
	
	
	
	// Protected instance methods
	
	/**
	 * Determines if the component state needs to detect backtracking. <br/>
	 * 
	 * The sleepInContext and awakeInContext methods get called only if this
	 * method returns tru.
	 * 
	 * @return true, if backtracking detection is needed
	 */
	protected abstract boolean needsBackTrackDetection();
	
	
	
	/**
	 * Called at sleep time when backtracking detection is required. <br/>
	 * 
	 * The default implementation does nothing.
	 */
	protected void sleepInContext(String contextID)
	{
	}
	
	
	
	/**
	 * Called at awake time if client-side backtracking was detected. <br/>
	 * 
	 * The default implementation throws an exception.
	 * 
	 * @see BackTrackException
	 * @throws BackTrackException
	 */
	protected void awakeFromContext(String contextID)
	{
		throw new BackTrackException(ApplicationException.SESSION_ALIVE);
	}
	
	
	
	
	// Inner class
	
	/**
	 * This implementation hooks into the BackTrackComponent backtracking
	 * detection to provide state persistence. <br/>
	 * 
	 * Simple backtracking problems can be corrected by restoring the
	 * appropriate component state upon client-side backtracking.
	 */
	public abstract static class DefaultImplementation extends
			BackTrackComponent
	{
		// Private instance variables
		
		private NSMutableDictionary	_persistentStorage	= null;
		
		
		
		
		// Constructor
		
		/**
		 * Public constructor
		 */
		public DefaultImplementation(WOContext context)
		{
			super(context);
			
			_persistentStorage = new NSMutableDictionary();
		}
		
		
		
		// Protected instance methods
		
		/**
		 * Determines if the component state needs to detect backtracking. <br/>
		 * 
		 * The sleepInContext and awakeInContext methods get called only if this
		 * method returns tru.
		 * 
		 * @return true
		 */
		protected boolean needsBackTrackDetection()
		{
			return true;
		}
		
		
		
		/**
		 * Returns the keys to the values that need to be persisted. <br/>
		 * 
		 * A null return value means that backtracking is prohibited. <br/>
		 * A NSArray.EmptyArray return value means that backtracking need not be
		 * handled. <br/>
		 * 
		 * The above 2 special cases are however best served by directly
		 * subclassing BackTrackComponent.
		 * 
		 * @see #needsStatePersistence
		 * @see BackTrackException
		 */
		protected abstract NSArray getPersistentKeys();
		
		
		
		/**
		 * Called at sleep time when state persistence is required. <br/>
		 * 
		 * The default implementation calls saveStateForContext. This method
		 * should in most cases not be overridden, but customized by overriding
		 * getPersistentKeys.
		 * 
		 * @see #saveStateForContext
		 * @see #getPersistentKeys
		 */
		protected void sleepInContext(String contextID)
		{
			saveStateForContext(contextID);
		}
		
		
		
		/**
		 * Called at awake time when state persistence is required. <br/>
		 * 
		 * The default implementation calls saveStateForContext. This method
		 * should in most cases not be overridden, but customized by overriding
		 * getPersistentKeys.
		 * 
		 * @see #restoreStateForContext
		 * @see #getPersistentKeys
		 */
		protected void awakeFromContext(String contextID)
		{
			restoreStateForContext(contextID);
		}
		
		
		
		// Private instance methods
		
		/**
		 * Utility method. Saves the state of the component.
		 * 
		 * @see sleepsInContext
		 */
		private void saveStateForContext(String contextID)
		{
			NSArray keys = getPersistentKeys();
			
			if (keys != null) {
				NSMutableDictionary storage = new NSMutableDictionary();
				int count = keys.count();
				
				for (int i = 0; i < count; i++) {
					String key = (String) keys.objectAtIndex(i);
					Object value = valueForKey(key);
					
					if (value != null) {
						storage.setObjectForKey(value, key); // clones the
						// value
					}
				}
				
				
				// _persistentStorage is an instance variable of type
				// NSMutableDictionary
				_persistentStorage.setObjectForKey(storage, contextID);
			}
		}
		
		
		
		/**
		 * Utility method. Restores the state of the component.
		 * 
		 * @see awakesFromContext
		 */
		private void restoreStateForContext(String contextID)
		{
			NSMutableDictionary storage = (NSMutableDictionary) _persistentStorage
					.objectForKey(contextID);
			
			if (storage == null) {
				throw new BackTrackException(ApplicationException.SESSION_ALIVE);
			} else {
				NSArray keys = getPersistentKeys();
				int count = keys.count();
				
				for (int i = 0; i < count; i++) {
					String key = (String) keys.objectAtIndex(i);
					Object value = storage.objectForKey(key);
					
					takeValueForKey(value, key);
				}
			}
		}
	}
}