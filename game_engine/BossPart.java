package game_engine;

import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class BossPart
{
	private int health = 100;
	private int x;
	private int y;
	private Image currentPic;
	private Image pic; 
	private Image deadPic;
	public final int WIDTH = 50;
	public final int HEIGHT = 50;
	private Rectangle rect;
	private int dir;
	
	public BossPart(int x, int y)	
	{		
		health = 100;	
		ImageIcon i = new ImageIcon(getClass().getResource("/game_engine/boss.png"));
		pic = i.getImage(); 
		i = new ImageIcon(getClass().getResource("/game_engine/boss_dead.png"));
		deadPic = i.getImage();
		
		currentPic = pic;
		
		this.x = x;
		this.y = y;
		
		if(x >= 500)
			dir = -1;
		else
			dir = 1;
		
		rect = new Rectangle(x, y, WIDTH, HEIGHT);
	}

	public int getHP()
	{
		return health;
	}

	public void shoot()
	{
		int bx;
		int by;
		if(dir == -1)
			bx = x - 1;
		else //dir == 1
			bx = x + WIDTH + 1;
		by = y + HEIGHT/2;
		Board.enemyBullets.add(new Bullet(bx, by, dir));
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}
	
	public void reduceHP(int damage)
	{
		health -= damage;
		if(health <= 0)
			currentPic = deadPic;
	}
	
	public Rectangle getRect()
	{
		return rect;
	}
	
	public Image getImage()
	{
		return currentPic;
	}
}
