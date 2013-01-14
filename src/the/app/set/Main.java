package the.app.set;

public class Main 
{

	public static void main(String[] args)
	{
		try
		{
			Main m = new Main();
			for (int i = 0; i < 5; i++)
			{
				Main.Test t = m.new Test();
				t.setI(i);
				t.run();
			}
		}
		catch (Exception e)
		{
			System.err.println("exception - message: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private class Test implements Runnable
	{

		private int i;
		
		public void setI(int i) { this.i = i; }
		public int getI() { return this.i; }
		
		public void run() {
			try
			{
				System.out.println("starting " + getI());
				Singleton.getInstance();
				System.out.println("date: "+Singleton.getDateTime());
				
				Singleton.setI(i);
				System.out.println("i is now " + Singleton.getI());
				
			}
			catch (Exception e)
			{
				System.err.println("exception - message: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
}
