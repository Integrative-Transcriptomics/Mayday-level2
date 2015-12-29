package mayday.wapiti.experiments.generic.reads;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.wapiti.experiments.base.AbstractExperimentSerializer;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class ReadsExperimentSerializer_2 extends
		AbstractExperimentSerializer<ReadsExperiment> {

	@Override
	protected ReadsExperiment loadDataFromStream(String name, String sourceDescription, TransMatrix tm, DataInputStream dis)
			throws IOException {

		ReadsData rd = new ReadsData(); 
		rd.getFullData().read(dis,1); // read the more compact version here
		
		ReadsExperiment re = new ReadsExperiment(name, sourceDescription, tm);
		re.setInitialData(rd);
		
		return re;
	}

	@Override
	protected void writeDataToStream(ReadsExperiment e, DataOutputStream dos) throws IOException {
		e.initialData.getFullData().write(dos);
	}

	@Override
	public void init() {
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
			getClass(),
			MC+".mappedreadsfullinfo_v2", 
			null,
			MC, 
			null, 
			"Florian Battke", 
			"battke@informatik.uni-tuebingen.de", 
			"Serializes mapped-read-based experiments", 
			"mapped reads (full info)"
		);
	}



}
