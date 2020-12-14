// Java Swing objects
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;

// Constraints, font, color, dimensions
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Insets;

// Others
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

// Graphing objects
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.data.category.DefaultCategoryDataset;

public class MainClass {
    // Java Swing Objects
    private final JFrame frame;
    private final JComboBox<String> dataImportOption;
    private final JTextField fieldForH;
    private final JTextField fieldForN;
    private final JTextField fieldForArea;
    private final JFileChooser fileChoose;
    private final JComboBox<String> option;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final GridBagConstraints constraints;

    // Data
    private final ArrayList<Double> xValues = new ArrayList<>();
    private final ArrayList<Double> fxValues = new ArrayList<>();
    private ArrayList<Double> xInterpolatedValues;
    private ArrayList<Double> fxInterpolatedValues;

    public MainClass() {
        // Frame
        frame = new JFrame("Numerical Integration Calculator");
        JLabel title = new JLabelC("Numerical Integration", true);

        // Data importing selector object
        JLabel dataImportLabel = new JLabelC("Import: ", false);
        dataImportOption = new JComboBox<>(new String[]{"Import Data", "Import File"});

        // H-value objects
        JLabel hLabel = new JLabelC("Enter h: ", false);
        fieldForH = new JTextField(12);

        // N-value objects
        JLabel nLabel = new JLabelC("Enter n: ", false);
        fieldForN = new JTextField(12);

        //Output Area
        fieldForArea = new JTextField(12);

        // Object for selecting integration method
        JLabel optionLabel = new JLabelC("Option: ", false);
        option = new JComboBox<>(new String[]{"Trapezoidal Rule", "Simpson's Rule"});

        // Object for importing file result field, and integration method objects
        fileChoose = new JFileChooser();

        // Show graph
        JButton showGraph = new JButton(new AbstractAction("Graph") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create dataset
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                for (int i = 0; i < fxInterpolatedValues.size(); i++)
                    dataset.addValue(fxInterpolatedValues.get(i), "f(x)", xInterpolatedValues.get(i));

                // Create chart panel
                JPanel panelForChart = new ChartPanel(ChartFactory.createLineChart(
                        "Lagrange Interpolated Polynomial", "x", "f(x)", dataset));

                // Open new frame with graph
                JFrame myFrame = new JFrame("Function of X");
                myFrame.add(panelForChart);
                myFrame.setSize(1012, 675);
                myFrame.setLocationRelativeTo(null);
                myFrame.setVisible(true);
            }
        });
        showGraph.setEnabled(false);


        // Browse file
        JButton browseFileButton = new JButton(new AbstractAction("Browse File") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Opens File Explorer
                fileChoose.showOpenDialog(null);

                // Display the name of the chosen text file
                File file = fileChoose.getSelectedFile();

                // If file was removed (file is null), then disable graph function
                if (file == null) {
                    JOptionPane.showMessageDialog(frame, "File has been deselected.");
                    showGraph.setEnabled(false);
                }
            }
        });
        browseFileButton.setEnabled(false);

        // Submit button ActionListener
        JButton findAreaButton = new JButton(new AbstractAction("Find Area") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int n = 0;
                double h = 0, area;
                boolean validEntries = true;
                xValues.clear();
                fxValues.clear();

                try {
                    // Try to parse the values for h, n and the file/data
                    h = getH();
                    n = getN();
                    getData((Objects.equals(dataImportOption.getSelectedItem(), "Import Data")) ? 1 : 0);
                } catch (Exception exception) {
                    // If there is some error, open a pane showing the error
                    if (exception instanceof FileNotFoundException || exception instanceof NullPointerException)
                        JOptionPane.showMessageDialog(frame, "Missing file.");
                    else if (exception instanceof NumberFormatException)
                        JOptionPane.showMessageDialog(frame, "Table contains non-parsable data.");
                    else
                        JOptionPane.showMessageDialog(frame, exception.getMessage());
                    validEntries = false;
                }

                // If no error was thrown, compute the area
                if (validEntries) {
                    boolean isTrap = String.valueOf(option.getSelectedItem()).charAt(0) == 'T';

                    // Numerical Integration Object
                    NumericalIntegration integrateObject = new NumericalIntegration(xValues, fxValues);

                    // Compute the area and obtain the sub-dataset for the graph
                    area = integrateObject.integrate(n, h, isTrap);
                    xInterpolatedValues = integrateObject.getxGraphValues();
                    fxInterpolatedValues = integrateObject.getfxGraphValues();
                    showGraph.setEnabled(true);

                    // Display the area
                    fieldForArea.setText(String.valueOf(Math.round(area * 1e6) / 1e6));
                } else
                    showGraph.setEnabled(false);

            }
        });

        // Add a row as long as the number of rows does not exceed 50
        JButton addRow = new JButton(new AbstractAction("Add Row") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tableModel.getRowCount() < 50) {
                    int index = (table.getSelectedRow() == -1) ? table.getRowCount() : table.getSelectedRow() + 1;
                    tableModel.insertRow(index, new String[]{"", ""});
                } else
                    JOptionPane.showMessageDialog(frame, "You have exceeded the maximum rows (50 rows).");
            }
        });

        // Delete a row as long as the number of rows is at least 3
        JButton deleteRow = new JButton(new AbstractAction("Delete Row") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tableModel.getRowCount() > 3) {
                    int index = (table.getSelectedRow() == -1) ? table.getRowCount() - 1 : table.getSelectedRow();
                    tableModel.removeRow(index);
                } else
                    JOptionPane.showMessageDialog(frame, "You must have at least 3 data points.");
            }
        });

        // Push all the rows down once
        JButton pushDown = new JButton(new AbstractAction("Push Down") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tableModel.getRowCount() >= 50)
                    JOptionPane.showMessageDialog(frame, "You have exceeded the maximum rows (50 rows).");
                else
                    tableModel.insertRow(0, new String[]{"", ""});
            }
        });

        // Clear all data in the table
        JButton clearData = new JButton(new AbstractAction("Clear table") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rowCount = tableModel.getRowCount();
                tableModel.setRowCount(0);
                tableModel.setRowCount(rowCount);
            }
        });

        // Changes features on the application depending on the method for importing data
        dataImportOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String option = (String) dataImportOption.getSelectedItem();

                // If import file is selected, enable browse file button and disable everything related to the table
                // Otherwise, do the opposite
                if (Objects.equals(option, "Import File")) {
                    clearData.doClick();
                    table.setGridColor(Color.WHITE);
                    table.clearSelection();
                    table.setEnabled(false);
                    addRow.setEnabled(false);
                    deleteRow.setEnabled(false);
                    pushDown.setEnabled(false);
                    clearData.setEnabled(false);
                    browseFileButton.setEnabled(true);
                } else {
                    table.setEnabled(true);
                    table.setGridColor(Color.BLACK);
                    addRow.setEnabled(true);
                    deleteRow.setEnabled(true);
                    pushDown.setEnabled(true);
                    clearData.setEnabled(true);
                    browseFileButton.setEnabled(false);
                }
            }
        });

        // Constraints settings
        constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        //Master panel
        JPanel masterPanel = new JPanel(new GridBagLayout());

        //Image Label
        JLabel imageLabel = new JLabel(new ImageIcon("src/main/java/Images/projectImage.png"));

        //Main Panel
        JPanel mainPanel = new JPanel(new GridBagLayout());

        //Input and Data Panel
        JPanel inputAndData = new JPanel(new GridLayout(1, 2));

        //Input Panel
        JPanel inputPanel = new JPanel(new GridBagLayout());

        //Data Panel
        JPanel dataPanel = new JPanel(new GridBagLayout());

        //Input Panel Design
        setConstaints(0, 0);
        inputPanel.add(dataImportLabel, constraints);

        setConstaints(1, 0);
        inputPanel.add(dataImportOption, constraints);

        setConstaints(0, 1);
        inputPanel.add(hLabel, constraints);

        setConstaints(1, 1);
        inputPanel.add(fieldForH, constraints);

        setConstaints(0, 2);
        inputPanel.add(nLabel, constraints);

        setConstaints(1, 2);
        inputPanel.add(fieldForN, constraints);

        setConstaints(0, 3);
        inputPanel.add(optionLabel, constraints);

        setConstaints(1, 3);
        inputPanel.add(option, constraints);

        setConstaints(0, 4);
        inputPanel.add(findAreaButton, constraints);

        setConstaints(1, 4);
        inputPanel.add(fieldForArea, constraints);

        setConstaints(0, 5);
        inputPanel.add(showGraph, constraints);

        //Data Panel Design
        tableModel = new DefaultTableModel(6, 2) {
            // Set column names
            @Override
            public String getColumnName(int index) {
                return (index == 0) ? "x" : "f(x)";
            }
        };
        table = new JTable(tableModel);
        table.setGridColor(Color.BLACK);
        table.setRowHeight(25);

        JScrollPane pane = new JScrollPane(table) {
            // Set pane dimensions
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(200, 170);
            }
        };

        setConstaints(0, 0);
        dataPanel.add(pane, constraints);

        // Stepper
        JPanel stepper = new JPanel(new GridLayout(4, 1));
        stepper.add(addRow);
        stepper.add(deleteRow);
        stepper.add(pushDown);
        stepper.add(clearData);
        setConstaints(1, 0);
        dataPanel.add(stepper, constraints);

        //Browse button and data field
        setConstaints(0, 1);
        dataPanel.add(browseFileButton, constraints);

        //Input and Data Panel Design
        inputAndData.add(inputPanel);
        inputAndData.add(dataPanel);

        //Main Panel Design
        constraints.fill = GridBagConstraints.CENTER;
        setConstaints(0, 0);
        mainPanel.add(title, constraints);

        constraints.ipady = 50;
        setConstaints(0, 1);
        mainPanel.add(inputAndData, constraints);

        //Master Panel Design
        masterPanel.add(imageLabel);
        masterPanel.add(mainPanel);

        // Frame settings
        frame.add(masterPanel);
        frame.setSize(new Dimension(1200, 525));
        frame.setBackground(Color.BLUE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new MainClass();
    }

    public void setConstaints(int x, int y) {
        constraints.gridx = x;
        constraints.gridy = y;
    }

    private double getH() throws InvalidNOrHException {
        String valueOfH = fieldForH.getText();

        // If H is non-positive or not a number, exception will be thrown
        try {
            double h = parseFractionalValue(valueOfH);
            if (h <= 0)
                throw new InvalidNOrHException();
            return h;
        } catch (Exception exception) {
            throw new InvalidNOrHException();
        }
    }

    private int getN() throws SimpsonsRuleException, InvalidNOrHException {
        String valueOfN = fieldForN.getText();
        double value;

        // If N is not an double, exception will be thrown
        try {
            value = parseFractionalValue(valueOfN);
        } catch (Exception exception) {
            throw new InvalidNOrHException();
        }

        // If decimal is entered, check if value can be interpreted as an integer
        if (Math.floor(value) != value)
            throw new InvalidNOrHException();

        int n = (int) Math.floor(value);

        // If N is non-positive or odd when Simpson's Rule is selected, error will be thrown
        if (n <= 0)
            throw new InvalidNOrHException();
        if (n % 2 == 1 && String.valueOf(option.getSelectedItem()).equals("Simpson's Rule"))
            throw new SimpsonsRuleException();

        return n;
    }

    private double parseFractionalValue(String value) {
        double h;

        // If value contains pi or a number followed by pi, parse the value to numbers
        if (value.contains("pi")) {
            Pattern pattern = Pattern.compile("[0-9.]+(?=pi)");
            Matcher matcher = pattern.matcher(value);

            // If there is text before pi, try to parse that text into a number and replace the entire value in the string
            // Otherwise, just replace pi with the pi constant
            if (matcher.find()) {
                double multiple = Double.parseDouble(matcher.group(0));
                value = value.replaceAll(".+pi", String.valueOf(multiple * Math.PI));
            }
            else
                value = value.replaceAll("pi", String.valueOf(Math.PI));
        }
        // If value contains '/', interpret the value as a fraction and try to obtain the quotient
        // Otherwise, simply parse the value
        if (value.contains("/") && StringUtils.countMatches(value, "/") == 1) {
            String[] num = value.split("/");
            double left = Double.parseDouble(num[0]);
            double right = Double.parseDouble(num[1]);

            // Division by 0 is illegal, throws an error
            if (right == 0)
                throw new NumberFormatException();

            h = left / right;
        }
        else
            h = Double.parseDouble(value);

        return h;
    }

    private void getData(int option) throws FileNotFoundException, FileFormatException, IllegalFileContentsException, IllegalTableContentsException, InsufficientDataException {
        if (option == 1)
            readTable();
        else
            readFile();
    }

    private void readTable() throws IllegalTableContentsException, InsufficientDataException {
        String num1, num2;
        for (int i = 0; i < table.getRowCount(); i++) {
            // Get the values of the first and second column for the ith row
            num1 = (String) table.getValueAt(i, 0);
            num2 = (String) table.getValueAt(i, 1);

            // Try to parse both values, throw an exception if not possible
            try {
                xValues.add(parseFractionalValue(num1));
                fxValues.add(parseFractionalValue(num2));
            } catch (Exception exception) {
                throw new IllegalTableContentsException();
            }
        }
        // Data set must contain at least 3 data points
        if (xValues.size() < 3)
            throw new InsufficientDataException();
    }

    private void readFile() throws FileNotFoundException, FileFormatException, IllegalFileContentsException, InsufficientDataException {
        File importedFile = fileChoose.getSelectedFile();

        // Get the file's extension
        String fileName = importedFile.getName();
        int periodIndex = fileName.lastIndexOf(".");
        String ext = fileName.substring(periodIndex + 1);

        // If the extension is not txt or csv, throw an error
        if (!ext.equals("txt") && !ext.equals("csv"))
            throw new FileFormatException();

        // Read the file contents
        Scanner fileScanner = new Scanner(importedFile);
        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();

            // Each row in file must contain one comma, throw an error otherwise
            if (StringUtils.countMatches(line, ",") != 1)
                throw new IllegalFileContentsException();

            // Try to get the numbers on the left and right side of the comma, throw an error if not possible
            String[] pair = line.split(",");
            try {
                xValues.add(parseFractionalValue(pair[0]));
                fxValues.add(parseFractionalValue(pair[1]));
            } catch (NumberFormatException exception) {
                throw new IllegalFileContentsException();
            }
        }
        // Data set must contain at least 3 data points
        if (xValues.size() < 3)
            throw new InsufficientDataException();
    }
}