/**
 * 
 */
package graphics;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import core.dijkstra;
import model.network;
import model.node;

/**
 * @author Masoud Gholami
 *
 */
public class NodeParetoDialog extends javax.swing.JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	node node;
	DateTimeFormatter formatter;
	network netw;
    
    /** Creates new form EdgeTimeTableDialog */
    public NodeParetoDialog(java.awt.Frame parent, node node, network netw) {
        super(parent, true);
        this.node = node;
        this.netw = netw;
        initComponents();
        setTitle("Node: " + node.toString());
        formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        TimeTextField.setValue(LocalDateTime.now().format(formatter));
    }

    private void initComponents() {
    	jButton1 = new javax.swing.JButton();
        jLabelTime = new javax.swing.JLabel();
        TimeTextField = new javax.swing.JFormattedTextField();
    	
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Node Pareeto Optimals");
        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonHandler(evt);
            }
        });
        
        jLabelTime.setText("Start Time: ");


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
                            .add(jLabelTime))
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(TimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 69, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelTime)
                    .add(TimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton1))
        );
        pack();

    }
    
    private void okButtonHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonHandler
    	String sTime = this.TimeTextField.getText();
    	ArrayList<node> startNodes = new ArrayList<node>();
    	LocalDateTime startTime = null;
    	startNodes.add(node);
    	try{
    		startTime = LocalDateTime.parse(sTime, formatter);
    	}catch(Exception e){
    		dispose();
    	}
    	
    	for (node node : netw.getNodes())
			node.getLabels().clear();
    	
    	dijkstra.pareto_opt(startNodes, startTime, netw);
        dispose();
    }//GEN-LAST:event_okButtonHandler
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField TimeTextField;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabelTime;
    // End of variables declaration//GEN-END:variables
}
