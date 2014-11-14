package mayday.GWAS.io.project;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import mayday.GWAS.io.project.factory.ProjectCreator;
import mayday.core.gui.MaydayDialog;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.generic.HierarchicalSetting;

public class ProjectMediator extends MaydayDialog implements ActionListener {
	
	private SettingComponent centerComponent;
	
	private BtnNext btnNext;
	private BtnBack btnBack;
	private BtnCancel btnCancel;
	
	private boolean closedWithOK = false;
	
	public ProjectMediator(String title, Window owner) {
		super(owner);
		setTitle(title);
		setMinimumSize(new Dimension(300, 400));
		setLayout(new BorderLayout());
		btnNext = new BtnNext();
		btnBack = new BtnBack();
		btnCancel = new BtnCancel();
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(btnBack);
		buttonPanel.add(btnNext);
		buttonPanel.add(btnCancel);
		
		btnBack.setEnabled(false);
		btnNext.setEnabled(true);
		btnCancel.setEnabled(true);
		
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.setModal(true);
		this.pack();
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4968015124148422837L;
	private ProjectDefinition pd;
	
	public void setInitialProjectDefinition(ProjectDefinition initial) {
		this.pd = initial;
		replace();
	}
	
	public void replace() {
		if(centerComponent != null)
			this.remove(centerComponent.getEditorComponent());
		
		HierarchicalSetting setting = pd.getSetting();
		this.centerComponent = setting.getGUIElement();
		
		JComponent editorComponent = centerComponent.getEditorComponent();
		this.add(editorComponent, BorderLayout.CENTER);
		
		pack();
	}
	
	public void next() {
		if(this.pd != null) {
			ProjectDefinition tmpPD = this.pd;
			boolean success = this.centerComponent.updateSettingFromEditor(false);
			
			if(success) {
				this.pd = this.pd.getNext();
				this.pd.setPrevious(tmpPD);
				this.btnBack.setEnabled(true);
				
				if(this.pd.isFinal()) {
					btnNext.setEnabled(false);
					this.pd.setFinalizeAction(new AbstractAction("Create Project") {
						/**
						 * 
						 */
						private static final long serialVersionUID = 6129901667624374518L;

						@Override
						public void actionPerformed(ActionEvent e) {
							closedWithOK = true;
							cancel();
						}
					});
				}
				
				this.replace();
			}
		}
	}
	
	public void back() {
		if(this.pd != null) {
			this.pd = this.pd.getPrevious();
			if(this.pd != null) {
				if(this.pd.getPrevious() == null) {
					this.btnBack.setEnabled(false);
				}
				this.btnNext.setEnabled(true);
				this.replace();
			}
		}
	}
	
	public void cancel() {
		this.removeAll();
		this.dispose();
		System.gc();
	}
	
	private class BtnNext extends JButton implements Command {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -2310181817225124898L;

		public BtnNext() {
			super("Next");
			addActionListener(ProjectMediator.this);
		}
		
		@Override
		public void execute() {
			ProjectMediator.this.next();
		}
	}
	
	private class BtnBack extends JButton implements Command {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1919037466334002318L;

		public BtnBack() {
			super("Back");
			addActionListener(ProjectMediator.this);
		}
		
		@Override
		public void execute() {
			ProjectMediator.this.back();
		}
	}
	
	private class BtnCancel extends JButton implements Command {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -4532688558252808636L;

		public BtnCancel() {
			super("Cancel");
			addActionListener(ProjectMediator.this);
		}
		
		@Override
		public void execute() {
			ProjectMediator.this.cancel();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if(o instanceof Command) {
			Command c = (Command)o;
			c.execute();
		}
	}
	
	public boolean closedWithOK() {
		return this.closedWithOK;
	}
	
	public void closeWithOK() {
		this.closedWithOK = true;
		cancel();
	}

	public ProjectCreator getProjectCreator() {
		return this.pd.getCreator();
	}
	
	public void finalizeConfiguration() {
		
	}
}
