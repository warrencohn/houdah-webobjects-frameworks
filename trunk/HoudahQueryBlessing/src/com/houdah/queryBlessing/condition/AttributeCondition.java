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

import com.webobjects.eocontrol.EOKeyValueArchiver;
import com.webobjects.eocontrol.EOKeyValueUnarchiver;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSCoder;
import com.webobjects.foundation.NSSelector;

/**
 * Condition applying to an EOQualifier.<br/>
 * 
 * Concrete subclasse of KeyCondition. Requires that the qualifier references a
 * given attribute.<br/>
 */
public class AttributeCondition extends KeyCondition
{
	// Protected class constants
	
	protected static final String	AS_LESS_THAN_KEY	= "asLessThan";
	
	
	protected static final String	AS_EQUALS_KEY		= "asEquals";
	
	
	protected static final String	AS_NOT_EQUALS_KEY	= "asNotEquals";
	
	
	protected static final String	AS_GREATER_THAN_KEY	= "asGreaterThan";
	
	
	
	// Protected instance variables
	
	/**
	 * Flag that determines if the attribute is considered present if found in a
	 * LESS THAN (OR EQUAL) clause.
	 */
	protected boolean				asLessThan;
	
	
	
	/**
	 * Flag that determines if the attribute is considered present if found in
	 * an EQUALS clause.
	 */
	protected boolean				asEquals;
	
	
	
	/**
	 * Flag that determines if the attribute is considered present if found in
	 * an NOT EQUALS clause.
	 */
	protected boolean				asNotEquals;
	
	
	
	/**
	 * Flag that determines if the attribute is considered present if found in a
	 * GRAETER THAN (OR EQUAL) clause.
	 */
	protected boolean				asGreaterThan;
	
	
	
	
	// Constructor
	
	/**
	 * Public constructor.
	 * 
	 * @param errorMessage
	 *            the errorMessage to refer to when a qualifier fails
	 *            verification
	 * @param conditionKey
	 *            the name of the attribute this condition applies to
	 * @param asLessThan
	 *            flag that determines if the attribute is considered present if
	 *            found in a LESS THAN (OR EQUAL) clauses
	 * @param asEquals
	 *            flag that determines if the attribute is considered present if
	 *            found in a EQUALS clauses
	 * @param greaterThan
	 *            flag that determines if the attribute is considered present if
	 *            found in a GREATER THAN (OR EQUAL) clauses
	 */
	public AttributeCondition(String errorMessage, String conditionKey, boolean asLessThan,
			boolean asEquals, boolean asNotEquals, boolean asGreaterThan)
	{
		super(errorMessage, conditionKey);
		
		this.asLessThan = asLessThan;
		this.asEquals = asEquals;
		this.asNotEquals = asNotEquals;
		this.asGreaterThan = asGreaterThan;
	}
	
	
	
	/**
	 * Public constructor.
	 * 
	 * @param errorMessage
	 *            the errorMessage to refer to when a qualifier fails
	 *            verification
	 * @param conditionKey
	 *            the name of the attribute this condition applies to
	 * @param asLessThan
	 *            flag that determines if the attribute is considered present if
	 *            found in a LESS THAN (OR EQUAL) clauses
	 * @param asEquals
	 *            flag that determines if the attribute is considered present if
	 *            found in a EQUALS clauses
	 * @param greaterThan
	 *            flag that determines if the attribute is considered present if
	 *            found in a GREATER THAN (OR EQUAL) clauses
	 */
	public AttributeCondition(String conditionKey, boolean asLessThan, boolean asEquals,
			boolean asNotEquals, boolean asGreaterThan)
	{
		this(null, conditionKey, asLessThan, asEquals, asNotEquals, asGreaterThan);
	}
	
	
	
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
	public boolean evaluate(String key, Object value, NSSelector selector, BlessingContext context)
	{
		if (conditionKey.equals(key)) {
			BlessingStatus status = context.status();
			boolean negate = status.negate();
			
			if (negate) {
				return (asLessThan && asEquals && asNotEquals && asGreaterThan)
						|| (asGreaterThan && ((selector == EOQualifier.QualifierOperatorLessThan) || (selector == EOQualifier.QualifierOperatorLessThanOrEqualTo)))
						|| (asNotEquals && (selector == EOQualifier.QualifierOperatorEqual)
								|| (selector == EOQualifier.QualifierOperatorCaseInsensitiveLike) || (selector == EOQualifier.QualifierOperatorLike))
						|| (asEquals && (selector == EOQualifier.QualifierOperatorNotEqual))
						|| (asLessThan && ((selector == EOQualifier.QualifierOperatorGreaterThan) || (selector == EOQualifier.QualifierOperatorGreaterThanOrEqualTo)));
			} else {
				return (asLessThan && asEquals && asNotEquals && asGreaterThan)
						|| (asLessThan && ((selector == EOQualifier.QualifierOperatorLessThan) || (selector == EOQualifier.QualifierOperatorLessThanOrEqualTo)))
						|| (asEquals && (selector == EOQualifier.QualifierOperatorEqual)
								|| (selector == EOQualifier.QualifierOperatorCaseInsensitiveLike) || (selector == EOQualifier.QualifierOperatorLike))
						|| (asNotEquals && (selector == EOQualifier.QualifierOperatorNotEqual))
						|| (asGreaterThan && ((selector == EOQualifier.QualifierOperatorGreaterThan) || (selector == EOQualifier.QualifierOperatorGreaterThanOrEqualTo)));
			}
		}
		
		context.setErrorCause(new BlessingErrorCause(this.errorMessage, key, value, selector));
		
		return false;
	}
	
	
	
	/**
	 * Creates a new status object for the current condition.<br/>
	 * 
	 * Called by the bless() method to create a status object representing the
	 * initial context.
	 * 
	 * @return a new status object
	 */
	public BlessingStatus newStatus()
	{
		return new BlessingStatus();
	}
	
	
	
	// Conformance with NSCoding
	
	public Class classForCoder()
	{
		return getClass();
	}
	
	
	public static Object decodeObject(NSCoder coder)
	{
		return new AttributeCondition((String) coder.decodeObject(), (String) coder.decodeObject(),
				coder.decodeBoolean(), coder.decodeBoolean(), coder.decodeBoolean(), coder
						.decodeBoolean());
	}
	
	
	public void encodeWithCoder(NSCoder coder)
	{
		coder.encodeObject(this.errorMessage);
		coder.encodeObject(this.conditionKey);
		coder.encodeBoolean(this.asLessThan);
		coder.encodeBoolean(this.asEquals);
		coder.encodeBoolean(this.asNotEquals);
		coder.encodeBoolean(this.asGreaterThan);
	}
	
	
	
	// Conformance with KeyValueCodingArchiving
	
	public void encodeWithKeyValueArchiver(EOKeyValueArchiver keyValueArchiver)
	{
		keyValueArchiver.encodeObject(this.errorMessage, ERROR_MESSAGE_KEY);
		keyValueArchiver.encodeObject(this.conditionKey, CONDITION_KEY_KEY);
		keyValueArchiver.encodeBool(this.asLessThan, AS_LESS_THAN_KEY);
		keyValueArchiver.encodeBool(this.asEquals, AS_EQUALS_KEY);
		keyValueArchiver.encodeBool(this.asNotEquals, AS_NOT_EQUALS_KEY);
		keyValueArchiver.encodeBool(this.asGreaterThan, AS_GREATER_THAN_KEY);
	}
	
	
	public static Object decodeWithKeyValueUnarchiver(EOKeyValueUnarchiver keyValueUnarchiver)
	{
		return new AttributeCondition((String) keyValueUnarchiver
				.decodeObjectForKey(ERROR_MESSAGE_KEY), (String) keyValueUnarchiver
				.decodeObjectForKey(CONDITION_KEY_KEY), keyValueUnarchiver
				.decodeBoolForKey(AS_LESS_THAN_KEY), keyValueUnarchiver
				.decodeBoolForKey(AS_EQUALS_KEY), keyValueUnarchiver
				.decodeBoolForKey(AS_NOT_EQUALS_KEY), keyValueUnarchiver
				.decodeBoolForKey(AS_GREATER_THAN_KEY));
	}
}