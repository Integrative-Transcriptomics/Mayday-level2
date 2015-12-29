package mayday.wapiti.experiments.impl.agilent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.filemanager.FileManager;
import mayday.core.tasks.AbstractTask;
import mayday.wapiti.experiments.base.AbstractExperimentSerializer;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class AgilentExperimentSerializer extends
		AbstractExperimentSerializer<AgilentExperiment> {

	@SuppressWarnings("deprecation")
	@Override
	protected AgilentExperiment loadDataFromStream(String name, String sourceDescription, TransMatrix tm, DataInputStream dis) throws IOException {
		// get filename from stream
		String filename = readString(dis);
		filename = targetDirectory.getCanonicalPath()+"/"+filename;
		
		LinkedList<Experiment> result = new LinkedList<Experiment>();
		LinkedList<String> files = new LinkedList<String>();
		files.add(filename);
		
		AbstractTask at = new AgilentImportPlugin.AgilentParserTask(files, result, tm);
		at.setProgressListener(progress);
		at.run();
		
		AgilentExperiment ge = (AgilentExperiment)result.get(0);
		ge.setName(name);
		
		return ge;		
	}

	@Override
	protected void writeDataToStream(AgilentExperiment e, DataOutputStream dos) throws IOException {
		// copy the gpr file to the target directory
		String cfn = new File(e.FileName).getName();
		FileManager.copy(e.FileName, targetDirectory.getCanonicalPath()+"/"+cfn);
		writeString(dos, cfn);
	}

	@Override
	public void init() {
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
			getClass(),
			MC+".Agilent", 
			null,
			MC, 
			null, 
			"Florian Battke", 
			"battke@informatik.uni-tuebingen.de", 
			"Serializes Agilent-based experiments", 
			"Agilent"		
		);
	}

}
