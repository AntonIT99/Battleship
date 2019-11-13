import javax.swing.*;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

@SuppressWarnings("serial")
public class Game extends JFrame implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, AdjustmentListener, ActionListener
{
	private int size; //taille d'un cot� de la grille en nombre de cases
    private int seconds = 0, minute = 0; //variables pour le timer
    //on utilise ici des LinkedList pour disposer de la m�thode getLast()
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private LinkedList<GridPosition> firePositionsPlayer = new LinkedList(); //positions de tirs du joueur 
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private LinkedList<GridPosition> firePositionsAI = new LinkedList(); //positions de tirs de l'IA
    //la m�thode get() des ArrayList est plus rapide donc plus avantageux pour cette situation
    private ArrayList<Ship> playerShips = new ArrayList<Ship>(); //bateaux du joueur
    private ArrayList<Ship> AIships = new ArrayList<Ship>(); // bateaux de l'IA
    //images
    private ImagePanel water = new ImagePanel("water.png");
    private ImagePanel victoryScreen = new ImagePanel("victory.png");
    private ImagePanel defeatScreen = new ImagePanel("defeat.png");
    private ImagePanel playerTurn = new ImagePanel("playerTurnFrame.png");
    private ImagePanel AITurn = new ImagePanel("AITurnFrame.png");
	private ImagePanel placeShips = new ImagePanel("placeShipFrame.png");
	private ImagePanel globalPanel = new ImagePanel("newGameBackground.png");
	private ImagePanel pauseMenu = new ImagePanel("pause.png");
	private ImagePanel pauseBack = new ImagePanel("back1.png");
	private ImagePanel pauseBackPressed = new ImagePanel("back2.png");
	private ImagePanel pauseSave = new ImagePanel("save1.png");
	private ImagePanel pauseSavePressed = new ImagePanel("save2.png");
	private ImagePanel pauseQuit = new ImagePanel("quit1.png");
	private ImagePanel pauseQuitPressed = new ImagePanel("quit2.png");
    private Grid grid;
    private JLabel timeLabel;
    private JScrollBar scrollbarH, scrollbarW;
    private Point MouseMovement = new Point(); //permet de detecter le mouvement du clic droit par la diff�rentielle avec MouseRPosition
    private Point MouseRPosition = new Point(); //position du clic droit
    private int scrollW = 50, scrollH = 50;
    private int selectedShip = 0; //indice du bateau � placer lors de la phase de placement
    private boolean gameLoaded = false; //=true si on a charg� une sauvegarde
    private boolean draggingGrid = false; //=true lorsque la grille est en train d'�tre boug�e
    private boolean mouseWheel = false; //=true lorsque la molette de la souris vient d'�tre utilis�e
    private boolean playerPreparation = true; // =true durant la phase de placmenet des bateaux, =false apr�s
    private boolean isPlayerTurn = false; //=true si c'est le tour du joueur, =false si c'est le tour de l'IA
    private boolean playerHasFired = false; //=true lorsque le joueur a tir�, sert � d�clencher le changement de tour
    private boolean soundException = false; //=true si les fichiers sons sont manquants ou ne peuvent pas �tre lus
    private boolean shipSpotted = false; //=true lorsque l'IA d�couvre un nouveau bateau. Permet � l'IA de se concentrer sur sa destruction. Devient false une fois celui-ci d�truit.
    private boolean orientationSpotted = false; //=true si l'IA connait l'orientation du bateau rep�r�, false si elle ne la connait pas encore.
    private GridPosition firstPositionHit, lastPositionHit; //Positions repectives du premier tir de l'IA sur un bateau touch�, et du tir r�ussi de l'IA le plus r�cent
    private boolean pause = false; //menu pause activ�/d�sactiv�
    private boolean exitPause = false; //sortie menu pause
    public boolean gameIsRunning = true; //le jeu est actif
    URL url1, url2; //chemin d'acc�s pour les sons
    AudioClip hitSound, missSound; //sons
    
    
    
    public Game(int gridSize, ArrayList<Ship> shipsPlayer, ArrayList<Ship> shipsAI, Save savegame)
    {
    	size = gridSize;
    	playerShips = shipsPlayer;
    	AIships = shipsAI;
    	
    	//parametrage de la fenetre et initialisation des objets graphiques
        setTitle("Battleship");
		setSize(Main.WRES,Main.HRES);
		setLocation(0,0);
		setResizable(false); //non redimensionable
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //permet de fermer la fen�tre
        setExtendedState(JFrame.MAXIMIZED_BOTH); //fenetre taille maximale
        timeLabel = new JLabel();
        timeLabel.setBounds(20,10, 100, 50);
        timeLabel.setForeground(Color.RED);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 32));
		grid = new Grid(size, Main.WRES/2,Main.HRES/2);		
		grid.addMouseListener(this);
		grid.addMouseMotionListener(this);
		water.resizeImage(grid.getPixelSize(), grid.getPixelSize());
		water.setBounds(Main.WRES/2 - grid.getPixelSize()/2, Main.HRES/2 - grid.getPixelSize()/2, grid.getPixelSize(), grid.getPixelSize());
		water.resizeImage(size*100, size*100);
		scrollbarH = new JScrollBar(JScrollBar.VERTICAL, 0, 1, 0, scrollH);
		scrollbarH.setBounds(Main.WRES-35,0, 30, Main.HRES-100);
		scrollbarH.addAdjustmentListener(this);
		scrollbarW = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, scrollW);
		scrollbarW.setBounds(0, Main.HRES-65, Main.WRES-20, 30);
		scrollbarW.addAdjustmentListener(this);
        
    	//ajout des bateaux du joueur sur la grille
    	for(int i = 0; i<playerShips.size(); i++)
    	{
    		playerShips.get(i).draw();
    		grid.add(playerShips.get(i).display, 0);
    	}

        if(savegame != null) //si on charge une sauvegarde, on charge les positions de tir, les bateaux sont deja charg�s
        {
        	firePositionsPlayer = savegame.loadPlayerFirePositions();
        	firePositionsAI = savegame.loadAIFirePositions();
        	gameLoaded = true;
        	
        } else { //sinon l'IA doit placer ses bateaux
        	
        	int nbTentativesPlacement = 1000; //nombre maximal de tentatives de placement pour un bateau. Si ce chiffre est atteint, le bateau ne peut probablement pas etre position� car la grille est trop petite : plus de place.
        	int k; //compteur des tentatives pour un bateau
        	
        	for (int i = 0; i<AIships.size() && gameIsRunning; i++)
            {
        		k = 0;
            	AIships.get(i).setRandomOrientation();
            	GridPosition randomPos;
            	do {
            		k++;
            		randomPos = GridPosition.random(size);
            		AIships.get(i).setPosition(randomPos);
            	} while (!AIships.get(i).canBePlaced(randomPos, AIships, size) && k<nbTentativesPlacement);
            	if (k>=nbTentativesPlacement) //le bateau ne peut pas etre position�, le jeu doit red�marrer
            	{
            		JOptionPane.showMessageDialog(this, "Ship is unable to be placed, no place found! Try with a bigger Grid. The Game will stop.");
            		gameIsRunning = false;
            		this.dispose();
            	}
            	AIships.get(i).setPosition(randomPos);
            	AIships.get(i).place();
        	}
        }
        for (int i = 0; i<AIships.size(); i++)
        {
        	AIships.get(i).draw();
    	}

        //chargement des sons
        if (Main.class.getClassLoader().getResource("assets/explosion.wav") != null && Main.class.getClassLoader().getResource("assets/missed.wav") != null) {
        	url1 = Main.class.getClassLoader().getResource("assets/explosion.wav");
        	url2 = Main.class.getClassLoader().getResource("assets/missed.wav");
        	hitSound= Applet.newAudioClip(url1);
            missSound = Applet.newAudioClip(url2);
        } else {
        	soundException = true;
        }
        
        //d�finition de la position des boutons du menu pause
        pauseMenu.setPosition(Main.WRES/2, Main.HRES/2);
        pauseBack.setPosition(960, 460);
        pauseBack.addMouseListener(this);
        pauseBackPressed.setPosition(960, 460);
        pauseSave.setPosition(960, 580);
        pauseSave.addMouseListener(this);
        pauseSavePressed.setPosition(960, 580);
        pauseQuit.setPosition(960, 700);
        pauseQuit.addMouseListener(this);
        pauseQuitPressed.setPosition(960, 700);
        
		globalPanel.setBounds(0,0,Main.WRES,Main.HRES);
		globalPanel.setLayout(null);
		globalPanel.add(timeLabel);
		globalPanel.add(scrollbarW);
		globalPanel.add(scrollbarH);
		globalPanel.add(grid);
		globalPanel.add(water);
		add(globalPanel);
		setVisible(true);
		
		//Ecouteurs
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		
		//d�marrage du Timer
		Timer timer = new Timer(1000, this);
		timer.start();
		
		//affiche "place your ships" lors d'une nouvelle partie
		if (!gameLoaded) {
			placeShips.setPosition(Main.WRES/2, Main.HRES/2);
			globalPanel.add(placeShips, 0);
			repaint();
			delay(1);
			globalPanel.remove(placeShips);
			repaint();
		}

		//boucle principale du jeu
		while (gameIsRunning) 
		{
			Thread.yield(); //tr�s important, ne pas enlever, assure l'execution des events
			if(playerPreparation)
			{
				if(selectedShip<playerShips.size() && playerShips.get(selectedShip).isPlaced()) //si le bateau est d�j� plac� on passe au bateau suivant en incr�mentant l'indice du bateau � placer
				{
					selectedShip++;
				}
				
				if (selectedShip>=playerShips.size() || gameLoaded)
				{
					playerPreparation = false;
					changeTurn();
				}
			} else {
				if (isPlayerTurn) { //si c'est le tour du joueur
					if (playerHasFired)
					{
						changeTurn();
					}
				
				} else { //si c'est le tour de l'IA
					delay(1);
					GridPosition fire = null;
					
					if (shipSpotted) //si l'IA avait rep�r� un bateau pr�c�mment qui n'est pas encore d�truit, elle va se concentrer sur sa destruction
					{
						if (orientationSpotted) { //on ne connait l'orientation du bateau
							if(firstPositionHit.x == lastPositionHit.x) //l'orientation du bateau est verticale
							{
								GridPosition target = new GridPosition(firstPositionHit.x, 0);
								if(firstPositionHit.y - lastPositionHit.y > 0) {
									target.y = lastPositionHit.y - 1;
								} else if (firstPositionHit.y - lastPositionHit.y < 0) {
									target.y = lastPositionHit.y + 1;
								} else {
									target.y = Main.randomInt(0, size-1);
								}
								if (firePositionsAI.getLast().getState() == GridPosition.HIT && !target.isPositionAlreadyTargeted(firePositionsAI) && target.isIncluded(new GridPosition(0,0), new GridPosition(size-1,size-1))) //si le coup pr�c�dent �tait r�ussi, on tire sur la case suivante de m�me abscisse dans le m�me sens (positif ou n�gatif), si celle ci n'a pas d�j� �t� cibl�e
								{
										fire = new GridPosition(target.x, target.y);
								
								} else if (firePositionsAI.getLast().getState() == GridPosition.MISSED || target.isPositionAlreadyTargeted(firePositionsAI) || !target.isIncluded(new GridPosition(0,0), new GridPosition(size-1,size-1))) { //si le coup pr�c�dent �tait manqu� ou si la case suivante cible a d�j� �t� vis�e (forc�ment manqu�e), on est all� trop loin, on tire sur la case � cot� de la position initiale dans l'autre sens
									
										int Y = 0;
										if(firstPositionHit.y - lastPositionHit.y > 0) {
											Y = firstPositionHit.y + 1;
										} else if (firstPositionHit.y - lastPositionHit.y < 0) {
											Y = firstPositionHit.y - 1;
										} else {
											Y = Main.randomInt(0, size-1);
										}
										fire = new GridPosition (firstPositionHit.x, Y);
										
								} else { //alternative afin d'empecher le programme de planter au cas o� aucune condition pr�c�dente ne venait � etre remplie (ne devrait normalement pas arriver)
									do { //tir al�atoire
										fire = GridPosition.random(size);
										
									} while (fire.isPositionAlreadyTargeted(firePositionsAI)); //v�rification si la position n'a pas d�j� �t� cibl�e
								}
									
							} else if (firstPositionHit.y == lastPositionHit.y) { //l'orientation du bateau est horizontale
								
								GridPosition target = new GridPosition(0, firstPositionHit.y);
								if(firstPositionHit.x - lastPositionHit.x > 0) {
									target.x = lastPositionHit.x - 1;
								} else if (firstPositionHit.y - lastPositionHit.y < 0) {
									target.x = lastPositionHit.x + 1;
								} else {
									target.x = Main.randomInt(0, size-1);
								}
								if (firePositionsAI.getLast().getState() == GridPosition.HIT && !target.isPositionAlreadyTargeted(firePositionsAI) && target.isIncluded(new GridPosition(0,0), new GridPosition(size-1,size-1))) //si le coup pr�c�dent �tait r�ussi, on tire sur la case suivante de m�me abscisse dans le m�me sens (positif ou n�gatif)
								{
										fire = new GridPosition(target.x, target.y);
										
								} else if (firePositionsAI.getLast().getState() == GridPosition.MISSED || target.isPositionAlreadyTargeted(firePositionsAI) || !target.isIncluded(new GridPosition(0,0), new GridPosition(size-1,size-1))) { //si le coup pr�c�dent �tait manqu�, on est all� trop loin, on tire sur la case � cot� de la position initiale dans l'autre sens
									int X = 0;
									if(firstPositionHit.x - lastPositionHit.x > 0) {
										X = firstPositionHit.x + 1;
									} else if (firstPositionHit.x - lastPositionHit.x < 0) {
										X = firstPositionHit.x - 1;
									} else {
										X = Main.randomInt(0, size-1);
									}
									fire = new GridPosition (X, firstPositionHit.y);
									
								} else { //alternative afin d'empecher le programme de planter au cas o� aucune condition pr�c�dente ne venait � etre remplie (ne devrait normalement pas arriver)
									do { //tir al�atoire
										fire = GridPosition.random(size);
										
									} while (fire.isPositionAlreadyTargeted(firePositionsAI)); //v�rification si la position n'a pas d�j� �t� cibl�e
								}
							}
						
						} else { //on ne connait pas encore son orientation
							
							if (!firstPositionHit.allAdjacentPositionsAlreadyTargeted(firePositionsAI, size)) { //si des cases adjacentes n'ont pas encore �t� cibl�es
								do { //tir al�atoire sur une case adjacente
									fire = GridPosition.randomAdjacent(firstPositionHit, size);
									
								} while (fire.isPositionAlreadyTargeted(firePositionsAI)); //v�rification si la position n'a pas d�j� �t� cibl�e
							} else {
								do { //tir al�atoire
									fire = GridPosition.random(size);
									
								} while (fire.isPositionAlreadyTargeted(firePositionsAI)); //v�rification si la position n'a pas d�j� �t� cibl�e
							}
							
						}
						
					} else { //sinon tir al�atoire
						
						do {
							fire = GridPosition.random(size);
							
						} while (fire.isPositionAlreadyTargeted(firePositionsAI)); //v�rification si la position n'a pas d�j� �t� cibl�e
					}
					
					firePositionsAI.add(new GridPosition(fire.x, fire.y));
					
					int hitShip = shipHit(firePositionsAI.getLast(), playerShips); //v�rification du tir : touch� ou manqu�
					
					
					if (hitShip != -1) //si touch�
					{ 
						if (!shipSpotted) //si le bateau vient d'�tre rep�r�, alors on r�cup�re les coordonn�es de ce tir
						{
							shipSpotted = true;
							firstPositionHit = new GridPosition (firePositionsAI.getLast().x, firePositionsAI.getLast().y);
							
						} else { //le bateau �tait d�ja rep�r�.
							
							orientationSpotted = true; //On a donc touch� au minimum 2 positions. On connait donc forc�ment son orientation.
							lastPositionHit = new GridPosition (firePositionsAI.getLast().x, firePositionsAI.getLast().y); //on r�cup�re les coordonn�es de ce tir
						}
						checkIfShipDestroyed(playerShips.get(hitShip), firePositionsAI);
						firePositionsAI.getLast().setState(GridPosition.HIT);
						if(!soundException) hitSound.play();
						
						if (playerShips.get(hitShip).isDestroyed()) { //si le bateau du joueur est coul�, l'IA doit chercher un nouveau bateau. Il n'y a donc plus de bateau rep�r�.
							shipSpotted = false;
							orientationSpotted = false;
						}
						
					} else { //si manqu�
						firePositionsAI.getLast().setState(GridPosition.MISSED);
						if(!soundException) missSound.play();
					}
					firePositionsAI.getLast().drawState(grid); //affichage du tir
					repaint();
					changeTurn();
				}
			}
			if (exitPause) {
				delay(0.2f);
				exitPause = false;
				pause = false;
				globalPanel.remove(pauseMenu);
				globalPanel.remove(pauseBack);
				globalPanel.remove(pauseQuit);
				globalPanel.remove(pauseSave);
				globalPanel.remove(pauseBackPressed);
				globalPanel.remove(pauseQuitPressed);
				globalPanel.remove(pauseSavePressed);
				repaint();
			}
		}
		
		if (!gameIsRunning)
		{
			this.dispose();
		}
		
		timer.stop();
        
    }

	@Override
	public void keyPressed(KeyEvent e) 
	{
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE ) //d�clenchement de l'affichage des boutons du menu pause si le joueur appuie sur echap
		{
			if (!pause) 
			{
				pause = true;
				globalPanel.add(pauseMenu, 0);
				globalPanel.add(pauseBack, 0);
				globalPanel.add(pauseQuit, 0);
				globalPanel.add(pauseSave, 0);
				repaint();
			} else {
				pause = false;
				globalPanel.remove(pauseMenu);
				globalPanel.remove(pauseBack);
				globalPanel.remove(pauseQuit);
				globalPanel.remove(pauseSave);
				globalPanel.remove(pauseBackPressed);
				globalPanel.remove(pauseQuitPressed);
				globalPanel.remove(pauseSavePressed);
				repaint();
			}
		}
	}



	@Override
	public void keyReleased(KeyEvent e) 
	{
		
	}



	@Override
	public void keyTyped(KeyEvent e)
	{
		
	}



	@Override
	public void mouseClicked(MouseEvent e) 
	{
		if (pause && e.getSource() == pauseBack) //bouton back to game du menu pause
		{
			globalPanel.add(pauseBackPressed, 0);
	        repaint();
	        exitPause = true;
	        
		} else if (pause && e.getSource() == pauseSave) { //bouton save du menu pause
			if (!playerPreparation) {
				globalPanel.add(pauseSavePressed, 0);
				try {
					Save.saveGame(size, playerShips, AIships, firePositionsPlayer, firePositionsAI);
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
			
	        repaint();
	        
		} else if (pause && e.getSource() == pauseQuit) { //bouton quitter du menu pause
			
			globalPanel.add(pauseQuitPressed, 0);
			repaint();
			gameIsRunning = false;
			
			
		}
		if (e.getSource() == grid && e.getButton() == MouseEvent.BUTTON1 && !pause) //clic gauche dans la grille
		{
			
			if(playerPreparation && e.getClickCount() == 1)
			{
				if (playerShips.get(selectedShip).canBePlaced(new GridPosition(e.getPoint()), playerShips, size))
				{
					playerShips.get(selectedShip).setPosition(new GridPosition(e.getPoint()));
					playerShips.get(selectedShip).draw();
					playerShips.get(selectedShip).place();
					repaint();
				}
			}
			if (isPlayerTurn && !playerPreparation && !playerHasFired && !pause) //si c'est le tour du joueur
			{
				if (e.getClickCount() == 2) //double clic gauche dans la grille
				{
					GridPosition fire = new GridPosition(e.getPoint());
					if (!fire.isPositionAlreadyTargeted(firePositionsPlayer)) //si la position n'a pas d�j� �t� vis�e
					{
						firePositionsPlayer.add(new GridPosition(fire.x, fire.y));
						
						int hitShip = shipHit(firePositionsPlayer.getLast(), AIships); //v�rification du tir : touch� ou manqu�
						
						if (hitShip != -1) //si touch�
						{
							checkIfShipDestroyed(AIships.get(hitShip), firePositionsPlayer);
							firePositionsPlayer.getLast().setState(GridPosition.HIT);
							if(!soundException) hitSound.play();
						} else { //si manqu�
							firePositionsPlayer.getLast().setState(GridPosition.MISSED);
							if(!soundException) missSound.play();
						}
						firePositionsPlayer.getLast().drawState(grid); //affichage du tir
						repaint();
						playerHasFired = true;
					}
				}
			}
		}
	}



	@Override
	public void mouseEntered(MouseEvent e) 
	{
		
	}



	@Override
	public void mouseExited(MouseEvent e) 
	{
		
	}



	@Override
	public void mousePressed(MouseEvent e) 
	{
		if (e.getButton() == MouseEvent.BUTTON3 && !pause) //clic droit appuy�
		{
			MouseRPosition.x = e.getXOnScreen(); //la grille est deokac� selon la variation des coordonn�es du curseur
			MouseRPosition.y = e.getYOnScreen();
			draggingGrid = true;
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{
		if (e.getButton() == MouseEvent.BUTTON3) //clic droit relach�
		{
			draggingGrid = false; //la grille n'est plus deplac�e
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) 
	{
		if(SwingUtilities.isRightMouseButton(e) && !pause)
		{
			MouseMovement.x = e.getXOnScreen() - MouseRPosition.x;
			MouseMovement.y = e.getYOnScreen() - MouseRPosition.y;
			
			grid.setPosition(grid.getPosition().x + MouseMovement.x, grid.getPosition().y + MouseMovement.y);
			water.setPosition(grid.getPosition().x, grid.getPosition().y);
			scrollbarW.setValue(positionToScrollValue(grid.getPosition().x, scrollW, Main.WRES, grid));
			scrollbarH.setValue(positionToScrollValue(grid.getPosition().y, scrollH, Main.HRES, grid));
			repaint();
			
			MouseRPosition.x = e.getXOnScreen();
			MouseRPosition.y = e.getYOnScreen();
		}
		
		
	}

	@Override
	public void mouseMoved(MouseEvent e) 
	{
		if(playerPreparation && e.getSource() == grid && !pause)
		{

			if (playerShips.get(selectedShip).isPlaceableX(new GridPosition(e.getPoint()),size))
			{
				playerShips.get(selectedShip).setPosition((new GridPosition(e.getPoint())).x, playerShips.get(selectedShip).getPosition().y);
				playerShips.get(selectedShip).draw();
				repaint();
			}
					
			if (playerShips.get(selectedShip).isPlaceableY(new GridPosition(e.getPoint()),size))
			{
				playerShips.get(selectedShip).setPosition(playerShips.get(selectedShip).getPosition().x, (new GridPosition(e.getPoint())).y);
				playerShips.get(selectedShip).draw();
				repaint();
			}	
		}
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) 
	{
		if (!draggingGrid && !pause)
		{
			if (e.getSource() == scrollbarH) 
			{
				grid.setPosition(grid.getPosition().x, scrollValueToPosition(e.getValue(), scrollH, Main.HRES, grid));
				water.setPosition(grid.getPosition().x, grid.getPosition().y);
			}
			if (e.getSource() == scrollbarW) 
			{
				grid.setPosition(scrollValueToPosition(e.getValue(), scrollW, Main.WRES, grid), grid.getPosition().y);
				water.setPosition(grid.getPosition().x, grid.getPosition().y);
			}
			repaint();
		}
		
		
	}

	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		
		//permet d'�viter que l'action s'effectue deux fois pour un seul mouvemet de la molette
		mouseWheel = mouseWheel ? false : true;
		if (mouseWheel && playerPreparation && !pause) 
		{
			playerShips.get(selectedShip).setOrientation(Math.floorMod((playerShips.get(selectedShip).getOrientation() + (int)e.getPreciseWheelRotation()), 4)); //n�cessite Java 8
			playerShips.get(selectedShip).draw();
			repaint();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		//incr�mentation du timer (compte les secondes et les minutes pendant la partie)
		seconds++;
		if (seconds == 60)
		{
			seconds = 0;
			minute++;
		}
		timeLabel.setText(minute + ":" + seconds);
	}
	
	public int shipHit (GridPosition position, ArrayList<Ship> ships) //renvoie l'indice du bateau touch�, renvoie -1 si aucun bateau n'est touch�
	{
		int index = -1;
		for (int i = 0; i<ships.size(); i++) //parcourt la liste des bateaux		
			{
			for (int j = 0; j<ships.get(i).getAllPositions().length; j++) ////parcourt les positions des bateaux de la liste
			{
				if (position.equals(ships.get(i).getAllPositions()[j])) //v�rifie si une position correspond � celle sur laquelle on a tir�
				{
					index = i;
				}
			}
		}
		
		return index;
	}
	
	public void changeTurn() //change de tour
	{
		delay(1);
		grid.removeAll();
		
		int winnerID = checkVictory();
		
		if(winnerID == 1) {//victoire du joueur
			victoryScreen.setPosition(Main.WRES/2, Main.HRES/2);
			globalPanel.add(victoryScreen, 0);
			repaint();
			delay(3);
			globalPanel.remove(victoryScreen);
			this.dispose();
			gameIsRunning = false;
		} else if (winnerID == 2) { // defaite du joueur
			defeatScreen.setPosition(Main.WRES/2, Main.HRES/2);
			globalPanel.add(defeatScreen, 0);
			repaint();
			delay(3);
			globalPanel.remove(defeatScreen);
			this.dispose();
			gameIsRunning = false;
		} else { //pas encore de vainqueur, la partie continue
			
			isPlayerTurn = isPlayerTurn ? false : true; //definit � qui c'est le tour en fonction du tour pr�c�dent
			if (isPlayerTurn) // si c'est maintenant le tour du joueur
			{
				playerTurn.setPosition(Main.WRES/2, Main.HRES/2);
				globalPanel.add(playerTurn, 0);
				repaint();
				delay(2);
				globalPanel.remove(playerTurn);
				
				//active l'affichage des bateaux coul�s de l'IA
				for (int i = 0; i<AIships.size(); i++)
				{
					if (AIships.get(i).isDestroyed())
					{
						grid.add(AIships.get(i).display);
					}
				}
				
				//active l'affichage des positions de tir du joueur
				for (int i = 0; i<firePositionsPlayer.size(); i++)
				{
					firePositionsPlayer.get(i).drawState(grid);
				}
				
				playerHasFired = false; //permet au joueur de tirer
				
			} else { // si c'est maintenant le tour de l'IA
				
				AITurn.setPosition(Main.WRES/2, Main.HRES/2);
				globalPanel.add(AITurn, 0);
				repaint();
				delay(2);
				globalPanel.remove(AITurn);
				
				//active l'affichage des bateaux du joueur
				for (int i = 0; i<playerShips.size(); i++)
				{
					if (playerShips.get(i).isPlaced() == true)
					{
						grid.add(playerShips.get(i).display);
					}
				}
				
				//active l'affichage des positions de tir de l'IA
				for (int i = 0; i<firePositionsAI.size(); i++)
				{
					firePositionsAI.get(i).drawState(grid);
				}
			}
			repaint();
			
			if (Settings.autosaving) {
				try {
					Save.saveGame(size, playerShips, AIships, firePositionsPlayer, firePositionsAI);
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
		}
	}
	
	public int checkVictory() //renvoie 0 si pas encore de gagnant, 1 si le joueur gagne, 2 si l'IA gagne
	{
		boolean isPlayerWinner = true;
		boolean isAIWinner = true;
		
		for (int i = 0; i<playerShips.size(); i++)
		{
			if (!playerShips.get(i).isDestroyed())
			{
				isAIWinner = false;
			}
		}
		
		for (int i = 0; i<AIships.size(); i++)
		{
			if (!AIships.get(i).isDestroyed())
			{
				isPlayerWinner = false;
			}
		}
		if (isPlayerWinner) {
			return 1;
		} else if (isAIWinner) {
			return 2;
		} else {
			return 0;
		}
	}
	
	public static int scrollValueToPosition (int sv, int svmax, int screenRes, Grid g) //convertit une valeur de la scrollbar en position pour la grille
	{
		return ((screenRes - 2 * Grid.LIMIT + g.getPixelSize()) / svmax) * sv + Grid.LIMIT - g.getPixelSize()/2;
	}
	
	public static int positionToScrollValue (int position, int svmax, int screenRes, Grid g) //convertit une position pour la grille en une valeur de la scrollbar
	{
		return (position - (Grid.LIMIT - g.getPixelSize()/2))/((screenRes - 2 * Grid.LIMIT + g.getPixelSize()) / svmax);
	}
	
	public static void delay(float seconds) //effectue une pause d'un certain temps en secondes
	{
		try {
			Thread.sleep((int)(seconds * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void checkIfShipDestroyed(Ship ship, LinkedList<GridPosition> firePositions) //detecte les bateaux coul�s et leur affecte le statut de d�truit
	{
		//initialis� par d�fault avec des valeurs false
		boolean[] hits = new boolean[ship.getAllPositions().length]; //tableau repr�sentant virtuellement les cases du bateau, true signifiant qu'elle est touch�e, false qu'elle ne l'est pas
		for (int i = 0; i<ship.getAllPositions().length; i++)
		{
			for (int j = 0; j<firePositions.size(); j++)
			{
				if(ship.getAllPositions()[i].equals(firePositions.get(j)))
				{
					hits[i] = true; //la i-�me case est touch�e
				}
			}
		}
		boolean allAreHit = true; //valeur qui passe � true si toutes les cases sont touch�es
		
		for (int i = 0; i<ship.getAllPositions().length; i++)
		{
			if (hits[i] != true) //� partir du moment o� au moins une case n'est pas touch�e, alors toutes les cases ne sont pas touch�es
			{
				allAreHit = false;
			}
		}
		
		if(allAreHit) //si toutes les cases sont touch�es, le bateau est d�truit
		{
			ship.destroy();
		}
		
	}
}
