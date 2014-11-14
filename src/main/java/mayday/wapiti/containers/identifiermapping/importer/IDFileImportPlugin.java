package mayday.wapiti.containers.identifiermapping.importer;

import java.util.List;

import mayday.wapiti.containers.identifiermapping.IdentifierMap;

public interface IDFileImportPlugin {
	
	public IdentifierMap importFrom(List<String> files);

}
