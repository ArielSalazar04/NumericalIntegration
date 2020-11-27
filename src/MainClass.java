import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.data.category.DefaultCategoryDataset;

public class MainClass{
    // Java Swing Objects
    private final JFrame frame;
    private final JPanel inputPanel;
    private final JTextField fieldForH;
    private final JTextField fieldForN;
    private final JTextField fieldForA;
    private final JFileChooser fileChoose;
    private final JComboBox option;
    private final JLabel dataField;

    // Screen dimensions
    private final static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final static int height = screenSize.height;
    private final static int width = screenSize.width;

    // Fonts
    private final Font titleFont = new Font("Serif", Font.BOLD, 32);
    private final Font labelFont = new Font("Serif", Font.PLAIN, 18);

    public MainClass() {
        // Frame
        frame = new JFrame();
        JLabel title = new JLabel("Numerical Integration Calculator");
        title.setFont(titleFont);

        // H-value objects
        JLabel hLabel = new JLabel("Enter a value for h: ", SwingConstants.CENTER);
        hLabel.setFont(labelFont);
        fieldForH = new JTextField(12);

        // N-value objects
        JLabel nLabel = new JLabel("Enter a value for n: ", SwingConstants.CENTER);
        nLabel.setFont(labelFont);
        fieldForN = new JTextField(12);

        // A-value objects
        JLabel aLabel = new JLabel("Enter a value for a: ", SwingConstants.CENTER);
        aLabel.setFont(labelFont);
        fieldForA = new JTextField(12);

        // Object for selecting integration method
        JLabel optionLabel = new JLabel("Integration option: ", SwingConstants.CENTER);
        optionLabel.setFont(labelFont);
        option = new JComboBox<>(new String[]{"Trapezoidal Rule", "Simpson's Rule"});

        // Object for importing file result field, and integration method objects
        fileChoose = new JFileChooser();

        // Object for results
        dataField = new JLabel("\n");
        dataField.setFont(labelFont);

        // Browse file
        JButton browseFileButton = new JButton(new AbstractAction("Browse File") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Opens File Explorer
                fileChoose.showOpenDialog(null);

                // If dataField has no parent, assign panel as parent
                if (dataField.getParent() == null)
                    inputPanel.add(dataField);

                // Display the name of the chosen text file
                File file = fileChoose.getSelectedFile();
                if (file == null)
                    dataField.setText("");
                else
                    dataField.setText(String.format("File Selected: %s", file.getName()));
            }
        });

        // Submit button ActionListener
        JButton findAreaButton = new JButton(new AbstractAction("Find Area") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fieldForH.getText().equals(""))
                    JOptionPane.showMessageDialog(frame, "Missing value for H");
                else if (fieldForN.getText().equals(""))
                    JOptionPane.showMessageDialog(frame, "Missing value for N");
                else if (Integer.parseInt(fieldForN.getText()) % 2 == 1 && String.valueOf(option.getSelectedItem()).equals("Simpson's Rule")) {
                    JOptionPane.showMessageDialog(frame, "N must be even when applying Simpson's Rule.");
                }
                else {
                    File importedFile = fileChoose.getSelectedFile();


                    // Data
                    ArrayList<Double> xValues = new ArrayList<>();
                    ArrayList<Double> fxValues = new ArrayList<>();

                    // Checks if file is selected
                    if (importedFile != null) {
                        String fileName = importedFile.getName();
                        int periodIndex = fileName.lastIndexOf(".");
                        String ext = fileName.substring(periodIndex+1);

                        if(ext.equals("txt") | ext.equals("csv"))
                        {
                            try {
                                // Read data from text file into ArrayLists
                                Scanner fileScanner = new Scanner(importedFile);
                                while (fileScanner.hasNextLine()) {
                                    String[] pair = fileScanner.nextLine().split(",");
                                    xValues.add(Double.parseDouble(pair[0]));
                                    fxValues.add(Double.parseDouble(pair[1]));
                                }


                                // Obtain text from input fields and compute area
                                double n = Double.parseDouble(fieldForN.getText());
                                double h = Double.parseDouble(fieldForH.getText());
                                double a = Double.parseDouble(fieldForA.getText());
                                boolean isTrap = String.valueOf(option.getSelectedItem()).charAt(0) == 'T';

                                // Lagrange Interpolation Object
                                LagrangeInterpolation obj = new LagrangeInterpolation(xValues, fxValues);
                                double area = obj.numericalIntegration(xValues, fxValues, n, h, isTrap);
                                ArrayList<Double> xInterpolatedValues = obj.getxGraphValues();
                                ArrayList<Double> fxInterpolatedValues = obj.getfxGraphValues();

                                // Display the area
                                dataField.setText(String.format("Area under curve: %f", area));

                                // Create dataset
                                DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                                for (int i = 0; i < fxInterpolatedValues.size(); i++)
                                    dataset.addValue(fxInterpolatedValues.get(i), "f(x)", xInterpolatedValues.get(i));

                                // Create chart panel
                                JPanel panelForChart = new ChartPanel(ChartFactory.createLineChart(
                                        "Lagrange Interpolated Polynomial", "x", "f(x)", dataset));

                                // Open new frame with graph
                                JFrame myFrame = new JFrame();
                                myFrame.add(panelForChart);
                                myFrame.setTitle("Function of X");
//                            myFrame.setPreferredSize(new Dimension(width / 2, height / 2));
                                myFrame.setLocationRelativeTo(null);
//                            myFrame.setResizable(false);
                                myFrame.setVisible(true);
                                myFrame.pack();

                            } catch (FileNotFoundException fileNotFoundException) {
                                fileNotFoundException.printStackTrace();
                            }
                        }
                        else
                            JOptionPane.showMessageDialog(frame, "File must have .txt or .csv extension");


                    }
                    else
                        JOptionPane.showMessageDialog(frame, "Missing file");
                }
            }
        });

        // Constraints settings
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.WEST;

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.add(title);

        // Input Panel
        inputPanel = new JPanel(new GridBagLayout());
        constraints.gridx = 0;
        constraints.gridy = 0;
        inputPanel.add(hLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        inputPanel.add(fieldForH, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        inputPanel.add(nLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        inputPanel.add(fieldForN, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        inputPanel.add(aLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        inputPanel.add(fieldForA, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        inputPanel.add(optionLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 3;
        inputPanel.add(option, constraints);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        constraints.gridx = 0;
        constraints.gridy = 0;
        buttonPanel.add(browseFileButton, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        buttonPanel.add(findAreaButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        buttonPanel.add(dataField, constraints);

        // Merge panels
        mainPanel.add(titlePanel);
        mainPanel.add(inputPanel);
        mainPanel.add(buttonPanel);

        // Frame settings
        frame.add(mainPanel);
        frame.setTitle("Numerical Integration Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setPreferredSize(new Dimension(width/3, height/4));
        frame.setLocationRelativeTo(null);
//        frame.setResizable(false);
        frame.setVisible(true);
        frame.pack();
    }

    public static void main(String [] args){
        new MainClass();
    }
}
