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

import com.houdah.appserver.components.Component;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

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
public class BackButtonComponent extends Component
{
	// Private class constants
	
	private static final long	serialVersionUID	= -7256170125232875223L;
	
	
	
	// Public class constants
	
	/**
	 * Action name
	 */
	public static final String	PREVIOUS_PAGE		= "previousPage";
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param context
	 *            calling context
	 */
	public BackButtonComponent(WOContext context)
	{
		super(context);
	}
	
	
	
	// Public instance methods
	
	/**
	 * Determines if the ancessor page is still available
	 * 
	 * @return true, if the page is still available
	 */
	public boolean hasPreviousPage()
	{
		return getAncessor() != null;
	}
	
	
	
	/**
	 * Action method that returns the previous page. <br/>
	 * 
	 * In the event where the previous page is no longer available a placeholder
	 * component is returned that will call
	 * WOApplication.handlePageRestorationErrorInContext() in its
	 * appendToResponse() method to generate the appropriate error page.
	 * 
	 * @return the ancessor page if it is still available
	 */
	public WOComponent previousPage()
	{
		WOComponent previousPage = getAncessor();
		
		if (previousPage != null) {
			return previousPage;
		} else {
			return new PageMissingComponent(context());
		}
	}
	
	
	public void setPreviousPage(WOComponent previousPage)
	{
		WOComponent page = context().page();
		
		if (page instanceof BackButtonPage) {
			((BackButtonPage) page).setPreviousPage(previousPage);
		} else {
			throw new IllegalStateException(
					"Cannot set previous page as the current page is not a BackButtonComponent");
		}
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
		WOComponent page = context().page();
		
		if (page instanceof BackButtonPage) {
			return ((BackButtonPage) page).getAncessor();
		} else {
			throw new IllegalStateException(
					"Cannot retrieve previous page as the current page is not a BackButtonComponent");
		}
	}
	
	
	
	
	// Private inner class
	
	/**
	 * Placeholder page returned when the requested ancessor page is no longer
	 * available. <br/>
	 * 
	 * Relies on WOApplication.handlePageRestorationErrorInContext() to handle
	 * the generation of its response.
	 */
	private static class PageMissingComponent extends Component
	{
		// Private class constants
		
		private static final long	serialVersionUID	= 3568625000229603759L;
		
		
		
		public PageMissingComponent(WOContext context)
		{
			super(context);
		}
		
		
		public void appendToResponse(WOResponse response, WOContext context)
		{
			response.setContent(WOApplication.application().handlePageRestorationErrorInContext(
					context).content());
		}
	}
}