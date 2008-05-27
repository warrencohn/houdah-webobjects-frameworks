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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.xerces.impl.xpath.regex.Match;
import org.apache.xerces.impl.xpath.regex.RegularExpression;

import com.webobjects.foundation.NSKeyValueCoding;

/**
 * Utility class for manipulating String objects
 * 
 * @author bernard
 */
public class StringUtilities
{
	// Protected class constants
	
	protected static final String	ELLIPSIS			= "...";
	
	
	protected static final String	INCLUSIVE			= ".?!,";
	
	
	protected static final String	EXCLUSIVE			= ";:- \n\t\r";
	
	
	
	// Private class constants
	
	private static final int		ASCII_0				= 48;
	
	
	private static final int		ASCII_9				= 57;
	
	
	private static final int		ASCII_A				= 65;
	
	
	private static final int		ASCII_Z				= 90;
	
	
	private static final int		ASCII_a				= 97;
	
	
	private static final int		ASCII_z				= 122;
	
	
	private static final int		ASCII_DOT			= 46;
	
	
	private static final int		ASCII_HYPHEN		= 45;
	
	
	private static final int		ASCII_UNDERSCORE	= 95;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 */
	private StringUtilities()
	{
		throw new IllegalStateException("Do not instantiate this utility class");
	}
	
	
	
	// Public class methods
	
	/**
	 * Creates a string from a file.<br/>
	 * 
	 * @param file
	 *            a pointer to the input file
	 * @return the string created from the file, null in the event of an error
	 */
	public static String stringFromFile(File file)
	{
		if (file == null) {
			return null;
		}
		
		try {
			FileInputStream stream = new FileInputStream(file);
			
			return stringFromInputStream(stream);
		} catch (IOException ioe) {
			return null;
		}
	}
	
	
	
	/**
	 * Creates a string by reading from an InputStream.<br/>
	 * 
	 * @param stream
	 *            the stream to read from
	 * @return the string read from the stream, null in the event of an error
	 */
	public static String stringFromInputStream(InputStream stream)
	{
		try {
			byte[] data = new byte[stream.available()];
			
			stream.read(data);
			stream.close();
			
			return new String(data);
		} catch (IOException ioe) {
			return null;
		}
	}
	
	
	
	/**
	 * Truncates a string to have at maximum maxLength characters.<br/>
	 * 
	 * Where truncation is necessary an ellipsis is appended to the truncated
	 * string. The resulting string, ellipsis included, will be maxLength
	 * characters long.
	 * 
	 * @param string
	 *            the string to truncate
	 * @param maxLength
	 *            the maximum acceptable length
	 * @return a truncated copy of the original string
	 */
	public static String truncateString(String string, int maxLength)
	{
		if ((string != null) && (string.length() > maxLength)) {
			return string.substring(0, maxLength - ELLIPSIS.length() + 1) + ELLIPSIS;
		} else {
			return string;
		}
	}
	
	
	
	/**
	 * Attempts an 'intelligent' truncation of the message to a maximum length.
	 * 
	 * Where truncation is necessary an ellipsis is appended to the truncated
	 * string. The resulting string, ellipsis included, will not exceed
	 * maxLength characters.
	 * 
	 * @param string
	 *            the String to truncate
	 * @param maxLength
	 *            the maximum number of characters in the String, negative if no
	 *            limit is set
	 * @return a truncated version of the input String
	 */
	public static String smartTruncateString(String string, int maxLength)
	{
		int cutPoint;
		
		if ((string != null)
				&& ((cutPoint = cutPoint(string, 0, maxLength, ELLIPSIS.length())) > 0)) {
			return string.substring(0, cutPoint) + ELLIPSIS;
		} else {
			return string;
		}
	}
	
	
	
	/**
	 * Attempts an 'intelligent' formatting of the string.
	 * 
	 * The string is formatted by inserting new lines so that no line in the
	 * string exceeds maxLineLength characters in length. This method tries to
	 * be smart in it's choice where to suggest splitting the string in that it
	 * favors cutting off at a natural separation and tries whenever possible to
	 * avoid cutting word.
	 * 
	 * @param string
	 *            the String to format
	 * @param maxLineLength
	 *            the maximum number of characters per line in the string
	 * @return a formatted version of the input String
	 */
	public static String smartFormatString(String string, int maxLineLength, String newLine)
	{
		if (string == null) {
			return null;
		} else {
			int maxIndex;
			int length = string.length();
			StringBuffer buffer = new StringBuffer(length);
			int currentIndex = 0;
			
			while ((maxIndex = currentIndex + maxLineLength) < length) {
				int cutPoint = -1;
				boolean skipOne = false;
				
				
				// First look for an existing new line marker
				for (int i = currentIndex; i < maxIndex; i++) {
					if (string.charAt(i) == '\n') {
						cutPoint = i;
						skipOne = true;
						break;
					}
				}
				
				
				// If none is found, then look for another occasion to split the
				// string
				if (cutPoint == -1) {
					cutPoint = cutPoint(string, currentIndex, maxLineLength, 0);
				}
				
				
				// If there is no need to cut, take the whole thing
				if (cutPoint == -1) {
					cutPoint = maxIndex;
				}
				
				
				// Cut where needed
				buffer.append(string.substring(currentIndex, cutPoint));
				buffer.append(newLine);
				
				
				// Update the currentIndex for the next iteration
				currentIndex = (skipOne ? cutPoint + 1 : cutPoint);
			}
			
			
			// Check for new lines in the remainder of the string
			while ((maxIndex = string.indexOf('\n', currentIndex)) > 0) {
				buffer.append(string.substring(currentIndex, maxIndex));
				buffer.append(newLine);
				
				currentIndex = maxIndex + 1;
			}
			
			
			// Append the remainder of the string
			buffer.append(string.substring(currentIndex, length));
			
			return buffer.toString();
		}
	}
	
	
	public static String cutWithEllipsis(String in, int maxLength, int endLength)
	{
		if (in == null) {
			return null;
		} else {
			if (in.length() <= maxLength) {
				return in;
			} else {
				int endCutPoint = cutPoint(reverse(in), 0, endLength, 0);
				int cutPoint = cutPoint(in, 0, maxLength - endLength, ELLIPSIS.length());
				String retval = in.substring(0, cutPoint) + ELLIPSIS
						+ in.substring(in.length() - endCutPoint);
				return retval;
			}
		}
	}
	
	
	public static String reverse(String in)
	{
		if (in == null) {
			return null;
		} else {
			String retval = new String();
			
			int len = in.length();
			for (int i = 1; i <= len; i++) {
				retval += in.charAt(len - i);
			}
			
			return retval;
		}
	}
	
	
	
	/**
	 * Determines if a string has got illegals characters defined by a max ascii
	 * code. Useful to forbid upload of file which file name is accented.<br/>
	 * 
	 * @param aString
	 *            the string to check
	 * @param maxAsciiCodeAllowed
	 *            the max ascii code allowed
	 * 
	 * @return the first illegal char not allowed
	 */
	public static Character hasIllegalChar(String aString, int minAsciiCodeAllowed,
			int maxAsciiCodeAllowed)
	{
		Character illegalChar = null;
		
		for (int i = 0; (illegalChar == null) && (i < aString.length()); i++) {
			char theChar = aString.charAt(i);
			if ((theChar > maxAsciiCodeAllowed) || (theChar < minAsciiCodeAllowed)
					|| !isLegalChar(theChar)) {
				illegalChar = new Character(theChar);
			}
		}
		
		return illegalChar;
	}
	
	
	
	/**
	 * Creates a copy of the string where all occurrences of either specialChar
	 * or escapeChar are prefixed with an escapeChar.
	 * 
	 * @param string
	 *            the string in which to escape all occurrences of specialChar
	 *            escape
	 * @param specialChar
	 *            the special char
	 * @param escapeChar
	 *            the escape char
	 * @return an appropriately modified copy of the string
	 */
	public static String escapeString(String string, char specialChar, char escapeChar)
	{
		if (string == null) {
			return null;
		} else {
			int length = string.length();
			StringBuffer buffer = new StringBuffer(length);
			
			for (int i = 0; i < length; i++) {
				char c = string.charAt(i);
				
				if (c == specialChar) {
					buffer.append(escapeChar);
					buffer.append(c);
				} else {
					buffer.append(c);
				}
			}
			
			return buffer.toString();
		}
	}
	
	
	public static String escapeChar(char c, String str)
	{
		if (str == null) {
			return null;
		} else {
			String newString = new String();
			char currChar;
			for (int i = 0; i < str.length(); i++) {
				currChar = str.charAt(i);
				if (currChar == c) {
					newString += "\\";
				}
				newString += currChar;
			}
			
			return newString;
		}
	}
	
	
	public static String escapeChars(String chars, String str)
	{
		if (str == null || chars == null) {
			return str;
		} else {
			String newString = new String();
			char currChar;
			
			for (int i = 0; i < str.length(); i++) {
				currChar = str.charAt(i);
				
				if (chars.indexOf(currChar) >= 0) {
					newString += "\\";
				}
				
				newString += currChar;
			}
			
			return newString;
		}
	}
	
	
	
	/**
	 * replaceStr withStr inStr. (i.e., Replace replaceStr with withStr in
	 * inStr)
	 * 
	 * @return a new string
	 */
	public static String replace(String replaceStr, String withStr, String inStr)
	{
		if (replaceStr == null || withStr == null || inStr == null) {
			return inStr;
		}
		String result = new String(inStr);
		int index = 0;
		while ((index = result.indexOf(replaceStr, index)) >= 0) {
			result = result.substring(0, index) + withStr
					+ result.substring(index + replaceStr.length(), result.length());
			index += withStr.length();
		}
		return result;
	}
	
	
	
	/**
	 * Does the equivalent of a simple s/{pattern}/{string}/g in good old perl.
	 * Tested to my general satisfaction.
	 * 
	 * @param regExpStr
	 * @param withStr
	 * @param inStr
	 * @return new String
	 */
	public static String replaceRegExpWithString(String regExpStr, String withStr, String inStr)
	{
		if (regExpStr == null || withStr == null || inStr == null) {
			return inStr;
		}
		RegularExpression regExp = new RegularExpression(regExpStr);
		StringBuffer result = new StringBuffer(inStr);
		int start = 0;
		Match match = new Match();
		while (regExp.matches(inStr, start, inStr.length(), match)) {
			result.replace(match.getBeginning(0), match.getEnd(0), withStr);
			start = match.getBeginning(0) + 1;
		}
		return result.toString();
	}
	
	
	public static String stripAllWhiteSpace(String string)
	{
		return stripWhiteSpace(string, false);
	}
	
	
	public static String stripExcessiveWhiteSpace(String string)
	{
		return stripWhiteSpace(string, true);
	}
	
	
	public static String pad(final String string, final char paddingChar, final int minWidth,
			final boolean padLeft)
	{
		int length = (string != null) ? string.length() : 0;
		
		if (length < minWidth) {
			StringBuffer buffer = new StringBuffer(minWidth);
			
			if ((!padLeft) && (string != null)) {
				buffer.append(string);
			}
			
			for (; length < minWidth; length++) {
				buffer.append(paddingChar);
			}
			
			if ((padLeft) && (string != null)) {
				buffer.append(string);
			}
			
			return buffer.toString();
		} else {
			return string;
		}
	}
	
	
	
	/**
	 * Replaces variables with values in a String.
	 * 
	 * "Welcome $where" + { "where" = "home" } => "Welcome home"
	 * 
	 * @param input
	 *            string with variable placeholders
	 * @param bindings
	 *            dictionary of variable values
	 * @return string with substituited values
	 */
	public static String bind(String input, NSKeyValueCoding bindings)
	{
		if ((input != null) && (input.indexOf('$') > -1)) {
			int mLength = input.length();
			StringBuffer buffer = new StringBuffer(mLength);
			char[] characters = new char[mLength];
			StringBuffer key = null;
			
			
			// Copy the string into an array
			input.getChars(0, mLength, characters, 0);
			
			for (int m = 0; m < mLength; m++) {
				char character = characters[m];
				
				if (key == null) {
					if (character == '$') {
						key = new StringBuffer();
					} else {
						buffer.append(character);
					}
				} else {
					if (Character.isLetter(character)) {
						key.append(character);
					} else {
						if (key.length() > 0) {
							Object value = bindings.valueForKey(key.toString());
							
							if (value != null) {
								buffer.append(value.toString());
								buffer.append(character);
							} else {
								buffer.append('$');
								buffer.append(key);
								buffer.append(character);
							}
						} else {
							buffer.append('$');
							
							if (character != '$') {
								buffer.append(character);
							}
						}
						
						key = null;
					}
				}
			}
			
			if (key != null) {
				Object value = bindings.valueForKey(key.toString());
				
				if (value != null) {
					buffer.append(value.toString());
				} else {
					buffer.append('$');
					buffer.append(key);
				}
			}
			
			return buffer.toString();
		}
		
		return input;
	}
	
	
	
	// Private class methods
	
	/**
	 * Determines where to cut a string so that the substring after startIndex
	 * does not exceed maxLength. Should cutting the string be required, space
	 * for a trailer (e.g. an ellipsis) of trailerLength characters is
	 * substarcted to maxLength.<br/>
	 * 
	 * This method tries to be smart in it's choice where to suggest splitting
	 * the string in that it favors cutting off at a natural separation and
	 * tries whenever possible to avoid cutting word.
	 * 
	 * @param string
	 *            the string in which to look for a split point
	 * @param startIndex
	 *            the index from which on to measure the length of the string
	 * @param maxLength
	 *            the maximum length (trailer included) allowed for the string
	 * @param trailerLength
	 *            the length of the trailer postfixed if cutting is necessary
	 * @return the suggested point (exclusive) at which to cut the string
	 */
	private static int cutPoint(String string, int startIndex, int maxLength, int trailerLength)
	{
		int endIndex = string.length();
		int length = endIndex - startIndex;
		
		if (maxLength > 0 && length > maxLength) {
			int cutIndex = startIndex + maxLength - trailerLength;
			int minCutIndex = cutIndex - (int) (0.5 * maxLength);
			
			for (int i = cutIndex; i > minCutIndex; i--) {
				char currentChar = string.charAt(i);
				
				if (INCLUSIVE.indexOf(currentChar) > 0) {
					cutIndex = i + 1;
					
					break;
				}
				
				if (EXCLUSIVE.indexOf(currentChar) > 0) {
					int currentIndex = i - 1;
					
					while ((EXCLUSIVE.indexOf(string.charAt(currentIndex)) > 0)
							&& (currentIndex > minCutIndex)) {
						currentIndex--;
					}
					
					cutIndex = currentIndex + 1;
					
					break;
				}
			}
			
			return cutIndex;
		}
		
		return -1;
	}
	
	
	private static String stripWhiteSpace(String string, boolean excessiveOnly)
	{
		if (string != null) {
			char[] chars = string.toCharArray();
			int count = chars.length;
			int shift = 0;
			boolean first = true;
			
			for (int i = 0; i < count; i++) {
				char c = chars[i];
				
				switch (c) {
					case ' ':
					case '\t':
					case '\n':
					case '\r':
						if (excessiveOnly && first) {
							chars[i - shift] = ' ';
							first = false;
						} else {
							shift++;
						}
						
						break;
					
					default:
						first = true;
						chars[i - shift] = c;
				}
			}
			
			if (shift != 0) {
				return new String(chars, 0, count - shift);
			} else {
				return string;
			}
		} else {
			return null;
		}
	}
	
	
	
	/**
	 * Determines if a char can be used in a file name.
	 * 
	 * @param aChar
	 *            the char to check
	 * 
	 * @return true or false
	 */
	private static boolean isLegalChar(char aChar)
	{
		return ((aChar >= ASCII_0 && aChar <= ASCII_9) // numbers are allowed
				|| (aChar >= ASCII_A && aChar <= ASCII_Z) // upper-case
				// letters are
				// allowed
				|| (aChar >= ASCII_a && aChar <= ASCII_z) // lower-case
				// letters are
				// allowed
				|| (aChar == ASCII_DOT) // '.' is allowed
				|| (aChar == ASCII_HYPHEN) // '-' is allowed
		|| (aChar == ASCII_UNDERSCORE)); // '_' is allowed
	}
}