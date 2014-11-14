package mayday.gsanalysis.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import mayday.core.gui.MaydayFrame;
import mayday.core.structures.Pair;
import mayday.gsanalysis.AbstractGSAnalysisPlugin;
import mayday.gsanalysis.Enrichment;
import mayday.gsanalysis.Result;
import mayday.gsanalysis.graph.GraphEnrichmentAnalysisPlugin;
import mayday.gsanalysis.gsea.GSEAPlugin;
import mayday.vis3.graph.GraphCanvas;

@SuppressWarnings("serial")
public class ResultFrame extends MaydayFrame {
	private Result result;
	private JTable table;
	private AbstractGSAnalysisPlugin plugin;
	private String graphPreferences;
	private JButton graphButton;

	public ResultFrame(Result result, AbstractGSAnalysisPlugin plugin, GraphCanvas graphCanvas) {
		this.result=result;
		this.plugin=plugin;
	}

	public ResultFrame(Result result, AbstractGSAnalysisPlugin plugin) {
		this.result=result;
		this.plugin=plugin;
	}

	public void addGraphPreferences(String graphPreferences, GraphEnrichmentAnalysisPlugin graphPlugin) {
		this.graphPreferences=graphPreferences;
		graphButton = new JButton(new ShowGraphAction(graphPlugin,result,this));

	}

	public JButton getGraphButton() {
		return graphButton;
	}

	public JTable getTable() {
		return table;
	}

	public void showResult() {
		DefaultTableModel model = new DefaultTableModel();		
		if(result.getEnrichmentWithClasses()) {
			model = new DefaultTableModel() {
				@Override	
				public Class<?> getColumnClass(int i) {
					if (i==3)
						return Integer.class;
					return (i>3)? Double.class : Object.class;
				}
			};
			ArrayList<String> columnNames = new ArrayList<String>();
			columnNames.add("Class1");
			columnNames.add("Class2");
			columnNames.add("Geneset");
			columnNames.add("Geneset size");
			columnNames.addAll(result.getSortedEnrichments(result.getClassCombinations().iterator().next()).get(0).getColumnIdentifiers());
			model.setColumnIdentifiers(columnNames.toArray());

			int nrow=result.getClassCombinations().size()*result.getSortedEnrichments(result.getClassCombinations().iterator().next()).size();
			model.setNumRows(nrow);
			int row=0;

			for(Pair<String,String> classPair : result.getClassCombinations()) {
				for(Enrichment e: result.getSortedEnrichments(classPair)) {
					model.setValueAt(classPair.getFirst(),row,0);
					model.setValueAt(classPair.getSecond(),row,1);
					model.setValueAt(e.getGeneset().getName(),row,2);
					model.setValueAt(e.getGeneset().getGenes().size(),row,3);
					int column=4;
					for(Double value: e.getValues()) {
						model.setValueAt(value, row, column);
						column++;
					}
					row++;
				}
			}
		}
		else {
			model = new DefaultTableModel() {
				@Override	
				public Class<?> getColumnClass(int i) {
					if (i==1)
						return Integer.class;
					return (i>1)? Double.class : Object.class;
				}
			};
			ArrayList<String> columnNames = new ArrayList<String>();
			columnNames.add("Geneset");
			columnNames.add("Geneset size");
			columnNames.addAll(result.getSortedEnrichments(null).get(0).getColumnIdentifiers());
			model.setColumnIdentifiers(columnNames.toArray());

			int nrow=result.getSortedEnrichments(null).size();
			model.setNumRows(nrow);
			int row=0;

			for(Enrichment e: result.getSortedEnrichments(null)) {
				model.setValueAt(e.getGeneset().getName(),row,0);
				model.setValueAt(e.getGeneset().getGenes().size(),row,1);
				int column=2;
				for(Double value: e.getValues()) {
					model.setValueAt(value, row, column);
					column++;
				}
				row++;
			}
		}
		table = new JTable(model) {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int x, int y) {
				return false;
			}

		};

		table.setRowSelectionAllowed(true);
		table.setAutoCreateRowSorter(true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.addMouseListener(new ResultListener(plugin,result,this));

		JTable lineTable = new LineNumberTable( table );
		JScrollPane tableScrollPane = new JScrollPane(table);
		tableScrollPane.setRowHeaderView(lineTable );


		//		table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
		//
		//			@Override
		//			public Component getTableCellRendererComponent(JTable table,
		//					Object value, boolean isSelected, boolean hasFocus,
		//					int row, int column) {
		//				if (value instanceof Double)
		//					value = Double.toString(((Double)value));
		//				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		//			}
		//			
		//		});


		setLayout(new BorderLayout());
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		add(buttonPanel, BorderLayout.NORTH);
		
		String text = plugin.getPreferences();

		JButton exportButton = new JButton(new ExportTableAction(table, result, plugin,text));		
		buttonPanel.add(exportButton);

		if(graphPreferences!=null) {
			text = text.replaceFirst("<html>", "");
			text = graphPreferences.replaceFirst("</html>", text);
		}
		
		if(graphButton!=null) {
			buttonPanel.add(graphButton);
		}

		if(plugin.getName().equals("GSEA")&&((GSEAPlugin)plugin).getPermutationTests()) {
			text=text.replaceFirst("<html>", "");
			text = "<html> Double click on line to show leading edge <p/><p/>" + text;
		}
		
		text = text.replaceAll("<p/>", "<br>");
		text = text.replaceFirst("<html>", "<html><font face=Helvetica size=-1>");
		
		JEditorPane jep = new JEditorPane("text/html",text);
		jep.setFont(table.getFont());		
				
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setTopComponent(tableScrollPane);		
		split.setBottomComponent(new JScrollPane(jep));

		setTitle("Enrichment Analysis Results");
		add(split, BorderLayout.CENTER);
		
		pack();
		setSize(800, 600);
		setVisible(true);
		split.setDividerLocation(.8);
	}


	class LineNumberTable extends JTable {
		private static final long serialVersionUID = 1L;
		private JTable mainTable;

		public LineNumberTable(JTable table){
			super();
			mainTable = table;
			setAutoCreateColumnsFromModel( false );
			setModel( mainTable.getModel() );
			setSelectionModel( mainTable.getSelectionModel() );
			setAutoscrolls( false );

			addColumn( new TableColumn() );
			getColumnModel().getColumn(0).setCellRenderer(mainTable.getTableHeader().getDefaultRenderer() );
			getColumnModel().getColumn(0).setPreferredWidth(50);
			setPreferredScrollableViewportSize(getPreferredSize());
		}

		public boolean isCellEditable(int row, int column){
			return false;
		}

		public Object getValueAt(int row, int column){
			return new Integer(row + 1);
		}

		public int getRowHeight(int row) {
			return mainTable.getRowHeight();
		}
	}

}
