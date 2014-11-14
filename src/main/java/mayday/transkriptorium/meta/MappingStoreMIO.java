package mayday.transkriptorium.meta;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import mayday.core.meta.GenericMIO;
import mayday.core.meta.HugeMIO;
import mayday.core.meta.MIType;
import mayday.core.meta.gui.AbstractMIRenderer;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.transkriptorium.data.MappingStore;

public class MappingStoreMIO extends GenericMIO<MappingStore> implements HugeMIO { 

	public final static String _myType = "PAS.MIO.MappingStore";
	protected final static long VERSION=1; // indicate which version will be stored
	
	@Override
	public MIType clone() {
		throw new RuntimeException("Mapping Store meta info cannot be cloned");
	}

	public void init() {	}
	
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(),
				_myType,
				new String[0],
				Constants.MC_METAINFO,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Holds a mapping store for an experiment",
				"Mapping Store MIO"
				);
	}

	@Override
	public boolean deSerialize(int serializationType, String serializedForm) {
		// should never be called since we are serialized as HugeMIO
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AbstractMIRenderer getGUIElement() {
		return new MIRendererEmpty();
	}

	@Override
	public String getType() {
		return _myType;
	}

	@Override
	public String serialize(int serializationType) {
		// should never be called since we are serialized as HugeMIO
		return null;
	}
	
	public String toString() {
		return "mapped reads ("+Value.getTotalReadCount()+" reads at "+Value.getTotalMappingCount()+" positions)";
	}

	@Override
	public void deserializeHuge(InputStream is) {
		DataInputStream dis = new DataInputStream(is);
		Value = new MappingStore();
		try {
			dis.mark(16);
			long l = dis.readLong();
			if (l<0) // this is a version identifier
				Value.read(dis, (int)-l);
			else {
				dis.reset();
				Value.read(dis,0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void serializeHuge(OutputStream os) {
		DataOutputStream dos = new DataOutputStream(os);
		try {
			dos.writeLong(-VERSION); // indicate version 1 as negative read count
			Value.write(dos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
