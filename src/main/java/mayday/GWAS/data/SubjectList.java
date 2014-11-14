package mayday.GWAS.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author jaeger
 *
 */
public class SubjectList implements Iterable<Subject> {

	private Subject[] subjects;
	private int counter = 0;
	
	private int numAffected = -1;
	
	private ArrayList<Subject> affectedSubjects;
	private ArrayList<Subject> unaffectedSubjects;
	
	/**
	 * @param numberOfLines
	 */
	public SubjectList(int numberOfLines) {
		this.subjects = new Subject[numberOfLines];
	}

	public SubjectList(List<Subject> selectedSubjects) {
		this.subjects = new Subject[selectedSubjects.size()];
		for(Subject s : selectedSubjects) {
			add(s);
		}
	}

	/**
	 * @param familyID
	 * @return all persons with the defined familyID
	 */
	public ArrayList<Subject> getFamily(int familyID) {
		ArrayList<Subject> list = new ArrayList<Subject>();
		for(int i = 0; i < subjects.length; i++) {
			if(subjects[i].getFamilyID().equals(familyID)) {
				list.add(subjects[i]);
			}
		}
		return list;
	}
	
	/**
	 * @param ID
	 * @return the person with the specified ID, or null if no person could be found with that ID
	 */
	public Subject getSubject(String ID) {
		for(int i = 0; i < subjects.length; i++) {
			if(subjects[i].getID().equals(ID)) {
				return subjects[i];
			}
		}
		return null;
	}
	
	/**
	 * @return all affected persons
	 */
	public ArrayList<Subject> getAffectedSubjects() {
		if(affectedSubjects != null)
			return affectedSubjects;
		affectedSubjects = new ArrayList<Subject>();
		for(int i = 0; i < subjects.length; i++) {
			if(subjects[i].affected()) {
				affectedSubjects.add(subjects[i]);
			}
		}
		return affectedSubjects;
	}
	
	/**
	 * @return all unaffected persons
	 */
	public ArrayList<Subject> getUnaffectedSubjects() {
		if(unaffectedSubjects != null)
			return unaffectedSubjects;
		unaffectedSubjects = new ArrayList<Subject>();
		for(int i = 0; i < subjects.length; i++) {
			if(!subjects[i].affected()) {
				unaffectedSubjects.add(subjects[i]);
			}
		}
		return unaffectedSubjects;
	}
	
	/**
	 * @return affected person indices
	 */
	public Integer[] getAffectedSubjectIndices() {
		ArrayList<Integer> affected = new ArrayList<Integer>();
		for(int i = 0; i < subjects.length; i++) {
			if(subjects[i].affected()) {
				affected.add(i);
			}
		}
		return affected.toArray(new Integer[0]);
	}
	
	/**
	 * @return unaffected person indices
	 */
	public Integer[] getUnaffectedSubjectIndices() {
		ArrayList<Integer> unaffected = new ArrayList<Integer>();
		for(int i = 0; i < subjects.length; i++) {
			if(!subjects[i].affected()) {
				unaffected.add(i);
			}
		}
		return unaffected.toArray(new Integer[0]);
	}
	
	/**
	 * @param p
	 */
	public void add(Subject p) {
		this.subjects[counter++] = p;
	}
	
	/**
	 * @param index
	 */
	public void remove(int index) {
		this.subjects[index] = null;
	}
	
	/**
	 * @param index
	 * @return person at index
	 */
	public Subject get(int index) {
		return this.subjects[index];
	}
	
	/**
	 * @return number of persons
	 */
	public int size() {
		return this.subjects.length;
	}

	@Override
	public Iterator<Subject> iterator() {
		Iterator<Subject> it = new Iterator<Subject>() {
			int position = 0;
			@Override
			public boolean hasNext() {
				return position < subjects.length;
			}
			@Override
			public Subject next() {
				return subjects[position++];
			}
			@Override
			public void remove() {
				SubjectList.this.remove(position);
			}
		};
		
		return it;
	}

	public void serialize(BufferedWriter bw) throws IOException {
		bw.append(String.valueOf(size()));
		bw.append("\n");
		for(Subject p : this) {
			bw.append(p.serialize());
			bw.append("\n");
		}
	}
	
	public int getNumberAffected() {
		if(numAffected == -1) {
			numAffected = getAffectedSubjects().size();
		}
		return numAffected;
	}
	
	public int getNumUnaffected() {
		if(numAffected == -1) {
			numAffected = getAffectedSubjects().size();
		}
		return size() - numAffected;
	}
	
	public String toString() {
		return "Subjects (" + size() + ")";
	}

	public SubjectList cloneProperly() {
		SubjectList pl = (SubjectList)clone();
		return pl;
	}
	
	public void clear(int newNumberOfSubjects) {
		this.subjects = new Subject[newNumberOfSubjects];
		this.numAffected = 0;
		this.counter = 0;
		this.affectedSubjects.clear();
		this.unaffectedSubjects.clear();
	}
	
    @SuppressWarnings("unchecked")
	public Object clone() {
        SubjectList l_subjectList = new SubjectList(size());
        if(affectedSubjects != null)
        	l_subjectList.affectedSubjects = (ArrayList<Subject>)this.affectedSubjects.clone();
        if(unaffectedSubjects != null)
        	l_subjectList.unaffectedSubjects = (ArrayList<Subject>)this.unaffectedSubjects.clone();
        
        l_subjectList.numAffected = numAffected;
        l_subjectList.counter = counter;
   
        for(int i = 0; i < subjects.length; i++)
        	l_subjectList.subjects[i] = subjects[i];
        
        return l_subjectList;
    }

	public SubjectList copyRemoveAll(List<Subject> selectedSubjects) {
		List<Subject> remaining = new ArrayList<Subject>();
		for(Subject s : this) {
			if(!selectedSubjects.contains(s)) {
				remaining.add(s);
			}
		}
		
		SubjectList rl = new SubjectList(remaining.size());
		for(Subject s : remaining) {
			rl.add(s);
		}
		
		return rl;
	}
}
