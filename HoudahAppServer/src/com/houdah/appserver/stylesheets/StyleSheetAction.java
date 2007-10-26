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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.houdah.appserver.application.DirectAction;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResourceManager;
import com.webobjects.appserver.WOResponse;

public class StyleSheetAction extends DirectAction
{
	// Public class constants
	
	/**
	 * A query dictionary key
	 */
	public static final String	PATH_KEY	= "path";
	
	
	
	/**
	 * A query dictionary key
	 */
	public static final String	BUNDLE_KEY	= "bundle";
	
	
	
	
	// Constructor
	
	public StyleSheetAction(WORequest request)
	{
		super(request);
	}
	
	
	
	// Action methods
	
	public WOActionResults cssAction() throws MalformedURLException
	{
		WOResponse response = new WOResponse();
		String filePath = (String) request().formValueForKey(
				StyleSheetAction.PATH_KEY);
		
		if (filePath != null) {
			String bundleName = (String) request().formValueForKey(
					StyleSheetAction.BUNDLE_KEY);
			
			try {
				URL url = new URL(filePath);
				InputStream stream = url.openStream();
				String cssFile = stringFromInputStream(stream);
				
				response.appendContentString(mangleURLs(cssFile, bundleName,
						context().request()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
	
	
	
	// Protected class methods
	
	protected static String stringFromInputStream(InputStream stream)
	{
		try {
			byte[] data = new byte[stream.available()];
			
			stream.read(data);
			stream.close();
			
			return new String(data);
		} catch (IOException ioe) {
			return null;
		}
	}
	
	
	protected static String mangleURLs(String cssFile, String bundleName,
			WORequest request)
	{
		WOApplication application = WOApplication.application();
		WOResourceManager resourceManager = application.resourceManager();
		
		Pattern p = Pattern.compile("(?i)\\surl\\([\"']?(.*?)[\"']?\\)([\\s;]?)");
		Matcher m = p.matcher(cssFile);
		StringBuffer sb = new StringBuffer();
		
		while (m.find()) {
			String fileName = m.group(1);
			StringBuffer url = new StringBuffer();
			
			url.append(" url('");
			url.append(resourceManager.urlForResourceNamed(fileName,
					bundleName, null, request));
			url.append("')");
			url.append(m.group(2));
					
			m.appendReplacement(sb, url.toString());
		}
		
		m.appendTail(sb);
		
		return sb.toString();
	}
}