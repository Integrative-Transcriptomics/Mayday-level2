package mayday.GWAS.io;

import java.io.IOException;

@SuppressWarnings("serial")
public class FileFormatUnknownException extends IOException {

	public FileFormatUnknownException() {
		super("File Format Unknown");
	}
}
