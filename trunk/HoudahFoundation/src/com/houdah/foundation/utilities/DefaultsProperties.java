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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSPropertyListSerialization;

/**
 * Custom properties storage.<br/>
 * 
 * Properties are grouped in domains. On the file system, each domain is
 * materilized as a file under a given directory (e.g. $HOME/defaults).</br>
 * 
 * We support two types of files:
 * <ul>
 * <li> The property file as defined by java.util.Properties </li>
 * <li> The plist file as defined by </li>
 * com.webobjects.foundation.NSPropertyListSerialization
 * </ul>
 * <br/>
 * 
 * The plist files are stored with a .plist extension. The Properties files are
 * named with no extension.<BR>
 * 
 * The two different file types lead to two different representations of the
 * domains they describe.
 * <ul>
 * <li> The property files lead to domain objects that are instances of
 * java.util.Properties they restrict values to be of String type. </li>
 * <li> The plist files lead to domain objects that are instances of
 * com.webobjects.foundation.NSMutableDictionary. The usual plist restrictions
 * apply: only objects of types String, NSArray and NSDictionary may be used as
 * values. </li>
 * </ul>
 * <br/>
 * 
 * These restrictions are for most part NOT enforced when manipulating the
 * domain representations. Failure to comply to these restrictions will however
 * lead to the impossibility to save to disk the offending domain.
 * 
 * @author bernard
 */
public class DefaultsProperties
{
	// Private class variables
	
	private static HashMap	roots;
	
	
	
	// Private instance variables
	
	private HashMap			defaults;
	
	
	private File			defaultsDirectory;
	
	
	
	
	// Constructor
	
	/**
	 * Private constructor. We use a single instance per root.
	 * 
	 * @see #getProperties
	 * @throws RuntimeException
	 *             in the event of an internal problem usually related to file
	 *             permissions or existence
	 */
	private DefaultsProperties(String rootDirectory)
	{
		defaults = new HashMap();
		defaultsDirectory = getDefaultsDirectory(rootDirectory);
	}
	
	
	
	// Public class methods
	
	/**
	 * DefaultsProperties lookup method.<BR>
	 * 
	 * Lazily creates the appropriate DefaultsProperties object.
	 * 
	 * @param rootDirectory
	 *            the directory to read the domain files from
	 * @return the appropriate pseudo-singleton DefaultsProperties object
	 * @throws RuntimeException
	 *             in the event of an internal problem usually related to file
	 *             permissions or existence
	 */
	public static synchronized DefaultsProperties getProperties(String rootDirectory)
	{
		DefaultsProperties instance = (DefaultsProperties) ((roots != null) ? roots.get(rootDirectory) : null);
		
		if (instance == null) {
			roots = (roots == null) ? new HashMap() : roots;
			instance = new DefaultsProperties(rootDirectory);
			
			roots.put(rootDirectory, instance);
		}
		
		return instance;
	}
	
	
	
	// Public instance methods
	
	/**
	 * Retrieves a the value of a property within a domain.
	 * 
	 * @param propertyName
	 *            the name of the property to retrieve
	 * @param domainName
	 *            the name of the domain hosting the property
	 * @return the requested value, null if either the domain or the poperty are
	 *         unknown
	 * @throws RuntimeException
	 *             in the event of an internal problem usually related to file
	 *             permissions or format
	 */
	public Object getPropertyInDomain(String propertyName, String domainName)
	{
		Object domain = getDomain(domainName);
		
		if (domain != null) {
			if (domain instanceof Properties) {
				return ((Properties) domain).getProperty(propertyName);
			} else if (domain instanceof NSMutableDictionary) {
				return ((NSMutableDictionary) domain).objectForKey(propertyName);
			} else {
				// This cannot happen unless the load method is flawed
				throw new RuntimeException("The property domain named '" + domainName + "' is of unknown type '" + domain.getClass().getName() + "'");
			}
		} else {
			return null;
		}
	}
	
	
	
	/**
	 * Stores a the value of a property within a domain.<BR>
	 * 
	 * Bear in mind the restrictions on value types applied by the two supported
	 * domain types.
	 * 
	 * @param propertyName
	 *            the name of the property to set
	 * @param domainName
	 *            the name of the domain hosting the property
	 * @param value
	 *            the value to store
	 * @return true if the operation succeeded, false if the domain does not
	 *         exist
	 * @throws RuntimeException
	 *             in the event of an internal problem usually related to file
	 *             permissions or format
	 */
	public boolean setPropertyInDomain(String propertyName, String domainName, Object value)
	{
		Object domain = getDomain(domainName);
		
		if (domain != null) {
			if (domain instanceof Properties) {
				((Properties) domain).put(propertyName, value);
			} else if (domain instanceof NSMutableDictionary) {
				((NSMutableDictionary) domain).setObjectForKey(value, propertyName);
			} else {
				throw new RuntimeException("The property domain named '" + domainName + "' is of unknown type");
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	
	
	/**
	 * Determines if a given domain exists.<BR>
	 * 
	 * This method lazily loads the domain from the file system if it exists.
	 * 
	 * @param domainName
	 *            the name of the domain
	 * @return true if the domain exists
	 * @throws RuntimeException
	 *             in the event of an internal problem usually related to file
	 *             permissions or format
	 */
	public boolean domainExists(String domainName)
	{
		return (getDomain(domainName) != null);
	}
	
	
	
	/**
	 * Determines if a given domain is of plist type.<BR>
	 * 
	 * This method lazily loads the domain from the file system if it exists.
	 * 
	 * @param domainName
	 *            the name of the domain
	 * @return true if the domain exists and is of plist type
	 * @throws RuntimeException
	 *             in the event of an internal problem usually related to file
	 *             permissions or format
	 */
	public boolean isPlistDomain(String domainName)
	{
		Object domain = getDomain(domainName);
		
		return ((domain != null) && (domain instanceof NSMutableDictionary));
	}
	
	
	
	/**
	 * Determines if a given domain is of plist type.<BR>
	 * 
	 * This method lazily loads the domain from the file system if it exists.
	 * 
	 * @param domainName
	 *            the name of the domain
	 * @return the requested domain, null if it does not yet exist
	 * @throws RuntimeException
	 *             in the event of an internal problem usually related to file
	 *             permissions or format
	 */
	public Object getDomain(String domainName)
	{
		Object domain = defaults.get(domainName);
		
		if (domain == null) {
			domain = loadDomain(domainName);
		}
		
		return domain;
	}
	
	
	
	/**
	 * Determines if a given domain is of plist type.<BR>
	 * 
	 * If a domain of that name already exists, it is retrieved.
	 * 
	 * @param domainName
	 *            the name of the domain
	 * @param isPlist
	 *            true if the new domain should be of plist type
	 * @return the new domain, or an existing domain of the same name
	 * @throws RuntimeException
	 *             in the event of an internal problem usually related to file
	 *             permissions or format
	 * @throws IllegalArgumentException
	 *             if a domain of that name already exists with a different type
	 */
	public Object createDomain(String domainName, boolean isPlist)
	{
		Object domain = getDomain(domainName);
		
		if (domain != null) {
			if (isPlist && isPlistDomain(domainName)) {
				return domain;
			} else {
				throw new IllegalArgumentException("The domain '" + domainName
						+ "' cannot be created because it already exists with a different type");
			}
		} else {
			if (isPlist) {
				domain = new NSMutableDictionary();
				putDomain(domainName, (NSMutableDictionary) domain);
			} else {
				domain = new Properties();
				putDomain(domainName, (Properties) domain);
			}
			
			return domain;
		}
	}
	
	
	
	/**
	 * Adds a new, externally created domain.<BR>
	 * 
	 * An existing domain of the same name is overwritten, no matter of which
	 * type it is.
	 * 
	 * @param domainName
	 *            the name of the domain
	 * @param domain
	 *            the domain to store
	 */
	public void putDomain(String domainName, NSMutableDictionary domain)
	{
		defaults.put(domainName, domain);
	}
	
	
	
	/**
	 * Adds a new, externally created domain.<BR>
	 * 
	 * An existing domain of the same name is overwritten, no matter of which
	 * type it is.
	 * 
	 * @param domainName
	 *            the name of the domain
	 * @param domain
	 *            the domain to store
	 */
	public void putDomain(String domainName, Properties domain)
	{
		defaults.put(domainName, domain);
	}
	
	
	
	/**
	 * Saves the named domain to the file system.<BR>
	 * 
	 * @param domainName
	 *            the name of the domain
	 * @throws RuntimeException
	 *             in the event of an internal problem usually related to file
	 *             permissions or format
	 */
	public synchronized void saveDomain(String domainName)
	{
		Object domain = defaults.get(domainName);
		
		if (domain != null) {
			File plistFile = new File(defaultsDirectory, domainName + ".plist");
			File propertyFile = new File(defaultsDirectory, domainName);
			
			if (domain instanceof NSMutableDictionary) {
				if (propertyFile.exists())
					throw new RuntimeException("A conflicting file (" + propertyFile.getAbsolutePath() + ") exists");
				
				if (plistFile.exists()) {
					if (!plistFile.canWrite())
						throw new RuntimeException("The domain file (" + plistFile.getAbsolutePath() + ") is not writeable");
					
					if (!plistFile.isFile())
						throw new RuntimeException("The path (" + plistFile.getAbsolutePath() + ") is not a file");
				} else {
					try {
						plistFile.createNewFile();
					} catch (IOException e) {
						throw new RuntimeException("Failed to create the domain file (" + plistFile.getAbsolutePath() + ")");
					}
				}
				
				try {
					File tmpFile = File.createTempFile(domainName, ".plist", new File(defaultsDirectory.getAbsolutePath(), "tmp"));
					FileWriter fileWriter = new FileWriter(tmpFile);
					String propertyList = NSPropertyListSerialization.stringFromPropertyList(domain);
					File oldPropertyFile = new File(propertyFile.getAbsolutePath(), "_old");
					
					fileWriter.write(propertyList, 0, propertyList.length());
					fileWriter.flush();
					fileWriter.close();
					
					if (tmpFile.renameTo(plistFile)) {
						loadDomain(domainName);
						oldPropertyFile.delete();
					} else {
						oldPropertyFile.renameTo(propertyFile);
						throw new RuntimeException("Failed to rename temporay file >" + tmpFile.getAbsolutePath() + "< to "
								+ plistFile.getAbsolutePath());
					}
				} catch (IOException e) {
					throw new RuntimeException("Failed to write the domain file (" + plistFile.getAbsolutePath() + ")");
				}
			} else if (domain instanceof Properties) {
				if (plistFile.exists())
					throw new RuntimeException("A conflicting file (" + plistFile.getAbsolutePath() + ") exists");
				
				if (propertyFile.exists()) {
					if (!propertyFile.canWrite())
						throw new RuntimeException("The domain file (" + propertyFile.getAbsolutePath() + ") is not writeable");
					
					if (!propertyFile.isFile())
						throw new RuntimeException("The path (" + propertyFile.getAbsolutePath() + ") is not a file");
				} else {
					try {
						propertyFile.createNewFile();
					} catch (IOException e) {
						throw new RuntimeException("Failed to create the domain file (" + propertyFile.getAbsolutePath() + ")");
					}
				}
				
				try {
					File tmpFile = File.createTempFile(domainName, ".tmp", new File(defaultsDirectory.getAbsolutePath(), "tmp"));
					FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);
					File oldPropertyFile = new File(propertyFile.getAbsolutePath(), "_old");
					
					((Properties) domain).store(fileOutputStream, null);
					fileOutputStream.flush();
					fileOutputStream.close();
					
					propertyFile.renameTo(oldPropertyFile);
					
					if (tmpFile.renameTo(propertyFile)) {
						loadDomain(domainName);
						oldPropertyFile.delete();
					} else {
						oldPropertyFile.renameTo(propertyFile);
						throw new RuntimeException("Failed to rename temporay file >" + tmpFile.getAbsolutePath() + "< to "
								+ propertyFile.getAbsolutePath());
					}
				} catch (IOException e) {
					throw new RuntimeException("Failed to write the domain file (" + propertyFile.getAbsolutePath() + ")");
				}
			} else {
				throw new RuntimeException("The property domain named '" + domainName + "' is of unknown type");
			}
		} else {
			throw new RuntimeException("There is no domain named '" + domainName + "'");
		}
	}
	
	
	
	// Private instance methods
	
	/**
	 * Saves the named domain to the file system.<BR>
	 * 
	 * @param domainName
	 *            the name of the domain
	 * @throws RuntimeException
	 *             in the event of an internal problem usually related to file
	 *             permissions or format
	 */
	private synchronized Object loadDomain(String domainName)
	{
		Object domain;
		File file = new File(defaultsDirectory, domainName + ".plist");
		
		if (file.exists()) {
			if (!file.canRead())
				throw new RuntimeException("The domain file (" + file.getAbsolutePath() + ") is not readable");
			
			domain = PropertyListUtilities.dictionaryFromFile(file);
		} else {
			file = new File(defaultsDirectory, domainName);
			
			if (file.exists()) {
				Properties properties;
				
				if (!file.canRead())
					throw new RuntimeException("The domain file (" + file.getAbsolutePath() + ") is not readable");
				
				properties = new Properties();
				
				try {
					properties.load(new FileInputStream(file));
				} catch (IOException ioe) {
					return null;
				}
				
				domain = properties;
			} else {
				// We don't crash if the domain does not yet exist
				return null;
			}
		}
		
		defaults.put(domainName, domain);
		
		return domain;
	}
	
	
	
	// Private class methods
	
	/**
	 * Builds a File reference to the named defaults directory.
	 * 
	 * @throws RuntimeException
	 *             in the event of an internal problem usually related to file
	 *             permissions or existence
	 */
	private File getDefaultsDirectory(String rootDirectory)
	{
		File _rootDirectory = new File(rootDirectory);
		File _defaultsDirectory = new File(_rootDirectory, "defaults");
		
		if (!_rootDirectory.exists())
			throw new RuntimeException("The root directory (" + _rootDirectory.getAbsolutePath() + ") does not exist");
		
		if (!_rootDirectory.canRead())
			throw new RuntimeException("The root directory (" + _rootDirectory.getAbsolutePath() + ") is not readable");
		
		if (!_defaultsDirectory.exists())
			throw new RuntimeException("The defaults directory (" + _defaultsDirectory.getAbsolutePath() + ") does not exist");
		
		if (!_defaultsDirectory.canRead())
			throw new RuntimeException("The defaults directory (" + _defaultsDirectory.getAbsolutePath() + ") is not readable");
		
		return _defaultsDirectory;
	}
	
	
	
	/**
	 * Returns the list of known domains.<BR>
	 * 
	 * @return the enumeration of domains names
	 */
	public Enumeration getDomainsList()
	{
		Vector domainsVector = new Vector();
		// Retrieve all the files in the defaults directory
		File[] files = defaultsDirectory.listFiles();
		// Keep the name witout extension of a file if :
		// - it's a ".plist" file
		// - it has no extension
		for (int i = 0; i < files.length; i++) {
			if (!files[i].isDirectory()) {
				String filename = files[i].getName();
				if (filename.indexOf(".plist") != -1)
					domainsVector.addElement(filename.substring(0, filename.indexOf(".plist")));
				else if (filename.indexOf(".") == -1)
					domainsVector.addElement(filename);
			}
		}
		return domainsVector.elements();
	}
}
