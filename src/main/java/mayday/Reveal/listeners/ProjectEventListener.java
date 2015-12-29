package mayday.Reveal.listeners;

import java.util.EventListener;


/**
 * @author jaeger
 *
 */
public interface ProjectEventListener extends EventListener {

	/**
	 * @param pe
	 */
	public void projectChanged(ProjectEvent pe);

}
