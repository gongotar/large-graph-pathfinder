/**
 * 
 * 
 */

package graphics;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.JLabel;

import core.dijkstra;
import model.network;
import model.node;

/**
 * @author Masoud Gholami
 *
 */

public class QueryDialog extends javax.swing.JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	network netw;
	ArrayList<node> startNodes;
	LocalDateTime startTime;
    
    /** Creates new form EdgeTimeTableDialog */
    public QueryDialog(java.awt.Frame parent, network netw) {
        super(parent, true);
        this.netw = netw;
        initComponents();
        setTitle("Query");
    }

    private void initComponents() {
    	jButton1 = new javax.swing.JButton();
    	StartNodesTextField = new javax.swing.JFormattedTextField();
    	jLabelStartNodes = new JLabel();
    	StartTimeTextField = new javax.swing.JFormattedTextField();
    	jLabelStartTime = new JLabel();
    	
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Query");
        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonHandler(evt);
            }
        });
        
        jLabelStartNodes.setText("Start Nodes (Id): ");
        jLabelStartTime.setText("Start Time: ");
        
        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jButton1)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabelStartNodes)
                            .add(jLabelStartTime))
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(StartNodesTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 69, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(StartTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 69, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelStartNodes)
                    .add(StartNodesTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelStartTime)
                    .add(StartTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton1))
        );

        pack();
    }
    
    private void okButtonHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonHandler
    	String sTime = this.StartTimeTextField.getText();
    	String sNodes = this.StartNodesTextField.getText();
    	startNodes = new ArrayList<node>();
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    	try{
    		startTime = LocalDateTime.parse(sTime, formatter);
    	}catch(Exception e){
    		dispose();
    	}
    	String[] nodes = sNodes.split(",");
    	int i = 0;
    	for (node node : netw.getNodes()) {
			if(node.getId() == Integer.valueOf(nodes[i].trim())){
				startNodes.add(node);
				i++;
				if(i >= nodes.length)
					break;
			}
		}
    	
    	dijkstra.pareto_opt(startNodes, startTime);
        dispose();
    }//GEN-LAST:event_okButtonHandler
    
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabelStartNodes;
    private javax.swing.JFormattedTextField StartNodesTextField;
    private javax.swing.JLabel jLabelStartTime;
    private javax.swing.JFormattedTextField StartTimeTextField;
}
