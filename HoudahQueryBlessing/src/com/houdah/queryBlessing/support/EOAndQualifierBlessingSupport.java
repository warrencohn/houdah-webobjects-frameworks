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

import com.houdah.queryBlessing.BlessingContext;
import com.houdah.queryBlessing.QualifierBlessing;
import com.houdah.queryBlessing.condition.Condition;

import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;

public class EOAndQualifierBlessingSupport implements QualifierBlessing.Support
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
		EOAndQualifier andQualifier = (EOAndQualifier) qualifier;
		NSArray qualifierBranch = andQualifier.qualifiers();
		int count = qualifierBranch.count();
		boolean result = false;
		
		for (int i = 0; (!result) && (i < count); i++) {
			EOQualifier childQualifier = (EOQualifier) qualifierBranch.objectAtIndex(i);
			
			result = result || QualifierBlessing._bless(childQualifier, condition, localContext);
		}
		
		return result;
	}
}