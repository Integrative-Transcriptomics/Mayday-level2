package mayday.wapiti.transformations.matrix;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.swing.JOptionPane;

import mayday.core.MaydayDefaults;
import mayday.core.io.ReadyBufferedReader;
import mayday.core.meta.MIGroup;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.filemanager.FileManager;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.structures.maps.MultiHashMap;
import mayday.core.structures.maps.MultiTreeMap;
import mayday.core.tasks.AbstractTask;
import mayday.core.tasks.ProgressListener;
import mayday.core.tasks.SubTaskProgressListener;
import mayday.core.tasks.TaskStateEvent;
import mayday.genetics.locusmap.LocusMapContainer;
import mayday.wapiti.containers.featuresummarization.FeatureSummarizationMapContainer;
import mayday.wapiti.containers.identifiermapping.IdentifierMapContainer;
import mayday.wapiti.experiments.base.AbstractExperimentSerializer;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.gui.ExperimentPanel;
import mayday.wapiti.gui.TransMatrixFrame;
import mayday.wapiti.gui.TransMatrixLayout;
import mayday.wapiti.gui.TransMatrixPane;
import mayday.wapiti.gui.layeredpane.ReorderableHorizontalPanel;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;
import mayday.wapiti.transformations.base.DatasetCreatingTransformation;
import mayday.wapiti.transformations.base.Transformation;


public class TransMatrix implements SettingChangeListener {

	protected MultiTreeMap<Experiment, Transformation> e2t = new MultiTreeMap<Experiment, Transformation>();
	protected MultiTreeMap<Transformation, Experiment> t2e = new MultiTreeMap<Transformation, Experiment>();
	protected HashSet<Experiment> emptyExperiments = new HashSet<Experiment>();
	protected HashMap<Transformation, Integer> minimumIndices = new HashMap<Transformation, Integer>();
	protected MultiHashMap<Integer, Transformation> minimumIndices_reverse = new MultiHashMap<Integer, Transformation>();
	protected TreeMap<Experiment, Integer> dsCreationCount = new TreeMap<Experiment, Integer>();
	protected HashMap<String, MIGroup> commonGroups = new HashMap<String, MIGroup>();

	protected Map<Experiment, ExperimentData> intermediateData;
	protected Map<Experiment, ExperimentState> intermediateState;
	
	protected boolean unhappinessIsWorthAnException = false;

	protected TransMatrixPane transmatrixPane;
	protected TransMatrixFrame transmatrixFrame; 
	
	public TransMatrix() {
		transmatrixPane = new TransMatrixPane(this);
		transmatrixFrame = new TransMatrixFrame(this);
	}

	public TransMatrixPane getPane() {		
		return transmatrixPane;
	}
	
	public TransMatrixFrame getFrame() {		
		return transmatrixFrame;
	}


	protected void notify(Collection<Experiment> ce) {
		getLayout().updateLayout();
	}

	protected class DependencySet {
		public List<Transformation> trans = new LinkedList<Transformation>();
		public List<List<Experiment>> exp = new LinkedList<List<Experiment>>();
		public String toString() {
			String s = "--\n";
			for (int i=0; i!=trans.size(); ++i)
				s+=trans.get(i).toString()+"\t"+exp.get(i).size()+"\t"+exp.get(i)+"\n";
			return s;
		}
	}
	
	protected DependencySet getAllAfter(int position) {

		DependencySet ret = new DependencySet();

		int maxIndex = getMinimumIndexMaximum();
		
		// add in order of index		
		for (int removePos = position+1; removePos<=maxIndex; ++removePos) {
			for (Transformation trans: getTransformationsForMinimumIndex(removePos)) {
				if (!ret.trans.contains(trans)) {
					ret.trans.add(trans);
					ret.exp.add(getExperiments(trans));
				}
			}
		}
		
		return ret;
	}
	
	protected Set<Experiment> setTransformation0(Transformation t, List<Experiment> exps) {
		LinkedList<Transformation> unhappy = unhappinessIsWorthAnException?new LinkedList<Transformation>():null; 
		
		Set<Experiment> affected = new HashSet<Experiment>();
		affected.addAll(getExperiments(t));
		
		DependencySet depends;
		if (t2e.containsKey(t)) {
			depends = getAllAfter(getMinimumIndex(t));
		} else {
			depends = new DependencySet();
		}
				
		depends.trans.add(0, t);
		depends.exp.add(0, exps);
		
		for (Transformation dt : depends.trans)
			removeTransform(dt);
		
		// now add them again, if they allow it
		for (int i=0; i!=depends.trans.size(); ++i) {
			Transformation t2 = depends.trans.get(i);
			if (!addChecked( t2, depends.exp.get(i)))
				if (unhappy==null)
					System.out.println("Removing unhappy transformation "+t2);
				else
					unhappy.add(t2);
		}		
		clearMinimumIndices();
		
		if (unhappy!=null && unhappy.size()>0)
			throw new UnhappyTransformationsException(unhappy);
				
		for (List<Experiment> le : depends.exp)
			affected.addAll(le);
		
		return affected;
	} 
	
	public void setTransformation_noUte(Transformation t, List<Experiment> exps) {
		notify(setTransformation0(t, exps));
		if (exps.size()==0)
			removeTransform(t);
	}

	protected boolean addChecked( Transformation t, List<Experiment> exps) {
		if (exps.size()==0)
			return true;
		// we know that the transformation is not yet in the matrix
		if (t2e.containsKey(t))
			throw new RuntimeException("addChecked called with already present transformation: "+t);
		// make sure the applicability is checked on THIS transmatrix, and not another one.
		// this is important in the case that we are currently in a checked_clone() transmatrix.
		List<Experiment> wrappedExps;
		if (unhappinessIsWorthAnException) {
			wrappedExps = new LinkedList<Experiment>();
			for (Experiment e : exps)
				wrappedExps.add(new SchizophrenicExperiment(e,this));
		} else {
			wrappedExps = exps;
		}
		boolean app = t.applicableTo(wrappedExps);
		if (!app)
			return false;
		for (Experiment e : exps) {
			e2t.put(e, t);
			t2e.put(t, e);
			emptyExperiments.remove(e);
			if (t instanceof DatasetCreatingTransformation)
				increaseDSCount(e);
		}
		if (t.getSetting()!=null)
			t.getSetting().addChangeListener(this);
		t.resetCache();
		return true;
	}

	protected void decreaseDSCount(Experiment e) {
		Integer i = dsCreationCount.get(e);
		if (i!=null)
			if (i==1)
				dsCreationCount.remove(e);
			else	
				dsCreationCount.put(e, i-1);
	}
	
	protected void increaseDSCount(Experiment e) {
		Integer i = dsCreationCount.get(e);
		if (i==null)
			i=0;
		dsCreationCount.put(e, i+1);
	}
	
	protected List<Experiment> removeTransform( Transformation t ) {	
		List<Experiment> le  = getExperiments(t);
		for (Experiment e : le) {
			e2t.remove(e, t);
			t2e.remove(t, e);
			if (t instanceof DatasetCreatingTransformation)
				decreaseDSCount(e);
			if (e2t.get(e).size()==0)
				emptyExperiments.add(e);
		}
		if (t.getSetting()!=null)
			t.getSetting().removeChangeListener(this);
		clearMinimumIndices();
		return le;
	}

	public void addTransformation_noUte(Transformation t, List<Experiment> exps) {
		LinkedList<Experiment> exps2 = new LinkedList<Experiment>(exps); // make sure the collection is modifyable
		exps2.addAll(getExperiments(t));
		setTransformation_noUte(t, exps2);
		if (t.getSetting()!=null)
			t.getSetting().addChangeListener(this);
	}


	public void addExperiment(Experiment e) {
		emptyExperiments.add(e);
		transmatrixPane.addPanel(e.getGUIElement());
		if (e.getSetting()!=null) 
			e.getSetting().addChangeListener(this);
		notify(Arrays.asList(new Experiment[]{e}));
		transmatrixFrame.update();
	}

	public LinkedList<Experiment> getExperiments(Transformation t) {
		LinkedList<Experiment> res = new LinkedList<Experiment>(t2e.get(t));
		// sort according to sorting in pane
		Collections.sort(res, new Comparator<Experiment>() {

			public int compare(Experiment o1, Experiment o2) {
				Integer i1 = getPane().getPositioner().indexOf(o1.getGUIElement());
				Integer i2 = getPane().getPositioner().indexOf(o2.getGUIElement());
				if (i1!=null && i2!=null)
					return i1.compareTo(i2);
				return 0;
			}
		});
		
		return res;
	}

	public LinkedList<Transformation> getTransformations(Experiment e) {
		return new LinkedList<Transformation>(e2t.get(e));
	}

	public Transformation getLastTransformation(Experiment e) {
		List<Transformation> lt = e2t.get(e);
		if (lt.size()>0)
			return lt.get(lt.size()-1);
		return null;
	}

	public void remove_noUte(Transformation t, Experiment... exps) {
		boolean fullRemove = exps.length==0;		
		List<Experiment> le;
		if (fullRemove)
			le = Collections.<Experiment>emptyList();
		else {
			le = getExperiments(t);
			for (Experiment e : exps)
				le.remove(e);
		}
		setTransformation_noUte(t, le);
		if (fullRemove)
			removeTransform(t);
	}

	public void remove_noUte(Collection<Experiment> exs) {
		Set<Experiment> affected = new HashSet<Experiment>();
		for (Experiment e : exs) {
			List<Transformation> affected_transforms = getTransformations(e);
			// first we remove the experiment from all transforms secretly
			for (Transformation t : affected_transforms)
				t2e.remove(t, e);
			// then we try to add the transforms again
			for (Transformation t : affected_transforms) {
				List<Experiment> le = getExperiments(t);
				setTransformation0(t, le);
				affected.addAll(le);
			}
			transmatrixPane.removePanel(e.getGUIElement());
			if (e.getSetting()!=null) 
				e.getSetting().removeChangeListener(this);
			e2t.remove(e);
			// remove MIO annotations
			for (MIGroup mg : commonGroups.values()) 
				mg.remove(e);
		}
		emptyExperiments.removeAll(exs);
		dsCreationCount.keySet().removeAll(exs);
		affected.removeAll(exs);
		notify(affected);
		transmatrixFrame.update();
	}
	
	public void replaceTransformation_noUte(Transformation told, Transformation tnew) {
		LinkedList<Transformation> unhappy = unhappinessIsWorthAnException?new LinkedList<Transformation>():null; 
		
		Set<Experiment> affected = new HashSet<Experiment>();
		
		List<Experiment> exps = getExperiments(told);
		
		affected.addAll(exps);
		
		DependencySet depends = getAllAfter(getMinimumIndex(told));		
		// first remove all later transforms and remember them

		depends.trans.add(0, told);
		depends.exp.add(0, exps);

		for (Transformation dt : depends.trans)
			removeTransform(dt);
		
		// replace the transformation in the dependency set
		depends.trans.set(0, tnew);

		// now add them again, if they allow it
		for (int i=0; i!=depends.trans.size(); ++i) {
			Transformation t = depends.trans.get(i);
			if (!addChecked( t, depends.exp.get(i)))
				if (unhappy==null)
					System.out.println("Removing unhappy transformation "+t);
				else
					unhappy.add(t);
		}		
		clearMinimumIndices();

		if (unhappy!=null && unhappy.size()>0)
			throw new UnhappyTransformationsException(unhappy);

		for (List<Experiment> le : depends.exp)
			affected.addAll(le);
		
		notify(affected);
	}
	
	public void insertTransformation_noUte(Transformation told, Transformation tnew, List<Experiment> exps) {
		LinkedList<Transformation> unhappy = unhappinessIsWorthAnException?new LinkedList<Transformation>():null; 
			
		Set<Experiment> affected = new HashSet<Experiment>();
		affected.addAll(exps);
		
		DependencySet depends = getAllAfter(getMinimumIndex(told)-1);
		// first remove all later transforms and remember them

		for (Transformation dt : depends.trans)
			removeTransform(dt);

		depends.trans.add(0, tnew);
		depends.exp.add(0, exps);

		// now add them again, if they allow it
		for (int i=0; i!=depends.trans.size(); ++i) {
			Transformation t = depends.trans.get(i);
			if (!addChecked( t, depends.exp.get(i)))
				if (unhappy==null)
					System.out.println("Removing unhappy transformation "+t);
				else
					unhappy.add(t);
		}		
		clearMinimumIndices();

		if (unhappy!=null && unhappy.size()>0)
			throw new UnhappyTransformationsException(unhappy);

		for (List<Experiment> le : depends.exp)
			affected.addAll(le);
		
		notify(affected);
	}
	
	protected void clearMinimumIndices() {
		minimumIndices.clear();
		minimumIndices_reverse.clear();
	}
	
	protected void updateMinimumIndices() {
		for (Transformation t : t2e.keySet())
			getMinimumIndex(t); // update all mappings
	}
	
	public int getMinimumIndex(Transformation t) {
		if (minimumIndices.containsKey(t))
			return minimumIndices.get(t);
		int min = 0;
		for (Experiment e : getExperiments(t)) {
			List<Transformation> lt = getTransformations(e);
			int nm = lt.indexOf(t);
			if (nm>0) { // we have a left neighbor in SOME experiment
				Transformation leftNeighbor = lt.get(nm-1);
				// find out where the left neighbor sits 
				min = Math.max(min, getMinimumIndex(leftNeighbor)+1);
			}
		}
		minimumIndices.put(t, min);
		minimumIndices_reverse.put(min,t);
		return min;
	}
	
	public int getMinimumIndexMaximum() {
		updateMinimumIndices();
		return minimumIndices.values().size()>0?Collections.max(minimumIndices.values()):-1;
	}
	
	public List<Transformation> getTransformationsForMinimumIndex(int index) {
		if (!minimumIndices_reverse.containsKey(index))
			updateMinimumIndices();
		return minimumIndices_reverse.get(index);
	}

	public ExperimentState getInputState(Transformation t, Experiment e) {
		LinkedList<Transformation> tlist = getTransformations(e);
		int idx = tlist.indexOf(t);
		if (idx>0)
			return tlist.get(idx-1).getExperimentState(e);
		else
			return e.getInitialState();
	}

	public Object[][] asMatrix() {
		
		int maxIdx = getMinimumIndexMaximum();
		// make sure minindices are present for all
		

		Object[][] res = new Object[e2t.size()][];
		int eidx = 0;
		for (Experiment e : e2t.keySet()) {
			res[eidx] = new Object[maxIdx+2];
			res[eidx][0] = e;
			for (Transformation t : getTransformations(e)) {
				int tidx = getMinimumIndex(t);
				res[eidx][tidx+1] = t;
			}			
			eidx++;
		}
		return res;
	}
	
	public Object[][] asMatrix(Collection<Experiment> subset) {
		return subClone(subset).asMatrix();
	}

	public String toString(Collection<Experiment> subset) {
		Object[][] am = asMatrix(subset);
		return toString(am).toString();
	}
	
	public String toString() {
		Object[][] am = asMatrix();
		return toString(am).toString();
	}
	
	protected static StringBuilder toString(Object[][] am) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i!=am.length; ++i) {
			for (int j=0; j!=am[i].length; ++j) {
				sb.append(am[i][j]+"\t");
			}
			sb.append("\n");
		}
		return sb;
	}


	public int getExperimentCount() {
		return e2t.size()+emptyExperiments.size();
	}

	public Collection<Experiment> getExperiments() {
		HashSet<Experiment> ret = new HashSet<Experiment>();
		ret.addAll(e2t.keySet());
		ret.addAll(emptyExperiments);
		
		LinkedList<Experiment> res = new LinkedList<Experiment>(ret);
		
		Collections.sort(res, new Comparator<Experiment>() {

			public int compare(Experiment o1, Experiment o2) {
				Integer i1 = getPane().getPositioner().indexOf(o1.getGUIElement());
				Integer i2 = getPane().getPositioner().indexOf(o2.getGUIElement());
				if (i1!=null && i2!=null)
					return i1.compareTo(i2);
				return 0;
			}
		});
		
		return Collections.unmodifiableCollection(res);
	}


	public int getTransformationCount() {
		return t2e.size();
	}

	public Collection<Transformation> getTransformations() {
		return Collections.unmodifiableCollection(t2e.keySet());
	}


	public TransMatrixLayout getLayout() {
		return transmatrixPane.getTMLayout();
	}
	
	public void stateChanged_noUte(SettingChangeEvent e) {
		// A transformation setting has changed. Check if the chains still are applicable
		for (Transformation t : new HashSet<Transformation>(t2e.keySet()))
			if (e.hasSource(t.getSetting()))
				setTransformation_noUte(t, getExperiments(t));
		// Maybe it was an experiment that triggered the event. check that also
		for (Experiment ex : new LinkedList<Experiment>(getExperiments())) {
			if (e.hasSource(ex.getSetting())) {
				if (!emptyExperiments.contains(ex)) {
					Transformation t = e2t.get(ex).get(0); // check chain from beginning
					setTransformation_noUte(t, getExperiments(t));
				}
			}
		}
		getLayout().updateLayout();
	}	
		
	public MIGroup getCommonMIGroup(String name, String plumaType) {
		MIGroup mg = commonGroups.get(name);
		if (mg == null)
			commonGroups.put(name, mg = new MIGroup(PluginManager.getInstance().getPluginFromID(plumaType), name, null));
		return mg;
	}
	
	public boolean canRunTransformations() {
		String unusedExperiments="";
		for (Experiment e : getExperiments()) {
			if (dsCreationCount.get(e)==null) {
				unusedExperiments+="- "+e.getName()+"\n";
			}
		}
		if (unusedExperiments.length()>0) {
			String message = "The following experiments will not be part of any DataSet:\n"+unusedExperiments;
			message+="\nDo you still wish to continue?";
			return (JOptionPane.showConfirmDialog(transmatrixFrame, message, "Continue applying transformations?", JOptionPane.YES_NO_OPTION)
					==JOptionPane.YES_OPTION);
		} 
		if (isExecuting()) {
			String message = "The matrix is currently executing.\n" +
							"Please let it finish before starting it again."; 
			JOptionPane.showMessageDialog(transmatrixFrame, message, "Can not continue at this moment", JOptionPane.OK_OPTION);
			return false;
		}
			
		return true;
	}
	
	public List<ProcessedExperiment> runTransformations(final boolean runParallel) {
		final List<ProcessedExperiment> res = new LinkedList<ProcessedExperiment>();
		
		AbstractTask transformationTask = new AbstractTask("Running transformations") {

			@Override
			protected void doWork() throws Exception {
				if (isExecuting())
					throw new RuntimeException("The matrix is currently executing.\n" +
							"Please let it finish before starting it again.");
				res.addAll(runTransformations0(this, runParallel));
			}

			@Override
			protected void initialize() {
			}
			
		};
		
		transformationTask.start();
		transformationTask.waitFor();
		
		return res;
	}
	
	protected List<ProcessedExperiment> runTransformations0(AbstractTask masterTask, boolean runParallel) {
		System.out.println("Starting...");
		long lstart=System.currentTimeMillis();
		// keep this for later restoring
		HashMap<String, MIGroup> baseAnnotation = commonGroups;
		try {
			intermediateData = Collections.synchronizedMap(new HashMap<Experiment, ExperimentData>());
			intermediateState = Collections.synchronizedMap(new HashMap<Experiment, ExperimentState>());
			System.gc();
			// fill with initial data
			for (Experiment e : getExperiments()) {
				intermediateData.put(e, e.getInitialData());
				intermediateState.put(e, e.getInitialState());
			}
				// sort transformations by execution index 
			MultiHashMap<Integer, Transformation> reverseIndex = new MultiHashMap<Integer, Transformation>();
			for (Transformation t : getTransformations()) {
				reverseIndex.put(getMinimumIndex(t),t);
			}
			double totalTransforms = t2e.size();
			double doneTransforms = 0;

			// create a new copy of the migroup object to capture annotations that are added during processing.
			// these will be dropped after processing (after they have been added to the dataset, for instance).
			commonGroups = new HashMap<String, MIGroup>();
			commonGroups.putAll(baseAnnotation);		

			// apply transformations in order, equal index means parallel execution
			for (int i=0; i!=reverseIndex.size(); ++i) {

				List<Transformation> parallelTrans = reverseIndex.get(i);
				if (MaydayDefaults.isDebugMode()) 
					masterTask.writeLog("Execution index "+(i+1)+": "+parallelTrans.size()+" transformations: "+parallelTrans+"\n");
				AbstractTask[] ts = new AbstractTask[parallelTrans.size()];

				ProgressListener subTask = null;
				if (!runParallel)
					subTask = new SubTaskProgressListener(masterTask, (int)(doneTransforms*10000/totalTransforms), (double)(10000/totalTransforms));

				// start all transforms now
				for (int j=0; j!=ts.length; ++j) {
					Transformation pt = parallelTrans.get(j);
					ts[j] = new TransformThread(pt, subTask);
					if (MaydayDefaults.isDebugMode()) 
						masterTask.writeLog("-- "+ts[j].getName()+"\n");
					if (!runParallel) {
						ts[j].waitFor();
						++doneTransforms;
						masterTask.setProgress((int)(10000*doneTransforms/totalTransforms));
						if (ts[j].getTaskState()==TaskStateEvent.TASK_FAILED)
							throw new RuntimeException("One of the transformations failed: "+parallelTrans.get(j).getName());
						if (ts[j].getTaskState()==TaskStateEvent.TASK_CANCELLED) 
							throw new RuntimeException("One of the transformations was canceled: "+parallelTrans.get(j).getName());
						for (Experiment e : getExperiments(pt)) {
							intermediateState.put(e, getInputState(pt, e));
						}
					}
				}
				if (runParallel) {
					// wait for results
					for (int j=0; j!=ts.length; ++j) {
						ts[j].waitFor();
						if (ts[j].getTaskState()==TaskStateEvent.TASK_FAILED)
							throw new RuntimeException("One of the transformations failed: "+parallelTrans.get(j).getName());
						if (ts[j].getTaskState()==TaskStateEvent.TASK_CANCELLED) 
							throw new RuntimeException("One of the transformations was canceled: "+parallelTrans.get(j).getName());
						doneTransforms++;
						masterTask.setProgress((int)(10000*doneTransforms/totalTransforms));
						Transformation pt = ((TransformThread)ts[j]).t;
						for (Experiment e : getExperiments(pt)) {
							intermediateState.put(e, getInputState(pt, e));
						}						
					}
				}


			}

			LinkedList<ProcessedExperiment> result = new LinkedList<ProcessedExperiment>();
			for (ReorderableHorizontalPanel rhp : getPane().getPositioner().panels()) {
				Experiment e = ((ExperimentPanel)rhp).getExperiment();			
				result.add( new ProcessedExperiment( e, intermediateData.get(e) )  );
			}

			long lend=System.currentTimeMillis();
			System.out.println("Transformations done in "+((lend-lstart)+" ms"));

			return result;

		} finally {
			// restore matrix to non-running state
			intermediateData = null;
			intermediateState = null;
			// restore annotation to previous state
			commonGroups.entrySet().removeAll(baseAnnotation.entrySet());
			for (Experiment e : getExperiments()) {
				e.getAnnotations().removeAll(commonGroups.values());
			}
			commonGroups = baseAnnotation;
		}

	}
	
	public boolean isExecuting() {
		return intermediateData!=null;
	}
	
	public ExperimentData getIntermediateData(Experiment e) {
		return intermediateData.get(e);
	}
	
	public ExperimentState getIntermediateState(Experiment e) {
		return intermediateState.get(e);
	}
	
	public ExperimentData setIntermediateData(Experiment e, ExperimentData d) {
		return intermediateData.put(e, d);
	}

	protected class TransformThread extends AbstractTask {
		protected Transformation t;
		protected ProgressListener parentPL;
		
		public TransformThread(Transformation trans, ProgressListener parentPL) {
			super(trans.getName());
			t=trans;
			this.parentPL = parentPL;
			start();
		}
		protected void doWork() throws Exception {
			if (MaydayDefaults.isDebugMode()) 
				System.out.println("- Starting Transformation "+t.getName());
			long l1 = System.currentTimeMillis();
			t.setProgressListener(this);
			t.compute();
			long l2 = System.currentTimeMillis();
			if (MaydayDefaults.isDebugMode()) 
				System.out.println("- Finished Transformation "+t.getName()+"  in "+(l2-l1)+" ms");
		}
		protected void initialize() {
		}
		@Override
		public void setProgress(int percentageX100, String progressInfo) {
			super.setProgress(percentageX100, progressInfo);
			if (parentPL!=null)
				parentPL.setProgress(percentageX100);
			// also notify the total transforms thread
			
		}
	}
	
	
	// ============== Checked actions for the user ===============
	
	@SuppressWarnings("unchecked")
	public DisposableTransMatrix checking_clone() {
		DisposableTransMatrix c = new DisposableTransMatrix();
		c.e2t = (MultiTreeMap<Experiment, Transformation>)e2t.clone();
		c.t2e = (MultiTreeMap<Transformation, Experiment>)t2e.clone();
		c.emptyExperiments = (HashSet<Experiment>)emptyExperiments.clone();
		c.minimumIndices = (HashMap<Transformation, Integer>)minimumIndices.clone();
		c.minimumIndices_reverse = (MultiHashMap<Integer, Transformation>)minimumIndices_reverse.clone();
		c.unhappinessIsWorthAnException=true;
		return c;
	}
	
	protected class DisposableTransMatrix extends TransMatrix {
		public void notify(Collection<Experiment> ce) {};
		public void dispose() {
			// clear the checking clone
			for (Transformation t : getTransformations())
				if (t.getSetting()!=null)
					t.getSetting().removeChangeListener(this);
			for (Experiment ex : getExperiments())
				if (ex.getSetting()!=null)
					ex.getSetting().removeChangeListener(this);
		}
		protected final void angry() {
			throw new RuntimeException("A non-disposable method was called on a disposable transmatrix clone.");
		}
		public void setTransformation(Transformation t, List<Experiment> exps) { angry(); }
		public void addTransformation(Transformation t, List<Experiment> exps) { angry(); }
		public void remove(Transformation t, Experiment... exps) { angry(); }
		public void remove(Collection<Experiment> exs) { angry(); }
		public void replaceTransformation(Transformation told, Transformation tnew) { angry(); }
		public void insertTransformation(Transformation tbefore, Transformation tnew, List<Experiment> exps) { angry(); }
		public void stateChanged(SettingChangeEvent e) { angry(); }
	}
		
	public TransMatrix happy_clone() {
		TransMatrix c = checking_clone();
		c.unhappinessIsWorthAnException=false;
		return c;
	}
	
	@SuppressWarnings("unchecked")
	protected TransMatrix subClone(Collection<Experiment> exs) {
		TransMatrix c = new TransMatrix();
		c.e2t = new MultiTreeMap<Experiment, Transformation>();
		for (Experiment e : exs) {
			for (Transformation t : e2t.get(e)) {
				c.e2t.put(e,t);
				c.t2e.put(t,e);
			}
		}
		c.emptyExperiments = (HashSet<Experiment>)emptyExperiments.clone();
		c.emptyExperiments.retainAll(exs);
		return c;
	}
	
	@SuppressWarnings("serial")
	protected class UnhappyTransformationsException extends RuntimeException {
		protected LinkedList<Transformation> t;
		public UnhappyTransformationsException(LinkedList<Transformation> t) {
			this.t = t;
		}
		public LinkedList<Transformation> getList() {
			return t;
		}
	}
	
	
	protected boolean handleUTE(UnhappyTransformationsException ute) {
		String message = "This action will remove further transformations: \n";
		for (Transformation t : ute.getList())
			message+="- "+t+"\n";
		message+="Do you really want to continue?";			
		return (JOptionPane.showConfirmDialog(transmatrixFrame, message, "Remove further transformations?", JOptionPane.YES_NO_OPTION)
				==JOptionPane.YES_OPTION);
	}
	
	public void setTransformation(Transformation t, Experiment... e) {
		List<Experiment> le = new LinkedList<Experiment>();
		for (Experiment ex : e)
			le.add(ex);
		setTransformation(t, le);
	}
	
	public void setTransformation(Transformation t, List<Experiment> exps) {
		DisposableTransMatrix sc = checking_clone();
		try {
			sc.setTransformation_noUte(t, exps);
			// if it works, do it
			setTransformation_noUte(t, exps);
		} catch (UnhappyTransformationsException ute) {
			if (handleUTE(ute)) // if they want it, do it
				setTransformation_noUte(t, exps);
		}			
		sc.dispose();
	}
	
	public void addTransformation(Transformation t, List<Experiment> exps) {
		t.setTransMatrix(this);
		t.getSetting();
		t.updateSettings(exps);
		DisposableTransMatrix sc = checking_clone();
		try {
			sc.addTransformation_noUte(t, exps);
			// if it works, do it
			addTransformation_noUte(t, exps);
		} catch (UnhappyTransformationsException ute) {
			if (handleUTE(ute)) // if they want it, do it
				addTransformation_noUte(t, exps);
		}			
		sc.dispose();
	}
	
	public void remove(Transformation t, Experiment... exps) {
		DisposableTransMatrix sc = checking_clone();
		try {
			sc.remove_noUte(t, exps);
			// if it works, do it
			remove_noUte(t, exps);
		} catch (UnhappyTransformationsException ute) {
			if (handleUTE(ute)) // if they want it, do it
				remove_noUte(t, exps);
		}	
		sc.dispose();
	}
	
	public void remove(Experiment e) {
		LinkedList<Experiment> le = new LinkedList<Experiment>();
		le.add(e);
		remove(le);
	}
	
	public void remove(Collection<Experiment> exs) {
		DisposableTransMatrix sc = checking_clone();
		try {
			sc.remove_noUte(exs);
			// if it works, do it
			remove_noUte(exs);
		} catch (UnhappyTransformationsException ute) {
			if (handleUTE(ute)) // if they want it, do it
				remove_noUte(exs);
		}	
		sc.dispose();
	}
	
	public void replaceTransformation(Transformation told, Transformation tnew) {
		tnew.setTransMatrix(this);
		DisposableTransMatrix sc = checking_clone();
		try {
			sc.replaceTransformation_noUte(told, tnew);
			// if it works, do it
			replaceTransformation_noUte(told, tnew);
		} catch (UnhappyTransformationsException ute) {
			if (handleUTE(ute)) // if they want it, do it
				replaceTransformation_noUte(told, tnew);
		}
		sc.dispose();
	}
	
	public void insertTransformation(Transformation tbefore, Transformation tnew, List<Experiment> exps) {
		tnew.setTransMatrix(this);
		DisposableTransMatrix sc = checking_clone();
		try {
			sc.insertTransformation_noUte(tbefore, tnew, exps);
			// if it works, do it
			insertTransformation_noUte(tbefore, tnew, exps);
		} catch (UnhappyTransformationsException ute) {
			if (handleUTE(ute)) // if they want it, do it
				insertTransformation_noUte(tbefore, tnew, exps);
		}	
		sc.dispose();
	}
	
	public void stateChanged(SettingChangeEvent e) {
		DisposableTransMatrix sc = checking_clone();
		try {
			sc.stateChanged_noUte(e);
		} catch (UnhappyTransformationsException ute) {
			String message = "Your changes resulted in the removal of the following transformations: \n";
			for (Transformation t : ute.getList())
				message+="- "+t+"\n";
			JOptionPane.showMessageDialog(transmatrixFrame, message, "Some transformations had to be removed", JOptionPane.OK_OPTION);
		}	
		// has to be done, no way around it
		stateChanged_noUte(e);
		
		sc.dispose();		
	}
	
	public boolean equals(TransMatrix other) {
		if (!other.emptyExperiments.equals(this.emptyExperiments))
			return false;
		if (!other.e2t.equals(this.e2t))
			return false;
		if (!other.t2e.equals(this.t2e))
			return false;
		return true;
	}

	
	public void saveToFile(final String fileName) {
		System.out.println("Saving transmatrix to "+fileName);
		AbstractTask at = new AbstractTask("Writing Matrix") {

			final LinkedList<Experiment> exs = new LinkedList<Experiment>(getExperiments());
			final LinkedList<Transformation> tfs = new LinkedList<Transformation>(getTransformations());
			
			protected double scale=10000.0/(double)(1+exs.size()+2*(tfs.size())+3+1);			
			
			@SuppressWarnings("unchecked")
			@Override
			protected void doWork() throws Exception {
				
				long tbefore = System.currentTimeMillis();

				ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(fileName));
				File targDir = new File(fileName).getParentFile();
				
				targDir = new File(targDir, ""+hashCode());
				targDir.mkdir();			
				
				int count = 0;
				HashMap<Experiment, Integer> exmap = new HashMap<Experiment, Integer>();
				
				setProgress((int)(count*scale), "Writing Identifier Mappings");
				zout.putNextEntry(new ZipEntry("IDMappings_0"));
				IdentifierMapContainer.INSTANCE.writeToStream(new BufferedWriter(new OutputStreamWriter(zout)));
				zout.closeEntry();
				++count;
				
				setProgress((int)(count*scale), "Writing Feature Summarization Mappings");
				zout.putNextEntry(new ZipEntry("FSMappings_0"));
				FeatureSummarizationMapContainer.INSTANCE.writeToStream(new BufferedWriter(new OutputStreamWriter(zout)));
				zout.closeEntry();
				++count;
				
				setProgress((int)(count*scale), "Writing Locus Mappings");
				zout.putNextEntry(new ZipEntry("LMappings_0"));
				LocusMapContainer.INSTANCE.writeToStream(new BufferedWriter(new OutputStreamWriter(zout)));
				zout.closeEntry();
				++count;

				for (int i = 0; i!=tfs.size(); ++i) {
					setProgress((int)(count*scale), "Writing Transformation "+(i+1));					
					zout.putNextEntry(new ZipEntry("Transformation_"+i));
					tfs.get(i).writeToStream(zout, targDir);
					zout.closeEntry();
					++count;
				}
				
				for (int i = 0; i!=exs.size(); ++i) {
					setProgress((int)(count*scale), "Writing Experiment "+(i+1));
	            	ProgressListener progress = new SubTaskProgressListener(this, (int)(count*scale), scale, "Writing Experiment "+(i+1) );
					zout.putNextEntry(new ZipEntry("Experiment_"+i));
					Experiment ex = exs.get(i);
					AbstractExperimentSerializer serializer = ex.getSerializer();
					BufferedOutputStream bos = new BufferedOutputStream(zout);
					serializer.writeToStream(ex, bos, targDir, progress);
					bos.flush();
					exmap.put(ex, i);
					zout.closeEntry();					
					++count;
				}
				
				clearMinimumIndices();  // make sure these are correct
				
				MultiHashMap<Integer, Transformation> reverseIndex = new MultiHashMap<Integer, Transformation>();
				for (Transformation t : getTransformations()) {
					reverseIndex.put(getMinimumIndex(t),t);
				}
				LinkedList<Transformation> byIndex = new LinkedList<Transformation>();
				TreeSet<Integer> minInd = new TreeSet<Integer>(reverseIndex.keySet());
				for (Integer i : minInd)
					for (Transformation t : reverseIndex.get(i))
						byIndex.add(t);
				
				for (int i = 0; i!=byIndex.size(); ++i) {
					setProgress((int)(count*scale), "Writing Matrix");					
					zout.putNextEntry(new ZipEntry("Matrix_"+i));

					Transformation t = byIndex.get(i);
					List<Experiment> le = getExperiments(t);
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(zout));
					bw.write("Transform="+tfs.indexOf(t)+"\n");
					bw.write("MinimumIndex="+getMinimumIndex(t)+"\n");
					
					for (Experiment e : le) {
						int tid = exmap.get(e);
						bw.write(tid+"\n");
					}
					bw.flush();
					zout.closeEntry();
					++count;
				}				
				
				// add external files to zip
				setProgress((int)(count*scale), "Zipping additional files");
				zout.putNextEntry(new ZipEntry("Additional_0/"));
				for (File f : targDir.listFiles()) {
					zout.putNextEntry(new ZipEntry("Additional_0/"+f.getName()));					
					FileManager.copy(new FileInputStream(f), zout, false);
					zout.closeEntry();
					f.delete();
				}
				
				targDir.delete();
				
				zout.finish();
				zout.close();
				
				long tafter = System.currentTimeMillis();
		        
		        System.out.println("Done saving in "+(tafter-tbefore)+" ms");
				
				setProgress(10000);
				
				System.gc();
			}

			@Override
			protected void initialize() {}					
			
		};
		at.start();		
	}
	
	public void loadFromFile(final String fileName) {
		System.out.println("Loading transmatrix from "+fileName);

		LinkedList<Experiment> le = new LinkedList<Experiment>(getExperiments());
		remove_noUte(le);
		
		AbstractTask at = new AbstractTask("Loading Matrix") {

			final LinkedList<Experiment> exs = new LinkedList<Experiment>();
			
			protected double scale;			
			
			@SuppressWarnings("unchecked")
			@Override
			protected void doWork() throws Exception {

				long tbefore = System.currentTimeMillis();
				
				int count = 0;
				HashMap<Integer, Transformation> transforms = new HashMap<Integer, Transformation>();
				
				ZipFile zip = new ZipFile(new File(fileName));
								
				scale = 10000.0/zip.size();
				
				File sourceDir = new File(java.lang.System.getProperty("java.io.tmpdir"), ""+hashCode());
				sourceDir.mkdir();
				sourceDir.deleteOnExit();

				//extract additional files first
				setProgress(0, "Extracting files");
				for(Enumeration e=zip.entries(); e.hasMoreElements(); ) {
					ZipEntry zi = (ZipEntry)e.nextElement();
					if (zi.getName().startsWith("Additional_0/") && !zi.isDirectory()) {
						File targ = new File(sourceDir, new File(zi.getName()).getName());
						FileOutputStream fos = new FileOutputStream(targ);
						FileManager.copy(zip.getInputStream(zi), fos);
						fos.flush();
						fos.close();
						targ.deleteOnExit();
					}
				}
				
		        for(Enumeration e=zip.entries(); e.hasMoreElements(); ) {
		        	ZipEntry zi = (ZipEntry)e.nextElement();
		        	
		        	if (zi.getName().contains("/"))
		        		continue;
		        	
		            InputStream input = zip.getInputStream(zi);
		            input = new BufferedInputStream(input);
		            
		            String entryType = zi.getName();
		            String parts[] = entryType.split("_");
		            entryType = parts[0];
		            int entryId = Integer.parseInt(parts[1]);
		            
		            if (entryType.equals("IDMappings")) {
		            	setProgress((int)(count*scale), "Reading Identifier Mappings");		
		            	IdentifierMapContainer.INSTANCE.readFromStream(new ReadyBufferedReader(new InputStreamReader(input)));		            	
		            	
		            } else if (entryType.equals("FSMappings")) {
		            	setProgress((int)(count*scale), "Reading Feature Summarization Mappings");		
		            	FeatureSummarizationMapContainer.INSTANCE.readFromStream(new ReadyBufferedReader(new InputStreamReader(input)));		
		            	
		            } else if (entryType.equals("LMappings")) {
		            	setProgress((int)(count*scale), "Reading Locus Mappings");		
		            	LocusMapContainer.INSTANCE.readFromStream(new ReadyBufferedReader(new InputStreamReader(input)));		
		            	
		            } else if (entryType.equals("Transformation")) {
		            	setProgress((int)(count*scale), "Reading Transformation "+(entryId+1));		
		            	Transformation t = AbstractTransformationPlugin.loadFromStream(input, sourceDir);
		            	transforms.put(entryId, t);
		            	
		            } else if (entryType.equals("Experiment")) {
		            	setProgress((int)(count*scale), "Reading Experiment "+(entryId+1));
		            	ProgressListener progress = new SubTaskProgressListener(this, (int)(count*scale), scale, "Reading Experiment "+(entryId+1) );
		            	Experiment ex = AbstractExperimentSerializer.loadFromStream(input, TransMatrix.this, sourceDir, progress);
		            	addExperiment(ex);
		            	exs.add(ex);
		            	
		            } else if (entryType.equals("Matrix")) {
		            	setProgress((int)(count*scale), "Reading Matrix");
		            	BufferedReader br = new BufferedReader(new InputStreamReader(input));
		            	String line = br.readLine();
		            	int tind = Integer.parseInt(line.split("=")[1]);		            	
		            	line = br.readLine();
		            	//don't need the min index now, because we know they are ordered
		            	LinkedList<Experiment> exps = new LinkedList<Experiment>();
		            	while ((line=br.readLine())!=null) {
		            		int exind = Integer.parseInt(line);
		            		Experiment ex = exs.get(exind);
		            		exps.add(ex);
		            	}
		            	addTransformation(transforms.get(tind), exps);
		            } 
		            
		            if (hasBeenCancelled()) 
		            	break;
		            
		            ++count;
		        }
				
		        zip.close();
		        
		        long tafter = System.currentTimeMillis();
		        
		        System.out.println("Done loading in "+(tafter-tbefore)+" ms");
		        
				setProgress(10000);

				System.gc();
				
			}

			@Override
			protected void initialize() {}					
			
		};
		at.start();
	}

	public boolean containsExperiment(Experiment ex) {
		return e2t.containsKey(ex) || emptyExperiments.contains(ex);
	}
	
}
 