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

package com.houdah.queryBlessing;

import com.houdah.eocontrol.qualifiers.InSetQualifier;
import com.houdah.queryBlessing.condition.Condition;
import com.houdah.queryBlessing.support.EOAndQualifierBlessingSupport;
import com.houdah.queryBlessing.support.EOKeyComparisonQualifierBlessingSupport;
import com.houdah.queryBlessing.support.EOKeyValueQualifierBlessingSupport;
import com.houdah.queryBlessing.support.EONotQualifierBlessingSupport;
import com.houdah.queryBlessing.support.EOOrQualifierBlessingSupport;
import com.houdah.queryBlessing.support.InSetQualifierBlessingSupport;

import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOKeyComparisonQualifier;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EONotQualifier;
import com.webobjects.eocontrol.EOOrQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

public abstract class QualifierBlessing
{
	// Static initializer
	
	static {
		// Register support for standard qualifiers
		// Support for custom qualifiers is typically registered by the
		// principal class
		QualifierBlessing.registerSupportForClass(new EOKeyValueQualifierBlessingSupport(),
				EOKeyValueQualifier.class);
		QualifierBlessing.registerSupportForClass(new EOKeyComparisonQualifierBlessingSupport(),
				EOKeyComparisonQualifier.class);
		QualifierBlessing.registerSupportForClass(new EONotQualifierBlessingSupport(),
				EONotQualifier.class);
		QualifierBlessing.registerSupportForClass(new EOAndQualifierBlessingSupport(),
				EOAndQualifier.class);
		QualifierBlessing.registerSupportForClass(new EOOrQualifierBlessingSupport(),
				EOOrQualifier.class);
		QualifierBlessing.registerSupportForClass(new InSetQualifierBlessingSupport(),
				InSetQualifier.class);
	}
	
	
	
	// Proteced class variables
	
	protected static NSMutableDictionary	blessingSupportForQualifierClasses;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 */
	private QualifierBlessing()
	{
		throw new IllegalStateException("Do not instantiate this utility class");
	}
	
	
	
	// Public class methods
	
	/**
	 * Register a support class for a given qualifier class.
	 * 
	 * @param support
	 *            the support class instance
	 * @param qualifierClass
	 *            the class of qualifiers to register support for
	 */
	public static void registerSupportForClass(Support support, Class qualifierClass)
	{
		blessingSupportForQualifierClasses().setObjectForKey(support, qualifierClass);
	}
	
	
	
	/**
	 * Retrieve a support class for a given qualifier class.
	 * 
	 * @param qualifierClass
	 *            the class of qualifiers to register support for
	 * @return the support class instance
	 */
	public static Support supportForQualifier(EOQualifier qualifier)
	{
		Support support = (Support) blessingSupportForQualifierClasses().objectForKey(
				qualifier.getClass());
		
		return support;
	}
	
	
	
	/**
	 * Bless a qualifier according to a provided condition.
	 * 
	 * @param qualifier
	 *            the qualifier to bless
	 * @param condition
	 *            the condtion to enforce
	 * @throws BlessingException
	 *             if the qualifier should not be blessed
	 */
	public static void bless(EOQualifier qualifier, Condition condition) throws BlessingException
	{
		bless(qualifier, condition, null);
	}
	
	
	
	/**
	 * Bless a qualifier according to a provided condition.
	 * 
	 * @param qualifier
	 *            the qualifier to bless
	 * @param condition
	 *            the condtion to enforce
	 * @param contextAdditions
	 *            values to hand off to the blessing context
	 * @throws BlessingException
	 *             if the qualifier should not be blessed
	 */
	public static void bless(EOQualifier qualifier, Condition condition,
			NSDictionary contextAdditions) throws BlessingException
	{
		BlessingContext context = new BlessingContext(condition.newStatus());
		
		context.takeValuesFromDictionary(contextAdditions);
		
		if (!_bless(qualifier, condition, context)) {
			NSMutableDictionary errorContext = context.localValues().mutableClone();
			BlessingErrorCause cause = context.errorCause();
			
			if (cause != null) {
				String message = cause.errorMessage();
				
				if (message != null) {
					errorContext.addEntriesFromDictionary(cause.dictionary());
					
					throw new BlessingException(message, errorContext);
				}
			}
			
			BlessingStatus status = context.status();
			String errorMessage = condition.errorMessage(status);
			
			if (errorMessage == null) {
				errorMessage = "BLESS_DENIED";
			}
			
			throw new BlessingException(errorMessage, errorContext);
		}
	}
	
	
	public static final boolean _bless(EOQualifier qualifier, Condition condition,
			BlessingContext context)
	{
		if (qualifier == null) {
			return evaluateNullQualifier(condition, context);
		}
		
		Support support = QualifierBlessing.supportForQualifier(qualifier);
		
		if (support != null) {
			return support.bless(qualifier, condition, context);
		} else {
			context.setErrorCause(new BlessingErrorCause("BLESS_NO_SUPPORT", null, null, null));
			
			return false;
		}
	}
	
	
	
	// Protected class methods
	
	protected static synchronized NSMutableDictionary blessingSupportForQualifierClasses()
	{
		if (QualifierBlessing.blessingSupportForQualifierClasses == null) {
			QualifierBlessing.blessingSupportForQualifierClasses = new NSMutableDictionary();
		}
		
		return QualifierBlessing.blessingSupportForQualifierClasses;
	}
	
	
	protected static final boolean evaluateNullQualifier(Condition condition,
			BlessingContext context)
	{
		return condition.evaluateNullQualifier(context);
	}
	
	
	
	
	// Public inner interfaces
	
	/**
	 * Support class to apply qualifier attribute coercion to a given qualifier.
	 */
	public static interface Support
	{
		// Public instance methods
		
		/**
		 * Bless a qualifier.<br/>
		 * 
		 * Compund qualifier should traverse their child qualifier hierarchy and
		 * attempt to bless the leaf qualifiers using the provided condition.<br/>
		 * 
		 * @param qualifier
		 *            the qualifier to bless
		 * @param condition
		 *            the condition to apply to the qualifier
		 * @param context
		 *            the current context. May be modified by this method
		 * @return true if the qualifier matches the condition
		 */
		public abstract boolean bless(EOQualifier qualifier, Condition condition,
				BlessingContext localContext);
	}
}