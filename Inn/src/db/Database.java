
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
 	private int MaxOccupancy;
 	

 	//getconnection()  		 :  connects to database*
 	//Connect to database with given username and password
 	public void getConnection(String user,String pass) throws ClassNotFoundException, SQLException {
        String jdbcUrl = System.getenv("HP_JDBC_URL");
        String jdbcUser = System.getenv("HP_JDBC_USER");
        String jdbcPW = System.getenv("HP_JDBC_PW");
        try{
            Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPW);
            con = conn;
            System.out.println("Connection established.");
        } catch (Exception e) {
            System.out.println(e);
        }
 		//export HP_JDBC_URL=jdbc:mysql://db.labthreesixfive.com/your_username_here?autoReconnect=true\&useSSL=false
 		//	export HP_JDBC_USER=
 		//	export HP_JDBC_PW=
 	}

 	//dbLocgout()			 : closes connection to db
 	public void dbLogout() throws SQLException {
 		//con.close();
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
 	
 	
 	//get all reservations today
 	public String getTodayRes(LocalDate today) {
        String sql = "SELECT FirstName, LastName, Room FROM lab7_reservations " + 
                     "WHERE CheckIn < CURDATE() AND Checkout > CURDATE()";
        
        String result = "\t\tFirst Name\t\tLastName\t\tRoomCode\n\n";
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                result += rs.getString("\t\tFirstName") + "\t\t";
                result += rs.getString("LastName") + "\t\t";
                result += rs.getString("Room") + "\n";
            }
        } catch (Exception e) {
            return "";
        }

        return result;
 		//return in a way where we can display in label with javafx?
 	}
 	
 	//getMaxOcc()					 :  returns max Occupancy for rooms
 	public void getMaxOcc() {
 		int max = 4;
 		String query = "SELECT MAX(MaxOccupancy) FROM Rooms as occupancy";
 		
        try (Statement state = con.createStatement();
             ResultSet res = state.executeQuery(query)) {
                max = res.getInt("occupancy");
        } catch (SQLException e) {
            System.out.println(e);
        }
        
        MaxOccupancy = max;
 	}

 	
		//newReservation()				 :  inserts new reservation into DB
 	//Parameters: Fields from Confirmation Page
	 public static void newReservation(String fname, String lname, int RoomCode, String checkin, 
	 									String checkout, int adults, int kids) {
	
		int rate;		
		int code;							
		String query = "Select rate FROM rooms where RoomCode = ?";
		try(PreparedStatement prep = con.prepareStatement(query)) {
			prep.executeUpdate();
			
			// get room rate
			ResultSet res = prep.executeQuery(query);
			if (res.next()) {
				rate = res.getInt("rate");
				
				// get max reservation code and increment by 1
				String getCode = "Select max(code) from reservations";
				try(PreparedStatement prep1 = con.prepareStatement(getCode)) {
					prep1.executeUpdate();
					ResultSet res2 = prep1.executeQuery(query);
					if (res2.next()) {
						code = res2.getInt("code") + 1;
						
						System.out.println("HERE");

						// insert new reservation
						String addReservation = "INSERT INTO reservations (room, checkin, checkout, rate, lastname, firstname, adults, kids)" +
									"Values (?, ?, ?, ?, ?, ?, ?, ?, ?)";	
						try(PreparedStatement prep2 = con.prepareStatement(addReservation)) {
							prep2.setInt(1, code);
							prep2.setInt(2, RoomCode);
							prep2.setString(3, checkin);
							prep2.setString(4, checkout);
							prep2.setInt(4, rate);
							prep2.setString(5, lname);
							prep2.setString(6, fname);
							prep2.setInt(7, adults);
							prep2.setInt(8, kids);
							prep2.executeUpdate();
						}
						
						catch(SQLException e) {
							System.out.println(e);
						}	

					}
				}
				catch(SQLException e) {
					System.out.println(e);
				}	
			}
		}
		
		catch(SQLException e) {
			System.out.println(e);
		}

 	}
 	
 	
 	//checkDateValid()				 :  returns boolean of validity of reservation based on dates

 	//Parameters: roomCode, checkin, checkout, ResCode
 	public boolean checkDateValid(int RoomCode, String checkin, String checkout, int ResCode) {
		// check for date conflict
		String query = "Select checkin, checkout FROM Reservations WHERE code = ?" +
			" AND (? between checkin and checkout OR ? between checkin and checkout) AND <> ?";
		
		try(PreparedStatement prep = con.prepareStatement(query);
			ResultSet res = prep.executeQuery(query)){
			prep.setInt(1, RoomCode);
			prep.setString(2, checkin);
			prep.setString(3, checkout);
			prep.setInt(4, ResCode);
			prep.executeUpdate();

			if(res.next() == false) {
				return true;
			}
				return false;
		}
		catch(SQLException e) {
			System.out.println(e);
		}

 		return true;

 	}
 	
 	
 	//getAvailRooms()				 :  gets list of available rooms
 	//Parameters: Info from new res page
 	public ArrayList<String> getAvailRooms(String code,String bed, LocalDate checkin, LocalDate checkout,int occ) {
 		//returns int of number of rooms found, if none found, call another to get 5 suggestions
 			
 		ArrayList<String> rooms = new ArrayList<>();
 		rooms.add("Abscond or bolster-			$175");
 		rooms.add("Convoke and sanguine-			$175");
 		return rooms;
 	}
 	
 	//getTotalCost()				 :  gets total cost of reservation
 	//Parameters: roomCode, checkin, checkout 
 	public double getTotalCost(int code, String checkin,String checkout) {
 		//Number of weekdays multipled by room base rate
 		//Number of weekend days multiplied by 110% of the room base rate
 		//An 18% tourism tax applied to the total of the above two calculations
 		return 1.0;
 	}

 	
 	//deleteReservation()			 :	deletes reservation
 	//Parameters: reservation code
 	public void deleteRes(String rCode){
 		try (PreparedStatement prep = con.prepareStatement("DELETE FROM Reservations WHERE Code=?")){
 			prep.setString(1, rCode);
 			prep.executeUpdate();
 		} catch (SQLException e) {
 			System.out.println(e);
 		}
 	}
 	
 
 	//searchRes()			 :  searches for reservation, T if valid, else F
 	//Parameters: reservation code
 	public boolean searchRes(int Code) {
 		String query = "SELECT LastName FROM Reservations where Code=";
        try (Statement state = con.createStatement();
                ResultSet res = state.executeQuery(query)) {
        	if(res.next() == false) {
        		return false;
        	}
        		return true;
           } catch (SQLException e) {
               System.out.println(e);
           }
        return false;
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


 	//getReservation()		 :  gets Reservation by resCode
 	//Parameters: resCode
 	public static Map<String, Integer> getReservation() {
 		//("FirstName": "Sydney" , "LastName": "Jaques ...)
 		LinkedHashMap <String, Integer> resInfo = new LinkedHashMap<>();
 		return resInfo;		
 	}

	
 }

