/*
 * @(#)PolyLineHandle.java
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
import org.jhotdraw.standard.LocatorHandle
import org.jhotdraw.standard.SingleFigureEnumerator
import org.jhotdraw.util.Undoable
import org.jhotdraw.util.UndoableAdapter
import java.awt._

/**
 * A handle for a node on the polyline.
 *
 * @version <$CURRENT_VERSION$>
 */
object PolyLineHandle {

  class UndoActivity(newView: DrawingView, newPointIndex: Int) extends UndoableAdapter(newView) {
    setUndoable(true)
    setRedoable(true)
    setPointIndex(newPointIndex)    

    override def undo: Boolean = {
      if (!super.undo) {
        return false
      }
      movePointToOldLocation
    }

    override def redo: Boolean = {
      if (!isRedoable) {
        return false
      }
      movePointToOldLocation
    }

    protected def movePointToOldLocation: Boolean = {
      val fe: FigureEnumeration = getAffectedFigures
      if (!fe.hasNext) {
        return false
      }
      val figure: PolyLineFigure = fe.next.asInstanceOf[PolyLineFigure]
      val backupPoint: Point = figure.pointAt(getPointIndex)
      figure.setPointAt(getOldPoint, getPointIndex)
      setOldPoint(backupPoint)
      true
    }

    def setOldPoint(newOldPoint: Point) {
      myOldPoint = newOldPoint
    }

    def getOldPoint: Point = myOldPoint

    def setPointIndex(newPointIndex: Int) {
      myPointIndex = newPointIndex
    }

    def getPointIndex: Int = myPointIndex

    private var myOldPoint: Point = null
    private var myPointIndex: Int = 0
  }

}

class PolyLineHandle(owner: PolyLineFigure, l: Locator, fIndex: Int) extends LocatorHandle(owner, l) {

  override def invokeStart(x: Int, y: Int, view: DrawingView) {
    setUndoActivity(createUndoActivity(view, fIndex))
    getUndoActivity.setAffectedFigures(new SingleFigureEnumerator(owner))
    (getUndoActivity.asInstanceOf[PolyLineHandle.UndoActivity]).setOldPoint(new Point(x, y))
  }

  override def invokeStep(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    val currentIndex: Int = (getUndoActivity.asInstanceOf[PolyLineHandle.UndoActivity]).getPointIndex
    myOwner.setPointAt(new Point(x, y), currentIndex)
  }

  override def invokeEnd(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    if ((x == anchorX) && (y == anchorY)) {
      setUndoActivity(null)
    }
  }

  private def myOwner: PolyLineFigure = owner

  /**
   * Factory method for undo activity. To be overriden by subclasses.
   */
  protected def createUndoActivity(newView: DrawingView, newPointIndex: Int): Undoable = new PolyLineHandle.UndoActivity(newView, newPointIndex)
}

