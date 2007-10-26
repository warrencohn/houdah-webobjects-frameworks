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

package com.houdah.web.control.controllers;

import com.houdah.eocontrol.EditingContextFactory;
import com.houdah.foundation.KVCObject;
import com.houdah.messages.MessageFactory;
import com.houdah.web.control.application.SecurityManager;
import com.houdah.web.control.application.Session;
import com.houdah.web.control.components.HCCLayout;

import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOObjectStore;
import com.webobjects.foundation.NSLog;

public class SessionController extends KVCObject
{
	// Public class constants
	
	// Private instance variables
	
	private Session					session;
	
	
	
	/**
	 * Name of the wrapper to use when building pages.
	 */
	private String					wrapperName;
	
	
	
	/**
	 * Editing context factory.
	 */
	private EditingContextFactory	editingContextFactory;
	
	
	
	/**
	 * Message factory.
	 */
	private MessageFactory			messageFactory;
	
	
	
	/**
	 * Delegate object responsible of notifying the user of the success or
	 * failure of an operation.
	 */
	private MessageCentral			messageCentral;
	
	
	
	/**
	 * Security manager: Grants or denies access to tasks
	 */
	private SecurityManager			securityManager;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor.
	 */
	public SessionController(Session session)
	{
		this.session = session;
		
		
		// Set a default value for the wrapper component
		this.wrapperName = HCCLayout.class.getName();
		
		this.editingContextFactory = EditingContextFactory.sharedInstance();
		this.messageFactory = MessageFactory.sharedInstance();
		this.securityManager = new SecurityManager();
		
		this.messageCentral = null;
	}
	
	
	
	// Public accessors
	
	/**
	 * Gets the owning session.
	 * 
	 * @return owner
	 */
	public Session session()
	{
		return this.session;
	}
	
	
	
	/**
	 * Gets the name of the WOComponent to be used as wrapper to all pages.
	 * 
	 * @return the name of the wrapper component
	 */
	public String wapperName()
	{
		return this.wrapperName;
	}
	
	
	
	/**
	 * Sets the name of the WOComponent to be used as wrapper to all pages.
	 * 
	 * @param wrapperName
	 *            the name of the wrapper component
	 */
	public void setWrapperName(String wrapperName)
	{
		this.wrapperName = wrapperName;
	}
	
	
	
	/**
	 * Gets the factory used for creating new editing contexts.
	 * 
	 * @return a factory instance
	 */
	public EditingContextFactory editingContextFactory()
	{
		return this.editingContextFactory;
	}
	
	
	
	/**
	 * Sets the factory to use for creating new editng contexts.
	 * 
	 * @param editingContextFactory
	 *            factory instance
	 * @see EditingContextFactory
	 */
	public void setEditingContextFactory(EditingContextFactory editingContextFactory)
	{
		this.editingContextFactory = editingContextFactory;
	}
	
	
	
	/**
	 * Gets the factory used for producing human readable message.
	 * 
	 * @return a factory instance
	 */
	public MessageFactory messageFactory()
	{
		return this.messageFactory;
	}
	
	
	
	/**
	 * Sets the factory to use for producing human readable message.
	 * 
	 * @param editingContextFactory
	 *            factory instance
	 * @see MessageFactory
	 */
	public void setMessageFactory(MessageFactory messageFactory)
	{
		this.messageFactory = messageFactory;
	}
	
	
	
	/**
	 * Sets the delegate object responsible of notifying the user of the success
	 * or failure of an operation.<br/>
	 * 
	 * With no messaging server set, no messaging features are available.<br/>
	 * 
	 * @param messageCentral
	 *            the delegate object
	 */
	public void setMessageCentral(MessageCentral messageCentral)
	{
		this.messageCentral = messageCentral;
	}
	
	
	
	/**
	 * Gets the security manager instance.
	 * 
	 * @return a security manager or null
	 */
	public SecurityManager securityManager()
	{
		return this.securityManager;
	}
	
	
	
	/**
	 * Sets the security manager instance.
	 * 
	 * @param securityManager
	 *            security manager instance
	 * @see SecurityManager
	 */
	public void setSecurityManager(SecurityManager securityManager)
	{
		this.securityManager = securityManager;
	}
	
	
	public EOEditingContext createEditingContext(EOObjectStore parentStore)
	{
		EOEditingContext editingContext = null;
		EditingContextFactory factory = editingContextFactory();
		
		if (parentStore == null) {
			editingContext = factory.editingContext();
			
			editingContext.setFetchTimestamp(System.currentTimeMillis());
		} else {
			editingContext = factory.editingContext(parentStore);
		}
		
		return editingContext;
	}
	
	
	protected WODisplayGroup createDisplayGroup()
	{
		WODisplayGroup displayGroup = new WODisplayGroup();
		
		displayGroup.setNumberOfObjectsPerBatch(15);
		
		return displayGroup;
	}
	
	
	
	// Public instance methods
	
	/**
	 * Notify the user of a successful completion of an operation.
	 * 
	 * @param confirmationMessage
	 *            the message to show to the user
	 */
	public void dispatchConfirmationMessage(String confirmationMessage)
	{
		if (this.messageCentral != null) {
			this.messageCentral.dispatchConfirmationMessage(confirmationMessage);
		} else {
			NSLog.err.appendln(confirmationMessage);
		}
	}
	
	
	
	/**
	 * Notify the user of a non fatal problem.
	 * 
	 * @param warningMessage
	 *            the message to show to the user
	 */
	public void dispatchWarningMessage(String warningMessage)
	{
		if (this.messageCentral != null) {
			this.messageCentral.dispatchWarningMessage(warningMessage);
		} else {
			NSLog.err.appendln(warningMessage);
		}
	}
	
	
	
	/**
	 * Notify the user of a failure to complete an operation or a fatal problem.
	 * 
	 * @param errorMessage
	 *            the message to show to the user
	 */
	public void dispatchErrorMessage(String errorMessage)
	{
		if (this.messageCentral != null) {
			this.messageCentral.dispatchErrorMessage(errorMessage);
		} else {
			NSLog.err.appendln(errorMessage);
		}
	}
}