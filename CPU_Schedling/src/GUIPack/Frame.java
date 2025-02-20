package GUIPack;
import javax.swing.JFrame;

public class Frame{
    private JFrame frame;

    public Frame(Panel panel){
        frame = new JFrame("GUI Process");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setFocusable(true);
    }
}
