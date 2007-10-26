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

package com.houdah.web.control.support;

import com.houdah.messages.MessageFactory;
import com.houdah.web.control.application.Application;
import com.houdah.web.control.application.Session;
import com.houdah.web.control.application.ThreadStorage;
import com.houdah.web.control.controllers.SessionController;
import com.houdah.web.view.form.values.Value;
import com.houdah.web.view.form.values.ValueExceptionHandler;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSLog;

public class ExceptionHandler implements ValueExceptionHandler
{
	// Private instance variables
	
	private Application	application;
	
	
	
	
	// Constructor
	
	public ExceptionHandler(Application application)
	{
		this.application = application;
	}
	
	
	
	// Public instance methods
	
	public void handleException(Value container, Exception exception)
	{
		ThreadStorage threadStorage = this.application.threadStorage();
		Session session = threadStorage.session();
		SessionController sessionController = session.sessionController();
		MessageFactory messageFactory = sessionController.messageFactory();
		
		String message = null;
		NSArray messages = messageFactory.messages(exception);
		int mCount = messages.count();
		
		if (mCount > 0) {
			message = (String) messages.objectAtIndex(0);
			
			if (mCount > 1) {
				NSLog.err.appendln(messages);
			}
		}
		
		if (message == null) {
			message = "Unknown error";
		}
		
		container.setErrorMessage(message);
	}
}