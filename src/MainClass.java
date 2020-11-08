import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class MainClass{
    // Frame, Panel, and Title
    private final JFrame frame;
    private final JPanel inputPanel;
    private final JLabel title;

    // H-value objects
    private final JLabel hLabel;
    private final JTextField fieldForH;

    // N-value objects
    private final JLabel nLabel;
    private final JTextField fieldForN;

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

    // Fonts
    private final Font titleFont = new Font("Serif", Font.BOLD, 32);
    private final Font labelFont = new Font("Serif", Font.PLAIN, 18);

    public MainClass() {
        // Data
        xValues = new ArrayList<>();
        fxValues = new ArrayList<>();

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

        // Object for selecting integration method
        optionLabel = new JLabel("Integration option: ", SwingConstants.CENTER);
        optionLabel.setFont(labelFont);
        option = new JComboBox(new String[]{"Trapezoidal Rule", "Simpson's Rule"});

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
                    xValues.clear();
                    fxValues.clear();

                    if (importedFile != null) {
                        try {
                            Scanner fileScanner = new Scanner(importedFile);
                            while (fileScanner.hasNextLine()) {
                                String[] pair = fileScanner.nextLine().split(",");
                                xValues.add(Double.parseDouble(pair[0]));
                                fxValues.add(Double.parseDouble(pair[1]));
                            }
                            double n = Double.parseDouble(fieldForN.getText());
                            double h = Double.parseDouble(fieldForH.getText());
                            double area = numericalIntegration(xValues, fxValues, n, h, String.valueOf(option.getSelectedItem()));
                            dataField.setText(String.format("Area under curve: %f", area));
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
        inputPanel.add(optionLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
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
        frame.setPreferredSize(new Dimension(width/3, height/4));
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.pack();
    }

    double numericalIntegration(ArrayList<Double> xCoords, ArrayList<Double> fxCoords, double n, double h, String option){
        if (option.equals("Trapezoidal Rule"))
            return trapezoidalRule(xCoords, fxCoords, n, h);
        else
            return simpsonsRule(xCoords, fxCoords, n, h);
    }
    double trapezoidalRule(ArrayList<Double> xCoords, ArrayList<Double> fxCoords, double n, double h){
        double sum = 0;

        // Increment f(xo)
        if (!xCoords.contains(0.0))
            sum += lagrangeInterpolation(xCoords, fxCoords, 0.0);
        else
            sum += fxCoords.get(xCoords.indexOf(0.0));

        // Increment 2 * ∑(fxi) for i ∈ [1, N-1]
        for (double i = h; i < n*h; i=i+h){
            if (!xCoords.contains(i))
                sum += 2 * lagrangeInterpolation(xCoords, fxCoords, i);
            else
                sum += 2 * fxCoords.get(xCoords.indexOf(i));
        }

        // Increment f(xn)
        if (!xCoords.contains(n*h))
            sum += lagrangeInterpolation(xCoords, fxCoords, n*h);
        else
            sum += fxCoords.get(xCoords.indexOf(n*h));

        return h * sum / 2;
    }
    double simpsonsRule(ArrayList<Double> xCoords, ArrayList<Double> fxCoords, double n, double h){
        double sum = 0;

        // Increment f(a)
        if (!xCoords.contains(0.0))
            sum += lagrangeInterpolation(xCoords, fxCoords, 0.0);
        else
            sum += fxCoords.get(xCoords.indexOf(0.0));

        // Increment 2 * ∑(fx2k) for i ∈ [1, (N-2)-1]
        for (double k = 1; k < n; k++){
            if (k % 2 == 0){
                if (!xCoords.contains(k * h))
                    sum += 2 * lagrangeInterpolation(xCoords, fxCoords, k * h);
                else
                    sum += 2 * fxCoords.get(xCoords.indexOf(k * h));
            }
            else{
                if (!xCoords.contains(k * h))
                    sum += 4 * lagrangeInterpolation(xCoords, fxCoords, k * h);
                else
                    sum += 4 * fxCoords.get(xCoords.indexOf(k * h));
            }
        }

        // Increment f(b)
        if (!xCoords.contains(n*h))
            sum += lagrangeInterpolation(xCoords, fxCoords, n*h);
        else
            sum += fxCoords.get(xCoords.indexOf(n*h));

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
