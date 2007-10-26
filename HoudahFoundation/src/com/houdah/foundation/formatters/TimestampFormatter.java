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

package com.houdah.foundation.formatters;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.webobjects.foundation.NSTimeZone;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSTimestampFormatter;

/**
 * Subclass of NSTimestampFormatter.<br/>
 * 
 * <ul>
 * <li>Implements non lenient parsing
 * <li>Allow only realistic dates to be parsed
 * <li>Work arounds a bug in NSTimestampFormatter where dates seem to be always
 * parsed in the GMT timezone
 * <li>Formats both NSTimestamps & dates
 * </ul>
 * 
 * <b>CAVEAT:</b> These are patches & workarounds most likely at the expense of
 * performance.
 * 
 * @author bernard
 */
public class TimestampFormatter extends NSTimestampFormatter
{
	// Private class constants
	
	private static final long	serialVersionUID	= -8825766013186015287L;
	
	
	
	// Private instance variables
	
	private DateFormatSymbols	dateFormatSymbols;
	
	
	private SimpleDateFormat	simpleDateFormat	= null;
	
	
	
	
	// Constructors
	
	public TimestampFormatter()
	{
		super();
		
		this.dateFormatSymbols = defaultDateFormatSymbols();
	}
	
	
	public TimestampFormatter(String aPattern)
	{
		super(aPattern);
		
		this.dateFormatSymbols = defaultDateFormatSymbols();
	}
	
	
	public TimestampFormatter(String aPattern, DateFormatSymbols symbols)
	{
		super(aPattern, symbols);
		
		this.dateFormatSymbols = symbols;
	}
	
	
	
	// Public instance methods
	
	/**
	 * Parses a string to produce an object. If the string does not specify a
	 * time zone, uses the default parse time zone.<br/>
	 * 
	 * Parsing is NOT lenient.
	 */
	public Object parseObject(String string, ParsePosition status)
	{
		Date date = getSimpleDateFormat().parse(string, status);
		
		if (date != null) {
			NSTimestamp timestamp = new NSTimestamp(date);
			
			return timestamp;
		} else {
			return null;
		}
	}
	
	
	
	/**
	 * Parses a string to produce an object.<br/>
	 * 
	 * Calls the two argument version with a ParsePosition initialized with a
	 * zero index.<br/>
	 * 
	 * Upon completion of the parsing verifies that there were no errors and
	 * that there were no unused characters at the end of the string to parse.
	 * 
	 * @param source
	 *            the string to parse
	 * @throws ParseException
	 *             if he specified string is invalid
	 */
	public Object parseObject(String source) throws ParseException
	{
		ParsePosition status = new ParsePosition(0);
		Object result = parseObject(source, status);
		int index = status.getIndex();
		
		if (index == 0) {
			throw new ParseException("Format.parseObject(String) failed", status.getErrorIndex());
		} else if ((source != null)
				&& (source.substring(index, source.length()).trim().length() > 0)) {
			throw new ParseException("Format.parseObject(String) failed", status.getErrorIndex());
		}
		
		return result;
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer,
	 *      java.text.FieldPosition)
	 */
	public StringBuffer format(Object date, StringBuffer stringBuffer, FieldPosition fieldPosition)
	{
		if (!(date instanceof NSTimestamp) && (date instanceof Date)) {
			return super.format(new NSTimestamp((Date) date), stringBuffer, fieldPosition);
		} else {
			return super.format(date, stringBuffer, fieldPosition);
		}
	}
	
	
	public void setDefaultFormatTimeZone(NSTimeZone zone)
	{
		throw new UnsupportedOperationException("Cannot change time zone");
	}
	
	
	public void setPattern(String pattern)
	{
		this.simpleDateFormat = null;
		
		super.setPattern(pattern);
	}
	
	
	
	// Private instance methods
	
	/**
	 * Lazily creates a SimpleDateFormat object used for parsing.
	 */
	private SimpleDateFormat getSimpleDateFormat()
	{
		if (this.simpleDateFormat == null) {
			if (pattern() != null) {
				this.simpleDateFormat = new SimpleDateFormat(pattern(), dateFormatSymbols);
			} else {
				this.simpleDateFormat = (SimpleDateFormat) DateFormat.getDateTimeInstance(
						DateFormat.SHORT, DateFormat.SHORT, Locale.US);
			}
			
			this.simpleDateFormat.setLenient(false);
		}
		
		return this.simpleDateFormat;
	}
}