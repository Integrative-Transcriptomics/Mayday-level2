package mayday.wapiti.experiments.impl.imagene;

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

public class IMGExperimentSerializer extends
		AbstractExperimentSerializer<IMGExperiment> {

	@SuppressWarnings("deprecation")
	@Override
	protected IMGExperiment loadDataFromStream(String name, String sourceDescription, TransMatrix tm, DataInputStream dis) throws IOException {
		// get filename from stream
		String filename1 = readString(dis);
		filename1 = targetDirectory.getCanonicalPath()+"/"+filename1;
		String filename2 = readString(dis);
		filename2 = targetDirectory.getCanonicalPath()+"/"+filename2;
		String method = readString(dis);
		
		LinkedList<Experiment> result = new LinkedList<Experiment>();
		LinkedList<String> files = new LinkedList<String>();
		files.add(filename1);
		files.add(filename2);
		
		AbstractTask at = new IMGImportPlugin.IMGParserTask(files, result, tm, method);
		at.setProgressListener(progress);
		at.run();
		
		IMGExperiment ge = (IMGExperiment)result.get(0);
		ge.setName(name);
		
		return ge;		
	}

	@Override
	protected void writeDataToStream(IMGExperiment e, DataOutputStream dos) throws IOException {
		// copy the img files to the target directory		
		String f1 = e.imgFiles[0];
		String f2 = e.imgFiles[1];		
		String cfn1 = new File(f1).getName();
		String cfn2 = new File(f2).getName();
		
		FileManager.copy(f1, targetDirectory.getCanonicalPath()+"/"+cfn1);
		FileManager.copy(f2, targetDirectory.getCanonicalPath()+"/"+cfn2);
		writeString(dos, cfn1);
		writeString(dos, cfn2);
		writeString(dos, e.method);
	}

	@Override
	public void init() {
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
			getClass(),
			MC+".IMG", 
			null,
			MC, 
			null, 
			"Florian Battke", 
			"battke@informatik.uni-tuebingen.de", 
			"Serializes Imagene-based experiments", 
			"Imagene"		
		);
	}

}
