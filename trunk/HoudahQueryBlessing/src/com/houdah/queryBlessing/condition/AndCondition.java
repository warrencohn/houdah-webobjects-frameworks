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

import com.webobjects.eocontrol.EOKeyValueUnarchiver;
import com.webobjects.foundation.NSCoder;

/**
 * Condition applying to an EOQualifier.<br/>
 * 
 * Concrete subclass of BinOpCondition. Applies two conditions to a qualifier.
 * Fails if either of the conditions is not verified.
 */
public class AndCondition extends BinOpCondition
{
	// Constructor
	
	/**
	 * Public constructor.
	 * 
	 * @param errorMessage
	 *            the errorMessage to refer to when a qualifier fails
	 *            verification
	 * @param firstCondition
	 *            the first of the two conditions that are to be verified
	 * @param secondCondition
	 *            the second of the two conditions that are to be verified
	 */
	public AndCondition(String errorMessage, Condition firstCondition, Condition secondCondition)
	{
		super(errorMessage, firstCondition, secondCondition);
	}
	
	
	
	/**
	 * Public constructor.
	 * 
	 * @param firstCondition
	 *            the first of the two conditions that are to be verified
	 * @param secondCondition
	 *            the second of the two conditions that are to be verified
	 */
	public AndCondition(Condition firstCondition, Condition secondCondition)
	{
		this(null, firstCondition, secondCondition);
	}
	
	
	
	// Protected instance method
	
	/**
	 * Determines the result ot return based on the result returned by the two
	 * child conditions.
	 * 
	 * @param firstResult
	 *            the result returned by the first condition
	 * @param secondResult
	 *            the result returned by the second condition
	 * @param the
	 *            result to return by the combined condition
	 */
	protected boolean computeResult(boolean firstResult, boolean secondResult)
	{
		return firstResult && secondResult;
	}
	
	
	
	// Conformance with NSCoding
	
	public static Object decodeObject(NSCoder coder)
	{
		return new AndCondition((String) coder.decodeObject(), (Condition) coder.decodeObject(),
				(Condition) coder.decodeObject());
	}
	
	
	
	// Conformance with KeyValueCodingArchiving
	
	public static Object decodeWithKeyValueUnarchiver(EOKeyValueUnarchiver keyValueUnarchiver)
	{
		return new AndCondition((String) keyValueUnarchiver.decodeObjectForKey(ERROR_MESSAGE_KEY),
				(Condition) keyValueUnarchiver.decodeObjectForKey(FIRST_CONDITION_KEY),
				(Condition) keyValueUnarchiver.decodeObjectForKey(SECOND_CONDITION_KEY));
	}
	
}