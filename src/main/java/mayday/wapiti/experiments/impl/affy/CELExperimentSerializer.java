package mayday.wapiti.experiments.impl.affy;

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
import affymetrix.fusion.cel.FusionCELData;

public class CELExperimentSerializer extends
		AbstractExperimentSerializer<CELExperiment> {

	@SuppressWarnings("deprecation")
	@Override
	protected CELExperiment loadDataFromStream(String name, String sourceDescription, TransMatrix tm, DataInputStream dis) throws IOException {
		// get filename from stream
		String filename = readString(dis);
		filename = targetDirectory.getCanonicalPath()+"/"+filename;
		
		FusionCELData cel = new FusionCELData();
		cel.setFileName(filename);
		cel.readHeader();		
		String cdfId = cel.getChipType();
		
		CDFData cdfd = CDFData.get(cdfId, true, new File(name).getParent());
		
		if (cdfd == null) 
			throw new RuntimeException("No CDF was supplied for "+cdfId+"."); 				
		
		LinkedList<Experiment> result = new LinkedList<Experiment>();
		LinkedList<String> files = new LinkedList<String>();
		files.add(filename);
		
		AbstractTask at = new CELImportPlugin.CELParserTask(0,1,result,files,tm,cdfd);
		at.setProgressListener(progress);
		at.run();
		
		CELExperiment ce = (CELExperiment)result.get(0);
		ce.setName(name);
		
		return ce;		
	}

	@Override
	protected void writeDataToStream(CELExperiment e, DataOutputStream dos) throws IOException {
		// copy the cel file to the target directory
		String cfn = new File(e.CELFileName).getName();
		FileManager.copy(e.CELFileName, targetDirectory.getCanonicalPath()+"/"+cfn);
		writeString(dos, cfn);
	}

	@Override
	public void init() {
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
			getClass(),
			MC+".CEL", 
			null,
			MC, 
			null, 
			"Florian Battke", 
			"battke@informatik.uni-tuebingen.de", 
			"Serializes CEL-based experiments", 
			"CEL"		
		);
	}

}
