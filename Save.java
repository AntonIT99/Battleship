import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class Save 
{
	private int savedGridSize;
	private ArrayList<Ship> savedPlayerShips, savedAIShips;
	private LinkedList<GridPosition> savedFirePositionsPlayer, savedFirePositionsAI;
	
	public Save() throws IOException 
	{
		FileInputStream fis = new FileInputStream("savegame.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		
		
		savedPlayerShips = new ArrayList<Ship>();
		savedAIShips = new ArrayList<Ship>();
		savedFirePositionsPlayer = new LinkedList<GridPosition>();
		savedFirePositionsAI  = new LinkedList<GridPosition>();
		
		String line = reader.readLine();
		 
        while(line != null) {

            String header = line.split(Pattern.quote("{"))[0];
            String content = line.split(Pattern.quote("{"))[1].split(Pattern.quote("}"))[0];
            
            //chargement taille de la grille
            if (header.equals("gridSize")) 
            {
            	savedGridSize = Integer.parseInt(content.split(Pattern.quote("}"))[0]);
            }
            
            //chargement des bateaux
            if (header.equals("PlayerShip") || header.equals("AIShip")) 
            {
            	Ship.Type shipType = Ship.Type.valueOf(content.split(";")[0]);
            	int x = Integer.parseInt(content.split(Pattern.quote("("))[1].split(":")[0]);
            	int y = Integer.parseInt(content.split(":")[1].split(Pattern.quote(")"))[0]);
            	GridPosition position = new GridPosition(x,y);
            	boolean isDestroyed = Boolean.parseBoolean(content.split(";")[3]);
            	int orientation = Integer.parseInt(content.split(";")[4]);
            	
            	Ship ship = new Ship(shipType);
            	ship.setPosition(position);
            	ship.setOrientation(orientation);
            	ship.place();
            	if (isDestroyed) {
            		ship.destroy();
            	}
            	
            	if (header.equals("PlayerShip"))
            	{
            		savedPlayerShips.add(ship);
            		
            	} else if (header.equals("AIShip")) {
            		
            		savedAIShips.add(ship);
            	}
            }
            
            //chargement des positions tirées
            if (header.equals("PlayerFire") || header.equals("AIFire")) 
            {
            	int x = Integer.parseInt(content.split(Pattern.quote("("))[1].split(":")[0]);
            	int y = Integer.parseInt(content.split(":")[1].split(Pattern.quote(")"))[0]);
            	int state = Integer.parseInt(content.split(";")[1]);
            	GridPosition position = new GridPosition(x,y);
            	position.setState(state);
            	
            	if (header.equals("PlayerFire"))
            	{
            		savedFirePositionsPlayer.add(position);
            		
            	} else if (header.equals("AIFire")) {
            		
            		savedFirePositionsAI.add(position);
            	}
            }
            line = reader.readLine();
        }
		fis.close();
	}
	
	public int loadGridSize() 
	{
		return savedGridSize;
	}
	
	public ArrayList<Ship> loadPlayerShips() 
	{
		return savedPlayerShips;
	}
	
	public ArrayList<Ship> loadAIShips() 
	{
		return savedAIShips;
	}
	
	public LinkedList<GridPosition> loadPlayerFirePositions() 
	{
		return savedFirePositionsPlayer;
	}
	
	public LinkedList<GridPosition> loadAIFirePositions() 
	{
		return savedFirePositionsAI;
	}
	
	public static void saveGame(int gridSize, ArrayList<Ship> playerShips, ArrayList<Ship> AIships, LinkedList<GridPosition> firePositionsPlayer, LinkedList<GridPosition> firePositionsAI) throws IOException
	{
			
		FileOutputStream fos = new FileOutputStream("savegame.txt");
		fos.write(new String("gridSize{" + gridSize + "}\n").getBytes());
		for (int i = 0; i<playerShips.size(); i++)
		{
			fos.write(("PlayerShip" + playerShips.get(i).toString() + "\n").getBytes());
		}
		
		for (int i = 0; i<AIships.size(); i++)
		{
			fos.write(("AIShip" + AIships.get(i).toString() + "\n").getBytes());
		}
		
		for (int i = 0; i<firePositionsPlayer.size(); i++)
		{
			fos.write(("PlayerFire{" + firePositionsPlayer.get(i).toString() + "}\n").getBytes());
		}
		
		for (int i = 0; i<firePositionsAI.size(); i++)
		{
			fos.write(("AIFire{" + firePositionsAI.get(i).toString() + "}\n").getBytes());
		}
		
		fos.flush();
		fos.close();
	}

}
   