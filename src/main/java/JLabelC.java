import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;

public class JLabelC extends JLabel {
    public JLabelC (String text, boolean isTitle){
        super(text, SwingConstants.CENTER);
        this.setFont((isTitle) ? new Font("Serif", Font.BOLD, 36) : new Font("Serif", Font.PLAIN, 20));
    }
}
