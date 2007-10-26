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

package com.houdah.foundation;

import java.util.Locale;
import java.util.TimeZone;

import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSTimeZone;

/**
 * Principal class of the HoudahFoundation project. Sets up various classes in the
 * project.
 * 
 * @author bernard
 */
public class PrincipalClass
{
	// Static initializer
	
	static {
		// Be polite, say 'Hello'
		NSLog.debug.appendln("Initializing HoudahFoundation");
		
		
		// Redirect System.err and System.out
		System.setOut(((NSLog.PrintStreamLogger) NSLog.out).printStream());
		System.setErr(((NSLog.PrintStreamLogger) NSLog.err).printStream());
		
		
		// Print out the java version
		System.out.println("Using Java version "
				+ System.getProperty("java.version") + " ("
				+ System.getProperty("java.vendor") + ")");
		
		
		// Configure debugging
		NSLog.debug.setAllowedDebugLevel(NSLog.DebugLevelDetailed);
		NSLog.allowDebugLoggingForGroups(NSLog.DebugGroupMultithreading);
		
		
		// Set locale: formatting, sorting, ...
		Locale.setDefault(new Locale("fr", "US", ""));
		
		
		// Now this is tricky. We have 2 incompatible time zone implementations
		// We need to force Java into using a JDK 1.3.1 compatible
		// implementation
		TimeZone.setDefault(TimeZone.getTimeZone("etc/GMT-1"));
		NSTimeZone.setDefault(TimeZone.getTimeZone("etc/GMT-1"));
	}
}