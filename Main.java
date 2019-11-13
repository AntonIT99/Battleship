import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Main 
{
    public static int WRES = 1920;
    public static int HRES = 1080; //TODO : ajouter une possibilité de choix de la résolution d'écran
    public static Clip clip;
    

    public static void main(String[] args)
    {
    	try {
    		//lancement de la musique
            URL url = Main.class.getClassLoader().getResource("assets/music.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            System.setProperty("sun.java2d.opengl", "true"); // active l'accélération matérielle >> meilleures performances
            
            boolean isProgramRunning = true;
            
            while (isProgramRunning) { 
	            MainMenu menu = new MainMenu(); //Game(10); //TODO: replace later with new MainMenu(); when testing is finished
	            while (!menu.createGame && !menu.load && !menu.goToSettings) {
	            	Thread.yield();
	            }
	            if (menu.createGame) {
	            	while (!menu.newGameMenu.launchGame && !menu.newGameMenu.backtoMainMenu) { //attends que le joueur lance une partie depuis le menu
	                	Thread.yield();
	                }
	            	if (menu.newGameMenu.launchGame)
	            	{
	            		//Nota Bene : New Game ne peut etre lancé que depuis le Main pour des raisons de paramétrage graphique
		            	Game game = new Game(menu.newGameMenu.gridSize, menu.newGameMenu.playerShips, menu.newGameMenu.AIships, null);
		            	
		            	while (game.gameIsRunning) { //attends que le joueur quitte la partie
		                	Thread.yield();
		                }
		            	clip.setMicrosecondPosition(0);
	            	}
	            	
	            } else if (menu.load) {
	            	
	            	Save savegame = new Save();
		            Game game = new Game(savegame.loadGridSize(), savegame.loadPlayerShips(), savegame.loadAIShips(), savegame);
		            	
		            while (game.gameIsRunning) { //attends que le joueur quitte la partie
		                Thread.yield();
		            }
		            clip.setMicrosecondPosition(0);
	            	
	            } else if (menu.goToSettings) {
	            	Settings settings = new Settings();
	            	while (!settings.backToMainMenu) { //attends que le joueur quitte le menu Settings
	                	Thread.yield();
	                }
	            }
            }
            clip.stop();
        }
    	catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
    		throw new RuntimeException(e);
    	}	  
    }
    
    public static int randomInt(int min, int max) //requiert Java 8 pour Random().ints()
    {
		return new Random().ints(min, (max+1)).limit(1).findFirst().getAsInt(); //retourne un entier aléatoire compris entre min et max
    }
}
