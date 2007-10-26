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

package com.houdah.appserver.components;

import com.houdah.appserver.backtrack.BackButtonPage;
import com.houdah.appserver.support.AccessKey;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSMutableSet;

public abstract class Page extends BackButtonPage
{
	// Private class constants
	
	private static final long		serialVersionUID	= 3631218571424431597L;
	
	
	
	// Private instance variables
	
	private AccessKey.Repository	accessKeys;
	
	
	private NSMutableSet			claimedIDs;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param context
	 *            the context in which this component is instantiated
	 */
	public Page(WOContext context)
	{
		super(context);
		
		WOComponent ancessor = context.page();
		
		if (ancessor instanceof Page) {
			Page page = (Page) ancessor;
			
			userInfo().addEntriesFromDictionary(page.userInfo());
		}
	}
	
	
	
	// Public instance methods
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#reset()
	 */
	public void reset()
	{
		super.reset();
		
		if (isStateless()) {
			this.accessKeys = null;
			this.claimedIDs = null;
		}
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#appendToResponse(com.webobjects.appserver.WOResponse,
	 *      com.webobjects.appserver.WOContext)
	 */
	public void appendToResponse(WOResponse response, WOContext context)
	{
		this.claimedIDs = null;
		
		super.appendToResponse(response, context);
	}
	
	
	
	/**
	 * Lazily creates the access key for a given label
	 * 
	 * @param label
	 *            the label in need of an access key
	 * @return a unique access key repository
	 */
	public AccessKey accessKey(String label, Object owner)
	{
		return AccessKey.accessKey(label, this.accessKeys(), owner);
	}
	
	
	
	/**
	 * Ensure that IDs remain unique.
	 * 
	 * @param id
	 *            the id to claim
	 * @return true for the first to claim an ID in a given appendToResponse()
	 *         phase
	 */
	public boolean claimID(String id)
	{
		NSMutableSet claimedIDs = claimedIDs();
		
		if (claimedIDs.containsObject(id)) {
			return false;
		} else {
			claimedIDs.addObject(id);
			
			return true;
		}
	}
	
	
	
	// Private instance methods
	
	/**
	 * Lazily creates the access key repository
	 * 
	 * @return the access key repository
	 */
	private AccessKey.Repository accessKeys()
	{
		if (this.accessKeys == null) {
			this.accessKeys = new AccessKey.Repository(AccessKey.defaultExcludes());
		}
		
		return this.accessKeys;
	}
	
	
	
	/**
	 * Lazily creates the ID repository
	 * 
	 * @return the ID repository
	 */
	private NSMutableSet claimedIDs()
	{
		if (this.claimedIDs == null) {
			this.claimedIDs = new NSMutableSet();
		}
		
		return this.claimedIDs;
	}
}
