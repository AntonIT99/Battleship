import java.awt.Point;
import java.util.LinkedList;

public class GridPosition {
    
    public int x, y; //coordonées en nombres de cases, le coin supérieur gauche de la grille étant x=0,y=0
    public final static int DEFAULT = 0, MISSED = 1, HIT = 2; //états possibles de la position : touché ou manqué
    public ImagePanel icon; //image de l'état de la position
    private int state = DEFAULT; //état de la position
    
    public GridPosition(int a, int b) 
    {
        x = a;
        y = b;
    }
    
    public GridPosition(Point point) //transcription d'un point de l'écran en position de la grille
    {
		x = point.x/Grid.SQUARESIZE;
		y = point.y/Grid.SQUARESIZE;
    }
    
    public Point getCenter() //renvoie les coordonnées du centre de la case
    {
    	return new Point(x*Grid.SQUARESIZE + Grid.SQUARESIZE/2, y*Grid.SQUARESIZE + Grid.SQUARESIZE/2);
    }
    
    public Point getTopLeftCorner() //renvoie les coordonnées du coin supérieur gauche de la case
    {
    	return new Point(x*Grid.SQUARESIZE, y*Grid.SQUARESIZE);
    }
    
    public boolean isIncluded(GridPosition topLeftCorner, GridPosition backRightCorner) //détermine si la position est incluse dans l'espace délimité par les deux positions en paramètre
    {
    	if (x>= topLeftCorner.x && x<= backRightCorner.x && y>= topLeftCorner.y && y<= backRightCorner.y)
    	{
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public int getState()
    {
    	return state;
    }
    
    public void setState(int s)
    {
    	state = s;
    }
    
    public void drawState(Grid grid) //déclenche l'affichage de l'état de la position : touché ou manqué
    {
    	if (state == MISSED)
    	{
    		icon = new ImagePanel("missed.png");
    		icon.setPosition(this.getCenter().x, this.getCenter().y);
    		grid.add(icon, 0);
    	} else if (state == HIT) {
    		icon = new ImagePanel("hit.png");
    		icon.setPosition(this.getCenter().x, this.getCenter().y);
    		grid.add(icon, 0);
    	}
    }
    
    public static GridPosition random(int gridSize) //renvoie une case aléatoire de la grille
    { 
		return new GridPosition(Main.randomInt(0, gridSize-1), Main.randomInt(0, gridSize-1));
    }
    
    public static GridPosition randomAdjacent(GridPosition pos, int gridSize) //renvoie une case aléatoire adjacente à la case en paramètre
    {
		return pos.AdjacentPositions(gridSize)[Main.randomInt(0, pos.AdjacentPositions(gridSize).length-1)];
    }
    
    public boolean isAdjacentTo(GridPosition pos, int gridSize) //determine si la case en paramètre est une case adjacente
    {
    	boolean adjacent = false;
    	for (int i = 0; i < this.AdjacentPositions(gridSize).length; i++)
    	{
    		if (pos.equals(this.AdjacentPositions(gridSize)[i]))
    		{
    			adjacent = true;
    		}
    	}
    	return adjacent;
    }
    
    public GridPosition[] AdjacentPositions (int gridSize) //renvoie un tableau de toutes les cases adjacentes
    {
    	GridPosition[] adjPos = new GridPosition [2];
    	
    	if ((x == 0 || x == gridSize-1) && (y == 0 || y == gridSize-1))
    	{
    		adjPos = new GridPosition [2];
    		if (x == 0) 
    		{
    			adjPos[0] = new GridPosition(x+1, y);
    		} else if (x == gridSize-1) {
    			adjPos[0] = new GridPosition(x-1, y);
    		}
    		
    		if (y == 0) 
    		{
    			adjPos[1] = new GridPosition(x, y+1);
    		} else if (y == gridSize-1) {
    			adjPos[1] = new GridPosition(x, y-1);
    		}
    		
    	} else if (((x == 0 || x == gridSize-1) && (y >= 1 && y <= gridSize-2)) || ((y == 0 || y == gridSize-1) && (x >= 1 && x <= gridSize-2))) {
    		adjPos = new GridPosition [3];
    		
    		if(x == 0 || x == gridSize-1)
    		{
    			if (x == 0)
    			{
    				adjPos[0] = new GridPosition(x+1,y);
    			} else if (x == gridSize-1) {
    				adjPos[0] = new GridPosition(x-1,y);
    			}
    			adjPos[1] = new GridPosition(x,y-1);
    			adjPos[2] = new GridPosition(x,y+1);
    		
    		} else if (x >= 1 && x <= gridSize-2) {
    		
    			if (y == 0)
    			{
    				adjPos[0] = new GridPosition(x,y+1);
    			} else if (y == gridSize-1) {
    				adjPos[0] = new GridPosition(x,y-1);
    			}
    			adjPos[1] = new GridPosition(x-1,y);
    			adjPos[2] = new GridPosition(x+1,y);
    		}
    		
    	} else if (this.isIncluded(new GridPosition(1, 1), new GridPosition(gridSize-2, gridSize-2))) {
    		adjPos = new GridPosition [4];
    		
    		adjPos[0] = new GridPosition(x-1,y);
    		adjPos[1] = new GridPosition(x+1,y);
    		adjPos[2] = new GridPosition(x,y-1);
    		adjPos[3] = new GridPosition(x,y+1);
    	}
    	
    	return adjPos;
    }
    
	public boolean isPositionAlreadyTargeted(LinkedList<GridPosition> firePositions) //vérifie si une case à déjà été ciblée
	{
		boolean positionAlreadyTargeted = false;
		for (int i = 0; i<firePositions.size(); i++)
		{
			if (this.equals(firePositions.get(i)))
			{
				positionAlreadyTargeted = true;
			}
		}
		
		return positionAlreadyTargeted;
	}
	
	public boolean allAdjacentPositionsAlreadyTargeted(LinkedList<GridPosition> firePositions, int gridSize) //vérifie si toutes les cases adjacentes ont déjà été ciblée
	{
		boolean positionsAlreadyTargeted = true;
		for (int i = 0; i<this.AdjacentPositions(gridSize).length; i++)
		{
			if (!this.AdjacentPositions(gridSize)[i].isPositionAlreadyTargeted(firePositions))
			{
				positionsAlreadyTargeted = false;
			}
		}
		
		return positionsAlreadyTargeted;
	}
    
    public String toString()
    {
    	return "("+ x + ":" + y + ")" + ";" + getState();
    }
	
    @Override //écrase la méthode equals héritée de la classe Object
	public boolean equals(Object obj) { //permet de vérifier l'équivalence entre deux GridPosition (si elles correspondent à la même case)

    	boolean equals = false;
    	
		if (obj instanceof GridPosition)
		{
			if (this.x == ((GridPosition)(obj)).x && this.y == ((GridPosition)(obj)).y)
			{
				equals = true;
			}
		}
		return equals;
	}
}
