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

package com.houdah.appserver.application;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.houdah.foundation.ForwardException;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableSet;

/**
 * @author bernard
 */
public class Application extends WOApplication
{
	static {
		// Clean out duplicate bundles from the development classpath
		String classPath = System.getProperty("java.class.path");
		
		if (classPath != null) {
			NSArray classPathArray = NSArray.componentsSeparatedByString(classPath,
					File.pathSeparator);
			int cCount = classPathArray.count();
			NSMutableSet bundles = new NSMutableSet(cCount);
			NSMutableArray array = new NSMutableArray(cCount);
			
			Pattern p = Pattern.compile("(?i).*[/\\\\]+((.)+)\\.framework[\\/]*?.*?");
			
			for (int c = 0; c < cCount; c++) {
				String classPathElement = (String) classPathArray.objectAtIndex(c);
				Matcher m = p.matcher(classPathElement);
				
				if ((m.find())) {
					String bundleName = m.group(1);
					
					if (!bundles.containsObject(bundleName)) {
						bundles.addObject(bundleName);
						array.addObject(classPathElement);
					}
				} else {
					array.addObject(classPathElement);
				}
			}
			
			if (array.count() < cCount) {
				System.setProperty("java.class.path", array
						.componentsJoinedByString(File.pathSeparator));
			}
		}
	}
	
	
	
	
	// Constructor
	
	public Application()
	{
		super();
	}
	
	
	
	// Public instance methods
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOApplication#appendToResponse(com.webobjects.appserver.WOResponse,
	 *      com.webobjects.appserver.WOContext)
	 */
	public void appendToResponse(WOResponse response, WOContext context)
	{
		response.setContentEncoding("UTF8");
		
		super.appendToResponse(response, context);
		
		String contentTypeKey = "Content-Type";
		String contentType = response.headerForKey(contentTypeKey);
		
		if ((contentType == null) || (contentType.startsWith("text/html"))) {
			response.setHeader("text/html;charset=utf-8", contentTypeKey);
		}
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOApplication#takeValuesFromRequest(com.webobjects.appserver.WORequest,
	 *      com.webobjects.appserver.WOContext)
	 */
	public void takeValuesFromRequest(WORequest request, WOContext context)
	{
		request.setDefaultFormValueEncoding("UTF8");
		
		super.takeValuesFromRequest(request, context);
	}
	
	
	public WOResponse handleException(Exception exception, WOContext context)
	{
		Throwable throwable = ForwardException.targetException(exception);
		String errorPageName = errorPageName();
		
		System.err.println("Handling uncaught exception: " + throwable.getLocalizedMessage());
		throwable.printStackTrace();
		
		if (errorPageName != null) {
			WOComponent errorPage = pageWithName(errorPageName, context);
			
			((ErrorPageInterface) errorPage).setThrowable(throwable);
			
			return errorPage.generateResponse();
		} else {
			Exception targetException;
			
			if (throwable instanceof Exception) {
				targetException = (Exception) throwable;
			} else {
				targetException = new NSForwardException(throwable);
			}
			
			return super.handleException(targetException, context);
		}
	}
	
	
	
	/**
	 * Name of the page handling errors. <br/>
	 * 
	 * Subclasses should override this to customize error handling bahavior.
	 * 
	 * @return the name of a page component implementing ErrorPageInterface
	 */
	public String errorPageName()
	{
		return null;
	}
	
	
	
	// Pseudo-private methods
	
	public boolean _isForeignSupportedDevelopmentPlatform()
	{
		if (!super._isForeignSupportedDevelopmentPlatform()) {
			String osName = System.getProperty("os.name");
			
			return "Windows XP".equals(osName);
		}
		
		return true;
	}
}