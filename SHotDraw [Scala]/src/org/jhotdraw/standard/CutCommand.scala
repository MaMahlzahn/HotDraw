/*
 * @(#)CutCommand.java
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
import org.jhotdraw.util.Undoable
import org.jhotdraw.util.UndoableAdapter
import scala.collection.mutable.ArrayBuffer

/**
 * Delete the selection and move the selected figures to
 * the clipboard.
 *
 * @see org.jhotdraw.util.Clipboard
 *
 * @version <$CURRENT_VERSION$>
 */
object CutCommand {

  class UndoActivity(myCommand: FigureTransferCommand) extends UndoableAdapter(myCommand.view) {

    setUndoable(true)
    setRedoable(true)

    /**
     * @see org.jhotdraw.util.Undoable#undo()
     */
    override def undo: Boolean = {
      if (super.undo && !getAffectedFigures.isEmpty) {
        getDrawingView.clearSelection
        myCommand.insertFigures(getAffectedFiguresReversed, 0, 0)
        true
      } else false
    }

    /**
     * @see org.jhotdraw.util.Undoable#redo()
     */
    override def redo: Boolean = {
      if (isRedoable) {
        myCommand.copyFigures(getSelectedFigures, getSelectedFiguresCount)
        myCommand.deleteFigures(getAffectedFigures)
        true
      } else false
    }

    /**
     * Preserve the selection of figures the moment the command was executed.
     * @param newSelectedFigures
     */
    def setSelectedFigures(newSelectedFigures: Seq[Figure]) {
      rememberSelectedFigures(newSelectedFigures)
    }

    /**
     * Preserve a copy of the enumeration in a private list.
     * @param toBeRemembered
     */
    protected def rememberSelectedFigures(toBeRemembered: Seq[Figure]) {
      mySelectedFigures = ArrayBuffer[Figure]()
      toBeRemembered foreach {mySelectedFigures += _}
    }

    /**
     * Returns the selection of figures to perform the command on.
     * @return
     */
    def getSelectedFigures: Seq[Figure] = mySelectedFigures

    /**
     * Returns the size of the selection.
     * @return
     */
    def getSelectedFiguresCount: Int = mySelectedFigures.size

    /**
     * @see org.jhotdraw.util.UndoableAdapter#release()
     */
    override def release {
      super.release
      getSelectedFigures foreach (_.release)
      setSelectedFigures(Seq[Figure]())
    }

    private var mySelectedFigures: ArrayBuffer[Figure] = ArrayBuffer()
  }

}

class CutCommand(name: String, newDrawingEditor: DrawingEditor) extends FigureTransferCommand(name, newDrawingEditor) {
  
  /**
   * @see org.jhotdraw.util.Command#execute()
   */
  override def execute {
    super.execute
    setUndoActivity(createUndoActivity)
    var fe: Seq[Figure] = view.selection
    var affected: ArrayBuffer[Figure] = ArrayBuffer[Figure]()
    var f: Figure = null
    var dfe: Seq[Figure] = null
    fe foreach {f =>
      affected += f
      dfe = f.getDependendFigures
      if (dfe != null) {
        dfe foreach (affected += _)
      }
    }
    fe = affected
    getUndoActivity.setAffectedFigures(fe)
    val ua: CutCommand.UndoActivity = getUndoActivity.asInstanceOf[CutCommand.UndoActivity]
    ua.setSelectedFigures(view.selection)
    copyFigures(ua.getSelectedFigures, ua.getSelectedFiguresCount)
    deleteFigures(getUndoActivity.getAffectedFigures)
    view.checkDamage
  }

  /**
   * @see org.jhotdraw.standard.AbstractCommand#isExecutableWithView()
   */
  override def isExecutableWithView: Boolean = view.selectionCount > 0

  /**
   * Factory method for undo activity
   * @return Undoable
   */
  protected def createUndoActivity: Undoable = new CutCommand.UndoActivity(this)
}

