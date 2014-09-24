package game_engine;

import java.util.ArrayList;

public class Boss
{
	private boolean alive = true;
	private int segment = 0;
	private long previousTime;
	private ArrayList<BossPart> bossParts;
	
	public Boss(ArrayList<BossPart> bp)
	{
		alive = true;
		bossParts = bp;
		previousTime = System.currentTimeMillis();
	}
	
	public void run()
	{
		for(int i = 0; i < bossParts.size(); i++)
		{
			if(bossParts.get(i).getHP() > 0) break;

			if(i == bossParts.size() - 1) //the boss is dead
			{
				bossParts.clear();
				alive = false;
			}
		}//end of for loop
		
		if(alive && System.currentTimeMillis() - previousTime > 300) //runs every 300 milliseconds
		{
			segment = (int)(Math.random() * bossParts.size());
			bossParts.get(segment).shoot();	//only one random segment shoots
			previousTime = System.currentTimeMillis();
		}	
	}

	public boolean isAlive()
	{
		return alive;
	}
	
	public ArrayList<BossPart> getParts()
	{
		return bossParts;
	}
}
