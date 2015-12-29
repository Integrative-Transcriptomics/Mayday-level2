package mayday.tiala.pairwise.data.viewmodel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.DataSet;
import mayday.vis3.model.ManipulationMethod;
import mayday.vis3.model.ProbeDataManipulator;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.Visualizer;
import mayday.vis3.model.manipulators.None;

public class EnrichedNonModifyingViewModel extends EnrichedViewModel {	


	public EnrichedNonModifyingViewModel(Visualizer viz, DataSet ds) {
		super(viz, ds);
		dataManipulator = new NoMethodProbeDataManipulator();	
		dataManipulator.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				eventfirer.fireEvent(new ViewModelEvent(EnrichedNonModifyingViewModel.this, ViewModelEvent.DATA_MANIPULATION_CHANGED));	
			}
		});
	}
	
	public class NoMethodProbeDataManipulator extends ProbeDataManipulator {
		
		public NoMethodProbeDataManipulator() {
			manipulationMethods = new ManipulationMethod[]{
					new None()
			};
			setManipulation0(manipulationMethods[0]);
		}
		
		public void setManipulation(ManipulationMethod manipulationMethod) {
		}
		
		protected void setManipulation0(ManipulationMethod manipulationMethod) {
			super.setManipulation(manipulationMethod);
		}
		
	}
	
	
}
