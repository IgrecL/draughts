import javax.swing.*;
import java.awt.*;

public class Pion {
    int x; //coordonnées x du pion (entre 0 et 9)
    int y; //coordonnées y du pion (entre 0 et 9)
    boolean couleur; // couleur du pion (quelle équipe) false = noir ; true = blanc
    boolean dame; // false = pion ; true = dame

    public Pion(int y, int x, boolean c) {
        this.x = x;
        this.y = y;
        couleur = c;
        dame = false;
    }
    
    public void setxy(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	//déplacement d'un pion vers une autre case
    public void deplacement(Pion[][] plateau, int Xf, int Yf) {
		Pion pionCopie = new Pion(Yf, Xf, couleur); //crée un pion copie avec les mêmes attributs que le pion du tableau qu'on déplace
		plateau[Yf][Xf] = pionCopie; //place une copie du pion copie dans la case d'arrivée
		plateau[y][x] = null;
		plateau[Yf][Xf].setxy(Xf, Yf);	
	}

	//teste si le pion peut manger dans une des quatre directions
    public boolean peutManger(Affichage affichage, Pion[][] plateau){
        if ((x>1)&&(y>1)) { //en haut à gauche
            if (voisin(plateau, 1) && sautPossible(affichage, plateau, 1)){
                return true;
            }
        }
        if ((x<8)&&(y>1)) { //en haut à droite
            if (voisin(plateau, 2) && sautPossible(affichage, plateau, 2)){
                return true;
            }
        }
        if ((x<8)&&(y<8)) { //en bas à droite
            if (voisin(plateau, 3) && sautPossible(affichage, plateau, 3)) {
                return true;
            }
        }
        if ((x>1)&& (y<8)) { //en bas à gauche
            if (voisin(plateau, 4)&& sautPossible(affichage, plateau, 4)){
                return true;
            }
        }
        return false;  
    }

	//teste si le pion peut manger dans la direction donnée
    public boolean voisin(Pion[][] plateau, int direction) { 
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
        if (plateau[y+deltay][x+deltax]!=null) {
            if (plateau[y+deltay][x+deltax].couleur != this.couleur) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

	//teste si le saut est possible dans une direction donnée (si voisin est vrai)
    public boolean sautPossible(Affichage affichage, Pion[][] plateau, int direction) {
        int deltax = 0;
        int deltay = 0;
        switch(direction) {
            case 1: //en haut à gauche
                deltax = -2;
                deltay = -2;
                break;
            case 2: //en haut à droite
                deltax = 2;
                deltay = -2;
                break;
            case 3: //en bas à gauche
                deltax = 2;
                deltay = 2;
                break;
            case 4: //en bas à droite
                deltax = -2;
                deltay = 2;
                break;
        }
        if (plateau[y+deltay][x+deltax]==null) { //vérifie s'il y a un pion derriere la cible
			affichage.Boutons[y+deltay][x+deltax].setBackground(Color.CYAN);
            return true;
        } else {
            return false;
        }
    }

	//teste si le pion peut bouger dans une des quatre directions
    public boolean peutBouger(Pion[][] plateau, boolean joueur) { 
        if (joueur) {
			if ((x>0)&&(y>0)) { //cas 1 : en haut à gauche (blanc) & en bas à gauche (noir)
				if (peutBougerDirection(plateau, 1, joueur)){
					return true;
				}
			}
			if ((x<9)&&(y>0)) { //cas 2 : en haut à droite (blanc) & en bas à droite (noir)		
				if (peutBougerDirection(plateau, 2, joueur)){				
					return true;
				} 
			}
			return false;
		} else {
			if ((x>0)&&(y<9)) { //cas 1 : en haut à gauche (blanc) & en bas à gauche (noir)
				if (peutBougerDirection(plateau, 1, joueur)){
					return true;
				}
			}
			if ((x<9)&&(y<9)) { //cas 2 : en haut à droite (blanc) & en bas à droite (noir)
				if (peutBougerDirection(plateau, 2, joueur)){
					return true;
				} 
			}
			return false;
		}
	}				

	//teste si le puion peut bouger dans la direction donnée
    public boolean peutBougerDirection (Pion[][] plateau, int direction, boolean joueur) {
        int deltax = 0;
        int deltay = 0;
        switch(direction) {
            case 1: //en haut à gauche (blanc) & en bas à gauche (noir)
                deltax = -1;
                break;
            case 2: //en haut à droite (blanc) & en bas à droite (noir)
                deltax = 1;
                break;
        }
        if (joueur) { //si le pion est blanc, il bouge en avant
			deltay = -1;
		} else { //si le pion est noir, il bouge en arrière
			deltay = 1;
		}
        if (plateau[y+deltay][x+deltax] == null) {
            return true;
        } else {
            return false;
        }
    }
    
    //colorie en bleu les cases d'arrivée possibles après l'action 'manger' pour les pions
	public void choixMange(Affichage affichage, Pion[][] plateau, boolean joueur) {
		for (int i=0 ; i<10 ; i++) {
			for (int j=0 ; j<10 ; j++) {
				if (plateau[(y+j)/2][(x+i)/2] != null) {
					if ((Math.abs(x-i) == 2)&&(Math.abs(y-j) == 2)&&(plateau[j][i] == null)&&(plateau[(y+j)/2][(x+i)/2].couleur != joueur)) {
						affichage.Boutons[j][i].setBackground(Color.CYAN);
					}
				}	
			}
		}			
	}
    
    //colorie les cases d'arrivée possibles après l'action 'avancer' pour le pion
	public void choixAvance(Affichage affichage, Pion[][] plateau, boolean joueur) {
		for (int i=0 ; i<10 ; i++) {
			for (int j=0 ; j<10 ; j++) {
				if (joueur) {
					if ((Math.abs(x-i) == 1)&&(y-j == 1)&&(plateau[j][i] == null)) {
						affichage.Boutons[j][i].setBackground(Color.CYAN);
					}
				} else {
					if ((Math.abs(x-i) == 1)&&(j-y == 1)&&(plateau[j][i] == null)) {
						affichage.Boutons[j][i].setBackground(Color.CYAN);
					}
				}	
			}
		}			
	}
	
	//cette méthode n'est pas utile pour le pion, mais est nécessaire pour la dame, qui hérite de la class pion
    public boolean peutMangerDirection(Affichage affichage, Pion[][] plateau, int direction) {
        return true;
    }
      
    //cette méthode n'est pas utile pour le pion, mais est nécessaire pour la dame, qui hérite de la class pion
	public int compteurPionsDiagonale(Pion[][] plateau, int Xf, int Yf, int diagonale) {
		return 0;
    }	
	
}
