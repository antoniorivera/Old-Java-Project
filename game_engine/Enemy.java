package game_engine;

import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class Enemy
{
	protected int x = 100;
	protected int y = 100;
	private Image pic;
	private int movement = -3;
	protected int[][] grid;
	private long pastTime;
	private int health = 30;
	
	public final int WIDTH = 40;	//in the getSquare1(), getSquare2() etc... methods I subtract one from the width to get the square that the character is in, not the one they are touching on the right
	public final int HEIGHT = 40;	//for collision detection to work, the character's "feet" need to be "in" the square they are standing on
	
	public Enemy(int newX, int newY, int[][] grid)
	{
		this.x = newX;
		this.y = newY;
		this.grid = grid;
		
		ImageIcon i = new ImageIcon(getClass().getResource("/game_engine/enemy.png"));
		pic = i.getImage(); 
		
		health = 30;
		
		pastTime = System.currentTimeMillis();
	}
	
	public void move()
	{
		if(movement < 0)
		{
			if(grid[y/50][(x+movement)/50] != 0 || grid[(y+HEIGHT)/50][(x+movement)/50] == 0)
				changeDirection();
		}
		else//movement > 0, moving right
		{
			if(grid[y/50][(x+movement+WIDTH)/50] != 0 || grid[(y+HEIGHT)/50][(x+movement+WIDTH)/50] == 0)
				changeDirection();
		}
		x += movement;
		
		if(canShoot())	//shoots at 2 times per second
			shoot();
	}
	
	private void changeDirection()	//meets the "private method quota"
	{
		movement *= -1;
	}
	
	public Image getImage()
	{
		return pic;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public void shoot()
	{
		int bx;
		int by;
		if(movement < 0)
			bx = x - 1;
		else
			bx = x + WIDTH + 1;
		by = y + HEIGHT/2;
		Board.enemyBullets.add(new Bullet(bx, by, movement/3));
	}
	
	public void reduceHP(int damage)
	{
		health -= damage;
	}
	
	public Rectangle getRect()
	{
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}
	
	public boolean isAlive()
	{
		if(health > 0)
			return true;
		else 
			return false;
	}
	
	public void adjustX(int delta)
	{
		x += delta;
	}
	
	public int getHP()
	{
		return health;
	}
	
	public void adjustY(int delta)
	{
		y += delta;
	}
	
	private boolean canShoot()	//another private method
	{
		if(System.currentTimeMillis() - pastTime > 500)
		{
			pastTime = System.currentTimeMillis();
			return true;
		}
		else
			return false;
	}
}
