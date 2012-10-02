package the.app.db.mongo;

import java.io.IOException;
import java.net.Socket;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.WriteConcern;

public class MongoFactory 
{

	public static void main(String[] args)
	{
		try
		{
			MongoFactory m = new MongoFactory();
			
			String ip = "192.168.23.112";
			int pt    = 27017;
			
			DB admindb = m.getDatabase(ip, "admin", pt, true, "admin", "dev=horse.play");
			System.out.println("is admindb auth'ed? "+admindb.isAuthenticated());
			
			for (String s : admindb.getMongo().getDatabaseNames())
			{
				System.out.println(s);
				DB tmp = m.getDatabase(admindb.getMongo(), s);
				System.out.println(tmp.getStats());
				System.out.println("  db:  "+s);
				//for (String t : tmp.getCollectionNames())
				//	System.out.println("    "+t);
			}
			System.out.println(" ");

			/*
			for (String s : admindb.getCollectionNames())
				System.out.println(s);
			
			String stuff = "hydra_rlm2357";
			DB rlm2357db = m.getDatabase(admindb.getMongo(), stuff);
			//System.out.println("is rlm2357db auth'ed? "+admindb.isAuthenticated());
			for (String s : rlm2357db.getCollectionNames())
				System.out.println(s);
			*/
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * closes the mongo object inside the db object, then nulls out the db object
	 * @param db
	 */
	public void close(DB db)
	{
		if (db != null)
		{
			db.getMongo().close();
			db = null;
		}
	}
	
	/**
	 * dbLocation should be defined in mongoweb.AppInfo
	 * dbName should be defined in mongoweb.AppInfo
	 * dbPort should be defined in mongoweb.AppInfo
	 * @param dbLocation
	 * @param dbName
	 * @param dbPort
	 * @return DB
	 * @throws Exception 
	 */
	public DB getDatabase(String dbLocation, String dbName, int dbPort, boolean validate, String username, String password) throws Exception
	{
		System.out.println("getDatabase("+dbLocation+","+dbName+","+dbPort+","+validate+","+username+","+password+")");
		Mongo m = getConnection(dbLocation, dbPort);
		DB db = getDatabase(m, dbName);
		if (validate)
			if (!auth(db, username, password))
				throw new Exception("username / password / db can not be authorized");
		return db;
	}
	
	/**
	 * 
	 * @param m
	 * @param dbName
	 * @return
	 */
	protected DB getDatabase(Mongo m, String dbName) 
	{
		return m.getDB(dbName);
	}	
	
	/**
	 * 
	 * @param db
	 * @param username
	 * @param passwd
	 * @return
	 */
	public boolean auth(DB db, String username, String passwd)
	{
		return db.authenticate(username, passwd.toCharArray());
	}
	
	/**
	 * 
	 * @param dbLocation
	 * @param dbPort
	 * @return
	 * @throws Exception
	 */
	public Mongo getConnection(String dbLocation, int dbPort) throws Exception
	{
		Mongo m = new Mongo(dbLocation , dbPort);
		m.setWriteConcern(WriteConcern.SAFE);
		if (!mongoRunningAt(m))
			throw new Exception ("cannot connect to mongo database at "+dbLocation+" on port "+dbPort);	
		return m;
	}
	
	/**
	 * 
	 * @param m
	 * @return
	 */
	public boolean mongoRunningAt(Mongo m) 
	{
		boolean connected = true;
	    try 
	    {
		    Socket socket = m.getMongoOptions().socketFactory.createSocket();
		    socket.connect(m.getAddress().getSocketAddress());
		    socket.close();
		} 
	    catch (IOException ex) 
	    {
		    connected = false;
		}
		return connected;
	}	
	
}
