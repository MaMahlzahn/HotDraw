/*
 * @(#)Test.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.test.application;

import org.jhotdraw.application.JHotDraw;
import junit.framework.TestCase;
// JUnitDoclet begin import
// JUnitDoclet end import

/*
 * Generated by JUnitDoclet, a tool provided by
 * ObjectFab GmbH under LGPL.
 * Please see www.junitdoclet.org, www.gnu.org
 * and www.objectfab.de for informations about
 * the tool, the licence and the authors.
 */

// JUnitDoclet begin javadoc_class
/**
 * TestCase NothingAppTest is generated by
 * JUnitDoclet to hold the tests for NothingApp.
 * @see org.jhotdraw.JHotDraw.nothing.NothingApp
 */
// JUnitDoclet end javadoc_class
public class JHotDrawTest
// JUnitDoclet begin extends_implements
extends TestCase
// JUnitDoclet end extends_implements
{
	// JUnitDoclet begin class
	// instance variables, helper methods, ... put them in this marker
	private JHotDraw app;
	// JUnitDoclet end class

	/**
	 * Constructor NothingAppTest is
	 * basically calling the inherited constructor to
	 * initiate the TestCase for use by the Framework.
	 */
	public JHotDrawTest(String name) {
		// JUnitDoclet begin method NothingAppTest
		super(name);
		// JUnitDoclet end method NothingAppTest
	}

	/**
	 * Factory method for instances of the class to be tested.
	 */
	public JHotDraw createInstance() throws Exception {
		// JUnitDoclet begin method testcase.createInstance
		return new JHotDraw();
		// JUnitDoclet end method testcase.createInstance
	}

	/**
	 * Method setUp is overwriting the framework method to
	 * prepare an instance of this TestCase for a single test.
	 * It's called from the JUnit framework only.
	 */
	protected void setUp() throws Exception {
		// JUnitDoclet begin method testcase.setUp
		super.setUp();
		app = createInstance();
		// JUnitDoclet end method testcase.setUp
	}

	/**
	 * Method tearDown is overwriting the framework method to
	 * clean up after each single test of this TestCase.
	 * It's called from the JUnit framework only.
	 */
	protected void tearDown() throws Exception {
		// JUnitDoclet begin method testcase.tearDown
		app = null;
		super.tearDown();
		// JUnitDoclet end method testcase.tearDown
	}

	// JUnitDoclet begin javadoc_method main()
	/**
	 * Method testMain is testing main
	 * @see org.jhotdraw.JHotDraw.nothing.NothingApp#main(java.lang.String[])
	 */
	// JUnitDoclet end javadoc_method main()
	public void testMain() throws Exception {
		// JUnitDoclet begin method main
		// JUnitDoclet end method main
	}

	// JUnitDoclet begin javadoc_method testVault
	/**
	 * JUnitDoclet moves marker to this method, if there is not match
	 * for them in the regenerated code and if the marker is not empty.
	 * This way, no test gets lost when regenerating after renaming.
	 * <b>Method testVault is supposed to be empty.</b>
	 */
	// JUnitDoclet end javadoc_method testVault
	public void testVault() throws Exception {
		// JUnitDoclet begin method testcase.testVault
		// JUnitDoclet end method testcase.testVault
	}

}
