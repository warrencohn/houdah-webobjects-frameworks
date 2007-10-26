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

import com.houdah.queryBlessing.BlessingStatus;

/**
 * Condition applying to an EOQualifier.<br/>
 * 
 * Concrete subclasses of this class represent specific conditions that a
 * qualifier can be verified against. E.g. one can require a qualifier to
 * provide a value for a given attribute.<br/>
 * 
 * The caller typically creates an instance of one of the subclasses that match
 * the desired condition and than call that instance's bless(qualifier) method
 * on qualifiers that need to match.
 */
public abstract class KeyCondition extends Condition
{
	// Protected class constants
	
	protected static final String	ERROR_MESSAGE_KEY	= "errorMessage";
	
	
	protected static final String	CONDITION_KEY_KEY	= "conditionKey";
	
	
	
	// Protected instance variables
	
	protected String				errorMessage;
	
	
	protected String				conditionKey;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor.
	 * 
	 * @param errorMessage
	 *            the errorMessage to refer to when a qualifier fails
	 *            verification
	 * @param conditionKey
	 *            the name of the attribute this condition applies to
	 */
	public KeyCondition(String errorMessage, String conditionKey)
	{
		this.errorMessage = errorMessage;
		this.conditionKey = conditionKey;
	}
	
	
	
	// Public instance methods
	
	/**
	 * Returns the error message to quote if this condition is not verified.<br/>
	 * 
	 * Called by the QualifierBlessing.bless() method when blessing is denied.
	 * 
	 * @return the error message, or null to use the default message
	 */
	public String errorMessage(BlessingStatus status)
	{
		return this.errorMessage;
	}
}