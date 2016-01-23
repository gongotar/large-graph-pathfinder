/**
 * 
 */
package graphics;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import model.node;

/**
 * @author Masoud Gholami
 *
 */
public class NodeLabelsDialog extends javax.swing.JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	node node;
	private JTable table;
    
    /** Creates new form EdgeTimeTableDialog */
    public NodeLabelsDialog(java.awt.Frame parent, node node) {
        super(parent, true);
        this.node = node;
        initComponents();
        setTitle("Node: " + node.toString());
    }

    private void initComponents() {
    	jButton1 = new javax.swing.JButton();
    	
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Node Labels");
        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonHandler(evt);
            }
        });
        
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        Object[][] data = new Object[node.getLabels().size()][6];
        
        for (int i = 0; i < data.length; i++) {
			data[i][0] = node.getLabels().get(i).getPath();
			data[i][1] = node.getLabels().get(i).getDuration();
			data[i][2] = node.getLabels().get(i).getStart();
			data[i][3] = node.getLabels().get(i).getChange();
			data[i][4] = node.getLabels().get(i).getCost();
			data[i][5] = node.getLabels().get(i).getRisk();
		}
        
        String[] headers = {"Path", "Duration", "Start Time", "Change", "Cost", "Risk"};
                
		table = new JTable(data, headers);
		
        add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                BorderLayout.CENTER);
        add(jButton1, BorderLayout.AFTER_LAST_LINE);
        pack();
    }
    
    private void okButtonHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonHandler
        dispose();
    }//GEN-LAST:event_okButtonHandler
    
    private javax.swing.JButton jButton1;
}
