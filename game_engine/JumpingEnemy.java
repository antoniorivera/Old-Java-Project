package game_engine;

import java.awt.Point;

public class JumpingEnemy extends Enemy
{
	private int dy;
	private boolean hasJumped = false;
	private boolean jumpReady = true;
	private long jumpTimer;
	int overlapX, overlapY;
	
	public JumpingEnemy(int newX, int newY, int[][] grid)
	{
		super(newX, newY, grid);
		dy = 0;
	}
	
	public Point getTile1()	//returns the tile index of the tile in which the top left corner of the player resides
	{
		return new Point(x/50, y/50);
	}
	
	public Point getTile2()	//returns the tile index of the tile in which the top right corner of the player resides
	{
		return new Point((x + WIDTH - 1)/50, y/50);
	}
	
	public void move()	//overrides the move method
	{
		if(jumpReady)
		{
			y -= 5;
			dy = -20;
			hasJumped = true;
			jumpReady = false;
			shoot();
		}
		
		y += dy;
		
		if(hasJumped && dy < 10)
		{
			dy++;
		}
		
		else if(!hasJumped)
		{
			dy = 0;
		}
		
		if(hasJumped)
		{
			if(grid[(y+HEIGHT)/50][(x+WIDTH)/50] == 1)
			{
				y = (y/50)*50;
				hasJumped = false;
				jumpTimer = System.currentTimeMillis();
			}
			
		}
		
		//jump collision detection
		if(grid[(int)getTile1().getY()][(int)getTile1().getX()] != 0)	//collision occurred
		{
			overlapX = ((int)getTile1().getX() * 50 + 50) - getX();
			overlapY = ((int)getTile1().getY() * 50 + 50) - getY();
			
			if(overlapX < overlapY)
				x += overlapX;
			else
			{
				y += overlapY;
				dy = 0;	//since the enemy hit its head on the ceiling, its y velocity should go to 0
			}
		}
		
		//top right corner check
		if(grid[(int)getTile2().getY()][(int)getTile2().getX()] != 0)	//collision occurred
		{
			overlapX = getX() + WIDTH - (int)getTile2().getX() * 50;
			overlapY = ((int)getTile2().getY() * 50 + 50) - getY();
			
			if(overlapX < overlapY)
				x -= overlapX;
			else
			{
				y += overlapY;
				dy = 0;	//since the character hit its head on the ceiling, its y velocity should go to 0
			}
		}
		
		if(!hasJumped && System.currentTimeMillis() - jumpTimer > 2000)
			jumpReady = true;
		
		super.move();
	}
}
