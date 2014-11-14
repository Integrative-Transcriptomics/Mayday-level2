package mayday.GWAS.listeners;

import java.util.EventListener;

/**
 * @author jaeger
 *
 */
public interface DataStorageListener extends EventListener {
	
	/**
	 * @param dse
	 */
	public void dataChanged(DataStorageEvent dse);

}
