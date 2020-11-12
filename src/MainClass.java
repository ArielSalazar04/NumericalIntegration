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
    // Frame and Title
    private final JFrame frame;
    private final JLabel title;

    // Panels
    private final JPanel mainPanel;
    private final JPanel titlePanel;
    private final JPanel inputPanel;
    private final JPanel buttonPanel;

    // Constraints
    private final GridBagConstraints constraints;

    // H-value objects
    private final JLabel hLabel;
    private final JTextField fieldForH;

    // N-value objects
    private final JLabel nLabel;
    private final JTextField fieldForN;

    // A-value objects
    private final JLabel aLabel;
    private final JTextField fieldForA;

    // Browse file
    private final JButton browseFileButton;
    private final JFileChooser fileChoose;

    // Differentiation options
    private final JLabel optionLabel;
    private final JComboBox option;

    // Result objects
    private final JLabel dataField;
    private final JButton findAreaButton;

    // Screen dimensions
    private final static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final static int height = screenSize.height;
    private final static int width = screenSize.width;

    // Data
    private final ArrayList<Double> xValues;
    private final ArrayList<Double> fxValues;

    // Interpolated Values
    private final ArrayList<Double> xInterpolatedValues;
    private final ArrayList<Double> fxInterpolatedValues;

    // Fonts
    private final Font titleFont = new Font("Serif", Font.BOLD, 32);
    private final Font labelFont = new Font("Serif", Font.PLAIN, 18);

    public MainClass() {
        // Data
        xValues = new ArrayList<>();
        fxValues = new ArrayList<>();
        xInterpolatedValues = new ArrayList<>();
        fxInterpolatedValues = new ArrayList<>();

        // Frame
        frame = new JFrame();
        title = new JLabel("Numerical Integration Calculator");
        title.setFont(titleFont);

        // H-value
        hLabel = new JLabel("Enter a value for h: ", SwingConstants.CENTER);
        hLabel.setFont(labelFont);
        fieldForH = new JTextField(12);

        // N-value
        nLabel = new JLabel("Enter a value for n: ", SwingConstants.CENTER);
        nLabel.setFont(labelFont);
        fieldForN = new JTextField(12);

        // A-value
        aLabel = new JLabel("Enter a value for a: ", SwingConstants.CENTER);
        aLabel.setFont(labelFont);
        fieldForA = new JTextField(12);

        // Object for selecting integration method
        optionLabel = new JLabel("Integration option: ", SwingConstants.CENTER);
        optionLabel.setFont(labelFont);
        option = new JComboBox<>(new String[]{"Trapezoidal Rule", "Simpson's Rule"});

        // Object for importing file result field, and integration method objects
        fileChoose = new JFileChooser();

        // Object for results
        dataField = new JLabel("\n");
        dataField.setFont(labelFont);

        // Browse file ActionListener
        browseFileButton = new JButton(new AbstractAction("Browse File") {
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
                    dataField.setText("\n");
                else
                    dataField.setText(String.format("File Selected: %s", file.getName()));
            }
        });

        // Submit button ActionListener
        findAreaButton = new JButton(new AbstractAction("Find Area") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fieldForH.getText().equals(""))
                    JOptionPane.showMessageDialog(frame, "Missing value for H");
                else if (fieldForN.getText().equals(""))
                    JOptionPane.showMessageDialog(frame, "Missing value for N");
                else if (Integer.parseInt(fieldForN.getText()) % 2 == 1 && String.valueOf(option.getSelectedItem()).equals("Simpson's Rule")){
                    JOptionPane.showMessageDialog(frame, "N must be even when applying Simpson's Rule.");
                }
                else {
                    File importedFile = fileChoose.getSelectedFile();

                    // Clear all data for next query
                    xValues.clear();
                    fxValues.clear();
                    xInterpolatedValues.clear();
                    fxInterpolatedValues.clear();

                    // Checks if file is selected
                    if (importedFile != null) {
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
                            String method = String.valueOf(option.getSelectedItem());
                            double area = numericalIntegration(xValues, fxValues, n, h, a, method);

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
                            myFrame.setPreferredSize(new Dimension(width/2, height/2));
                            myFrame.setLocationRelativeTo(null);
                            myFrame.setResizable(false);
                            myFrame.setVisible(true);
                            myFrame.pack();

                        } catch (FileNotFoundException fileNotFoundException) {
                            fileNotFoundException.printStackTrace();
                        }

                    }
                    else {
                        JOptionPane.showMessageDialog(frame, "Missing file");
                    }
                }
            }
        });

        // Constraints settings
        constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.WEST;

        // Main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Title Panel
        titlePanel = new JPanel();
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
        buttonPanel = new JPanel(new GridBagLayout());
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
        frame.setPreferredSize(new Dimension(width/3, height/4));
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.pack();
    }

    double numericalIntegration(ArrayList<Double> xCoords, ArrayList<Double> fxCoords, double n, double h, double a, String option){
        if (option.equals("Trapezoidal Rule"))
            return trapezoidalRule(xCoords, fxCoords, n, h, a);
        else
            return simpsonsRule(xCoords, fxCoords, n, h, a);
    }
    double trapezoidalRule(ArrayList<Double> xCoords, ArrayList<Double> fxCoords, double n, double h, double a){
        double sum = 0, fxo, fxi, fxn;

        // Increment f(xo)
        if (!xCoords.contains(a))
            fxo = lagrangeInterpolation(xCoords, fxCoords, a);
        else
            fxo = fxCoords.get(xCoords.indexOf(a));

        sum += fxo;
        xInterpolatedValues.add(a);
        fxInterpolatedValues.add(fxo);

        // Increment 2 * ∑(fxi) for i ∈ [1, N-1]
        for (double i = a+h; i < n*h+a; i=i+h){
            if (!xCoords.contains(i))
                fxi = 2 * lagrangeInterpolation(xCoords, fxCoords, i);
            else
                fxi = 2 * fxCoords.get(xCoords.indexOf(i));
            sum += fxi;
            xInterpolatedValues.add(i);
            fxInterpolatedValues.add(fxi);
        }

        // Increment f(xn)
        if (!xCoords.contains(n*h+a))
            fxn = lagrangeInterpolation(xCoords, fxCoords, n*h+a);
        else
            fxn = fxCoords.get(xCoords.indexOf(n*h+a));

        sum += fxn;
        xInterpolatedValues.add(n*h+a);
        fxInterpolatedValues.add(fxn);

        return h * sum / 2;
    }
    double simpsonsRule(ArrayList<Double> xCoords, ArrayList<Double> fxCoords, double n, double h, double a){
        double sum = 0, fxo, fxi, fxn;
        
        // Increment f(a)
        if (!xCoords.contains(a))
            fxo = lagrangeInterpolation(xCoords, fxCoords, a);
        else
            fxo = fxCoords.get(xCoords.indexOf(a));

        sum += fxo;
        xInterpolatedValues.add(a);
        fxInterpolatedValues.add(fxo);

        // Increment 2 * ∑(fx2k) for i ∈ [1, (N-2)-1]
        for (double k = 1; k < n; k++){
            if (k % 2 == 0){
                if (!xCoords.contains(k*h+a))
                    fxi = 2 * lagrangeInterpolation(xCoords, fxCoords, k*h+a);
                else
                    fxi = 2 * fxCoords.get(xCoords.indexOf(k*h+a));
            }
            else{
                if (!xCoords.contains(k*h+a))
                    fxi = 4 * lagrangeInterpolation(xCoords, fxCoords, k*h+a);
                else
                    fxi = 4 * fxCoords.get(xCoords.indexOf(k*h+a));
            }
            sum += fxi;
            xInterpolatedValues.add(k*h+a);
            fxInterpolatedValues.add(fxi);
        }

        // Increment f(b)
        if (!xCoords.contains(n*h+a))
            fxn = lagrangeInterpolation(xCoords, fxCoords, n*h+a);
        else
            fxn = fxCoords.get(xCoords.indexOf(n*h+a));

        sum += fxn;
        xInterpolatedValues.add(n*h+a);
        fxInterpolatedValues.add(fxn);

        return h * sum / 3;
    }

    public double lagrangeInterpolation(ArrayList<Double> xCoords, ArrayList<Double> yCoords, double x){
        int n = xCoords.size();
        double interpolatedValue = 0, lagrangian, xi, xj;

        for (int i = 0; i < n; i++){
            lagrangian = 1;
            xi = xCoords.get(i);
            for (int j = 0; j < n; j++){
                if (i != j){
                    xj = xCoords.get(j);
                    lagrangian *= (x - xj)/(xi - xj);
                }
            }
            interpolatedValue += lagrangian * yCoords.get(i);
        }
        return interpolatedValue;
    }

    public static void main(String [] args){
        new MainClass();
    }
}
