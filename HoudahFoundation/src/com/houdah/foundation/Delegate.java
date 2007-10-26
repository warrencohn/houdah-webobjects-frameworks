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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Proxy class to delegates. Delegates are specified using Java interfaces which
 * they implement (partially).
 * 

 */
public class Delegate
{
	// Private instance variables
	
	/**
	 * Interface informally implemented by the delegate object
	 */
	private Class			interfaceClass;
	
	
	
	/**
	 * Wrapped delegate object
	 */
	private Object			delegateObject;
	
	
	
	// Private class variables
	
	/**
	 * Cache of implmented methods. <br/>
	 * 
	 * This cache is indexed by interface classes. For each class there is a
	 * nested HashMap indexed by delegate object class. For each delegate class
	 * there is a HashMap indexed by method name. The values are methods on the
	 * delegate.
	 */
	private static HashMap	methodsCache;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param interfaceClass
	 *            interface informally implemented by the delegate. Not null
	 * @param delegateObject
	 *            wrapped delegate object. Not null
	 */
	public Delegate(Class interfaceClass, Object delegateObject)
	{
		this.interfaceClass = interfaceClass;
		this.delegateObject = delegateObject;
		
		if (this.interfaceClass == null) {
			throw new IllegalArgumentException(
					"Interface class argument may not be null");
		}
		
		if (this.delegateObject == null) {
			throw new IllegalArgumentException(
					"Delegate object argument may not be null");
		}
	}
	
	
	
	// Public instance methods
	
	public Class interfaceClass()
	{
		return this.interfaceClass;
	}
	
	
	public Object delegateObject()
	{
		return this.delegateObject;
	}
	
	
	public boolean respondsTo(String name)
	{
		return (methodForName(name) != null);
	}
	
	
	public Object perform(String name)
	{
		return perform(name, new Object[0]);
	}
	
	
	public Object perform(String name, Object arg1)
	{
		return perform(name, new Object[] { arg1 });
	}
	
	
	public Object perform(String name, Object arg1, Object arg2)
	{
		return perform(name, new Object[] { arg1, arg2 });
	}
	
	
	public Object perform(String name, Object arg1, Object arg2, Object arg3)
	{
		return perform(name, new Object[] { arg1, arg2, arg3 });
	}
	
	
	public boolean booleanPerform(String name)
	{
		return ((Boolean) perform(name, new Object[0])).booleanValue();
	}
	
	
	public boolean booleanPerform(String name, Object arg1)
	{
		return ((Boolean) perform(name, new Object[] { arg1 })).booleanValue();
	}
	
	
	public boolean booleanPerform(String name, Object arg1, Object arg2)
	{
		return ((Boolean) perform(name, new Object[] { arg1, arg2 }))
				.booleanValue();
	}
	
	
	public boolean booleanPerform(String name, Object arg1, Object arg2,
			Object arg3)
	{
		return ((Boolean) perform(name, new Object[] { arg1, arg2, arg3 }))
				.booleanValue();
	}
	
	
	public boolean booleanPerform(String name, Object[] arguments)
	{
		return ((Boolean) perform(name, arguments)).booleanValue();
	}
	
	
	
	// Protected instance methods
	
	private Object perform(String name, Object[] arguments)
	{
		Method method = methodForName(name);
		
		if (method == null) {
			throw new IllegalArgumentException(this.delegateObject.getClass()
					+ " doesn't implement method named " + name);
		}
		
		try {
			return method.invoke(this.delegateObject, arguments);
		} catch (InvocationTargetException invocationtargetexception) {
			throw new ForwardException(invocationtargetexception
					.getTargetException());
		} catch (IllegalAccessException illegalaccessexception) {
			throw new IllegalArgumentException("Method is not public: "
					+ method + "\n" + illegalaccessexception.getMessage());
		} catch (IllegalArgumentException illegalargumentexception) {
			throw new IllegalArgumentException(
					"Method has wrong number of arguments: " + method + "\n"
							+ illegalargumentexception.getMessage());
		}
	}
	
	
	
	// Private instance methods
	
	private Method methodForName(String name)
	{
		Class delegateClass = this.delegateObject.getClass();
		HashMap methodsByName = Delegate.methodsByName(this.interfaceClass,
				delegateClass);
		
		return (Method) methodsByName.get(name);
	}
	
	
	
	// Private class methods
	
	private static HashMap methodsByName(Class interfaceClass,
			Class delegateClass)
	{
		if (Delegate.methodsCache == null) {
			Delegate.methodsCache = new HashMap();
		}
		
		HashMap methodsForInterface = (HashMap) Delegate.methodsCache
				.get(interfaceClass);
		
		if (methodsForInterface == null) {
			methodsForInterface = new HashMap();
			Delegate.methodsCache.put(interfaceClass, methodsForInterface);
		}
		
		HashMap methodsForDelegate = (HashMap) methodsForInterface
				.get(delegateClass);
		
		if (methodsForDelegate == null) {
			methodsForDelegate = setUpMethodsForDelegate(interfaceClass,
					delegateClass);
			methodsForInterface.put(delegateClass, methodsForDelegate);
		}
		
		return methodsForDelegate;
	}
	
	
	private static HashMap setUpMethodsForDelegate(Class interfaceClass,
			Class delegateClass)
	{
		Method[] interfaceMethods = interfaceClass.getMethods();
		HashMap methodsForDelegate = new HashMap(interfaceMethods.length);
		
		for (int i = 0; i < interfaceMethods.length; i++) {
			Method iMethod = interfaceMethods[i];
			String name = iMethod.getName();
			Method dMethod = null;
			
			try {
				dMethod = delegateClass.getMethod(name, iMethod
						.getParameterTypes());
			} catch (NoSuchMethodException e) {
				dMethod = null;
			}
			
			if (dMethod != null) {
				methodsForDelegate.put(name, dMethod);
			}
		}
		
		return methodsForDelegate;
	}
}
