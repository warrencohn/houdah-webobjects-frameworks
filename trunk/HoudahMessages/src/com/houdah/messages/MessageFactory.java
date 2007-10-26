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

package com.houdah.messages;

import java.util.Enumeration;

import com.houdah.foundation.ForwardException;
import com.houdah.foundation.utilities.StringUtilities;
import com.houdah.ruleengine.RuleContext;
import com.houdah.ruleengine.RuleModel;
import com.houdah.ruleengine.RuleModelUtilities;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSSet;
import com.webobjects.foundation.NSValidation;

public class MessageFactory
{
	// Public class constants
	
	public static final String		MESSAGE_KEY			= "message";
	
	
	public static final String		MESSAGE_CODE_KEY	= "messageCode";
	
	
	
	// Private class variables
	
	private static MessageFactory	sharedInstance		= null;
	
	
	private static RuleModel		messageModel;
	
	
	
	// Protected instance variables
	
	protected RuleContext			messageContext;
	
	
	
	
	// Constructor
	
	public MessageFactory()
	{
		this.messageContext = new RuleContext(messageModel());
	}
	
	
	
	// Public instance methods
	
	/**
	 * Accessor method.
	 * 
	 * Retrieves the rule context used for deriving message values.<br/>
	 * 
	 * You may set default values on this context. E.g. you may implement
	 * localization by using a "language" key in your rule files and setting it
	 * a value in this context.
	 * myMessageFactory.ruleContext().takeValueForKey("English", "language");
	 */
	public RuleContext messageContext()
	{
		return this.messageContext;
	}
	
	
	
	/**
	 * Build an appropriate message.
	 * 
	 * @param messageKey
	 *            key (e.g. error code) to the message
	 * @param arguments
	 *            contextual information used both for retrieving the message
	 *            and for binding its variables
	 * @return a readable message. null, in the event of a problem
	 */
	public String message(String messageKey, NSDictionary arguments)
	{
		RuleContext localContext = new RuleContext(this.messageContext);
		
		if (arguments != null) {
			Enumeration keyEnumeration = arguments.keyEnumerator();
			
			while (keyEnumeration.hasMoreElements()) {
				String key = (String) keyEnumeration.nextElement();
				Object value = arguments.objectForKey(key);
				
				localContext.takeValueForKey(value, key);
			}
		}
		
		localContext.takeValueForKey(null, MessageFactory.MESSAGE_KEY);
		localContext.takeValueForKey(messageKey, MessageFactory.MESSAGE_CODE_KEY);
		
		String message = (String) localContext.valueForKey(MessageFactory.MESSAGE_KEY);
		
		return StringUtilities.bind(message, localContext);
	}
	
	
	/**
	 * Build the appropriate messages.
	 * 
	 * @param throwable
	 *            throwable for which a detailed message is needed
		 * @return
	 */
	public NSArray messages(Throwable throwable)
	{
		Throwable targetException = ForwardException.targetException(throwable);
		
		NSMutableArray messages = new NSMutableArray();
		
		if (targetException instanceof MessageException) {
			MessageException messageException = (MessageException) targetException;
			String messageCode = messageException.messageCode();
			NSDictionary context = messageException.context();
			String message = message(messageCode, context);
			
			if (message == null) {
				message = messageCode;
				
				if (message == null) {
					message = targetException.getClass().getName();
				}
			}
			
			messages.addObject(message);
		} else {
			String message = targetException.getLocalizedMessage();
			
			if (message == null) {
				message = targetException.getMessage();
				
				if (message == null) {
					message = targetException.getClass().getName();
				}
			}
			
			messages.addObject(message);
		}
		
		if (targetException instanceof NSValidation.ValidationException) {
			NSValidation.ValidationException validationException = (NSValidation.ValidationException) targetException;
			
			NSArray additionalExceptions = validationException.additionalExceptions();
			int aCount = (additionalExceptions != null) ? additionalExceptions.count() : 0;
			
			for (int a = 0; a < aCount; a++) {
				Exception childException = (Exception) additionalExceptions.objectAtIndex(a);
				
				messages.addObjectsFromArray(messages(childException));
			}
		}
		
		return messages;
	}
	
	
	
	// Protected instance methods
	
	
	// Public class methods
	
	public static MessageFactory sharedInstance()
	{
		if (MessageFactory.sharedInstance == null) {
			setSharedInstance(new MessageFactory());
		}
		
		return MessageFactory.sharedInstance;
	}
	
	
	public static void setSharedInstance(MessageFactory factory)
	{
		MessageFactory.sharedInstance = factory;
	}
	
	
	
	// Private class methods
	
	private RuleModel messageModel()
	{
		if (MessageFactory.messageModel == null) {
			synchronized (MessageFactory.class) {
				if (MessageFactory.messageModel == null) {
					NSSet includeFiles = new NSSet(new Object[] { "labels.dictionary" });
					RuleModel newRuleModel = RuleModelUtilities.loadFromBundles("message", null,
							null, includeFiles);
										
					MessageFactory.messageModel = newRuleModel;
				}
			}
		}
		
		return MessageFactory.messageModel;
	}
}