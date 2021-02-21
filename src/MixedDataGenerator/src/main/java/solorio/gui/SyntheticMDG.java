
package solorio.gui;

import javax.swing.JFrame;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.event.ActionEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
/**
 *
 * @author SSF
 */
public class SyntheticMDG {
    public static void main(String[] args) {
        FlatLightLaf.install();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(675, 650);
        frame.setTitle("Mixed Data Generator");
        MixedDataGeneratorJPanel mdgJpanel = new MixedDataGeneratorJPanel(frame);
        
        mdgJpanel.setVisible(true);
        frame.add(mdgJpanel);
        
        JMenuBar jMenuBar = new JMenuBar();
        jMenuBar.setVisible(true);
        
        JMenu fileMenu = new javax.swing.JMenu();
        fileMenu.setText("File");
        JMenu helpMenu = new javax.swing.JMenu();
        helpMenu.setText("Help");
        
        JMenuItem helpjMenuItem = new JMenuItem();
        helpjMenuItem.setText("Help");
        helpjMenuItem.setEnabled(true);
        helpjMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpjMenuItemActionPerformed(evt);
            }

        private void helpjMenuItemActionPerformed(ActionEvent evt) {
            String str = "\n";
            str += "Mixed Data Generator is a data generator for producing synthetic mixed datasets described by relevant,  "
                    + "irrelevant, and redundant features. Additional parameters can be set; push the \"More...\" button for parameter details help. \n";
            MoreInfoPanel morePanel = new MoreInfoPanel(str);
            morePanel.setVisible(true);
      
            }
        });
        
        JMenuItem oboutjMenuItem = new JMenuItem();
        oboutjMenuItem.setText("About...");
        oboutjMenuItem.setEnabled(true);
        oboutjMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutjMenuItemActionPerformed(evt);
            }

            private void aboutjMenuItemActionPerformed(ActionEvent evt) {
                JAboutDialog acercaDDialog = new JAboutDialog();
                acercaDDialog.setVisible(true);
            }
        });
        
        helpMenu.add(helpjMenuItem);
        helpMenu.add(oboutjMenuItem);
        
        //jMenuBar.add(fileMenu);
        jMenuBar.add(helpMenu);
        
        frame.setJMenuBar(jMenuBar);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
        
  
     
    }
}
