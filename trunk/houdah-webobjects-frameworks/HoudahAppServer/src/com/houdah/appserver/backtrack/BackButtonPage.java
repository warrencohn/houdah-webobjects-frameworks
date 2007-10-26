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

import java.lang.ref.WeakReference;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSLog;

/**
 * Subclass of Component that adds server side backtracking features. <br/>
 * 
 * The idea is that the constructor of a top-level component is called when the
 * page is instantiate from the previous request-response loop by a call to
 * pageWithName. Thus in the constructor it is possible to access the calling
 * context and determine it's top-level page. <br/>
 * 
 * A weak reference to that calling page is stored and can be retrieved by the
 * previousPage() action method.
 * 
 * For this to work the top-level component has to extend BackButtonPage as it
 * is the only one to be able get hold of the calling page. <br/>
 * 
 * Sub-components that want to provide a 'Back' button need only extend
 * BackButtonComponent so that they can call the previousPage() action method.
 * <br/>
 * 
 * @author bernard
 */
public class BackButtonPage extends BackButtonComponent
{
	// Private class constants
	
	private static final long	serialVersionUID	= 8292838217644188221L;
	
	
	
	// Private instance variables
	
	/**
	 * Weak reference to the ancessor page assigned by top-level components
	 * (pages). Does not prevent the ancessor from being garbage collected one
	 * it has expired from the cache.
	 */
	private WeakReference		ancessor;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor<br/>
	 * 
	 * Grabs a reference to the current page from the current context. If this
	 * component is itself a top-level component, that component is the
	 * calling/ancessor page.
	 * 
	 * @param context
	 *            calling context
	 */
	public BackButtonPage(WOContext context)
	{
		super(context);
		
		ancessor = new WeakReference(context.page());
	}
	
	
	
	// Public instance methods
	
	public void setPreviousPage(WOComponent page)
	{
		setAncessor(page);
	}
	
	
	
	// Protected instance methods
	
	/**
	 * Retrieves the ancessor for the current page. <br/>
	 * 
	 * The ancessor is stored by the top-level component which has to be an
	 * instance of BackButtonComponent. <br/>
	 * 
	 * This method may return null if the ancessor page has expired from the
	 * page cache or if the current page is not an instance BackButtonComponent.
	 * <br/>
	 * 
	 * CAVEAT: Even though this method is exposed, you will probably want to
	 * call previousPage() rather than getAncessor(). This method may indeed
	 * return null which leaves the responibility of handling that situation to
	 * the caller. Thus this method serves mainly for debugging purposes of for
	 * implementing specialized subclasses.
	 * 
	 * @return the ancessor page, null if it is not/no longer known
	 */
	protected WOComponent getAncessor()
	{
		if (parent() == null) {
			return (WOComponent) ancessor.get();
		} else {
			return super.getAncessor();
		}
	}
	
	
	private void setAncessor(WOComponent previousPage)
	{
		if (parent() == null) {
			this.ancessor = new WeakReference(previousPage);
			;
		} else {
			WOComponent page = context().page();
			
			if (page instanceof BackButtonPage) {
				((BackButtonPage) page).setAncessor(previousPage);
			} else {
				if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelCritical)) {
					NSLog.out
							.appendln("WARNING: cannot set previous page as the current page is not a BackButtonPage");
				}
			}
		}
	}
}
