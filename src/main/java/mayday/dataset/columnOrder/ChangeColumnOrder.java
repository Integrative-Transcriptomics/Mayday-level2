package mayday.dataset.columnOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JOptionPane;

import mayday.core.DataSet;
import mayday.core.Experiment;
import mayday.core.MasterTable;
import mayday.core.Mayday;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.meta.gui.MIGroupSelectionDialog;
import mayday.core.meta.gui.MIGroupSelectionPanel.FilterCriteria;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.DatasetPlugin;
import mayday.core.settings.SettingDialog;
import mayday.core.tasks.AbstractTask;

public class ChangeColumnOrder extends AbstractPlugin implements DatasetPlugin{

	@Override
	public PluginInfo register() throws PluginManagerException {

		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.datasets.DatasetReorderColumns",
				new String[0],
				Constants.MC_DATASET,
				new HashMap<String, Object>(),
				"Alicia Owen",
				"alicia.owen@student.uni-tuebingen.de",
				"Changes the order of the columns within the expression matrix according to user's specifications",
				"Change Expression Matrix Column Order"
				);
		pli.addCategory("Transform");
		return pli;
	}

	@Override
	public void init() {
	}

	@Override
	public List<DataSet> run(List<DataSet> dataSets) {

		// set up the settings dialog
		final ChangeColumnOrderSettings settings;
		settings = new ChangeColumnOrderSettings();

		SettingDialog sd = new SettingDialog(Mayday.sharedInstance, "Change order of experiments ", settings);
		sd.showAsInputDialog();

		final DataSet dataSet = dataSets.get(0);
		final List<String> columnNames = new ArrayList<String>(dataSet.getMasterTable().getExperimentDisplayNames());

		// a comparator that sorts numbers numerically and strings lexicographically
		final Comparator<String> comp = new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {

				if(isInteger(o1) && !isInteger(o2)  ){

					return -1; 

				}else if(!isInteger(o1) && isInteger(o2)){

					return 1;

				}else if(isInteger(o1) && isInteger(o2)){

					int one = Integer.parseInt(o1);
					int two = Integer.parseInt(o2);
					return one-two;

				}
				return o1.compareTo(o2);
			}

		};


		if(sd.closedWithOK() && settings.getSelection().getSelectedIndex() == 0){

			//sort columns alphabetically by their names
			Collections.sort(columnNames, comp);
			reorderExperiments(dataSet, columnNames);

		}else if(sd.closedWithOK() && settings.getSelection().getSelectedIndex() == 1){
			//sort by meta information objects. Open dialog for user to specify the MIOs he wants to sort by.
			
			//start a MIO selection dialog
			MIGroupSelectionDialog dialog = new MIGroupSelectionDialog(dataSet.getMIManager());
			
			//apply filter to dialog to get only experiment MIOs
			dialog.addFilterCriteria(new  FilterCriteria() {
				public boolean pass(MIGroup mg) {
					for (Object o : mg.getObjects())
						if (o instanceof Experiment)
							return true;
					return false;
				}
			});
			
			//check if there are any experiment MIOs available
			if (dialog.getSelectableCount()==0)
				JOptionPane.showMessageDialog(null,"No experiment meta information found","No Experiment Meta Information",JOptionPane.ERROR_MESSAGE);
			
			// set up the dialog for experiment meta information objects
			dialog.setTitle("Experiment Meta information objects");
			dialog.setDialogDescription("Please select experiment meta information by which to sort. It should have the following format: column title followed by a new line. ");
			dialog.setVisible(true);

			if(dialog.isCanceled() || dialog.getSelection().size() == 0){
				return null;
			} else {
			MIGroupSelection<MIType> selection = dialog.getSelection();			//get the selected meta information group
			Set<Entry<Object, MIType>> setOfMIOs = selection.get(0).getMIOs();

			//turn set into a map (A), then sort the map by its values (B)  and put the ordered keys into the list colNames (C)
			//(A)
			Map<String, String> map = new HashMap<String, String>();
	
			for(Entry<Object, MIType> entry : setOfMIOs){
				map.put(entry.getKey().toString(), entry.getValue().toString());
			}
			
			//transform the map into a list
			List<Entry<String, String>> list = new ArrayList<Entry<String, String>>(map.entrySet());
			
			//(B) sort entries by their values
			Collections.sort(list, new Comparator<Entry<String, String>>() {

				@Override
				public int compare(Entry<String, String> o1, Entry<String, String> o2) {
					
					return ((o1.getValue()).compareTo(o2.getValue()));
				}
				
			});
			//(C)
			List<String> colNames  = new ArrayList<String>();
			
			for(Entry<String, String> entry : list){
				colNames.add(entry.getKey());
			}
	
			reorderExperiments(dataSet, colNames);

			}
		}else if (sd.closedWithOK() && settings.getSelection().getSelectedIndex() == 2){

			//sort with help of file specifying user-defined sorting
			AbstractTask task = new AbstractTask("Check user input validity") {

				@Override
				protected void initialize() {}

				@Override
				protected void doWork() throws Exception {
					List<String> inputColumnNames = (new ColumnOrderParser()).parse(settings.getSelectedPath());

					if(columnNames.size() != inputColumnNames.size()){

						throw new Exception("Number of provided column names does not match number of column names in data set.");

					}else{
						//check whether user's input was valid by sorting and comparing the column titles
						List<String>sort1 = new ArrayList<String>(columnNames);
						List<String>sort2 = new ArrayList<String>(inputColumnNames);

						Collections.sort(sort1);
						Collections.sort(sort2);

						if(sort1.equals(sort2)){

							reorderExperiments(dataSet, inputColumnNames);
						}else{
							throw new Exception("Provided column names do not match column names in data set.");
						}
					}

				}

			};

			task.start();
		}

		return null;
	}

	/**
	 * This method will reorder the columns of a data set.
	 * @param dataSet the data set which will be reordered
	 * @param newColumnNames a list containing column names in user-defined order (the table will be reordered to match this order)
	 */
	public void reorderExperiments(DataSet dataSet, List<String> newColumnNames){


		List<String> originalColumnNames = new ArrayList<String>(dataSet.getMasterTable().getExperimentDisplayNames());

		//map the column names to their original positions within the master table
		HashMap<String, Integer> map = new HashMap<String, Integer>();

		for(int i = 0; i < originalColumnNames.size(); i++){
			map.put(originalColumnNames.get(i), i);
		}

		int[] newIndices = new int[dataSet.getMasterTable().getNumberOfExperiments()]; 
		// at position [0], this array will contain the column number that will appear at position [0] after reordering

		int i = 0;
		//traverse list of sorted column names and fill array 'newIndices' with the corresponding columns' positions
		for(String name : newColumnNames){
			
			newIndices[i] = map.get(name);
			i++;
		}

		MasterTable mt = dataSet.getMasterTable();
		mt.reorderExperiments(newIndices);

	}

	/**
	 * A method to check whether a String is an integer or not
	 * @param s the string
	 * @return	whether the string is an integer or not
	 */
	public static boolean isInteger(String s) {
		try { 
			Integer.parseInt(s); 

		} catch(NumberFormatException e) { 
			return false; 
		}

		return true;
	}

}
