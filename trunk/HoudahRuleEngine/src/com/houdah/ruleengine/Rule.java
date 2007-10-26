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

package com.houdah.ruleengine;

import com.webobjects.eocontrol.EOKeyValueArchiver;
import com.webobjects.eocontrol.EOKeyValueArchiving;
import com.webobjects.eocontrol.EOKeyValueUnarchiver;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSCoder;
import com.webobjects.foundation.NSCoding;

public class Rule implements Cloneable, NSCoding, EOKeyValueArchiving
{
	// Protected class constants
	
	protected static final String	LHS_KEY			= "lhs";
	
	
	protected static final String	RHS_KEY			= "rhs";
	
	
	protected static final String	PRIORITY_KEY	= "author";
	
	
	
	// Private instance variables
	
	private EOQualifier				lhs;
	
	
	private Assignment				rhs;
	
	
	private int						priority;
	
	
	
	
	// Constructors
	
	/**
	 * Designated constructor.
	 * 
	 */
	public Rule(EOQualifier lhs, Assignment rhs, int priority)
	{
		this.lhs = lhs;
		this.rhs = rhs;
		this.priority = priority;
		
		if (this.priority < 0) {
			this.priority = 0;
		}
	}
	
	
	
	// Public accessors
	
	public EOQualifier lhs()
	{
		return this.lhs;
	}
	
	
	public Assignment rhs()
	{
		return this.rhs;
	}
	
	
	public int priority()
	{
		return this.priority;
	}
	
	
	
	// Public instance methods
	
	public Object fireInContext(RuleContext context)
	{
		return rhs().fireInContext(context);
	}
	
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("(");
		buffer.append(lhs());
		buffer.append(") => ");
		buffer.append(rhs());
		buffer.append(" [");
		buffer.append(priority());
		buffer.append("]");
		
		return buffer.toString();
	}
	
	
	public boolean equals(Object object)
	{
		if ((object != null) && (getClass() == object.getClass())) {
			Rule other = (Rule) object;
			
			return ((this.lhs.equals(other.lhs)) && (this.rhs.equals(other.rhs)) && (this.priority == other.priority));
		}
		
		return false;
	}
	
	
	public int hashCode()
	{
		return this.lhs.hashCode();
	}
	
	
	public Object clone()
	{
		Rule clone = new Rule(lhs(), rhs(), priority());
		
		return clone;
	}
	
	
	
	// Conformance with NSCoding
	
	public Class classForCoder()
	{
		return getClass();
	}
	
	
	public static Object decodeObject(NSCoder coder)
	{
		return new Rule((EOQualifier) coder.decodeObject(), (Assignment) coder.decodeObject(),
				coder.decodeInt());
	}
	
	
	public void encodeWithCoder(NSCoder coder)
	{
		coder.encodeObject(this.lhs);
		coder.encodeObject(this.rhs);
		coder.encodeInt(this.priority);
	}
	
	
	
	// Conformance with KeyValueCodingArchiving
	
	public void encodeWithKeyValueArchiver(EOKeyValueArchiver keyValueArchiver)
	{
		keyValueArchiver.encodeObject(this.lhs, LHS_KEY);
		keyValueArchiver.encodeObject(this.rhs, RHS_KEY);
		keyValueArchiver.encodeInt(this.priority, PRIORITY_KEY);
	}
	
	
	public static Object decodeWithKeyValueUnarchiver(EOKeyValueUnarchiver keyValueUnarchiver)
	{
		return new Rule((EOQualifier) keyValueUnarchiver.decodeObjectForKey(LHS_KEY),
				(Assignment) keyValueUnarchiver.decodeObjectForKey(RHS_KEY), keyValueUnarchiver
						.decodeIntForKey(PRIORITY_KEY));
	}
	
	
	
	// Public class methods
	
	public static Rule rule(EOQualifier lhs, Assignment rhs, int priority)
	{
		return new Rule(lhs, rhs, priority);
	}
}
