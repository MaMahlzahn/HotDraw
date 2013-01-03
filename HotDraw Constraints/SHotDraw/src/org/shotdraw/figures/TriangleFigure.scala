/*
 * @(#)TriangleFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	��� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.figures

import java.awt.geom.GeneralPath
import java.awt.Insets
import java.awt.Rectangle
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Point
import org.shotdraw.framework.Handle
import org.shotdraw.standard.BoxHandleKit
import org.shotdraw.util.StorableInput
import org.shotdraw.util.StorableOutput
import org.shotdraw.framework.Connector
import org.shotdraw.standard.AbstractConnector
import org.shotdraw.standard.AbstractFigure
import ch.epfl.lamp.cassowary.SimplexSolver

/**
 * An triangle figure.
 *
 * @version <$CURRENT_VERSION$>
 */
class TriangleFigure(origin: Point, corner: Point, solver: SimplexSolver) extends RectangularFigure(origin, corner, solver) {
  def this(solver: SimplexSolver) {
    this(new Point(0, 0), new Point(0, 0), solver)
  }
  
  override def drawBackground(g: Graphics) {
    val r = displayBox
    val g2d = g.asInstanceOf[Graphics2D]
    val path = new GeneralPath
    path.moveTo(r.x + r.width / 2.0, r.y)
    path.lineTo(r.x, r.y + r.height)
    path.lineTo(r.x + r.width, r.y + r.height)
    path.closePath
    g2d.fill(path)
  }

  override def drawFrame(g: Graphics) {
    val r = displayBox
    val g2d = g.asInstanceOf[Graphics2D]
    val path = new GeneralPath
    path.moveTo(r.x + r.width / 2.0, r.y)
    path.lineTo(r.x, r.y + r.height)
    path.lineTo(r.x + r.width, r.y + r.height)
    path.closePath
    g2d.draw(path)
  }

  override def newFigure(origin: Point, corner: Point, solver: SimplexSolver) = new TriangleFigure(origin, corner, solver)

  override def connectorAt(x: Int, y: Int): Connector =  new ShortestDistanceConnector(this)
}

