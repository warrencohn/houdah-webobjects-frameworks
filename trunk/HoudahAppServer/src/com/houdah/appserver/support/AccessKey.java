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

package com.houdah.appserver.support;

import java.util.Enumeration;

import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSMutableSet;
import com.webobjects.foundation.NSSet;

/**
 * An access key is a hot key assigned to a HTML element.<br/>
 * 
 * This class provides a representation of such an access key. It also provides
 * for a representation of a repository of keys.
 * 
 * 
 */
public class AccessKey
{
	// Private class variables
	
	private static NSSet	defaultExcludes;
	
	
	
	// Protected instance variables
	
	/**
	 * The access key (hot key)
	 */
	protected Character		key;
	
	
	
	/**
	 * The HTML string for displaying said key attached to a label
	 */
	protected String		displayLabel;
	
	
	
	/**
	 * The owner. Optional
	 */
	protected Object		owner;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param key
	 *            access key (hot key)
	 * @param displayLabel
	 *            the HTML string for displaying said key attached to a label
	 * @param owner
	 *            the owning component
	 */
	private AccessKey(Character key, String displayLabel, Object owner)
	{
		this.key = key;
		this.displayLabel = displayLabel;
		this.owner = owner;
	}
	
	
	
	// Public instance methods
	
	/**
	 * Access key (hot key)
	 * 
	 * @return access key
	 */
	public Character key()

	{
		return this.key;
	}
	
	
	
	/**
	 * The HTML string for displaying said key attached to a label
	 * 
	 * @return HTML string for displaying said key attached to a label
	 */
	public String displayLabel()
	{
		return this.displayLabel;
	}
	
	
	
	/**
	 * The owning component
	 * 
	 * @return an object, may be null
	 */
	public Object owner()
	{
		return this.owner;
	}
	
	
	
	// Public class methods
	
	/**
	 * Factory method.
	 * 
	 * If possible gets or creates an AccessKey with the desired hot key and a
	 * label providing for CSS highlighting of the key within the label.
	 * 
	 * @param label
	 *            Label used to generate or retrieve the AccessKey for. Not null
	 * @param localAccessKeys
	 *            Local repository of access keys. Typically owned by a Page
	 *            component
	 * @return an AccessKey object. May or may not hold an actual key
	 */
	public static AccessKey accessKey(String label, Repository localAccessKeys,
			Object owner)
	{
		AccessKey accessKey = localAccessKeys.get(label, owner);
		
		if (accessKey == null) {
			int count = label.length();
			
			
			// Try first letter
			
			if (count > 0) {
				accessKey = registerKey(label, 0, localAccessKeys, owner);
			}
			
			
			// Try uppercase letters
			
			for (int i = 0; (accessKey == null) && (i < count); i++) {
				char key = label.charAt(i);
				
				if (Character.isUpperCase(key)) {
					accessKey = registerKey(label, i, localAccessKeys, owner);
				}
			}
			
			
			// Try initials
			boolean wasSpace = false;
			
			for (int i = 0; (accessKey == null) && (i < count); i++) {
				char key = label.charAt(i);
				
				if (wasSpace) {
					accessKey = registerKey(label, i, localAccessKeys, owner);
				}
				
				wasSpace = (' ' == key);
			}
			
			
			// Try remaining letters
			
			for (int i = 1; (accessKey == null) && (i < count); i++) {
				char key = label.charAt(i);
				
				if (Character.isLetter(key)) {
					accessKey = registerKey(label, i, localAccessKeys, owner);
				}
			}
			
			
			// We were unable to create a functional access key
			
			if (accessKey == null) {
				accessKey = dummyAccessKey(label);
				
				localAccessKeys.register(label, accessKey, owner);
			}
		}
		
		return accessKey;
	}
	
	
	
	/**
	 * Create and return a 'dummy' AccessKey with no actual key
	 * 
	 * @param label
	 *            Label used to generate or retrieve the AccessKey for. Not null
	 * @param localAccessKeys
	 *            Local repository of access keys. Typically owned by a Page
	 *            component
	 * @return an AccessKey with no actual key and a display string of equal to
	 *         label
	 */
	public static AccessKey dummyAccessKey(String label)
	{
		return new AccessKey(null, label, null);
	}
	
	
	
	// Protected class methods
	
	protected static AccessKey registerKey(String label, int index,
			Repository localAccessKeys, Object owner)
	{
		Character character = new Character(Character.toUpperCase(label
				.charAt(index)));
		
		if (localAccessKeys.validateKey(character)) {
			String prefix = label.substring(0, index);
			String suffix = label.substring(index + 1, label.length());
			StringBuffer displayString = new StringBuffer(label.length() + 30);
			
			displayString.append(prefix);
			displayString.append(("<span class=\"accessKey\">"));
			displayString.append(label.charAt(index));
			displayString.append("</span>");
			displayString.append(suffix);
			
			AccessKey accessKey = new AccessKey(character, displayString
					.toString(), owner);
			
			localAccessKeys.register(label, accessKey, owner);
			
			return accessKey;
		} else {
			return null;
		}
	}
	
	
	
	
	// Inner classes
	
	/**
	 * Holder of known AccessKey objects. Typically owned by a Page component.
	 */
	public static class Repository
	{
		// Private instance variabls
		
		private NSMutableDictionary	accessKeysByLabel;
		
		
		private NSMutableSet		unavailableKeys;
		
		
		
		
		// Constructor
		
		/**
		 * Designated constructor.
		 * 
		 * @param globalExcludes
		 *            Set of Characters not to use as access keys. E.g over
		 *            conflict with common browser shortcuts
		 */
		public Repository(NSSet globalExcludes)
		{
			this.accessKeysByLabel = new NSMutableDictionary();
			
			this.unavailableKeys = new NSMutableSet(globalExcludes);
		}
		
		
		
		// Public instance methods
		
		public synchronized AccessKey get(String label, Object owner)
		{
			NSSet accessKeysForLabel = (NSSet) this.accessKeysByLabel
					.objectForKey(label);
			
			if (accessKeysForLabel != null) {
				Enumeration iterator = accessKeysForLabel.objectEnumerator();
				
				while (iterator.hasMoreElements()) {
					AccessKey accessKey = (AccessKey) iterator.nextElement();
					
					if (owner != null) {
						Object registeredOwner = accessKey.owner();
						
						if ((registeredOwner != null)
								&& (!registeredOwner.equals(owner))) {
							continue;
						}
					}
					
					return accessKey;
				}
			}
			
			return null;
		}
		
		
		public synchronized boolean validateKey(Character key)
		{
			return (!this.unavailableKeys.containsObject(key));
		}
		
		
		public synchronized void register(String label, AccessKey accessKey,
				Object owner)
		{
			NSMutableSet accessKeysForLabel = (NSMutableSet) this.accessKeysByLabel
					.objectForKey(label);
			
			if (accessKeysForLabel == null) {
				accessKeysForLabel = new NSMutableSet();
				
				this.accessKeysByLabel.setObjectForKey(accessKeysForLabel,
						label);
			}
			
			accessKeysForLabel.addObject(accessKey);
			
			Character key = accessKey.key();
			
			if (key != null) {
				this.unavailableKeys.addObject(key);
			}
		}
	}
	
	
	
	
	// Public class methods
	
	/**
	 * A hard-coded exclude list to prevent conficts with common shortcuts
	 * 
	 * @return the shared exclude list
	 */
	public static NSSet defaultExcludes()
	{
		if (AccessKey.defaultExcludes == null) {
			NSMutableSet set = new NSMutableSet();
			
			set.addObject(new Character('A')); // Windows. IE Favorites
			set.addObject(new Character('B')); // Windows. FireFox Bookmarks
			set.addObject(new Character('E')); // Windows. IE Edit, FireFox
			// Edit
			set.addObject(new Character('F')); // Windows. IE File, FireFox
			// File
			set.addObject(new Character('G')); // Windows. FireFox Go
			set.addObject(new Character('H')); // Windows. IE Help, FireFox
			// Help
			set.addObject(new Character('T')); // Windows. IE Tools, FireFox
			// Tools
			set.addObject(new Character('V')); // Windows. IE View, FireFox
			// View
			
			AccessKey.defaultExcludes = set.immutableClone();
			
		}
		
		return AccessKey.defaultExcludes;
	}
	
}