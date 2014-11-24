package mayday.Reveal.actions;

import java.util.HashMap;

import mayday.Reveal.data.ProjectHandler;
import mayday.clustering.IProgressState;
import mayday.core.tasks.AbstractTask;

public abstract class RevealTask extends AbstractTask implements IProgressState {

	protected ProjectHandler projectHandler = null;
	private HashMap<String, Object> stateEvent = new HashMap<String, Object>();
	
	public RevealTask(String title, ProjectHandler projectHandler) {
		super(title);
		this.projectHandler = projectHandler;
	}

	@Override
	public boolean reportCurrentFractionalProgressStatus(double FractionalStatus) {
		int value = (int) (FractionalStatus * 10000.0);
		setProgress(new Integer(value));
		return hasBeenCancelled();
	}

	@Override
	public void setMaxProgressStatus(int ProgressMax) {
		this.stateEvent.put("ACTIONS.MAX",new Integer(ProgressMax));
	}

	@Override
	public void setMinProgressStatus(int ProgressMin) {
		this.stateEvent.put("ACTIONS.MIN",new Integer(ProgressMin));
	}

	@Override
	public boolean reportCurrentAbsolutProgressStatus(int CurrentStatus) {
		this.stateEvent.put("ACTIONS.VALUE", new Integer(CurrentStatus));
		return hasBeenCancelled();
	}

	@Override
	public boolean increaseProgressStatus(int Progress) {
		int curStatus = ((Integer) this.stateEvent.get("ACTIONS.VALUE"));
		curStatus+=Progress;
		this.stateEvent.put("ACTIONS.VALUE", new Integer(curStatus));
		return hasBeenCancelled();
	}
}
