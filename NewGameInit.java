import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.awt.*;


@SuppressWarnings("serial")
public class NewGameInit extends JFrame implements ActionListener
{   
	private JPanel mainPanel; 
	private JButton createGame; 
	private JButton back; 
	private JLabel backgroundLabel;
	private JLabel gridText;
	private JLabel aircraftCarrierText, cruiserText, submarineText, destroyerText, battleshipText;
	private ImageIcon backgroundImage;
	@SuppressWarnings("rawtypes")
	private JComboBox gridSizeBox;
	@SuppressWarnings("rawtypes")
	private JComboBox nb_AIRCRAFT_CARRIER, nb_CRUISER, nb_SUBMARINE, nb_DESTROYER, nb_BATTLESHIP;
	private int nbAircraftCarrier = 1, nbCruiser = 1, nbSubmarine = 1, nbDestroyer = 1, nbBattleship = 1;
	private ImagePanel aircraftCarrierIcon;
	private ImagePanel cruiserIcon;
	private ImagePanel submarineIcon;
	private ImagePanel battleshipIcon;
	private ImagePanel destroyerIcon;
	public int gridSize = 10;
	public boolean launchGame = false;
	public boolean backtoMainMenu = false;
	public ArrayList<Ship> playerShips, AIships;
	
    
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public NewGameInit()
    {   
        setTitle("Battleship");
		this.setSize(Main.WRES,Main.HRES);
		this.setLocation(0,0);
		this.setResizable(false); //non redimensionable
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //permet de fermer la fenetre
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); //mode plein ecran
        
        //fond
        backgroundImage = new ImageIcon("assets/selectionScreen.png");
        backgroundLabel = new JLabel(backgroundImage);
        
        //panneau global
        mainPanel = new JPanel();
        mainPanel.setBounds(0,0,Main.WRES,Main.HRES);
		mainPanel.setLayout(null);
        mainPanel.setOpaque(false);
        
        //boutons
        createGame = new JButton("Create Game");
        createGame.setBounds(Main.WRES/2+650,Main.HRES/2+400,200,50);
        createGame.setFont(new Font("Arial", Font.BOLD, 20));
		createGame.setBackground(new Color(0,0,0));
		createGame.setForeground(Color.white);
		createGame.addActionListener(this);
		
        back = new JButton("Back to Menu");
        back.setBounds(Main.WRES/2+450,Main.HRES/2+400,200,50);
        back.setFont(new Font("Arial", Font.BOLD, 20));
		back.setBackground(new Color(0,0,0));
		back.setForeground(Color.white);
		back.addActionListener(this);
		
		
		
		//textes
		gridText = new JLabel();
		gridText.setBounds(50, 50,400,50);
		gridText.setForeground(Color.WHITE);
		gridText.setFont(new Font("Arial", Font.BOLD, 40));
		gridText.setText("Grid Size :");
		
		aircraftCarrierText = new JLabel();
		aircraftCarrierText.setBounds(400, 150,400,50);
		aircraftCarrierText.setForeground(Color.WHITE);
		aircraftCarrierText.setFont(new Font("Arial", Font.BOLD, 20));
		aircraftCarrierText.setText("Number of Aircraft Carriers :");
		
		cruiserText = new JLabel();
		cruiserText.setBounds(400, 625,400,50);
		cruiserText.setForeground(Color.WHITE);
		cruiserText.setFont(new Font("Arial", Font.BOLD, 20));
		cruiserText.setText("Number of Cruisers :");
		
        submarineText = new JLabel();
		submarineText.setBounds(800, 625,400,50);
		submarineText.setForeground(Color.WHITE);
		submarineText.setFont(new Font("Arial", Font.BOLD, 20));
		submarineText.setText("Number of Submarines  :");
		
		destroyerText = new JLabel();
		destroyerText.setBounds(1200, 625,400,50);
		destroyerText.setForeground(Color.WHITE);
		destroyerText.setFont(new Font("Arial", Font.BOLD, 20));
		destroyerText.setText("Number of Destroyers :");
		
		battleshipText = new JLabel();
		battleshipText.setBounds(900, 150,400,50);
		battleshipText.setForeground(Color.WHITE);
		battleshipText.setFont(new Font("Arial", Font.BOLD, 20));
		battleshipText.setText("Number of Battleships :");
		
		//JComboboxes
        String[] sizes = { "6", "8", "10", "12", "14", "16", "20", "22", "30" };
        gridSizeBox = new JComboBox(sizes);
		gridSizeBox.setSelectedIndex(2);
		gridSizeBox.setBounds(260,65,50,25);
		gridSizeBox.addActionListener(this);
		
		String[] quantityAircraftCarrier = { "0", "1","2","3","4","5"};
        nb_AIRCRAFT_CARRIER = new JComboBox(quantityAircraftCarrier);
		nb_AIRCRAFT_CARRIER.setSelectedIndex(1);
		nb_AIRCRAFT_CARRIER.setBounds(580,400,50,25);
		nb_AIRCRAFT_CARRIER.addActionListener(this);
		
		String[] quantityBattleship = { "0", "1","2","3","4","5"};
        nb_BATTLESHIP = new JComboBox(quantityBattleship);
        nb_BATTLESHIP.setSelectedIndex(1);
        nb_BATTLESHIP.setBounds(1000,400,50,25);
        nb_BATTLESHIP.addActionListener(this);
        
		String[] quantityCruiser = { "0", "1","2","3","4","5","6"};
        nb_CRUISER = new JComboBox(quantityCruiser);
		nb_CRUISER.setSelectedIndex(1);
		nb_CRUISER.setBounds(450,790,50,25);
		nb_CRUISER.addActionListener(this);
		
		String[] quantitySubmarine = { "0", "1","2","3","4","5","6"};
        nb_SUBMARINE = new JComboBox(quantitySubmarine);
		nb_SUBMARINE.setSelectedIndex(1);
		nb_SUBMARINE.setBounds(900,790,50,25);
		nb_SUBMARINE.addActionListener(this);
		
		String[] quantityDestroyer = { "0", "1","2","3","4","5","6","7","8" };
        nb_DESTROYER = new JComboBox(quantityDestroyer);
        nb_DESTROYER.setSelectedIndex(1);
        nb_DESTROYER.setBounds(1300,790,50,25);
        nb_DESTROYER.addActionListener(this);
        
        
		
		//Icons
		aircraftCarrierIcon = new ImagePanel("aircraftCarrier.png");
		aircraftCarrierIcon.setPosition(600,300);
		cruiserIcon = new ImagePanel("cruiser.png");
		cruiserIcon.setPosition(480,720);
		submarineIcon = new ImagePanel("submarine.png");
		submarineIcon.setPosition(925,720);
		destroyerIcon = new ImagePanel("destroyer.png");
		destroyerIcon.setPosition(1330,720);
		battleshipIcon = new ImagePanel("battleship.png");
		battleshipIcon.setPosition(1140,300);

		//ajout des composants
        mainPanel.add(createGame);
        mainPanel.add(back);
        mainPanel.add(gridSizeBox);
        mainPanel.add(gridText);
        mainPanel.add(nb_AIRCRAFT_CARRIER);
        mainPanel.add(nb_CRUISER);
        mainPanel.add(nb_SUBMARINE);
        mainPanel.add(nb_DESTROYER);
        mainPanel.add(nb_BATTLESHIP);
        mainPanel.add(aircraftCarrierIcon);
        mainPanel.add(cruiserIcon);
        mainPanel.add(submarineIcon);
        mainPanel.add(destroyerIcon);
        mainPanel.add(battleshipIcon);
        mainPanel.add(aircraftCarrierText);
        mainPanel.add(cruiserText);
        mainPanel.add(submarineText);
        mainPanel.add(destroyerText);
        mainPanel.add(battleshipText);
        this.add(mainPanel);
        this.add(backgroundLabel);
        
        this.setVisible(true); //fenetre visible

    }
    public void actionPerformed (ActionEvent e){
        if (e.getSource() == back) {
			this.dispose();
			backtoMainMenu = true;
        }
        if (e.getSource() == createGame) {
        	
        	if (nbAircraftCarrier != 0 || nbCruiser != 0 || nbSubmarine != 0 || nbDestroyer !=0 || nbBattleship !=0)
        	{
        		playerShips = new ArrayList<Ship>();
        		AIships = new ArrayList<Ship>();
        		
        		for(int i = 0; i<nbAircraftCarrier; i++)
            	{
        			playerShips.add(new Ship(Ship.Type.AIRCRAFT_CARRIER));
                    AIships.add(new Ship(Ship.Type.AIRCRAFT_CARRIER));
            	}
        		
        		for(int i = 0; i<nbBattleship; i++)
            	{
        			playerShips.add(new Ship(Ship.Type.BATTLESHIP));
                    AIships.add(new Ship(Ship.Type.BATTLESHIP));
            	}
        		
        		for(int i = 0; i<nbCruiser; i++)
            	{
        			playerShips.add(new Ship(Ship.Type.CRUISER));
                    AIships.add(new Ship(Ship.Type.CRUISER));
            	}
        		
        		for(int i = 0; i<nbDestroyer; i++)
            	{
        			playerShips.add(new Ship(Ship.Type.DESTROYER));
                    AIships.add(new Ship(Ship.Type.DESTROYER));
            	}
        		
        		for(int i = 0; i<nbSubmarine; i++)
            	{
        			playerShips.add(new Ship(Ship.Type.SUBMARINE));
                    AIships.add(new Ship(Ship.Type.SUBMARINE));
            	}
        		
        		launchGame = true;
    			this.dispose();
        		
        	} else {
        		JOptionPane.showMessageDialog(this, "There must be at least one ship to start the game!");
        	}

        }
        if (e.getSource() == gridSizeBox)
        {
			gridSize = Integer.parseInt((String) gridSizeBox.getSelectedItem());
		}
    	if (e.getSource() == nb_AIRCRAFT_CARRIER)
        {
			nbAircraftCarrier = Integer.parseInt((String) nb_AIRCRAFT_CARRIER.getSelectedItem());
		}
		if (e.getSource() == nb_CRUISER)
        {
			nbCruiser = Integer.parseInt((String) nb_CRUISER.getSelectedItem());
		}
		if (e.getSource() == nb_SUBMARINE)
        {
			nbSubmarine = Integer.parseInt((String) nb_SUBMARINE.getSelectedItem());
		}
		if (e.getSource() == nb_DESTROYER)
        {
			nbDestroyer = Integer.parseInt((String) nb_DESTROYER.getSelectedItem());
		}
		if (e.getSource() == nb_BATTLESHIP)
        {
			nbBattleship = Integer.parseInt((String) nb_BATTLESHIP.getSelectedItem());
		}

}	}

