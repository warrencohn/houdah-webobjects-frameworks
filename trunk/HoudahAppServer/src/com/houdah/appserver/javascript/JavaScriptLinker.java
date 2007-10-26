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

package com.houdah.appserver.javascript;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODynamicElement;
import com.webobjects.appserver.WOElement;
import com.webobjects.appserver.WOResourceManager;
import com.webobjects.appserver.WOResponse;
import com.webobjects.appserver._private.WODeployedBundle;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSBundle;
import com.webobjects.foundation.NSComparator;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableSet;

/**
 * Dynamic element to expose JavaScript files to the client by linking to them.
 * <br/>
 * 
 * Retrieves all *.js files from all available bundles.
 * 
 * 
 */
public class JavaScriptLinker extends WODynamicElement
{
	// Private instance variables
	
	private String	content	= null;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param name
	 *            unused
	 * @param associations
	 *            bindings from WOD file
	 * @param children
	 *            unused
	 */
	public JavaScriptLinker(String name, NSDictionary associations, WOElement children)
	{
		super(name, associations, children);
	}
	
	
	
	// Public instance methods
	
	public void appendToResponse(WOResponse response, WOContext context)
	{
		if (this.content == null) {
			synchronized (this) {
				StringBuffer buffer = new StringBuffer();
				NSMutableSet knownURLs = new NSMutableSet();
				WOResourceManager resourceManager = WOApplication.application().resourceManager();
				NSArray frameworks = NSBundle.frameworkBundles();
				NSMutableArray allBundles = new NSMutableArray(frameworks.count() + 1);
				
				allBundles.addObjectsFromArray(frameworks);
				allBundles.addObject(NSBundle.mainBundle());
				
				for (int bCount = allBundles.count(), b = 0; b < bCount; b++) {
					NSBundle bundle = (NSBundle) allBundles.objectAtIndex(b);
					String bundleName = bundle.name();
					
					
					// HACK ALERT: I unfortunately had to resort to private API
					WODeployedBundle deployedBundle = resourceManager
							._cachedBundleForFrameworkNamed(bundleName);
					NSArray jsFiles = deployedBundle._allResourceNamesWithExtension("js", false);
					NSArray sortedJSFiles = null;
					
					try {
						sortedJSFiles = jsFiles
								.sortedArrayUsingComparator(NSComparator.AscendingCaseInsensitiveStringComparator);
					} catch (Exception e) {
						e.printStackTrace();
						
						sortedJSFiles = jsFiles;
					}
					
					for (int jCount = sortedJSFiles.count(), j = 0; j < jCount; j++) {
						String fileName = (String) sortedJSFiles.objectAtIndex(j);
						String resourceUrl = resourceManager.urlForResourceNamed(fileName,
								bundleName, null, context.request());
						
						if ((resourceUrl != null) && (!knownURLs.containsObject(resourceUrl))) {
							buffer.append("<script language=\"JavaScript\" ");
							buffer.append("src=\"");
							buffer.append(resourceUrl);
							buffer.append("\"");
							buffer.append("></script>\n");
							
							knownURLs.addObject(resourceUrl);
						}
					}
				}
				
				this.content = buffer.toString();
			}
		}
		
		response.appendContentString(this.content);
	}
}