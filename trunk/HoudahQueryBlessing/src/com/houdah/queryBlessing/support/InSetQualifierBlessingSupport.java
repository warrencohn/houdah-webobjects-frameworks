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

package com.houdah.queryBlessing.support;

import java.util.Enumeration;

import com.houdah.eocontrol.qualifiers.InSetQualifier;
import com.houdah.queryBlessing.BlessingContext;
import com.houdah.queryBlessing.QualifierBlessing;
import com.houdah.queryBlessing.condition.Condition;

import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSSet;

public class InSetQualifierBlessingSupport implements QualifierBlessing.Support
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
	public boolean bless(EOQualifier qualifier, Condition condition, BlessingContext localContext)
	{
		InSetQualifier inSetQualifier = (InSetQualifier) qualifier;
		String keyPath = inSetQualifier.keyPath();
		NSSet values = inSetQualifier.values();
		
		if (values.count() > 0) {
			Enumeration valueEnumeration = values.objectEnumerator();
			boolean result = false;
			
			while (valueEnumeration.hasMoreElements() && (!result)) {
				Object value = valueEnumeration.nextElement();
				
				result = condition.evaluate(keyPath, value, EOQualifier.QualifierOperatorEqual,
						localContext);
			}
			
			return result;
		} else {
			return condition.evaluate(keyPath, null, EOQualifier.QualifierOperatorEqual,
					localContext);
		}
	}
}