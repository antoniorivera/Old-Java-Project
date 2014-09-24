package game_engine;

import java.awt.Point;

public class Bullet
{
	int xPos;
	int yPos;
	int direction;	//-1 or 1
	
	public Bullet(int newX, int newY, int dir)
	{
		xPos = newX;
		yPos = newY;
		direction = dir;	
	}
	
	public void move()
	{
		xPos += (direction*5);
	}
	
	public Point getPoint()
	{
		return new Point(xPos, yPos);
	}

	public int getX()
	{
		return xPos;
	}
	
	public int getY()
	{
		return yPos;
	}
}
