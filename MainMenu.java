import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.awt.*;


@SuppressWarnings("serial")
public class MainMenu extends JFrame implements ActionListener
{   
    private JPanel mainPanel;
    private JButton newGame, loadGame, settings, quit;
    private JLabel backgroundLabel;
    private ImageIcon backgroundImage;
    public boolean load = false;
    public boolean createGame = false;
    public boolean goToSettings = false;
    public NewGameInit newGameMenu;
    
    public MainMenu()
    {
        setTitle("Battleship");
		setSize(Main.WRES,Main.HRES);
		setLocation(0,0);
		setResizable(false); //non redimensionable
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //permet de fermer la fenetre
        setExtendedState(JFrame.MAXIMIZED_BOTH); //mode plein ecran
        
        backgroundImage = new ImageIcon("assets/menu.png");
        backgroundLabel = new JLabel(backgroundImage);
        
        mainPanel = new JPanel();
        mainPanel.setBounds(0,0,Main.WRES,Main.HRES);
		mainPanel.setLayout(null);
        mainPanel.setOpaque(false);
        
        
        newGame = new JButton("New Game");
        newGame.setBounds(Main.WRES/2-150,Main.HRES/2-150,300,100);
        newGame.setFont(new Font("Arial", Font.BOLD, 40));
		newGame.setForeground(Color.red);
		newGame.setContentAreaFilled(false);
		newGame.setOpaque(false);
		newGame.addActionListener(this);
        
        loadGame = new JButton("Load Game");
        loadGame.setBounds(Main.WRES/2-150,Main.HRES/2-50,300,100);
        loadGame.setFont(new Font("Arial", Font.BOLD, 40));
		loadGame.setForeground(Color.red);
		loadGame.setContentAreaFilled(false);
		loadGame.setOpaque(false);
		loadGame.addActionListener(this);
        
        settings = new JButton("Settings");
        settings.setBounds(Main.WRES/2-150,Main.HRES/2+50,300,100);
        settings.setFont(new Font("Arial", Font.BOLD, 40));
		settings.setForeground(Color.red);
		settings.setContentAreaFilled(false);
		settings.setOpaque(false);
		settings.addActionListener(this);
        
        quit = new JButton("Quit");
        quit.setBounds(Main.WRES/2-150,Main.HRES/2+150,300,100);
        quit.setFont(new Font("Arial", Font.BOLD, 40));
		quit.setForeground(Color.red);
		quit.setContentAreaFilled(false);
		quit.setOpaque(false);
		quit.addActionListener(this);
        
        this.add(mainPanel);
        mainPanel.add(newGame);
        mainPanel.add(loadGame);
        mainPanel.add(settings);
        mainPanel.add(quit);
        this.add(backgroundLabel);
        
        this.setVisible(true); //fenetre visible
    }
    
    public void actionPerformed (ActionEvent e){
        
        if (e.getSource() == newGame) {
        	this.dispose();
        	newGameMenu = new NewGameInit();
        	createGame = true;
        }
        
        if (e.getSource() == loadGame) {
        	
        	File f = new File("savegame.txt");
        	if (f.isFile()) {
        		this.dispose();
    			load = true;
        	} else {
        		JOptionPane.showMessageDialog(this, "No saved game.");
        	}
        }
        
        if (e.getSource() == settings) {
			this.dispose();
			goToSettings = true;
        }
        
        if (e.getSource() == quit) {
            if (JOptionPane.showConfirmDialog(this, "Quit the Game?", "Quit?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                this.dispose();
            	System.exit(0);
            }
        }
    }
}
