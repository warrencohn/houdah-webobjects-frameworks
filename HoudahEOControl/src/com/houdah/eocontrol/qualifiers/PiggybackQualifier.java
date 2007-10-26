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

package com.houdah.eocontrol.qualifiers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.webobjects.eocontrol.EOClassDescription;
import com.webobjects.eocontrol.EOKeyValueArchiver;
import com.webobjects.eocontrol.EOKeyValueArchiving;
import com.webobjects.eocontrol.EOKeyValueUnarchiver;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSCoder;
import com.webobjects.foundation.NSCoding;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSMutableSet;

/**
 * Allows for attaching information to a qualifier by means of a userInfo
 * dictionary.<br/>
 * 
 * Serves for piggybacking values on a qualifier or for tagging it so it can be
 * easily retrieved in a qualifier tree. This might come in handy were all two
 * pieces of code share is a qualifier and yet they need to pass each other some
 * parameters.<br/>
 * 
 * Yes, I know, this is hackish.
 * 
 * @author bernard
 */
public class PiggybackQualifier extends Qualifier implements Serializable,
		NSCoding, EOKeyValueArchiving
{
	// Private class constants
	
	private static final long		serialVersionUID	= 8570128031395191858L;
	
	
	
	// Protected instance variables
	
	protected NSMutableDictionary	userInfo			= null;
	
	
	protected EOQualifier			qualifier;
	
	
	
	
	// Constructor
	
	public PiggybackQualifier(EOQualifier qualifier)
	{
		this.qualifier = qualifier;
	}
	
	
	
	// Public instance methods
	
	public NSMutableDictionary userInfo()
	{
		// if (this.userInfo == null) {
		synchronized (this) {
			if (this.userInfo == null) {
				this.userInfo = new NSMutableDictionary();
			}
		}
		// }
		
		return this.userInfo;
	}
	
	
	public EOQualifier qualifier()
	{
		return this.qualifier;
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eocontrol.EOQualifier#qualifierWithBindings(com.webobjects.foundation.NSDictionary,
	 *      boolean)
	 */
	public EOQualifier qualifierWithBindings(NSDictionary bindings,
			boolean requireAll)
	{
		EOQualifier boundQualifier = qualifier().qualifierWithBindings(
				bindings, requireAll);
		
		if (boundQualifier != null) {
			if (boundQualifier == qualifier()) {
				return this;
			} else {
				PiggybackQualifier pbQualifier = new PiggybackQualifier(
						boundQualifier);
				
				pbQualifier.userInfo().addEntriesFromDictionary(userInfo());
				
				return pbQualifier;
			}
		} else {
			return null;
		}
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eocontrol.EOQualifier#validateKeysWithRootClassDescription(com.webobjects.eocontrol.EOClassDescription)
	 */
	public void validateKeysWithRootClassDescription(
			EOClassDescription classDescription)
	{
		qualifier().validateKeysWithRootClassDescription(classDescription);
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eocontrol.EOQualifier#addQualifierKeysToSet(com.webobjects.foundation.NSMutableSet)
	 */
	public void addQualifierKeysToSet(NSMutableSet keySet)
	{
		qualifier().addQualifierKeysToSet(keySet);
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eocontrol.EOQualifierEvaluation#evaluateWithObject(java.lang.Object)
	 */
	public boolean evaluateWithObject(Object object)
	{
		return qualifier().evaluateWithObject(object);
	}
	
	
	public boolean equals(Object obj)
	{
		if (obj instanceof PiggybackQualifier) {
			return qualifier().equals(((PiggybackQualifier) obj).qualifier());
		} else {
			return false;
		}
	}
	
	
	public int hashCode()
	{
		return this.qualifier.hashCode();
	}
	
	
	public String toString()
	{
		return "(" + qualifier().toString() + ")";
	}
	
	
	public Class classForCoder()
	{
		return getClass();
	}
	
	
	public static Object decodeObject(NSCoder coder)
	{
		PiggybackQualifier pbQualifier = new PiggybackQualifier(
				(EOQualifier) coder.decodeObject());
		
		pbQualifier.userInfo = (NSMutableDictionary) coder.decodeObject();
		
		return pbQualifier;
	}
	
	
	public void encodeWithCoder(NSCoder nscoder)
	{
		nscoder.encodeObject(qualifier());
		nscoder.encodeObject(userInfo());
	}
	
	
	public void encodeWithKeyValueArchiver(EOKeyValueArchiver keyValueArchiver)
	{
		keyValueArchiver.encodeObject(qualifier(), "qualifier");
		keyValueArchiver.encodeObject(userInfo(), "userInfo");
	}
	
	
	public static Object decodeWithKeyValueUnarchiver(
			EOKeyValueUnarchiver keyvalueUnarchiver)
	{
		PiggybackQualifier pbQualifier = new PiggybackQualifier(
				(EOQualifier) keyvalueUnarchiver
						.decodeObjectForKey("qualifier"));
		
		pbQualifier.userInfo = (NSMutableDictionary) keyvalueUnarchiver
				.decodeObjectForKey("qualifier");
		
		return pbQualifier;
	}
	
	
	private void writeObject(ObjectOutputStream objectOutputStream)
			throws IOException
	{
		ObjectOutputStream.PutField putfield = objectOutputStream.putFields();
		
		putfield.put("qualifier", qualifier());
		putfield.put("userInfo", userInfo());
		objectOutputStream.writeFields();
	}
	
	
	private void readObject(ObjectInputStream objectInputStream)
			throws IOException, ClassNotFoundException
	{
		ObjectInputStream.GetField getfield = null;
		
		getfield = objectInputStream.readFields();
		this.qualifier = (EOQualifier) getfield.get("qualifier", null);
		this.userInfo = (NSMutableDictionary) getfield.get("userInfo", null);
	}
}