/*
 * modified by Guenter Jaeger
 * on May 19th, 2010
 */
package mayday.expressionmapping.view;

import mayday.expressionmapping.controller.MainFrame;
import mayday.expressionmapping.model.geometry.Point;
import mayday.expressionmapping.model.geometry.container.PointList;
import mayday.expressionmapping.view.information.AttractorTableWindow;
import mayday.expressionmapping.view.information.AttractorWindow;
import mayday.expressionmapping.view.information.AttractorWindow2D;
import mayday.expressionmapping.view.information.AttractorWindow3D;
import mayday.expressionmapping.view.information.AttractorWindow4D;
import mayday.expressionmapping.view.information.InfoWindow;
import mayday.expressionmapping.view.plot.ExpressionSimplex2DJOGL;
import mayday.expressionmapping.view.plot.ExpressionSimplex3DJOGL;
import mayday.expressionmapping.view.plot.ExpressionSimplex4DJOGL;
import mayday.expressionmapping.view.plot.ExpressionSimplexBaseJOGL;

/**
 * @author Stephan Gade
 * 
 */
public class WindowFactory {
	/**
	 * @param master
	 * @param id
	 * @param title
	 * @return InfoWindow
	 */
	public static InfoWindow createInfoWindow(MainFrame master, int id, String title) {
		return new InfoWindow(master, id, title);
	}

	/**
	 * @param points
	 * @return ExpressionSimplex
	 */
	public static ExpressionSimplexBaseJOGL createExpressionSimplex(PointList<? extends Point> points) {
		switch (points.getDimension()) {
		case 2:
			return new ExpressionSimplex2DJOGL(points);
		case 3:
			return new ExpressionSimplex3DJOGL(points);
		case 4:
			return new ExpressionSimplex4DJOGL(points);
		default: {
			System.err.println("The given dimension "
						+ points.getDimension()
						+ " doesn't match to any ExpressionSimplex. Returning null!");
			return null;
		}
		}
	}

	/**
	 * @param master
	 * @return AttractorWindow
	 */
	public static AttractorWindow createAttractorWindow(MainFrame master) {
		switch (master.getDimension()) {
		case 2:
			return new AttractorWindow2D(master);
		case 3:
			return new AttractorWindow3D(master);
		case 4:
			return new AttractorWindow4D(master);
		default: {
			System.err.println("The given dimension " + master.getDimension()
					+ " doesn't match to any AttractorWindow. Returning null.");
			return null;
		}
		}
	}

	/**
	 * @param master
	 * @param id
	 * @param title
	 * @return AttractorTableWindow
	 */
	public static AttractorTableWindow createAttractorTable(MainFrame master,
			int id, String title) {
		return new AttractorTableWindow(title, id, master);
	}
}
