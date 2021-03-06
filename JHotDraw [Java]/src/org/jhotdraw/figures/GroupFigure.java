/*
 * @(#)GroupFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package org.jhotdraw.figures;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.jhotdraw.framework.FigureAttributeConstant;
import org.jhotdraw.framework.FigureEnumeration;
import org.jhotdraw.framework.Handle;
import org.jhotdraw.framework.HandleEnumeration;
import org.jhotdraw.standard.CompositeFigure;
import org.jhotdraw.standard.FigureEnumerator;
import org.jhotdraw.standard.HandleEnumerator;
import org.jhotdraw.standard.RelativeLocator;

/**
 * A Figure that groups a collection of figures.
 *
 * @version <$CURRENT_VERSION$>
 */
public  class GroupFigure extends CompositeFigure {

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = 8311226373023297933L;

   /**
	* GroupFigures cannot be connected
	*/
	public boolean canConnect() {
		return false;
	}

   /**
	* Gets the display box. The display box is defined as the union
	* of the contained figures.
	*/
	public Rectangle displayBox() {
		FigureEnumeration fe = figures();
		Rectangle r = new Rectangle();

		while (fe.hasNextFigure()) {
			r.add(fe.nextFigure().displayBox());
		}
		return r;
	}

	public void basicDisplayBox(Point origin, Point corner) {
		// do nothing
		// we could transform all components proportionally
	}

	public FigureEnumeration decompose() {
		return new FigureEnumerator(fFigures);
	}

   /**
	* Gets the handles for the GroupFigure.
	*/
	public HandleEnumeration handles() {
		List<Handle> handles = new ArrayList<Handle>();
		handles.add(new GroupHandle(this, RelativeLocator.northWest()));
		handles.add(new GroupHandle(this, RelativeLocator.northEast()));
		handles.add(new GroupHandle(this, RelativeLocator.southWest()));
		handles.add(new GroupHandle(this, RelativeLocator.southEast()));
		return new HandleEnumerator(handles);
	}

   /**
	* Sets the attribute of all the contained figures.
	* @deprecated see setAttribute(FigureAttributeConstant,Object)
	*/
	public void setAttribute(String name, Object value) {
		super.setAttribute(name, value);
		FigureEnumeration fe = figures();
		while (fe.hasNextFigure()) {
			fe.nextFigure().setAttribute(name, value);
		}
	}

	/**
	 * Sets the attribute of the GroupFigure as well as all contained Figures.
	 */
	public void setAttribute(FigureAttributeConstant fac, Object object){
		super.setAttribute(fac, object);
		FigureEnumeration fe = figures();
		while (fe.hasNextFigure()) {
			fe.nextFigure().setAttribute(fac, object);
		}		
	}
}
