package mayday.wapiti.experiments.base;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import mayday.core.Preferences;
import mayday.core.io.EfficientBufferedReader;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.tasks.ProgressListener;
import mayday.wapiti.Constants;
import mayday.wapiti.transformations.matrix.TransMatrix;

public abstract class AbstractExperimentSerializer<T extends Experiment> extends AbstractPlugin {

	public final static String MC = Constants.MCBASE+"Serializer";

	protected File targetDirectory;
	protected ProgressListener progress;

	protected final static void writeString(DataOutputStream dos, String s) throws IOException {
		dos.writeChars(s);
		dos.writeChar('\n');
	}
	
	protected final static String readString(DataInputStream dis) throws IOException {
		StringBuffer sb = new StringBuffer();
		char c;
		while ((c = dis.readChar())!='\n')
			sb.append(c);
		return sb.toString();
	}
	
	/* serialization ************************************/

	protected abstract void writeDataToStream(T e, DataOutputStream dos) throws IOException;
	protected abstract T loadDataFromStream(String name, String sourceDescription, TransMatrix tm, DataInputStream dis) throws IOException;

	public final void writeToStream( T e, OutputStream os, File targetDir, ProgressListener pl ) throws IOException {
		
		DataOutputStream dos = new DataOutputStream(os);
		
		// write my pluma id and my settings
		try {
			writeString( dos, register().getIdentifier() );
		} catch (PluginManagerException e1) {
			e1.printStackTrace();			
		}
		writeString( dos, e.getName() );
		writeString( dos, e.getSourceDescription() );
		
		progress = pl;
		targetDirectory = targetDir;
		writeDataToStream(e,dos);
		dos.flush();
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
		try {
			if (getSetting()!=null) {
				getSetting().toPrefNode().saveTo(bw);
			} 
		} catch (Exception exe) {
			exe.printStackTrace();
			throw new RuntimeException(exe.getMessage());
		}
		bw.flush();
	}

	public final static Experiment loadFromStream( InputStream is, TransMatrix tm, File targetDir, ProgressListener pl ) throws IOException {
		
		DataInputStream dis = new DataInputStream(is);
		
		String plumaID = readString(dis); 
		
		// fix access for old wapiti files		
		plumaID = plumaID.replace("WAPITI", "SeaSight"); 
		
		AbstractExperimentSerializer<?> serializer = 
			(AbstractExperimentSerializer<?>)PluginManager.getInstance().getPluginFromID(plumaID).newInstance();
		
		String name = readString(dis);
		String desc = readString(dis);
		
		serializer.progress = pl;
		serializer.targetDirectory=targetDir;
		Experiment exper = serializer.loadDataFromStream(name, desc, tm, dis);
		
		EfficientBufferedReader br = new EfficientBufferedReader( new InputStreamReader(is) );

		br.mark(2);
		if (br.read()!=-1 && exper.getSetting()!=null) {
			br.reset();
			Preferences prefNode = Preferences.createUnconnectedPrefTree("null","null");
			try {
				prefNode.loadFrom(br, true);
			}  catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
			exper.getSetting().fromPrefNode(prefNode);
		}			
		
		return exper;
	}
	

}
