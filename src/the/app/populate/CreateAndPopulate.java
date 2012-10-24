package the.app.populate;

import java.util.ArrayList;
import java.util.Vector;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.WriteConcern;

import the.app.db.mongo.MongoFactory;

public class CreateAndPopulate 
{

	public static int NOTHING 		= 0;
	public static int INSERT 		= 1;
	public static int UPDATE 		= 2;
	public static int DELETE		= 3;
	public static int SHOW   		= 4;
	public static int GET 			= 5;
	
	public static void main(String[] args)
	{
		try
		{
			CreateAndPopulate cp = new CreateAndPopulate();
			
			String dbName 			= "";
			String collectionName 	= "";
			int numOfDocs 			= 0;
			int action 				= CreateAndPopulate.NOTHING;

			if (dbName.equals("")) 
				System.err.println("dbname must not be \"\"");
			else if (collectionName.equals(""))
				System.err.println("collectionName must not be \"\"");
			else
				cp.run(dbName, collectionName, numOfDocs, action);
			
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
		
		// create and/or get db
		DB db = createOrGetDb(mf, m, dbName);
		
		// create and/or get col
		DBCollection col = createOrGetCollection(db, collectionName);

		//------------------------------------------------------------------------------		
		System.out.println("number of documents in "+col.getName()+" in db " + db.getName() +" before action:  "+col.count());		
		if (action == INSERT)
		{
			col = removeExistingDocuments(col);
			col = populateCollection(col, numOfDocs);
		}
		else if (action == UPDATE)
		{
			col = upsertCollection(col, numOfDocs);
		}
		else if (action == NOTHING)
		{
			System.out.println("doing nothing and doing it well");
		}
		else if (action == SHOW)
		{
			showDocsInCollection(col);
		}
		else if (action == DELETE)
		{
			col = removeExistingDocuments(col);
		}
		else if (action == GET)
		{
			getData(mf, col, "teams");
		}
		
		System.out.println("number of documents in "+col.getName()+" after action:    "+col.count());

		mf.close(db);
	}
	
	
	/**
	 * 
	 * @param mf
	 * @param col
	 * @param index
	 * @throws Exception
	 */
	public void getData(MongoFactory mf, DBCollection col, String index) throws Exception
	{
		
		mf.createIndex(col, "purchase_orders.items.amount", 1);
		
		BasicDBObject query = new BasicDBObject();
		query.put("purchase_orders.items.amount","$1300.00");
		DBCursor cur = col.find(query);
		System.out.println("how many records found in get? "+cur.size());
		try 
		{
			while(cur.hasNext()) 
			{
				System.out.println(cur.next());
			}
		} 
		finally 
		{
			cur.close();
		}
		
		mf.dropIndex(col, "purchase_orders.items.amount"+"_1");
	}

	/**
	 * 
	 * @param mf
	 * @param m
	 * @param dbName
	 * @return
	 */
	public DB createOrGetDb(MongoFactory mf, Mongo m, String dbName)
	{
		return mf.getDatabase(m, dbName);
	}
	
	/**
	 * 
	 * @param db
	 * @param collectionName
	 * @return
	 */
	public DBCollection createOrGetCollection(DB db, String collectionName) 
	{
		return db.getCollection(collectionName);
	}
	
	/**
	 * 
	 * @param col
	 * @param numOfDocs
	 * @return
	 */
	public DBCollection populateCollection(DBCollection col, int numOfDocs) 
	{
		System.out.println("starting populate of collection "+col.getName()+" at "+new java.util.Date());
		Vector<String> teams = new Vector<String>();
		teams.add("auburn tigers");
		teams.add("atlanta falcons");
		teams.add("atlanta braves");
		teams.add("purdue boilermakers");
		int teamCnt = 0;
		
		for (int i = 1; i <= numOfDocs; i++)
		{
			BasicDBObject sad = new BasicDBObject();
			sad.put("_id",i);
			sad.put("email","bill"+i+"@mail.com");
			sad.put("first_name", "bill");
			sad.put("last_name", "donges");
			sad.put("age", 38);
			
			BasicDBObject custom = new BasicDBObject();
			custom.put("kids",2);
			custom.put("cars",2);
			custom.put("houses",1);
			sad.put("custom",custom);
			
			ArrayList<String> team = new ArrayList<String>();
			team.add(teams.get(teamCnt));
			teamCnt++;
			if (teamCnt == 4) { teamCnt = 0; }
			sad.put("teams", team);
			
			ArrayList<BasicDBObject> orders = new ArrayList<BasicDBObject>();
			for (int j = 0; j < 2; j++)
			{
				BasicDBObject order = new BasicDBObject();
				order.put("order_id", i+"_"+j);
				order.put("order_date", "1/1/2012");
				order.put("amount","$50"+j+".00");

				ArrayList<BasicDBObject> orderItems = new ArrayList<BasicDBObject>();
				for (int k = 0; k < 3; k++)
				{
					String itemName = "phone";
					String itemAmount = "$100.00";
					
					if (k == 1) {
						itemName = "tablet";
						itemAmount = "$300.00";
					} else if (k == 2) {
						itemName = "tv";
						itemAmount = "$1300.00";
					}
					
					BasicDBObject orderItem = new BasicDBObject();
					orderItem.put("item_id", i+"_"+j+"_"+k);
					orderItem.put("item_name", itemName);
					orderItem.put("amount", itemAmount);
					orderItems.add(orderItem);
				}
				order.put("items",orderItems);
				orders.add(order);
			}
			sad.put("purchase_orders", orders);
			
			col.insert(sad);
		}
		System.out.println("ending populate of collection "+col.getName()+" at "+new java.util.Date());
		return col;
	}
	
	/**
	 * 
	 * @param col
	 * @param numOfDocs
	 * @return
	 */
	public DBCollection upsertCollection(DBCollection col, int numOfDocs) 
	{
		System.out.println("starting populate of collection "+col.getName()+" at "+new java.util.Date());
		for (int i = 1; i <= numOfDocs; i++)
		{
			BasicDBObject sad = new BasicDBObject();
			sad.append("_id",i);
			BasicDBObject custom = new BasicDBObject().append("$set", new BasicDBObject().append("custom", new BasicDBObject("pets",1)));
			
			col.update(sad, custom, true, true);

			if (i % 1000 == 0)
				System.out.println("on row "+i);
		}
		System.out.println("ending populate of collection "+col.getName()+" at "+new java.util.Date());
		return col;
	}	
	
	/**
	 * 
	 * @param col
	 * @return
	 */
	public DBCollection removeExistingDocuments(DBCollection col)
	{
		col.remove(new BasicDBObject());
		return col;
	}
	
	/**
	 * 
	 * @param col
	 */
	public void showDocsInCollection(DBCollection col) 
	{
		for (int i = 0; i < col.count(); i++)
		{
			DBCursor cur = col.find();
			while (cur.hasNext())
				System.out.println(cur.next().toString());
		}
	}
}
