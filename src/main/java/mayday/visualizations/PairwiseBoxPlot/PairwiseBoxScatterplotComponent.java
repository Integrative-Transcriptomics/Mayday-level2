package mayday.visualizations.PairwiseBoxPlot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mayday.core.ClassSelectionModel;
import mayday.core.Experiment;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.math.Statistics;
import mayday.core.plugins.probe.ProbeMenu;
import mayday.vis3.ColorProvider;
import mayday.vis3.SparseZBuffer;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.vis2base.ChartComponent;
import mayday.vis3.vis2base.DataSeries;
import mayday.vis3.vis2base.Shape;
import mayday.visualizations.Helpers;
import wsi.ra.chart2d.GraphicsModifier;

/**
 * Creates a visualization for two or more groups of experiments: each group is represented by a boxplot 
 * visualizing the distribution of the expression values of a specific probe within this group of experiments. 
 * The expression values are also visualized within the boxplots as a scatterplot. 
 * Single points can be selected and, if available, will be connected to the corresponding partner in the 
 * other group(s).
 * 
 * 
 * @author Alicia Owen
 *
 */
public class PairwiseBoxScatterplotComponent extends ChartComponent implements ViewModelListener, ProbeListListener {

	
	private static final long serialVersionUID = 1L;

	/**
	 * contains all selected data points 
	 */
	protected DataSeries selectionLayer;

	/**
	 * a list of lists containing the expression values per group. If paired = true, we can assume that elements 1 of list 1,2,3 ... are paired.
	 */
	private List<List<Double>> expressionValuesPerGroup;


	protected ProbeColoring probeColorSetter;
	/**
	 * maps every experiment to a coordinate in two-dimensional space
	 */
	Map<Experiment, double[]> mapExperimentsToCoordinates = new HashMap<Experiment, double[]>();;

	/**
	 * the class selection dialog allowing the user to define multiple groups
	 */
	private ClassSelectionModel classSelection;


	/**
	 * if groups contain identical number of elements, assume that data is paired (paired = true). If groups aren't equal in size, assume that data is not paired.
	 */
	private boolean paired = true;
	/**
	 * the probe that is visualized
	 */
	private Probe probe;
	
	private Map<Experiment,Integer > mapExperimentToIndex = new HashMap<Experiment,Integer>();

	protected SparseZBuffer szb = new SparseZBuffer();
	
	protected DataSeries[] Layers;
	protected Rectangle selRect;
	
	public PairwiseBoxScatterplotComponent(Probe probe, ClassSelectionModel classSelection) {
		this.classSelection = classSelection;
	
			this.probe = probe;
	
		setName("Pairwise box and scatter plot");
		setGrid(0.0, 0.2);
		setGridEmphasize(0.0, 1.0);

		//add mouse listeners
		addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				double[] clicked = getPoint(e.getX(), e.getY());
				switch (e.getButton()) {
				case MouseEvent.BUTTON1:
					Experiment exp = (Experiment)szb.getObject(clicked[0], clicked[1]);
					if (exp!=null) {
						if (e.getClickCount()==2) {
							PropertiesDialogFactory.createDialog(exp).setVisible(true);
						} else {
							int CONTROLMASK = getToolkit().getMenuShortcutKeyMask();
							if ((e.getModifiers()&CONTROLMASK) == CONTROLMASK) {
								// 			toggle selection of the clicked pexperiment
								viewModel.toggleExperimentSelected(exp);
							} else {
								// 			select only one experiment
								viewModel.setExperimentSelection(exp);
							}
						}
					}
					break;
				case MouseEvent.BUTTON3:
					ProbeMenu pm = new ProbeMenu(viewModel.getSelectedProbes(), viewModel.getDataSet().getMasterTable());
					pm.getPopupMenu().show(PairwiseBoxScatterplotComponent.this, e.getX(), e.getY());
					break;
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (selRect!=null) {
					System.out.println("Selecting points within: "+selRect);
					Graphics2D g = ((Graphics2D)farea.getGraphics());
					drawSelectionRectangle(g);
					int CONTROLMASK = getToolkit().getMenuShortcutKeyMask();
					boolean control = ((e.getModifiers()&CONTROLMASK) == CONTROLMASK);
					boolean alt = e.isAltDown();
					selectByRectangle(selRect, control, alt);
					selRect = null;
				}
			}
		});

		farea.addMouseMotionListener(new MouseMotionListener() {

			protected Point dragPoint;
			protected Point targPoint;

			public void mouseDragged(MouseEvent e) {					
				Graphics2D g = ((Graphics2D)farea.getGraphics());
				if (selRect==null) {
					dragPoint = e.getPoint();
				} else {
					drawSelectionRectangle(g);
				}
				targPoint = e.getPoint();
				selRect = new Rectangle(dragPoint, new Dimension(1,1));
				selRect.add(targPoint);					
				drawSelectionRectangle(g);					
			}

			public void mouseMoved(MouseEvent e) {
			}
		});


	}

	@Override
	public String getAutoTitleY(String ytitle) {
		String manip = viewModel.getDataManipulator().getManipulation().getDataDescription();
		if (manip.length()>0)
			manip = ", "+manip;
		return "Expression value"+manip;
	}

	@Override
	public String getAutoTitleX(String xtitle) {
		return "Experiment Groups";
	}


	/**
	 * a class representing the boxplot and allowing to paint it
	 */
	public class BoxShape extends Shape
	{
		double[] quartiles;
		Color c;
		ProbeList pl;

		public BoxShape(double[] quartiles, ProbeList pl)
		{
			this.quartiles = quartiles;
			this.pl = pl;
		}

		public void paint(Graphics2D g)
		{
			double[] ypos = new double[5];
			for (int i=0; i!=quartiles.length; ++i) 
				ypos[i]=(quartiles[i]-quartiles[0]); 	//dist to minimum. ypos[0]: bottom whisker; ypos[1]: botttom of box; ypos[2]: median; ypos[3]: top of box; ypos[4]: top whisker

			double width = .2;

			g.setColor(Color.BLACK);

			Line2D.Double line = new Line2D.Double();
			Rectangle2D.Double rect = new Rectangle2D.Double();

			// min-max line
			Graphics2D g2 = (Graphics2D)g;

			line.setLine(-width, ypos[4], width, ypos[4]); 				// top whisker
			g2.draw(line);

			line.setLine(0, ypos[4], 0, ypos[0]); 						// vertical line
			g2.draw(line);

			line.setLine(-width, ypos[0], width, ypos[0]); 				// bottom whisker
			g2.draw(line);

			g.setColor(Color.WHITE);			
			rect.setRect(-width, ypos[1], 2*width, ypos[3]-ypos[1]); 	// the box
			g2.fill(rect);
			//			g.setColor(pl.getColor());
			g.setColor(Color.BLACK);
			rect.setRect(-width, ypos[1], 2*width, ypos[3]-ypos[1]);
			g2.draw(rect);

			line.setLine(-width, ypos[2], width, ypos[2]);				// the median
			g2.draw(line);

		}
	}

	/**
	 * creates the components that will appear on the chart
	 */
	public void createView() 
	{

		double maxY = 0;	
		double minX = 1;
		double maxX;

		int prevGroupSize = 0;
		int groupSize;

		expressionValuesPerGroup  = new ArrayList<List<Double>>();
		
		//extract the expression values for each group that is defined by the user's class selection
		for(int i = 0; i < classSelection.getNumClasses(); i++){
			if(i > 0){
				prevGroupSize = expressionValuesPerGroup.get(i-1).size();
			}
			List<Double> groupExpressionValues = getGroupExpressionValuesFromIndex(classSelection.toIndexList(classSelection.getClassesLabels().get(i)));
			expressionValuesPerGroup.add(groupExpressionValues);	
			groupSize = expressionValuesPerGroup.get(i).size();

			if(i > 0 && (prevGroupSize != groupSize)){
				paired = false;
			}
		}

		maxX = expressionValuesPerGroup.size();
		DataSeries series_arr[] = new DataSeries[(int)maxX];
		//construct the boxplots visualizing the expression value distribution within the  groups
		int i = 1;

		for(List<Double> values : expressionValuesPerGroup){

			double[] quartiles = getQuartiles(values);

			if (Double.isNaN(quartiles[0]) || Double.isNaN(quartiles[1]) || Double.isNaN(quartiles[2]) ||
					Double.isNaN(quartiles[3]) || Double.isNaN(quartiles[4])) {
				// nothing to paint
			}else{
				series_arr[i-1] = new DataSeries();
				series_arr[i-1].setShape(new BoxShape(getQuartiles(values), null));		//define the shape that will represent the data points
				series_arr[i-1].addPoint(i, getQuartiles(values)[0], null);				//set the point where the above shape shall appear
				series_arr[i-1].setConnected(true);

				addDataSeries(series_arr[i-1]);		//adds this DataSeries to the ChartComponent

				maxY = Math.max(maxY, getQuartiles(values)[4]);


			}
			i++;
		}

		// draw these invisible points to get a good grid
		DataSeries beauty = new DataSeries();

		beauty.addPoint(minX - .5, maxY+.1, null);
		beauty.addPoint(maxX + .5, maxY+.1, null);
		beauty.setColor(Color.WHITE);
		beauty.setConnected(true);			
		addDataSeries(beauty);

		setScalingUnitX(1.0);
		select(Color.RED);

		DataSeries ds = getDataSeriesFromExperiments(mapExperimentsToCoordinates.keySet());	
		ds.setColor(Color.blue);									//set the color of the scattered points
		ds.setAfterJumpModifier(probeColorSetter);
		addDataSeries(ds);


		//the labels for the boxplots on the x-axis: group <# elements contained>
		Map<Double, String> xLabels = new HashMap<Double, String>();

		for(int group = 1; group < expressionValuesPerGroup.size()+1; group ++){
			xLabels.put((double)group, classSelection.getClassNames().get(group-1)+" (" + classSelection.getClassCount(classSelection.getClassesLabels().get(group-1)) + ")");
		}

		setXLabeling(xLabels);
	}

	/**
	 * returns the expression values of the experiments indicated by indexList
	 * @param indexList a list of indices representing experiments
	 * @return the list of expression values for the experiments
	 */
	private List<Double> getGroupExpressionValuesFromIndex(List<Integer> indexList) {

		List<Double> expressionValues  = new ArrayList<Double>();	

		int xVal = expressionValuesPerGroup.size()+1; //the x-coordinate for the group


		for(int i = 0 ; i < indexList.size(); i++){

			Double expressionVal = probe.getValue(indexList.get(i));
			expressionValues.add(expressionVal);
			Experiment exp = viewModel.getDataSet().getMasterTable().getExperiment(indexList.get(i)); //get the experiment
			mapExperimentsToCoordinates.put(exp, new double[]{xVal, expressionVal});
			mapExperimentToIndex.put(exp, i);
		}


		return expressionValues;
	}

	/**
	 * This method draws the profile of selected probes into the chart
	 * @param selection_color the color in which the profile will be painted
	 */
	public void select(Color selection_color)
	{
		if (selectionLayer!=null)
			removeDataSeries(selectionLayer);

		Set<Experiment> s = new HashSet<Experiment>();

		s.addAll(viewModel.getSelectedExperiments());

		selectionLayer = viewProfile(s, paired); //get profiles for selected probes

		selectionLayer.setAfterJumpModifier(new ProbeColorSetter());
		selectionLayer.setStroke(new BasicStroke(3));
		addDataSeries(selectionLayer); //add the profiles to the chart
		selectionLayer.setColor(selection_color);
		clearBuffer();
		repaint();
	}

	/**
	 * Computes the profile of a set of probes and returns it as {@link DataSeries}
	 * @param pl a set of probes that should be visualized
	 * @return profile plot of the selected probes as an overlay over boxplots
	 */
	protected DataSeries viewProfile(Collection<Experiment> experiments, boolean paired) {

		DataSeries series = new DataSeries();
		series.setShape(new Shape() {
			public void paint(Graphics2D g) {				
				g.fillRect(-1,-1,3,3); // a square spanning form -1 to 1 in width and height 
			}
			public boolean wantDeviceCoordinates() {
				return true;
			}
		});			

		for(Experiment experiment: experiments) {	

			
			if(paired){
				int experimentPosition = mapExperimentToIndex.get(experiment);
				int i = 1;
				for(List<Double> groupExpressionVals : expressionValuesPerGroup){
					series.addDPoint((double)i, groupExpressionVals.get(experimentPosition));
					i++;
				}
				
			}else{
				series.addPoint(mapExperimentsToCoordinates.get(experiment)[0], mapExperimentsToCoordinates.get(experiment)[1], experiment);
			}


			series.jump();
		}
		if(paired){
			series.setConnected(true);
		}
		return series;
	}


	protected ColorProvider coloring; //knows the colors of the probes

	private class ProbeColorSetter implements GraphicsModifier {
		//modifies the color of the graphical representation of the probe
		public void modify(Graphics2D g, Object o) {
			if (o instanceof Probe)
				g.setColor(coloring.getColor((Probe)o));
		}		
	}

	/**
	 * sets the plotContainer up. For this it is necessary to define at  least two groups of experiments which is done using
	 * the ClassSelectionDialog. The container for the plot is given a name and listeners. 
	 * 
	 */
	public void setup(PlotContainer plotContainer) {
		super.setup(plotContainer);
		plotContainer.setPreferredTitle("Pairwise Boxplot", this);
		viewModel.addViewModelListener(this);
		viewModel.addRefreshingListenerToAllProbeLists(this,false);
		coloring = new ColorProvider(viewModel);	

		updatePlot();
	}

	public void viewModelChanged(ViewModelEvent vme) {
		if(vme.getChange() == ViewModelEvent.EXPERIMENT_SELECTION_CHANGED) {
			select(Color.red);
			
		}
		return;
	}

	public void probeListChanged(ProbeListEvent event) {
		switch (event.getChange()) {
		case ProbeListEvent.LAYOUT_CHANGE: //fall
		case ProbeListEvent.CONTENT_CHANGE:
			updatePlot();
			break;
		}
	}

	public void removeNotify() {
		super.removeNotify();
		if (coloring!=null)
			coloring.removeNotify();
		viewModel.removeViewModelListener(this);
		viewModel.removeRefreshingListenerToAllProbeLists(this);
	}

	protected class IterableExperiment implements Iterable<Double>, Iterator<Double> {

		int experiment;
		Iterator<Probe> pb;

		public IterableExperiment(ProbeList pl, int experiment) {
			this.experiment=experiment;
			pb = pl.getAllProbes().iterator();
		}

		public Iterator<Double> iterator() {
			return this;
		}

		public boolean hasNext() {
			return pb.hasNext();
		}

		public Double next() {
			return pb.next().getValue(experiment);
		}

		public void remove() {
		}

	}

	/**
	 * Computes the 4 quartiles of a given set of data points.
	 * @param values the array of values
	 * @return ann array with the 4 quartiles
	 */
	private double[] getQuartiles(List<Double> values){

		double q0 ,q1, q2, q3, q4;
		double median = Statistics.median(values);
		q0 = Helpers.findMin(values);
		q1 = Statistics.median(Helpers.valuesSmallerThan(values, median, true));
		q2 = median;
		q3 = Statistics.median(Helpers.valuesGreaterThan(values, median, true));
		q4 = Helpers.findMax(values);

		return new double[] {q0, q1, q2, q3, q4};

	}

	// ----------------------------- scatter plot methods -----------------------------------//



	public DataSeries getDataSeriesFromExperiments(Collection<Experiment> experiments) {

		DataSeries ds = new DataSeries();

		for (Experiment experiment : experiments) {

			double xx = mapExperimentsToCoordinates.get(experiment)[0];
			double yy = mapExperimentsToCoordinates.get(experiment)[1];
			ds.addPoint(xx, yy, experiment);
			szb.setObject(xx, yy, experiment);
		}

		return ds;
	}


	/**
	 * 
	 * @param r
	 * @param control
	 * @param alt
	 */
	protected void selectByRectangle(Rectangle r, boolean control, boolean alt) {

		Set<Experiment> newSelection = new HashSet<Experiment>();

		double[] clicked1 = getPoint(r.x, r.y);						//the grid coordinate for the rectangles left bottom corner
		double[] clicked2 = getPoint(r.x+r.width, r.y+r.height);	//the grid coordinate for the rectangles top right corner

		//iterate over all experiments and find out whether they are within the selection range of the rectangle
		for(Experiment exp : mapExperimentsToCoordinates.keySet()){
			double xVal = mapExperimentsToCoordinates.get(exp)[0];
			double yVal = mapExperimentsToCoordinates.get(exp)[1];

			boolean inX = (xVal>=clicked1[0] && xVal<clicked2[0]);	//check whether this probe is within the borders of the rectangle
			boolean inY = (yVal<clicked1[1] && yVal>clicked2[1]);
			if (inX && inY)

				//add experiment to selection by adding index of the experiment, find it with indexGrp1.get(j)
				newSelection.add(exp);
		}


		/*
		 * selection modes: 
		 * - no modifier: replace
		 * - ctrl: union with previous selection
		 * - alt: intersect with previous selection
		 * - ctrl+alt: remove from previous selection
		 */

		Set<Experiment> previousSelection = viewModel.getSelectedExperiments();
		if (control && alt) {
			previousSelection = new HashSet<Experiment>(previousSelection);
			previousSelection.removeAll(newSelection);
			newSelection = previousSelection;
		} else if (control) {
			newSelection.addAll(previousSelection);
		} else if (alt) {
			newSelection.retainAll(previousSelection);
		} else {
			// nothing to do with prev selection
		}

		viewModel.setExperimentSelection(newSelection);
	}


	private class ProbeColoring implements GraphicsModifier {
		public void modify(Graphics2D g, Object o) {
			if (o instanceof Probe)
				g.setColor(coloring.getColor((Probe)o));
		}		
	}

	/**
	 * 
	 * @param g
	 */
	protected void drawSelectionRectangle(Graphics2D g) {
		if (selRect==null)
			return;
		g.setXORMode(getBackground()); 
		g.setColor(Color.RED);				//selection rectangle is painted red
		Stroke oldStroke = g.getStroke();	
		g.setStroke(new BasicStroke(2));	//using BasicStroke (2)
		g.draw(selRect);					//rectangle is drawn	
		g.setStroke(oldStroke);				//the stroke is returned to its original type
		g.setPaintMode();	
		return;
	}


}