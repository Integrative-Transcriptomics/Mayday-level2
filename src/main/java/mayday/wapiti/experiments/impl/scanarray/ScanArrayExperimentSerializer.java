package mayday.wapiti.experiments.impl.scanarray;

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

public class ScanArrayExperimentSerializer extends
		AbstractExperimentSerializer<ScanArrayExperiment> {

	@SuppressWarnings("deprecation")
	protected ScanArrayExperiment loadDataFromStream(String name, String sourceDescription, TransMatrix tm, DataInputStream dis) throws IOException {
		// get filename from stream
		String filename = readString(dis);
		filename = targetDirectory.getCanonicalPath()+"/"+filename;
		String fVal = readString(dis);
		String bVal = readString(dis);
		
		LinkedList<Experiment> result = new LinkedList<Experiment>();
		LinkedList<String> files = new LinkedList<String>();
		files.add(filename);
		
		AbstractTask at = new ScanArrayImportPlugin.ScanArrayParserTask(files, result, tm, fVal, bVal);
		at.setProgressListener(progress);
		at.run();
		
		ScanArrayExperiment ge = (ScanArrayExperiment)result.get(0);
		ge.setName(name);
		
		return ge;		
	}

	@Override
	protected void writeDataToStream(ScanArrayExperiment e, DataOutputStream dos) throws IOException {
		// copy the gpr file to the target directory
		String cfn = new File(e.FileName).getName();
		FileManager.copy(e.FileName, targetDirectory.getCanonicalPath()+"/"+cfn);
		writeString(dos, cfn);
		writeString(dos, e.fVal);
		writeString(dos, e.bVal);
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
			getClass(),
			MC+".ScanArray", 
			null,
			MC, 
			null, 
			"Florian Battke", 
			"battke@informatik.uni-tuebingen.de", 
			"Serializes ScanArray-based experiments", 
			"ScanArray"		
		);
	}

	public void init() {};
	
}
