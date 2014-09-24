package game_engine;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;

public class Player
{
	private int x = 100;
	private int y = 100;
	private int dx = 0;
	private int dy = 0;
	private double btLevel = 100;
	private Image pic;
	private Image hitPic;
	private Image currentPic;
	private boolean hasJumped = false;
	private boolean jumpReady = true;
	private int health = 100;	//starting health
	private boolean alive = true;
	private boolean invulnerable = false;
	private long pTime;
	private int dir = 1;
	private boolean bulletTime = false;
	
	public final int WIDTH = 50;	//in the getSquare1(), getSquare2() etc... methods I subtract one from the width to get the square that the character is in, not the one they are touching on the right
	public final int HEIGHT = 50;	//for collision detection to work, the character's "feet" need to be "in" the square they are standing on
	
	public Player()
	{
		ImageIcon i = new ImageIcon(getClass().getResource("/game_engine/player.png"));
		pic = i.getImage(); 
		currentPic = pic;
		i = new ImageIcon(getClass().getResource("/game_engine/hitPlayer1.png"));
		hitPic = i.getImage();
		health = 100;
		invulnerable = false;
		bulletTime = false;
		btLevel = 100;
	}
	
	public void keyPressed(KeyEvent e)
	{
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_W && !hasJumped && jumpReady)		
		{
			y -= 5;
			dy = -20;
			hasJumped = true;
			jumpReady = false;
		}
		
		if(key == KeyEvent.VK_A  && x > 0)
		{
			dx = -5;
			dir = -1;
		}
		
		if(key == KeyEvent.VK_D  && x < 1000)
		{
			dx = 5;
			dir = 1;
		}
		
		if(key == KeyEvent.VK_SPACE)
		{
			shoot();
		}
		
		if(key == KeyEvent.VK_SHIFT && btLevel > 0)	//bullet time mechanic
		{
			Board.time.setDelay(20);
			bulletTime = true;
		}
		
		//for testing purposes
		/*if(key == KeyEvent.VK_ENTER)
		{
			System.out.println("Tile 1: " + getTile1().getX() + "," + getTile1().getY());
			System.out.println("Tile 2: " + getTile2().getX() + "," + getTile2().getY());
			System.out.println("Tile 3: " + getTile3().getX() + "," + getTile3().getY());
			System.out.println("Tile 4: " + getTile4().getX() + "," + getTile4().getY());
			System.out.println();
			
			for(int row = 0; row < Levels.level5o.length; row++)
			{
				for(int column = 0; column < Levels.level5o[0].length; column++)
					System.out.print(Levels.level5o[row][column] + "\t");
				System.out.println();
			}
		}*/
	}
	
	public void keyReleased(KeyEvent e)
	{
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_W)
		{
			jumpReady = true;
		}
		
		if(key == KeyEvent.VK_A)
		{
			dx = 0;
		}
		
		if(key == KeyEvent.VK_D)
		{
			dx = 0;
		}
		
		if(key == KeyEvent.VK_SHIFT)	//bullet time mechanic
		{
			Board.time.setDelay(10);
			bulletTime = false;
		}
	}
	
	public int getBTLevel()
	{
		return (int)btLevel;
	}
	
	public void move()
	{
		if(bulletTime && btLevel > 0)
			btLevel--;
		
		if(btLevel <= 0)	//ends bullet time when you run out of time
			Board.time.setDelay(10);
		
		if(!bulletTime && btLevel <= 100)
			btLevel += .2;
		
		x += dx;
		y += dy;
		
		if(hasJumped && dy < 10)
		{
			dy++;
		}
		else if(!hasJumped)
		{
			dy = 0;
		}
		
		if(invulnerable)
		{
			if(System.currentTimeMillis() - pTime > 1000)
			{
				invulnerable = false;
				currentPic = pic;
			}
		}
	}
	
	public Image getImage()
	{
		return currentPic;
	}
	
	public int getX()
	{
		return x;
	}
	
	public void setX(int newX)
	{
		x = newX;
	}
	
	public void setY(int newY)
	{
		y = newY;
	}
	
	public int getY()
	{
		return y;
	}
	
	public void setDX(int a)
	{
		dx = a;
	}
	
	public void setDY(int a)
	{
		dy = a;
	}
	
	public Point getTile1()	//returns the tile index of the tile in which the top left corner of the player resides
	{
		return new Point(x/50, y/50);
	}
	
	public Point getTile2()	//returns the tile index of the tile in which the top right corner of the player resides
	{
		return new Point((x + WIDTH - 1)/50, y/50);
	}

	public Point getTile3()	//returns the tile index of the tile in which the bottom left corner of the player resides
	{
		return new Point(x/50, (y + HEIGHT)/50);
	}
	
	public Point getTile4()	//returns the tile index of the tile in which the bottom right corner of the player resides
	{
		return new Point((x + WIDTH - 1)/50, (y + HEIGHT)/50);
	}
	
	public void adjustX(int delta)
	{
		x += delta;
	}
	
	public void adjustY(int delta)
	{
		y += delta;
	}
	
	public void setHasJumped(boolean input)
	{
		hasJumped = input;
	}
	
	public Rectangle getRect()
	{
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}
	
	public void reduceHP(int damage)
	{
		if(invulnerable)	//protects the player if he is invulnerable
			return;
		
		health -= damage;
		if(health <= 0)
			alive = false;
	}
	
	public int getHP()
	{
		return health;
	}
	
	public void setHP(int h)
	{
		health = h;
	}
	
	public boolean isInvulnerable()
	{
		return invulnerable;
	}
	
	public void shoot()
	{
		int bx;
		int by;
		if(dir == -1)
			bx = x - 1;
		else
			bx = x + WIDTH + 1;
		by = y + HEIGHT/2;
		Board.playerBullets.add(new Bullet(bx, by, dir));
	}
	
	public void setInvulnerable(boolean tf)
	{
		invulnerable = tf;
		if(tf)
			currentPic = hitPic;
		pTime = System.currentTimeMillis();
	}
}
