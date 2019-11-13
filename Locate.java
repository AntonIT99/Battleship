import java.awt.Point;

public interface Locate {
	
	public void setPosition (int x, int y); //règle la position à partir du centre
    
    public Point getPosition(); //renvoie les coordonnées du centre

}
