/*
 * @(#)DeleteCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.standard

import org.jhotdraw.framework.DrawingEditor
import org.jhotdraw.framework.Figure
import org.jhotdraw.framework.FigureEnumeration
import org.jhotdraw.util.Undoable
import org.jhotdraw.util.UndoableAdapter

/**
 * Command to delete the selection.
 *
 * @version <$CURRENT_VERSION$>
 */
object DeleteCommand {

  class UndoActivity(myCommand: FigureTransferCommand) extends UndoableAdapter(myCommand.view) {
    setUndoable(true)
    setRedoable(true)

    /**
     * @see org.jhotdraw.util.Undoable#undo()
     */
    override def undo: Boolean = {
      if (super.undo && getAffectedFigures.hasNext) {
        getDrawingView.clearSelection
        setAffectedFigures(myCommand.insertFigures(getAffectedFiguresReversed, 0, 0))
        true
      } else false
    }

    /**
     * @see org.jhotdraw.util.Undoable#redo()
     */
    override def redo: Boolean = {
      if (isRedoable) {
        myCommand.deleteFigures(getAffectedFigures)
        getDrawingView.clearSelection
        true
      } else false
    }
  }

}

class DeleteCommand(name: String, newDrawingEditor: DrawingEditor) extends FigureTransferCommand(name, newDrawingEditor) {
  /**
   * @see org.jhotdraw.util.Command#execute()
   */
  override def execute {
    super.execute
    setUndoActivity(createUndoActivity)
    var fe: FigureEnumeration = view.selection
    var affected: List[Figure] = List[Figure]()
    var f: Figure = null
    var dfe: FigureEnumeration = null
    fe foreach { f =>
      affected ::= f
      dfe = f.getDependendFigures
      if (dfe != null) {
        dfe foreach {
          affected ::=  _
        }
      }
    }
    getUndoActivity.setAffectedFigures(new FigureEnumerator(affected))
    deleteFigures(getUndoActivity.getAffectedFigures)
    view.checkDamage
  }

  /**
   * @see org.jhotdraw.standard.AbstractCommand#isExecutableWithView()
   */
  protected override def isExecutableWithView: Boolean = view.selectionCount > 0

  /**
   * Factory method for undo activity
   * @return Undoable
   */
  protected def createUndoActivity: Undoable = new DeleteCommand.UndoActivity(this)
}

