/**
 * @(#)Undoable.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 * http://www.jhotdraw.org
 * http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 * http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.util

import org.jhotdraw.framework.FigureEnumeration
import org.jhotdraw.framework.DrawingView

/**
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
abstract trait Undoable {
  /**
   * Undo the activity
   * @return true if the activity could be undone, false otherwise
   */
  def undo: Boolean

  def redo: Boolean

  def isUndoable: Boolean

  def setUndoable(newIsUndoable: Boolean)

  def isRedoable: Boolean

  def setRedoable(newIsRedoable: Boolean)

  /**
   * Releases all resources related to an undoable activity
   */
  def release

  def getDrawingView: DrawingView

  def setAffectedFigures(newAffectedFigures: FigureEnumeration)

  def getAffectedFigures: FigureEnumeration

  def getAffectedFiguresCount: Int
}