/*
 * @(#)HandleEnumerator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.standard


import org.jhotdraw.framework.Handle
import org.jhotdraw.framework.HandleEnumeration

import scala.collection.JavaConversions._


/**
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
object HandleEnumerator {
  def getEmptyEnumeration: HandleEnumeration = {
    return singletonEmptyEnumerator
  }

  private var singletonEmptyEnumerator: HandleEnumerator = new HandleEnumerator(List[Handle]())
}

class HandleEnumerator extends HandleEnumeration {
  
  def this(c: Collection[Handle]) {
    this()
    myInitialCollection = c
    reset
  }
  
  def this(c: java.util.Collection[Handle]) {
    this()
    myInitialCollection = c
    reset
  }

  /**
   * Returns true if the enumeration contains more elements; false
   * if its empty.
   */
  def hasNext: Boolean = myIterator.hasNext

  /**
   * Returns the next element of the enumeration. Calls to this
   * method will enumerate successive elements.
   * @exception java.util.NoSuchElementException If no more elements exist.
   */
  def next: Handle = myIterator.next

  /**
   * Returns a list with all elements currently available in the enumeration.
   * That means, elements retrieved already by calling nextHandle() are not
   * contained. This method does not change the position of the enumeration.
   * Warning: this method is not necessarily synchronized so this enumeration should not
   * be modified at the same time!
   *
   * @return list with all elements currently available in the enumeration.
   */
  override def toList: List[Handle] = {
    var handles: List[Handle] = List[Handle]()
    while (hasNext) {
      handles ::= next
    }
    myIterator = handles.iterator
    handles
  }

  /**
   * Reset the enumeration so it can be reused again. However, the
   * underlying collection might have changed since the last usage
   * so the elements and the order may vary when using an enumeration
   * which has been reset.
   */
  def reset = myIterator = myInitialCollection.iterator

  private var myIterator: Iterator[Handle] = null
  private var myInitialCollection: Collection[Handle] = null
}

