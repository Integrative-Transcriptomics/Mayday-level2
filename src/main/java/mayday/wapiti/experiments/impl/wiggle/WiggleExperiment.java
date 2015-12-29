package mayday.wapiti.experiments.impl.wiggle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mayday.wapiti.experiments.base.AbstractExperiment;
import mayday.wapiti.experiments.base.AbstractExperimentSerializer;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentSetting;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class WiggleExperiment extends AbstractExperiment {

	protected AbstractWiggleData wiggleData;
	protected ExperimentSetting setting;
	
	public WiggleExperiment(TransMatrix transMatrix, List<String> filesFWD, List<String> filesBWD, List<String> filesBOTH, String species) {
		super(makeName(filesFWD,filesBWD,filesBOTH), null, transMatrix);
		setInitialState(new WiggleInitialState(this));
		wiggleData = new AbstractWiggleData(filesFWD, filesBWD, filesBOTH, species);
		setting = makeSetting(new File(filesFWD.get(0)).getName());
	}
	
	protected ExperimentSetting makeSetting(String name) {
		return new WiggleExperimentSetting(this, name);
	}
	
	public ExperimentSetting getSetting() {
		return setting;
	}	
	
	public ExperimentData getInitialData() {
		return new ConcreteWiggleData(wiggleData, ((WiggleExperimentSetting)getSetting()).getSummaryFunction());
	}

	public String getIdentifier() {
		return "wiggle";
	}
	public AbstractExperimentSerializer<WiggleExperiment> getSerializer() {
		return new WiggleExperimentSerializer();
	}

	protected final static String makeName(List<String> files1, List<String> files2, List<String> files3) {
		List<String> files = new ArrayList<String>();
		files.addAll(files1);
		files.addAll(files2);
		files.addAll(files3);		
		String res = files.size()+" wiggle file";
		if (files.size()>1)
			res+="s: ";
		else
			res+=": ";
		for (int i=0; i!=files.size(); ++i)
			res+=new File(files.get(i)).getName()+(i<files.size()-1?", ":"");
		return res;
	}

	
}
