/**
 * 
 */
package graphics;

import java.awt.BorderLayout;
import java.time.LocalTime;
import java.util.ArrayList;

import javax.swing.table.TableModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import model.edge;
import model.timetable_row;

/**
 * @author Masoud Gholami
 *
 */
public class EdgeTimeTableDialog  extends javax.swing.JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	edge edge;
	private JTable table;
    
    /** Creates new form EdgeTimeTableDialog */
    public EdgeTimeTableDialog(java.awt.Frame parent, edge edge) {
        super(parent, true);
        this.edge = edge;
        initComponents();
        setTitle("Edge: " + edge.toString());
    }

    private void initComponents() {
    	jButton1 = new javax.swing.JButton();
    	
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Edge Timetable");
        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonHandler(evt);
            }
        });
        
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        final TableModel dataModel = new AbstractTableModel() {

            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			String[] headers = {"Line", "Start Time", "End Time", "Cost", "Risk"};
			private int rowCount = 25;
			private int colCount = headers.length;
			Object[][] rowData = new Object[rowCount][colCount];

			public int getColumnCount() { 
                return colCount; 
            }

            public int getRowCount() { 
                return rowCount;
            }

            public Object getValueAt(int row, int col) { 
                return rowData[row][col]; 
            }
            
            public boolean isCellEditable(int row, int col){ 
            	return true; 
            }
            
            @SuppressWarnings("unused")
			public void setValueAt(Object value, int row, int col) {
            	try{
            		switch (col) {
					case 0:	// line
						int line;
						if(value instanceof Integer)
							line = (int) value;
						else
							line = Integer.valueOf((String)value);
						break;
					case 1: // Start Time
						LocalTime stime;
						if(value instanceof LocalTime)
							stime = (LocalTime) value;
						else
							stime = LocalTime.parse((String)value);
						break;
					case 2: // End Time
						LocalTime etime;
						if(value instanceof LocalTime)
							etime = (LocalTime) value;
						else
							etime = LocalTime.parse((String)value);;
						break;
					case 3:	// cost
						double cost;
						if(value instanceof Double)
							cost = (double) value;
						else
							cost = Double.valueOf((String)value);
						break;
					case 4:	// risk
						double risk;
						if(value instanceof Double)
							risk = (double) value;
						else
							risk = Double.valueOf((String)value);
						break;
					default:
						break;
					}
            		rowData[row][col] = value;
            		
            	}catch(Exception e){
            	}
            	
            	fireTableCellUpdated(row, col);
            }
            
            @Override
            public String getColumnName(int column) {
            	return headers[column];
            }
        };
        
		table = new JTable(dataModel);
		
		if(edge.getTimetable().size() > 0){
			for (int i = 0; i < edge.getTimetable().size(); i++) {
				timetable_row row = edge.getTimetable().get(i);
				int j = 0;
				table.setValueAt(row.getLine(), i, j++);
				table.setValueAt(row.getStart_time(), i, j++);
				table.setValueAt(row.getEnd_time(), i, j++);
				table.setValueAt(row.getCost(), i, j++);
				table.setValueAt(row.getVariation(), i, j++);
			}
		}
		
        add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                BorderLayout.CENTER);
        add(jButton1, BorderLayout.AFTER_LAST_LINE);
        pack();
    }
    
    private void okButtonHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonHandler
    	ArrayList<timetable_row> timetable = new ArrayList<timetable_row>();
    	int rows = table.getRowCount();
    	Object value;
    	for (int i = 0; i < rows; i++) {
    		if(table.getValueAt(i, 0) == null || 
    				((String)table.getValueAt(i, 0)) == "")
    			break;
    		value = table.getValueAt(i, 0);
    		int line = Integer.valueOf((String) value);
    		value = table.getValueAt(i, 1);
    		LocalTime stime = LocalTime.parse((String)value);
    		value = table.getValueAt(i, 2);
    		LocalTime etime = LocalTime.parse((String)value);
    		value = table.getValueAt(i, 3);
    		double cost = Double.valueOf((String) value);
    		value = table.getValueAt(i, 4);
    		double risk = Double.valueOf((String) value);
    		
    		timetable_row row = new timetable_row(i, line, cost, stime, etime, risk);
    		timetable.add(row);
		}
    	edge.setTimetable(timetable);
        dispose();
    }//GEN-LAST:event_okButtonHandler
    
    private javax.swing.JButton jButton1;
}
