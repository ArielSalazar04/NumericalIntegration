import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.data.category.DefaultCategoryDataset;

public class MainClass{
    // Java Swing Objects
    private final JFrame frame;
    private final JPanel inputPanel;
    private final JComboBox dataImportOption;
    private final JTextField fieldForH;
    private final JTextField fieldForN;
    private final JTextField fieldForArea;
    private final JFileChooser fileChoose;
    private final JComboBox option;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JLabel dataField;

    // Screen dimensions
    private final static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final static int height = screenSize.height;
    private final static int width = screenSize.width;

    // Fonts
    private final Font titleFont = new Font("Serif", Font.BOLD, 32);
    private final Font labelFont = new Font("Serif", Font.PLAIN, 18);


    // Data
    private final ArrayList<Double> xValues = new ArrayList<>();
    private final ArrayList<Double> fxValues = new ArrayList<>();
    private ArrayList<Double> xInterpolatedValues;
    private ArrayList<Double> fxInterpolatedValues;

    public MainClass() {
        // Frame
        frame = new JFrame();
        JLabel title = new JLabel("Numerical Integration Calculator");
        title.setFont(titleFont);

        //Data importing selector object
        JLabel dataImportLabel = new JLabel("Import: ",SwingConstants.CENTER);
        dataImportLabel.setFont(labelFont);
        dataImportOption = new JComboBox<>(new String[]{"Import Data","Import File"});

        // H-value objects
        JLabel hLabel = new JLabel("Enter h: ", SwingConstants.CENTER);
        hLabel.setFont(labelFont);
        fieldForH = new JTextField(12);

        // N-value objects
        JLabel nLabel = new JLabel("Enter n: ", SwingConstants.CENTER);
        nLabel.setFont(labelFont);
        fieldForN = new JTextField(12);

        //Output Area
        fieldForArea = new JTextField(12);

        // Object for selecting integration method
        JLabel optionLabel = new JLabel("Option: ", SwingConstants.CENTER);
        optionLabel.setFont(labelFont);
        option = new JComboBox<>(new String[]{"Trapezoidal Rule", "Simpson's Rule"});

        // Object for importing file result field, and integration method objects
        fileChoose = new JFileChooser();

        // Object for results
        dataField = new JLabel("\n");
        dataField.setFont(labelFont);

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
                JFrame myFrame = new JFrame();
                myFrame.add(panelForChart);
                myFrame.setTitle("Function of X");
                myFrame.setSize(width / 2, height / 2);
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

                // If dataField has no parent, assign panel as parent
                if (dataField.getParent() == null)
                    inputPanel.add(dataField);

                // Display the name of the chosen text file
                File file = fileChoose.getSelectedFile();
                if (file == null) {
                    dataField.setText("");
                    showGraph.setEnabled(false);
                }
                else
                    dataField.setText(String.format("File: %s", file.getName()));
            }
        });
        browseFileButton.setEnabled(false);

        // Submit button ActionListener
        JButton findAreaButton = new JButton(new AbstractAction("Find Area") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int n = 0;
                double h=0, area;
                boolean validEntries = true;
                xValues.clear();
                fxValues.clear();

                try {
                    h = getH();
                    n = getN();
                    getData((Objects.equals(dataImportOption.getSelectedItem(), "Import Data")) ? 1 : 0);
                } catch (Exception exception) {
                    if (exception instanceof FileNotFoundException || exception instanceof NullPointerException)
                        JOptionPane.showMessageDialog(frame, "Missing file.");
                    else if (exception instanceof NumberFormatException)
                        JOptionPane.showMessageDialog(frame, "Table contains non-parsable data.");
                    else
                        JOptionPane.showMessageDialog(frame, exception.getMessage());
                    validEntries = false;
                }
                if (validEntries) {
                    boolean isTrap = String.valueOf(option.getSelectedItem()).charAt(0) == 'T';

                    // Lagrange Interpolation Object
                    LagrangeInterpolation obj = new LagrangeInterpolation(xValues, fxValues);

                    try {
                        area = obj.numericalIntegration(xValues, fxValues, n, h, isTrap);
                        xInterpolatedValues = obj.getxGraphValues();
                        fxInterpolatedValues = obj.getfxGraphValues();

                        // Display the area
                        fieldForArea.setText(String.valueOf(Math.round(area * 1e6) / 1e6));
                        showGraph.setEnabled(true);
                    } catch (IndexOutOfBoundsException exception) {
                        JOptionPane.showMessageDialog(frame, "ERROR: Please enter at least 3 data points.");
                    }
                } else
                    showGraph.setEnabled(false);

            }
        });

        JButton addRow = new JButton(new AbstractAction("+") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tableModel.getRowCount() <50)
                    tableModel.addRow(new String[]{"", ""});
                else
                    JOptionPane.showMessageDialog(frame, "You have exceeded the maximum rows (50 rows).");
            }
        });

        JButton deleteRow = new JButton(new AbstractAction("-") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tableModel.getRowCount() > 3)
                    tableModel.removeRow(tableModel.getRowCount()-1);
                else
                    JOptionPane.showMessageDialog(frame, "You must have at least 3 data points.");
            }
        });

        JButton clearData = new JButton(new AbstractAction("Clear table") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rowCount = tableModel.getRowCount();
                tableModel.setRowCount(0);
                tableModel.setRowCount(rowCount);
            }
        });

        dataImportOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String option = (String)dataImportOption.getSelectedItem();
                assert option != null;
                if (option.equals("Import File")){
                    table.setGridColor(Color.WHITE);
                    int rowCount = table.getRowCount();
                    tableModel.setRowCount(0);
                    tableModel.setRowCount(rowCount);
                    table.clearSelection();
                    table.setEnabled(false);
                    browseFileButton.setEnabled(true);
                    addRow.setEnabled(false);
                    deleteRow.setEnabled(false);
                    clearData.setEnabled(false);
                }
                else{
                    table.setEnabled(true);
                    table.setGridColor(Color.BLACK);
                    browseFileButton.setEnabled(false);
                    addRow.setEnabled(true);
                    deleteRow.setEnabled(true);
                    clearData.setEnabled(true);
                }
            }
        });

        inputPanel = new JPanel();

        // Constraints settings
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        //Master panel
        JPanel masterPanel = new JPanel(new GridBagLayout());

        //Image Label
        JLabel imageLabel = makeImageLabel("/Images/projectImage.jpeg");

        //Main Panel
        JPanel mainPanel = new JPanel(new GridBagLayout());

        //Input and Data Panel
        JPanel inputAndData = new JPanel(new GridLayout(1,2));

        //Input Panel
        JPanel inputPanel = new JPanel(new GridBagLayout());

        //Data Panel
        JPanel dataPanel = new JPanel(new GridBagLayout());

        //Input Panel Design
        constraints.gridx = 0;
        constraints.gridy = 0;
        inputPanel.add(dataImportLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        inputPanel.add(dataImportOption, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        inputPanel.add(hLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        inputPanel.add(fieldForH, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        inputPanel.add(nLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        inputPanel.add(fieldForN, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        inputPanel.add(optionLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 3;
        inputPanel.add(option, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        inputPanel.add(findAreaButton, constraints);

        constraints.gridx = 1;
        constraints.gridy = 4;
        inputPanel.add(fieldForArea, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        inputPanel.add(showGraph, constraints);

        //Data Panel Design
        tableModel = new DefaultTableModel(6, 2){
            @Override
            public String getColumnName(int index) {
                return (index == 0) ? "x" : "fx";
            }
        };
        table = new JTable(tableModel);
        table.setGridColor(Color.BLACK);
        table.setRowHeight(20);

        JScrollPane pane = new JScrollPane(table) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(175, 140);
            }
        };

        constraints.gridx = 0;
        constraints.gridy = 0;
        dataPanel.add(pane, constraints);

        // Stepper
        JPanel stepper = new JPanel(new GridLayout(3, 1));
        stepper.add(addRow);
        stepper.add(deleteRow);
        stepper.add(clearData);
        constraints.gridx = 1;
        constraints.gridy = 0;
        dataPanel.add(stepper, constraints);

       //Browse button and data field
        constraints.gridx = 0;
        constraints.gridy = 1;
        dataPanel.add(browseFileButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        dataPanel.add(dataField, constraints);

        //Input and Data Panel Design
        inputAndData.add(inputPanel);
        inputAndData.add(dataPanel);

        //Main Panel Design
        constraints.fill = GridBagConstraints.CENTER;
        constraints.gridx = 0;
        constraints.gridy = 0;
        mainPanel.add(title, constraints);

        constraints.ipady = 50;
        constraints.gridx = 0;
        constraints.gridy = 1;
        mainPanel.add(inputAndData, constraints);

        //Master Panel Design
        masterPanel.add(imageLabel);
        masterPanel.add(mainPanel);

        // Frame settings
        frame.add(masterPanel);
        frame.setTitle("Numerical Integration Calculator");
        frame.setSize(new Dimension(900,400));
        frame.setBackground(Color.BLUE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

    }

    public static void main(String [] args){
        new MainClass();
    }

    public boolean containsOneLetter(String line,char letter){
        int count  = 0;
        for(int i = 0; i < line.length(); i++)
        {
            count = (line.charAt(i)== letter) ? count+1:count;
        }
        return count == 1;

    }
    private JLabel makeImageLabel(String path){
        JLabel imageLabel = new JLabel();
        URL image = getClass().getResource(path);
        ImageIcon imageIcon = new ImageIcon(image);
        imageLabel.setIcon(imageIcon);
        return imageLabel;
    }
    private double getH() throws InvalidNOrHException {
        String valueOfH = fieldForH.getText();
        try {
            double h =  parseFractionalValue(valueOfH);
            if(h<=0)
            {
                throw new InvalidNOrHException();
            }
            return h;
        } catch (Exception exception){
            throw new InvalidNOrHException();
        }
    }
    private int getN() throws SimpsonsRuleException, InvalidNOrHException {
        String valueOfN = fieldForN.getText();
        double value;

        try {
            value = parseFractionalValue(valueOfN);
        } catch (Exception exception){
            throw new InvalidNOrHException();
        }

        if (Math.floor(value) != value)
            throw new InvalidNOrHException();

        int n = (int) Math.floor(value);

        if (n <= 0)
            throw new InvalidNOrHException();
        if (n % 2 == 1 && String.valueOf(option.getSelectedItem()).equals("Simpson's Rule"))
            throw new SimpsonsRuleException();

        return n;
    }
    private double parseFractionalValue(String value){
        double h;
        if (value.contains("pi") || value.contains("Pi") || value.contains("PI")) {
            value = value.replace("pi", String.valueOf(Math.PI));
            value = value.replace("Pi", String.valueOf(Math.PI));
            value = value.replace("PI", String.valueOf(Math.PI));
        }
        if (value.contains("/") && containsOneLetter(value, '/')) {
            String[] num = value.split("/");
            double left = Double.parseDouble(num[0]);
            double right = Double.parseDouble(num[1]);

            if (right == 0)
                throw new NumberFormatException();

            h = left/right;
        }
        else
            h = Double.parseDouble(value);

        return h;
    }
    private void getData(int option) throws FileNotFoundException, FileFormatException, IllegalFileContentsException,IllegalTableContentsException {
        if (option == 1)
            readTable();
        else
            readFile();
    }
    private void readTable() throws IllegalTableContentsException {
        String num1, num2;
        for (int i = 0; i < table.getRowCount(); i++){
            num1 = (String) table.getValueAt(i, 0);
            num2 = (String) table.getValueAt(i, 1);
            try {
                xValues.add(parseFractionalValue(num1));
                fxValues.add(parseFractionalValue(num2));
            }
            catch(Exception exception){
                throw new IllegalTableContentsException();
            }
        }
    }
    private void readFile() throws FileNotFoundException, FileFormatException, IllegalFileContentsException {
        File importedFile = fileChoose.getSelectedFile();

        String fileName = importedFile.getName();
        int periodIndex = fileName.lastIndexOf(".");
        String ext = fileName.substring(periodIndex + 1);

        if (!ext.equals("txt") && !ext.equals("csv"))
            throw new FileFormatException();

        Scanner fileScanner = new Scanner(importedFile);

        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();

            if (!containsOneLetter(line, ','))
                throw new FileFormatException();

            String[] pair = line.split(",");
            try{
                xValues.add(parseFractionalValue(pair[0]));
                fxValues.add(parseFractionalValue(pair[1]));
            } catch (NumberFormatException exception){
                throw new IllegalFileContentsException();
            }
        }
    }

}

