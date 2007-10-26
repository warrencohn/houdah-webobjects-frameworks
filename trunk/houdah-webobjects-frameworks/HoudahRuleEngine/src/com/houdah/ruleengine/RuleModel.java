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

import java.util.Enumeration;

import com.houdah.foundation.utilities.DictionaryUtilities;

import com.webobjects.eocontrol.EOKeyValueArchiver;
import com.webobjects.eocontrol.EOKeyValueArchiving;
import com.webobjects.eocontrol.EOKeyValueUnarchiver;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSCoder;
import com.webobjects.foundation.NSCoding;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

public class RuleModel implements Cloneable, NSCoding, EOKeyValueArchiving
{
	// Protected class constants
	
	protected static final String	RULES_KEY		= "rules";
	
	
	protected static final String	KEYPATH_BINDING	= "keyPath";
	
	
	
	// Private instance variables
	
	private NSArray					rules;
	
	
	private NSMutableDictionary		candidateRulesByKey;
	
	
	
	
	// Constructors
	
	/**
	 * Designated constructor.
	 * 
	 */
	public RuleModel(NSArray rules)
	{
		// Set the rules in this model
		
		this.rules = rules.immutableClone();
		
		finishInitialization();
	}
	
	
	
	// Public accessors
	
	public NSArray rules()
	{
		return this.rules;
	}
	
	
	
	// Public instance methods
	
	public NSArray candidateRulesForKey(String key)
	{
		NSArray candidateRules = (NSArray) this.candidateRulesByKey.objectForKey(key);
		
		return candidateRules;
	}
	
	
	private void finishInitialization()
	{
		// Candidate rules are sorted according to priority, from highest to
		// lowest. Within that, they are sorted by the number of qualifier keys
		// in the left-hand side. This guarantees that the rule at the front of
		// the returned array (index 0) will be the most specific rule possible.
		
		EOSortOrdering priorityOrdering = new EOSortOrdering("priority",
				EOSortOrdering.CompareDescending);
		EOSortOrdering qualifierKeysCountOrdering = new EOSortOrdering(
				"lhs.allQualifierKeys.count", EOSortOrdering.CompareDescending);
		EOSortOrdering qualifierKeyAlphabeticOrdering = new EOSortOrdering(
				"lhs.toString", EOSortOrdering.CompareDescending);
		
		NSArray candidateSort = new NSArray(new EOSortOrdering[] { priorityOrdering,
				qualifierKeysCountOrdering, qualifierKeyAlphabeticOrdering });
		
		NSArray allRules = rules();
		NSDictionary rulesByKey = DictionaryUtilities.dictionaryFromArrayWithKeyPath(allRules,
				"rhs.keyPath", true);
		NSMutableDictionary candidateRules = new NSMutableDictionary();
		Enumeration allKeys = rulesByKey.keyEnumerator();
		
		while (allKeys.hasMoreElements()) {
			String currentKey = (String) allKeys.nextElement();
			NSArray matchingRules = (NSArray) rulesByKey.objectForKey(currentKey);
			
			matchingRules = EOSortOrdering.sortedArrayUsingKeyOrderArray(matchingRules,
					candidateSort);
			
			candidateRules.setObjectForKey(matchingRules, currentKey);
		}
		
		this.candidateRulesByKey = candidateRules;
	}
	
	
	public String toString()
	{
		return rules().toString();
	}
	
	
	public boolean equals(Object object)
	{
		if ((object != null) && (getClass() == object.getClass())) {
			RuleModel other = (RuleModel) object;
			
			return (this.rules.equals(other.rules));
		}
		
		return false;
	}
	
	
	public int hashCode()
	{
		return this.rules.hashCode();
	}
	
	
	public Object clone()
	{
		RuleModel clone = new RuleModel(rules());
		
		return clone;
	}
	
	
	
	// Conformance with NSCoding
	
	public Class classForCoder()
	{
		return getClass();
	}
	
	
	public static Object decodeObject(NSCoder coder)
	{
		return new RuleModel((NSArray) coder.decodeObject());
	}
	
	
	public void encodeWithCoder(NSCoder coder)
	{
		coder.encodeObject(this.rules);
	}
	
	
	
	// Conformance with KeyValueArchiving
	
	public void encodeWithKeyValueArchiver(EOKeyValueArchiver keyValueArchiver)
	{
		keyValueArchiver.encodeObject(this.rules, RULES_KEY);
	}
	
	
	public static Object decodeWithKeyValueUnarchiver(EOKeyValueUnarchiver keyValueUnarchiver)
	{
		return new RuleModel((NSArray) keyValueUnarchiver.decodeObjectForKey(RULES_KEY));
	}
}