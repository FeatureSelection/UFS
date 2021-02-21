
package solorio.gui;

import java.awt.GridLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 *
 * @author SSF
 */
public class MoreInfoPanel extends JDialog {

    JTextArea txt_main;
    JScrollPane scroll;

    public MoreInfoPanel(String text) {
        super();
        txt_main = new JTextArea(text);
        txt_main.setEditable(false);
        txt_main.setLineWrap(true);
        scroll = new JScrollPane(txt_main);
        scroll.setBounds(0, 0, 500, 400);
        scroll.setViewportView(txt_main);

        setModalityType(ModalityType.APPLICATION_MODAL);
        //setIconImage(new ImageIcon(getClass().getResource("/icons/logo.png")).getImage());
        setSize(500, 400);
        setLayout(new GridLayout(1, 1));
        add(scroll);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("Information");
    }
}
