package mayday.Reveal.settings;

import mayday.Reveal.data.SubjectList;
import mayday.core.settings.generic.HierarchicalSetting;

public class SubjectListSetting extends HierarchicalSetting {

	private SubjectList personList;
	private SubjectList selectedSubjects;
	
	private SubjectSelectionSetting personNames;
	private SubjectSelectionModel model;
	
	public SubjectListSetting(SubjectList personList) {
		super("Subject Selection Setting");
		this.personList = personList;
		
		this.model = new SubjectSelectionModel(personList);
		addSetting(personNames = new SubjectSelectionSetting("Subject Selection", "Select the subjectes of choice", model));
	}
	
	public SubjectList getSelectedSubjects() {
		selectedSubjects = personNames.getSelectedSubjects();
		return selectedSubjects;
	}
	
	public void setModel(SubjectSelectionModel model) {
		this.model = model;
		this.personNames.setModel(model);
	}
	
	public SubjectListSetting clone() {
		SubjectListSetting pls = new SubjectListSetting(personList);
		pls.setModel(this.model);
		pls.fromPrefNode(this.toPrefNode());
		return pls;
	}
}
