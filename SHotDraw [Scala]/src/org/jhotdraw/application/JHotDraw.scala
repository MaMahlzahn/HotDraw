/*
 * @(#) JHotDraw.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.application

import javax.swing.JToolBar
import org.jhotdraw.contrib.PolygonTool
import org.jhotdraw.figures.DiamondFigure
import org.jhotdraw.figures.ElbowConnection
import org.jhotdraw.figures.EllipseFigure
import org.jhotdraw.figures.LineConnection
import org.jhotdraw.figures.LineFigure
import org.jhotdraw.figures.RectangleFigure
import org.jhotdraw.figures.RoundRectangleFigure
import org.jhotdraw.figures.TextFigure
import org.jhotdraw.figures.TextTool
import org.jhotdraw.figures.TriangleFigure
import org.jhotdraw.framework.Tool
import org.jhotdraw.standard.ConnectionTool
import org.jhotdraw.standard.CreationTool

/**
 * A very basic example for demonstraing JHotDraw's capabilities. It contains only
 * a few additional tools apart from the selection tool already provided by its superclass.
 * It uses only a single document interface (SDI) and not a multiple document interface (MDI)
 * because the applicateion frame is derived DrawApplication rather than MDI_DrawApplication.
 * To enable MDI for this application, NothingApp must inherit from MDI_DrawApplication
 * and be recompiled.
 *
 * @version <$CURRENT_VERSION$>
 */

object JHotDraw extends DrawApplication {

  def main(args: Array[String]) {
    val window: DrawApplication = JHotDraw
    window.open
  }

  private final val serialVersionUID: Long = -2982180306746332521L
  
  override protected def createTools(palette: JToolBar) {
    super.createTools(palette)
    var tool: Tool = new TextTool(this, new TextFigure)
    palette.add(createToolButton(DrawApplication.IMAGES + "TEXT", "Text Tool", tool))
    tool = new CreationTool(this, new RectangleFigure)
    palette.add(createToolButton(DrawApplication.IMAGES + "RECT", "Rectangle Tool", tool))
    tool = new CreationTool(this, new RoundRectangleFigure)
    palette.add(createToolButton(DrawApplication.IMAGES + "RRECT", "Round Rectangle Tool", tool))
    tool = new CreationTool(this, new EllipseFigure)
    palette.add(createToolButton(DrawApplication.IMAGES + "ELLIPSE", "Ellipse Tool", tool))
    tool = new CreationTool(this, new TriangleFigure)
    palette.add(createToolButton(DrawApplication.IMAGES + "TRIANGLE", "Triangle Tool", tool))
    tool = new CreationTool(this, new DiamondFigure)
    palette.add(createToolButton(DrawApplication.IMAGES + "DIAMOND", "Diamond Tool", tool))
    tool = new CreationTool(this, new LineFigure)
    palette.add(createToolButton(DrawApplication.IMAGES + "LINE", "Line Tool", tool))
    tool = new PolygonTool(this)
    palette.add(createToolButton(DrawApplication.IMAGES + "POLYGON", "Polygon Tool", tool))
    tool = new ConnectionTool(this, new LineConnection)
    palette.add(createToolButton(DrawApplication.IMAGES + "CONN", "Connection Tool", tool))
    tool = new ConnectionTool(this, new ElbowConnection)
    palette.add(createToolButton(DrawApplication.IMAGES + "OCONN", "Elbow Connection Tool", tool))
  }
}
