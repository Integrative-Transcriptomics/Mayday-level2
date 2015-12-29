package mayday.mqi.shell;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;

import mayday.core.pluma.PluginManager;
import mayday.core.pluma.filemanager.FMFile;
import mayday.core.structures.StringListModel;
import mayday.mushell.snippet.AbstractSnippet;
import mayday.mushell.snippet.SnippetEvent;

public class MQISnippet extends AbstractSnippet 
{
	private JScrollPane jsp;
	private StringListModel objects;
	private StringListModel objectsAndFunctions;
	private JList lsResultList;	
	
	
	
	public MQISnippet(Connection con)
	{
		objects = new StringListModel();
		objectsAndFunctions= new StringListModel();
		lsResultList = new JList(objects);
		lsResultList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount()==2 && e.getButton()==MouseEvent.BUTTON1) {
					String lastSnippet = (String)lsResultList.getSelectedValue();
					eventfirer.fireEvent(new SnippetEvent(MQISnippet.this,lastSnippet,false));
				}
			}
		});
		jsp = new JScrollPane(lsResultList);
		jsp.setName("Tables available");
		try {
			fill(con);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public JComponent getComponent() 
	{
		return jsp;
	}
	
	private void fill(Connection con) throws Exception
	{
		objects.add("SELECT * FROM probes");
		objects.add("addToProbeList('',name,dataset)");
		
		// SQL keywords:
		FMFile rconn = PluginManager.getInstance().getFilemanager().getFile("mayday/mqi/shell/sql.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(rconn.getStream()));
		
		try 
		{
			String line=br.readLine();
			while (line!=null)
			{
				objectsAndFunctions.add(line);
				line=br.readLine();
			}
				
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String[] tok= con.getMetaData().getStringFunctions().split(",");
		for(String t:tok)
			objectsAndFunctions.add(t);
		
		tok= con.getMetaData().getNumericFunctions().split(",");
		for(String t:tok)
			objectsAndFunctions.add(t);
		
		ResultSet res=con.getMetaData().getTables(null, "APP", "%", null);
		
//		ResultSetMetaData md=res.getMetaData();
//		int cc=md.getColumnCount();
		
		Set<String> names=new HashSet<String>();		
		while(res.next())
		{
			names.add(res.getString(3));
		}

		
		res=con.getMetaData().getFunctions(null, "APP", "%");
		while(res.next())
		{
			names.add(res.getString(3));	
		}
		
		for(String s:names)
		{
			objects.add(s);
			objectsAndFunctions.add(s);
		}
	}
	
	public StringListModel getFullModel() {
		return objectsAndFunctions;
	}

}
