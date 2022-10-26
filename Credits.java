import javax.swing.*;
import java.awt.*;

public class Credits {
    JFrame frame;
       
    public Credits() {
		frame = new JFrame("Jeu de Dames");
		frame.setLayout(new GridLayout(6,1,0,0));
		
        frame.add(new JLabel("Jeu de Dames", JLabel.CENTER));
        frame.add(new JLabel("Projet d'algorithmique & programmation", JLabel.CENTER));
        frame.add(new JLabel(" ", JLabel.CENTER));
		frame.add(new JLabel("LARBRE Yohan", JLabel.CENTER));
		frame.add(new JLabel("CESANA Filippo", JLabel.CENTER));
		frame.add(new JLabel("TSHINKENKE Elsa", JLabel.CENTER));
		
		frame.pack();
        frame.setSize(270,150);
        frame.setResizable(false);
        frame.setVisible(true);
	}

}
