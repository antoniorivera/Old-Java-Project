package game_engine;

import javax.swing.JFrame;

public class Window
{
	static JFrame frame;
	public static void main(String[] args)
	{
		frame = new JFrame("Pointless Conflict");
		frame.add(new Board());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 800);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
	}
	
	public static void setTitle(String s)
	{
		frame.setTitle(s);
	}
}