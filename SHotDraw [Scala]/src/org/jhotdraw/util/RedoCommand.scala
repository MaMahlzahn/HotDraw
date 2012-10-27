/*
 * @(#)RedoCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.util

import org.jhotdraw.standard._
import org.jhotdraw.framework._

/**
 * Command to redo the latest undone change in the drawing.
 *
 * @version <$CURRENT_VERSION$>
 */
class RedoCommand(name: String, newDrawingEditor: DrawingEditor) extends AbstractCommand(name, newDrawingEditor) {

  override def execute {
    super.execute
    val um: UndoManager = getDrawingEditor.getUndoManager
    if ((um == null) || !um.isRedoable) {
      return
    }
    val lastRedoable: Undoable = um.popRedo
    val hasBeenUndone: Boolean = lastRedoable.redo
    if (hasBeenUndone && lastRedoable.isUndoable) {
      um.pushUndo(lastRedoable)
    }
    lastRedoable.getDrawingView.checkDamage
    getDrawingEditor.figureSelectionChanged(lastRedoable.getDrawingView)
  }

  /**
   * Used in enabling the redo menu item.
   * Redo menu item will be enabled only when there is at least one redoable
   * activity in the UndoManager.
   */
  override def isExecutableWithView: Boolean = {
    val um: UndoManager = getDrawingEditor.getUndoManager
    ((um != null) && (um.getRedoSize > 0))
  }
}

