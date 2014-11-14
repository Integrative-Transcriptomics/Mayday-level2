package mayday.wapiti.experiments.impl.genepix;

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

public class GPRExperimentSerializer extends
		AbstractExperimentSerializer<GPRExperiment> {

	@SuppressWarnings("deprecation")
	@Override
	protected GPRExperiment loadDataFromStream(String name, String sourceDescription, TransMatrix tm, DataInputStream dis) throws IOException {
		// get filename from stream
		String filename = readString(dis);
		filename = targetDirectory.getCanonicalPath()+"/"+filename;
		String fVal = readString(dis);
		String bVal = readString(dis);
		
		LinkedList<Experiment> result = new LinkedList<Experiment>();
		LinkedList<String> files = new LinkedList<String>();
		files.add(filename);
		
		AbstractTask at = new GPRImportPlugin.GPRParserTask(files, result, tm, fVal, bVal);
		at.setProgressListener(progress);
		at.run();
		
		GPRExperiment ge = (GPRExperiment)result.get(0);
		ge.setName(name);
		
		return ge;		
	}

	@Override
	protected void writeDataToStream(GPRExperiment e, DataOutputStream dos) throws IOException {
		// copy the gpr file to the target directory
		String cfn = new File(e.GPRFileName).getName();
		FileManager.copy(e.GPRFileName, targetDirectory.getCanonicalPath()+"/"+cfn);
		writeString(dos, cfn);
		writeString(dos, e.fVal);
		writeString(dos, e.bVal);
	}

	@Override
	public void init() {
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
			getClass(),
			MC+".GPR", 
			null,
			MC, 
			null, 
			"Florian Battke", 
			"battke@informatik.uni-tuebingen.de", 
			"Serializes GPR-based experiments", 
			"GPR"		
		);
	}

}
