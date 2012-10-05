package the.app.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import the.app.db.mongo.MongoFactory;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

public class DbManagementAction 
{

	private DBMgmt dbm_;
	private MongoFactory mcf_;

	public DbManagementAction(DBMgmt dbm)
	{
		mcf_ = new MongoFactory();
		dbm_ = dbm;
	}	
	
	/**
	 * 
	 * @param db
	 */
	public void close(DB db) throws Exception
	{
		mcf_.close(db);
	}
	
	/**
	 * 
	 * @return
	 */
	public Mongo getMongo() throws Exception
	{
		return mcf_.getConnection(dbm_.getDbLocation(), dbm_.getDbPort());
	}
	
	/**
	 * 
	 * @param dbName
	 * @return
	 * @throws Exception
	 */
	public DB getDB(String dbName) throws Exception
	{
		DB db = null;
		if (dbm_.isUseToAuth())
		{
			if (dbm_.getAuthDB() == null)
			{
				dbm_.setAuthDB(mcf_.getDatabase(dbm_.getDbLocation(), dbm_.getAuthDbName(), dbm_.getDbPort(), dbm_.isValidate(), dbm_.getDbUsername(), dbm_.getDbPassword()));
				db = dbm_.getAuthDB().getMongo().getDB(dbName);
			}
			else
			{
				db = dbm_.getAuthDB().getMongo().getDB(dbName);
			}
		}
		else
		{
			db = mcf_.getDatabase(dbm_.getDbLocation(), dbName, dbm_.getDbPort(), dbm_.isValidate(), dbm_.getDbUsername(), dbm_.getDbPassword());
		}
		return db;
	}
	
	/**
	 * 
	 * @param dbs
	 * @return
	 */
	public List<Stats> getDbStats() throws Exception
	{
		List<String> dbs = getDbs();
		List<Stats> stats = new ArrayList<Stats>();
		for (int i = 0; i < dbs.size(); i++)
		{
			stats.add(getDbStats(dbs.get(i)));
		}
		return stats;
	}
	
	/**
	 * 
	 * @param db
	 * @return
	 */
	public Stats getDbStats(String dbName) throws Exception
	{
		DB db = getDB(dbName);
		return new Stats(db.getStats());
	}
	
	/**
	 * 
	 * @return
	 */
	public List<String> getDbs() throws Exception
	{
		DB db = getDB(dbm_.getDbName());
		List<String> dbs = db.getMongo().getDatabaseNames();
		return dbs;
	}
	
	/**
	 * 
	 * @param dbName
	 * @return
	 */
	public List<String> getCols(String dbName) throws Exception
	{
		DB db = getDB(dbName);
		Set<String> set = db.getCollectionNames();
		List<String> cols = new ArrayList<String>();
		for (String s : set) 
		{
			cols.add(s);
		}
		return cols;
	}
	
	/**
	 * 
	 * @param dbName
	 * @param colName
	 * @return
	 */
	public List<String> getRows(String dbName, String colName) throws Exception
	{
		List<String> rows = new ArrayList<String>();
		DB db = getDB(dbName);
		DBCollection col = db.getCollection(colName);
		DBCursor cur = col.find();
		while (cur.hasNext())
		{
			String s = cur.next().toString();
			rows.add(s);
		}
		return rows;
	}

}
