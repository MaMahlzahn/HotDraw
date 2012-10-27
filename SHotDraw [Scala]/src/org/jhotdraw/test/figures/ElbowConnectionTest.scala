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
package org.jhotdraw.test.figures

import org.jhotdraw.figures.ElbowConnection
import junit.framework.TestCase

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
 * TestCase ElbowConnectionTest is generated by
 * JUnitDoclet to hold the tests for ElbowConnection.
 * @see org.jhotdraw.figures.ElbowConnection
 */
class ElbowConnectionTest(name: String) extends TestCase(name) {

  /**
   * Factory method for instances of the class to be tested.
   */
  def createInstance: ElbowConnection = {
    new ElbowConnection
  }

  /**
   * Method setUp is overwriting the framework method to
   * prepare an instance of this TestCase for a single test.
   * It's called from the JUnit framework only.
   */
  override protected def setUp {
    super.setUp
    elbowconnection = createInstance
  }

  /**
   * Method tearDown is overwriting the framework method to
   * clean up after each single test of this TestCase.
   * It's called from the JUnit framework only.
   */
  override protected def tearDown {
    elbowconnection = null
    super.tearDown
  }

  /**
   * Method testUpdateConnection is testing updateConnection
   * @see org.jhotdraw.figures.ElbowConnection#updateConnection()
   */
  def testUpdateConnection {
  }

  /**
   * Method testLayoutConnection is testing layoutConnection
   * @see org.jhotdraw.figures.ElbowConnection#layoutConnection()
   */
  def testLayoutConnection {
  }

  /**
   * Method testHandles is testing handles
   * @see org.jhotdraw.figures.ElbowConnection#handles()
   */
  def testHandles {
  }

  /**
   * Method testConnectedTextLocator is testing connectedTextLocator
   * @see org.jhotdraw.figures.ElbowConnection#connectedTextLocator(org.jhotdraw.framework.Figure)
   */
  def testConnectedTextLocator {
  }

  /**
   * JUnitDoclet moves marker to this method, if there is not match
   * for them in the regenerated code and if the marker is not empty.
   * This way, no test gets lost when regenerating after renaming.
   * <b>Method testVault is supposed to be empty.</b>
   */
  def testVault {
  }

  private var elbowconnection: ElbowConnection = null
}

