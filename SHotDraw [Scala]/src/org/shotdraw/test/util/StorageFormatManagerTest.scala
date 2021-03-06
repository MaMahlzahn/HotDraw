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
package org.shotdraw.test.util

import junit.framework.TestCase
import org.shotdraw.util.StandardStorageFormat
import org.shotdraw.util.StorageFormatManager
import org.shotdraw.util.StorageFormat

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
 * TestCase StorageFormatManagerTest is generated by
 * JUnitDoclet to hold the tests for StorageFormatManager.
 * @see org.shotdraw.util.StorageFormatManager
 */
class StorageFormatManagerTest(name: String) extends TestCase(name) {

  /**
   * Factory method for instances of the class to be tested.
   */
  def createInstance: StorageFormatManager = {
    new StorageFormatManager
  }

  /**
   * Method setUp is overwriting the framework method to
   * prepare an instance of this TestCase for a single test.
   * It's called from the JUnit framework only.
   */
  override protected def setUp() {
    super.setUp
    storageformatmanager = createInstance
  }

  /**
   * Method tearDown is overwriting the framework method to
   * clean up after each single test of this TestCase.
   * It's called from the JUnit framework only.
   */
  override protected def tearDown() {
    storageformatmanager = null
    super.tearDown
  }

  /**
   * Method testAddStorageFormat is testing addStorageFormat
   * @see org.shotdraw.util.StorageFormatManager#addStorageFormat(org.shotdraw.util.StorageFormat)
   */
  def testAddStorageFormat() {
  }

  /**
   * Method testRemoveStorageFormat is testing removeStorageFormat
   * @see org.shotdraw.util.StorageFormatManager#removeStorageFormat(org.shotdraw.util.StorageFormat)
   */
  def testRemoveStorageFormat() {
  }

  /**
   * Method testContainsStorageFormat is testing containsStorageFormat
   * @see org.shotdraw.util.StorageFormatManager#containsStorageFormat(org.shotdraw.util.StorageFormat)
   */
  def testContainsStorageFormat() {
  }

  /**
   * Method testSetGetDefaultStorageFormat is testing setDefaultStorageFormat
   * and getDefaultStorageFormat together by setting some value
   * and verifying it by reading.
   * @see org.shotdraw.util.StorageFormatManager#setDefaultStorageFormat(org.shotdraw.util.StorageFormat)
   * @see org.shotdraw.util.StorageFormatManager#getDefaultStorageFormat()
   */
  def testSetGetDefaultStorageFormat() {
    val tests = List(new StandardStorageFormat, null)
    
    tests foreach { e =>
      storageformatmanager.setDefaultStorageFormat(e)
      assert(e == storageformatmanager.getDefaultStorageFormat)
    }
  }

  /**
   * Method testRegisterFileFilters is testing registerFileFilters
   * @see org.shotdraw.util.StorageFormatManager#registerFileFilters(javax.swing.JFileChooser)
   */
  def testRegisterFileFilters() {
  }

  /**
   * Method testFindStorageFormat is testing findStorageFormat
   * @see org.shotdraw.util.StorageFormatManager#findStorageFormat(javax.swing.filechooser.FileFilter)
   */
  def testFindStorageFormat() {
  }

  /**
   * JUnitDoclet moves marker to this method, if there is not match
   * for them in the regenerated code and if the marker is not empty.
   * This way, no test gets lost when regenerating after renaming.
   * <b>Method testVault is supposed to be empty.</b>
   */
  def testVault() {
  }

  private var storageformatmanager: StorageFormatManager = null
}

