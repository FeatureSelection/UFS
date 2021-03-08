
package solorio.gui;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;
import solorio.dataGenerators.MixedDataGenerator;
import solorio.gui.wekaBeans.AttributeSummarizer;
import solorio.utils.SwingUtils;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.CSVSaver;
import weka.core.converters.ConverterUtils;

/**
 *
 * @author SSF
 */
public class MixedDataGeneratorJPanel extends javax.swing.JPanel {

    JFrame parentFrame;

    private Instances inputData;
    private Instances generatedData;    
    private String outputPath;
    
    public static final Color DARK_GREEN = new Color(0,153,0);
    

    /**
     * Creates new form MixedDataGeneratorJPanel
     */
    public MixedDataGeneratorJPanel() {
        initComponents();
        setNewDataSetDefaultValues();
    }

    public MixedDataGeneratorJPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        initComponents();
        setNewDataSetDefaultValues();
    }

    private void setNewDataSetDefaultValues() {

        inputData = null;
        generatedData = null;
        loadDatasetjButton.setEnabled(false);
        statusTextjLabel.setForeground(Color.black);
        statusTextjLabel.setText("None");
        //dataLoadedjLabel.setEnabled(false);

        enableAllComponents(true);

        //General section default values
        datasetNamejTextField.setText("");
        numOfClassesjTextField.setText("2");
        classBalanceRatejTextField.setText("1.0");
        numOfObjectsjTextField.setText("300");
        numOfFeaturesDisplayerjLabel.setText("");
        seedjTextField.setText("1");

        //Numerical feature section default values
        numOfRelNumFjTextField.setText("10");
        numOfIrrelNumFjTextField.setText("5");
        equalIntervaljComboBox.setSelectedIndex(0);
        numOfRedNumFjTextField.setText("20");
        numGenjComboBox.setSelectedIndex(0);

        //Non-numerical feare section default values
        numOfRelNomFjTextField.setText("10");
        maxNumAllowedValuesjTextField.setText("5");
        numOfIrrelNomFjTextField.setText("5");
        numOfRedNomFjTextField.setText("20");
        nomGenjComboBox.setSelectedIndex(0);

        generateDatajButton.setEnabled(true);
        resetjButton.setEnabled(true);
        outputDirectoryjTextField.setText(System.getProperty("user.home") + File.separator + "FS_RES_DIR");
        outputPath = outputDirectoryjTextField.getText();
        histogramjButton.setEnabled(false);

    }

    private void setFromExistingDatasetDefaultValues() {
        loadDatasetjButton.setEnabled(true);
        statusTextjLabel.setForeground(Color.BLACK);
        statusTextjLabel.setText("Not loaded");
        statusTextjLabel.setEnabled(true);

        enableAllComponents(false);

        generateDatajButton.setEnabled(false);
        resetjButton.setEnabled(false);
        histogramjButton.setEnabled(false);
    }

    private void enableAllComponents(boolean val) {
        //General section default values
        datasetNamejTextField.setEnabled(val);
        numOfClassesjTextField.setEnabled(val);
        classBalanceRatejTextField.setEnabled(val);
        numOfObjectsjTextField.setEnabled(val);
        seedjTextField.setEnabled(val);

        //Numerical feature section default values
        numOfRelNumFjTextField.setEnabled(val);
        numOfIrrelNumFjTextField.setEnabled(val);
        equalIntervaljComboBox.setEnabled(val);
        numOfRedNumFjTextField.setEnabled(val);
        numGenjComboBox.setEnabled(val);
        //optionsNumGenjButton.setEnabled(val);

        //Non-numerical feare section default values
        numOfRelNomFjTextField.setEnabled(val);
        maxNumAllowedValuesjTextField.setEnabled(val);
        numOfIrrelNomFjTextField.setEnabled(val);
        numOfRedNomFjTextField.setEnabled(val);
        nomGenjComboBox.setEnabled(val);
        //optionNomGenjButton.setEnabled(val);
    }

    private void setValuesForFromExistingDataset() {
        if (inputData != null) {
            //dataLoadedjLabel.setForeground(Color.getHSBColor(10, 90, 55));
            statusTextjLabel.setForeground(Color.BLUE);
            statusTextjLabel.setText("Data loaded");
            datasetNamejTextField.setEnabled(true);
            datasetNamejTextField.setText(inputData.relationName() + "_New");
            numOfClassesjTextField.setText(Integer.toString(inputData.numClasses()));
            numOfObjectsjTextField.setText(Integer.toString(inputData.numInstances()));
            numOfFeaturesDisplayerjLabel.setText(Integer.toString(inputData.numAttributes()));
            numOfRedNumFjTextField.setEnabled(true);
            numGenjComboBox.setEnabled(true);
            optionsNumGenjButton.setEnabled(false);
            numOfRedNomFjTextField.setEnabled(true);
            nomGenjComboBox.setEnabled(true);
            optionNomGenjButton.setEnabled(false);
            generateDatajButton.setEnabled(true);
            resetjButton.setEnabled(true);
        }
    }

    private MixedDataGenerator getGeneratorFromInputParameters() {

        MixedDataGenerator mixedDataGenerator;

        //General parameters
        String dataSetname = datasetNamejTextField.getText();
        String numOfClasses = numOfClassesjTextField.getText();
        String classBalanceRate = classBalanceRatejTextField.getText();
        String numOfObjects = numOfObjectsjTextField.getText();
        String seed = seedjTextField.getText();

        //Numerical features parameters
        String numOfRelNum = numOfRelNumFjTextField.getText();
        String numOfIrrelNum = numOfIrrelNumFjTextField.getText();
        String numOfRedNum = numOfRedNumFjTextField.getText();

        //Non-numerical feature parameters
        String numOfRelNom = numOfRelNomFjTextField.getText();
        String maxNumAllowedValues = maxNumAllowedValuesjTextField.getText();
        String nuOfIrrelNom = numOfIrrelNomFjTextField.getText();
        String numOfRedNom = numOfRedNomFjTextField.getText();

        try {
            int numOfClassesP = Integer.parseInt(numOfClasses);
            double classBalanceRateP = Double.parseDouble(classBalanceRate);
            int numOfObjectsP = Integer.parseInt(numOfObjects);
            int seedP = Integer.parseInt(seed);

            int numOfRelNumP = Integer.parseInt(numOfRelNum);
            int numOfIrrelNumP = Integer.parseInt(numOfIrrelNum);
            int numOfRedNumP = Integer.parseInt(numOfRedNum);

            int numOfRelNomP = Integer.parseInt(numOfRelNom);
            int maxNumAllowedValuesP = Integer.parseInt(maxNumAllowedValues);
            int nuOfIrrelNomP = Integer.parseInt(nuOfIrrelNom);
            int numOfRedNomP = Integer.parseInt(numOfRedNom);

            if (inputData == null) {
                mixedDataGenerator = new MixedDataGenerator();
            } else {
                mixedDataGenerator = new MixedDataGenerator(inputData);
            }

            if (!dataSetname.equals("") && inputData == null) {
                mixedDataGenerator.setRelationName(dataSetname);
            }

            mixedDataGenerator.setNumClasses(numOfClassesP);
            mixedDataGenerator.setClassBalanceRate(classBalanceRateP);
            mixedDataGenerator.setNumExamples(numOfObjectsP);
            mixedDataGenerator.setSeed(seedP);

            mixedDataGenerator.setNumOfNumericRelevantAttributes(numOfRelNumP);
            mixedDataGenerator.setNumOfNumericIrrelevantAttributes(numOfIrrelNumP);
            mixedDataGenerator.setEqualIntervalForIrrelevantNumericAttributes(Boolean.valueOf(equalIntervaljComboBox.getSelectedItem().toString()));
            mixedDataGenerator.setNumOfNumericRedundantAttributes(numOfRedNumP);
            //mixedDataGenerator.setRedundantNumericalGen(redNumericalGen);

            mixedDataGenerator.setNumOfNominalRelevantAttributes(numOfRelNomP);
            mixedDataGenerator.setMaxNumOfValuesForNominalAttributes(maxNumAllowedValuesP);
            mixedDataGenerator.setNumOfNominalIrrelevantAttributes(nuOfIrrelNomP);
            mixedDataGenerator.setNumOfNominalRedundantAttributes(numOfRedNomP);
            //mixedDataGenerator.setRedundantNominalGen(redNominalGen);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Input error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return mixedDataGenerator;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dataGenerationbuttonGroup = new javax.swing.ButtonGroup();
        label1 = new java.awt.Label();
        featuresjPanel = new javax.swing.JPanel();
        numericaljPanel = new javax.swing.JPanel();
        NumOfNumRelFjLabel = new javax.swing.JLabel();
        numOfIrrelNumFjLabel = new javax.swing.JLabel();
        numOfRedNumFjLabel = new javax.swing.JLabel();
        NumGeneratorjLabel = new javax.swing.JLabel();
        numOfRelNumFjTextField = new javax.swing.JTextField();
        numGenjComboBox = new javax.swing.JComboBox<>();
        numOfRedNumFjTextField = new javax.swing.JTextField();
        optionsNumGenjButton = new javax.swing.JButton();
        numOfIrrelNumFjTextField = new javax.swing.JTextField();
        equalIntervaljLabel = new javax.swing.JLabel();
        equalIntervaljComboBox = new javax.swing.JComboBox<>();
        nonNumericaljPanel = new javax.swing.JPanel();
        numOfRelNomFjLabel = new javax.swing.JLabel();
        numOfIrrelNomFjLabel = new javax.swing.JLabel();
        numOfRedNomFjLabel = new javax.swing.JLabel();
        numOfRelNomFjTextField = new javax.swing.JTextField();
        numOfIrrelNomFjTextField = new javax.swing.JTextField();
        numOfRedNomFjTextField = new javax.swing.JTextField();
        redNomGenjLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        maxNumAllowedValuesjTextField = new javax.swing.JTextField();
        nomGenjComboBox = new javax.swing.JComboBox<>();
        optionNomGenjButton = new javax.swing.JButton();
        generaljPanel1 = new javax.swing.JPanel();
        datasetNamejLabel = new javax.swing.JLabel();
        numOfObjectsjLabel = new javax.swing.JLabel();
        numOfClassesjLabel = new javax.swing.JLabel();
        seedjLabel = new javax.swing.JLabel();
        datasetNamejTextField = new javax.swing.JTextField();
        numOfObjectsjTextField = new javax.swing.JTextField();
        numOfClassesjTextField = new javax.swing.JTextField();
        seedjTextField = new javax.swing.JTextField();
        classBalanceRatejLabel = new javax.swing.JLabel();
        classBalanceRatejTextField = new javax.swing.JTextField();
        numOfFeaturesjLabel = new javax.swing.JLabel();
        numOfFeaturesDisplayerjLabel = new javax.swing.JLabel();
        dataGenerationjPanel = new javax.swing.JPanel();
        loadDatasetjButton = new javax.swing.JButton();
        newDatasetjRadioButton = new javax.swing.JRadioButton();
        fromExistingDatasetjRadioButton = new javax.swing.JRadioButton();
        statusTextjLabel = new javax.swing.JLabel();
        statusjLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        outputDirectoryjTextField = new javax.swing.JTextField();
        histogramjButton = new javax.swing.JButton();
        formatjLabel = new javax.swing.JLabel();
        outputFormatjComboBox = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        generateDatajButton = new javax.swing.JButton();
        resetjButton = new javax.swing.JButton();
        morejButton = new javax.swing.JButton();
        closejButton = new javax.swing.JButton();

        label1.setText("label1");

        featuresjPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Features", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        numericaljPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Numerical"));

        NumOfNumRelFjLabel.setText("Num. of Relevant:");

        numOfIrrelNumFjLabel.setText("Num. of Irrelevant:");

        numOfRedNumFjLabel.setText("Num. of Redundant:");

        NumGeneratorjLabel.setText("Generator:");

        numOfRelNumFjTextField.setText("10");
        numOfRelNumFjTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                numOfRelNumFjTextFieldKeyTyped(evt);
            }
        });

        numGenjComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Linear Comb" }));

        numOfRedNumFjTextField.setText("20");
        numOfRedNumFjTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                numOfRedNumFjTextFieldKeyTyped(evt);
            }
        });

        optionsNumGenjButton.setText("Options...");
        optionsNumGenjButton.setEnabled(false);

        numOfIrrelNumFjTextField.setText("5");
        numOfIrrelNumFjTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                numOfIrrelNumFjTextFieldKeyTyped(evt);
            }
        });

        equalIntervaljLabel.setText("Equal interval for irrelevant features:");

        equalIntervaljComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));
        equalIntervaljComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                equalIntervaljComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout numericaljPanelLayout = new javax.swing.GroupLayout(numericaljPanel);
        numericaljPanel.setLayout(numericaljPanelLayout);
        numericaljPanelLayout.setHorizontalGroup(
            numericaljPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(numericaljPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(numericaljPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(NumOfNumRelFjLabel)
                    .addComponent(numOfRedNumFjLabel)
                    .addComponent(numOfIrrelNumFjLabel))
                .addGap(33, 33, 33)
                .addGroup(numericaljPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(numOfIrrelNumFjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numOfRedNumFjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numOfRelNumFjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(numericaljPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(numericaljPanelLayout.createSequentialGroup()
                        .addComponent(NumGeneratorjLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numGenjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(equalIntervaljLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 79, Short.MAX_VALUE)
                .addGroup(numericaljPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(optionsNumGenjButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(equalIntervaljComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        numericaljPanelLayout.setVerticalGroup(
            numericaljPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(numericaljPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(numericaljPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NumOfNumRelFjLabel)
                    .addComponent(numOfRelNumFjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(numericaljPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numOfIrrelNumFjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(equalIntervaljLabel)
                    .addComponent(equalIntervaljComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numOfIrrelNumFjLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(numericaljPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numOfRedNumFjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NumGeneratorjLabel)
                    .addComponent(numOfRedNumFjLabel)
                    .addComponent(numGenjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(optionsNumGenjButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        nonNumericaljPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Non-Numerical"));

        numOfRelNomFjLabel.setText("Num. of Relevant:");

        numOfIrrelNomFjLabel.setText("Num. of Irrelevant:");

        numOfRedNomFjLabel.setText("Num. of Redundant:");

        numOfRelNomFjTextField.setText("10");
        numOfRelNomFjTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                numOfRelNomFjTextFieldKeyTyped(evt);
            }
        });

        numOfIrrelNomFjTextField.setText("5");
        numOfIrrelNomFjTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                numOfIrrelNomFjTextFieldKeyTyped(evt);
            }
        });

        numOfRedNomFjTextField.setText("20");
        numOfRedNomFjTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                numOfRedNomFjTextFieldKeyTyped(evt);
            }
        });

        redNomGenjLabel.setText("Generator:");

        jLabel1.setText("Maximun number of allowed values:");

        maxNumAllowedValuesjTextField.setText("5");
        maxNumAllowedValuesjTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                maxNumAllowedValuesjTextFieldKeyTyped(evt);
            }
        });

        nomGenjComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Non-Matching-NG", "Mixed-NG" }));

        optionNomGenjButton.setText("Options...");
        optionNomGenjButton.setEnabled(false);

        javax.swing.GroupLayout nonNumericaljPanelLayout = new javax.swing.GroupLayout(nonNumericaljPanel);
        nonNumericaljPanel.setLayout(nonNumericaljPanelLayout);
        nonNumericaljPanelLayout.setHorizontalGroup(
            nonNumericaljPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nonNumericaljPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(nonNumericaljPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(numOfRelNomFjLabel)
                    .addComponent(numOfIrrelNomFjLabel)
                    .addComponent(numOfRedNomFjLabel))
                .addGap(29, 29, 29)
                .addGroup(nonNumericaljPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(numOfIrrelNomFjTextField)
                    .addComponent(numOfRedNomFjTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                    .addComponent(numOfRelNomFjTextField))
                .addGap(18, 18, 18)
                .addGroup(nonNumericaljPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(nonNumericaljPanelLayout.createSequentialGroup()
                        .addComponent(redNomGenjLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nomGenjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 85, Short.MAX_VALUE)
                .addGroup(nonNumericaljPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(optionNomGenjButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(maxNumAllowedValuesjTextField))
                .addContainerGap())
        );
        nonNumericaljPanelLayout.setVerticalGroup(
            nonNumericaljPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nonNumericaljPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(nonNumericaljPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numOfRelNomFjLabel)
                    .addComponent(numOfRelNomFjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(maxNumAllowedValuesjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(nonNumericaljPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numOfIrrelNomFjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numOfIrrelNomFjLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(nonNumericaljPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numOfRedNomFjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numOfRedNomFjLabel)
                    .addComponent(redNomGenjLabel)
                    .addComponent(nomGenjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(optionNomGenjButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout featuresjPanelLayout = new javax.swing.GroupLayout(featuresjPanel);
        featuresjPanel.setLayout(featuresjPanelLayout);
        featuresjPanelLayout.setHorizontalGroup(
            featuresjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(featuresjPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(featuresjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(numericaljPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(nonNumericaljPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        featuresjPanelLayout.setVerticalGroup(
            featuresjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(featuresjPanelLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(numericaljPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nonNumericaljPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(6, 6, 6))
        );

        generaljPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "General", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        datasetNamejLabel.setText("Dataset name:");

        numOfObjectsjLabel.setText("Number of objects:");

        numOfClassesjLabel.setText("Num. of classes/clusters:");

        seedjLabel.setText("Seed:");

        numOfObjectsjTextField.setText("300");
        numOfObjectsjTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                numOfObjectsjTextFieldKeyTyped(evt);
            }
        });

        numOfClassesjTextField.setText("2");
        numOfClassesjTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                numOfClassesjTextFieldKeyTyped(evt);
            }
        });

        seedjTextField.setText("1");
        seedjTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                seedjTextFieldKeyTyped(evt);
            }
        });

        classBalanceRatejLabel.setText("Class balance rate:");

        classBalanceRatejTextField.setText("1.0");
        classBalanceRatejTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                classBalanceRatejTextFieldKeyTyped(evt);
            }
        });

        numOfFeaturesjLabel.setText("Number of features:");

        javax.swing.GroupLayout generaljPanel1Layout = new javax.swing.GroupLayout(generaljPanel1);
        generaljPanel1.setLayout(generaljPanel1Layout);
        generaljPanel1Layout.setHorizontalGroup(
            generaljPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generaljPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(generaljPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(datasetNamejLabel)
                    .addComponent(numOfObjectsjLabel)
                    .addComponent(numOfClassesjLabel))
                .addGap(27, 27, 27)
                .addGroup(generaljPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(generaljPanel1Layout.createSequentialGroup()
                        .addGroup(generaljPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(numOfClassesjTextField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(numOfObjectsjTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE))
                        .addGap(36, 36, 36)
                        .addGroup(generaljPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(numOfFeaturesjLabel)
                            .addComponent(classBalanceRatejLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(generaljPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(generaljPanel1Layout.createSequentialGroup()
                                .addComponent(numOfFeaturesDisplayerjLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(generaljPanel1Layout.createSequentialGroup()
                                .addComponent(classBalanceRatejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addComponent(seedjLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(seedjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(datasetNamejTextField))
                .addContainerGap())
        );
        generaljPanel1Layout.setVerticalGroup(
            generaljPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generaljPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(generaljPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(datasetNamejLabel)
                    .addComponent(datasetNamejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(generaljPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(classBalanceRatejLabel)
                    .addComponent(classBalanceRatejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numOfClassesjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numOfClassesjLabel)
                    .addComponent(seedjLabel)
                    .addComponent(seedjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(generaljPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numOfObjectsjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numOfObjectsjLabel)
                    .addComponent(numOfFeaturesjLabel)
                    .addComponent(numOfFeaturesDisplayerjLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dataGenerationjPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data Generation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        loadDatasetjButton.setText("Load data...");
        loadDatasetjButton.setEnabled(false);
        loadDatasetjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadDatasetjButtonActionPerformed(evt);
            }
        });

        dataGenerationbuttonGroup.add(newDatasetjRadioButton);
        newDatasetjRadioButton.setSelected(true);
        newDatasetjRadioButton.setText("New dataset");
        newDatasetjRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newDatasetjRadioButtonActionPerformed(evt);
            }
        });

        dataGenerationbuttonGroup.add(fromExistingDatasetjRadioButton);
        fromExistingDatasetjRadioButton.setText("From existing dataset");
        fromExistingDatasetjRadioButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fromExistingDatasetjRadioButtonMouseClicked(evt);
            }
        });
        fromExistingDatasetjRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fromExistingDatasetjRadioButtonActionPerformed(evt);
            }
        });

        statusTextjLabel.setText("None");

        statusjLabel.setText("Status:");

        javax.swing.GroupLayout dataGenerationjPanelLayout = new javax.swing.GroupLayout(dataGenerationjPanel);
        dataGenerationjPanel.setLayout(dataGenerationjPanelLayout);
        dataGenerationjPanelLayout.setHorizontalGroup(
            dataGenerationjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataGenerationjPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(newDatasetjRadioButton)
                .addGap(66, 66, 66)
                .addComponent(fromExistingDatasetjRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loadDatasetjButton)
                .addGap(18, 18, 18)
                .addComponent(statusjLabel)
                .addGap(18, 18, 18)
                .addComponent(statusTextjLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        dataGenerationjPanelLayout.setVerticalGroup(
            dataGenerationjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataGenerationjPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dataGenerationjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newDatasetjRadioButton)
                    .addComponent(fromExistingDatasetjRadioButton)
                    .addComponent(loadDatasetjButton)
                    .addComponent(statusjLabel)
                    .addComponent(statusTextjLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Outputs", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jButton1.setText("Data Output Dir");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        outputDirectoryjTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputDirectoryjTextFieldActionPerformed(evt);
            }
        });

        histogramjButton.setText("Feature Histograms");
        histogramjButton.setEnabled(false);
        histogramjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                histogramjButtonActionPerformed(evt);
            }
        });

        formatjLabel.setText("Format:");

        outputFormatjComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ARFF", "CSV" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outputDirectoryjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(formatjLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(outputFormatjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addComponent(histogramjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(outputDirectoryjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(histogramjButton)
                    .addComponent(formatjLabel)
                    .addComponent(outputFormatjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        generateDatajButton.setText("Generate data");
        generateDatajButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateDatajButtonActionPerformed(evt);
            }
        });

        resetjButton.setText("Reset");
        resetjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetjButtonActionPerformed(evt);
            }
        });

        morejButton.setText("More...");
        morejButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                morejButtonActionPerformed(evt);
            }
        });

        closejButton.setText("Close");
        closejButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closejButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(generateDatajButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resetjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(morejButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 203, Short.MAX_VALUE)
                .addComponent(closejButton, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(generateDatajButton)
                    .addComponent(resetjButton)
                    .addComponent(morejButton)
                    .addComponent(closejButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(featuresjPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dataGenerationjPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(generaljPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dataGenerationjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(generaljPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(featuresjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void generateDatajButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateDatajButtonActionPerformed

        MixedDataGenerator mdg = getGeneratorFromInputParameters();
        try {
            statusTextjLabel.setText("Generating data...");
            mdg.defineDataFormat();
            generatedData = mdg.generateExamples();
            writeInstances(generatedData, generatedData.relationName(),  (String) outputFormatjComboBox.getSelectedItem());
            statusTextjLabel.setForeground(DARK_GREEN);
            statusTextjLabel.setText("All Done!");
            histogramjButton.setEnabled(true);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input error", JOptionPane.ERROR_MESSAGE);
            statusTextjLabel.setForeground(Color.RED);
            statusTextjLabel.setText("Failed");
        }
    }//GEN-LAST:event_generateDatajButtonActionPerformed

    private void resetjButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetjButtonActionPerformed
        newDatasetjRadioButton.setSelected(true);
        setNewDataSetDefaultValues();
    }//GEN-LAST:event_resetjButtonActionPerformed

    private void fromExistingDatasetjRadioButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fromExistingDatasetjRadioButtonMouseClicked
        loadDatasetjButton.setEnabled(true);
    }//GEN-LAST:event_fromExistingDatasetjRadioButtonMouseClicked

    private void closejButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closejButtonActionPerformed
        parentFrame.dispose();
    }//GEN-LAST:event_closejButtonActionPerformed

    private void loadDatasetjButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadDatasetjButtonActionPerformed
      
        try {

           File file = SwingUtils.getFile("arff", "csv");

            if (file == null) {
                return;
            }
            if(file.getAbsolutePath().toLowerCase().endsWith("csv".toLowerCase())){
                 CSVLoader loader = new CSVLoader();
                 loader.setSource(file);
                inputData = loader.getDataSet();  
            }else{        
                ConverterUtils.DataSource source = new ConverterUtils.DataSource(file.getAbsolutePath());
                inputData = source.getDataSet();
            }
            
            if (inputData == null) {
                JOptionPane.showMessageDialog(this, "Wrong input file! ", "Message", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (inputData.classIndex() == -1) {
                inputData.setClassIndex(inputData.numAttributes() - 1);
            }
            setFromExistingDatasetDefaultValues();
            setValuesForFromExistingDataset();

        } catch (Exception ex) {
            System.out.println(ex);
            JOptionPane.showMessageDialog(this, "Error in input file!: " + ex, "Message", JOptionPane.ERROR_MESSAGE);
        } finally {
            this.setFocusable(true);
        }
    }//GEN-LAST:event_loadDatasetjButtonActionPerformed

    private void fromExistingDatasetjRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fromExistingDatasetjRadioButtonActionPerformed
        setFromExistingDatasetDefaultValues();
    }//GEN-LAST:event_fromExistingDatasetjRadioButtonActionPerformed

    private void newDatasetjRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newDatasetjRadioButtonActionPerformed
        setNewDataSetDefaultValues();
    }//GEN-LAST:event_newDatasetjRadioButtonActionPerformed

    private void numOfClassesjTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_numOfClassesjTextFieldKeyTyped
        char c = evt.getKeyChar();
        if (Character.isLetter(c)) {
            getToolkit().beep();
            evt.consume();
            JOptionPane.showMessageDialog(this, "\"You should enter only numbers\"! ", "Message", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_numOfClassesjTextFieldKeyTyped

    private void classBalanceRatejTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_classBalanceRatejTextFieldKeyTyped
        char c = evt.getKeyChar();
        if (Character.isLetter(c)) {
            getToolkit().beep();
            evt.consume();
            JOptionPane.showMessageDialog(this, "\"Invalid input\"! ", "Message", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_classBalanceRatejTextFieldKeyTyped

    private void numOfObjectsjTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_numOfObjectsjTextFieldKeyTyped
        char c = evt.getKeyChar();
        if (Character.isLetter(c)) {
            getToolkit().beep();
            evt.consume();
            JOptionPane.showMessageDialog(this, "\"Invalid input\"! ", "Message", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_numOfObjectsjTextFieldKeyTyped

    private void seedjTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_seedjTextFieldKeyTyped
        char c = evt.getKeyChar();
        if (Character.isLetter(c)) {
            getToolkit().beep();
            evt.consume();
            JOptionPane.showMessageDialog(this, "\"Invalid input\"! ", "Message", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_seedjTextFieldKeyTyped

    private void numOfRelNumFjTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_numOfRelNumFjTextFieldKeyTyped
        char c = evt.getKeyChar();
        if (Character.isLetter(c)) {
            getToolkit().beep();
            evt.consume();
            JOptionPane.showMessageDialog(this, "\"Invalid input\"! ", "Message", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_numOfRelNumFjTextFieldKeyTyped

    private void numOfIrrelNumFjTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_numOfIrrelNumFjTextFieldKeyTyped
        char c = evt.getKeyChar();
        if (Character.isLetter(c)) {
            getToolkit().beep();
            evt.consume();
            JOptionPane.showMessageDialog(this, "\"Invalid input\"! ", "Message", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_numOfIrrelNumFjTextFieldKeyTyped

    private void numOfRedNumFjTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_numOfRedNumFjTextFieldKeyTyped
        char c = evt.getKeyChar();
        if (Character.isLetter(c)) {
            getToolkit().beep();
            evt.consume();
            JOptionPane.showMessageDialog(this, "\"Invalid input\"! ", "Message", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_numOfRedNumFjTextFieldKeyTyped

    private void numOfRelNomFjTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_numOfRelNomFjTextFieldKeyTyped
        char c = evt.getKeyChar();
        if (Character.isLetter(c)) {
            getToolkit().beep();
            evt.consume();
            JOptionPane.showMessageDialog(this, "\"Invalid input\"! ", "Message", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_numOfRelNomFjTextFieldKeyTyped

    private void numOfIrrelNomFjTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_numOfIrrelNomFjTextFieldKeyTyped
        char c = evt.getKeyChar();
        if (Character.isLetter(c)) {
            getToolkit().beep();
            evt.consume();
            JOptionPane.showMessageDialog(this, "\"Invalid input\"! ", "Message", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_numOfIrrelNomFjTextFieldKeyTyped

    private void numOfRedNomFjTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_numOfRedNomFjTextFieldKeyTyped
        char c = evt.getKeyChar();
        if (Character.isLetter(c)) {
            getToolkit().beep();
            evt.consume();
            JOptionPane.showMessageDialog(this, "\"Invalid input\"! ", "Message", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_numOfRedNomFjTextFieldKeyTyped

    private void maxNumAllowedValuesjTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_maxNumAllowedValuesjTextFieldKeyTyped
        char c = evt.getKeyChar();
        if (Character.isLetter(c)) {
            getToolkit().beep();
            evt.consume();
            JOptionPane.showMessageDialog(this, "\"Invalid input\"! ", "Message", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_maxNumAllowedValuesjTextFieldKeyTyped

    private void equalIntervaljComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_equalIntervaljComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_equalIntervaljComboBoxActionPerformed

    private void morejButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_morejButtonActionPerformed
        String str = "\n";
        str += "Data Generation: \nData generation can be in two ways -> New dataset or from an existing one. \n\n";
        
        str += "Outputs: \n";
        str += "Output Dir -> The directory where the generated data will be saved.\n"
                + "Format-> The output format which the dataset will be saved (ARFF or CSV).\n"
                + "Feature Histograms -> Show a panel containing a matrix of histogram plots of features (first 100) in the generated dataset. \n\n";
        
        str += "General:\n";
        str += "dataset name -> The name of the generated dataset.\n"
                + "Num of classes/clusters -> The number of classes in the generated dataset.\n"
                + "Num of objects -> The number of objects in the generated dataset.\n"
                + "Class balance rate -> Index to control the proportion of objects in each class. Any number in the interval (0 and 1] can be set.\n"
                + "Seed -> The seed used for producing the dataset. \n\n";
        
        str += "Features:\n";
        str += "Num. of Relevant -> The number of relevant features. For each cluster, numerical features follow a Multivariate Normal distribution; "
                + "meanwhile for non-numerical features, a Multivariate Multinomial distribution is followed.\n";
        str += "Num. of Irrelevant -> The number of irrelevant features. "
                + "Irrelevant features follow a Uniform distribution between an especific interval value for all clusters.\n";
        str += "Num. of Redundant -> The number of redundant features. "
                + "Redundant featutes are generated randomized from the relevant ones.\n";
        str += "Generator -> The generator used for producing redundant features.\n";
        
        str += "Equal interval for irrelevant feature ->A boolean value to configure whether irrelevant features will take values in the same interval or not.\n";
        
        str += "Maximun number of allowed values -> The maximun number of allowed values for non-numerical features.\n";
        MoreInfoPanel morePanel = new MoreInfoPanel(str);
        morePanel.setVisible(true);
    }//GEN-LAST:event_morejButtonActionPerformed

    private void outputDirectoryjTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputDirectoryjTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_outputDirectoryjTextFieldActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
         JFileChooser jfch = new JFileChooser();
        jfch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (jfch.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            outputDirectoryjTextField.setText(jfch.getSelectedFile().getPath());
            outputPath = outputDirectoryjTextField.getText();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void histogramjButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_histogramjButtonActionPerformed
        if (generatedData != null) {
                   try {
                       ShowHist(generatedData);
                   } catch (Exception ex) {
                       JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input error", JOptionPane.ERROR_MESSAGE);
                   }
               }
    }//GEN-LAST:event_histogramjButtonActionPerformed

    private void ShowHist(Instances instances) throws Exception {

        AttributeSummarizer as = new AttributeSummarizer();
        as.setActive(false);
        as.setInstances(instances);

        final javax.swing.JFrame jf = new javax.swing.JFrame();
        jf.getContentPane().setLayout(new java.awt.BorderLayout());

        jf.getContentPane().add(as, java.awt.BorderLayout.CENTER);
        jf.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                jf.dispose();
            }
        });
        jf.setSize(830, 600);
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
    }
    
        private void writeInstances(Instances instances, String name, String output) throws IOException, JAXBException {

        if (output.equalsIgnoreCase("CSV")) {
            CSVSaver saver = new CSVSaver();
            saver.setInstances(instances);//set the dataset we want to convert and save as CSV
            saver.setFile(new File(outputPath + File.separator + name + ".csv"));
            saver.writeBatch();
  
        } else {
            ArffSaver saver = new ArffSaver();
            instances.setClassIndex(instances.numAttributes() - 1);
            saver.setInstances(instances);
            saver.setFile(new File(outputPath + File.separator + name + ".arff"));
            saver.writeBatch();
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel NumGeneratorjLabel;
    private javax.swing.JLabel NumOfNumRelFjLabel;
    private javax.swing.JLabel classBalanceRatejLabel;
    private javax.swing.JTextField classBalanceRatejTextField;
    private javax.swing.JButton closejButton;
    private javax.swing.ButtonGroup dataGenerationbuttonGroup;
    private javax.swing.JPanel dataGenerationjPanel;
    private javax.swing.JLabel datasetNamejLabel;
    private javax.swing.JTextField datasetNamejTextField;
    private javax.swing.JComboBox<String> equalIntervaljComboBox;
    private javax.swing.JLabel equalIntervaljLabel;
    private javax.swing.JPanel featuresjPanel;
    private javax.swing.JLabel formatjLabel;
    private javax.swing.JRadioButton fromExistingDatasetjRadioButton;
    private javax.swing.JPanel generaljPanel1;
    private javax.swing.JButton generateDatajButton;
    private javax.swing.JButton histogramjButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private java.awt.Label label1;
    private javax.swing.JButton loadDatasetjButton;
    private javax.swing.JTextField maxNumAllowedValuesjTextField;
    private javax.swing.JButton morejButton;
    private javax.swing.JRadioButton newDatasetjRadioButton;
    private javax.swing.JComboBox<String> nomGenjComboBox;
    private javax.swing.JPanel nonNumericaljPanel;
    private javax.swing.JComboBox<String> numGenjComboBox;
    private javax.swing.JLabel numOfClassesjLabel;
    private javax.swing.JTextField numOfClassesjTextField;
    private javax.swing.JLabel numOfFeaturesDisplayerjLabel;
    private javax.swing.JLabel numOfFeaturesjLabel;
    private javax.swing.JLabel numOfIrrelNomFjLabel;
    private javax.swing.JTextField numOfIrrelNomFjTextField;
    private javax.swing.JLabel numOfIrrelNumFjLabel;
    private javax.swing.JTextField numOfIrrelNumFjTextField;
    private javax.swing.JLabel numOfObjectsjLabel;
    private javax.swing.JTextField numOfObjectsjTextField;
    private javax.swing.JLabel numOfRedNomFjLabel;
    private javax.swing.JTextField numOfRedNomFjTextField;
    private javax.swing.JLabel numOfRedNumFjLabel;
    private javax.swing.JTextField numOfRedNumFjTextField;
    private javax.swing.JLabel numOfRelNomFjLabel;
    private javax.swing.JTextField numOfRelNomFjTextField;
    private javax.swing.JTextField numOfRelNumFjTextField;
    private javax.swing.JPanel numericaljPanel;
    private javax.swing.JButton optionNomGenjButton;
    private javax.swing.JButton optionsNumGenjButton;
    private javax.swing.JTextField outputDirectoryjTextField;
    private javax.swing.JComboBox<String> outputFormatjComboBox;
    private javax.swing.JLabel redNomGenjLabel;
    private javax.swing.JButton resetjButton;
    private javax.swing.JLabel seedjLabel;
    private javax.swing.JTextField seedjTextField;
    private javax.swing.JLabel statusTextjLabel;
    private javax.swing.JLabel statusjLabel;
    // End of variables declaration//GEN-END:variables
}
