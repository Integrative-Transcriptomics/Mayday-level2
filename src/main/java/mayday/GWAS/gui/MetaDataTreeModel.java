package mayday.GWAS.gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.ProjectHandler;
import mayday.GWAS.data.meta.MetaInformation;
import mayday.GWAS.data.meta.MetaInformationManager;

@SuppressWarnings("serial")
public class MetaDataTreeModel extends DefaultTreeModel {
	
	private ProjectHandler projectHandler;
	
	private List<String> projectsInTree = new ArrayList<String>();
	private List<DefaultMutableTreeNode> projectNodes;
	
	public MetaDataTreeModel(ProjectHandler projectHandler) {
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
				removeProject(i);
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
				projectsInTree.remove(i);
				return;
			}
		}
	}
	
	public void removeProject(int index) {
		projectNodes.remove(index);
		projectsInTree.remove(index);
	}

	public void add(DataStorage project) {
		DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode(project.getAttribute().getName());
		projectNodes.add(projectNode);
		projectsInTree.add(project.getAttribute().getName());
	}
	
	public void updateProject(DataStorage project) {
		if(project == null) {
			((DefaultMutableTreeNode)root).removeAllChildren();
			nodeStructureChanged(root);
			return;
		}
		
		String projectName = project.getAttribute().getName();
		int index = projectsInTree.indexOf(projectName);
		if(index != -1) {
			((DefaultMutableTreeNode)root).removeAllChildren();
			DefaultMutableTreeNode projectNode = projectNodes.get(index);
			((DefaultMutableTreeNode)root).add(projectNode);
			projectNode.removeAllChildren();
			addChildren(projectNode, project);
			nodeStructureChanged(root);
		}
	}
	
	private void addChildren(DefaultMutableTreeNode projectNode, DataStorage project) {
		MetaInformationManager mim = project.getMetaInformationManager();
		
		for(String key : mim.keySet()) {
			DefaultMutableTreeNode newKeyNode = new DefaultMutableTreeNode(key);
			
			List<MetaInformation> list = mim.get(key);
			
			for(MetaInformation info : list) {
				DefaultMutableTreeNode mInfoNode = new DefaultMutableTreeNode(info);
				newKeyNode.add(mInfoNode);
			}
			
			projectNode.add(newKeyNode);
		}
	}

	public boolean contains(String name) {
		return projectsInTree.contains(name);
	}

	public int indexOf(String projectName) {
		return projectsInTree.indexOf(projectName);
	}
}
