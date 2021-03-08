
package solorio.utils;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author solorio
 */
public class SwingUtils {

    public static File getFile(String... extensions) {
        File file = null;
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("").getAbsoluteFile());
        fc.addChoosableFileFilter(new JFileFilter(extensions));
        fc.setAcceptAllFileFilterUsed(false);

        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
        }
        return file;
    }

    public static File saveFile(String[] extensions) {
        File file = null;
        JFrame parentFrame = new JFrame();

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save as");
        fc.setCurrentDirectory(new File("output").getAbsoluteFile());

        for (String extension : extensions) {
            fc.addChoosableFileFilter(new FileNameExtensionFilter(extension.toUpperCase() + " files", extension.toLowerCase()));
        }

        int userSelection = fc.showSaveDialog(parentFrame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            switch (fc.getFileFilter().getDescription()) {
                case "DATASET files":
                    fc.setSelectedFile(new File(fc.getSelectedFile().getAbsolutePath() + ".DataSet"));
                    break;
                case "ARFF files":
                    fc.setSelectedFile(new File(fc.getSelectedFile().getAbsolutePath() + ".arff"));
                    break;
                default:
                    fc.setSelectedFile(new File(fc.getSelectedFile().getAbsolutePath() + ".csv"));
                    break;
            }
            file = fc.getSelectedFile();
            System.out.println("Save as file: " + file.getAbsolutePath());
        }
        return file;

    }

    private static class JFileFilter extends FileFilter {

        String[] extensions;

        public JFileFilter(String... extensions) {
            this.extensions = extensions;
        }

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            String s = f.getName();
            int i = s.lastIndexOf('.');

            if (i > 0 && i < s.length() - 1) {
                for (String item : extensions) {
                    if (s.substring(i + 1).toLowerCase().equals(item.toLowerCase())) {
                        return true;
                    }
                }
            }

            return false;
        }

        @Override
        public String getDescription() {
            return StringUtils.join(extensions, ",");
        }
    }
}
