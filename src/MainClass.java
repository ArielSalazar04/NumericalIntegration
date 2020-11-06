import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainClass implements ActionListener {
    // Java Swing Objects
    private final JFrame frame;
    private final JPanel panel;
    private final JLabel title;
    private final JLabel dataField;
    private final JButton submit;

    // Screen dimensions
    private final static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final static int height = screenSize.height;
    private final static int width = screenSize.width;

    public MainClass() {
        // Frame
        frame = new JFrame();
        title = new JLabel("Numerical Differentiation Calculator");
        submit = new JButton("Browse File");
        submit.addActionListener(this);
        dataField = new JLabel("Text");

        // Panel settings
        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(240, 225, 240, 225));
        panel.setLayout(new GridLayout(0, 1));
        panel.add(title);
        panel.add(submit);

        // Frame settings
        frame.add(panel, BorderLayout.CENTER);
        frame.setTitle("Numerical Differentiation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width/2, height/2);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.pack();
    }

    public static void main(String [] args){
        new MainClass();
    }

    @Override
    public void actionPerformed(ActionEvent e){
        // Opens File Explorer
        JFileChooser fileChoose = new JFileChooser();
        fileChoose.showOpenDialog(null);

        // If dataField has no parent, assign panel as parent
        if (dataField.getParent() == null)
            panel.add(dataField);

        // Display the name of the chosen text file
        String fileName = fileChoose.getSelectedFile().getName();
        dataField.setText(String.format("File: %s", fileName));
    }
}
