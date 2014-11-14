package mayday.GWAS.events;

import java.util.EventListener;

public interface SNPListListener extends EventListener {

	public void snpListChanged(SNPListEvent event);
}
