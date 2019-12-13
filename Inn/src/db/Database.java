
package db;

import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.io.BufferedReader;
import java.io.FileReader;

public class Database {
 	private static Connection con;
 	

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
 	
 	
 	//restartDB()
 	public void restartDB() {
 		String delete = "DELETE FROM lab7_rooms";
 		String delete2 = "DELETE FROM lab7_reservations";
 		String create = "CREATE TABLE IF NOT EXISTS lab7_rooms (\n" + 
 				"  RoomCode char(5) PRIMARY KEY,\n" + 
 				"  RoomName varchar(30) NOT NULL,\n" + 
 				"  Beds int(11) NOT NULL,\n" + 
 				"  bedType varchar(8) NOT NULL,\n" + 
 				"  maxOcc int(11) NOT NULL,\n" + 
 				"  basePrice DECIMAL(6,2) NOT NULL,\n" + 
 				"  decor varchar(20) NOT NULL,\n" + 
 				"  UNIQUE (RoomName)\n" + 
 				");";
 		String create2 = "CREATE TABLE IF NOT EXISTS lab7_reservations (\n" + 
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
 		String insert = " INSERT INTO lab7_rooms SELECT * FROM INN.rooms;";
 		
 		String insert2 = "INSERT INTO lab7_reservations SELECT CODE, Room,\n" + 
 				"   DATE_ADD(CheckIn, INTERVAL 9 YEAR),\n" + 
 				"   DATE_ADD(Checkout, INTERVAL 9 YEAR),\n" + 
 				"   Rate, LastName, FirstName, Adults, Kids FROM INN.reservations;";
 		
 		try(Statement state = con.createStatement()){
 			state.executeUpdate(delete2);
 			state.executeUpdate(delete);
 			state.executeUpdate(create);
 			state.executeUpdate(create2);
 			state.executeUpdate(insert);
 			state.executeUpdate(insert2);
 		} catch (SQLException e) {
 			System.out.println(e);
 		}
 	}
 	
 	
 	
 	//get all reservations today
 	public String getTodayRes() {
        String sql = "SELECT FirstName, LastName,Room\n" + 
        		" FROM lab7_reservations WHERE CheckIn <= date(now()) AND Checkout > date(now())";
        System.out.println(sql);
        String result = "\t\tFirst Name\t\tLastName\t\tRoomCode\n\n";
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                result += "\t\t" + rs.getString("FirstName") + "\t\t\t";
                result += rs.getString("LastName") + "\t\t\t";
                result += rs.getString("Room") + "\n";
            }
        } catch (Exception e) {
            return "";
        }
        System.out.println(result);
        return result;
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
            br.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        ArrayList<String> rooms = new ArrayList<String>();
        String result = "Room Code<>Room Name<>Beds<>Bed Type" + 
                        "<>Max Occupants<>Base Price<>Decor" + 
                        "<>Room Popularity<>Next Date Available" + 
                        "<>Most Recent Stay<>Most Recent Checkout";
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
                String data = String.format("%s<>%s<>%s<>%s<>%s<>%s<>%s" + 
                                            "<>%s<>%s<>%s<>%s", 
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
            br.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
        
        ArrayList<String> revenue = new ArrayList<String>();
        revenue.add("Room<>Jan<>Feb<>Mar<>Apr<>May<>Jun<>" + 
                    "Jul<>Aug<>Sep<>Oct<>Nov<>Dec<>Year Total");
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

                String data = String.format("%s<>%7.2f<>%7.2f<>%7.2f<>%7.2f<>%7.2f<>" + 
                                            "%7.2f<>%7.2f<>%7.2f<>%7.2f<>%7.2f<>%7.2f<>" +
                                            "%7.2f<>%7.2f", 
                    roomcode, months.get(mos[0]), months.get(mos[1]), months.get(mos[2]),
                    months.get(mos[3]), months.get(mos[4]), months.get(mos[5]), months.get(mos[6]),
                    months.get(mos[7]), months.get(mos[8]), months.get(mos[9]), 
                    months.get(mos[10]), months.get(mos[11]), sum);
                
                year += sum;
                revenue.add(data);
            }
            revenue.add(String.format("<>All<>%.2f<>%.2f<>%.2f<>%.2f<>%.2f<>" + 
                                    "%.2f<>%.2f<>%.2f<>%.2f<>%.2f<>%.2f<>%.2f<>%.2f\n", 
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
 		String query = "SELECT MAX(maxOcc) as occupancy FROM lab7_rooms as occupancy";
 		
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

 	
	//addNewReservation()				 :  inserts new reservation into DB
 	//Parameters: Fields from Confirmation Page
	public boolean addNewReservation(String fname, String lname, String RoomCode, String checkin, 
	 									String checkout, int adults, int kids) {
		int rate;		
		int code;
		String query = "Select BasePrice FROM lab7_rooms where RoomCode = ?";
		try(PreparedStatement prep = con.prepareStatement(query)) {
			prep.setString(1, RoomCode);			
			// get room rate
			ResultSet res = prep.executeQuery();
			if (res.next()) {
				rate = res.getInt("BasePrice");
				
				// get max reservation code and increment by 1
				String getCode = "Select max(Code) code from lab7_reservations";
				try(PreparedStatement prep1 = con.prepareStatement(getCode)) {
					ResultSet res2 = prep1.executeQuery();
					if (res2.next()) {
						code = res2.getInt("code") + 1;
						// insert new reservation
						String addReservation = "INSERT INTO lab7_reservations (Code, Room, CheckIn, CheckOut, Rate, LastName, FirstName, Adults, Kids)" +
									"Values (?, ?, ?, ?, ?, ?, ?, ?, ?)";	
						try(PreparedStatement prep2 = con.prepareStatement(addReservation)) {
							prep2.setInt(1, code);
							prep2.setString(2, RoomCode);
							prep2.setString(3, checkin);
							prep2.setString(4, checkout);
							prep2.setInt(5, rate);
							prep2.setString(6, lname);
							prep2.setString(7, fname);
							prep2.setInt(8, adults);
							prep2.setInt(9, kids);
							prep2.executeUpdate();
						} catch(SQLException e) {
							System.out.println(e);
							return false;
						}	
					}
				} catch(SQLException e) {
					System.out.println(e);
					return false;
				}	
			}
		} catch(SQLException e) {
			System.out.println(e);
			return false;
		}
		return true;
 	}
 	
 	
 	//checkUpdateValid()				 :  returns boolean of validity of reservation based on dates
 	//Parameters: roomCode, checkin, checkout, ResCode
 	public boolean checkUpdateValid(String RoomCode, String checkin, String checkout, int ResCode) {
		// check for date conflict
		String query = "Select * from lab7_reservations res where \n" + 
				" 	res.Room=? and\n" + 
				" 	res.Code != ? and \n" + 
				" 	Checkin <= ? AND CheckOut > ?";
		try(PreparedStatement prep = con.prepareStatement(query)){
			prep.setString(1, RoomCode);
			prep.setInt(2, ResCode);
			prep.setString(3, checkin);
			prep.setString(4, checkout);
			ResultSet res = prep.executeQuery();
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
 		ArrayList<String> resultingRes = new ArrayList<>();

		//Avail rooms on dates
 		String query = "select distinct RoomName from lab7_reservations res join lab7_rooms r on \n" + 
 				"    res.Room = r.RoomCode" +
 				"	where Room not in \n" + 
 				"    (\n" + 
 				"    select Room from lab7_reservations where ? \n" +
 				"         between Checkin and Checkout OR ? \n" +
 				"         between Checkin and Checkout\n" + 
 				"    ) " +
 				"    and maxOcc >= ?";
 		try (PreparedStatement prep = con.prepareStatement(query)){
 			prep.setString(1, checkin);
 			prep.setString(2, checkout);
 			prep.setInt(3, occ);
 			ResultSet r = prep.executeQuery();
 			while(r.next()) {
 				resultingRes.add(r.getString("RoomName"));
 			} 	 			
 		} catch (SQLException e) {
 			System.out.println(e);
 		}	
 		
 		//Avail rooms on similar dates
 		ArrayList<String> simCheckin = new ArrayList<>();
 		ArrayList<String> simCheckout = new ArrayList<>();
 		simCheckin.add(checkin);
 		simCheckout.add(checkout);
 		LocalDate arrival = LocalDate.parse(checkin);
 		LocalDate departure = LocalDate.parse(checkout);

		LocalDate add = arrival.plusDays(Long.valueOf(1));
		LocalDate sub = arrival.minusDays(Long.valueOf(1));
		simCheckin.add(add.toString());
		simCheckout.add(sub.toString());
		
		LocalDate add1 = departure.plusDays(Long.valueOf(1));
		LocalDate sub1 = departure.minusDays(Long.valueOf(1));
		simCheckout.add(add1.toString());
		simCheckout.add(sub1.toString());
		
 		for(int i = 0; i < simCheckin.size(); i++) {
 			for(int j = 0; j < simCheckout.size(); j++) {
 		 		try (PreparedStatement prep = con.prepareStatement(query)){
 		 			if(LocalDate.parse(simCheckout.get(j)).compareTo(LocalDate.parse(simCheckin.get(i)))<= 0) {
 		 				continue;
 		 			}
 		 			prep.setString(1, simCheckin.get(i));
 		 			prep.setString(2, simCheckout.get(j));
 		 			prep.setInt(3, occ);
 		 			ResultSet r = prep.executeQuery();
 		 			while(r.next()) {
 		 				String name = r.getString("RoomName");
 		 				if((!resultingRes.contains(name)) &&
 		 						(!resultingRes.contains(name + "~*~" + simCheckin.get(i) + "~*~" + simCheckout.get(j))))
 		 					resultingRes.add(r.getString("RoomName") + "~*~" + simCheckin.get(i) + "~*~" + simCheckout.get(j));
 		 			} 	 			
 		 		} catch (SQLException e) {
 		 			System.out.println(e);
 		 		}
 			}
 		}
 		return resultingRes;
 	}
 	
 	
 	//getAvailRooms()				 :  gets list of available rooms
 	//Parameters: Info from new res page
 	public ArrayList<String> getAvailRooms(String code,String bed, String checkin, String checkout,int occ) {
 		//returns int of number of rooms found, if none found, call another to get 5 suggestions
 		ArrayList<String> finalResult = new ArrayList<>();
 		boolean c = false;
 		String query = "select distinct RoomName from lab7_reservations res join lab7_rooms r on \n" + 
 				"    res.Room = r.RoomCode" +
 				"	where Room not in \n" + 
 				"    (\n" + 
 				"    select Room from lab7_reservations where ? \n" +
 				"         between Checkin and Checkout OR ? \n" +
 				"         between Checkin and Checkout\n" + 
 				"    ) " +
 				"    and maxOcc >= ?";
 		
 		if(!code.equalsIgnoreCase("any")) {
 			query += " AND res.room= ?";
 			c = true;
 		}
 		
 		if(!bed.equals("Any")) 
 			query += " AND bedType LIKE '" + bed + "'";
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
            		finalResult.add(room);
            	}
            }
        } catch (SQLException e) {
            System.out.println(e);
        } 
 		return finalResult;
 	}
 	
 	
 	//getRoomCode()					 : gets room code
 	//Parameters: roomname
 	public String getRoomCode(String name) {
 		String query = "select RoomCode from lab7_rooms\n" + 
 				"where roomname=?";
 		try(PreparedStatement prep = con.prepareStatement(query)){
 			prep.setString(1, name);
 			try(ResultSet res = prep.executeQuery()){
 				if(res.next())
 					return res.getString("RoomCode");
 			}
 		} catch (SQLException e) {
 			System.out.println(e);
 		}
 		return "";
 	}

 	
 	//getBedType()					 : gets bed type from roomname
 	//Parameters: roomname
 	public String getBedType(String name) {
 		String query = "select bedtype from lab7_rooms\n" + 
 				"where roomname=?";
 		try(PreparedStatement prep = con.prepareStatement(query)){
 			prep.setString(1, name);
 			ResultSet res = prep.executeQuery();
 			if(res.next())
 				return res.getString("bedtype");
 		} catch (SQLException e) {
 			System.out.println(e);
 		}
 		return ""; 	}
 	
 	
 	//getTotalCost()				 :  gets total cost of reservation
 	//Parameters: roomCode, checkin, checkout 
 	public double getTotalCost(String code, String checkin,String checkout) {
 		String rateQuery = "select BasePrice from Rooms where RoomId=?";
 		int rate = 0;
 		double cost = 0;
 		try (PreparedStatement prep = con.prepareStatement(rateQuery)){
 			prep.setString(1, code);
 			ResultSet res = prep.executeQuery();
 			if(res.next())
 				rate = res.getInt("BasePrice");
 		} catch (SQLException e) {
 			System.out.println(e);
 		}
 		System.out.println("THE CHECKIN DAY: " + checkin);
 		String dayQuery = "SELECT BusinessDays\n" + 
 				", (TIMESTAMPDIFF(DAY, ?, ?))\n" + 
 				"    - BusinessDays\n" + 
 				"    As WeekendDays\n" + 
 				"FROM (\n" + 
 				"    SELECT (TIMESTAMPDIFF(DAY, ?, ?))\n" + 
 				"        - (TIMESTAMPDIFF(WEEK, ?, ?) * 2)\n" + 
 				"        - (CASE WHEN DAYOFWEEK(?) = 1 THEN 1 ELSE 0 END)\n" + 
 				"        - (CASE WHEN DAYOFWEEK(?) = 1 THEN 1 ELSE 0 END) \n" + 
 				"        As BusinessDays\n" + 
 				") a";
 		int weekDays = 0;
 		int weekends = 0;
 		try (PreparedStatement prep2 = con.prepareStatement(dayQuery)){
 			prep2.setString(1, checkin);
 			prep2.setString(2, checkout);
 			prep2.setString(3, checkin);
 			prep2.setString(4, checkout);
 			prep2.setString(5, checkin);
 			prep2.setString(6, checkout);
 			prep2.setString(7, checkin);
 			prep2.setString(8, checkout);
 			System.out.println(prep2);
 			ResultSet res2 = prep2.executeQuery();
 			if(res2.next()) {
 				weekDays = res2.getInt("BusinessDays");
 				weekends = res2.getInt("WeekendDays");
 			}
 			
 		} catch (SQLException e) {
 			System.out.println(e);
 		}
 		//Number of weekdays multipled by room base rate
 		cost = (rate * weekDays);
 		//Number of weekend days multiplied by 110% of the room base rate
 		double oneTenPerc = (rate * 1.10);
 		System.out.println("110% of rate " + rate + " = " + rate*1.10);
 		
 		cost += (weekends * oneTenPerc);
 		System.out.println("COST AFTER ADDING WEEKEND COST: " + cost);
 		
 		//An 18% tourism tax applied to the total of the above two calculations
 		System.out.println("cost: " + cost + " * .18) " +  " = " + cost+(cost*.18));

 		cost = cost+(cost*.18);
 		return cost;
 	}

 	
 	//deleteReservation()			 :	deletes reservation
 	//Parameters: reservation code
 	public void deleteRes(int rCode){
 		try (PreparedStatement prep = con.prepareStatement("DELETE FROM lab7_reservations WHERE Code=?")){
 			prep.setInt(1, rCode);
 			prep.executeUpdate();
 		} catch (SQLException e) {
 			System.out.println(e);
 		}
 	}
 	
 
 	//searchRes()			 :  searches for reservation, T if valid, else F
 	//Parameters: reservation code
 	public boolean searchRes(int Code) throws SQLException {
 		String query = "SELECT LastName FROM lab7_reservations where Code = ? ";

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


	public void updateRes(Integer resCode, String fName, String lName, 
			String checkin, String checkout, int adults, int kids) {
		String updateQuery = "UPDATE lab7_reservations SET FirstName=? ,"
				+ "LastName=?, "
				+ "CheckIn=?, "
				+ "Checkout=?, "
				+ "Adults=?, "
				+ "Kids=? "
				+ "WHERE Code=?";
 		try (PreparedStatement prep = con.prepareStatement(updateQuery)){
 			prep.setString(1, fName);
 			prep.setString(2, lName);
 			prep.setString(3, checkin);
 			prep.setString(4, checkout);
 			prep.setInt(5, adults);
 			prep.setInt(6, kids);
 			prep.setInt(7, resCode);
 			prep.executeUpdate();

 		} catch (SQLException e) {
            System.out.println(e);
        } 


	}
 	//getReservation()       :  gets Reservation by resCode
 	//Parameters: resCode
 	public LinkedHashMap<String, String> getReservation(int code){
 		String query = "Select * from lab7_reservations res join lab7_rooms r on res.Room=r.RoomCode where Code = ?";
 		LinkedHashMap <String, String> resInfo = new LinkedHashMap<>();
 		try (PreparedStatement prep = con.prepareStatement(query)){
 			prep.setInt(1, code);
 			ResultSet res = prep.executeQuery();
 			if (res.next()){
 				resInfo.put("FirstName", res.getString("FirstName"));
 				resInfo.put("LastName", res.getString("LastName"));
 				resInfo.put("Room", res.getString("Room"));
 				resInfo.put("CheckIn", res.getString("Checkin"));
 				resInfo.put("CheckOut", res.getString("CheckOut"));
 				resInfo.put("Adults", res.getString("Adults"));
 				resInfo.put("Kids", res.getString("Kids"));
 				resInfo.put("BedType", res.getString("BedType"));
 				resInfo.put("RoomName", res.getString("RoomName"));
 			}
 		} catch (SQLException e) {
			System.out.println(e);
		}
 		return resInfo;

 	}
 	
 	public String getAllInfo(ResultSet res) throws SQLException {
 		String code = String.valueOf(res.getInt("Code"));
 		String room = res.getString("Room");
 		String checkin = res.getString("CheckIn");
 		String checkout = res.getString("CheckOut");
 		String rate = String.valueOf(res.getInt("Rate"));
 		String lName = res.getString("LastName");
 		String fName = res.getString("FirstName");
 		String adults = String.valueOf(res.getInt("Adults"));
 		String kids = String.valueOf(res.getInt("Kids"));
 		String roomName = res.getString("RoomName");
        String data = String.format("%-20s<>%-20s<>%-45s<>%-20s<>%-35s<>%-30s<>%-20s<>%-20s<>%-20s<>%s%n", code.toString(),
        		room,roomName, checkin,checkout, rate.toString(), lName, fName, adults.toString(), kids.toString());
 		return data;
 	}
 	
	//resLookup()			 :  gets list of reservations that match search
 	//Parameters: firstname, lastname, list of dates, roomcode, rescode
 	public ArrayList<String> resLookup(String fName, String lName, String dates, String roomCode, String resCode){
 		ArrayList<String> resultingRes = new ArrayList<String>();
 		String base = "SELECT * From lab7_reservations res join lab7_rooms r on"
 				+ " res.Room=r.RoomCode where ";
 		
 		//fName
 		if(fName.length() != 0) {
 			String FnameQuery = base + "FirstName LIKE ?";
 	 		try (PreparedStatement prep = con.prepareStatement(FnameQuery)){
 	 			prep.setString(1, fName);
 	 			ResultSet r = prep.executeQuery();
 	 			while(r.next()) {
 	 				resultingRes.add(getAllInfo(r));
 	 			}
 	 		} catch (SQLException e) {
 	 			System.out.println(e);
 	 		}	
 		}
 		
 		
 		//lName
 		if(lName.length() != 0) {
 			String LnameQuery = base + "LastName LIKE ?";
 	 		try (PreparedStatement prep2 = con.prepareStatement(LnameQuery)){
 	 			prep2.setString(1, lName);
 	 			ResultSet r2 = prep2.executeQuery();
 	 			while(r2.next()) {
 	 				String result = getAllInfo(r2);
 	 				if(!resultingRes.contains(result)) {
 	 					resultingRes.add(result);
 	 				}
 	 			}
 	 		} catch (SQLException e) {
 	 			System.out.println(e);
 	 		}	
 		}
 		
 		//dates
 		if(dates.length() != 0) {
 			String[] splitDates = dates.split(",");
 			for(String d : splitDates) {
 	 			String datesQuery = base;
 				datesQuery += "? BETWEEN checkin and checkout";
	 	 		try (PreparedStatement prep3 = con.prepareStatement(datesQuery)){
	 	 			prep3.setString(1, d);
	 	 			ResultSet r3 = prep3.executeQuery();
	 	 			while(r3.next()) {
	 	 				String result = getAllInfo(r3);
	 	 				if(!resultingRes.contains(result)) {
	 	 					resultingRes.add(result);
	 	 				}	 	 			
	 	 			}
	 	 		} catch (SQLException e) {
	 	 			System.out.println(e);
	 	 		}
 			}
 		}
 		
 		//roomCode
 		if(roomCode.length() != 0) {
 			String roomCodeQuery = base + "res.Room LIKE ?";
 	 		try (PreparedStatement prep4 = con.prepareStatement(roomCodeQuery)){
 	 			prep4.setString(1, roomCode);
 	 			ResultSet r4 = prep4.executeQuery();
 	 			while(r4.next()) {
 	 				String result = getAllInfo(r4);
 	 				if(!resultingRes.contains(result)) {
 	 					resultingRes.add(result);
 	 				} 	 			
 	 			}
 	 		} catch (SQLException e) {
 	 			System.out.println(e);
 	 		}	
 		}
 		
 		//resCode
 		if(resCode.length() != 0) {
 			String resCodeQuery = base + "res.Code LIKE ?";
 	 		try (PreparedStatement prep5 = con.prepareStatement(resCodeQuery)){
 	 			prep5.setInt(1, Integer.parseInt(resCode));
 	 			ResultSet r5 = prep5.executeQuery();
 	 			while(r5.next()) {
 	 				String result = getAllInfo(r5);
 	 				if(!resultingRes.contains(result)) {
 	 					resultingRes.add(result);
 	 				} 	 			
 	 			}
 	 		} catch (SQLException e) {
 	 			System.out.println(e);
 	 		}	
 		}
        String joined = String.format("%-20s<>%-20s<>%-45s<>%-20s<>%-35s<>%-30s<>%-20s<>%-20s<>%-20s<>%s%n", "Code", "Room", "RoomName", "CheckIn", "CheckOut",
        		"Rate", "LastName", "FirstName", "Adults", "Kids");
        resultingRes.add(0, joined);
 		return resultingRes;
 	}
}

