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

package com.houdah.eoaccess;

import com.houdah.eoaccess.coercion.BestMatchQualifierCoercionSupport;
import com.houdah.eoaccess.coercion.BestRelationshipMatchesQualifierCoercionSupport;
import com.houdah.eoaccess.coercion.ExistsInRelationshipQualifierCoercionSupport;
import com.houdah.eoaccess.coercion.InSetQualifierCoercionSupport;
import com.houdah.eoaccess.coercion.InSubqueryQualifierCoercionSupport;
import com.houdah.eoaccess.coercion.PeriodQualifierCoercionSupport;
import com.houdah.eoaccess.coercion.PiggybackQualifierCoercionSupport;
import com.houdah.eoaccess.coercion.QualifierAttributeCoercion;
import com.houdah.eoaccess.databaseContext.DatabaseContext;
import com.houdah.eoaccess.databaseContext.DatabaseContextDelegate;
import com.houdah.eoaccess.qualifiers.BestMatchQualifierSupport;
import com.houdah.eoaccess.qualifiers.BestRelationshipMatchesQualifierSupport;
import com.houdah.eoaccess.qualifiers.ExistsInRelationshipQualifierSupport;
import com.houdah.eoaccess.qualifiers.FalseQualifierSupport;
import com.houdah.eoaccess.qualifiers.InSetQualifierSupport;
import com.houdah.eoaccess.qualifiers.InSubqueryQualifierSupport;
import com.houdah.eoaccess.qualifiers.PeriodQualifierSupport;
import com.houdah.eoaccess.qualifiers.PiggybackQualifierSupport;
import com.houdah.eoaccess.qualifiers.TrueQualifierSupport;
import com.houdah.eocontrol.qualifiers.BestMatchQualifier;
import com.houdah.eocontrol.qualifiers.BestRelationshipMatchesQualifier;
import com.houdah.eocontrol.qualifiers.ExistsInRelationshipQualifier;
import com.houdah.eocontrol.qualifiers.FalseQualifier;
import com.houdah.eocontrol.qualifiers.InSetQualifier;
import com.houdah.eocontrol.qualifiers.InSubqueryQualifier;
import com.houdah.eocontrol.qualifiers.PeriodQualifier;
import com.houdah.eocontrol.qualifiers.PiggybackQualifier;
import com.houdah.eocontrol.qualifiers.TrueQualifier;

import com.webobjects.eoaccess.EODatabaseContext;
import com.webobjects.eoaccess.EOQualifierSQLGeneration;
import com.webobjects.foundation.NSLog;

/**
 * Principal class of the HoudahEOAccess project. Sets up various classes in the
 * project.
 * 
 * @author bernard
 */
public class PrincipalClass
{
	// Static initializer
	
	static {
		// Be polite, say 'Hello'
		NSLog.debug.appendln("Initializing HoudahEOAccess");
		
		
		// Configure debugging
		if (Boolean.getBoolean("EOAdaptorDebugEnabled")) {
			NSLog.allowDebugLoggingForGroups(NSLog.DebugGroupDatabaseAccess);
		}
		
		
		// Setting up qualifier SQL generation support
		NSLog.debug
				.appendln("Setting up SQL generation support for custom qualifiers");
		
		EOQualifierSQLGeneration.Support.setSupportForClass(
				new BestMatchQualifierSupport(), BestMatchQualifier.class);
		
		EOQualifierSQLGeneration.Support.setSupportForClass(
				new BestRelationshipMatchesQualifierSupport(),
				BestRelationshipMatchesQualifier.class);
		
		EOQualifierSQLGeneration.Support.setSupportForClass(
				new InSetQualifierSupport(), InSetQualifier.class);
		
		EOQualifierSQLGeneration.Support.setSupportForClass(
				new InSubqueryQualifierSupport(), InSubqueryQualifier.class);
		
		EOQualifierSQLGeneration.Support.setSupportForClass(
				new ExistsInRelationshipQualifierSupport(),
				ExistsInRelationshipQualifier.class);
		
		EOQualifierSQLGeneration.Support.setSupportForClass(
				new PeriodQualifierSupport(), PeriodQualifier.class);
		
		EOQualifierSQLGeneration.Support.setSupportForClass(
				new TrueQualifierSupport(), TrueQualifier.class);
		
		EOQualifierSQLGeneration.Support.setSupportForClass(
				new FalseQualifierSupport(), FalseQualifier.class);
		
		EOQualifierSQLGeneration.Support.setSupportForClass(
				new PiggybackQualifierSupport(), PiggybackQualifier.class);
		
		
		// Setting up qualifier coercion support
		NSLog.debug
				.appendln("Setting up coercion support for custom qualifiers");
		
		QualifierAttributeCoercion.registerSupportForClass(
				new BestMatchQualifierCoercionSupport(),
				BestMatchQualifier.class);
		
		QualifierAttributeCoercion.registerSupportForClass(
				new BestRelationshipMatchesQualifierCoercionSupport(),
				BestRelationshipMatchesQualifier.class);
		
		QualifierAttributeCoercion.registerSupportForClass(
				new InSetQualifierCoercionSupport(), InSetQualifier.class);
		
		QualifierAttributeCoercion.registerSupportForClass(
				new InSubqueryQualifierCoercionSupport(),
				InSubqueryQualifier.class);
		
		QualifierAttributeCoercion.registerSupportForClass(
				new ExistsInRelationshipQualifierCoercionSupport(),
				ExistsInRelationshipQualifier.class);
		
		QualifierAttributeCoercion.registerSupportForClass(
				new PeriodQualifierCoercionSupport(), PeriodQualifier.class);
		
		QualifierAttributeCoercion.registerSupportForClass(
				new PiggybackQualifierCoercionSupport(),
				PiggybackQualifier.class);
		
		
		// Setting up EOAcces delegates
		
		NSLog.debug
				.appendln("Setting the HoudahEOAccess DatabaseContext as database context class");
		EODatabaseContext.setContextClassToRegister(DatabaseContext.class);
		
		NSLog.debug
				.appendln("Setting the HoudahEOAccess DatabaseContextDelegate as default delegate of EODatabaseContext");
		EODatabaseContext.setDefaultDelegate(new DatabaseContextDelegate());
	}
}