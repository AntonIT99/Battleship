import java.util.ArrayList;

public class Ship {
    
    private int length;
	private boolean isDestroyed = false;
	private boolean isPlaced = false;
    private GridPosition position = new GridPosition(-1, -1); //position du coin supérieur gauche du bateau
    private int orientation = HORIZONTAL1;
    public ImagePanel display;
    private Type type;
    
    
    public static final int HORIZONTAL1 = 0, VERTICAL1 = 1, HORIZONTAL2 = 2, VERTICAL2 = 3;
   
    
    public enum Type 
    {
        AIRCRAFT_CARRIER, DESTROYER, SUBMARINE, CRUISER, BATTLESHIP
    }
    
    public Ship(Type t) 
    {
    	type = t;
    	if (type == Type.AIRCRAFT_CARRIER)
    	{
    		length = 4;
    		display = new ImagePanel("aircraftCarrier.png");
    	}
    	
    	if (type == Type.DESTROYER)
    	{
    		length = 2;
    		display = new ImagePanel("destroyer.png");
    	}
    	
    	if (type == Type.SUBMARINE)
    	{
    		length = 3;
    		display = new ImagePanel("submarine.png");
    	}
    	
    	if (type == Type.CRUISER)
    	{
    		length = 3;
    		display = new ImagePanel("cruiser.png");
    	}
    	
    	if (type == Type.BATTLESHIP)
    	{
    		length = 5;
    		display = new ImagePanel("battleship.png");
    	}
    }
    
    public void setOrientation(int o) 
    {
    	double angle = 0;
    	if (orientation == VERTICAL1)
    	{
    		switch (o)
    		{
    			case VERTICAL1:
    				angle = 0;
    				break;
				case HORIZONTAL1:
					angle = -Math.PI/2;
					break;
				case HORIZONTAL2:
					angle = Math.PI/2;
					break;
				case VERTICAL2:
					angle = Math.PI;
					break;
    		}
    	} else if (orientation == VERTICAL2) {
    		switch (o)
    		{
    			case VERTICAL1:
    				angle = Math.PI;
    				break;
				case HORIZONTAL1:
					angle = Math.PI/2;
					break;
				case HORIZONTAL2:
					angle = -Math.PI/2;
					break;
				case VERTICAL2:
					angle = 0;
					break;
    		}
    	} else if (orientation == HORIZONTAL1) {
    		switch (o)
    		{
    			case VERTICAL1:
    				angle = Math.PI/2;
    				break;
				case HORIZONTAL1:
					angle = 0;
					break;
				case HORIZONTAL2:
					angle = Math.PI;
					break;
				case VERTICAL2:
					angle = -Math.PI/2;
					break;
    		}
    	} else if (orientation == HORIZONTAL2) {
    		switch (o)
    		{
    			case VERTICAL1:
    				angle = -Math.PI/2;
    				break;
				case HORIZONTAL1:
					angle = Math.PI;
					break;
				case HORIZONTAL2:
					angle = 0;
					break;
				case VERTICAL2:
					angle = Math.PI/2;
					break;
    		}
    	}

    	if (angle != 0) 
    	{
    		display.rotate(angle);
    	}
    	orientation = o;
    	
	}
    
    public void setRandomOrientation() 
    {
    	setOrientation(Main.randomInt(0, 3));
    }
    
    public void setPosition(GridPosition p)
    {
    	position.x = p.x;
    	position.y = p.y;
    }
    
    public void setPosition(int x, int y)
    {
    	position.x = x;
    	position.y = y;
    }
    
    public void draw() //méthode qui actualise l'image du bateau conformément à sa position. A appeler après avoir placé un bateau ou lors du changement de sa position
    {
    	if (orientation == VERTICAL1 || orientation == VERTICAL2)
    	{
    		display.setBounds(position.getTopLeftCorner().x, position.getTopLeftCorner().y, display.image.getWidth(), display.image.getHeight());
    	} else if (orientation == HORIZONTAL1 || orientation == HORIZONTAL2) {
    		display.setBounds(position.getTopLeftCorner().x, position.getTopLeftCorner().y, display.image.getWidth(), display.image.getHeight());
    	}
    	display.repaint();
    }
    
    public int getOrientation() 
	{
		return orientation;
	}
    
    public GridPosition getPosition()
    {
        return position;
    }
    
    public boolean isPlaceableX (GridPosition pos1, int gridSize) //vérifie si les coordonnées X du bateau rentrent dans la grille
    {
    	int newPosition = -1;
    	if (orientation == VERTICAL1 || orientation == VERTICAL2)
    	{
    		newPosition = pos1.x;
    	} else if (orientation == HORIZONTAL1 || orientation == HORIZONTAL2) {
    		newPosition = pos1.x + length - 1;
    	}
    	
    	if (newPosition>= 0 && newPosition<= gridSize-1)
    	{
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public boolean isPlaceableY (GridPosition pos1, int gridSize) //vérifie si les coordonnées Y du bateau rentrent dans la grille
    {
    	int newPosition = -1;
    	if (orientation == VERTICAL1 || orientation == VERTICAL2)
    	{
    		newPosition = pos1.y + length - 1;
    	} else if (orientation == HORIZONTAL1 || orientation == HORIZONTAL2) {
    		newPosition = pos1.y;
    	}
    	
    	if (newPosition>= 0 && newPosition<= gridSize-1)
    	{
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public Type getType() 
    {
    	return type;
    }
    
    public void destroy()
    {
    	isDestroyed = true;
    }
    
    public boolean isDestroyed()
    {
    	return isDestroyed;
    }
    
    public void place()
    {
    	isPlaced = true;
    }
    
    public boolean isPlaced()
    {
    	return isPlaced;
    }
    
    public GridPosition[] getAllPositions() //revoie toutes les cases occupées par un bateau
    {
    	GridPosition[] pos = new GridPosition[length];
    	
    	for (int i = 0; i<length; i++)
    	{
    		if (position.equals(new GridPosition(-1,-1))) {
    			pos[i] = new GridPosition(-1,-1);
    		} else {
    			if (orientation == VERTICAL1 || orientation == VERTICAL2) {
        			pos[i] = new GridPosition(position.x, position.y+i);
        		} else if (orientation == HORIZONTAL1 || orientation == HORIZONTAL2) {
        			pos[i] = new GridPosition(position.x+i, position.y);
        		}
    		}
    	}
    	return pos;
    }
    
    public boolean canBePlaced(GridPosition pos, ArrayList<Ship> shipList, int gridSize) //méthode qui détermine si un bateau peut-être placé ou non sur une position (dépend de la taille de la grille et des autres bateaux)
    {
    	boolean noContactWithOtherShip = true;
    	
    	if (isPlaceableX(pos, gridSize) && isPlaceableY(pos, gridSize))
    	{
    		for (int i = 0; i<shipList.size(); i++) //parcourt la liste des bateaux
    		{
    			for (int j = 0; j<shipList.get(i).getAllPositions().length; j++) //parcourt les cases occupées par un bateau de la liste
    			{
    				for (int k = 0; k<this.getAllPositions().length; k++) // parcourt les cases que le bateau à placer occuperait s'il est placé à la position en paramètre
    				{
    					if(shipList.get(i).isPlaced() && (shipList.get(i).getAllPositions()[j].equals(this.getAllPositions()[k]) || shipList.get(i).getAllPositions()[j].isAdjacentTo(this.getAllPositions()[k], gridSize))) //vérifie qu'il y n'a pas un bateau de la liste au même endroit ou sur des cases adjacentes
    					{
    						noContactWithOtherShip = false;
    					}
    				}
    			}
    		}
    	} else {
    		
    		noContactWithOtherShip = false;
    	}
    	return noContactWithOtherShip;
    }
    
    public String toString() 
    {
		return "{" + getType() + ";" + getPosition().toString() + ";" + isDestroyed() + ";" + getOrientation() + "}";
    }
    
}

