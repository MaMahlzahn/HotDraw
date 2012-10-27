/*
 * JHDDragSource.java
 *
 * Created on January 28, 2003, 4:49 PM
 */
package org.jhotdraw.contrib.dnd

import org.jhotdraw.framework._
import org.jhotdraw.standard.DeleteFromDrawingVisitor
import org.jhotdraw.util.Undoable
import java.awt.Component
import java.awt.dnd._
import javax.swing.JComponent
import org.jhotdraw.standard.FigureEnumerator


/**
 *
 * @author  Administrator
 */
object JHDDragSourceListener {
  private def log(message: String) {
  }

  class RemoveUndoActivity(view: DrawingView) extends org.jhotdraw.util.UndoableAdapter(view) {
    log("RemoveUndoActivity created " + view)
    setUndoable(true)
    setRedoable(true)

    override def undo: Boolean = {
      if (isUndoable) {
        if (getAffectedFigures.hasNext) {
          log("RemoveUndoActivity undo")
          getDrawingView.clearSelection
          setAffectedFigures(getDrawingView.insertFigures(getAffectedFigures, 0, 0, false))
          undone = true
          return true
        }
      }
      false
    }

    override def redo: Boolean = {
      if (isRedoable) {
        log("RemoveUndoActivity redo")
        val deleteVisitor: DeleteFromDrawingVisitor = new DeleteFromDrawingVisitor(getDrawingView.drawing)
        getAffectedFigures foreach { f =>
          f.visit(deleteVisitor)
        }
        getDrawingView.clearSelection
        setAffectedFigures(deleteVisitor.getDeletedFigures)
        undone = false
        return true
      }
      false
    }

    /**
     * Since this is a delete activity, figures can only be released if the
     * action has not been undone.
     */
    override def release {
      if (undone == false) {
        getAffectedFigures foreach { f =>
          getDrawingView.drawing.remove(f)
          f.release
        }
      }
      setAffectedFigures(FigureEnumerator.getEmptyEnumeration)
    }

    private var undone: Boolean = false
  }

}

class JHDDragSourceListener(myEditor: DrawingEditor, newView: DrawingView) extends java.awt.dnd.DragSourceListener {
  import JHDDragSourceListener._
  protected def editor: DrawingEditor = myEditor

  /**
   * This method is invoked to signify that the Drag and Drop operation is complete.
   * This is the last method called in the process.
   */
  def dragDropEnd(dsde: DragSourceDropEvent) {
    val view: DrawingView = dsde.getDragSourceContext.getComponent.asInstanceOf[DrawingView]
    log("DragSourceDropEvent-dragDropEnd")
    if (dsde.getDropSuccess) {
      if (dsde.getDropAction == DnDConstants.ACTION_MOVE) {
        log("DragSourceDropEvent-ACTION_MOVE")
        setSourceUndoActivity(createSourceUndoActivity(view))
        val df: DNDFigures = DNDHelper.processReceivedData(DNDFiguresTransferable.DNDFiguresFlavor, dsde.getDragSourceContext.getTransferable).asInstanceOf[DNDFigures]
        getSourceUndoActivity.setAffectedFigures(df.getFigures)
        val deleteVisitor: DeleteFromDrawingVisitor = new DeleteFromDrawingVisitor(view.drawing)
        getSourceUndoActivity.getAffectedFigures foreach { f => f.visit(deleteVisitor)}
        view.clearSelection
        view.checkDamage
        myEditor.getUndoManager.pushUndo(getSourceUndoActivity)
        myEditor.getUndoManager.clearRedos
        myEditor.figureSelectionChanged(view)
      } else if (dsde.getDropAction == DnDConstants.ACTION_COPY) {
        log("DragSourceDropEvent-ACTION_COPY")
      }
    }
    if (autoscrollState) {
      dsde.getDragSourceContext.getComponent match {
        case jc: JComponent =>
          jc.setAutoscrolls(autoscrollState)
          autoscrollState = false
        case _ =>
      }
    }
  }

  /**
   * Called as the hotspot enters a platform dependent drop site.
   */
  def dragEnter(dsde: DragSourceDragEvent) {
    log("DragSourceDragEvent-dragEnter")
    if (!autoscrollState) {
      val c: Component = dsde.getDragSourceContext.getComponent
      if (classOf[JComponent].isInstance(c)) {
        val jc: JComponent = c.asInstanceOf[JComponent]
        autoscrollState = jc.getAutoscrolls
        jc.setAutoscrolls(false)
      }
    }
  }

  /**
   * Called as the hotspot exits a platform dependent drop site.
   */
  def dragExit(dse: DragSourceEvent) {
  }

  /**
   * Called as the hotspot moves over a platform dependent drop site.
   */
  def dragOver(dsde: DragSourceDragEvent) {
  }

  /**
   * Called when the user has modified the drop gesture.
   */
  def dropActionChanged(dsde: DragSourceDragEvent) {
    log("DragSourceDragEvent-dropActionChanged")
  }

  /**
   * Factory method for undo activity
   */
  protected def createSourceUndoActivity(drawingView: DrawingView): Undoable = {
    return new JHDDragSourceListener.RemoveUndoActivity(drawingView)
  }

  protected def setSourceUndoActivity(undoable: Undoable) {
    sourceUndoable = undoable
  }

  protected def getSourceUndoActivity: Undoable = {
    return sourceUndoable
  }

  private var sourceUndoable: Undoable = null
  private var autoscrollState: Boolean = false
}

