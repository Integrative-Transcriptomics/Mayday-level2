/*
 * modified by Guenter Jaeger
 * on May 22, 2010
 */
package mayday.expressionmapping.controller;


import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.JSplitPane;

import mayday.core.settings.Setting;
import mayday.expressionmapping.gnu_trove_adapter.TIntArrayList;
import mayday.expressionmapping.gnu_trove_adapter.TIntObjectHashMap;
import mayday.expressionmapping.gnu_trove_adapter.TIntObjectIterator;
import mayday.expressionmapping.model.geometry.ClusterPoint;
import mayday.expressionmapping.model.geometry.DataPoint;
import mayday.expressionmapping.model.geometry.Point;
import mayday.expressionmapping.model.geometry.container.PointList;
import mayday.expressionmapping.view.WindowFactory;
import mayday.expressionmapping.view.information.AttractorTableWindow;
import mayday.expressionmapping.view.information.AttractorWindow;
import mayday.expressionmapping.view.information.InfoWindow;
import mayday.expressionmapping.view.plot.ExpressionSimplexBaseJOGL;
import mayday.expressionmapping.view.ui.EMViewSetting;
import mayday.expressionmapping.view.ui.PlotSplitPane;
import mayday.vis3.gui.PlotWindow;
import mayday.vis3.model.Visualizer;

/**
 * @author Stephan Gade
 */
public class MainFrame extends PlotWindow implements Runnable, WindowListener {
	private static final long serialVersionUID = -9208446051480186315L;
	/**
	 * 
	 */
	public static final int DATA = 0;
	/**
	 * 
	 */
	public static final int CLUSTER = 1;

	/*
	 * Variable Declaration
	 */
	private PointList<? extends Point> points;
	private int type;
	private boolean shutdown;
	private List<String> annotations = null;
	private ExpressionSimplexBaseJOGL simplex;
	private AttractorWindow attractorWindow;
	private EMViewSetting viewSettings;
	private TIntObjectHashMap<InfoWindow> infoWindowMap;
	private TIntObjectHashMap<AttractorTableWindow> mainAttracTableMap;
	private TIntObjectHashMap<AttractorTableWindow> attracTableMap;

	/*
	 * the main controller
	 */
	private MainController master;

	/**
	 * @param plotComponent 
	 * @param viz 
	 */
	public MainFrame(Visualizer viz) {
		super(new PlotSplitPane(JSplitPane.HORIZONTAL_SPLIT), viz);
		
		((JSplitPane)content).setContinuousLayout(true);
		((JSplitPane)content).setDividerLocation(0.5);
		/*
		 * initialite the mappings for the infoirmation windows
		 */
		this.infoWindowMap = new TIntObjectHashMap<InfoWindow>();
		this.mainAttracTableMap = new TIntObjectHashMap<AttractorTableWindow>();
		this.attracTableMap = new TIntObjectHashMap<AttractorTableWindow>();
        /*
         * add the window listener to the main window
         */
        this.addWindowListener(this);
		/*
		 * set the shutdown state
		 */
		this.shutdown=false;
		
		this.setPreferredTitle("Expression Mapping", null);
	}

	/**
	 * @return point list
	 */
	public PointList<? extends Point> getPoints() {
		return this.points;
	}

	/**
	 * @return dimension
	 */
	public int getDimension() {
		return this.points.getDimension();
	}

	/**
	 * @return true if annotations list set, else false
	 */
	public boolean isAnnotationsListSet() {
		return (this.annotations != null);
	}

	/**
	 * @return annotations
	 */
	public List<String> getAnnotations() {
		return this.annotations;
	}

	/**
	 * @param index
	 */
	public void runInformationWindow(int index) {
		if (!this.infoWindowMap.contains(index)) {
			switch (this.type) {
				case MainFrame.DATA: {
					createDataInformationWindow(index);
					break;
				}
				case MainFrame.CLUSTER: {
					createClusterInformationWindow(index);
					break;
				}
			}
		} else {
			this.infoWindowMap.get(index).requestFocus();
		}
	}

	private void createDataInformationWindow(int index) {
		String title = null;

		StringBuffer information = null;

		String[] groupLabels = this.points.getGroupLabels();

		DataPoint selectedPoint = (DataPoint) points.get(index);

		int pointID = selectedPoint.getID();

		title = "Information for Data point: " + (pointID + 1);

		information = new StringBuffer();

		/* retrieve information about the data point
		 */
		double[] tmpValues = selectedPoint.getValues();
		double[] tmpCoordinates = selectedPoint.getCoordinates();

		information.append("<html>");
		information.append("ProbeID:  " + (pointID + 1));

		if (this.annotations != null) {
			information.append("<p/>" + "Annotation: " + annotations.get(index));
		} else {
			information.append("<p/>" + "Annotation: Probe " + (pointID + 1) + " (unspecified)");
		}
		information.append("<br><br>");

		information.append("<table border=\"1\">");

		/* create head of table with group labels
		 */
		information.append("<thead>");
		information.append("<th></th>");
		for (int i = 0; i < groupLabels.length; ++i) {
			information.append("<th>" + groupLabels[i] + "</th>");
		}
		information.append("</thead>");

		/* table body
		 */
		information.append("<tbody>");

		/* first row: coordinate
		 */
		information.append("<tr>");
		information.append("<td>Coordinates</td>");
		for (int i = 0; i < tmpCoordinates.length; ++i) {
            double temp = Math.round(tmpCoordinates[i]*1000);
            temp /= 1000;
			information.append("<td>" + temp + "</td>");
		}
		information.append("</tr>");

		/* second row: values
		 */
		information.append("<tr>");
		information.append("<td>Values</td>");
		for (int i = 0; i < tmpValues.length; ++i) {
            double temp = Math.round(tmpValues[i]*1000);
            temp /= 1000;
			information.append("<td>" + temp + "</td>");
		}
		information.append("</tr>");

		information.append("</tbody>");

		information.append("</table>");
		information.append("</html>");

		InfoWindow infWindow = WindowFactory.createInfoWindow(this, index, title);
		infWindow.setInformation(information.toString());
		this.infoWindowMap.put(index, infWindow);
		infWindow.run();
	}

	private void createClusterInformationWindow(int index) {
		String title = null;

		StringBuffer information = null;

		String[] groupLabels = this.points.getGroupLabels();

		ClusterPoint selectedCluster = (ClusterPoint) points.get(index);

		int clusterID = selectedCluster.getID();

		title = "Information for Cluster: " + (clusterID + 1);

		information = new StringBuffer();

		/* retrieve information about the cluster point
		 */
		double[] tmpCoordinates = selectedCluster.getCoordinates();
		int[] pointIndices = selectedCluster.getMemberList();

		information.append("<html>");
		information.append("ClusterID:  " + (clusterID + 1));

		information.append("<br><br>");

		information.append("<table border=\"1\">");

		/* create head of table with group labels
		 */
		information.append("<thead>");
		information.append("<th></th>");
		for (int i = 0; i < groupLabels.length; ++i) {
			information.append("<th>" + groupLabels[i] + "</th>");
		}
		information.append("</thead>");

		/* table body
		 */
		information.append("<tbody>");

		/* first row: coordinate
		 */
		information.append("<tr>");
		information.append("<td>Coordinates</td>");
		for (int i = 0; i < tmpCoordinates.length; ++i) {
			//double temp = Math.round(tmpCoordinates[i]*1000)/1000;
			//information.append("<td>" + temp + "</td>");
			information.append("<td>" + tmpCoordinates[i] + "</td>");
		}
		information.append("</tr>");

		information.append("</tbody>");
		information.append("</table>");

		information.append("<br><br>");

		information.append("<p/>Members:");

		information.append("<table border=\"1\"> <thead><th>ID</th>");

		information.append("<th>Annotation</th></thead><tbody>");

		if (this.annotations != null) {
			for (int i = 0; i < pointIndices.length; ++i) {
				information.append("<tr>");
				information.append("<td>" + (pointIndices[i] + 1) + "</td>");

				information.append("<td>" + annotations.get(pointIndices[i]) + "</td>");
				information.append("</tr>");
			}
		} else {
			for (int i = 0; i < pointIndices.length; ++i) {

				information.append("<tr>");
				information.append("<td>" + (pointIndices[i] + 1) + "</td>");

				information.append("<td>(unspecified)</td>");
				information.append("</tr>");
			}
		}

		information.append("</tbody></table>");

		information.append("</html>");

		InfoWindow infWindow = WindowFactory.createInfoWindow(this, index, title);
		infWindow.setInformation(information.toString());
		this.infoWindowMap.put(index, infWindow);
		infWindow.run();
	}

	private void closeInfoWindows() {
		for (TIntObjectIterator<InfoWindow> iter = this.infoWindowMap.iterator(); iter.hasNext();) {
			/*
			 * get the next info Window
			 */
			iter.advance();
			/*
			 * close the info window
			 */
			iter.value().setVisible(false);
			iter.value().dispose();
		}
	}
	/**
	 * @param id
	 */
	public void removeInfoWindow(int id) {
		this.infoWindowMap.remove(id);
	}

	/**
	 * @param id
	 */
	public void removeAttracTable(int id) {
		this.attracTableMap.remove(id);
	}

	/**
	 * @param id
	 */
	public void removeMainAttracTable(int id) {
		this.mainAttracTableMap.remove(id);
	}

	/**
	 * @param id
	 * @param mouseButton
	 */
	public void signalAttractorClick(int id, int mouseButton) {
		/* 
		 * if clicked attractor is middle attractor ?
		 * id must be 2^2-2
		 */
		if (id == (int) (Math.pow(2, this.getDimension()) - 2)) {
			TIntArrayList accessList = this.points.getAttractorPoints(id);

			//simplex.colorPoints(accessList, new ColorPack(redMap, -1), null, null);
			simplex.changeProbeSelection(accessList);

			if(mouseButton == MouseEvent.BUTTON3) {
				if (!this.attracTableMap.contains(id)) {
					String title = new String("Points (" + accessList.size() + ") of the Attractor " + (id + 1) + " (Middle Attractor)");

					AttractorTableWindow attractorTable = WindowFactory.createAttractorTable(this, id, title);
					attractorTable.setMainAccessList(accessList);
					this.attracTableMap.put(id, attractorTable);
					attractorTable.run();
				} else {
					this.attracTableMap.get(id).requestFocus();
				}
			}
		} //clicked attractor is a "main attractor", an attractor corresponding to the edges
		else if (id < this.getDimension()) {
			int counterID = (int) (Math.pow(2, this.getDimension()) - 3 - id);

			TIntArrayList mainAccessList = this.points.getAttractorPoints(id);
			TIntArrayList subAccessyList = this.points.getAttractorPoints(counterID);

			simplex.changeProbeSelection(mainAccessList);

			if(mouseButton == MouseEvent.BUTTON3) {
				if (!this.attracTableMap.contains(id)) {
					String title = new String("Points (" + (mainAccessList.size() + subAccessyList.size()) + ") of the Attractor " + (id + 1) + " (" + this.points.getGroupLabels()[id] + ")");

					AttractorTableWindow attractorTable = WindowFactory.createAttractorTable(this, id, title);
					attractorTable.setMainAccessList(mainAccessList);
					attractorTable.setSubAccessList(subAccessyList);
					this.attracTableMap.put(id, attractorTable);
					attractorTable.run();
				} else {
					this.attracTableMap.get(id).requestFocus();
				}
			}
		} else {
			int counterID = id;
			id = (int) (Math.pow(2, this.getDimension()) - 3 - counterID);

			TIntArrayList mainAccessList = this.points.getAttractorPoints(id);
			TIntArrayList subAccessList = this.points.getAttractorPoints(counterID);

			simplex.changeProbeSelection(subAccessList);

			if(mouseButton == MouseEvent.BUTTON3) {
				if (!this.attracTableMap.contains(counterID)) {
					String title = new String("Points (" + (mainAccessList.size() + subAccessList.size()) + ") of the Attractor " + (counterID + 1) + " (" + this.points.getGroupLabels()[id] + ")");

					AttractorTableWindow attractorTable = WindowFactory.createAttractorTable(this, counterID, title);
					attractorTable.setMainAccessList(mainAccessList);
					attractorTable.setSubAccessList(subAccessList);
					this.attracTableMap.put(counterID, attractorTable);
					attractorTable.run();
				} else {
					this.attracTableMap.get(counterID).requestFocus();
				}
			}
		}
	}

	/**
	 * @param id
	 * @param mouseButton
	 */
	public void signalMainAttractorClick(int id, int mouseButton) {
//		ColorMap redMap = new ColorMap(Color.darkGray, Color.red);
//		redMap.setMinMax(0.25, 1);

//		ColorPack colPack = new ColorPack(redMap, id);

		TIntArrayList accessList = this.points.getMainAttractorPoints(id);

		//simplex.colorPoints(accessList, colPack, null, null);
		simplex.changeProbeSelection(accessList);

		if(mouseButton == MouseEvent.BUTTON3) {
			/*
			 * start the table window, if it is not open yet
			 */
			if (!this.mainAttracTableMap.contains(id)) {

				String title = new String("Points (" + accessList.size() + ") of the  Main Attractor " + (id + 1) + " (" + this.points.getGroupLabels()[id] + ")");

				AttractorTableWindow attractorTable = WindowFactory.createAttractorTable(this, id, title);
				attractorTable.setMainAccessList(accessList);
				this.mainAttracTableMap.put(id, attractorTable);
				attractorTable.run();

			} else {
				this.mainAttracTableMap.get(id).requestFocus();
			}
		}
	}

	/**
	 * 
	 */
	public void signalClearAttractor() {
		//simplex.resetPointColor();
		simplex.clearProbeSelection();
	}

	private void initComponents() {
		switch (this.type) {
			case MainFrame.DATA: {
				this.setMinimumSize(new Dimension(1200, 600));
				JSplitPane splitPane = (JSplitPane)content;
				splitPane.setOneTouchExpandable(true);

				simplex = WindowFactory.createExpressionSimplex(master.session.points);
				simplex.setPreferredSize(new Dimension(600, 600));

				attractorWindow = WindowFactory.createAttractorWindow(this);
				attractorWindow.setPreferredSize(new Dimension(600, 600));
				
				if(viewSettings == null){
					viewSettings = new EMViewSetting(this);
				}
				
				//add everything to the view menu
				for (Setting s : viewSettings.getChildren()){
					this.addViewSetting(s, simplex);
				}

				splitPane.setLeftComponent(attractorWindow);
				splitPane.setRightComponent(simplex);
				splitPane.setDividerLocation(0.5);
				break;
			}
			case MainFrame.CLUSTER: {
				this.setMinimumSize(new Dimension(600, 600));
				JSplitPane splitPane = (JSplitPane)content;
		
				simplex = WindowFactory.createExpressionSimplex(master.session.points);
				simplex.setPreferredSize(new Dimension(600, 600));
				
				splitPane.add(simplex);
				splitPane.setDividerSize(0);
				break;
			}
		}
	}

	private void plot() {
		simplex.plot();
	}

	/**
	 * shutdown
	 */
	public void shutdown() {
		this.shutdown=true;
		/*
		 * shut down all info windows
		 */
		this.closeInfoWindows();
		this.setVisible(false);
		this.dispose();
	}

	public void run() {
		/*
		 * set up all components and group them in the main window
		 */
		initComponents();
		plot();
		/*
		 * make the MainWindow visible
		 */
		this.setVisible(true);
	}
	
	/**
	 * update attractor window and simplex window
	 */
	public void updateComponents() {
		this.simplex.setSelectionColor(viewSettings.getSelectionColor());
		this.simplex.setPointSize(viewSettings.getPointSize());
		this.simplex.updatePlot();
		
		this.attractorWindow.setSelectionColor(viewSettings.getSelectionColor());
		this.attractorWindow.updatePlot();
	}

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		if(!this.shutdown)  {
			/*
			 * Perform Closing routine only when close with mouse click not with
			 * with shutdown
			 * we have to differentiate if this is a data or a cluster frame
			 * if the window showing the actual data, EM should be closed
			 * but it should be possible just to close the cluster window
			 */
			switch(this.type) {
				case MainFrame.DATA: {
//					if (JOptionPane.showConfirmDialog(this, "Exit ExpressionMapping?", "ExpressionMapping: Confirm exit",
//						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
						this.closeInfoWindows();
						this.setVisible(false);
						this.dispose();
						this.master.shutdown();
//					}
				}
				case MainFrame.CLUSTER: {
						this.closeInfoWindows();
						this.setVisible(false);
						this.dispose();
				}
			}
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	/**
	 * @param points
	 */
	public void setDataPoints(PointList<? extends Point> points) {
		this.points = points;
	}

	/**
	 * @param annotations
	 */
	public void setAnnotations(List<String> annotations) {
		this.annotations = annotations;
	}

	/**
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @param mainController
	 */
	public void setMainController(MainController mainController) {
		this.master = mainController;
	}
}
