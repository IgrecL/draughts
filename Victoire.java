import javax.swing.*;
import java.awt.*;

public class Victoire {
    JFrame frame;
    String texte;
       
    public Victoire(boolean vainqueur) {
		frame = new JFrame("Victoire");
		frame.setLayout(new GridLayout(2,1,0,0));
		
        if (vainqueur) {
			texte = "Victoire du joueur blanc.";
		} else {
			texte = "Victoire du joueur noir.";
		}
		
        frame.add(new JLabel("BRAVO !", JLabel.CENTER));        
        frame.add(new JLabel(texte, JLabel.CENTER));
		
		frame.pack();
        frame.setSize(200,100);
        frame.setResizable(false);
        frame.setVisible(true);
	}

}
