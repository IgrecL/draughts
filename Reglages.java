public class Reglages {
	boolean autoplay; //les coups obligatoires sont joués automatiquement
	boolean IA; //le joueur noir est une "IA" qui joue des coups aléatoires quand ils ne sont pas obligatoires
	boolean recommencer; //s'il est à true, fait relancer la partie
	boolean debug; //mode automatique pour les deux joueurs (ne peut être changé que manuellement)
	int seRend; //0 = personne ne se rend | 1 = les blancs se rendent | 2 = les noirs se rendent
       
    public Reglages() {
		autoplay = true;
		IA = false;
		recommencer = false;
		debug = true; //mode debug
		seRend = 0;
	}
	
	public void setAutoplay(boolean autoplay) {
		this.autoplay = autoplay;
	}
	
	public void setIA(boolean IA) {
		this.IA = IA;
	}
}
