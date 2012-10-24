package the.app.aggregation;

import java.util.List;

import the.app.db.mongo.MongoFactory;
import the.app.populate.CreateAndPopulate;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteConcern;

public class Campaign 
{

	public static int NOTHING 		= 0;
	public static int GEN_BIG_TEST  = 6;
	public static int GET_BIG_TEST 	= 7;
	public static int NEXT_ID		= 8;
	
	public static void main(String[] args)
	{
		try
		{
			Campaign c = new Campaign();
			
			String dbName 			= "big";
			String collectionName 	= "campaign_sends";
			int numOfDocs 			= 0;
			int action 				= Campaign.GET_BIG_TEST;

			c.run(dbName, collectionName, numOfDocs, action);
		}
		catch (Exception e)
		{
			System.err.println("exception - message: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @param dbName
	 * @param collectionName
	 * @param numOfDocs
	 * @param action
	 * @param removeExisting
	 * @throws Exception
	 */
	public void run(String dbName, String collectionName, int numOfDocs, int action) throws Exception
	{
		// create mongo factory instance
		MongoFactory mf = new MongoFactory();
		
		// get connection
		Mongo m = mf.getConnection("localhost", 27017);
		m.setWriteConcern(WriteConcern.SAFE);

		// create the createandpopulate instance
		CreateAndPopulate cap = new CreateAndPopulate(); 
		
		// create and/or get db
		DB db = cap.createOrGetDb(mf, m, dbName);
		
		// create and/or get col
		DBCollection col = cap.createOrGetCollection(db, collectionName);

		//------------------------------------------------------------------------------		
		System.out.println("number of documents in "+col.getName()+" in db " + db.getName() +" before action " + action + ":  "+col.count());		
		if (action == GEN_BIG_TEST)
		{
			runBigTest(mf, col, numOfDocs);
		}
		else if (action == GET_BIG_TEST)
		{
			getBigTest(mf, col);
		}
		else if (action == NEXT_ID)
		{
			getNextCampaignId(col);
		}
		
		System.out.println("number of documents in "+col.getName()+" after action:    "+col.count());

		mf.close(db);
	}
	
	/**
	 * 
	 * @return
	 */
	public int getRandNumOfDocs()
	{
		int a = 10000;
		int b = 100000;
		double ran = Math.random();
		return (int)((b-a)*ran + a);
	}
	
	/**
	 * 
	 * @param col
	 * @return
	 */
	public List<?> getDistinctCampaignIds(DBCollection col)
	{
		return col.distinct("campaign_id");
	}
	
	/**
	 * 
	 * @param col
	 */
	public int getNextCampaignId(DBCollection col)
	{
		System.out.println("start getNextCampaignId");
		List<?> l = getDistinctCampaignIds(col);
		int nextId = 0;
		for (int i = 0; i < l.size(); i++)
		{
			int id = Integer.parseInt(l.get(i).toString());
			if (nextId < id)
				nextId = id;
		}
		nextId++;
		System.out.println("end getNextCampaignId");
		return nextId;
	}
	
	/**
	 * 
	 * @param mf
	 * @param col
	 * @param numOfDocs
	 */
	public void runBigTest(MongoFactory mf, DBCollection col, int numOfDocs)
	{
		int campaign_id = getNextCampaignId(col);
		int starting_user_id = 1;

		if (numOfDocs == 0)
			numOfDocs = getRandNumOfDocs();
		
		System.out.println("creating "+numOfDocs+" for campaign "+campaign_id);
		for (int i = 1; i <= numOfDocs; i++)
		{
			BasicDBObject sad = new BasicDBObject();
			sad.put("campaign_id", campaign_id);
			sad.put("user_id", String.valueOf(starting_user_id + i));
			sad.put("sent_date", new java.util.Date());
			col.insert(sad);
			if (i % 100000 == 0) { System.out.println("on " + i); }
		}
	}
	
	/**
	 * 
	 * @param mf
	 * @param col
	 */
	public void getBigTest(MongoFactory mf, DBCollection col)
	{
		String todo ="";
		
		//todo = "both";
		//todo = "agg";
		todo = "distinct";
		
		if (todo.equals("agg") || todo.equals("both"))
		{
			System.out.println("----------------------------------------------------------");
			System.out.println("start aggregation test");
			System.out.println("----------------------------------------------------------");
			
			DBObject fields = new BasicDBObject("campaign_id", 1);
			fields.put("_id", 0);
			DBObject project = new BasicDBObject("$project",fields);
			
			DBObject groupfields = new BasicDBObject("_id","$campaign_id");
			groupfields.put("number_of_sends", new BasicDBObject("$sum",1));
			DBObject group = new BasicDBObject("$group", groupfields);
			
			System.out.println("starting aggregation at "+new java.util.Date());
			AggregationOutput output = col.aggregate(project, group);
			System.out.println("ending aggregation at "+new java.util.Date());
			System.out.println("output: " + output);
	
			System.out.println("----------------------------------------------------------");
			System.out.println("end aggregation test");
			System.out.println("----------------------------------------------------------");
		}
		else if (todo.equals("distinct") || todo.equals("both"))
		{
			System.out.println("----------------------------------------------------------");
			System.out.println("start distinct test");
			System.out.println("----------------------------------------------------------");
			
			System.out.println("starting distinct at "+new java.util.Date());
			List<?> l = col.distinct("campaign_id");
			System.out.println("got distinct, starting count at "+new java.util.Date());
			for (int i = 0; i < l.size(); i++)
			{
				System.out.println(l.get(i));
				
				System.out.println("campaign_id " + i + " has " + col.count(new BasicDBObject("campaign_id",i)));
			}
			System.out.println("got distinct, ending count at "+new java.util.Date());
			
			System.out.println("----------------------------------------------------------");
			System.out.println("end distinct test");
			System.out.println("----------------------------------------------------------");
		}
	}
	
}
