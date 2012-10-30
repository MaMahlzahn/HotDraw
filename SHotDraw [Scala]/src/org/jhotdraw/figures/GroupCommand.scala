/*
 * @(#)GroupCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.figures

import org.jhotdraw.framework._
import org.jhotdraw.standard._
import org.jhotdraw.util._

/**
 * Command to group the selection into a GroupFigure.
 *
 * @see GroupFigure
 *
 * @version <$CURRENT_VERSION$>
 */
object GroupCommand {

  class UndoActivity(newDrawingView: DrawingView) extends UndoableAdapter(newDrawingView) {
    setUndoable(true)
    setRedoable(true)

    override def undo: Boolean = {
      if (!super.undo) {
        return false
      }
      getDrawingView.clearSelection
      getDrawingView.drawing.orphanAll(getAffectedFigures)
      var affectedFigures: List[Figure] = List[Figure]()
      getAffectedFigures foreach { f =>
        getDrawingView.drawing.addAll(f.figures)
        getDrawingView.addToSelectionAll(f.figures)
        f.figures foreach {affectedFigures ::= _}
      }
      setAffectedFigures(affectedFigures)
      true
    }

    override def redo: Boolean = {
      if (isRedoable) {
        groupFigures
        true
      } else false
    }

    def groupFigures {
      getDrawingView.drawing.orphanAll(getAffectedFigures)
      getDrawingView.clearSelection
      val group: GroupFigure = new GroupFigure
      group.addAll(getAffectedFigures)
      val figure: Figure = getDrawingView.drawing.add(group)
      getDrawingView.addToSelection(figure)
      setAffectedFigures(List(figure))
    }
  }

}

class GroupCommand(name: String, newDrawingEditor: DrawingEditor) extends AbstractCommand(name, newDrawingEditor) {

  override def execute {
    super.execute
    setUndoActivity(createUndoActivity)
    getUndoActivity.setAffectedFigures(view.selection)
    (getUndoActivity.asInstanceOf[GroupCommand.UndoActivity]).groupFigures
    view.checkDamage
  }

  override def isExecutableWithView: Boolean = view.selectionCount > 1

  /**
   * Factory method for undo activity
   */
  protected def createUndoActivity: Undoable = new GroupCommand.UndoActivity(view)
}

