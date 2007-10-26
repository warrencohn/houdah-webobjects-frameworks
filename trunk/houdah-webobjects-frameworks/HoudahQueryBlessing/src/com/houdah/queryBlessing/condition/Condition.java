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

package com.houdah.queryBlessing.condition;

import com.houdah.queryBlessing.BlessingContext;
import com.houdah.queryBlessing.BlessingErrorCause;
import com.houdah.queryBlessing.BlessingStatus;

import com.webobjects.eocontrol.EOKeyValueArchiving;
import com.webobjects.foundation.NSCoding;
import com.webobjects.foundation.NSSelector;

/**
 * Condition applying to an EOQualifier.<br/>
 * 
 * Concrete subclasses of this class represent specific conditions that a
 * qualifier can be verified against. E.g. one can require a qualifier to
 * provide a value for a given attribute.<br/>
 * 
 * The caller typically creates an instance of one of the subclasses that match
 * the desired condition and then calls upon QualifierBlessing.bless().
 */
public abstract class Condition implements NSCoding, EOKeyValueArchiving
{
	// Public instance methods
	
	
	/**
	 * Evaluates the condition.<br/>
	 * 
	 * Called upon by support classes when a qualifier's key and value are
	 * known.<br/>
	 * 
	 * Needs to be implemented by concrete subclasses.
	 * 
	 * @param key
	 *            for which a qualifier provided a value
	 * @param value
	 *            the value provided. Null if no value is available. NullValue
	 *            if key is compared to a NullValue
	 * @param selector
	 *            the relationship between key and value. A qualifier operator
	 * @param context
	 *            the current context. May be modified by this method
	 * @return true if the condition is verified
	 */
	public abstract boolean evaluate(String key, Object value, NSSelector selector,
			BlessingContext context);
	
	
	
	/**
	 * Evaluates the condition against a null qualifier.<br/>
	 * 
	 * The default implementation merely returns false. This should be
	 * sufficient for most uses. Some subclasses may nonetheless have to
	 * override this method. Eg. if the condition prohibits a given criteria to
	 * appear in the clause
	 * 
	 * @param context
	 *            the current context. May be modified by this method
	 * @return true if the condition is verified
	 */
	public boolean evaluateNullQualifier(BlessingContext context)
	{
		context.setErrorCause(new BlessingErrorCause("BLESS_NO_QUALIFIER", null, null, null));
		
		return false;
	}
	
	
	
	/**
	 * Returns the error message to quote if this condition is not verified.<br/>
	 * 
	 * Called by the QualifierBlessing.bless() method when blessing is denied.
	 * 
	 * @return the error message, or null to use the default message
	 */
	public String errorMessage(BlessingStatus status)
	{
		return null;
	}
	
	
	
	/**
	 * Creates a new status object for the current condition.<br/>
	 * 
	 * @return a new status object
	 */
	public abstract BlessingStatus newStatus();
}