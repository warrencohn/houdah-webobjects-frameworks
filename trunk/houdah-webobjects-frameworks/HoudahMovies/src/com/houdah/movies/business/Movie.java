// Movie.java
// Created on Sun Sep 02 17:02:52 Europe/Zurich 2007 by Apple EOModeler Version 5.2

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

public class Movie extends EOGenericRecord {

    public Movie() {
        super();
    }

/*
    // If you implement the following constructor EOF will use it to
    // create your objects, otherwise it will use the default
    // constructor. For maximum performance, you should only
    // implement this constructor if you depend on the arguments.
    public Movie(EOEditingContext context, EOClassDescription classDesc, EOGlobalID gid) {
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

    public String category() {
        return (String)storedValueForKey("category");
    }

    public void setCategory(String value) {
        takeStoredValueForKey(value, "category");
    }

    public NSTimestamp dateReleased() {
        return (NSTimestamp)storedValueForKey("dateReleased");
    }

    public void setDateReleased(NSTimestamp value) {
        takeStoredValueForKey(value, "dateReleased");
    }

    public String posterName() {
        return (String)storedValueForKey("posterName");
    }

    public void setPosterName(String value) {
        takeStoredValueForKey(value, "posterName");
    }

    public String rated() {
        return (String)storedValueForKey("rated");
    }

    public void setRated(String value) {
        takeStoredValueForKey(value, "rated");
    }

    public BigDecimal revenue() {
        return (BigDecimal)storedValueForKey("revenue");
    }

    public void setRevenue(BigDecimal value) {
        takeStoredValueForKey(value, "revenue");
    }

    public String title() {
        return (String)storedValueForKey("title");
    }

    public void setTitle(String value) {
        takeStoredValueForKey(value, "title");
    }

    public String trailerName() {
        return (String)storedValueForKey("trailerName");
    }

    public void setTrailerName(String value) {
        takeStoredValueForKey(value, "trailerName");
    }

    public com.houdah.movies.business.PlotSummary plotSummary() {
        return (com.houdah.movies.business.PlotSummary)storedValueForKey("plotSummary");
    }

    public void setPlotSummary(com.houdah.movies.business.PlotSummary value) {
        takeStoredValueForKey(value, "plotSummary");
    }

    public com.houdah.movies.business.Studio studio() {
        return (com.houdah.movies.business.Studio)storedValueForKey("studio");
    }

    public void setStudio(com.houdah.movies.business.Studio value) {
        takeStoredValueForKey(value, "studio");
    }

    public com.houdah.movies.business.Voting voting() {
        return (com.houdah.movies.business.Voting)storedValueForKey("voting");
    }

    public void setVoting(com.houdah.movies.business.Voting value) {
        takeStoredValueForKey(value, "voting");
    }

    public NSArray directors() {
        return (NSArray)storedValueForKey("directors");
    }

    public void setDirectors(NSArray value) {
        takeStoredValueForKey(value, "directors");
    }

    public void addToDirectors(com.houdah.movies.business.Talent object) {
        includeObjectIntoPropertyWithKey(object, "directors");
    }

    public void removeFromDirectors(com.houdah.movies.business.Talent object) {
        excludeObjectFromPropertyWithKey(object, "directors");
    }

    public NSArray reviews() {
        return (NSArray)storedValueForKey("reviews");
    }

    public void setReviews(NSArray value) {
        takeStoredValueForKey(value, "reviews");
    }

    public void addToReviews(com.houdah.movies.business.Review object) {
        includeObjectIntoPropertyWithKey(object, "reviews");
    }

    public void removeFromReviews(com.houdah.movies.business.Review object) {
        excludeObjectFromPropertyWithKey(object, "reviews");
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
