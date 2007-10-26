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

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Enumeration;

import com.houdah.eocontrol.qualifiers.BestMatchQualifier;
import com.houdah.eocontrol.qualifiers.BestRelationshipMatchesQualifier;
import com.houdah.eocontrol.qualifiers.ExistsInRelationshipQualifier;
import com.houdah.eocontrol.qualifiers.InSubqueryQualifier;
import com.houdah.foundation.utilities.StringUtilities;

import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOKeyComparisonQualifier;
import com.webobjects.eocontrol.EOKeyValueArchiver;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOKeyValueUnarchiver;
import com.webobjects.eocontrol.EONotQualifier;
import com.webobjects.eocontrol.EOOrQualifier;
import com.webobjects.eocontrol.EOQualifierVariable;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSBundle;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSPropertyListSerialization;
import com.webobjects.foundation.NSSet;

public class RuleModelUtilities
{
	// Public class constants
	
	/**
	 * Extension of the model files.
	 */
	public static String		MODEL_EXT			= ".d2wmodel";
	
	
	
	// Private class variables
	
	private static NSDictionary	compressionLookup	= null;
	
	
	private static NSDictionary	expansionLookup		= null;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 */
	private RuleModelUtilities()
	{
		throw new IllegalStateException("Do not instantiate this utility class");
	}
	
	
	
	// Public class methods
	
	/**
	 * Loads model files from all bundles in the classpath.<br/>
	 * 
	 * Caveat: For compatibility with Apple's RuleEditor, this method
	 * automatically adds the ".d2wmodel" extension to all names (with or
	 * without extension that you pass into this method. That is we use "double
	 * extensions": the one specified as an argument plus the ".d2wmodel" one.
	 * To the caller this is mostly transparent. Remember not to pass the
	 * extension or names including the extension ".d2wmodel" as arguments.
	 * Remember to name your files including the ".d2wmodel" extension.
	 * 
	 * @param extension
	 *            file name extension to look for
	 * @param includeNames
	 *            explicit list of file names (w/o extension) to accept, accepts
	 *            all if null.
	 * @param excludeNames
	 *            explicit list of file names (w/o extension) to refuse, accepts
	 *            all if null
	 * @param includesFiles
	 *            explicit list of file names (with extension) to accept in
	 *            addition to otherwise accepted files
	 */
	public static RuleModel loadFromBundles(String extension, NSSet includeNames,
			NSSet excludeNames, NSSet includesFiles)
	{
		NSMutableArray rules = new NSMutableArray();
		NSArray frameworkBundles = NSBundle.frameworkBundles();
		NSArray allBundles = frameworkBundles.arrayByAddingObject(NSBundle.mainBundle());
		Enumeration bundleEnumeration = allBundles.objectEnumerator();
		String fullExtension = extension + MODEL_EXT;
		int extLength = fullExtension.length();
		
		while (bundleEnumeration.hasMoreElements()) {
			NSBundle bundle = (NSBundle) bundleEnumeration.nextElement();
			NSArray ruleFilePaths = bundle.resourcePathsForLocalizedResources(fullExtension, null);
			Enumeration ruleFilePathEnumeration = ruleFilePaths.objectEnumerator();
			
			while (ruleFilePathEnumeration.hasMoreElements()) {
				String resourcePath = (String) ruleFilePathEnumeration.nextElement();
				
				if ((includeNames != null) || (excludeNames != null)) {
					int separatorIndex = resourcePath.lastIndexOf('.', resourcePath.length()
							- MODEL_EXT.length() - 1);
					
					if (separatorIndex < 0) {
						separatorIndex = 0;
					}
					
					int lastIndex = resourcePath.length() - extLength - 1;
					String resourceName = resourcePath.substring(separatorIndex + 1, lastIndex);
					
					if (includeNames != null) {
						if (!includeNames.containsObject(resourceName)) {
							continue;
						}
					}
					
					if (excludeNames != null) {
						if (excludeNames.containsObject(resourceName)) {
							continue;
						}
					}
				}
				
				
				// System.err.println("** bundle: " + bundle.name());
				// System.err.println("** resourcePath: " + resourcePath);
				
				InputStream inputStream = bundle.inputStreamForResourcePath(resourcePath);
				String string = StringUtilities.stringFromInputStream(inputStream);
				
				rules.addObjectsFromArray(RuleModelUtilities.decode(string));
			}
			
			if (includesFiles != null) {
				Enumeration includeFileEnumeration = includesFiles.objectEnumerator();
				
				while (includeFileEnumeration.hasMoreElements()) {
					String includeFile = (String) includeFileEnumeration.nextElement();
					
					if (includeFile.indexOf('.') > -1) {
						String includeFilePath = bundle.resourcePathForLocalizedResourceNamed(
								includeFile + MODEL_EXT, null);
						
						if (includeFilePath != null) {
							InputStream inputStream = bundle
									.inputStreamForResourcePath(includeFilePath);
							String string = StringUtilities.stringFromInputStream(inputStream);
							
							rules.addObjectsFromArray(RuleModelUtilities.decode(string));
						}
					}
				}
			}
		}
		
		return new RuleModel(rules);
	}
	
	
	public static RuleModel loadFromFile(File file)
	{
		String contents = StringUtilities.stringFromFile(file);
		
		return new RuleModel(RuleModelUtilities.decode(contents));
	}
	
	
	
	// Protected class methods
	
	protected static NSArray decode(String string)
	{
		if (string != null) {
			NSDictionary dictionary = NSPropertyListSerialization.dictionaryForString(string);
			EOKeyValueUnarchiver unarchiver = new EOKeyValueUnarchiver(transformDictionary(
					dictionary, RuleModelUtilities.expansionLookup()));
			NSArray rules = (NSArray) unarchiver.decodeObjectForKey(RuleModel.RULES_KEY);
			
			unarchiver.finishInitializationOfObjects();
			
			return rules;
		} else {
			return null;
		}
	}
	
	
	protected static String encode(NSArray rules)
	{
		if (rules != null) {
			EOKeyValueArchiver archiver = new EOKeyValueArchiver();
			
			archiver.encodeObject(rules, RuleModel.RULES_KEY);
			
			return NSPropertyListSerialization.stringFromPropertyList(transformDictionary(archiver
					.dictionary(), RuleModelUtilities.compressionLookup()));
		} else {
			return null;
		}
	}
	
	
	protected static NSDictionary transformDictionary(NSDictionary dictionary, NSDictionary lookup)
	{
		NSMutableDictionary transformedDictionary = new NSMutableDictionary(dictionary.count());
		Enumeration keys = dictionary.keyEnumerator();
		
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = dictionary.objectForKey(key);
			
			transformedDictionary.setObjectForKey(RuleModelUtilities.transform(value, lookup), key);
		}
		
		return transformedDictionary;
	}
	
	
	protected static NSArray transformArray(NSArray array, NSDictionary lookup)
	{
		NSMutableArray transformedArray = new NSMutableArray(array.count());
		Enumeration objects = array.objectEnumerator();
		
		while (objects.hasMoreElements()) {
			Object object = objects.nextElement();
			
			transformedArray.addObject(RuleModelUtilities.transform(object, lookup));
		}
		
		return transformedArray;
	}
	
	
	protected static String transformString(String string, NSDictionary lookup)
	{
		Object replacement = lookup.objectForKey(string);
		
		return (replacement != null) ? (String) replacement : string;
	}
	
	
	protected static Object transform(Object object, NSDictionary lookup)
	{
		if (object instanceof NSDictionary) {
			return RuleModelUtilities.transformDictionary((NSDictionary) object, lookup);
		} else if (object instanceof NSArray) {
			return RuleModelUtilities.transformArray((NSArray) object, lookup);
		} else if (object instanceof String) {
			return RuleModelUtilities.transformString((String) object, lookup);
		} else {
			return object;
		}
	}
	
	
	protected static NSDictionary compressionLookup()
	{
		if (RuleModelUtilities.compressionLookup == null) {
			NSMutableDictionary lookup = new NSMutableDictionary();
			
			lookup.setObjectForKey("EOAndQualifier", EOAndQualifier.class.getName());
			lookup.setObjectForKey("EOKeyComparisonQualifier", EOKeyComparisonQualifier.class
					.getName());
			lookup.setObjectForKey("EOKeyValueQualifier", EOKeyValueQualifier.class.getName());
			lookup.setObjectForKey("EONotQualifier", EONotQualifier.class.getName());
			lookup.setObjectForKey("EOOrQualifier", EOOrQualifier.class.getName());
			lookup.setObjectForKey("EOQualifierVariable", EOQualifierVariable.class.getName());
			lookup.setObjectForKey("BestMatchQualifier", BestMatchQualifier.class.getName());
			lookup.setObjectForKey("BestRelationshipMatchesQualifier",
					BestRelationshipMatchesQualifier.class.getName());
			lookup.setObjectForKey("ExistsInRelationshipQualifier",
					ExistsInRelationshipQualifier.class.getName());
			lookup.setObjectForKey("InSubqueryQualifier", InSubqueryQualifier.class.getName());
			
			lookup.setObjectForKey("BooleanAssignment", BooleanAssignment.class.getName());
			lookup.setObjectForKey("KeyValueAssignment", KeyValueAssignment.class.getName());
			lookup.setObjectForKey("ObjectValueAssignment", ObjectValueAssignment.class.getName());
			lookup
					.setObjectForKey("PropertyListAssignment", PropertyListAssignment.class
							.getName());
			lookup.setObjectForKey("Rule", Rule.class.getName());
			lookup.setObjectForKey("SelfAssignment", SelfAssignment.class.getName());
			lookup.setObjectForKey("SimpleAssignment", SimpleAssignment.class.getName());
			
			RuleModelUtilities.compressionLookup = lookup;
		}
		
		return RuleModelUtilities.compressionLookup;
	}
	
	
	protected static NSDictionary expansionLookup()
	{
		if (RuleModelUtilities.expansionLookup == null) {
			NSDictionary compressionLookup = RuleModelUtilities.compressionLookup();
			Enumeration keys = compressionLookup.keyEnumerator();
			NSMutableDictionary lookup = new NSMutableDictionary(compressionLookup.count());
			
			while (keys.hasMoreElements()) {
				Object key = keys.nextElement();
				Object value = compressionLookup.objectForKey(key);
				
				lookup.setObjectForKey(key, value);
			}
			
			
			// Direct To Web rule editor compatibility
			lookup.setObjectForKey(BooleanAssignment.class.getName(),
					"com.webobjects.directtoweb.BooleanAssignment");
			lookup.setObjectForKey(Rule.class.getName(), "com.webobjects.directtoweb.Rule");
			lookup.setObjectForKey(SimpleAssignment.class.getName(),
					"com.webobjects.directtoweb.Assignment");
			lookup.setObjectForKey(KeyValueAssignment.class.getName(),
					"com.webobjects.directtoweb.KeyValueAssignment");
			
			RuleModelUtilities.expansionLookup = lookup;
		}
		
		return RuleModelUtilities.expansionLookup;
	}
	
	
	
	
	// Protected inner classes
	
	/**
	 * A filter for listing only D2W model files in a directory.
	 */
	protected static class D2WModelFilter implements FilenameFilter
	{
		public boolean accept(File dir, String name)
		{
			return ((name != null) && (name.endsWith(MODEL_EXT)) && (new File(dir, name).isFile()));
		}
	}
}