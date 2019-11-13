import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;


@SuppressWarnings("serial")
public class Settings extends JFrame implements ActionListener, ItemListener
{   
	public static boolean autosaving = false;
    private JPanel mainPanel;
    private JButton back;
    private JLabel backgroundLabel;
    private ImageIcon backgroundImage;
    private JButton mute;
    private ImagePanel controls = new ImagePanel("controls.png");
    private boolean musicActive = true;
    private ImageIcon muteIcon = new ImageIcon("assets/muteIcon.png");
    private ImageIcon unmuteIcon = new ImageIcon("assets/unmuteIcon.png");
    private ImageIcon checkboxIcon = new ImageIcon("assets/checkboxIcon.png");
    private ImageIcon checkboxIconSelected = new ImageIcon("assets/checkboxIconSelected.png");
    private JCheckBox autosave;
	public boolean backToMainMenu = false;

    
    public Settings()
    {   
        setTitle("Battleship");
		setSize(Main.WRES,Main.HRES);
		setLocation(0,0);
		setResizable(false); //non redimensionable
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //permet de fermer la fenêtre
        setExtendedState(JFrame.MAXIMIZED_BOTH); //mode plein écran

        backgroundImage = new ImageIcon("assets/newGameBackground.png");
        backgroundLabel = new JLabel(backgroundImage);
        
        
        mainPanel = new JPanel();
        mainPanel.setBounds(0,0,Main.WRES,Main.HRES);
		mainPanel.setLayout(null);
        mainPanel.setOpaque(false);
        
        
        
        back = new JButton("Back to Menu");
        back.setBounds(Main.WRES/2-450,Main.HRES/2+400,200,50);
        back.setFont(new Font("Arial", Font.BOLD, 20));
		back.setBackground(new Color(0,0,0));
		back.setForeground(Color.white);
		back.addActionListener(this);
        
		//checkbox
		autosave = new JCheckBox("Auto-Save");
		autosave.setSelected(false);
		autosave.setBounds(Main.WRES/2-850,Main.HRES/2-100,500,200);
		autosave.setFont(new Font("Arial", Font.BOLD, 40));
		autosave.setIcon(checkboxIcon);
		autosave.setSelectedIcon(checkboxIconSelected);
		autosave.setForeground(Color.white);
		autosave.setOpaque(false);
		autosave.addItemListener(this);
		
		
        mute = new JButton(muteIcon);
        mute.setBounds(100,700,300,300);
		mute.setBackground(new Color(0,0,0));
		mute.setForeground(Color.white);
		mute.addActionListener(this);
        
		controls.setPosition(Main.WRES/2+100, Main.HRES/2);
     
		mainPanel.add(autosave);
		mainPanel.add(controls);
        mainPanel.add(back);        
        mainPanel.add(mute);
        this.add(mainPanel);
        this.add(backgroundLabel);
        
        this.setVisible(true); //fenêtre visible

    }
    public void actionPerformed (ActionEvent e){
        if (e.getSource() == back) {
			this.dispose();
			backToMainMenu = true;
        }
        
        if (e.getSource() == mute) {
        	if (musicActive) {
        		Main.clip.stop();
        		musicActive = false;
        		mute.setIcon(unmuteIcon);
        		repaint();
        	} else {
        		Main.clip.loop(Clip.LOOP_CONTINUOUSLY);
        		musicActive = true;
        		mute.setIcon(muteIcon);
        		repaint();
        	}
        }
    }
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == autosave)
		{
			autosaving = autosaving ? false : true;
		}
		
	}

}
