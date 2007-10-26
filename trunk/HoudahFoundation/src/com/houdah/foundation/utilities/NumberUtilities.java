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

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Utility class for manipulating Number and Boolean objects
 * 
 * @author bernard
 */
public class NumberUtilities
{
	// Public class constants
	
	public static Number		ZERO_NUMBER			= new Short((short) 0);
	
	
	public static Number		ONE_NUMBER			= new Short((short) 1);
	
	
	public static BigInteger	ZERO_BIG_INTEGER	= new BigInteger("0");
	
	
	public static BigInteger	ONE_BIG_INTEGER		= new BigInteger("1");
	
	
	public static BigDecimal	ZERO_BIG_DECIMAL	= new BigDecimal(0);
	
	
	public static BigDecimal	ONE_BIG_DECIMAL		= new BigDecimal(1);
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 */
	private NumberUtilities()
	{
		throw new IllegalStateException("Do not instantiate this utility class");
	}
	
	
	
	// Public class methods
	
	public static boolean classIsBoolean(Class aClass)
	{
		return (aClass == Boolean.TYPE) || (aClass == Boolean.class);
	}
	
	
	public static boolean classIsNumber(Class aClass)
	{
		return Number.class.isAssignableFrom(aClass) || (aClass == Integer.TYPE)
				|| (aClass == Double.TYPE) || (aClass == Long.TYPE) || (aClass == Float.TYPE)
				|| (aClass == Byte.TYPE) || (aClass == Short.TYPE);
	}
	
	
	public static boolean classIsNumberOrBoolean(Class aClass)
	{
		return classIsNumber(aClass) || classIsBoolean(aClass);
	}
	
	
	public static Number numberForBoolean(Boolean aBoolean)
	{
		if (aBoolean != null) {
			return aBoolean.booleanValue() ? ONE_NUMBER : ZERO_NUMBER;
		} else {
			return null;
		}
	}
	
	
	public static Boolean booleanForNumber(Number aNumber)
	{
		if (aNumber != null) {
			if (aNumber.getClass() == BigDecimal.class) {
				return ((BigDecimal) aNumber).compareTo(ZERO_BIG_DECIMAL) == 0 ? Boolean.FALSE
						: Boolean.TRUE;
			} else if (aNumber.getClass() == BigInteger.class) {
				return ((BigInteger) aNumber).compareTo(ZERO_BIG_INTEGER) == 0 ? Boolean.FALSE
						: Boolean.TRUE;
			} else {
				return (aNumber.intValue() == 0) ? Boolean.FALSE : Boolean.TRUE;
			}
		} else {
			return null;
		}
	}
	
	
	public static Number convertToMatchClass(Boolean aBoolean, Class prototype)
	{
		return convertToMatchNumberClass(numberForBoolean(aBoolean), prototype);
	}
	
	
	public static Number convertToMatchNumberClass(Number aNumber, Class prototype)
	{
		assert prototype != null;
		
		if (aNumber != null) {
			if (aNumber.getClass() != prototype) {
				if ((prototype == Integer.TYPE) || (prototype == Integer.class)) {
					return IntegerFactory.integerForInt(aNumber.intValue());
				} else if ((prototype == Double.TYPE) || (prototype == Double.class)) {
					return new Double(aNumber.doubleValue());
				} else if ((prototype == Float.TYPE) || (prototype == Float.class)) {
					return new Float(aNumber.floatValue());
				} else if ((prototype == Long.TYPE) || (prototype == Long.class)) {
					return new Long(aNumber.longValue());
				} else if ((prototype == Short.TYPE) || (prototype == Short.class)) {
					return new Short(aNumber.shortValue());
				} else if ((prototype == Byte.TYPE) || (prototype == Byte.class)) {
					return new Byte(aNumber.byteValue());
				} else if (BigDecimal.class.isAssignableFrom(prototype)) {
					if (aNumber instanceof BigInteger) {
						return new BigDecimal((BigInteger) aNumber);
					} else if (aNumber instanceof Double) {
						return new BigDecimal(aNumber.doubleValue());
					} else {
						return BigDecimal.valueOf(aNumber.longValue());
					}
				} else if (BigInteger.class.isAssignableFrom(prototype)) {
					if (aNumber instanceof BigDecimal) {
						return ((BigDecimal) aNumber).toBigInteger();
					} else {
						return BigInteger.valueOf(aNumber.longValue());
					}
				}
			}
		}
		
		return aNumber;
	}
	
	
	public static Object convertToNumberOrBoolean(Object object, Class targetClass)
	{
		assert targetClass != null;
		
		if (object != null) {
			Class objectClass = object.getClass();
			
			if (classIsNumber(targetClass)) {
				if (classIsNumber(objectClass)) {
					return convertToMatchNumberClass((Number) object, targetClass);
				} else if (classIsBoolean(objectClass)) {
					return convertToMatchClass((Boolean) object, targetClass);
				}
			} else if (classIsBoolean(targetClass)) {
				if (classIsBoolean(objectClass)) {
					return object;
				} else if (classIsNumber(objectClass)) {
					return booleanForNumber((Number) object);
				}
			}
		}
		
		return object;
	}
}
