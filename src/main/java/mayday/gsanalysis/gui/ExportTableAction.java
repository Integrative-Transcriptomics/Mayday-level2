package mayday.gsanalysis.gui;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import mayday.core.LastDirListener;
import mayday.core.MaydayDefaults;
import mayday.core.gui.MaydayDialog;
import mayday.core.structures.Pair;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.gsanalysis.AbstractGSAnalysisPlugin;
import mayday.gsanalysis.Geneset;
import mayday.gsanalysis.Result;
import mayday.gsanalysis.gsea.GSEAEnrichment;
import mayday.gsanalysis.gsea.GSEAPlugin;

@SuppressWarnings("serial")
public class ExportTableAction extends AbstractAction  {

	private Result result;
	private AbstractGSAnalysisPlugin plugin;
	private JTable tabular;
	private String preferences;
	
	public ExportTableAction(JTable tabular, Result result, AbstractGSAnalysisPlugin plugin, String preferences) {
		super("Export table...");
		this.result=result;
		this.plugin=plugin;
		this.tabular=tabular;
		this.preferences=preferences;
	}
	
	public void actionPerformed(ActionEvent e) {
		new ExportTabularProbeListViewerDialog().setVisible(true);
	}
	
	
	public void export( String fileName, boolean includeHeader, boolean selectionOnly, boolean leadingEdge )
	throws FileNotFoundException, IOException
	{	FileWriter l_fileWriter = new FileWriter( fileName );
	
	
		//write preferences
		l_fileWriter.write("#");
		l_fileWriter.write(preferences.replaceFirst("<html>", "").replaceFirst("</html>", "").replace("<p/>","\n").replace("\n", "\n#"));
		l_fileWriter.write("\n");
		TableModel model = tabular.getModel();
		int l_rows = model.getRowCount();
		int l_columns = 0;

		

		l_columns = model.getColumnCount();

		if ( includeHeader ) {
			for (int i=0; i!=l_columns; ++i) {
				if(i!=0) {
					l_fileWriter.write("\t");
				}
				l_fileWriter.write(tabular.getColumnName(i));
			}
			
			if(leadingEdge) {
				l_fileWriter.write("\t");
				l_fileWriter.write("Leading edge");
			}
			l_fileWriter.write( "\n" );
			
		}      

		if(selectionOnly) {
			for(int i:tabular.getSelectedRows()) {
				for ( int j = 0; j < l_columns; ++j ) {
					Object l_value = model.getValueAt( i, j ); 
					if ( l_value != null )           
						l_fileWriter.write( l_value.toString() );
					else
						l_fileWriter.write( "" );
					if ( j < l_columns - 1 ) 
						l_fileWriter.write("\t");
				}
				
				if(leadingEdge) {
					Pair<String,String> classes = new Pair<String,String>("","");
					l_fileWriter.write("\t");
					String class1 = (String)tabular.getValueAt(i, 0);
					String class2 = (String)tabular.getValueAt(i, 1);
					classes.set(class1, class2);
					String genesetName = (String)tabular.getValueAt(i,2);
				
					Geneset geneset = null;
					for(Geneset g:plugin.getGenesets()) {
						if(g.getName().equals(genesetName)) {
							geneset=g;
							break;
						}
					}
					PermutableMatrix leadingEdgeMatrix=((GSEAEnrichment) result.getEnrichment(geneset,classes)).getLeadingEdge();
					for(int row=0;row!=leadingEdgeMatrix.nrow();row++) {
						l_fileWriter.write(leadingEdgeMatrix.getRowName(row));
						if(row!=leadingEdgeMatrix.nrow()) {
							l_fileWriter.write(", ");
						}
					}
					
				}
				l_fileWriter.write( "\n" );
			}
		}
		else {
			for ( int i = 0; i < l_rows; ++i ) {
				for ( int j = 0; j < l_columns; ++j ) {
					Object l_value = model.getValueAt( i, j ); 
					if ( l_value != null )           
						l_fileWriter.write( l_value.toString() );
					else
						l_fileWriter.write( "" );
					if ( j < l_columns - 1 ) 
						l_fileWriter.write("\t");
				}
				if(leadingEdge) {
					Pair<String,String> classes = new Pair<String,String>("","");
					l_fileWriter.write("\t");
					String class1 = (String)tabular.getValueAt(i, 0);
					String class2 = (String)tabular.getValueAt(i, 1);
					classes.set(class1, class2);
					String genesetName = (String)tabular.getValueAt(i,2);
				
					Geneset geneset = null;
					for(Geneset g:plugin.getGenesets()) {
						if(g.getName().equals(genesetName)) {
							geneset=g;
							break;
						}
					}
					PermutableMatrix leadingEdgeMatrix=((GSEAEnrichment) result.getEnrichment(geneset,classes)).getLeadingEdge();
					for(int row=0;row!=leadingEdgeMatrix.nrow();row++) {
						l_fileWriter.write(leadingEdgeMatrix.getRowName(row));
						if(row!=leadingEdgeMatrix.nrow()) {
							l_fileWriter.write(", ");
						}
					}
				}
				l_fileWriter.write( "\n" );
			}
		}
		l_fileWriter.close();    
	}
	
	public class ExportTabularProbeListViewerDialog
	extends MaydayDialog
	implements ActionListener
	{
		private JCheckBox includeHeaderCheckBox;
		private JCheckBox selectionOnlyCheckBox;
		private JCheckBox leadingEdgeCheckBox;
		private boolean includeHeader;
		private boolean selectionOnly;
		private boolean leadingEdge;

		public ExportTabularProbeListViewerDialog( )
		{
			setModal( true );
			setTitle( "Export" );
			init();
		}


		protected void init()
		{
			this.includeHeader = true;
			this.leadingEdge=false;
			
			// init checkboxes for header selection and identifiers only
			this.includeHeaderCheckBox = new JCheckBox( "Include Header" );
			this.includeHeaderCheckBox.setMnemonic( KeyEvent.VK_H );
			this.includeHeaderCheckBox.setSelected( this.includeHeader );   
			this.includeHeaderCheckBox.addActionListener( this );


			this.selectionOnlyCheckBox = new JCheckBox( "Selection Only" );
			this.selectionOnlyCheckBox.setMnemonic( KeyEvent.VK_L );
			this.selectionOnlyCheckBox.setSelected( this.selectionOnly );   
			this.selectionOnlyCheckBox.addActionListener( this );

			if(plugin.getName().equals("GSEA")&&((GSEAPlugin)plugin).getPermutationTests()) {
				this.leadingEdgeCheckBox = new JCheckBox( "Include leading edge" );
				this.leadingEdgeCheckBox.setMnemonic( KeyEvent.VK_L );
				this.leadingEdgeCheckBox.setSelected( this.leadingEdge );   
				this.leadingEdgeCheckBox.addActionListener( this );
			}	
			// create ok and cancel buttons
			JButton l_okButton = new JButton( new OkAction() );
			l_okButton.setMnemonic( KeyEvent.VK_ENTER );

			JButton l_cancelButton = new JButton( new CancelAction() );       
			l_cancelButton.setMnemonic( KeyEvent.VK_ESCAPE );


			// create the main content pane
			JPanel l_contentPane = new JPanel();
			l_contentPane.setLayout( new BoxLayout( l_contentPane, BoxLayout.Y_AXIS ) );
			l_contentPane.setBorder( BorderFactory.createEmptyBorder( 20, 20, 20, 20 ) );

			// create a box to hold content settings
			Box l_dimensionsSubHBox = Box.createHorizontalBox();

			l_dimensionsSubHBox.add( new JLabel( "Content" ) );
			l_dimensionsSubHBox.add( Box.createHorizontalGlue() );

			// create a box thats holds the "include header" check box
			Box l_includeHeaderBox = Box.createHorizontalBox();
			l_includeHeaderBox.add( this.includeHeaderCheckBox );
			l_includeHeaderBox.add( Box.createHorizontalGlue() );

			// create a box thats holds the "selection only" check box
			Box l_selectionOnlyBox = Box.createHorizontalBox();
			l_selectionOnlyBox.add( this.selectionOnlyCheckBox );
			l_selectionOnlyBox.add( Box.createHorizontalGlue() );
			
			Box l_leadingEdgeBox = null;
			if(plugin.getName().equals("GSEA")&&((GSEAPlugin)plugin).getPermutationTests()) {
				// create a box thats holds the "leading Edge" check box
				l_leadingEdgeBox = Box.createHorizontalBox();
				l_leadingEdgeBox.add( this.leadingEdgeCheckBox );
				l_leadingEdgeBox.add( Box.createHorizontalGlue() );
			}
			// create two boxes to hold the content settings
			Box l_dimensionsVBox = Box.createVerticalBox();    
			Box l_dimensionsHBox = Box.createHorizontalBox();

			l_dimensionsVBox.add( l_dimensionsSubHBox );
			l_dimensionsVBox.add( l_includeHeaderBox );
			l_dimensionsVBox.add( l_selectionOnlyBox );
			if(plugin.getName().equals("GSEA")&&((GSEAPlugin)plugin).getPermutationTests()) {
				l_dimensionsVBox.add( l_leadingEdgeBox );
			}
			l_dimensionsVBox.add( Box.createVerticalGlue() );

			l_dimensionsHBox.add( l_dimensionsVBox );
			l_dimensionsHBox.add( Box.createVerticalGlue() );


			// create the upper portion of the dialog
			Box l_upperBox = Box.createHorizontalBox();

			l_upperBox.add( Box.createHorizontalGlue() );
			l_upperBox.add( new JLabel( "      " ) );
			l_upperBox.add( l_dimensionsHBox );
			l_upperBox.add( Box.createHorizontalGlue() );

			// add the upper part of the dialog to the main content pane
			l_contentPane.add( l_upperBox );

			// create a box for ok and cancel buttons
			Box l_buttonPanel = Box.createHorizontalBox();

			l_buttonPanel.add( Box.createHorizontalGlue() );
			l_buttonPanel.add( l_cancelButton );
			l_buttonPanel.add( new JLabel( " " ) );
			l_buttonPanel.add( l_okButton );

			// make the ok button the default button of the dialog
			getRootPane().setDefaultButton( l_okButton );

			// add the buttons to the main content pane
			l_contentPane.add( Box.createVerticalStrut( 10 ) );
			l_contentPane.add( l_buttonPanel );   

			// add the main content pane to the dialog's content pane
			getContentPane().add( l_contentPane, BorderLayout.CENTER );			

			// set the size of the dialog
			setSize( getPreferredSize().width + 10, getPreferredSize().height + 20 );

			setResizable( false );		
		}


		public void actionPerformed( ActionEvent event )
		{
			if ( event.getSource() == includeHeaderCheckBox )
			{
				this.includeHeader = includeHeaderCheckBox.isSelected();
			}

			if ( event.getSource() == selectionOnlyCheckBox )
			{
				this.selectionOnly = selectionOnlyCheckBox.isSelected();
			}
			
			if ( event.getSource() == leadingEdgeCheckBox )
			{
				this.leadingEdge = leadingEdgeCheckBox.isSelected();
			}
		}


		protected class OkAction
		extends AbstractAction	
		{
			public OkAction()
			{
				super( "OK" );
			}


			public void actionPerformed( ActionEvent event )
			{
				//MZ 23.01.04
				String s_lastExportPath=
					MaydayDefaults.Prefs.NODE_PREFS.get(
							MaydayDefaults.Prefs.KEY_LASTSAVEDIR,
							MaydayDefaults.Prefs.DEFAULT_LASTSAVEDIR
					);

				//old: JFileChooser l_chooser;
				JFileChooser l_chooser=new JFileChooser();
				l_chooser.addActionListener(new LastDirListener());

				/*
				if ( MaydayDefaults.s_lastExportPath.equals( "" ) )
				{
					l_chooser = new JFileChooser();
				}
				else
				{
					l_chooser = new JFileChooser( MaydayDefaults.s_lastExportPath );
				}//*/

				if(!s_lastExportPath.equals(""))
				{
					l_chooser.setCurrentDirectory(new File(s_lastExportPath));
				}

				//end MZ


				String l_defaultFileName = "Table" + plugin.getName();
				l_defaultFileName = l_defaultFileName.toLowerCase();
				l_defaultFileName = l_defaultFileName.replace( ' ', '_' ); // replace spaces
				l_defaultFileName += "." + MaydayDefaults.DEFAULT_TABULAR_EXPORT_EXTENSION;

				l_chooser.setSelectedFile( new File( l_defaultFileName ) );

				int l_option = l_chooser.showSaveDialog( (Component)event.getSource() );

				if ( l_option  == JFileChooser.APPROVE_OPTION )
				{
					String l_fileName = l_chooser.getSelectedFile().getAbsolutePath();
					MaydayDefaults.s_lastExportPath = l_chooser.getCurrentDirectory().getAbsolutePath();

					// if the user presses cancel, then quit
					if ( l_fileName == null )
					{
						return;
					}
					
					// ask before overwriting file
					if (new File(l_fileName).exists() && 
							JOptionPane.showConfirmDialog(ExportTabularProbeListViewerDialog.this, 
									"Do you really want to overwrite the existing file \""+l_fileName+"\"?",
									"Confirm file overwrite", 
									JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
									!=JOptionPane.YES_OPTION
					) {
						return;
					}
						
					

					try
					{      
						export( l_fileName, includeHeader, selectionOnly, leadingEdge );

						// dialog window can be closed savely now
						dispose();      
					}
					catch ( FileNotFoundException exception )
					{
						String l_message = MaydayDefaults.Messages.FILE_NOT_FOUND;
						l_message = l_message.replaceAll( MaydayDefaults.Messages.REPLACEMENT, l_fileName );

						JOptionPane.showMessageDialog( null,
								l_message,
								MaydayDefaults.Messages.ERROR_TITLE,
								JOptionPane.ERROR_MESSAGE ); 
					}
					catch ( IOException exception )
					{
						JOptionPane.showMessageDialog( null,
								exception.getMessage(),
								MaydayDefaults.Messages.ERROR_TITLE,
								JOptionPane.ERROR_MESSAGE ); 
					}
				}			
			}
		}


		protected class CancelAction
		extends AbstractAction	
		{
			public CancelAction()
			{
				super( "Cancel" );
			}		


			public void actionPerformed( ActionEvent event )
			{
				// close the dialog window
				dispose();
			}
		}

	}




}

