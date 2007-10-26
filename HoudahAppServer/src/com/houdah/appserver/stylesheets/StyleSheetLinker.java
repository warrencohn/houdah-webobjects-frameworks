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

package com.houdah.appserver.stylesheets;

import java.net.URL;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODynamicElement;
import com.webobjects.appserver.WOElement;
import com.webobjects.appserver.WOResourceManager;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSBundle;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * Dynamic element to expose CSS files to the client by linking to them. <br/>
 * 
 * Retrieves the style.css, screen.css and print.css files from all available
 * bundles. The last two are media specific.
 * 
 * 
 */
public class StyleSheetLinker extends WODynamicElement
{
	// Protected class constants
	
	protected static final String[]	MEDIA_KEYS	= new String[] { "style", "screen",
			"print"						};
	
	
	
	// Private instance variables
	
	private String					content	= null;
	
	
	
	
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
	public StyleSheetLinker(String name, NSDictionary associations,
			WOElement children)
	{
		super(name, associations, children);
	}
	
	
	
	// Public instance methods
	
	public void appendToResponse(WOResponse response, WOContext context)
	{
		if (this.content == null) {
			synchronized (this) {
				StringBuffer buffer = new StringBuffer();
				WOResourceManager resourceManager = WOApplication.application()
						.resourceManager();
				NSMutableDictionary fileURLsByMedia = new NSMutableDictionary();
				NSArray frameworks = NSBundle.frameworkBundles();
				NSMutableArray allBundles = new NSMutableArray(frameworks
						.count() + 1);
				
				allBundles.addObjectsFromArray(frameworks);
				allBundles.addObject(NSBundle.mainBundle());
				
				
				// Group available files by media type
				
				for (int bCount = allBundles.count(), b = 0; b < bCount; b++) {
					NSBundle bundle = (NSBundle) allBundles.objectAtIndex(b);
					String bundleName = bundle.name();
					
					for (int mCount = MEDIA_KEYS.length, m = 0; m < mCount; m++) {
						String mediaName = MEDIA_KEYS[m];
						String fileName = mediaName + ".css";
						URL pathURL = resourceManager.pathURLForResourceNamed(
								fileName, bundleName, null);
						NSMutableArray urls = (NSMutableArray) fileURLsByMedia
								.objectForKey(fileName);
						
						if (urls == null) {
							urls = new NSMutableArray();
							fileURLsByMedia.setObjectForKey(urls, fileName);
						}
						
						if (pathURL != null) {
							String url = makeURL(pathURL, fileName, bundleName,
									resourceManager, context);
							urls.addObject(url);
						}
					}
				}
				
				
				// Generate grouped links
				
				for (int mCount = MEDIA_KEYS.length, m = 0; m < mCount; m++) {
					String mediaName = MEDIA_KEYS[m];
					String fileName = mediaName + ".css";
					NSArray urls = (NSArray) fileURLsByMedia
							.objectForKey(fileName);
					int uCount = urls.count();
					
					if (uCount > 0) {
						buffer.append("<style type=\"text/css\" ");
						
						if (!"style".equals(mediaName)) {
							buffer.append("media=\"");
							buffer.append(mediaName);
							buffer.append("\"");
						}
						
						buffer.append(">\n");
						
						for (int u = 0; u < uCount; u++) {
							buffer.append("@import url(\"");
							buffer.append(urls.objectAtIndex(u));
							buffer.append("\");\n");
						}
						
						buffer.append("</style>");
					}
				}
				
				this.content = buffer.toString();
			}
		}
		
		response.appendContentString(this.content);
	}
	
	
	
	// Protected instance methods
	
	protected String makeURL(URL pathURL, String fileName, String bundleName,
			WOResourceManager resourceManager, WOContext context)
	{
		if (!context.request().isUsingWebServer()) {
			
			NSMutableDictionary queryDict = new NSMutableDictionary(2);
			
			queryDict.setObjectForKey(pathURL.toExternalForm(),
					StyleSheetAction.PATH_KEY);
			queryDict.setObjectForKey(bundleName, StyleSheetAction.BUNDLE_KEY);
			
			String url = context.directActionURLForActionNamed(
					"StyleSheetAction/css", queryDict);
			
			
			// bug workaround
			url = url.replaceAll("&amp;", "&");
			
			return url;
			
			
			// StringBuffer url = new
			// StringBuffer(context.directActionURLForActionNamed(
			// "HVCStyleSheetAction/css", null));
			//
			// try {
			// url.append("&");
			// url.append(HVCStyleSheetAction.PATH_KEY);
			// url.append("=");
			// url.append(URLEncoder.encode(pathURL.toExternalForm(), "UTF-8"));
			//
			// url.append("&");
			// url.append(HVCStyleSheetAction.BUNDLE_KEY);
			// url.append("=");
			// url.append(URLEncoder.encode(bundleName, "UTF-8"));
			//
			// }
			// catch (UnsupportedEncodingException e) {
			// new ForwardException(e);
			// }
			//
			// return url.toString();
		} else {
			return resourceManager.urlForResourceNamed(fileName, bundleName,
					null, context.request());
		}
	}
}
