import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Affichage {
    JFrame frame;
    JMenuBar mb; //menu
    ImageIcon noir; ImageIcon blanc; ImageIcon noirDame; ImageIcon blancDame; //images des pions
    JButton[][] Boutons; //tableau de boutons
    Pion[][] plateau; //état du plateau
	int inputX = -1;
	int inputY = -1;
       
    public Affichage(Reglages reglages) {	
		frame = new JFrame("Jeu de Dames");
        		
		//création de la barre de menu et des quatre menus
		mb = new JMenuBar(); frame.setJMenuBar(mb);
		creerMenuReglages(reglages);
		creerMenuReset(reglages);
		creerMenuSeRendre(reglages);
		creerMenuCredits(reglages);	
		
		defIcones(true); //définition des images pour les jetons (de base, le joueur 1 joue les blancs -> true)
		creerBoutons(); //création des 100 boutons
		
		frame.setLayout(new GridLayout(10,10,0,0));
		frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700,700);
        frame.setResizable(false);
        frame.setVisible(true);	
	}

	//change le plateau du main
	public void setPlateau(Pion[][] plateau) {
		plateau = this.plateau;
	}

	//change les valeurs de inputX et inputY (utile dans les boutons)
	public void setXY(int newX, int newY) { 
		inputY = newY; inputX = newX;
	}

	//crée le menu "Reglages"
	public void creerMenuReglages(Reglages reglages) {
		JMenu settings = new JMenu();
		settings.setText("Reglages");
		mb.add(settings);
		
		//ensemble de deux boutons liés qui permettent de choisir avec quelle couleur joue le joueur 1
		ButtonGroup bg = new ButtonGroup();
		JRadioButtonMenuItem J1Blanc = new JRadioButtonMenuItem("J1 joue les blancs"); bg.add(J1Blanc); settings.add(J1Blanc);
		JRadioButtonMenuItem J1Noir = new JRadioButtonMenuItem("J1 joue les noirs"); bg.add(J1Noir); settings.add(J1Noir);
		J1Blanc.setSelected(true);			
		
		//action du bouton "J1 joue les noirs"
		J1Noir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				defIcones(false); //on définit les icones pour que le J1 soit noir
				update(plateau); //actualise l'affichage			
			}
		});
		
		//action du bouton "J1 joue les blancs"
		J1Blanc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				defIcones(true); //on définit les icones pour que le J1 soit blanc
				update(plateau); //actualise l'affichage
			}
		});	
		
		//bouton qui permet d'activer ou de désactiver l'autoplay
		JMenuItem boutonAutoplay = new JMenuItem();
		boutonAutoplay.setText("Desactiver le mode automatique");
		settings.add(boutonAutoplay);
		
		//action du bouton "Desactiver le mode automatique"
		boutonAutoplay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (reglages.autoplay) {
					reglages.setAutoplay(false);
					boutonAutoplay.setText("Activer le mode automatique");
				} else {
					reglages.setAutoplay(true);
					boutonAutoplay.setText("Desactiver le mode automatique");					
				}
			}
		});			
		
		//bouton qui permet d'activer le mode IA (coups aléatoires du joueur noir)
		JMenuItem boutonIA = new JMenuItem();
		boutonIA.setText("Activer le mode IA pour le joueur noir");
		settings.add(boutonIA);
		
		//action du bouton "Activer le mode IA pour le joueur noir"
		boutonIA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (reglages.autoplay) {
					reglages.setIA(true);
					settings.remove(boutonIA);
				} 
			}
		});				
	}

	//crée le menu "Reset"
	public void creerMenuReset(Reglages reglages) {
		JMenu reset = new JMenu();
		reset.setText("Reset");
		mb.add(reset);
		
		//création du bouton reset
		JMenuItem boutonReset = new JMenuItem();
		boutonReset.setText("Recommencer la partie");
		reset.add(boutonReset);
		
		//action du bouton reset
		boutonReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reglages.recommencer = true;		
			}
		});						
	}

	//crée le menu "Se rendre"
	public void creerMenuSeRendre(Reglages reglages) {
		JMenu seRendre = new JMenu();
		seRendre.setText("Se rendre");
		mb.add(seRendre);
		
		//création du bouton "Les blancs se rendent"
		JMenuItem boutonBlancSeRend = new JMenuItem();
		boutonBlancSeRend.setText("Les blancs se rendent");
		seRendre.add(boutonBlancSeRend);
		boutonBlancSeRend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reglages.seRend = 1;	
				mb.remove(seRendre);	
			}
		});		
		
		//création du bouton "Les noirs se rendent"
		JMenuItem boutonNoirSeRend = new JMenuItem();
		boutonNoirSeRend.setText("Les noirs se rendent");
		seRendre.add(boutonNoirSeRend);		
		boutonNoirSeRend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reglages.seRend = 2;	
				mb.remove(seRendre);		
			}
		});	
	}

	//crée le menu "Credits"
	public void creerMenuCredits(Reglages reglages) {
		JMenu credits = new JMenu();
		credits.setText("Credits");
		mb.add(credits);
		
		//création du bouton crédits
		JMenuItem boutonCredits = new JMenuItem();
		boutonCredits.setText("Afficher les credits");
		credits.add(boutonCredits);

		//action du bouton "Afficher les crédits"
		boutonCredits.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Credits frameCredits = new Credits();		
			}
		});	
	}

	//crée toutes les icones (à la bonne taille pour tenir dans les cases)
	public void defIcones(boolean couleurJ1) {
		if (couleurJ1) {
			//crée et resize chaque image
			noir = new ImageIcon(new ImageIcon("Icones/noir.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
			blanc = new ImageIcon(new ImageIcon("Icones/blanc.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
			noirDame = new ImageIcon(new ImageIcon("Icones/noir dame.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));		
			blancDame = new ImageIcon(new ImageIcon("Icones/blanc dame.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
		} else {
			noir = new ImageIcon(new ImageIcon("Icones/blanc.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
			blanc = new ImageIcon(new ImageIcon("Icones/noir.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
			noirDame = new ImageIcon(new ImageIcon("Icones/blanc dame.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));		
			blancDame = new ImageIcon(new ImageIcon("Icones/noir dame.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
		}
	}

	//crée les 100 boutons qui correspondent aux cases du plateau
	public void creerBoutons() {
        Boutons = new JButton[10][10]; //crée un tableau 10×10 de Jbutton
        for (int i = 0; i<10; i++) {
			for (int j = 0; j<10; j++) {
				Boutons[i][j] = new JButton(); //on définit les Jbutton de chaque slot
				Boutons[i][j].setBackground(Color.WHITE); //on met un fond blanc au bouton
				frame.add(Boutons[i][j]); //on ajoute le bouton à la frame
				
				int x = j;
				int y = i;
				
				Boutons[i][j].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setXY(x,y);
					}
				});	
				
			}
		}	
	}

	//met à jour l'affichage du tableau de pions, en modifiant les icones des boutons
	public void update(Pion[][] p) {
		plateau = p;
        for (int i = 0; i<10; i++) {
			for (int j = 0; j<10; j++) {
				if (plateau[i][j]!=null) {
					if (!plateau[i][j].couleur)	{
						if (!plateau[i][j].dame) {
                            Boutons[i][j].setIcon(noir);
                        } else {
                            Boutons[i][j].setIcon(noirDame);
                        }
					} else if (plateau[i][j].couleur) {
                        if (!plateau[i][j].dame) {
                            Boutons[i][j].setIcon(blanc);
                        } else {
                            Boutons[i][j].setIcon(blancDame);
                        }
					}
				} else {
					Boutons[i][j].setIcon(null);
				}
			}
		}
	}
	
	//remet toutes les cases en blanc
	public void clean() {
		for (int i = 0; i<10; i++) {
			for (int j = 0; j<10; j++) {
				Boutons[i][j].setBackground(Color.WHITE);
			}
		}
	}
	
	//IA (coups aléatoires pour le joueur adverse)
	public void IA() {
		setXY((int) (10*Math.random()), (int) (10*Math.random()));
	}
			
	
}
