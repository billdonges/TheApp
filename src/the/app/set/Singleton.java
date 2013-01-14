package the.app.set;

public class Singleton 
{

	private static int i;
	
	public static void setI(int ii) { i = ii; }
	public static int getI() { return i; }
	
	private static Singleton instance;

	private static class Holder 
	{
		static 
		{
			System.out.println("instance created");
			instance = new Singleton();
		}
		
		public static Singleton getInstance()
		{
			return instance;
		}
	}
	
	private Singleton()
	{
	}
	
	public static Singleton getInstance()
	{
		return Holder.getInstance();
	}
	
	public static String getDateTime()
	{
		return new java.util.Date().toString();
	}
	
}
