
package db;

import java.time.LocalDate;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Database {
 	private static Connection con;
 	private static boolean hashTables;
 	

 	//getconnection()  		 :  connects to database*
 	//Connect to database with given username and password
 	public void getConnection(String user,String pass) throws ClassNotFoundException, SQLException {
 		//export HP_JDBC_URL=jdbc:mysql://db.labthreesixfive.com/your_username_here?autoReconnect=true\&useSSL=false
 		//	export HP_JDBC_USER=
 		//	export HP_JDBC_PW=
 	}

 	//dbLocgout()			 : closes connection to db
 	public void dbLogout() throws SQLException {
 		//con.close();
 	}
 	
 	//getDatabaseMetaData()  :  prints all database columns and values
 	 public static void getDatabaseMetaData() throws SQLException {
 		 //example of how to print db 
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

 	 }



 	//createTable()			 :  creates all database tables (only needs to be called if tables don't exist)
 	public static void createTables(){
 		if(!hashTables) {
 			try (Statement state = con.createStatement()) {
 				String createRooms = "CREATE TABLE IF NOT EXISTS lab7_rooms (\n" + 
 						"  RoomCode char(5) PRIMARY KEY,\n" + 
 						"  RoomName varchar(30) NOT NULL,\n" + 
 						"  Beds int(11) NOT NULL,\n" + 
 						"  bedType varchar(8) NOT NULL,\n" + 
 						"  maxOcc int(11) NOT NULL,\n" + 
 						"  basePrice DECIMAL(6,2) NOT NULL,\n" + 
 						"  decor varchar(20) NOT NULL,\n" + 
 						"  UNIQUE (RoomName)\n" + 
 						");";
 				state.executeUpdate(createRooms);
 				
 				String createRes = "CREATE TABLE IF NOT EXISTS lab7_reservations (\n" + 
 						"  CODE int(11) PRIMARY KEY,\n" + 
 						"  Room char(5) NOT NULL,\n" + 
 						"  CheckIn date NOT NULL,\n" + 
 						"  Checkout date NOT NULL,\n" + 
 						"  Rate DECIMAL(6,2) NOT NULL,\n" + 
 						"  LastName varchar(15) NOT NULL,\n" + 
 						"  FirstName varchar(15) NOT NULL,\n" + 
 						"  Adults int(11) NOT NULL,\n" + 
 						"  Kids int(11) NOT NULL,\n" + 
 						"  UNIQUE (Room, CheckIn),\n" + 
 						"  UNIQUE (Room, Checkout),\n" + 
 						"  FOREIGN KEY (Room) REFERENCES lab7_rooms (RoomCode)\n" + 
 						");";
 				state.executeUpdate(createRes);
 				
 				String insertRooms = " INSERT INTO lab7_rooms SELECT * FROM INN.rooms;";
 				state.executeUpdate(insertRooms);
 				
 				String insertRes = "INSERT INTO lab7_reservations SELECT CODE, Room,\n" + 
 						"   DATE_ADD(CheckIn, INTERVAL 9 YEAR),\n" + 
 						"   DATE_ADD(Checkout, INTERVAL 9 YEAR)\n" + 
 						"   Rate, LastName, FirstName, Adults, Kids FROM INN.reservations;";
 				state.executeUpdate(insertRes);
 			} catch (SQLException e) {
 				System.out.println(e);
 			}
 		}
 		hashTables = true;
 	}

 	//restartDB()			 :  wipe and delete all database tables (cannot be undone)
 	public static void restartDB() {
 		try (Statement state = con.createStatement()){
 			String sql = "DROP TABLE ...;";
 			state.executeUpdate(sql);


 		} catch (SQLException e) {
 			System.out.println(e);
 		}
 	}
 	
 	//get all reservations today
 	public String getTodayRes(LocalDate today) {
 		return "	First Name		Last Name 			RoomCode\n\n"
 				+ "	Sydney			Jaques				AAA\n"
 				+ "	Pramika			Kumar				AAB\n"
 				+ "	Pranathi			Guntupalli			AAC\n"
 				+ "	Rafi				Cohn-Gruenwald		AAD\n";
 		//return in a way where we can display in label with javafx?
 	}

	//add new reservation
 	public static void newReservation(String list, String[] urls){
 		//outline for inserting into table
 		
 		String addNew = "INSERT OR IGNORE INTO BlockLists (BlockID, BlockName)\n"
 				+ " VALUES(null, ?);";
 		try (PreparedStatement prep = con.prepareStatement(addNew)){
 			prep.setString(1, list);
 			prep.executeUpdate();
 		} catch (SQLException e) {
 			System.out.println(e);
 		}	

 		//outline of how to do a transaction (faster for many insert statements)
 		StringBuilder insertQuery = new StringBuilder("BEGIN TRANSACTION;\n");
 		for(String url : urls) {
 			insertQuery.append((" INSERT OR IGNORE INTO Items (id, Item)\n" + 
 					" VALUES(null, '" + url + "');\n"));
 		}
 		insertQuery.append("COMMIT;");
 		try (Statement state2 = con.createStatement()){
 			state2.executeUpdate(insertQuery.toString());
 		} catch (SQLException e) {
 			System.out.println(e);
 		}
 	}
 	
 	//getAvailRooms()				 :  gets list of available rooms
 	//Parameters: Info from new res page
 	public ArrayList<String> getAvailRooms(String code,String bed, LocalDate checkin, LocalDate checkout,int occ) {
 		//returns int of number of rooms found, if less than 5, call to get another method for 5 suggestions
 		ArrayList<String> rooms = new ArrayList<>();
 		rooms.add("Abscond or bolster-			$175");
 		rooms.add("Convoke and sanguine-			$175");
 		return rooms;
 	
 	}
 	
 	
 	//checkNewRes()					 :  checks validity of reservation
 	//Parameters: Date,RoomCode
 	public boolean checkNewRes(int occupancy, LocalDate date, String roomCode) {
 		//returns boolean if reservation is valid
 		//valid if occupancy is ok and dates don't overlap
 		return true;
 	}
 	

 	//deleteReservation()			 :	deletes reservation
 	//Parameters: ?
 	public static void deleteRes(String list){
 		//deletes from reservation table and customer table?? if that's their only reservation?
 		String delete = "DELETE FROM ItemSettings WHERE BlockID= (SELECT BlockID FROM BlockLists WHERE BlockName=?);";
 		String delete2 = "DELETE FROM Items WHERE ID NOT IN (SELECT US.ID FROM ItemSettings US);";

 		try (Statement state2 = con.createStatement();
 				PreparedStatement prep = con.prepareStatement(delete)){
 			prep.setString(1, list);
 			prep.executeUpdate();
 			state2.executeUpdate(delete2);	
 		} catch (SQLException e) {
 			System.out.println(e);
 		}
 	}
 	
 	
 	//searchRes()			 :  searches for reservation, T if valid, else F
 	//Parameters: reservation code
 	public boolean searchRes(int Code) {
 		//searches Reservations for code
 		//if result set is empty, false
 		return true;
 	}
 	
 	//getRes()				 :  get reservaton by code
 	//Parameters: reservation code
 	public Map<String, String> getRes(int code){
 		//returns map of result from reservation: 
        LinkedHashMap<String, String> res = new LinkedHashMap<>();

 		return res;

 	}

 	//updateRes()			 :	updates reservation
 	//Parameters: what to update, value
 	public static void updateRes(Integer elapsedTime, String url) {
 		//delete first res, insert all new one? 

 	}


 	//getReservation()		 :  gets Reservation by ??
 	//Parameters: ??
 	public static Map<String, Integer> getReservation() {
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
 			System.out.println(e);
 		}
 		return recents;		
 	}

	
 }

