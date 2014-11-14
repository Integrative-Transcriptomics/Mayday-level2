package mayday.wapiti.experiments.impl.clone;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.wapiti.experiments.base.AbstractExperimentSerializer;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class ClonedExperimentSerializer extends
		AbstractExperimentSerializer<ClonedExperiment> {

	@Override
	protected ClonedExperiment loadDataFromStream(String name, String sourceDescription, TransMatrix tm, DataInputStream dis) throws IOException {
		// get base index from stream
		int baseIndex = dis.readInt();
		
		ClonedExperiment ce = new ClonedExperiment(tm, sourceDescription, baseIndex);
		ce.setName(name);
		
		return ce;		
	}

	@Override
	protected void writeDataToStream(ClonedExperiment e, DataOutputStream dos) throws IOException {
		dos.writeInt(e.getBaseIndex());
	}

	@Override
	public void init() {
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
			getClass(),
			MC+".CLONE", 
			null,
			MC, 
			null, 
			"Florian Battke", 
			"battke@informatik.uni-tuebingen.de", 
			"Serializes cloned experiments", 
			"Clone"		
		);
	}

}
