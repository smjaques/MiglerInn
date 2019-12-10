
package db;

import java.util.*;
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
import java.io.BufferedReader;
import java.io.FileReader;

public class Database {
 	private static Connection con;
 	private static boolean hashTables;
 	private int MaxOccupancy;
 	

 	//getconnection()  		 :  connects to database*
 	//Connect to database with given username and password
 	public void getConnection() throws ClassNotFoundException, SQLException {
        String jdbcUrl = System.getenv("HP_JDBC_URL");
        String jdbcUser = System.getenv("HP_JDBC_USER");
        String jdbcPW = System.getenv("HP_JDBC_PW");
        try{
            System.out.println("Attempting to establish connection...");
            Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPW);
            con = conn;
            System.out.println("Connection established.");
        } catch (Exception e) {
            System.out.println(e);
        }
 	}

 	//dbLocgout()			 : closes connection to db
 	public void dbLogout() throws SQLException {
 		con.close();
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
                result += "\t\t" + rs.getString("FirstName") + "\t\t";
                result += rs.getString("LastName") + "\t\t";
                result += rs.getString("Room") + "\n";
            }
        } catch (Exception e) {
            return "";
        }

        return result;
 		//return in a way where we can display in label with javafx?
 	}

    //see rooms ordered by popularity
    public ArrayList<String> seeRooms(){
        String line;
        String query = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader("FR1.sql"));
            while((line = br.readLine()) != null){
                query += line + "\n";
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        ArrayList<String> rooms = new ArrayList<String>();
        String result = "\tRoom Code\t\tRoom Name\t\t\t\tBeds\t\tBed Type" + 
                        "\t\tMax Occupants\t\tBase Price\t\tDecor" + 
                        "\t\tRoom Popularity\t\tNext Date Available" + 
                        "\t\tMost Recent Stay\t\tMost Recent Checkout\n\n";
        rooms.add(result);
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()){
                String code = rs.getString("RoomCode");
                String name  = rs.getString("RoomName");
                String beds = rs.getString("Beds");
                String bedType = rs.getString("bedType");
                String maxOcc = rs.getString("maxOcc");
                String basePrice = rs.getString("basePrice");
                String decor = rs.getString("decor");
                String pop = rs.getString("Room Popularity");
                String nextCheckin = rs.getString("Next Available Check-in Date");
                String stay = rs.getString("Most Recent Stay");
                String checkout = rs.getString("Most Recent Checkout");
                String data = String.format("\t%s\t\t%s\t\t\t\t%s\t\t%s\t\t%s\t\t%s\t\t%s" + 
                                            "\t\t%s\t\t%s\t\t%s\t\t%s", 
                        code, name, beds, bedType, maxOcc, basePrice, decor, 
                        pop, nextCheckin, stay, checkout);
                rooms.add(data);
            }
        } catch (Exception e) {
            System.out.println(e);
            return rooms;
        }

        return rooms;
    }

    public ArrayList<String> getRev(){
        String line;
        String query = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader("FR6.sql"));
            while((line = br.readLine()) != null) {
                query += line + "\n";
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
        
        ArrayList<String> revenue = new ArrayList<String>();
        revenue.add("\tRoom\tJan       Feb       Mar       Apr       May       Jun       " + 
                    "Jul       Aug       Sep       Oct       Nov       Dec       Year Total\n");
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            String[] mos = {"January", "February", "March", "April", "May", "June",
                            "July", "August", "September", "October", "November", "December"};
            float[] mo_rev = new float[12];
            float year = 0;
            for (int i = 0; i < 10; i++){
                HashMap<String, Float> months = new HashMap<String, Float>();
                float sum = 0;
                String roomcode = "N/A";
                for (int j = 0; j < 12; j++){
                    rs.next();
                    roomcode = rs.getString("room");
                    String month = rs.getString("month");
                    float rev = rs.getFloat("rev");
                    sum += rev;
                    months.put(month, rev);
                    switch (month) {
                        case "January": mo_rev[0] += rev;
                                        break;
                        case "February": mo_rev[1] += rev;
                                         break;
                        case "March": mo_rev[2] += rev;
                                      break;
                        case "April": mo_rev[3] += rev;
                                      break;
                        case "May": mo_rev[4] += rev;
                                    break;
                        case "June": mo_rev[5] += rev;
                                     break;
                        case "July": mo_rev[6] += rev;
                                     break;
                        case "August": mo_rev[7] += rev;
                                       break;
                        case "September": mo_rev[8] += rev;
                                          break;
                        case "October": mo_rev[9] += rev;
                                        break;
                        case "November": mo_rev[10] += rev;
                                         break;
                        case "December": mo_rev[11] += rev;
                                         break;
                    }
                }

                String data = String.format("\t%s\t%7.2f   %7.2f   %7.2f   %7.2f   %7.2f   " + 
                                            "%7.2f   %7.2f   %7.2f   %7.2f   %7.2f   %7.2f   " +
                                            "%7.2f   %7.2f\n", 
                    roomcode, months.get(mos[0]), months.get(mos[1]), months.get(mos[2]),
                    months.get(mos[3]), months.get(mos[4]), months.get(mos[5]), months.get(mos[6]),
                    months.get(mos[7]), months.get(mos[8]), months.get(mos[9]), 
                    months.get(mos[10]), months.get(mos[11]), sum);
                
                year += sum;
                revenue.add(data);
            }
            revenue.add(String.format("\tAll\t%.2f  %.2f  %.2f  %.2f  %.2f  " + 
                                    "%.2f  %.2f  %.2f  %.2f  %.2f  %.2f  %.2f  %.2f\n", 
                    mo_rev[0], mo_rev[1], mo_rev[2], mo_rev[3], mo_rev[4], mo_rev[5],
                    mo_rev[6], mo_rev[7], mo_rev[8], mo_rev[9], mo_rev[10], mo_rev[11], year));
        } catch (Exception e) {
            System.out.println(e);
        }

        return revenue;
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
 	public static void newReservation(){

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
 	public boolean searchRes(int Code) throws SQLException {
 		String query = "SELECT LastName FROM Reservations where Code = ? ";

        try (PreparedStatement prep = con.prepareStatement(query)) {
            prep.setInt(1, Code);
            try (ResultSet res = prep.executeQuery()) {
                if (res.next() == false) {
                	return false;
                }
                	return true;
           } catch (SQLException e) {
               System.out.println(e);
           }
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

