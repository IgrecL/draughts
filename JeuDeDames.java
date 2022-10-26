import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.TimeUnit;

public class JeuDeDames {
    
    public static void main (String[] args) throws InterruptedException {
        // création du plateau (tableau à double entrée de pions), qu'on remplit via initiation(plateau)
        Pion plateau[][] = new Pion[10][10]; 
		initialisation(plateau);

		//met les réglages par défaut du jeu (modifiables dans le menu) et affiche le plateau
		Reglages reglages = new Reglages();
		Affichage affichage = new Affichage(reglages);
		affichage.update(plateau);
		
		//variables pour la boucle
		boolean fini = false; //si la partie est finie
		boolean joueurActuel = true; //quel joueur est en train de jouer (true = blanc, joueur 1 | false = noir, joueur 2)
		
		//boucle du jeu
		while (!fini) {
			tour(affichage, reglages, joueurActuel, plateau); //tour du joueur
            joueurActuel = !joueurActuel; //on change de joueur pour le tour suivant
            
            //si le joueur adverse ne peut plus jouer, la partie est finie
			if (aucuneActionPossible(affichage, joueurActuel, plateau)) {
				fini = true;
			} 
			
			//si l'utilisateur appuie sur le bouton 'recommencer', on remet à zéro la partie
			if (reglages.recommencer) { 
				plateau = new Pion[10][10];
				affichage = relancer(affichage, reglages, plateau);
				joueurActuel = true;
			}
			
			//si un des deux joueurs se rend
			if (reglages.seRend != 0) { 
				if (reglages.seRend == 1) {
					joueurActuel = true;
				}
				if (reglages.seRend == 2) {
					joueurActuel = false;
				}
				fini = true;
			}
		}
		Victoire victoire = new Victoire(!joueurActuel); //fenêtre de victoire         
    }

	//place les pions sur le plateau
    public static void initialisation(Pion[][] plateau) {
        for (int i=0; i<10; i++) {           
            for (int j=i%2; j<10; j+=2){
                if (i<4) {                
					plateau[i][j] = new Pion(i,j,false); //crée un pion noir aux coordonnés (i,j)
				}
				if (i>5) {
					plateau[i][j] = new Pion(i,j,true); //crée un pion blanc aux coordonnés (i,j)
				}
            }
        }
    }

	//parcourt tout le tableau selon la couleur du joueur et renvoie le nombre de pions qui peuvent manger un pion adverse
    public static int scanner(Affichage affichage, Pion[][] plateau, boolean joueur) { 
        int compteur = 0;
        for (int i=0; i<10; i++) {           
            for (int j=0; j<10; j++) {
                if (plateau[i][j] != null) { //on doit vérifier qu'il y a un pion avant de regarder s'il a tel ou tel attribut
                    if (plateau[i][j].couleur == joueur) {
                        if (plateau[i][j].peutManger(affichage, plateau)) {
                            compteur++;
                            affichage.Boutons[i][j].setBackground(Color.ORANGE); //on colorie en orange la case du pion
                        }
                    }
                }
            }
        }
        return compteur;
    }
    
    //vérifie si la partie est finie (si aucun pion ne peut bouger pour le joueur actuel)
    public static boolean aucuneActionPossible(Affichage affichage, boolean joueur, Pion[][] plateau) {
		int compteur = 0;
		for (int i=0; i< 10;i++){
			for (int j=0; j<10;j++){
				if (plateau[i][j] != null) {
					if (plateau[i][j].couleur == joueur) {
						if ((plateau[i][j].peutBouger(plateau, joueur))||(plateau[i][j].peutManger(affichage, plateau))) {
							compteur++;
						}
					}
				}
			}
		}
		if (compteur == 0){
			return true;
		} else {
			return false;
		}
	}       
	
	//promotion d'un pion en dame lorsqu'il atteint la dernière ligne
    public static void promotion(Affichage affichage, Pion[][] plateau) {
		boolean couleur = false;
		for (int j=0; j<10; j+=9) { //teste la ligne 0 puis la ligne 9
			for (int i=0; i<10; i++) {
				if ((j==0)&&(plateau[j][i] != null)) { couleur = plateau[j][i].couleur; }
				if ((j==9)&&(plateau[j][i] != null)) { couleur = !plateau[j][i].couleur; }
				if (plateau[j][i] != null) {
					if ((couleur)&&(!plateau[j][i].dame)) {
						plateau[j][i] = new Dame(j, i, plateau[j][i].couleur);
						affichage.update(plateau);
						for (int k=0 ; k<3; k++) {
							affichage.Boutons[j][i].setBackground(Color.YELLOW);
							wait(100);
							affichage.Boutons[j][i].setBackground(Color.WHITE);
							wait(100);
						}                  
					}
				}
			}
		}
    }
 
	//tour du joueur : si un de ses pions peut manger, il le fait, sinon il doit faire bouger un de ses pions
    public static void tour(Affichage affichage, Reglages reglages, boolean joueur, Pion[][] plateau) {
        if (scanner(affichage, plateau, joueur) != 0) {
			tourMange(affichage, reglages, plateau, joueur); //le joueur devra forcément faire manger un de ses pions
		} else {
			tourAvance(affichage, reglages, plateau, joueur); //le joueur devra faire avancer un pion de son choix
		}
		promotion(affichage, plateau); //on teste si un pion peut être promu en dame à la fin du tour
    }
    
    //tour où le joueur est obligé de faire manger un de ses pions
    public static void tourMange(Affichage affichage, Reglages reglages, Pion[][] plateau, boolean joueur) {
        //sélection du pion joué
        selectionPionMange(affichage, reglages, plateau, joueur);
		int Xi = affichage.inputX; int Yi = affichage.inputY; //on définit Xi et Yi (coordonnées du pion sélectionné) qui serviront plus tard
		affichage.setXY(-1,-1); //on enlève les inputs actuels de coordonnées
		
		//sélection de la case d'arrivée
		selectionCaseMange(affichage, reglages, plateau, joueur, Xi, Yi);
		int Xf = affichage.inputX; int Yf = affichage.inputY; //on définit Xf et Yf (coordonnées de la case d'arrivée après avoir mangé) qui serviront plus tard
		affichage.setXY(-1,-1); //on enlève les inputs actuels de coordonnées
			
		//suppression du pion adverse et déplacement du pion du joueur
		if (!plateau[Yi][Xi].dame) {
            mangerPion(affichage, plateau, joueur, Xi, Yi, Xf, Yf);
        } else {
            affichage.setXY(Xf, Yf);
            int diagonale = diagonaleMange(affichage, Xi, Yi);
            mangerDame(affichage, plateau, joueur, Xi, Yi, Xf, Yf, diagonale);
        }

        //boucle pour les coups successifs
        while (plateau[Yf][Xf].peutManger(affichage, plateau)) {
            affichage.Boutons[Yf][Xf].setBackground(Color.GREEN);
            selectionCaseMange(affichage, reglages, plateau, joueur, Xf, Yf);
            affichage.clean();
            if (!plateau[Yf][Xf].dame) {
                mangerPion(affichage, plateau, joueur, Xf, Yf, affichage.inputX, affichage.inputY);
            } else {
                int diagonale = diagonaleMange(affichage, Xf, Yf);
                mangerDame(affichage, plateau, joueur, Xf, Yf, affichage.inputX, affichage.inputY, diagonale);                
            }
            Xf = affichage.inputX; Yf = affichage.inputY;
            affichage.setXY(-1,-1);
        }

	}   
	
	//sélection du pion joué dans le cas de l'action 'manger'
	public static void selectionPionMange(Affichage affichage, Reglages reglages, Pion[][] plateau, boolean joueur) {
        boolean pionChoisi = false; //le pion est correctement sélectionné
        while (!pionChoisi) {
			affichage.setXY(-1,-1);
			//joue automatiquement si l'autoplay est activé
			if (reglages.autoplay) {
				autoSelectionPionMange(affichage, plateau, joueur);
			}
			//on attend que le joueur sélectionne une case
			while ((affichage.inputX == -1)&&(affichage.inputY == -1)) { 
				if ((!joueur || reglages.debug)&&(reglages.IA)) { wait(5); affichage.IA(); }
				else { wait(100); }
			}
			//vérifie si le choix de pion est valide
			if (plateau[affichage.inputY][affichage.inputX] != null) {
				if ((plateau[affichage.inputY][affichage.inputX].couleur == joueur)&&(plateau[affichage.inputY][affichage.inputX].peutManger(affichage, plateau))) {
					pionChoisi = true;
				}
			}
		}
		affichage.clean(); //enlève les cases bleues des pions qui peuvent manger
		affichage.Boutons[affichage.inputY][affichage.inputX].setBackground(Color.GREEN); //on colorie en vert la case correctement sélectionnée					
	}
	
	//sélection de la case d'arrivée de l'action 'manger'
	public static void selectionCaseMange(Affichage affichage, Reglages reglages, Pion[][] plateau, boolean joueur, int Xi, int Yi) {
        boolean caseChoisie = false; //la case est correctement sélectionnée
		while (!caseChoisie) {
			affichage.setXY(-1,-1);
			plateau[Yi][Xi].peutManger(affichage, plateau);
			plateau[Yi][Xi].choixMange(affichage, plateau, joueur); //colorie les choix possibles
			//joue automatiquement si l'autoplay est activé
			if ((reglages.autoplay)&&(!plateau[Yi][Xi].dame)) {
				autoSelectionCaseMange(affichage, plateau, joueur, Xi, Yi);
			}
			//on attend que le joueur sélectionne une case
			while ((affichage.inputX == -1)&&(affichage.inputY == -1)) {
				if ((!joueur || reglages.debug)&&(reglages.IA)) { wait(5); affichage.IA(); }
				else { wait(100); }
			}
			//vérifie si le choix de la case est valide
			int diagonale = diagonaleMange(affichage, Xi, Yi); //dans quelle diagonale l'action se fait		
			if (diagonale>0) {
                if (!plateau[Yi][Xi].dame) { //si le pion est un pion
                    if ((Math.abs(Xi-affichage.inputX) == 2)&&(Math.abs(Yi-affichage.inputY) == 2)&&(plateau[Yi][Xi].voisin(plateau, diagonale))&&(plateau[Yi][Xi].sautPossible(affichage, plateau, diagonale))) {
				        caseChoisie = true;
				    }
                } else { //si c'est une dame
                    if (Math.abs(Xi-affichage.inputX) == Math.abs(Yi-affichage.inputY)) { // Vérifie que c'est bien une diagonale
                        if ((plateau[Yi][Xi].peutMangerDirection(affichage, plateau, diagonale))&&(plateau[affichage.inputY][affichage.inputX] == null)) {
							if (plateau[Yi][Xi].compteurPionsDiagonale(plateau, affichage.inputX, affichage.inputY, diagonale) == 1) {
								caseChoisie = true;
							}
                        }
                    }
                }
			}
		}
		affichage.clean(); //on recolorie la case du pion en blanc		
	}
    
    //détermination de la diagonale de l'action 'manger'
    public static int diagonaleMange(Affichage affichage, int Xi, int Yi) {
		int diagonale = 0;
		if ((Xi>affichage.inputX)&&(Yi>affichage.inputY)) { diagonale = 1; } //en haut à gauche
		else if ((Xi<affichage.inputX)&&(Yi>affichage.inputY)) { diagonale = 2; } //en haut à droite
		else if ((Xi<affichage.inputX)&&(Yi<affichage.inputY)) { diagonale = 3; } //en bas à droite
		else if ((Xi>affichage.inputX)&&(Yi<affichage.inputY)) { diagonale = 4; } //en bas à gauche
		return diagonale;
	}
	
	//fait tous les déplacements nécessaires dans l'action 'manger' d'un pion
	public static void mangerPion(Affichage affichage, Pion[][] plateau, boolean joueur, int Xi, int Yi, int Xf, int Yf) {
		plateau[Yi][Xi].deplacement(plateau, (Xi+Xf)/2, (Yi+Yf)/2);
		affichage.update(plateau);
		wait(80);
		plateau[(Yi+Yf)/2][(Xi+Xf)/2].deplacement(plateau, Xf,Yf);
		affichage.update(plateau);		
	}
	
	//fait tous les déplacements nécessaires dans l'action 'manger' d'une dame
	public static void mangerDame(Affichage affichage, Pion[][] plateau, boolean joueur, int Xi, int Yi, int Xf, int Yf, int diagonale) {
        int j = Yi;      
        switch(diagonale) {
            case 1: //en haut à gauche
                for (int i = Xi; i > Xf; i--) {
                    plateau[j][i].deplacement(plateau, (i-1), (j-1));
                    affichage.update(plateau);
                    wait(80);
                    j--;
                }
                break;
            case 2: //en haut à droite
                for (int i = Xi; i < Xf; i++) {
                    plateau[j][i].deplacement(plateau, (i+1), (j-1));
                    affichage.update(plateau);
                    wait(80);
                    j--;
                }
                break;
            case 3: //en bas à gauche
                for (int i = Xi; i < Xf; i++) {
                    plateau[j][i].deplacement(plateau,(i+1), (j+1));
                    affichage.update(plateau);
                    wait(80);
                    j++;
                }
                break;
            case 4: //en bas à droite
                for (int i = Xi; i > Xf; i--) {
                    plateau[j][i].deplacement(plateau, (i-1), (j+1));
                    affichage.update(plateau);
                    wait(80);
                    j++;                    
                }
                break;
        }			
	}

	//tour où le joueur est obligé de faire avancer un de ses pions
    public static void tourAvance(Affichage affichage, Reglages reglages, Pion[][] plateau, boolean joueur) {
        //sélection du pion joué
        selectionPionAvance(affichage, reglages, plateau, joueur);
		int Xi = affichage.inputX; int Yi = affichage.inputY; //on définit Xi et Yi (coordonnées du pion sélectionné) qui serviront plus tard
		affichage.setXY(-1,-1); //on enlève les inputs actuels de coordonnées
		
		//sélection de la case d'arrivée
		selectionCaseAvance(affichage, reglages, plateau, joueur, Xi, Yi);
		int Xf = affichage.inputX; int Yf = affichage.inputY; //on définit Xf et Yf (coordonnées de la case d'arrivée) qui serviront plus tard
		affichage.setXY(-1,-1); //on enlève les inputs actuels de coordonnées
		
		//déplacement du pion du joueur
		if (!plateau[Yi][Xi].dame) {
            plateau[Yi][Xi].deplacement(plateau, Xf, Yf);
        } else {
            affichage.setXY(Xf, Yf);
            int diagonale = diagonaleMange(affichage, Xi, Yi);
            mangerDame(affichage, plateau, joueur, Xi, Yi, Xf, Yf, diagonale);
        }
		affichage.update(plateau);
	} 

	//sélection du pion joué dans le cas de l'action 'avancer'
	public static void selectionPionAvance(Affichage affichage, Reglages reglages, Pion[][] plateau, boolean joueur) {
        boolean pionChoisi = false; //le pion est correctement sélectionné
        while (!pionChoisi) {
			affichage.setXY(-1,-1);
			//on attend que le joueur sélectionne une case
			while ((affichage.inputX == -1)&&(affichage.inputY == -1)) {
				if ((!joueur || reglages.debug)&&(reglages.IA)) { wait(5); affichage.IA(); }
				else { wait(100); }
			}
			//vérifie si le choix de pion est valide
			if (plateau[affichage.inputY][affichage.inputX] != null) {
				if ((plateau[affichage.inputY][affichage.inputX].couleur == joueur)&&(plateau[affichage.inputY][affichage.inputX].peutBouger(plateau, joueur))) {
					pionChoisi = true;
				}
			}
		}
		affichage.Boutons[affichage.inputY][affichage.inputX].setBackground(Color.GREEN); //on colorie en vert la case correctement sélectionnée				
	}
	
	//sélection de la case d'arrivée de l'action 'avancer'
	public static void selectionCaseAvance(Affichage affichage, Reglages reglages, Pion[][] plateau, boolean joueur, int Xi, int Yi) {
        boolean caseChoisie = false; //la case est correctement sélectionnée
		while (!caseChoisie) {
			affichage.setXY(-1,-1);
			plateau[Yi][Xi].choixAvance(affichage, plateau, joueur); //colorie les choix possibles (pour les pions et pour les dames à la fois)
			//on attend que le joueur sélectionne une case
			while ((affichage.inputX == -1)&&(affichage.inputY == -1)) {
				if ((!joueur || reglages.debug)&&(reglages.IA)) { wait(5); affichage.IA(); }
				else { wait(100); }
			}
			//instanciation de la diagonale
			int diagonale;
			if (!plateau[Yi][Xi].dame) {
				diagonale = diagonaleAvance(affichage, Xi, Yi);
			} else {
				diagonale = diagonaleMange(affichage, Xi, Yi);
			}
			//test de la validité de la case
			if (diagonale>0) {
				if (!plateau[Yi][Xi].dame) { //si le pion est un pion
                    if (joueur) { //on fait deux cas selon le joueur car les pions ne peuvent avancer que dans une direction
					    if ((Math.abs(Xi-affichage.inputX) == 1)&&(Yi-affichage.inputY == 1)&&(plateau[Yi][Xi].peutBougerDirection(plateau, diagonale, joueur))) {
						    caseChoisie = true;
					    }
				    } else {
					    if ((Math.abs(Xi-affichage.inputX) == 1)&&(Yi-affichage.inputY == -1)&&(plateau[Yi][Xi].peutBougerDirection(plateau, diagonale, joueur))) {
						    caseChoisie = true;
					    }
				    }                    
                } else { //si le pion est une dame
                    if (Math.abs(Xi-affichage.inputX) == Math.abs(Yi-affichage.inputY)) { // Vérifie que c'est bien une diagonale
                        if ((plateau[affichage.inputY][affichage.inputX] == null)&&(plateau[Yi][Xi].compteurPionsDiagonale(plateau, affichage.inputX, affichage.inputY, diagonale) == 0)) {
						    caseChoisie = true;
                        }
                    }
                }		
			}
		}
		affichage.clean();	
	}

    //détermination de la diagonale de l'action 'avancer'
    public static int diagonaleAvance(Affichage affichage, int Xi, int Yi) {
		int diagonale = 0;
		if (Xi>affichage.inputX) {
			diagonale = 1;
		} else if (Xi<affichage.inputX) {
			diagonale = 2;
		}
		return diagonale;
	}		
	
    //sélection automatique du pion qui peut manger s'il est seul
    public static void autoSelectionPionMange(Affichage affichage, Pion[][] plateau, boolean joueur) {
        int compteur = 0;
        int X = -1; int Y = -1;
        for (int i=0; i<10; i++) {           
            for (int j=0; j<10; j++) {
                if (plateau[i][j] != null) {
                    if (plateau[i][j].couleur == joueur) {
                        if (plateau[i][j].peutManger(affichage, plateau)) {
                            compteur++;
                            Y = i; X = j;
                        }
                    }
                }
            }
        }
        if (compteur==1) {
			affichage.setXY(X, Y);
		} 		
	}
	
	//sélection automatique de la case d'arrivée de l'action 'manger' s'il n'y en a qu'une seule
	public static void autoSelectionCaseMange(Affichage affichage, Pion[][] plateau, boolean joueur, int Xi, int Yi) {
		int compteur = 0;
		int X = 1; int Y = -1;
		for (int i=0 ; i<10 ; i++) {
			for (int j=0 ; j<10 ; j++) {
				if (plateau[(Yi+j)/2][(Xi+i)/2] != null) {
					if ((Math.abs(Xi-i) == 2)&&(Math.abs(Yi-j) == 2)&&(plateau[j][i] == null)&&(plateau[(Yi+j)/2][(Xi+i)/2].couleur != joueur)) {
						compteur++;
						X = i; Y = j;
					}
				}	
			}
		}
		if (compteur==1) {
			wait(300);
			affichage.setXY(X, Y);
		} 			
	}
	
	//relance la partie (renvoie le nouvel affichage, qui est la fenêtre de jeu)
	public static Affichage relancer(Affichage affichage, Reglages reglages, Pion[][] plateau) {
		affichage.frame.dispose();
		affichage = new Affichage(reglages);
		initialisation(plateau);
		affichage.update(plateau);
		reglages.recommencer = false;
		return affichage;
	}
	
	//permet d'attendre un nombre donné de millisecondes
    public static void wait(int ms) {
		try { Thread.sleep(ms); }
		catch(InterruptedException ex) { Thread.currentThread().interrupt(); }
	}
			     
}
