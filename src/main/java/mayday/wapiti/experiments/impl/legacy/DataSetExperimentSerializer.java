package mayday.wapiti.experiments.impl.legacy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import mayday.core.Preferences;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.wapiti.experiments.base.AbstractExperimentSerializer;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentSetting;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.AbstractFeatureExpressionData;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionExperiment;
import mayday.wapiti.transformations.matrix.TransMatrix;

/** Stores a datasetexperiment such that it is later loaded as a featureexpressionexperiment */
public class DataSetExperimentSerializer extends
AbstractExperimentSerializer<FeatureExpressionExperiment> {


	public void init() {} 	

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".DataSet", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Stores Experiments derived from datasets", 
		"DataSet");
	}

	@Override
	protected FeatureExpressionExperiment loadDataFromStream(String name, String sourceDescription, TransMatrix tm, DataInputStream dis) throws IOException {
		String identifier = readString(dis); 
		int NoF = dis.readInt();
		DoubleVector d = new DoubleVector(NoF);
		for (int i=0; i!=NoF; ++i) {
			String pname = readString(dis);
			d.setName(i, pname);
			double val = dis.readDouble();
			d.set(i, val);
		}
		FeatureExpressionExperiment e = new ParsedFeatureExpressionExperiment(name, sourceDescription, tm, identifier, d);
		Preferences pn = Preferences.createUnconnectedPrefTree("null", "null");
		
		int len = dis.readInt();
		StringBuffer sb = new StringBuffer();
		for (int i=0; i!=len; ++i) 
			sb.append(dis.readChar());
		StringReader sr = new StringReader(sb.toString());
		BufferedReader br = new BufferedReader(sr);
		
		try {
			pn.loadFrom(br);
		} catch (Exception e1) {			
			e1.printStackTrace();
			throw new IOException(e1.getMessage());
		}
		e.getSetting().fromPrefNode(pn);
		return e;
	}

	@Override
	protected void writeDataToStream(FeatureExpressionExperiment e,
			DataOutputStream dos) throws IOException {
		writeString(dos, e.getIdentifier());
		// write number of lines
		dos.writeInt((int)e.getInitialState().getNumberOfFeatures());
		
		AbstractVector d = ((FeatureExpressionData)e.getInitialData()).getExpressionVector(0);		
		for (int i=0; i!=d.size(); ++i) {
			writeString(dos, d.getName(i));
			dos.writeDouble(d.get(i));
		}	
		
		StringWriter sw = new StringWriter();
		BufferedWriter bw = new BufferedWriter(sw);
		
		try {
			e.getSetting().toPrefNode().saveTo(bw);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new IOException(e1.getMessage());
		}
		
		StringBuffer buf = sw.getBuffer();
		dos.writeInt(buf.length());
		for (int i = 0; i!=buf.length(); ++i)
			dos.writeChar(buf.charAt(i));
	}

	public static class ParsedFeatureExpressionExperiment extends FeatureExpressionExperiment {

		protected String id;
		protected FeatureExpressionData initialData;
		
		public ParsedFeatureExpressionExperiment(String name, String sourceDesc, TransMatrix tm, String id, DoubleVector d) {
			super(name, sourceDesc, tm);
			this.id=id;
			initialData = new AbstractFeatureExpressionData(d);
		}

		public String getIdentifier() {
			return id;
		}

		@Override
		public AbstractExperimentSerializer<FeatureExpressionExperiment> getSerializer() {
			return new DataSetExperimentSerializer();
		}

		public ExperimentData getInitialData() {
			return initialData;
		}

		@Override
		protected ExperimentSetting makeSetting(String name) {
			return new DataSetExperimentSetting(this, name);
		}

		@Override
		protected ExperimentState makeInitialState() {
			return new DataSetExperimentInitialState(this);
		}
	};	


}
