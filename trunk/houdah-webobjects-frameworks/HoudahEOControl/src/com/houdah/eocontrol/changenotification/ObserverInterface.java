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

package com.houdah.eocontrol.changenotification;

import com.webobjects.foundation.NSNotification;

/**
 * Interface to be implemented by objects that need to listen to changes.<br/>
 * 
 * CAVEAT: in most cases it is recommended to not directly implement the
 * interface, but rather create an inner class that implements the interface
 * or better yet extends the default implementation. Indeed if an object
 * (e.g. an EO) which participates in an inheritance hierarchy is used as
 * receiver for notifications, registering or unregistering might break
 * functionality in a parent class.
 */
public interface ObserverInterface
{
	/**
	 * Method called when an object on which locally cached values depend is
	 * modified.<br/>
	 * 
	 * This hook is provided as an opportunity to clear locally cached
	 * values. It's a good idea not to recreate the cached values
	 * immediately, but on an as-needed basis. You should refrain from
	 * changing persisted EO attributes from within this method as this
	 * might kick off another chain of notifications.<br/>
	 * 
	 * Once the caches cleared, it would be a very good idea to unregister
	 * from further notifications until the cache is recreated.
	 * 
	 * @see com.houdah.eocontrol.changenotification.ChangeNotificationCenter#objectFromNotification
	 * @see com.houdah.eocontrol.changenotification.ChangeNotificationCenter#registerCacheDependancy
	 * @see com.houdah.eocontrol.changenotification.ChangeNotificationCenter#unRegisterCacheDependancy
	 */
	public void watchedObjectChanged(NSNotification notification);
}