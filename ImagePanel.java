import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.*;

@SuppressWarnings("serial")
public class ImagePanel extends JPanel implements Locate {
	
	public BufferedImage image;
	
	public ImagePanel(String fileName) {
		try {
			image = ImageIO.read(new File("assets/" + fileName));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		setOpaque(false);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, null);
	}

	@Override
	public void setPosition(int x, int y) //règle la position à partir du centre
	{
		setBounds(x - image.getWidth()/2, y - image.getHeight()/2, image.getWidth(), image.getHeight());
	}

	@Override
	public Point getPosition() //renvoie les coordonnées du centre
	{
		return new Point(getBounds().x + getBounds().width/2, getBounds().y + getBounds().height/2);
	}
	
	
	public void rotate(double angle) 
	{
	    double sin = Math.abs(Math.sin(angle)); 
	    double cos = Math.abs(Math.cos(angle));
	    int width = image.getWidth(); 
	    int height = image.getHeight();
	    int newWidth = (int) Math.floor(width * cos + height * sin);
	    int newhHeigth = (int) Math.floor(height * cos + width * sin);
	    
	    GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	    BufferedImage newImage = config.createCompatibleImage(newWidth, newhHeigth, Transparency.TRANSLUCENT);
	    Graphics2D g = newImage.createGraphics();
	    
	    g.translate((newWidth - width) / 2, (newhHeigth - height) / 2);
	    g.rotate(angle, width / 2, height / 2);
	    g.drawRenderedImage(image, null);
	    g.dispose();
	    
	    image = newImage;
	}
	
	public void resizeImage(int newWidth, int newHeight)
	{
		BufferedImage newImage = new BufferedImage(newWidth, newHeight, image.getType());
		Graphics2D g = newImage.createGraphics();
		
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(image, 0, 0, newWidth, newHeight, 0, 0, image.getWidth(),image.getHeight(), null);
		g.dispose();
		
		image = newImage;
	}
	
}