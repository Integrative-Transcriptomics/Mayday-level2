package mayday.GWAS.listeners;

import java.util.Set;

import mayday.GWAS.data.ProjectHandler;
import mayday.core.EventFirer;

/**
 * @author jaeger
 *
 */
public class ProjectEventHandler implements DataStorageListener {

	private ProjectHandler projectHandler;
	
	protected EventFirer<ProjectEvent, ProjectEventListener> eventfirer = new EventFirer<ProjectEvent, ProjectEventListener>() {
		protected void dispatchEvent(ProjectEvent event, ProjectEventListener listener) {
			listener.projectChanged(event);
		}		
	};
	
	/**
	 * @param projectHandler
	 */
	public ProjectEventHandler(ProjectHandler projectHandler) {
		this.projectHandler = projectHandler;

		for(int i = 0; i < projectHandler.numberOfProjects(); i++) {
			this.projectHandler.get(i).addDataStorageListener(this);
		}
	}

	@Override
	public void dataChanged(DataStorageEvent dse) {
		fireProjectChanged(this, ProjectEvent.PROJECT_CHANGED);
	}
	
	/**
	 * @param listener
	 */
	public void addProjectEventListener(ProjectEventListener listener) {
		this.eventfirer.addListener(listener);
	}
	
	/**
	 * @param listener
	 */
	public void removeProjectEventListener(ProjectEventListener listener) {
		this.eventfirer.removeListener(listener);
	}
	
	/**
	 * @param source 
	 * @param change
	 */
	public void fireProjectChanged(Object source, int change) {
		synchronized(this) {
			eventfirer.fireEvent(new ProjectEvent(source, change));
		}
	}
	
	public Set<ProjectEventListener> getProjectEventListeners() {
		return this.eventfirer.getListeners();
	}
}
