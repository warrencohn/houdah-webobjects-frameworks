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

package com.houdah.foundation.utilities;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.webobjects.foundation.NSTimeZone;
import com.webobjects.foundation.NSTimestamp;

/**
 * A repository of utility methods that apply to NSTimestamp or other date
 * objects.
 * 
 */
public class TimestampUtilities
{
	// Public class constants
	
	/**
	 * Possible second argument for the truncate method
	 * 
	 * @see #truncate(Date, int)
	 */
	public static final int	TRUNC_START_OF_DAY	= 0, TRUNC_START_OF_MONTH = 1,
			TRUNC_START_OF_QUARTER = 2, TRUNC_START_OF_YEAR = 3;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 */
	private TimestampUtilities()
	{
		throw new IllegalStateException("Do not instantiate this utility class");
	}
	
	
	
	// Public class methods
	
	/**
	 * Adds time to a date.<br/>
	 * 
	 * This utility method replaces
	 * NSTimestamp.timestampByAddingGregorianUnits() which does all its
	 * computations in the GMT timezone. This leads to several problems. For one
	 * DST changes don't happen as expected. Adding 1 month to a CET midnight of
	 * a first of a month does not bring us to the first of the next month, but
	 * only as many days further as there are days in the preceeding month.
	 * 
	 * @param date
	 *            the original timestamp or date
	 * @param year
	 *            number of years to add
	 * @param month
	 *            number of months to add
	 * @param day
	 *            number of days to add
	 * @param hour
	 *            number of hours to add
	 * @param minute
	 *            number of minutes to add
	 * @param second
	 *            number of seconds to add
	 * 
	 * @return a new date corresponding to original date to which we add
	 *         specified time.
	 */
	public static NSTimestamp timestampByAddingGregorianUnits(Date date, int year, int month,
			int day, int hour, int minute, int second)
	{
		GregorianCalendar calendar = new GregorianCalendar();
		
		calendar.setTime(date);
		
		return new NSTimestamp(calendar.get(GregorianCalendar.YEAR) + year, calendar
				.get(GregorianCalendar.MONTH)
				+ month + 1, calendar.get(GregorianCalendar.DAY_OF_MONTH) + day, calendar
				.get(GregorianCalendar.HOUR_OF_DAY)
				+ hour, calendar.get(GregorianCalendar.MINUTE) + minute, calendar
				.get(GregorianCalendar.SECOND)
				+ second, TimeZone.getDefault());
	}
	
	
	
	/**
	 * Truncate a date according to the given truncation type.<br/>
	 * 
	 * @param date
	 *            given date
	 * @param truncationType
	 *            truncation type (see class constants)
	 * 
	 * @return the truncated date.
	 */
	public static NSTimestamp truncate(Date date, int truncationType)
	{
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		
		int year = calendar.get(GregorianCalendar.YEAR);
		int month = calendar.get(GregorianCalendar.MONTH) + 1;
		int day = calendar.get(GregorianCalendar.DAY_OF_MONTH);
		int hour = calendar.get(GregorianCalendar.HOUR_OF_DAY);
		int minute = calendar.get(GregorianCalendar.MINUTE);
		int second = calendar.get(GregorianCalendar.SECOND);
		
		if (truncationType == TRUNC_START_OF_DAY) {
			hour = 0;
			minute = 0;
			second = 0;
		} else if (truncationType == TRUNC_START_OF_MONTH) {
			day = 1;
			hour = 0;
			minute = 0;
			second = 0;
		} else if (truncationType == TRUNC_START_OF_QUARTER) {
			month = (month + 2) / 3;
			day = 1;
			hour = 0;
			minute = 0;
			second = 0;
		} else if (truncationType == TRUNC_START_OF_YEAR) {
			month = 1;
			day = 1;
			hour = 0;
			minute = 0;
			second = 0;
		} else {
			throw new IllegalArgumentException("Unknown truncation type. Refer to class constants.");
		}
		
		return new NSTimestamp(year, month, day, hour, minute, second, NSTimeZone.getDefault());
	}
	
	
	
	/**
	 * Start of day for today.
	 * 
	 * @return the date used by convention to represent today's day
	 */
	public static NSTimestamp today()
	{
		return truncate(new NSTimestamp(), TRUNC_START_OF_DAY);
	}
}