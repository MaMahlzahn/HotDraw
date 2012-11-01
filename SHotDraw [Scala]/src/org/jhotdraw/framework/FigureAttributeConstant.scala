package org.jhotdraw.framework
import org.jhotdraw.figures.FigureAttributes
import java.awt.Color

trait FigureAttributeConstant[T] {
  def setAttribute(figAttr: FigureAttributes, value: T)
  def getAttribute(figAttr: FigureAttributes): T
}

case object FrameColor extends FigureAttributeConstant[Color] {
  def setAttribute(figAttr: FigureAttributes, value: Color) {
    figAttr.setFrameColor(value)
  }
  def getAttribute(figAttr: FigureAttributes): Color = figAttr.getFrameColor
}

case object FillColor extends FigureAttributeConstant[Color] {
  def setAttribute(figAttr: FigureAttributes, value: Color) {
    figAttr.setFillColor(value)
  }
  def getAttribute(figAttr: FigureAttributes): Color = figAttr.getFillColor
}

case object TextColor extends FigureAttributeConstant[Color] {
  def setAttribute(figAttr: FigureAttributes, value: Color) {
    figAttr.setTextColor(value)
  }
  def getAttribute(figAttr: FigureAttributes): Color = figAttr.getTextColor
}

case object ArrowMode extends FigureAttributeConstant[Int] {
  def setAttribute(figAttr: FigureAttributes, value: Int) {
    figAttr.setArrowMode(value)
  }
  def getAttribute(figAttr: FigureAttributes): Int = figAttr.getArrowMode
}

case object FontName extends FigureAttributeConstant[String] {
  def setAttribute(figAttr: FigureAttributes, value: String) {
    figAttr.setFontName(value)
  }
  def getAttribute(figAttr: FigureAttributes): String = figAttr.getFontName
}

case object FontSize extends FigureAttributeConstant[Int] {
  def setAttribute(figAttr: FigureAttributes, value: Int) {
    figAttr.setFontSize(value)
  }
  def getAttribute(figAttr: FigureAttributes): Int = figAttr.getFontSize
}

case object FontStyle extends FigureAttributeConstant[Int] {
  def setAttribute(figAttr: FigureAttributes, value: Int) {
    figAttr.setFontStyle(value)
  }
  def getAttribute(figAttr: FigureAttributes): Int = figAttr.getFontStyle
}