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

import com.houdah.foundation.KVCObject;
import com.houdah.foundation.KVCUtility;

import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSMutableSet;

public class RuleContext extends KVCObject
{
	// Private instance variables
	
	private RuleModel			model;
	
	
	private RuleContext			parentContext;
	
	
	private NSMutableDictionary	storedValues;
	
	
	private NSMutableDictionary	cachedValues;
	
	
	private NSMutableDictionary	keysByDependancies;
	
	
	private NSMutableSet		keysWithGlobalDependancy;
	
	
	
	
	// Constructors
	
	/**
	 * Constructor.
	 * 
	 */
	public RuleContext(RuleContext parentContext)
	{
		this(null, parentContext);
		assert (parentContext != null);
	}
	
	
	
	/**
	 * Constructor.
	 * 
	 */
	public RuleContext(RuleModel model)
	{
		this(model, null);
		assert (model != null);
	}
	
	
	
	/**
	 * Designated constructor.
	 * 
	 */
	private RuleContext(RuleModel model, RuleContext parentContext)
	{
		this.model = model;
		this.parentContext = parentContext;
		this.storedValues = new NSMutableDictionary();
		this.cachedValues = new NSMutableDictionary();
		this.keysByDependancies = new NSMutableDictionary();
		this.keysWithGlobalDependancy = new NSMutableSet();
	}
	
	
	
	// Public accessors
	
	public RuleModel model()
	{
		if (this.model != null) {
			return this.model;
		}
		
		return this.parentContext.model();
	}
	
	
	
	// Public instance methods
	
	public Object valueForKey(String key)
	{
		Object value = storedValueForKey(key);
		
		if (value == null) {
			value = inferredValueForKey(key);
		}
		
		return value;
	}
	
	
	public void takeValueForKey(Object value, String key)
	{
		clearGlobalDependancies();
		clearDirectDependancies(key);
		
		takeStoredValueForKey(value, key);
	}
	
	
	public void takeValuesFromDictionary(NSDictionary dictionary)
	{
		clearGlobalDependancies();
		
		if (dictionary != null) {
			Enumeration keyEnumeration = dictionary.keyEnumerator();
			
			while (keyEnumeration.hasMoreElements()) {
				String key = (String) keyEnumeration.nextElement();
				Object value = dictionary.objectForKey(key);
				
				clearDirectDependancies(key);
				
				takeStoredValueForKey(value, key);
			}
		}
	}
	
	
	public NSArray valuesForKeyWhileTakingSuccessiveValuesForKey(String key, NSArray values,
			String valueKey)
	{
		int vCount = values.count();
		NSMutableArray results = new NSMutableArray(vCount);
		
		for (int v = 0; v < vCount; v++) {
			Object value = values.objectAtIndex(v);
			
			takeValueForKey(value, valueKey);
			
			Object result = valueForKey(key);
			
			if (result == null) {
				result = NSKeyValueCoding.NullValue;
			}
			
			results.addObject(result);
		}
		
		
		// Clean up context
		takeValueForKey(null, valueKey);
		
		return results;
	}
	
	
	public NSArray allPossibleValuesForKey(String key)
	{
		return allPossibleValuesForKey(key, false);
	}
	
	
	public NSArray allPossibleValuesUniquedByPriorityForKey(String key)
	{
		return allPossibleValuesForKey(key, true);
	}
	
	
	
	// Protected instance methods
	
	protected RuleContext parentContext()
	{
		return this.parentContext;
	}
	
	
	protected Object storedValueForKey(String key)
	{
		Object value = this.storedValues.objectForKey(key);
		
		if ((value == null) && (this.parentContext != null)) {
			return this.parentContext.storedValueForKey(key);
		}
		
		return value;
	}
	
	
	protected void takeStoredValueForKey(Object value, String key)
	{
		if (value != null) {
			this.storedValues.setObjectForKey(value, key);
		} else {
			this.storedValues.removeObjectForKey(key);
		}
	}
	
	
	protected Object inferredValueForKey(String key)
	{
		Object value = this.cachedValues.objectForKey(key);
		
		if (value != null) {
			return (value != NSKeyValueCoding.NullValue) ? value : null;
		} else {
			NSArray candidates = model().candidateRulesForKey(key);
			
			if (candidates != null) {
				Enumeration candidatesEnumerator = candidates.objectEnumerator();
				NSMutableSet significantKeys = new NSMutableSet();
				KVCUtility kvcUtility = KVCUtility.sharedInstance();
				
				
				// Iterate through all of the candidate rules, evaluating their
				// left-hand side in this context. If one returns YES, fire
				// it and return the value of firing it. If none returns YES,
				// we just wind up returning null.
				while (candidatesEnumerator.hasMoreElements()) {
					Rule candidateRule = (Rule) candidatesEnumerator.nextElement();
					EOQualifier lhs = candidateRule.lhs();
					
					if (lhs != null) {
						significantKeys.unionSet(lhs.allQualifierKeys());
					}
					
					if ((lhs == null) || lhs.evaluateWithObject(this)) {
						value = candidateRule.fireInContext(this);
						
						if (significantKeys.count() > 0) {
							Enumeration significantKeyEnumeration = significantKeys
									.objectEnumerator();
							
							while (significantKeyEnumeration.hasMoreElements()) {
								String significantKey = kvcUtility
										.firstPathComponent((String) significantKeyEnumeration
												.nextElement());
								NSMutableArray dependantKeys = (NSMutableArray) this.keysByDependancies
										.objectForKey(significantKey);
								
								if (dependantKeys == null) {
									this.keysByDependancies.setObjectForKey(
											dependantKeys = new NSMutableArray(), significantKey);
								}
								
								dependantKeys.addObject(key);
							}
						} else {
							this.keysWithGlobalDependancy.addObject(key);
						}
						
						this.cachedValues.setObjectForKey((value != null) ? value
								: NSKeyValueCoding.NullValue, key);
						
						clearGlobalDependancies();
						clearDirectDependancies(key);
						
						return value;
					}
				}
			}
			
			return null;
		}
	}
	
	
	protected NSArray allPossibleValuesForKey(String key, boolean uniqued)
	{
		NSArray candidates = model().candidateRulesForKey(key);
		
		if (candidates != null) {
			NSMutableArray values = new NSMutableArray();
			Enumeration candidatesEnumerator = candidates.objectEnumerator();
			int lastMatchedPriority = Integer.MAX_VALUE;
			
			while (candidatesEnumerator.hasMoreElements()) {
				Rule candidateRule = (Rule) candidatesEnumerator.nextElement();
				EOQualifier lhs = candidateRule.lhs();
				int priority = candidateRule.priority();
				
				if (uniqued && (priority == lastMatchedPriority)) {
					continue;
				}
				
				if ((lhs == null) || lhs.evaluateWithObject(this)) {
					Object value = candidateRule.fireInContext(this);
					
					values.addObject(value);
					
					lastMatchedPriority = priority;
				}
			}
			
			return values;
		}
		
		return NSArray.EmptyArray;
	}
	
	
	
	// Private instance methods
	
	private void clearGlobalDependancies()
	{
		Enumeration globalKeys = this.keysWithGlobalDependancy.objectEnumerator();
		
		while (globalKeys.hasMoreElements()) {
			String dependantKey = (String) globalKeys.nextElement();
			
			this.cachedValues.removeObjectForKey(dependantKey);
		}
		
		this.keysWithGlobalDependancy.removeAllObjects();
	}
	
	
	private void clearDirectDependancies(String key)
	{
		NSArray dependantKeys = (NSArray) this.keysByDependancies.objectForKey(key);
		
		if (dependantKeys != null) {
			Enumeration dependantKeyEnumeration = dependantKeys.objectEnumerator();
			
			while (dependantKeyEnumeration.hasMoreElements()) {
				String dependantKey = (String) dependantKeyEnumeration.nextElement();
				
				this.cachedValues.removeObjectForKey(dependantKey);
			}
			
			this.keysByDependancies.removeObjectForKey(key);
		}
		
		this.cachedValues.removeObjectForKey(key);
	}
}