package game_engine;

import java.awt.Image;

public class Tile
{
	private Image i;
	int x, y;
	
	public Tile(Image i, int x, int y)
	{
		this.i = i;
		this.x = x;
		this.y = y;
	}
	
	public Image getImage()
	{
		return i;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
}
