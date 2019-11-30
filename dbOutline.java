
 import java.time.LocalDate;
 import java.time.format.DateTimeFormatter;
 import java.sql.Connection;
 import java.sql.DatabaseMetaData;
 import java.sql.DriverManager;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.LinkedHashMap;
 import java.util.Map;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;


 public class DatabaseController {
 	private static Connection con;
 	private static boolean hashTables;
 	private static String dateFormat = "yyy/MM/dd";
     static final Logger logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
     private static String elapsed = "elapsedTime";


 	public static void main(String[] args) throws SQLException, ClassNotFoundException {
 		if(con==null) getConnection();
 	}
 	//getDatabaseMetaData()  :  prints all database columns and values
 	 public static void getDatabaseMetaData() throws SQLException {
 		DatabaseMetaData dbmd = con.getMetaData();
 		Statement stmt = con.createStatement();
 		String[] types = {"TABLE"};
 		ResultSet rs = dbmd.getTables(null, null, "%", types);
 		ArrayList<String> tableNames = new ArrayList<>();
 		while(rs.next())
 			tableNames.add(rs.getString("TABLE_NAME"));
 		//print BlockLists
 		System.out.println(tableNames.get(0));
 		ResultSet res = stmt.executeQuery("SELECT * FROM BlockLists");
 		//print column values
 		System.out.println("BlockID     BlockName");
 		while (res.next()) {
 			int blockid = res.getInt("BlockID");
 			String blockname = res.getString("BlockName");
 			System.out.println(blockid + "          " + blockname);
 		    }

 		System.out.println("\n");

 		//print URLs Table
 		System.out.println(tableNames.get(1));
 		rs = stmt.executeQuery("SELECT * FROM ItemSettings");

 		//permanent column names
 		System.out.println("ID    BlockId");

 		//print column values
 		while (rs.next()) {
 		   int id = rs.getInt("ID");
 		   int blockid = rs.getInt("BlockID");
 		   System.out.println(id+"        "+ blockid);
 		}
 		System.out.println("\n");


 		//print WebsiteUsage Table
 		System.out.println(tableNames.get(2));
 		rs = stmt.executeQuery("SELECT * FROM Items");

 		//permanent column names - no need to grab from table
 		System.out.println("ID      Item");

 		while(rs.next()) {
 			int id = rs.getInt("ID");
 			String url = rs.getString("Item");
 			System.out.println(id + "    " + url);
 		}
 		System.out.println("\n");


 		System.out.println(tableNames.get(3));
 		rs = stmt.executeQuery("SELECT * FROM WebsiteUsage");

 		//permanenet column names
 		System.out.println("ID     Elapsed     Date");
 		//print column values
 		while(rs.next()) {
 			int id = rs.getInt("id");
 			int time = rs.getInt(elapsed);
 			String date = rs.getString("Date");
 			System.out.println(id + "      " + time + "        " + date + "      ");

 		}
 		System.out.println();	
 	}


 	//getconnection()  		 :  connects to database*
 	private static void getConnection() throws ClassNotFoundException, SQLException {
 		MessageDigest messageDigest;
 		String encryptedString = "";
 		try {
 			messageDigest = MessageDigest.getInstance("SHA-256");
 			messageDigest.update("password".getBytes());
 			encryptedString = new String(messageDigest.digest());
 		} catch (NoSuchAlgorithmException e) {
 			FocusKeeper.logger.error("%s", e);
 		}
 		Class.forName("org.sqlite.JDBC");
 		con = DriverManager.getConnection("jdbc:sqlite:FocusKeeper.db", "admin", encryptedString);
 	}

 	//createTable()			 :  creates all database tables with correct columns (only needs to be called if tables don't exist)
 	public static void createTable(){
 		//creates our three tables: URLs, URLSettings, WebsiteUsage
 		if(!hashTables) {
 			try (Statement state = con.createStatement()) {
 				String createURLS = "CREATE TABLE IF NOT EXISTS Items (\n"
 						+ " ID INTEGER PRIMARY KEY AUTOINCREMENT, \n"
 						+ " Item text NOT NULL UNIQUE);";
 				state.executeUpdate(createURLS);

 				String createBlockList = "CREATE TABLE IF NOT EXISTS BlockLists (\n"
 						+ " BlockID INTEGER PRIMARY KEY AUTOINCREMENT, \n"
 						+ " BlockName text NOT NULL UNIQUE);";
 				state.executeUpdate(createBlockList);


 				String createUsage = "CREATE TABLE IF NOT EXISTS WebsiteUsage (\n"
 						+ " ID INTEGER NOT NULL, \n"
 						+ " elapsedTime int DEFAULT 0, \n"
 						+ " Date text NOT NULL, \n"
 						+ " CONSTRAINT UQ_WebsiteUsage UNIQUE(ID, DATE));";   

 				state.executeUpdate(createUsage);	

 				String addNew = "INSERT OR IGNORE INTO BlockLists (BlockID, BlockName)\n"
 						+ " VALUES(null,'Distractions');";
 				state.executeUpdate(addNew);

 				String createSettings = "CREATE TABLE IF NOT EXISTS ItemSettings (\n"
 						+ " ID INTEGER NOT NULL, \n"
 						+ " BlockID INTEGER NOT NULL, \n"
 						+ " CONSTRAINT UQ_URLSettings UNIQUE(ID, BLOCKID));";

 				state.executeUpdate(createSettings);
 			} catch (SQLException e) {
 				FocusKeeper.logger.error("%s", e);
 			}
 		}
 		hashTables = true;
 	}

 	//restartDB()			 :  wipe and delete all database tables (cannot be undone)
 	public static void restartDB() {
 		if(con == null) {
 			try {
 				getConnection();
 			} catch (ClassNotFoundException|SQLException e1) {
 				FocusKeeper.logger.error("%s", e1);
 			}
 		}
 		try (Statement state = con.createStatement()){
 			String sql = "DROP TABLE Items;";
 			state.executeUpdate(sql);

 			sql = "DROP TABLE ItemSettings;";
 			state.executeUpdate(sql);

 			sql = "DROP TABLE WebsiteUsage;";
 			state.executeUpdate(sql);

 			sql = "DROP TABLE BlockLists;";
 			state.executeUpdate(sql);
 		} catch (SQLException e) {
 			FocusKeeper.logger.error("%s", e);
 		}
 	}

 	//addList()				 :  adds new URLS and list when new blocklist is created
 	//Parameters: Name of new List, and list of URLS in list
 	public static void addList(String list, String[] urls){
 		//adds new list as field in URLSettings database
 		String addNew = "INSERT OR IGNORE INTO BlockLists (BlockID, BlockName)\n"
 				+ " VALUES(null, ?);";
 		try (PreparedStatement prep = con.prepareStatement(addNew)){
 			prep.setString(1, list);
 			prep.executeUpdate();
 		} catch (SQLException e) {
 			FocusKeeper.logger.error("%s", e);
 		}	

 		//adds new URLS to URLs database
 		StringBuilder insertQuery = new StringBuilder("BEGIN TRANSACTION;\n");
 		for(String url : urls) {
 			insertQuery.append((" INSERT OR IGNORE INTO Items (id, Item)\n" + 
 					" VALUES(null, '" + url + "');\n"));
 		}
 		insertQuery.append("COMMIT;");
 		try (Statement state2 = con.createStatement()){
 			state2.executeUpdate(insertQuery.toString());
 		} catch (SQLException e) {
 			FocusKeeper.logger.error("%s", e);
 		}

 		//insert into URLSettings id of URLS where URL is in block list passed to function
 		String insert = "";
         for(String url : urls) {
         	insert = "INSERT or IGNORE INTO ItemSettings (ID, BlockID)\n"
         			+ " VALUES((SELECT ID from Items where Item=?), (SELECT BlockID from BlockLists"
         					+ " where BlockName=?));";
     		try (PreparedStatement prep2 = con.prepareStatement(insert)){
     			prep2.setString(1, url);
     			prep2.setString(2, list);
     			prep2.executeUpdate();
     		} catch (SQLException e) {
     			FocusKeeper.logger.error("%s", e);
     		}
         }


 	}

 	//deleteList()			 :	deletes blocklist and it's URLs (if not used by another list)
 	//Parameters: Name of the list to be deleted
 	public static void deleteList(String list){
 		//deletes entry in URLSettings where URL is attached to list being dropped
 		//deletes URLS in URLs database where they only existed in deleted list
 		String delete = "DELETE FROM ItemSettings WHERE BlockID= (SELECT BlockID FROM BlockLists WHERE BlockName=?);";
 		String delete2 = "DELETE FROM Items WHERE ID NOT IN (SELECT US.ID FROM ItemSettings US);";
 		String delete3 = "DELETE FROM BlockLists WHERE BlockName=?;";

 		try (Statement state2 = con.createStatement();
 				PreparedStatement prep = con.prepareStatement(delete);
 				PreparedStatement prep2 = con.prepareStatement(delete3)){
 			prep.setString(1, list);
 			prep.executeUpdate();
 			prep2.setString(1, list);
 			prep2.executeUpdate();
 			state2.executeUpdate(delete2);	
 		} catch (SQLException e) {
 			FocusKeeper.logger.error("%s", e);
 		}
 	}

 	//addURLUsage()			 :	updates URL Usage time each time user goes on new website
 	//Parameters: Time spent on website in current period, name of website
 	public static void addURLUsage(Integer elapsedTime, String url) {
 		//adds current elapsed time to current value in URLUsage database
 		//adds new entry if first visit in day
 		int currentTime = 0;
 		int id = 0;
 		LocalDate localDate = LocalDate.now();
         String date = DateTimeFormatter.ofPattern(dateFormat).format(localDate);
 		String getID = "SELECT ID FROM Items WHERE Item = ?;";

 		try(PreparedStatement prep = con.prepareStatement(getID)){
 			prep.setString(1, url);
 			try(ResultSet gotID = prep.executeQuery()){
 				id = gotID.getInt("ID");
 			}
 		} catch(SQLException e) {
 			FocusKeeper.logger.error("%s", e);
 		}
 		String getUsage = "SELECT * FROM WebsiteUsage WHERE ID = ? AND"
 				+ " Date = ?;";
 		try(PreparedStatement prep2 = con.prepareStatement(getUsage)){
 			prep2.setInt(1, id);
 			prep2.setString(2, date);
 			//does this work??
 			try(ResultSet usage = prep2.executeQuery()){
 				if(usage.next()) {
 					currentTime = usage.getInt(elapsed);
 				} else currentTime = 0;
 				currentTime += elapsedTime;
 			}
 		} catch (SQLException e) {
 			FocusKeeper.logger.error("%s", e);
 		}

 		String add = "INSERT OR IGNORE INTO WebsiteUsage (ID, elapsedTime, Date)\n"
 				+ " VALUES(?, ?, ?);";

 		try(PreparedStatement prep3 = con.prepareStatement(add)){
 			prep3.setInt(1, id);
 			prep3.setInt(2, currentTime);
 			prep3.setString(3, date);
 			prep3.executeUpdate();
 		} catch (SQLException e) {
 			FocusKeeper.logger.error("%s", e);
 		}

 		String insert = "UPDATE WebsiteUsage SET ID= ?, elapsedTime = ?, Date = ? WHERE"
 				 		+ " ID = ? AND Date = ?;";

 		try(PreparedStatement prep4 = con.prepareStatement(insert)){
 			prep4.setInt(1, id);
 			prep4.setInt(2, currentTime);
 			prep4.setString(3, date);
 			prep4.setInt(4, id);
 			prep4.setString(5, date);
 			prep4.executeUpdate();

 		} catch (SQLException e) {
 			FocusKeeper.logger.error("%s", e);
 		}
 	}

 	//getMostUsed()		:		returns a map with url and total minutes spent on that site in the given date range
 	//Parameters: Two Strings in form "yyy/MM/dd" representing start date and end date, including
 	public static Map<String, Integer> getMostUsed(String start, String end) {
 		//queries to get sites with highest elapsedTime in WebsiteUsage

 		LinkedHashMap <String, Integer> mostUsed = new LinkedHashMap<>();
 		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
 		StringBuilder datesForQuery = new StringBuilder("(");

 		//convert String to LocalDate
 		LocalDate newStart = LocalDate.parse(start, formatter);
 		LocalDate newEnd = LocalDate.parse(end, formatter);

         String formattedDate = DateTimeFormatter.ofPattern(dateFormat).format(newStart);
         datesForQuery.append("'" + formattedDate + "'");
 		if(newStart.isBefore(newEnd)) {
 			newStart = newStart.plusDays(1);
 			for (LocalDate date = newStart; ((date.isBefore(newEnd)) || (date.isEqual(newEnd))); date = date.plusDays(1)) {
 		        formattedDate = DateTimeFormatter.ofPattern(dateFormat).format(date);
 		        datesForQuery.append(", '" + formattedDate + "'");
 			}
 		}
 		datesForQuery.append(")");
 		String get = "SELECT u.Item AS item, sum(w.elapsedTime) AS elapsed"
 				+ " FROM WebsiteUsage AS w"
 				+ " LEFT JOIN Items AS u on w.ID = u.ID"
 				+ " WHERE w.Date IN " + datesForQuery 
 				+ " GROUP BY u.Item"
 				+ " ORDER BY elapsed DESC LIMIT 5;";

 		//query for most used
 		try (Statement state = con.createStatement();
 			ResultSet rs = state.executeQuery(get)){
 			while(rs.next()) {
 				mostUsed.put(rs.getString("item"), rs.getInt("elapsed"));
 			}
 		} catch (SQLException e) {
 			FocusKeeper.logger.error("%s", e);
 		}

 		return mostUsed;
 	}


 	//getRecentlyUsed()		 :  gets top 5 most visited sites that day (to display on hope page of application)
 	public static Map<String, Integer> getRecentlyUsed() {
 		//queries to get sites recently used in WebsiteUsage
 		//same as print but add to a dictionary-type thing

 		//("www.instagram.com", 45)
 		LinkedHashMap <String, Integer> recents = new LinkedHashMap<>();
 		String getRecent = "SELECT u.Item AS item, w.elapsedTime as elapsed"
 				+ " FROM WebsiteUsage AS w"
 				+ " LEFT JOIN Items as u on w.ID = u.ID"
 				+ " ORDER BY elapsed DESC LIMIT 5;";

 		try (Statement state = con.createStatement();
 			ResultSet usage = state.executeQuery(getRecent)){		
 	        //get values and add to recents list
 	        while(usage.next()) {
 	        	int time = usage.getInt("elapsed");
 	        	String foundURL = usage.getString("item");
 	        	recents.put(foundURL, time);
 	        }
 		} catch (SQLException e) {
 			FocusKeeper.logger.error("%s", e);
 		}
 		return recents;		
 	}

 	//getTotalTimeToday()	 :  gets total screen time user has spent today 
 	public static int getTotalTimeToday() {
 		int totalTimeToday = 0;
 		LocalDate localDate = LocalDate.now();
         String date = DateTimeFormatter.ofPattern(dateFormat).format(localDate);
         String get = "SELECT * FROM WebsiteUsage WHERE Date='" + date + "';";

 		try (Statement state = con.createStatement();
 	        ResultSet rs = state.executeQuery(get)){
 	        while(rs.next()) {
 	        	totalTimeToday += rs.getInt(elapsed);  	
 	        }
 		} catch (SQLException e) {
 			FocusKeeper.logger.error("%s", e);
 		} 

         return totalTimeToday;
 	}

 	//getTotalFocusTimeToday()  :  gets total screen time user has spenr in focus today
 	public static int getTotalFocusTimeToday() {
 		int totalTimeToday = 0;
 		LocalDate localDate = LocalDate.now();
         String date = DateTimeFormatter.ofPattern(dateFormat).format(localDate);
         String get = "SELECT * FROM WebsiteUsage WHERE Date = '" + date + "'"
         		+ " AND ID IN (SELECT ID FROM ItemSettings WHERE BlockID = 1);";

 		try (Statement state = con.createStatement();
 	        ResultSet rs = state.executeQuery(get)){
 	        while(rs.next()) {
 	        	totalTimeToday += rs.getInt(elapsed);
 	        }
 		} catch (SQLException e) {
 			FocusKeeper.logger.error("%s", e);
 		}

         return totalTimeToday;
 	}	
 }
