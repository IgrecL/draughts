import javax.swing.*;
import java.awt.*;

public class Dame extends Pion {
    public Dame(int y, int x, boolean c) {
        super(y, x, c);
        dame = true;
    }
    
    //déplacement d'une dame vers une autre case
    public void deplacement(Pion[][] plateau, int Xf, int Yf) {
		Dame pionCopie = new Dame(Yf, Xf, couleur); //crée un pion copie avec les mêmes attributs que le pion du tableau qu'on déplace
		plateau[Yf][Xf] = pionCopie; //place une copie du pion copie dans la case d'arrivée
		plateau[y][x] = null;
		plateau[Yf][Xf].setxy(Xf, Yf);	
	}
	
	//teste si la dame peut manger dans une des quatre directions
    public boolean peutManger(Affichage affichage, Pion[][] plateau){ 
        for (int i = 1; i<5; i++) {
            if (peutMangerDirection(affichage, plateau, i)) {
                return true;
            } 
        }
        return false;
    }
	
	//teste si la dame peut manger dans la direction donnée
    public boolean peutMangerDirection(Affichage affichage, Pion[][] plateau, int direction) { 
        boolean joueur = couleur;
        int deltax = 0;
        int deltay = 0;
        switch(direction) {
            case 1: //en haut à gauche
                deltax = -1;
                deltay = -1;
                break;
            case 2: //en haut à droite
                deltax = 1;
                deltay = -1;
                break;
            case 3: //en bas à droite
                deltax = 1;
                deltay = 1;
                break;
            case 4: //en bas à gauche
                deltax = -1;
                deltay = 1;
                break;
        }
        while ((x+deltax>=0)&&(x+deltax<10)&&(y+deltay>=0)&&(y+deltay<10)) {
            if (plateau[y+deltay][x+deltax]==null) {
                if (deltax>0) { deltax++; }
                else { deltax--; }
                if (deltay>0) { deltay++; }
                else { deltay--; }
            } else if (plateau[y+deltay][x+deltax].couleur == joueur) { //s'il rencontre une case blanche, arrêter d'avancer sur la diagonale
                return false;
            } else { // regarde la case derriere le pion
                if (deltax>0) { deltax++; }
                else { deltax--; }
                if (deltay>0) { deltay++; }
                else { deltay--; }
                if (((x+deltax>=0)&&(x+deltax<10)&&(y+deltay>=0)&&(y+deltay<10))&&(plateau[y+deltay][x+deltax]==null)) { // case vide
					while ((x+deltax>=0)&&(x+deltax<10)&&(y+deltay>=0)&&(y+deltay<10)) {
						affichage.Boutons[y+deltay][x+deltax].setBackground(Color.CYAN);
						if (deltax>0) { deltax++; }
						else { deltax--; }
						if (deltay>0) { deltay++; }
						else { deltay--; }
						if (((x+deltax>=0)&&(x+deltax<10)&&(y+deltay>=0)&&(y+deltay<10))&&(plateau[y+deltay][x+deltax] != null)) {
							deltax = 10;
						}
					}
                    return true;
                } else {
                    return false;
                }
            }   
        }
        return false;
    }

	//teste si la dame peut bouger dans une des quatre directions
    public boolean peutBouger(Pion[][] plateau, boolean joueur) { 
		for (int i = 1 ; i < 5 ; i++) {
            if (peutBougerDirection(plateau, joueur, i)) {
                return true;
            }
        }
		return false;
	}	
	
	//teste si la dame peut bouger dans la direction donnée
    public boolean peutBougerDirection (Pion[][] plateau, boolean joueur, int direction) {
        int deltax = 0;
        int deltay = 0;
        switch(direction) {
            case 1: //en haut à gauche (blanc) & en bas à gauche (noir)
                deltax = -1;
                deltay = -1;
                break;
            case 2: //en haut à droite (blanc) & en bas à droite (noir)
                deltax = 1;
                deltay = -1;
                break;
            case 3:
                deltax = 1;
                deltay = 1;
                break;
            case 4:
                deltax = -1;
                deltay = 1;
                break;          
        }
        if ((x+deltax>=0)&&(x+deltax<10)&&(y+deltay>=0)&&(y+deltay<10)) {
			if (plateau[y+deltay][x+deltax]==null) {
				return true;
			} else {
				return false;
			}
        } else {
            return false;
        }
    }
	
    //renvoie le nombre de pions entre la dame et la case sélectionnée
	public int compteurPionsDiagonale(Pion[][] plateau, int Xf, int Yf, int diagonale) {
        int compteur = 0;
        int j = y;
        switch(diagonale) {
            case 1: //en haut à gauche
                for (int i = x; i > Xf; i--) {
					if ((i>=0)&&(i<10)&&(j>=0)&&(j<10)) {
						if (plateau[j][i] != null) {
							compteur++;
						}
						j--;
					} 
                }
                break;
            case 2: //en haut à droite
                for (int i = x; i < Xf; i++) {
                    if ((i>=0)&&(i<10)&&(j>=0)&&(j<10)) {
						if (plateau[j][i] != null) {
							compteur++;
						}
						j--;
					}
                }
                break;
            case 3: //en bas à droite
                for (int i = x; i < Xf; i++) {
                    if ((i>=0)&&(i<10)&&(j>=0)&&(j<10)) {
						if (plateau[j][i] != null) {
							compteur++;
						}
						j++;
					}
                }
                break;
            case 4: //en bas à gauche
                for (int i = x; i > Xf; i--) {
                    if ((i>=0)&&(i<10)&&(j>=0)&&(j<10)) {
						if (plateau[j][i] != null) {
							compteur++;
						}
						j++; 
					}                   
                }
                break;
        }
        return (compteur-1); //on return -1 car le compteur compte avec la dame incluse
    }	
    
	//colorie les cases d'arrivée possibles après l'action 'avancer' pour la dame
	public void choixAvance(Affichage affichage, Pion[][] plateau, boolean joueur) {
		int deltax = 0;
		int deltay = 0;
		if (plateau[y][x].dame) {
			for (int k = 1; k < 5; k++) {
				switch (k) {
					case 1: 
						deltax = -1;
						deltay = -1;
						break;
					case 2:
						deltax = 1;
						deltay = -1;
						break;
					case 3:
						deltax = 1;
						deltay = 1;
						break;
					case 4:
						deltax = -1;
						deltay = 1;
						break;
				}
				while ((x+deltax>=0)&&(x+deltax<10)&&(y+deltay>=0)&&(y+deltay<10)) {
					if (plateau[y+deltay][x+deltax] == null) {
						affichage.Boutons[y+deltay][x+deltax].setBackground(Color.CYAN);
						if (deltax > 0) { deltax++; }
						else { deltax--; }
						if (deltay > 0) { deltay++; }
						else { deltay--; }
					} else {
						break;
					}
				}
			}
		}			
	}
    
}
