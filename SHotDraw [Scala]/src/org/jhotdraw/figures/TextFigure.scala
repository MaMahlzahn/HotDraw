/*
 * @(#)TextFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.figures

import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import java.awt.Toolkit
import java.io.IOException
import java.io.ObjectInputStream
import org.jhotdraw.framework.Figure
import org.jhotdraw.framework.FigureAttributeConstant
import org.jhotdraw.framework.FigureChangeEvent
import org.jhotdraw.framework.FigureChangeListener
import org.jhotdraw.framework.Handle


import org.jhotdraw.standard.NullHandle
import org.jhotdraw.standard.OffsetLocator
import org.jhotdraw.standard.RelativeLocator
import org.jhotdraw.standard.TextHolder
import org.jhotdraw.util.ColorMap
import org.jhotdraw.util.StorableInput
import org.jhotdraw.util.StorableOutput
import java.lang.Object

/**
 * A text figure.
 *
 * @see TextTool
 *
 * @version <$CURRENT_VERSION$>
 */
object TextFigure {

  private final val serialVersionUID: Long = 4599820785949456124L
}

class TextFigure extends AttributeFigure with FigureChangeListener with TextHolder {
  setFillColor(ColorMap.color("None"));
  private var fOriginX: Int = 0
  private var fOriginY: Int = 0
  @transient
  private var fSizeIsDirty: Boolean = true
  @transient
  private var fWidth: Int = 0
  @transient
  private var fHeight: Int = 0
  private var fText: String = ""
  private var fFont: Font = new Font(getFontName, getFontStyle, getFontSize)
  private var fIsReadOnly: Boolean = false
  private var fObservedFigure: Figure = null
  private var fLocator: OffsetLocator = null

  /**
   * @see org.jhotdraw.framework.Figure#moveBy(int, int)
   */
  override def moveBy(x: Int, y: Int) {
    willChange
    basicMoveBy(x, y)
    if (getLocator != null) {
      getLocator.moveBy(x, y)
    }
    changed
  }

  protected def basicMoveBy(x: Int, y: Int) {
    fOriginX += x
    fOriginY += y
  }

  /**
   * @see org.jhotdraw.framework.Figure#basicDisplayBox(java.awt.Point, java.awt.Point)
   */
  def basicDisplayBox(newOrigin: Point, newCorner: Point) {
    fOriginX = newOrigin.x
    fOriginY = newOrigin.y
  }

  /**
   * @see org.jhotdraw.framework.Figure#displayBox()
   */
  def displayBox: Rectangle = {
    val extent: Dimension = textExtent
    new Rectangle(fOriginX, fOriginY, extent.width, extent.height)
  }

  /**
   * @see org.jhotdraw.standard.TextHolder#textDisplayBox()
   */
  def textDisplayBox: Rectangle = displayBox

  /**
   * Tests whether this figure is read only.
   */
  def readOnly: Boolean = fIsReadOnly

  /**
   * Sets the read only status of the text figure.
   */
  def setReadOnly(isReadOnly: Boolean) {
    fIsReadOnly = isReadOnly
  }

  /**
   * Gets the font.
   * @see org.jhotdraw.standard.TextHolder#getFont()
   */
  def getFont: Font = fFont

  /**
   * Usually, a TextHolders is implemented by a Figure subclass. To avoid casting
   * a TextHolder to a Figure this method can be used for polymorphism (in this
   * case, let the (same) object appear to be of another type).
   * Note, that the figure returned is not the figure to which the TextHolder is
   * (and its representing figure) connected.
   * @return figure responsible for representing the content of this TextHolder
   * @see org.jhotdraw.standard.TextHolder#getRepresentingFigure()
   */
  def getRepresentingFigure: Figure = this

  /**
   * Sets the font.
   */
  def setFont(newFont: Font) {
    willChange
    fFont = newFont
    markDirty
    changed
  }
  
  override def setFontStyle(value: Int) {
    super.setFontStyle(value)
    setFont(new Font(getFont.getName, value, getFont.getSize))
  }
  
  override def setFontName(value: String) {
    super.setFontName(value)
    setFont(new Font(value, getFont.getStyle, getFont.getSize))
  }
  
  override def setFontSize(value: Int) {
    super.setFontSize(value)
    setFont(new Font(getFont.getName, getFont.getStyle, value))
  }

  /**
   * Updates the location whenever the figure changes itself.
   * @see org.jhotdraw.framework.Figure#changed()
   */
  override def changed {
    super.changed
  }


  /**
   * Gets the text shown by the text figure.
   * @see org.jhotdraw.standard.TextHolder#getText()
   */
  def getText: String = fText

  /**
   * Sets the text shown by the text figure.
   * @see org.jhotdraw.standard.TextHolder#setText(java.lang.String)
   */
  def setText(newText: String) {
    if (newText == null || !(newText == fText)) {
      willChange
      fText = newText
      markDirty
      changed
    }
  }

  /**
   * Tests whether the figure accepts typing.
   * @see org.jhotdraw.standard.TextHolder#acceptsTyping()
   */
  def acceptsTyping: Boolean = !fIsReadOnly

  /**
   * @see org.jhotdraw.figures.AttributeFigure#drawBackground(java.awt.Graphics)
   */
  override def drawBackground(g: Graphics) {
    val r: Rectangle = displayBox
    g.fillRect(r.x, r.y, r.width, r.height)
  }

  /**
   * @see org.jhotdraw.figures.AttributeFigure#drawFrame(java.awt.Graphics)
   */
  override def drawFrame(g: Graphics) {
    g.setFont(fFont)
    g.setColor(getTextColor)
    val metrics: FontMetrics = g.getFontMetrics(fFont)
    val r: Rectangle = displayBox
    g.drawString(getText, r.x, r.y + metrics.getAscent)
  }

  protected def textExtent: Dimension = {
    if (!fSizeIsDirty) {
      return new Dimension(fWidth, fHeight)
    }
    val metrics: FontMetrics = Toolkit.getDefaultToolkit.getFontMetrics(fFont)
    fWidth = metrics.stringWidth(getText)
    fHeight = metrics.getHeight
    fSizeIsDirty = false
    new Dimension(fWidth, fHeight)
  }

  protected def markDirty {
    fSizeIsDirty = true
  }

  /**
   * Gets the number of columns to be overlaid when the figure is edited.
   * @see org.jhotdraw.standard.TextHolder#overlayColumns()
   */
  def overlayColumns: Int = {
    val length: Int = getText.length
    var columns: Int = 20
    if (length != 0) {
      columns = getText.length + 3
    }
    columns
  }

  /**
   * @see org.jhotdraw.framework.Figure#handles()
   */
  def handles: Seq[Handle] = {
    new NullHandle(this, RelativeLocator.northWest) ::
        new NullHandle(this, RelativeLocator.northEast) ::
        new NullHandle(this, RelativeLocator.southEast) ::
        new FontSizeHandle(this, RelativeLocator.southWest) :: Nil
  }

  /**
   * @see org.jhotdraw.util.Storable#write(org.jhotdraw.util.StorableOutput)
   */
  override def write(dw: StorableOutput) {
    super.write(dw)
    val r: Rectangle = displayBox
    dw.writeInt(r.x)
    dw.writeInt(r.y)
    dw.writeString(getText)
    dw.writeString(fFont.getName)
    dw.writeInt(fFont.getStyle)
    dw.writeInt(fFont.getSize)
    dw.writeBoolean(fIsReadOnly)
    dw.writeStorable(getObservedFigure)
    dw.writeStorable(getLocator)
  }

  /**
   * @see org.jhotdraw.util.Storable#read(org.jhotdraw.util.StorableInput)
   */
  override def read(dr: StorableInput) {
    super.read(dr)
    markDirty
    basicDisplayBox(new Point(dr.readInt, dr.readInt), null)
    setText(dr.readString)
    fFont = new Font(dr.readString, dr.readInt, dr.readInt)
    fIsReadOnly = dr.readBoolean
    setObservedFigure(dr.readStorable.asInstanceOf[Figure])
    if (getObservedFigure != null) {
      getObservedFigure.addFigureChangeListener(this)
    }
    setLocator(dr.readStorable.asInstanceOf[OffsetLocator])
  }

  private def readObject(s: ObjectInputStream) {
    s.defaultReadObject
    if (getObservedFigure != null) {
      getObservedFigure.addFigureChangeListener(this)
    }
    markDirty
  }

  /**
   * @see org.jhotdraw.standard.TextHolder#connect(org.jhotdraw.framework.Figure)
   */
  def connect(figure: Figure) {
    if (getObservedFigure != null) {
      getObservedFigure.removeFigureChangeListener(this)
    }
    setObservedFigure(figure)
    setLocator(new OffsetLocator(getObservedFigure.connectedTextLocator(this)))
    getObservedFigure.addFigureChangeListener(this)
    willChange
    updateLocation
    changed
  }

  /**
   * @see org.jhotdraw.framework.FigureChangeListener#figureChanged(org.jhotdraw.framework.FigureChangeEvent)
   */
  def figureChanged(e: FigureChangeEvent) {
    willChange
    updateLocation
    changed
  }

  /**
   * @see org.jhotdraw.framework.FigureChangeListener#figureRemoved(org.jhotdraw.framework.FigureChangeEvent)
   */
  def figureRemoved(e: FigureChangeEvent) {
    if (listener != null) {
      val rect: Rectangle = invalidateRectangle(displayBox)
      listener.figureRemoved(new FigureChangeEvent(this, rect, e))
    }
  }

  /**
   * @see org.jhotdraw.framework.FigureChangeListener#figureRequestRemove(org.jhotdraw.framework.FigureChangeEvent)
   */
  def figureRequestRemove(e: FigureChangeEvent) {}

  /**
   * @see org.jhotdraw.framework.FigureChangeListener#figureInvalidated(org.jhotdraw.framework.FigureChangeEvent)
   */
  def figureInvalidated(e: FigureChangeEvent) {}

  /**
   * @see org.jhotdraw.framework.FigureChangeListener#figureRequestUpdate(org.jhotdraw.framework.FigureChangeEvent)
   */
  def figureRequestUpdate(e: FigureChangeEvent) {}

  /**
   * Updates the location relative to the connected figure.
   * The TextFigure is centered around the located point.
   */
  protected def updateLocation {
    if (getLocator != null) {
      val p: Point = getLocator.locate(getObservedFigure)
      p.x -= size.width / 2 + fOriginX
      p.y -= size.height / 2 + fOriginY
      if (p.x != 0 || p.y != 0) {
        basicMoveBy(p.x, p.y)
      }
    }
  }

  /**
   * @see org.jhotdraw.framework.Figure#release()
   */
  override def release {
    super.release
    disconnect(getObservedFigure)
  }

  /**
   * Disconnects a text holder from a connect figure.
   * @see org.jhotdraw.standard.TextHolder#disconnect(org.jhotdraw.framework.Figure)
   */
  def disconnect(disconnectFigure: Figure) {
    if (disconnectFigure != null) {
      disconnectFigure.removeFigureChangeListener(this)
    }
    setLocator(null)
    setObservedFigure(null)
  }

  protected def setObservedFigure(newObservedFigure: Figure) {
    fObservedFigure = newObservedFigure
  }

  def getObservedFigure: Figure = fObservedFigure

  protected def setLocator(newLocator: OffsetLocator) {
    fLocator = newLocator
  }

  protected def getLocator: OffsetLocator = fLocator

  /**
   * @see org.jhotdraw.framework.Figure#getTextHolder()
   */
  override def getTextHolder: TextHolder = this
}

