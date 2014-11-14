package mayday.exportjs;

import java.io.File;

import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.settings.typed.StringSetting;

public class OutputWriterSetting extends HierarchicalSetting {

	private OutputWriter outputWriter;

	private PathSetting path;
	private StringSetting htmlName;
	private BooleanHierarchicalSetting externalDataFile;
	private StringSetting dataName;
	private BooleanHierarchicalSetting externalLibFile;
	private StringSetting protovisLibName;


	public OutputWriterSetting(OutputWriter outputWriter){
		super("Save Files");

		this.outputWriter = outputWriter;

		this.path = new PathSetting("Path", null, outputWriter.getHtmlFile().getParent(), true, false, false);
		this.htmlName = new StringSetting("Html File", null, outputWriter.getHtmlFile().getName());
		this.externalDataFile = new BooleanHierarchicalSetting("External Data File", null, outputWriter.isExternalDataFile());
		this.dataName = new StringSetting("Data File", null, outputWriter.getDataFile().getName());
		this.externalDataFile.addSetting(this.dataName);
		this.externalLibFile = new BooleanHierarchicalSetting("External Lib File", null, outputWriter.isExternalLibFile());
		this.protovisLibName = new StringSetting("Protovis Library", null, outputWriter.getProtovisLibFile().getName());
		this.externalLibFile.addSetting(this.protovisLibName);
		this.addSetting(this.path).addSetting(this.htmlName).addSetting(this.externalDataFile).addSetting(this.externalLibFile);
	}

	public void updateOutputWriter(){
		
		String htmlName = this.htmlName.getStringValue();
		if (!htmlName.isEmpty()){
			this.outputWriter.setHtmlFile(new File(this.path.getValueString(), htmlName));
		}

		String dataName = this.dataName.getStringValue();
		if (!dataName.isEmpty()){
			this.outputWriter.setDataFile(new File(this.path.getValueString(), dataName));
		}

		String protovisLibName = this.protovisLibName.getStringValue();
		if (!protovisLibName.isEmpty()){
			this.outputWriter.setProtovisLibFile(new File(this.path.getValueString(), protovisLibName));
		}

		this.outputWriter.setExternalDataFile(this.externalDataFile.getBooleanValue());
		this.outputWriter.setExternalLibFile(this.externalLibFile.getBooleanValue());
	}

	public OutputWriterSetting clone() {
		OutputWriterSetting  clonedSetting;
		clonedSetting = new OutputWriterSetting(this.outputWriter);
		return clonedSetting;
	}
}
