
package db;

import java.util.*;
import java.time.LocalDate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.io.BufferedReader;
import java.io.FileReader;

public class Database {
 	private static Connection con;
 	private static boolean hashTables;
 	

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
 	public int getMaxOcc() {
 		int max=4;
 		String query = "SELECT MAX(MaxOccupancy) as occupancy FROM Rooms as occupancy";
 		
        try (Statement state = con.createStatement();
             ResultSet res = state.executeQuery(query)) {
        	if(res.next()) {
                max = res.getInt("occupancy");
                return max;
        	}
        } catch (SQLException e) {
            System.out.println(e);
        }
        return max;
        
 	}

 	
		//newReservation()				 :  inserts new reservation into DB
 	//Parameters: Fields from Confirmation Page
	 public void newReservation(String fname, String lname, String RoomCode, String checkin, 
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
							prep2.setString(2, RoomCode);
							prep2.setString(3, checkin);
							prep2.setString(4, checkout);
							prep2.setInt(4, rate);
							prep2.setString(5, lname);
							prep2.setString(6, fname);
							prep2.setInt(7, adults);
							prep2.setInt(8, kids);
							prep2.executeUpdate();
						} catch(SQLException e) {
							System.out.println(e);
						}	
					}
				} catch(SQLException e) {
					System.out.println(e);
				}	
			}
		} catch(SQLException e) {
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
 	
 	
 	//getSuggestedRooms				 :  get suggested rooms
 	//Paramteters: Inputted date from New Reservation page
 	public ArrayList<String> getSuggestedRooms(String code,String bed, String checkin, String checkout,int occ){
 		
 		return new ArrayList<String>();
 	}
 	
 	
 	//getAvailRooms()				 :  gets list of available rooms
 	//Parameters: Info from new res page
 	public ArrayList<String> getAvailRooms(String code,String bed, String checkin, String checkout,int occ) {
 		//returns int of number of rooms found, if none found, call another to get 5 suggestions
 		ArrayList<String> finalResult = new ArrayList<>();
 		boolean c = false;
 		String query = "select * from Reservations res\n" + 
 				"join Rooms r on\n" + 
 				"    res.Room=r.RoomId\n" + 
 				"where ? not between res.checkin and res.checkout\n" + 
 				"    and ? not between res.checkin and res.checkout\n" + 
 				"    and r.MaxOccupancy <= ?";
 		
 		if(!code.equalsIgnoreCase("any")) {
 			query += " AND res.room= ?";
 			c = true;
 		}
 		
 		if(!bed.equals("Any")) 
 			query += " AND bedType LIKE '" + bed + "'";
 		System.out.println(query);
 		try (PreparedStatement prep = con.prepareStatement(query)) {
            prep.setString(1, checkin);
            prep.setString(2,  checkout);
            prep.setInt(3,  occ);            
            if(c) {
            	prep.setString(4,  code);
            }
            try (ResultSet res = prep.executeQuery()) {
            	while(res.next()) {
            		String room = res.getString("RoomName");
            		if(!finalResult.contains(room))
            			finalResult.add(res.getString("RoomName"));
            	}
            }
        } catch (SQLException e) {
            System.out.println(e);
        } 
 		if(finalResult.size() == 0) {
 			finalResult = getSuggestedRooms(code, bed, checkin, checkout, occ);
 		}
 		return finalResult;
 	}
 	
 	
 	//getRoomCode()					 : gets room code
 	//Parameters: roomname
 	public String getRoomCode(String name) {
 		String query = "select roomid from Rooms\n" + 
 				"where roomname=?";
 		try(PreparedStatement prep = con.prepareStatement(query)){
 			prep.setString(1, name);
 			try(ResultSet res = prep.executeQuery()){
 				if(res.next())
 					return res.getString("roomid");
 			}
 		} catch (SQLException e) {
 			System.out.println(e);
 		}
 		return "";
 	}

 	
 	//getBedType()					 : gets bed type from roomname
 	//Parameters: roomname
 	public String getBedType(String name) {
 		String query = "select bedtype from Rooms\n" + 
 				"where roomname=?";
 		try(PreparedStatement prep = con.prepareStatement(query)){
 			prep.setString(1, name);
 			try(ResultSet res = prep.executeQuery()){
 				if(res.next())
 					return res.getString("bedtype");
 			}
 		} catch (SQLException e) {
 			System.out.println(e);
 		}
 		return ""; 	}
 	
 	
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


	public static void updateRes(Integer code, String colName, String value) {
		String Updatequery = "Update reservations set ? = ? where Code =  ?";
		try(PreparedStatement pstmt = con.prepareStatement(Updatequery)){
			pstmt.setInt(1, code);
			pstmt.setString(2, colName);
			if (colName == "Adults"){
				int num = Integer.parseInt(value);
				pstmt.setInt(3, num);
			} else{
				pstmt.setString(3, value);
			}
			int rowCount = pstmt.executeUpdate();
			System.out.format("Updated %d records for %s reservations%n", rowCount, code);
			} catch (SQLException e) {
				System.out.println(e);
			}
	}
 	//getReservation()       :  gets Reservation by resCode
 	//Parameters: resCode
 	public LinkedHashMap<String, String> getReservation(int code){
 		String query = "Select FirstName, LastName, Room, CheckIn, CheckOut, Adults, Kids from reservations where Code = ?";
 		LinkedHashMap <String, String> resInfo = new LinkedHashMap<>();
 		try (PreparedStatement prep = con.prepareStatement(query)){
 			prep.setInt(1, code);
 			ResultSet res = prep.executeQuery(query);
 			while (res.next()){
 				resInfo.put("FirstName", res.getString("FirstName"));
 				resInfo.put("LastName", res.getString("LastName"));
 				resInfo.put("Room", res.getString("Room"));
 				resInfo.put("CheckIn", res.getString("Checkin"));
 				resInfo.put("CheckOut", res.getString("CheckOut"));
 				resInfo.put("Adults", res.getString("Adults"));
 				resInfo.put("Kids", res.getString("Kids"));
 			}
 		} catch (SQLException e) {
			System.out.println(e);
		}
 		return resInfo;

 	}
 	
	//resLookup()			 :  gets list of reservations that match search
 	//Parameters: firstname, lastname, list of dates, roomcode, rescode
 	public ArrayList<String> resLookup(String fName, String lName, String dates, String roomCode, String resCode){
 		return new ArrayList<String>();
 	}


 	
}

