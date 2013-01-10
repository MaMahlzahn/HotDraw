package org.shotdraw.framework.align

import org.shotdraw.figures.RectangularFigure
import org.shotdraw.framework.DrawingView
import org.shotdraw.framework.Figure
import org.shotdraw.standard.AbstractFigure
import org.shotdraw.framework.DrawingView
import org.shotdraw.framework.Figure
import ch.epfl.lamp.cassowary.Constraint


class BottomAlign(view: DrawingView) extends Align("Bottom", view) {

  private var figure: RectangularFigure = view.selection(0).asInstanceOf[RectangularFigure] //TODO Change that
  
  override def constraints = view.selection.foldLeft(List[Constraint]())((l,f) => f match {
    case rf: RectangularFigure => l ::: List(rf.db.cy :== figure.db.cy+figure.db.cheight-rf.db.cheight)
    case _ => l
  })
  
}