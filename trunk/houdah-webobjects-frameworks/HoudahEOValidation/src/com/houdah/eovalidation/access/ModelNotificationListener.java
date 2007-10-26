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

package com.houdah.eovalidation.access;

import java.util.Enumeration;

import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOEntityClassDescription;
import com.webobjects.eoaccess.EOModel;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eocontrol.EOClassDescription;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;

/**
 * Listen to EOModel notifications so we can install our own
 * EOEntityClassDescription subclass
 * 
 * Based on code by Chuck Hill, Sacha Mallais and Project Wonder
 * 
 * @author bernard
 */
public class ModelNotificationListener
{
	// Private instance methods
	
	private EOModel				model;
	
	
	private NSMutableDictionary	cachedDescriptionsByEntityName;
	
	
	private NSMutableDictionary	cachedDescriptionsByClassName;
	
	
	
	
	// Public class methods: public interface
	
	/**
	 * Install our own validation system by forcing the use of our own
	 * EOEntityClassDescription subclass.<br/>
	 * 
	 * This method needs to be called before any EOModels are loaded. Typically
	 * it is called from the framework's principal class.
	 */
	public static void install()
	{
		NSNotificationCenter defaultCenter = NSNotificationCenter.defaultCenter();
		NSSelector modelAddedNotification = new NSSelector("modelAddedNotification",
				new Class[] { NSNotification.class });
		
		defaultCenter.addObserver(ModelNotificationListener.class, modelAddedNotification,
				EOModelGroup.ModelAddedNotification, null);
		
		EOClassDescription.invalidateClassDescriptionCache();
	}
	
	
	
	// Constructor
	
	/**
	 * Designated constructor.
	 * 
	 * Sets a new instance of ModelNotificationListener to listen for model
	 * notifications. It substituites itself to the EOModel which would have
	 * assumed that role.
	 * 
	 * @param model
	 *            model to to observe
	 */
	public ModelNotificationListener(EOModel model)
	{
		// Initialize instance
		this.model = model;
		
		
		// Wire it up
		NSNotificationCenter defaultCenter = NSNotificationCenter.defaultCenter();
		NSSelector classDescriptionNeededForClass = new NSSelector(
				"classDescriptionNeededForClassNotification", new Class[] { NSNotification.class });
		NSSelector classDescriptionNeededForEntityName = new NSSelector(
				"classDescriptionNeededForEntityNameNotification",
				new Class[] { NSNotification.class });
		
		
		// Remove the model as notification listener
		defaultCenter.removeObserver(model(),
				EOClassDescription.ClassDescriptionNeededForClassNotification, null);
		defaultCenter.removeObserver(model(),
				EOClassDescription.ClassDescriptionNeededForEntityNameNotification, null);
		
		
		// Install ourselves as listener
		defaultCenter.addObserver(this, classDescriptionNeededForClass,
				EOClassDescription.ClassDescriptionNeededForClassNotification, null);
		defaultCenter.addObserver(this, classDescriptionNeededForEntityName,
				EOClassDescription.ClassDescriptionNeededForEntityNameNotification, null);
		
		
		// Prevent the listener from being garbage collected before the model
		NSMutableDictionary userInfo = model.userInfo().mutableClone();
		
		userInfo.setObjectForKey(this, ModelNotificationListener.class.getName());
		model.setUserInfo(userInfo);
	}
	
	
	
	// Public instance methods
	
	
	/**
	 * Forces a newly loaded entity to use our subclass of
	 * EOEntityClassDescription
	 * 
	 * @param notification
	 *            notification of which entity was loaded
	 */
	public void entityLoadedNotification(NSNotification notification)
	{
		EOEntity entity = (EOEntity) notification.object();
		
		try {
			// HACK: We push the class description rather rudely into the entity
			// to have it ready when classDescriptionForNewInstances() is called
			// on it.
			synchronized (entity) {
				NSKeyValueCoding.Utility.takeValueForKey(entity,
						classDescriptionForEntityNamed(entity.name()), "classDescription");
			}
		} catch (RuntimeException ex) {
			// Ignore entities without class definitions
		}
	}
	
	
	
	/**
	 * Checks the model to see if it has an entity corresponding to this class
	 * and registers a class description for it if it does. This is the most
	 * common notification.
	 * 
	 * @param notification
	 *            notification of which class a description is needed for.
	 */
	public void classDescriptionNeededForClassNotification(NSNotification notification)
	{
		// Try and find an entity in this model implemented by the class in the
		// notification, or implemented by a superclass of the the class in the
		// notification.
		Class classFromThisModel = (Class) notification.object();
		EOEntityClassDescription classDescriptionFromModel = null;
		
		while ((classFromThisModel != null) && (classDescriptionFromModel == null)) {
			classDescriptionFromModel = classDescriptionForClassNamed(classFromThisModel.getName());
			
			if (classDescriptionFromModel == null) {
				classFromThisModel = classFromThisModel.getSuperclass();
			}
		}
		
		
		// If the model does not contain the entity (class) requested, ask for
		// it again. Don't ask me why this works, but it prevents the "A class
		// description of a generic record cannot be null" exception. I think
		// there is a more efficient implementation for this, but I am too tired
		// now to see it and this works.
		if (classFromThisModel != (Class) notification.object()) {
			// Voodoo.
			EOClassDescription.classDescriptionForClass(((Class) notification.object()));
		}
		// Otherwise we have the right description so register it.
		else if (classDescriptionFromModel != null) {
			EOClassDescription.registerClassDescription(classDescriptionFromModel,
					(Class) notification.object());
		}
	}
	
	
	
	/**
	 * Checks the model to see if it has an entity with ths name and registers a
	 * class description for it if it does. This notification is used with
	 * entities implemented as EOGenericRecords.
	 * 
	 * @param notification
	 *            notification of which entity a description is needed for.
	 * @exception ClassNotFoundException
	 *                if the className for the entity can not be resolved into a
	 *                class
	 */
	public void classDescriptionNeededForEntityNameNotification(NSNotification notification)
			throws ClassNotFoundException
	{
		String entityName = (String) notification.object();
		EOEntityClassDescription classDescription = classDescriptionForEntityNamed(entityName);
		
		if (classDescription != null) {
			Class entityClass = Class.forName(classDescription.entity().className());
			EOClassDescription.registerClassDescription(classDescription, entityClass);
		}
	}
	
	
	
	// Accessors
	
	public EOModel model()
	{
		return this.model;
	}
	
	
	
	// Public class methods
	
	/**
	 * When a new model is created or loaded, install a listener instance to
	 * handle entity notifications for that model.
	 * 
	 * @param notification
	 *            notification of which model was added.
	 */
	public static void modelAddedNotification(NSNotification notification)
	{
		EOModel model = (EOModel) notification.object();
		ModelNotificationListener listener = new ModelNotificationListener(model);
		NSSelector selector = new NSSelector("entityLoadedNotification",
				new Class[] { NSNotification.class });
		
		NSNotificationCenter.defaultCenter().addObserver(listener, selector,
				EOModel.EntityLoadedNotification, null);
	}
	
	
	
	// Protected instance methods
	
	/**
	 * Prime the caches in order to guarantee unique class description
	 * instances.
	 */
	protected synchronized void primeCaches()
	{
		Enumeration entityEnumerator = model().entities().objectEnumerator();
		
		this.cachedDescriptionsByEntityName = new NSMutableDictionary();
		this.cachedDescriptionsByClassName = new NSMutableDictionary();
		
		while (entityEnumerator.hasMoreElements()) {
			EOEntity entity = (EOEntity) entityEnumerator.nextElement();
			
			
			// Exclude prototypes and malformed entities
			if (entity.className() != null) {
				EOClassDescription descriptionForEntity = createClassDescriptionEntity(entity);
				
				this.cachedDescriptionsByEntityName.setObjectForKey(descriptionForEntity, entity
						.name());
				
				
				// No need to cache for EOGenericRecord as they are never
				// requested by class
				if (!entity.className().equals("EOGenericRecord")) {
					this.cachedDescriptionsByClassName.setObjectForKey(descriptionForEntity, entity
							.className());
				}
			}
		}
	}
	
	
	
	/**
	 * Creates a new instance of a custom subclass of EOEntityClassDescription.
	 * 
	 * Subclasses may override this method to provide alternative
	 * implementations.
	 * 
	 * @param entity
	 *            entity to create class description for
	 * @return EOClassDescription class description for this entity
	 */
	protected EOClassDescription createClassDescriptionEntity(EOEntity entity)
	{
		return new ValidatingEntityClassDescription(entity);
	}
	
	
	
	/**
	 * Tries to find a class description for the named entity.
	 * 
	 * @param entityName
	 *            name of entity to lookup class description for.
	 * @return EOEntityClassDescription class description for this entity or
	 *         null if the entity is not in the model.
	 */
	protected synchronized EOEntityClassDescription classDescriptionForEntityNamed(String entityName)
	{
		if (this.cachedDescriptionsByEntityName == null) {
			primeCaches();
		}
		
		return (EOEntityClassDescription) this.cachedDescriptionsByEntityName
				.objectForKey(entityName);
	}
	
	
	
	/**
	 * Tries to find a class description for the named class.
	 * 
	 * @param className
	 *            name of class to lookup class description for.
	 * @return EOEntityClassDescription class description for this class or null
	 *         if an entity with this className is not in the model
	 */
	protected synchronized EOEntityClassDescription classDescriptionForClassNamed(String className)
	{
		if (this.cachedDescriptionsByClassName == null) {
			primeCaches();
		}
		
		return (EOEntityClassDescription) this.cachedDescriptionsByClassName
				.objectForKey(className);
	}
}