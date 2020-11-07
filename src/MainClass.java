import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class MainClass{
    // Frame, Panel, and Title
    private final JFrame frame;
    private final JPanel panel;
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
    private final JComboBox option;

    // Result objects
    private final JLabel dataField;
    private final JButton submitButton;

    // Screen dimensions
    private final static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final static int height = screenSize.height;
    private final static int width = screenSize.width;

    // Data
    private final ArrayList<Double> xValues;
    private final ArrayList<Double> fxValues;

    public MainClass() {
        // Data
        xValues = new ArrayList<>();
        fxValues = new ArrayList<>();

        // Frame
        frame = new JFrame();
        title = new JLabel("Numerical Integration Calculator");

        // H-value
        hLabel = new JLabel("Enter a value for h", SwingConstants.CENTER);
        fieldForH = new JTextField();

        // N-value
        nLabel = new JLabel("Enter a value for n", SwingConstants.CENTER);
        fieldForN = new JTextField();

        // Browse file ActionListener
        browseFileButton = new JButton(new AbstractAction("Browse File") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Opens File Explorer
                fileChoose.showOpenDialog(null);

                // If dataField has no parent, assign panel as parent
                if (dataField.getParent() == null)
                    panel.add(dataField);

                // Display the name of the chosen text file
                String fileName = fileChoose.getSelectedFile().getName();
                dataField.setText(String.format("File: %s", fileName));
            }
        });

        // File, result field, and integration method objects
        fileChoose = new JFileChooser();
        dataField = new JLabel("Text");
        String [] options = {"Trapezoidal Rule", "Simpson's Rule"};
        option = new JComboBox(options);

        // Submit button ActionListener
        submitButton = new JButton(new AbstractAction("Find Area") {
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

        // Panel settings
        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(240, 225, 240, 225));
        panel.setLayout(new GridLayout(0, 1));
        panel.add(title);
        panel.add(hLabel);
        panel.add(fieldForH);
        panel.add(nLabel);
        panel.add(fieldForN);
        panel.add(option);
        panel.add(browseFileButton);
        panel.add(submitButton);

        // Frame settings
        frame.add(panel, BorderLayout.CENTER);
        frame.setTitle("Numerical Differentiation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width/2, height/2);
        frame.setLocationRelativeTo(null);
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
