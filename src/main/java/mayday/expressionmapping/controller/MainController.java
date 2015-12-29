package mayday.expressionmapping.controller;

import java.util.ArrayList;
import java.util.List;

import mayday.core.ClassSelectionModel;
import mayday.core.MasterTable;
import mayday.core.ProbeList;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.math.distance.measures.ManhattanDistance;
import mayday.core.tasks.AbstractTask;
import mayday.expressionmapping.gnu_trove_adapter.TIntArrayList;
import mayday.expressionmapping.io.IOFactory;
import mayday.expressionmapping.io.reader.AnnotationReaderInterface;
import mayday.expressionmapping.io.reader.AnnotationSource;
import mayday.expressionmapping.io.reader.DataSource;
import mayday.expressionmapping.io.reader.ExpressionReaderInterface;
import mayday.expressionmapping.model.algorithm.AlgorithmFactory;
import mayday.expressionmapping.model.algorithm.cluster.ClusterAlgorithm;
import mayday.expressionmapping.model.algorithm.transform.TransformAlgorithm;
import mayday.expressionmapping.model.geometry.DataPoint;
import mayday.expressionmapping.model.geometry.container.PointList;
import mayday.expressionmapping.view.ui.EMSettings;
import mayday.vis3.model.Visualizer;

/**
 * @author jaeger
 *
 */
public class MainController implements Runnable {
	protected DataSet session;

	private IOFactory ioFactory;

	private boolean shutdown = false;

	private EMSettings settings;
	private MainFrame dataFrame;
	private MainFrame clusterFrame;
	
	private MasterTable masterTable;

	/**
	 * @param probes
	 * @param masterTable
	 * @param settings
	 */
	public MainController(ProbeList probes, MasterTable masterTable,
			EMSettings settings) {
		
		this.masterTable = masterTable;
		/*
		 * Initiate the data set for this session, it carries all neccesary data
		 */
		this.session = new DataSet();
		this.settings = settings;

		/*
		 * set the ProbeList and the MasterTable
		 */
		this.session.probes = probes;
		this.session.masterTable = masterTable;

		/*
		 * set the threee windows, optionsDialog, dataFrame and clusterFrame to
		 * null
		 */
		this.dataFrame = null;

		/*
		 * create an IOFactory using the ProbeList the factory is used to create
		 * the reader for the expression values and annotations
		 */
		this.ioFactory = new IOFactory(this.session.probes);
		
		this.validateGroupingInput();
	}
	
	/**
	 * @return master table
	 */
	public MasterTable getMasterTable() {
		return this.masterTable;
	}

	/**
	 * This is the main method of the MainController class. All neccessary steps
	 * to compute the simplex are performed here. Finnaly the simplex is
	 * started.
	 */
	@Override
	public void run() {
		/*
		 * check for shutdown of the main routine
		 */
		if (this.shutdown) {
			return;
		}
		/*
		 * read the the data
		 */
		this.readInput();
		/*
		 * start the calculation of the barycentric coordinates and the
		 * attractor frequencies
		 */
		this.transformRawData();
		/*
		 * start the MainFrame and therewith the visualization of the data
		 */
		
		Visualizer viz = new Visualizer(masterTable.getDataSet(), null);
		viz.getViewModel().addProbeListToSelection(this.session.probes);
		this.dataFrame = new MainFrame(viz);
		this.dataFrame.setTitle(new String("Data from "
				+ this.session.probes.getName() + " ("
				+ this.session.points.size() + " data points)"));
		this.dataFrame.setDataPoints(this.session.points);
		this.dataFrame.setAnnotations(this.session.annotations);
		this.dataFrame.setType(MainFrame.DATA);
		this.dataFrame.setMainController(this);
		viz.addPlot(this.dataFrame);
		this.dataFrame.run();
		
		dataFrame.run();
		/*
		 * cluster the data if desired
		 */
		if (settings.getClusteringAlgorithm() > 0) {
			this.clusterData();

			Visualizer viz2 = new Visualizer(masterTable.getDataSet(), null);
			viz2.getViewModel().addProbeListToSelection(this.session.probes);
			this.clusterFrame = new MainFrame(viz2);
			this.clusterFrame.setTitle(new String("Cluster from "
					+ this.session.probes.getName() + " ("
					+ this.session.clusters.size() + " cluster points)"));
			this.clusterFrame.setDataPoints(session.clusters);
			this.clusterFrame.setType(MainFrame.CLUSTER);
			this.clusterFrame.setMainController(this);
			viz2.addPlot(this.clusterFrame);
			
			clusterFrame.run();
		}
	}
	
	/**
	 * perform shutdown on all necessary components
	 */
	public void shutdown() {
		/*
		 * shutdown initiated
		 */
		this.shutdown = true;
		/*
		 * close data frame
		 */
		if (this.dataFrame != null && this.dataFrame.isVisible()) {
			this.dataFrame.shutdown();
		}
		/*
		 * close the cluster frame
		 */
		if (this.clusterFrame != null && this.clusterFrame.isVisible()) {
			this.clusterFrame.shutdown();
		}
	}

	private void validateGroupingInput() {
		ClassSelectionModel model = settings.getClassSelectionModel();

		int numClasses = model.getNumClasses();
		session.groupMappings = new ArrayList<TIntArrayList>();
		
		for(int i = 0; i < numClasses; i++) {
			TIntArrayList mapping = new TIntArrayList();
			mapping.addAll(model.toIndexList(i));
			session.groupMappings.add(mapping);
		}
		
		session.groupNames = new ArrayList<String>(model.getClassesLabels());
		session.groupMode = settings.getCombiningMethod();
	}
	
	private void readInput() {

		/*
		 * create sources for the data and the probe annotations
		 */
		DataSource source = ioFactory.getDataSource();
		AnnotationSource annoSource = ioFactory.getAnnotationSource();

		/*
		 * use these sources to create the reader
		 */
		ExpressionReaderInterface expReader = this.ioFactory
				.getExpressionReader(source, this.session.groupMappings,
						this.session.groupMode);
		AnnotationReaderInterface annoReader = this.ioFactory
				.getAnnotationReader(annoSource);

		/*
		 * get the data and annotations from the reader
		 */
		List<DataPoint> dataPoints = expReader.readExpressionValues();
		this.session.points = new PointList<DataPoint>(dataPoints,
				this.session.masterTable.getExperimentNames());
		this.session.annotations = annoReader.readAnnotations();

	}

	/**
	 * Transforms the raw data, that is compute the barycentric coordinates and
	 * computes the attractor frequencies. Both can be seen as transformations
	 * of the PointList holding the data points.
	 */
	private void transformRawData() {
		/*
		 * get the computer for the barycentric coordinates and the nearest
		 * attractors
		 */
		TransformAlgorithm baryCompute = AlgorithmFactory
				.getTransformAlgorithm(settings.getBarycentricCoordsCompMethod());
		/*
		 * compute the attractor frequencies The distance is set to Manhattan.
		 * Only Euclidean and Manhattan makes sense, Manhattan is supposed to
		 * perform faster.
		 */
		TransformAlgorithm attracCompute = AlgorithmFactory
				.getAttractorComputer(Constants.ATTRAC, new ManhattanDistance());
		/*
		 * transform the points
		 */
		baryCompute.transform(this.session.points);
		attracCompute.transform(this.session.points);
	}

	/**
	 * In case a clustering is needed, this method performs a clustering of the
	 * data points. A new PointList is created containing the cluster points.
	 * 
	 * @param numberOfClusters
	 */
	private void clusterData() {
		DistanceMeasurePlugin manhattan = new ManhattanDistance();
		
		final ClusterAlgorithm clusterAlgo = AlgorithmFactory.getClusterAlgorithm(
				settings.getClusteringAlgorithm(), manhattan);

		TransformAlgorithm attracCompute = AlgorithmFactory
				.getAttractorComputer(Constants.ATTRAC, manhattan);

		AbstractTask cTask = new AbstractTask(clusterAlgo.getTitle()) {
			@Override
			protected void initialize() {}

			@Override
			protected void doWork() throws Exception {
				session.clusters = clusterAlgo
						.cluster(session.points, settings.getNumberOfClusters(),
								settings.getMaxNumberOfRounds());
			}
		};
		
		clusterAlgo.setClusterTask(cTask);
		
		cTask.start();
		cTask.waitFor();
		
		attracCompute.transform(this.session.clusters);
	}
}
