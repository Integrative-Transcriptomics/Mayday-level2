package mayday.Reveal.gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.ProjectHandler;
import mayday.Reveal.data.SNVList;

@SuppressWarnings("serial")
public class DataTreeModel extends DefaultTreeModel {

	private ProjectHandler projectHandler;
	
	private List<String> projectsInTree = new ArrayList<String>();
	private List<DefaultMutableTreeNode> projectNodes;
	
	public DataTreeModel(ProjectHandler projectHandler) {
		super(new DefaultMutableTreeNode("Root"));
		this.projectHandler = projectHandler;
		this.projectNodes = new LinkedList<DefaultMutableTreeNode>();
	}
	
	public void removeProject() {
		List<String> remainingProjects = new ArrayList<String>();
		
		for(int i = 0; i < projectHandler.numberOfProjects(); i++) {
			remainingProjects.add(projectHandler.get(i).getAttribute().getName());
		}
		
		for(int i = 0; i < projectsInTree.size(); i++) {
			if(!remainingProjects.contains(projectsInTree.get(i))) {
				if(projectHandler.getSelectedProject() == projectNodes.get(i).getUserObject()) {
					projectHandler.setSelectedProject(null);
				}
				removeProject(projectsInTree.get(i));
			}
		}
	}
	
	/**
	 * @param projectName
	 */
	public void removeProject(String projectName) {
		for(int i = 0; i < projectNodes.size(); i++) {
			if(projectsInTree.get(i).equals(projectName)) {
				projectNodes.remove(i);
				((DefaultMutableTreeNode) this.root).remove(i);
				projectsInTree.remove(i);
				//invoke a structure change event
				nodeStructureChanged(this.root);
				return;
			}
		}
	}
	
	public void removeProject(int index) {
		projectNodes.remove(index);
		((DefaultMutableTreeNode) this.root).remove(index);
		projectsInTree.remove(index);
		//invoke a structure change event
		nodeStructureChanged(this.root);
	}

	public void add(DataStorage project) {
		DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode(project);
		addChildren(projectNode, project);
		
		projectNodes.add(projectNode);
		projectsInTree.add(project.getAttribute().getName());
		
		((DefaultMutableTreeNode) this.root).add(projectNode);
		
		nodeStructureChanged(this.root);
	}
	
	public void updateProject(DataStorage project) {
		String projectName = project.getAttribute().getName();
		int index = projectsInTree.indexOf(projectName);
		if(index != -1) {
			DefaultMutableTreeNode projectNode = projectNodes.get(index);
			projectNode.removeAllChildren();
			addChildren(projectNode, project);
			reload(projectNode);
		}
	}
	
	private void addChildren(DefaultMutableTreeNode projectNode, DataStorage project) {
		DefaultMutableTreeNode subjectsNode = new DefaultMutableTreeNode(project.getSubjects());
		DefaultMutableTreeNode genesNode = new DefaultMutableTreeNode(project.getGenes());
		
		if(project.getSubjects() != null)
			projectNode.add(subjectsNode);
		if(project.getGenes() != null)
			projectNode.add(genesNode);
		
		DefaultMutableTreeNode snpsNode = new DefaultMutableTreeNode("SNVLists");
		projectNode.add(snpsNode);
		
		for(SNVList l : project.getSNVLists()) {
			DefaultMutableTreeNode extSNPsNode = new DefaultMutableTreeNode(l);
			snpsNode.add(extSNPsNode);
		}
	}

	public boolean contains(String name) {
		return projectsInTree.contains(name);
	}

	public int indexOf(String projectName) {
		return projectsInTree.indexOf(projectName);
	}
}
