package mayday.Reveal.settings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mayday.Reveal.data.SubjectList;
import mayday.core.Preferences;
import mayday.core.gui.MaydayDialog;
import mayday.core.settings.AbstractSettingComponent;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.typed.StringSetting;

public class SubjectSelectionSetting extends StringSetting {

	public enum LayoutStyle {
		FULL
	}
	
	protected LayoutStyle style = LayoutStyle.FULL;
	
	private SubjectSelectionModel model;
	
	private String buttonName; 
	
	public SubjectSelectionSetting(String Name, String Description, SubjectSelectionModel model) {
		super("", Description, model.serialize());
		this.model = model;
		this.buttonName = Name;
	}
	
	public String getButtonName() {
		return this.buttonName;
	}

	public SubjectList getSelectedSubjects() {
		return this.model.getSelectedSubjects();
	}
	
	public SettingComponent getGUIElement() {
		switch (style) {
		case FULL: return new SubjectSelectionSettingComponent(this, model);
		}
		return null;
	}
	
	public boolean fromPrefNode(Preferences prefNode) {
		// not doing anything
		return true;
	}

	public Preferences toPrefNode() {
		// not serializing anything
		Preferences myNode = Preferences.createUnconnectedPrefTree(getName(), "-not-serialized-");
		return myNode;
	}
	
	protected void showEditingDialog(SubjectSelectionModel model) {
		SubjectSelectionDialog dialog = new SubjectSelectionDialog(model);
		dialog.setModal(true);
		dialog.setVisible(true);	
	}
	
	public SubjectSelectionModel getModel() {
		return this.model;
	}
	
	public SubjectSelectionSetting clone() {
		return new SubjectSelectionSetting(getName(), getDescription(), getModel());
	}
	
	public SubjectSelectionSetting setLayoutStyle(LayoutStyle style) {
		this.style = style;
		return this;
	}
	
	public Component getMenuItem( final Window parent ) {
		final JMenuItem jmi = new JMenuItem("Subject Selection");
		jmi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showEditingDialog(getModel());	
			}
		});
		return jmi;
	}
	
	private class SubjectSelectionSettingComponent extends AbstractSettingComponent<SubjectSelectionSetting> {

		private SubjectSelectionModel model;
		private SubjectSelectionPanel panel;
		private JLabel text;
		
		public SubjectSelectionSettingComponent(SubjectSelectionSetting s, SubjectSelectionModel model) {
			super(s);
			this.model = model;
			this.text = new JLabel(model.serialize());
		}

		@Override
		public void stateChanged(SettingChangeEvent e) {
			if(SubjectSelectionSetting.this.model != null) {
				this.model = getModel();
				this.panel.setModel(this.model);
				this.text.setText(model.serialize());
			}
		}

		@Override
		protected Component getSettingComponent() {
			if (panel==null) {
				panel = new SubjectSelectionPanel(model);
				panel.setText(text);
			}
			return panel;
		}

		@Override
		protected String getCurrentValueFromGUI() {
			if(panel != null)
				return getModel().serialize();
			return null;
		}
	}
	
	@SuppressWarnings("serial")
	private class SubjectSelectionPanel extends JPanel {
		
		private SubjectSelectionModel model;
		private JButton showDialog;
		private JLabel text;
		
		public SubjectSelectionPanel(SubjectSelectionModel model) {
			this.model = model;
			this.showDialog = new JButton(getButtonName());
			this.showDialog.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					showEditingDialog(getModel());
					updateText();
				}
			});
			this.add(showDialog, BorderLayout.CENTER);
		}
		
		public void setText(JLabel text) {
			this.text = text;
			add(text, BorderLayout.SOUTH);
		}
		
		public void updateText() {
			text.setText(model.serialize());
		}

		public SubjectSelectionModel getModel() {
			return this.model;
		}
		
		public void setModel(SubjectSelectionModel model) {
			this.model = model;
		}
	}
	
	@SuppressWarnings({"serial", "rawtypes"})
	private class SubjectSelectionDialog extends MaydayDialog {
		
		private SubjectSelectionModel model;
		
		private JButton toUnselected;
		private JButton toSelected;
		private JButton selectAll;
		private JButton unselectAll;
		private JButton okButton;
		private JButton cancelButton;
		
		private JList selectedList;
		private JList unselectedList;
		private DefaultListModel selectedListModel;
		private DefaultListModel unselectedListModel;
		
		
		@SuppressWarnings("unchecked")
		public SubjectSelectionDialog(SubjectSelectionModel model) {
			this.model = model;
			
			this.setLayout(new BorderLayout());
			
			this.toUnselected = new JButton("Set Unselected");
			this.toSelected = new JButton("Set Selected");
			this.selectAll = new JButton("Select All");
			this.unselectAll = new JButton("Unselect All");
			
			selectedList = new JList(selectedListModel = new DefaultListModel());
			unselectedList = new JList(unselectedListModel = new DefaultListModel());
			
			SubjectList subjects = getModel().getUnselectedSubjects();
			SubjectList selectedSubjects = getModel().getSelectedSubjects();
			JLabel unselectedLabel = new JLabel("Unselected:");
			JLabel selectedLabel = new JLabel("Selected:");
			
			for(int i = 0; i < subjects.size(); i++) {
				unselectedListModel.addElement(subjects.get(i).getID());
			}
			
			for(int i = 0; i < selectedSubjects.size(); i++) {
				selectedListModel.addElement(selectedSubjects.get(i).getID());
			}
			
			JScrollPane selectedListScroller = new JScrollPane(selectedList);
			JScrollPane unselectedListScroller = new JScrollPane(unselectedList);
			
			toSelected.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int[] selected = unselectedList.getSelectedIndices();
					List<String> elements = new ArrayList<String>();
					for(int s : selected) {
						String element = (String)unselectedListModel.getElementAt(s);
						selectedListModel.addElement(element);
						elements.add(element);
					}
					for(String element : elements) {
						unselectedListModel.removeElement(element);
					}
				}
			});
			
			toUnselected.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int[] selected = selectedList.getSelectedIndices();
					List<String> elements = new ArrayList<String>();
					for(int s : selected) {
						String element = (String)selectedListModel.getElementAt(s);
						unselectedListModel.addElement(element);
						elements.add(element);
					}
					for(String element : elements) {
						selectedListModel.removeElement(element);
					}
				}
			});
			
			selectAll.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int size = unselectedListModel.getSize();
					for(int i = 0; i < size; i++) {
						selectedListModel.addElement(unselectedListModel.getElementAt(i));
					}
					unselectedListModel.removeAllElements();
				}
			});
			
			unselectAll.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int size = selectedListModel.getSize();
					for(int i = 0; i < size; i++) {
						unselectedListModel.addElement(selectedListModel.getElementAt(i));
					}
					selectedListModel.removeAllElements();
				}
			});
			
			JPanel left = new JPanel(new BorderLayout());
			left.add(unselectedLabel, BorderLayout.NORTH);
			left.add(unselectedListScroller, BorderLayout.CENTER);
			left.add(toSelected, BorderLayout.SOUTH);
			
			JPanel right = new JPanel(new BorderLayout());
			right.add(selectedLabel, BorderLayout.NORTH);
			right.add(selectedListScroller, BorderLayout.CENTER);
			right.add(toUnselected, BorderLayout.SOUTH);
			
			JPanel optionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			okButton = new JButton("Apply");
			okButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int size = selectedListModel.getSize();
					List<String> selected = new ArrayList<String>();
					for(int i = 0; i < size; i++)
						selected.add((String)selectedListModel.getElementAt(i));
					getModel().setSelected(selected);
					dispose();
				}
			});
			
			cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			
			optionButtonPanel.add(selectAll);
			optionButtonPanel.add(unselectAll);
			optionButtonPanel.add(okButton);
			optionButtonPanel.add(cancelButton);
			
			JPanel mainPanel = new JPanel(new GridLayout(1,2));
			mainPanel.add(left);
			mainPanel.add(right);
			
			this.setLayout(new BorderLayout());
			this.add(mainPanel, BorderLayout.CENTER);
			this.add(optionButtonPanel, BorderLayout.SOUTH);
			
			this.setTitle("Subject selection ...");
			this.setPreferredSize(new Dimension(400, 600));
			this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			pack();
		}
		
		public SubjectSelectionModel getModel() {
			return this.model;
		}
	}

	public void setModel(SubjectSelectionModel model) {
		this.model = model;
	}
}
