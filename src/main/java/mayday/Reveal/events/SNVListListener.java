package mayday.Reveal.events;

import java.util.EventListener;

public interface SNVListListener extends EventListener {

	public void snpListChanged(SNVListEvent event);
}
