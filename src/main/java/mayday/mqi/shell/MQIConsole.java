package mayday.mqi.shell;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.gui.dataset.DataSetSelectionDialog;
import mayday.core.gui.probelist.ProbeListSelectionDialog;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.filemanager.FMFile;
import mayday.core.probelistmanager.ProbeListManager;
import mayday.mqi.MQIUtils;
import mayday.mushell.Console;
import mayday.mushell.autocomplete.ListAutoCompleter;

@SuppressWarnings("serial")
public class MQIConsole extends Console
{
	private MQISnippet mqiSnippet;
	private Connection con;

	public MQIConsole(String title) {
		super(title);
		throw new IllegalArgumentException();

	}

	public MQIConsole (Connection con) {
		super("Query Mayday");		
		this.con=con;
		setDispatcher(new MQIDispatcher(con));

		mqiSnippet = new MQISnippet(con);
		addSnippetField(mqiSnippet);

		inputField.setAutoCompleter(new ListAutoCompleter(mqiSnippet.getFullModel()));
		inputField.setTokenizer(new MQITokenizer());

		motd();
		addWindowListener(new CloseListener());
	}

	public void init() {
		super.init();
		
		JMenu createViewMenu = new JMenu("Create view");
		createViewMenu.add(new AbstractAction("Create view for a dataset") {
			@Override
			public void actionPerformed(ActionEvent e) {
				DataSet ds = null;
				if (DataSetManager.singleInstance.getNumberOfObjects()>1) {
					DataSetSelectionDialog dssd = new DataSetSelectionDialog();
					dssd.setDialogDescription("Please select a DataSet to create as view CURRENTDATASET");
					dssd.setModal(true);
					dssd.setVisible(true);
					if (dssd.getSelection().size()>0)
						ds = dssd.getSelection().get(0);
				} else {
					if (DataSetManager.singleInstance.getNumberOfObjects()==1)
						ds = DataSetManager.singleInstance.getDataSets().get(0);
				}
				if (ds!=null)
					try {
						Statement st=con.createStatement();
						MQIUtils.createSingleDatasetFunction(st, ds);
						outputField.print("(Re-)created the view CURRENTDATASET\n");
					} catch (SQLException e1) {
						e1.printStackTrace();
						outputField.print("Could not create the view. Check the messages for details.\n");
					}
			}
		});
		createViewMenu.add(new AbstractAction("Create view for a probelist") {
			@Override
			public void actionPerformed(ActionEvent e) {
				ProbeList pl = null;
				List<ProbeListManager> lplm = new LinkedList<ProbeListManager>();
				for (DataSet ds : DataSetManager.singleInstance.getDataSets())
					lplm.add(ds.getProbeListManager());

				ProbeListSelectionDialog plsd = new ProbeListSelectionDialog(lplm);
				plsd.setDialogDescription("Please select a ProbeList to create as view CURRENTPROBELIST");
				plsd.setModal(true);
				plsd.setVisible(true);
				if (plsd.getSelection().size()>0)
					pl = plsd.getSelection().get(0);					
				if (pl!=null)
					try {
						Statement st=con.createStatement();
						MQIUtils.createSingleProbeListFunction(st, pl.getAllProbes());
						outputField.print("(Re-)created the view CURRENTPROBELIST\n");
					} catch (SQLException e1) {
						e1.printStackTrace();
						outputField.print("Could not create the view. Check the messages for details.\n");
					}
			}
		});
		
		JMenuBar jmb = getJMenuBar();
		jmb.add(createViewMenu);
		setJMenuBar(jmb);
	}


	private void motd()
	{
		FMFile rconn = PluginManager.getInstance().getFilemanager().getFile("mayday/mqi/shell/motd.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(rconn.getStream()));
		
		StringBuffer motd=new StringBuffer();
		try 
		{
			String line=br.readLine();
			while (line!=null)
			{
				motd.append(line+"\n");
				line=br.readLine();
			}
				
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		outputField.print(motd.toString());
	}

	private class CloseListener extends WindowAdapter
	{
		public void windowClosing(WindowEvent e) 
		{
			try{
				con.commit();
				con.close();
				try 
				{
					DriverManager.getConnection("jdbc:derby:mqi;shutdown=true");
				} catch (SQLException ex) 
				{
					// do nothing.
				}
			}
			catch (Exception ex) 
			{
				try {
					con.rollback();
					con.close();
				} catch (SQLException e1) 
				{
					// do nothing.
					e1.printStackTrace();
				}
				ex.printStackTrace();
			}

		}
	}



}
