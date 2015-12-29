package mayday.wapiti.transformations.base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashMap;

import mayday.core.Preferences;
import mayday.core.gui.PreferencePane;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginManager;
import mayday.core.tasks.ProgressListener;
import mayday.wapiti.Constants;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.gui.TransformationButton;
import mayday.wapiti.transformations.matrix.TransMatrix;

public abstract class AbstractTransformationPlugin extends AbstractPlugin implements Transformation {

	public final static String MC = Constants.MCBASE+"Transformation";
	
	protected TransMatrix transMatrix;
	protected HashMap<Experiment, TransformationButton> gui = new HashMap<Experiment, TransformationButton>();
	protected HashMap<Experiment, ExperimentState> states = new HashMap<Experiment, ExperimentState>();

	protected ProgressListener progressListener;
	
	public AbstractTransformationPlugin() {
		// empty for pluma
	}
	
	public void setTransMatrix(TransMatrix tm) {
		transMatrix = tm;
	}
	
	public TransformationButton getGUIElement(Experiment e) {
		TransformationButton tb = gui.get(e);
		if (tb==null) {
			tb = new TransformationButton(this,e);
			gui.put(e, tb);
		}			
		return tb;
	}

	public int compareTo(Transformation o) {
		return new Integer(hashCode()).compareTo(o.hashCode());
	}
	
	public String toString() {
		return getIdentifier();
	}
	
	public String getIdentifier() {
		return getName();
	}
	
	public String getIdentifier(Experiment e) {
		return getIdentifier(); // most transforms' identifiers are independent of the exp
	}
	
	public String getName() {
		return PluginManager.getInstance().getPluginFromClass(getClass()).getName();
	}
	
	
	public TransMatrix getTransMatrix() {
		return transMatrix;
	}
	
	public ExperimentState getExperimentState(Experiment e) {
		ExperimentState es = states.get(e);
		if (es==null) {
			// use the transmatrix from the experiment so that this works with checked_clone in TransMatrix
			ExperimentState inputState = e.getTransMatrix().getInputState(this,e);
			es = makeState(inputState);
			states.put(e,es);
		}
		return es;
	}
	
	
	protected abstract ExperimentState makeState(ExperimentState inputState);

	public void resetCache() {
		gui.clear();
		states.clear();
	}
	
	public void init() {}

	public PreferencePane getPreferencesPanel() {
		// do not export settings as preferences
        return null;
    }
	
	// if settings need to be initialized depending on the selected experiments, it can be done in this method.
	// will be called after getSettings();
	public void updateSettings(Collection<Experiment> experiments) {
		// void for overriders
	}
	
	
	public final void writeToStream( OutputStream os, File targetDir  ) throws IOException {
		// write my pluma id and my settings
		BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(os) );
		bw.write( PluginManager.getInstance().getPluginFromClass(this.getClass()).getIdentifier() );
		bw.write("\n");
		try {
			if (getSetting()!=null) {
				getSetting().toPrefNode().saveTo(bw, true);
			} 
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		bw.flush();
	}
	
	public static Transformation loadFromStream( InputStream is, File sourceDir ) throws IOException {
		BufferedReader br = new BufferedReader( new InputStreamReader(is) );
		String plumaID = br.readLine();
		
		// fix access for old wapiti files		
		plumaID = plumaID.replace("WAPITI", "SeaSight"); 
		
		Transformation t = (Transformation)PluginManager.getInstance().getPluginFromID(plumaID).newInstance();
		br.mark(2);
		if (br.read()!=-1 && t.getSetting()!=null) {
			br.reset();
			Preferences prefNode = Preferences.createUnconnectedPrefTree("null","null");
			try {
				prefNode.loadFrom(br, true);
			}  catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
			t.getSetting().fromPrefNode(prefNode);
		}
		return t;
	}
	
	public void setProgressListener(ProgressListener pl) {
		this.progressListener=pl;
	}

	protected void setProgress(int percentageX100, String message) {
		if (progressListener!=null)
			progressListener.setProgress(percentageX100, message);
	}
	
	protected void setProgress(int percentageX100) {
		if (progressListener!=null)
			progressListener.setProgress(percentageX100);
	}
	
}
