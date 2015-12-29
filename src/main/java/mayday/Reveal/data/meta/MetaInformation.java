package mayday.Reveal.data.meta;

import java.io.BufferedWriter;
import java.io.IOException;

public interface MetaInformation {
	
	public void serialize(BufferedWriter bw) throws IOException;
	
	public boolean deSerialize(String serial);

	public Class<?> getResultClass();
	
	public String getName();
}
