// Talent.java
// Created on Sun Sep 02 17:03:56 Europe/Zurich 2007 by Apple EOModeler Version 5.2

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

package com.houdah.movies.business;

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import java.math.BigDecimal;
import java.util.*;

public class Talent extends EOGenericRecord {

    public Talent() {
        super();
    }

/*
    // If you implement the following constructor EOF will use it to
    // create your objects, otherwise it will use the default
    // constructor. For maximum performance, you should only
    // implement this constructor if you depend on the arguments.
    public Talent(EOEditingContext context, EOClassDescription classDesc, EOGlobalID gid) {
        super(context, classDesc, gid);
    }

    // If you add instance variables to store property values you
    // should add empty implementions of the Serialization methods
    // to avoid unnecessary overhead (the properties will be
    // serialized for you in the superclass).
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    }
*/

    public String firstName() {
        return (String)storedValueForKey("firstName");
    }

    public void setFirstName(String value) {
        takeStoredValueForKey(value, "firstName");
    }

    public String lastName() {
        return (String)storedValueForKey("lastName");
    }

    public void setLastName(String value) {
        takeStoredValueForKey(value, "lastName");
    }

    public com.houdah.movies.business.TalentPhoto photo() {
        return (com.houdah.movies.business.TalentPhoto)storedValueForKey("photo");
    }

    public void setPhoto(com.houdah.movies.business.TalentPhoto value) {
        takeStoredValueForKey(value, "photo");
    }

    public NSArray moviesDirected() {
        return (NSArray)storedValueForKey("moviesDirected");
    }

    public void setMoviesDirected(NSArray value) {
        takeStoredValueForKey(value, "moviesDirected");
    }

    public void addToMoviesDirected(com.houdah.movies.business.Movie object) {
        includeObjectIntoPropertyWithKey(object, "moviesDirected");
    }

    public void removeFromMoviesDirected(com.houdah.movies.business.Movie object) {
        excludeObjectFromPropertyWithKey(object, "moviesDirected");
    }

    public NSArray roles() {
        return (NSArray)storedValueForKey("roles");
    }

    public void setRoles(NSArray value) {
        takeStoredValueForKey(value, "roles");
    }

    public void addToRoles(com.houdah.movies.business.MovieRole object) {
        includeObjectIntoPropertyWithKey(object, "roles");
    }

    public void removeFromRoles(com.houdah.movies.business.MovieRole object) {
        excludeObjectFromPropertyWithKey(object, "roles");
    }
}
