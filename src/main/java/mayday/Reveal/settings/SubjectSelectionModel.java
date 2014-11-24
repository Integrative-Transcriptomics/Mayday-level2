package mayday.Reveal.settings;

import java.util.ArrayList;
import java.util.List;

import mayday.Reveal.data.Subject;
import mayday.Reveal.data.SubjectList;

public class SubjectSelectionModel {

	private SubjectList availableSubjects;
	private List<Subject> selectedSubjects;
	
	public SubjectSelectionModel(SubjectList availableSubjects) {
		this.availableSubjects = availableSubjects;
		this.selectedSubjects = new ArrayList<Subject>();
	}
	
	public void setSelected(String id) {
		Subject s = availableSubjects.getSubject(id);
		if(s != null && !selectedSubjects.contains(s)) {
			selectedSubjects.add(s);
		}
	}
	
	public void removeSelected(String id) {
		Subject s = availableSubjects.getSubject(id);
		if(s != null && selectedSubjects.contains(s)) {
			selectedSubjects.remove(s);
		}
	}
	
	public SubjectList getSelectedSubjects() {
		SubjectList sl = new SubjectList(selectedSubjects);
		return sl;
	}
	
	public void clearSelection() {
		this.selectedSubjects.clear();
	}
	
	public String serialize() {
		int numSelected = 0;
		if(selectedSubjects != null) {
			numSelected = selectedSubjects.size();
		}
		return numSelected + " subjects have been selected.";
	}

	public SubjectList getAvailableSubjects() {
		return this.availableSubjects;
	}

	public SubjectList getUnselectedSubjects() {
		return getAvailableSubjects().copyRemoveAll(selectedSubjects);
	}

	public void setSelected(List<String> selected) {
		clearSelection();
		for(String s : selected)
			setSelected(s);
	}
}
