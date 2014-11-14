package mayday.wapiti.experiments.impl.wiggle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.filemanager.FileManager;
import mayday.core.tasks.AbstractTask;
import mayday.wapiti.experiments.base.AbstractExperimentSerializer;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class WiggleExperimentSerializer extends
		AbstractExperimentSerializer<WiggleExperiment> {

	@SuppressWarnings("deprecation")
	@Override
	protected WiggleExperiment loadDataFromStream(String name, String sourceDescription, TransMatrix tm, DataInputStream dis) throws IOException {
		String species = readString(dis);
		
		List<String> filesFWD = readFiles(dis);
		List<String> filesBWD = readFiles(dis);
		List<String> filesBOTH = readFiles(dis);

		List<Experiment> result = new LinkedList<Experiment>();
		
		AbstractTask at = new WiggleImportPlugin.WiggleParserTask(filesFWD, filesBWD, filesBOTH, species, result, tm);
		at.setProgressListener(progress);
		at.run();
		
		WiggleExperiment ge = (WiggleExperiment)result.get(0);
		ge.setName(name);
		
		return ge;		
	}

	@Override
	protected void writeDataToStream(WiggleExperiment e, DataOutputStream dos) throws IOException {
		// copy the wiggle files to the target directory
		writeString(dos, e.wiggleData.species);
		writeFiles(dos, e.wiggleData.wigData_fwd.values());
		writeFiles(dos, e.wiggleData.wigData_bwd.values());
		writeFiles(dos, e.wiggleData.wigData_both.values());
	}
	
	protected void writeFiles(DataOutputStream dos, Collection<File> files) throws IOException {
		dos.writeInt(files.size());
		for (File file : files) {
			String cfn = file.getName();
			FileManager.copy(file, targetDirectory.getCanonicalPath()+"/"+cfn);
			writeString(dos, cfn);
		}
	}

	
	protected List<String> readFiles(DataInputStream dis) throws IOException {
		int size = dis.readInt();
		List<String> ret = new ArrayList<String>(size);
		for (int i=0; i!=size; ++i) {
			String file = readString(dis);
			file = targetDirectory.getCanonicalPath()+"/"+file;
			ret.add(file);
		}
		return ret;
	}
	
	@Override
	public void init() {
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
			getClass(),
			MC+".wiggle", 
			null,
			MC, 
			null, 
			"Florian Battke", 
			"battke@informatik.uni-tuebingen.de", 
			"Serializes wiggle-based experiments", 
			"Wiggle"		
		);
	}

}
