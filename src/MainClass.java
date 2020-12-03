import javax.swing.*;
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

        //Data importing selector object
        JLabel dataImportLabel = new JLabel("Import: ",SwingConstants.CENTER);
        dataImportLabel.setFont(labelFont);
        dataImportOption = new JComboBox<>(new String[]{"Import Data","Import File"});

        // H-value objects
        JLabel hLabel = new JLabel("Enter a value for h: ", SwingConstants.CENTER);
        hLabel.setFont(labelFont);
        fieldForH = new JTextField(12);

        // N-value objects
        JLabel nLabel = new JLabel("Enter a value for n: ", SwingConstants.CENTER);
        nLabel.setFont(labelFont);
        fieldForN = new JTextField(12);

        //Output Area
        fieldForArea = new JTextField(12);

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
        browseFileButton.setEnabled(false);

        // Submit button ActionListener
        JButton findAreaButton = new JButton(new AbstractAction("Find Area") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fieldForH.getText().equals(""))
                    JOptionPane.showMessageDialog(frame, "Missing value for H");
                else if (fieldForN.getText().equals(""))
                    JOptionPane.showMessageDialog(frame, "Missing value for N");
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

                        if(ext.equals("txt") || ext.equals("csv"))
                        {
                            try {
                                // Read data from text file into ArrayLists
                                Scanner fileScanner = new Scanner(importedFile);
                                boolean flag = true;

                                while (fileScanner.hasNextLine())
                                {
                                    String line = fileScanner.nextLine();
                                    if(!containsOneLetter(line,','))
                                    {
                                        flag = false;
                                        JOptionPane.showMessageDialog(frame, "Invalid formatting, each row must contain one comma");

                                        break;
                                    }

                                    String[] pair = line.split(",");
                                    try {
                                        xValues.add(Double.parseDouble(pair[0]));
                                        fxValues.add(Double.parseDouble(pair[1]));

                                    } catch(NumberFormatException n)
                                    {
                                        flag = false;
                                        JOptionPane.showMessageDialog(frame, "Invalid formatting, one line contains a non-parsible number");
                                        break;
                                    }
                                }
                                int n = 0;
                                double h = 0;

                                try{
                                    // Obtain text from input fields and compute area
                                     n = Integer.parseInt(fieldForN.getText());
                                     String valueOfH=fieldForH.getText();
                                     if(valueOfH.contains("pi")||valueOfH.contains("Pi")||valueOfH.contains("PI"))
                                     {
                                         valueOfH=valueOfH.replace("pi",String.valueOf(Math.PI));
                                         valueOfH=valueOfH.replace("Pi",String.valueOf(Math.PI));
                                         valueOfH=valueOfH.replace("PI",String.valueOf(Math.PI));
                                     }

                                     if(valueOfH.contains("/")&&containsOneLetter(valueOfH,'/'))
                                     {
                                         String num[] = valueOfH.split("/");
                                         try
                                         {
                                             double left = Double.parseDouble(num[0]);
                                             double right = Double.parseDouble(num[1]);
                                             if(right == 0 )
                                             {
                                                 throw new ArithmeticException();
                                             }
                                              h = left/right;
                                         }
                                         catch(Exception exp) { }
                                     }
                                     else
                                     {
                                         h = Double.parseDouble(valueOfH);
                                     }
                                    if(n<=0 || h<=0 ){
                                        throw new NumberFormatException();
                                    }
                                }
                                catch(NumberFormatException exp)
                                {
                                    flag = false;
                                    JOptionPane.showMessageDialog(frame, "Invalid formatting, " +
                                            "N must be an integer, H must be a decimal or a valid fraction," +
                                            " both must be positive");
                                }

                                if(n % 2 == 1 && String.valueOf(option.getSelectedItem()).equals("Simpson's Rule")){
                                    flag = false;
                                    JOptionPane.showMessageDialog(frame, "N must be even when applying Simpson's Rule.");
                                }

                                if(flag)
                                {
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
//                                    myFrame.pack();
                                    myFrame.setTitle("Function of X");
                                    myFrame.setSize(width / 2, height / 2);
                                    myFrame.setLocationRelativeTo(null);
//                                    myFrame.setResizable(false);
                                    myFrame.setVisible(true);
                                }

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

        dataImportOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String option = (String)dataImportOption.getSelectedItem();
                assert option != null;
                if (option.substring(7).equals("File")){
                    table.setEnabled(false);
                    browseFileButton.setEnabled(true);
                    table.setGridColor(Color.WHITE);
                    table.clearSelection();
                }
                else{
                    table.setEnabled(true);
                    browseFileButton.setEnabled(false);
                    table.setGridColor(Color.BLACK);
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

        //Data Panel Design
        table = new JTable(5,2);
        table.setGridColor(Color.BLACK);
        table.setRowHeight(20);
        constraints.gridx = 0;
        constraints.gridy = 0;
        dataPanel.add(table,constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        dataPanel.add(browseFileButton, constraints);

        //Input and Data Panel Design
        inputAndData.add(inputPanel);
        inputAndData.add(dataPanel);

        //Main Panel Design
        constraints.fill = GridBagConstraints.CENTER;
        constraints.gridx = 0;
        constraints.gridy = 0;
        mainPanel.add(title, constraints);

        constraints.ipady = 100;
        constraints.gridx = 0;
        constraints.gridy = 1;
        mainPanel.add(inputAndData, constraints);

        //Master Panel Design
        masterPanel.add(imageLabel);
        masterPanel.add(mainPanel);

        // Frame settings
        frame.add(masterPanel);
        frame.setTitle("Numerical Integration Calculator");
        frame.setSize(new Dimension(840,390));
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

}

