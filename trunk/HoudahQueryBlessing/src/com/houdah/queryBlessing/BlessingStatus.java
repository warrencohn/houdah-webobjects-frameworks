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

package com.houdah.queryBlessing;

/**
 * Status class used for propagating state while verifying compliance of a
 * qualifier with a Condition.<br/>
 */
public class BlessingStatus implements Cloneable
{
	// Private instance variables
	
	/**
	 * Flag determining if the current qualifier branch is negated, i.e contains
	 * an EONotQualifier at a higher level.
	 */
	private boolean	negate;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor.
	 */
	public BlessingStatus()
	{
		this.negate = false;
	}
	
	
	
	// Public instance methods
	
	/**
	 * Gets the flag determining if the current qualifier branch is negated, i.e
	 * contains an EONotQualifier at a higher level.
	 * 
	 * @return the negate flag
	 */
	public boolean negate()
	{
		return negate;
	}
	
	
	
	/**
	 * Sets the flag determining if the current qualifier branch is negated, i.e
	 * contains an EONotQualifier at a higher level.
	 * 
	 * @param the
	 *            new negate flag
	 */
	public void setNegate(boolean negate)
	{
		this.negate = negate;
	}
	
	
	
	/**
	 * Clones the current status for use in a new branch of the qualifier.
	 * 
	 * @return a copy of this status object
	 */
	public BlessingStatus newStatus()
	{
		try {
			return (BlessingStatus) clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Wow, I did not expect that one!");
		}
	}
	
	
	
	/**
	 * For debugging purposes
	 * 
	 * @return a human readable represenation of the status object
	 */
	public String toString()
	{
		return "Status <negate=" + negate + ">";
	}
}