package game_engine;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

public class Board extends JPanel implements ActionListener
{
	Player p;
	public static Timer time;
	public static int[][] grid;
	ArrayList<Tile> map;
	Image tile1, tile2;
	public static final int PLAYER_WIDTH = 50;
	public static final int PLAYER_HEIGHT = 50;
	public static final int TILE_LENGTH = 50;
	public static final int BOSSLEVEL1 = 5;	//keeps track of which level the boss appears at
	public static final int BOSSLEVEL2 = 9; //keeps track of the second boss's level
	private long prevTime = System.currentTimeMillis();
	private long currentTime;
	public static ArrayList<Bullet> enemyBullets = new ArrayList<Bullet>();
	public static ArrayList<Bullet> playerBullets = new ArrayList<Bullet>();
	private Image bulletImage;
	private Image titleImage;
	private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	Font font;
	Font font2;
	Font font3;
	public int stage = 1;	//keeps track of which level the player is on
	public boolean hasChangedLevel = false;
	ArrayList<BossPart> bParts = new ArrayList<BossPart>();
	Boss boss;
	private long creditTime;
	private boolean hasStarted = false;
	private int creditSpot = -1;
	private String[] creditTitles = {"Lead Programmer", "Character Design", "Matrix Mode Idea", "Collision Detection", "Level Design", "Enemy AI", "Thanks for Playing!"};
	private String[] creditNames = {"Antonio Rivera", "Antonio Rivera", "Alex Lanphere", "Antonio Rivera", "Antonio Rivera", "Antonio Rivera", "Press Enter to Restart"};
	
	public Board()
	{
		map = new ArrayList<Tile>();
		initializeTileImages();
		
		font = new Font("Calibri", Font.BOLD, 18);
		font2 = new Font("Lucida Console", Font.BOLD, 32);
		font3 = new Font("Calibri", Font.PLAIN, 12);
		
		ImageIcon i = new ImageIcon(getClass().getResource("/game_engine/bullet.png"));
		bulletImage = i.getImage();
		
		i = new ImageIcon(getClass().getResource("/game_engine/Title Screen.png"));
		titleImage = i.getImage();
		
		
		p = new Player();
		setFocusable(true);
		addKeyListener(new AL());
		time = new Timer(10, this);
		/*Timer t2 = new Timer(1000, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{

			}
		});
		t2.start();*/
		time.start();

		grid = Levels.level1;
		
		initializeTiles();
		
		stage = 1;
		
		creditTime = System.currentTimeMillis();
	}

	public void actionPerformed(ActionEvent e)
	{
		if(hasStarted)	//only runs the game if the game has started
		{
			if(p.getHP() <= 0)
			{
				System.out.println("Dead");
				stage = 1;
				hasStarted = false;
				enemies.clear();
				if(boss != null)
				{
					for(BossPart b: boss.getParts())
						b.reduceHP(100);	//kills all parts of the boss
					boss = null;
					bParts = new ArrayList<BossPart>();	//fixes the problem of the invincible boss
				}
				enemyBullets.clear();
				playerBullets.clear();
			}
			
			currentTime = System.currentTimeMillis();
			checkCollisions();
			if(currentTime - prevTime > 30)
			{
				repaint();
				prevTime = currentTime;
			}
			p.move();
			
			for(Enemy enem: enemies)
				enem.move();
			
			for(Bullet b: enemyBullets)
				b.move();
			
			for(Bullet b: playerBullets)
				b.move();
			
			for(int i = 0; i < enemies.size(); i++)
			{
				if(!enemies.get(i).isAlive())
				{
					System.out.println(enemies.get(i).getHP());
					enemies.remove(i);
					i--;
				}
			}
			
			//creates the boss
			if(stage == BOSSLEVEL1 && boss == null)
			{
				System.out.println("making a new boss");
				boss = new Boss(bParts);	
			}
			
			if(stage == BOSSLEVEL2 && boss == null)
			{
				boss = new Boss(bParts);
			}
	
			if(boss != null)
			{
				boss.run();
			}
			
			if(enemies.size() == 0 && hasChangedLevel == false)
			{
				if(stage != BOSSLEVEL1 && stage != BOSSLEVEL2)
				{
					for(int row = 0; row < grid.length; row++)
					{
						grid[row][grid[0].length-1] = 4;
					}
					
					System.out.println("Can go to next stage");
					
					hasChangedLevel = true;
				}
				else	//boss stage
				{
					if(!boss.isAlive())
					{
						System.out.println("Boss is dead.");
						for(int row = 0; row < grid.length; row++)
						{
							for(int column = 0; column < grid[0].length; column++)	//gets rid of boss's collision detection
							{
								if(grid[row][column] == 6)
									grid[row][column] = 0;
							}
							for(int r = 0; r < grid.length; r++)	//allows player to advance
							{
								grid[r][grid[0].length-1] = 4;
							}
							
							hasChangedLevel = true;
						}
					}
				}
			}
			
			checkCollisions();
			checkCollisions();//running this twice prevents the character from partially phasing through the walls (visually)
		}
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		
		Graphics2D g2d = (Graphics2D)g;
		
		if(!hasStarted && p.getHP() > 0)
		{
			g2d.drawImage(titleImage, 0, 0, null);
		}
		
		else
		{
			if(stage == BOSSLEVEL2 + 1)
			{
				g2d.setFont(font2);
				if(System.currentTimeMillis() - creditTime > 2500)
				{
					if(creditSpot < creditTitles.length - 1)	//assumes creditTitles and creditNames are the same length
					{
						creditSpot++;
						creditTime = System.currentTimeMillis();
					}
					else
					{
						hasStarted = false;
					}
				}
				
				g2d.drawString(creditTitles[creditSpot], 380, 390);
				g2d.drawString(creditNames[creditSpot], 370, 420);
			}
			
			else
			{
				for(int i = 0; i < map.size(); i++)
				{
					g2d.drawImage(map.get(i).getImage(), map.get(i).getX(), map.get(i).getY(), null);
				}
				
				g2d.drawImage(p.getImage(), p.getX(), p.getY(), null);
				
				for(Enemy e: enemies)
				{
					g2d.drawImage(e.getImage(), e.getX(), e.getY(), null);
				}
				
				for(Bullet b: enemyBullets)
				{
					g2d.drawImage(bulletImage, b.getX(), b.getY(), null);
				}
				
				for(Bullet b: playerBullets)
				{
					g2d.drawImage(bulletImage, b.getX(), b.getY(), null);
				}
				
				if(boss != null && boss.isAlive())
				{
					for(BossPart bp: boss.getParts())
					{
						g2d.drawImage(bp.getImage(), bp.getX(), bp.getY(), null);
					}
				}
				
				displayStats(g2d);
			}
		}
		
		if(p != null && p.getHP() <= 0)
		{
			g2d.setFont(font2);
			g2d.setColor(new Color(153, 0, 18));
			g2d.drawString("Press Enter to Restart", 300, 400);
		}
	}
	
	public void initializeTileImages()
	{
		//sets up the images
		ImageIcon i = new ImageIcon(getClass().getResource("/game_engine/Tile1.png"));
		tile1 = i.getImage();
		i = new ImageIcon(getClass().getResource("/game_engine/Tile2.png"));
		tile2 = i.getImage();
	}
	
	public void checkCollisions()	//"a posteriori" collision detection
	{
		int overlapX, overlapY;
		
		//checks if the player can advance to the next stage
		if(enemies.size() == 0 && (grid[(int)p.getTile1().getY()][(int)p.getTile1().getX()] == 4 || grid[(int)p.getTile2().getY()][(int)p.getTile2().getX()] == 4 || grid[(int)p.getTile3().getY()][(int)p.getTile3().getX()] == 4 || grid[(int)p.getTile4().getY()][(int)p.getTile4().getX()] == 4))
		{
			stage++;
			enemyBullets.clear();	//makes sure bullets don't remain on the screen when changing screens
			playerBullets.clear();	//same as above
			if(stage == BOSSLEVEL1)	//gives the player extra health for fighting the boss
				p.setHP(200);	//gives the player maximum health for fighting the boss
			if(stage == BOSSLEVEL1 + 1)
			{
				if(p.getHP() > 100)
					p.setHP(200);
				else
					p.setHP(100);
			}
			if(stage == BOSSLEVEL2) //gives the player extra health for fighting the boss
			{
				if(p.getHP() > 100)
					p.setHP(200);
				else
					p.setHP(100);
			}
			if(stage == BOSSLEVEL1 + 1)	//gets rid of the boss
				boss = null;
			if(stage == BOSSLEVEL2 + 1)
				boss = null;
			hasChangedLevel = false;
			setLevel();
			clearTiles();
			initializeTiles();
			p.setX(1);
		}
		
		//top left corner check
		if(grid[(int)p.getTile1().getY()][(int)p.getTile1().getX()] != 0)	//collision occurred
		{
			overlapX = ((int)p.getTile1().getX() * TILE_LENGTH + PLAYER_WIDTH) - p.getX();
			overlapY = ((int)p.getTile1().getY() * TILE_LENGTH + PLAYER_HEIGHT) - p.getY();
			
			if(overlapX < overlapY)
				p.adjustX(overlapX);
			else
			{
				p.adjustY(overlapY);
				p.setDY(0);	//since the character hit its head on the ceiling, its y velocity should go to 0
			}
		}
		
		//top right corner check
		if(grid[(int)p.getTile2().getY()][(int)p.getTile2().getX()] != 0)	//collision occurred
		{
			overlapX = p.getX() + PLAYER_WIDTH - (int)p.getTile2().getX() * TILE_LENGTH;
			overlapY = ((int)p.getTile2().getY() * TILE_LENGTH + TILE_LENGTH) - p.getY();
			
			if(overlapX < overlapY)
				p.adjustX(-overlapX);
			else
			{
				p.adjustY(overlapY);
				p.setDY(0);	//since the character hit its head on the ceiling, its y velocity should go to 0
			}
		}
		
		//bottom left corner check
		if(grid[(int)p.getTile3().getY()][(int)p.getTile3().getX()] != 0)	//collision occurred
		{
			//p.setHasJumped(false);	//allows the player to jump again once they collide with the ground
			overlapX = (int)p.getTile3().getX() * TILE_LENGTH + PLAYER_WIDTH - p.getX();
			overlapY = p.getY() + PLAYER_HEIGHT - ((int)p.getTile3().getY() * TILE_LENGTH);
			
			if(overlapX < overlapY)
				p.adjustX(overlapX);
			else
				p.adjustY(-overlapY);
		}
		
		//bottom right corner check
		if(grid[(int)p.getTile4().getY()][(int)p.getTile4().getX()] != 0)	//collision occurred
		{
			//p.setHasJumped(false);	//allows the player to jump again once they collide with the ground
			overlapX = (p.getX() + PLAYER_WIDTH) - (int)p.getTile4().getX() * TILE_LENGTH;
			overlapY = (p.getY() + PLAYER_HEIGHT) - ((int)p.getTile4().getY() * TILE_LENGTH);
			
			if(overlapX < overlapY)
				p.adjustX(-overlapX);
			else
				p.adjustY(-overlapY);
		}
		
		//problem in the "if" statement below
		if(grid[(int)p.getTile3().getY()][(int)p.getTile3().getX()] != 0 || grid[(int)p.getTile4().getY()][(int)p.getTile4().getX()] != 0)
			p.setHasJumped(false);
		
		if(grid[(int)p.getTile4().getY()][(int)p.getTile4().getX()] == 0 && grid[(int)p.getTile3().getY()][(int)p.getTile3().getX()] == 0)
		{
			p.setHasJumped(true);
		}
		
		//enemy-shot bullet collision detection
		for(int i = 0; i < enemyBullets.size(); i++)
		{
			if(p.getRect().contains(enemyBullets.get(i).getPoint()))	//calculates damage and removes the bullet if it hits something
			{
				p.reduceHP(10);
				p.setInvulnerable(true);
				enemyBullets.remove(i);
				i--;	//ensures the loop doesn't skip any enemyBullets
				continue;	//prevents an IndexOutOfBoundsException
			}
			
			try
			{
				if(grid[enemyBullets.get(i).getY()/50][enemyBullets.get(i).getX()/50] == 1)	//removes enemyBullets that hit a solid object
				{
						enemyBullets.remove(i);
						i--;
				}
			}
			catch(IndexOutOfBoundsException e)	//if a bullet ends up out of the grid, remove it
			{
				enemyBullets.remove(i);
				i--;
			}
		}
		
		//player-shot bullet collision detection
		for(int i = 0; i < playerBullets.size(); i++)
		{
			for(int j = 0; j < enemies.size(); j++)	//loops through the enemies
			{
				if(enemies.get(j).getRect().contains(playerBullets.get(i).getPoint()))	//calculates damage to the enemy and removes the bullet if it hits an enemy
				{
					enemies.get(j).reduceHP(10);	//for some reason, each bullet counts as hitting the enemy twice
					playerBullets.remove(i);
					i--;	//ensures the loop doesn't skip any playerBullets
					break;	//prevents an IndexOutOfBoundsException
				}
			}
			
			if(i < 0)	
				continue;
			
			try
			{
				if(grid[playerBullets.get(i).getY()/50][playerBullets.get(i).getX()/50] == 1)	//removes enemyBullets that hit a solid object
				{
						playerBullets.remove(i);
						i--;
				}
			}
			catch(IndexOutOfBoundsException e)	//if a bullet ends up out of the grid, remove it
			{
				playerBullets.remove(i);
				i--;
			}
		}	//end of player bullet collision detection
		
		//boss collision detection
		if(boss != null && boss.isAlive())
		{
			for(int i = 0; i < playerBullets.size(); i++)
			{
				for(int j = 0; j < boss.getParts().size(); j++)
				{
					if(boss.getParts().get(j).getRect().contains(playerBullets.get(i).getPoint()))
					{
						boss.getParts().get(j).reduceHP(10);
						playerBullets.remove(i);
						i--;
						break;
					}
				}
			}
		}
		
		//enemy collision detection
		if(!p.isInvulnerable())
		{
			for(Enemy e: enemies)
			{
				correctOverlap(p, e);
			}
		}
	}	//end of collision detection method
	
	public void displayStats(Graphics2D g2d)
	{
		//g2d.drawString("HP: " + p.getHP(), 100, 100); backup
		g2d.setColor(Color.WHITE);
		g2d.setFont(font);
		g2d.drawString("HP: " + p.getHP(), 30, 15);
		
		g2d.setColor(Color.GREEN);
		if(p.getHP() <= 30)
			g2d.setColor(Color.RED);
		g2d.fillRect(100, 5, 2*p.getHP(), 15);
		
		g2d.setColor(Color.WHITE);
		if(p.getBTLevel() > 0)
			g2d.fillRect(100, 25, p.getBTLevel(), 10);
		
		g2d.setFont(font3);
		g2d.drawString("Matrix Mode", 20, 35);
	}
	
	public void correctOverlap(Player p, Enemy e)
	{
		Rectangle r1 = p.getRect();
		Rectangle r2 = e.getRect();
		
		if(!r1.intersects(r2))
			return;
		
		if(r1.x > r2.x)	//r1 to the right
		{
			if(r1.y > r2.y)	//r1 above
			{
				if(r2.x + r2.width - r1.x > r1.y + r1.height - r2.y)
				{
					p.adjustY(-(r1.y + r1.height - r2.y));
					p.reduceHP(10);
					if(!p.isInvulnerable())
						p.setInvulnerable(true);
				}
				else
				{
					p.adjustX(r2.x + r2.width - r1.x);
					p.reduceHP(10);
					if(!p.isInvulnerable())
						p.setInvulnerable(true);
				}
			}
			else	//r1 below
			{
				if(r2.x + r2.width - r1.x > r1.y - (r2.y + r2.height))
				{
					p.adjustY(r1.y - (r2.y + r2.height));
					p.reduceHP(10);
					if(!p.isInvulnerable())
						p.setInvulnerable(true);
				}
				else
				{
					p.adjustX(r2.x + r2.width - r1.x);
					p.reduceHP(10);
					if(!p.isInvulnerable())
						p.setInvulnerable(true);
				}
			}
		}
		else	//r1 to the left
		{
			if(r1.y > r2.y)	//r1 above
			{
				if(r1.x + r1.width - r2.x > r1.y + r1.height - r2.y)
				{
					p.adjustY(-(r1.y + r1.height - r2.y));
					p.reduceHP(10);
					if(!p.isInvulnerable())
						p.setInvulnerable(true);
				}
				else
				{
					p.adjustX(-(r1.x + r1.width - r2.x));
					p.reduceHP(10);
					if(!p.isInvulnerable())
						p.setInvulnerable(true);
				}
			}
			else //r1 below
			{
				if(r1.x + r1.width - r2.x > r2.y + r2.height - r1.y)
				{
					p.adjustY(r2.y + r2.height - r1.y);
					p.reduceHP(10);
					if(!p.isInvulnerable())
						p.setInvulnerable(true);
				}
				else
				{
					p.adjustX(-(r1.x + r1.width - r2.x));
					p.reduceHP(10);
					if(!p.isInvulnerable())
						p.setInvulnerable(true);
				}
			}
		}
	}
	
	public void initializeTiles()
	{
		for(int rows = 0; rows < grid.length; rows++)
		{ 
			for(int columns = 0; columns < grid[0].length; columns++)
			{
				if(grid[rows][columns] == 1)
					map.add(new Tile(tile1, columns*50, rows*50));
				if(grid[rows][columns] == 2)
				{
					enemies.add(new Enemy(columns*50, rows*50 + 10, grid));	//the +10 adjusts for the size of the enemy (40)
					grid[rows][columns] = 0;	//resets the space to blank (air) so that the collision detection functions correctly
				}
				if(grid[rows][columns] == 3)
				{
					enemies.add(new JumpingEnemy(columns*50, rows*50 + 10, grid));
					grid[rows][columns] = 0;
				}
				if(grid[rows][columns] == 6)
				{
					bParts.add(new BossPart(columns*50, rows*50));
				}
			}
		}
	}
	
	public void reset()	//something wrong with this method... messes up the timer
	{
		map = new ArrayList<Tile>();
		initializeTileImages();
		
		font = new Font("Calibri", Font.BOLD, 18);
		font2 = new Font("Lucida Console", Font.BOLD, 32);
		
		ImageIcon i = new ImageIcon(getClass().getResource("/game_engine/bullet.png"));
		bulletImage = i.getImage();
		
		i = new ImageIcon(getClass().getResource("/game_engine/TitleScreenPlaceholderImage.png"));
		titleImage = i.getImage();
		
		
		p = new Player();
		setFocusable(true);
		addKeyListener(new AL());
		time = new Timer(10, this);

		time.start();

		grid = Levels.level1;
		
		initializeTiles();
		
		stage = 1;
		
		creditTime = System.currentTimeMillis();
		prevTime = System.currentTimeMillis();
		
		enemyBullets = new ArrayList<Bullet>();
		playerBullets = new ArrayList<Bullet>();
		enemies = new ArrayList<Enemy>();
		bParts = new ArrayList<BossPart>();
		hasStarted = false;
		creditSpot = -1;
	}
	
	public void clearTiles()
	{
		/*for(int i = 0; i < map.size(); i++)
		{
			map.remove(i);
			i--;
		}*/
		
		map.clear();
	}
	
	public void setLevel()
	{
		grid = Levels.get(stage);
	}
	
	private class AL extends KeyAdapter
	{
		public void keyPressed(KeyEvent e)
		{
			if(hasStarted)
				p.keyPressed(e);
			else
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					Levels.resetLevels();	//ensures that the enemies get initialized
					hasStarted = true;
					hasChangedLevel = false;
					creditSpot = -1;
					enemies = new ArrayList<Enemy>();
					
					//restarts the game if the credits are done and the player presses enter
					map = new ArrayList<Tile>();
					initializeTileImages();
					
					font = new Font("Calibri", Font.BOLD, 18);
					font2 = new Font("Lucida Console", Font.BOLD, 32);
					
					ImageIcon i = new ImageIcon(getClass().getResource("/game_engine/bullet.png"));
					bulletImage = i.getImage();
					
					i = new ImageIcon(getClass().getResource("/game_engine/Title Screen.png"));
					titleImage = i.getImage();
					
					
					p = new Player();
					playerBullets = new ArrayList<Bullet>();
					setFocusable(true);
					//addKeyListener(new AL()); removing this solves the double-damage problem with bullets
					
					time.setDelay(10);
					time.start();

					grid = Levels.level1;

					initializeTiles();
					
					stage = 1;
					
					creditTime = System.currentTimeMillis();
				}
			}
		}
		
		public void keyReleased(KeyEvent e)
		{
			if(hasStarted)
				p.keyReleased(e);
		}
	}
}
