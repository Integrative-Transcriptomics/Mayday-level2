package mayday.GWAS.viewmodel;

import mayday.vis3.model.ViewModelEvent;

@SuppressWarnings("serial")
public class RevealViewModelEvent extends ViewModelEvent {

	public static final int SNP_SELECTION_CHANGED = 0x11;
	public static final int PERSON_SELECTION_CHANGED = 0x12;
	
	public RevealViewModelEvent(Object source, int change) {
		super(source, change);
	}
}
