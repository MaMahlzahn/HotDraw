/*
 * @(#)FigureEnumerator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package org.jhotdraw.standard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jhotdraw.framework.Figure;
import org.jhotdraw.framework.FigureEnumeration;


/**
 * An Enumeration for a Collection of Figures.
 *
 * @version <$CURRENT_VERSION$>
 */
public final class FigureEnumerator implements FigureEnumeration {
	private Iterator<Figure> myIterator;
	private Collection<Figure> myInitialCollection;

	private static FigureEnumerator singletonEmptyEnumerator =
		new FigureEnumerator(new ArrayList<Figure>());

	public FigureEnumerator(Collection<Figure> c) {
		myInitialCollection = c;
		reset();
	}

	/**
	 * Returns true if the enumeration contains more elements; false
	 * if its empty.
	 */
	public boolean hasNextFigure() {
		return myIterator.hasNext();
	}

	/**
	 * Returns the next element of the enumeration. Calls to this
	 * method will enumerate successive elements.
	 * @exception java.util.NoSuchElementException If no more elements exist.
	 */
	public Figure nextFigure() {
		return myIterator.next();
	}

	public static FigureEnumeration getEmptyEnumeration() {
		return singletonEmptyEnumerator;
	}

	/**
	 * Reset the enumeration so it can be reused again. However, the
	 * underlying collection might have changed since the last usage
	 * so the elements and the order may vary when using an enumeration
	 * which has been reset.
	 */
	public void reset() {
		myIterator = myInitialCollection.iterator();
	}
	
	public FigureEnumerator addAll(FigureEnumerator fe) {
		List<Figure> tmp = new ArrayList<Figure>(myInitialCollection);
		tmp.addAll(fe.myInitialCollection);
		return new FigureEnumerator(tmp);
	}
}
