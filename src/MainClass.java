import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class MainClass {

    public MainClass(){
        // Frame
        JFrame frame = new JFrame();

        // Labels and buttons
        JLabel title = new JLabel("Numerical Differentiation Calculator");
        JButton submit = new JButton("Find Area");

        // Panel
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(240, 225, 240, 225));
        panel.setLayout(new GridLayout(0, 1));
        panel.add(title);
        panel.add(submit);

        // Initialize settings for frame
        frame.add(panel, BorderLayout.CENTER);
        frame.setTitle("Numerical Differentiation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    public static void main(String [] args){
        new MainClass();
    }
}
