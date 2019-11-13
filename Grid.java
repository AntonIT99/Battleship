import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class Grid extends JPanel implements Locate {
    
    private int size; //nombre de cases par colone ou par ligne de la grille (grille carrÃ©e)
    public final static int SQUARESIZE = 100; //taille d'une case
    public final static int LIMIT = 500; //nombre de pixels limitant le déplacement de la grille (nombre minimum de pixels du coté la grille qui doivent rester visible dans les limites de l'écran)
    
    //(x,y) est la position du centre de la grille
    public Grid(int s, int x, int y) {
        super();
        size = s;
        setPosition(x, y);
		setLayout(null);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        
        
        for (int i = 0; i<=size; i++) {
            g.setColor(Color.cyan);
            g.drawLine(0, i*SQUARESIZE, size*SQUARESIZE, i*SQUARESIZE);
            g.drawLine(i*SQUARESIZE, 0, i*SQUARESIZE, size*SQUARESIZE);
        }
    }
    
    public int getPixelSize() {
        return size*SQUARESIZE+1;
    }

	@Override
	public void setPosition(int x, int y) //règle la position à partir du centre
	{
		Point topLeftCorner = new Point (x - getPixelSize()/2, y - getPixelSize()/2);
		Point backRightCorner = new Point (x + getPixelSize()/2, y + getPixelSize()/2);
		
		if (backRightCorner.x < LIMIT)
		{
			x = LIMIT - getPixelSize()/2;
		}
		
		if (backRightCorner.y < LIMIT)
		{
			y = LIMIT - getPixelSize()/2;
		}
		
		if (topLeftCorner.x > Main.WRES - LIMIT)
		{
			x = Main.WRES - LIMIT + getPixelSize()/2;
		}
		
		if (topLeftCorner.y > Main.HRES - LIMIT)
		{
			y = Main.HRES - LIMIT + getPixelSize()/2;
		}
		setBounds(x - getPixelSize()/2, y - getPixelSize()/2, getPixelSize(), getPixelSize());
	}

	@Override
	public Point getPosition() //renvoie les coordonnées du centre
	{
		return new Point(getBounds().x + getPixelSize()/2, getBounds().y + getPixelSize()/2);
	}
    

}
