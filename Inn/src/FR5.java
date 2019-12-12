// First name
// Last name
// A range of dates
// Room code
// Reservation code


// put all the info in an array; assuming there are empty
   public static String req5(String fName, String lName, String checkIndates, String checkOutdates, String roomCode, String resCode){

        String[] inputs = {fName, lName, checkIndates, checkOutdates, roomCode, resCode};
        int numNull = 0;

        for(int i = 0; i < 6; i++){
            if(i == 3){
                i++;
            }

            if(inputs[i] != ""){
                numNull++;
            }
        }

        String statement = "SELECT * FROM reservations";
        int i = 0;

        if(numNull > 0){
            statement = statement + " WHERE ";

            while(numNull > 0){
                //First Name
                if(i == 0 && inputs[i] != ""){
                    statement = statement + "FirstName = '" + inputs[i] + "'";
                    numNull--;

                    if(numNull > 0){
                        statement = statement + " AND ";
                    }
                }

                //Last Name
                else if(i == 1 && inputs[i]!= ""){
                    statement = statement + "LastName = '" + inputs[i] + "'";
                    numNull--;

                    if(numNull > 0){
                        statement = statement + " AND ";
                    }
                }

                //Date Range
                else if(i == 2 && inputs[i] != ""){
                    statement = statement + "((CheckIn <= '" + inputs[i] + "' AND ";
                    statement = statement + "CheckOut >= '" + inputs[i] + "') OR ";

                    statement = statement + "(CheckIn >= '" + inputs[i] + "' AND ";
                    statement = statement + "CheckIn <= '" + inputs[i] + "') OR";

                    statement = statement + "(CheckIn >= '" + inputs[i] + "' AND ";
                    statement = statement + "CheckOut <= '" + inputs[i+1] + "'))";

                    numNull--;
                    i++;

                    if(numNull > 0){
                        statement = statement + " AND ";
                    }
                }

                //Room Code
                else if(i == 4 && inputs[i] != ""){
                    statement = statement + "Room = '" + inputs[i] + "'";
                    numNull--;

                    if(numNull > 0){
                        statement = statement + " AND ";
                    }
                }

                //Reservation Code
                else if(i == 5 && inputs[i] != ""){
                    statement = statement + "Code = '" + inputs[i] + "'";
                    numNull--;

                    if(numNull > 0){
                        statement = statement + " AND ";
                    }
                }

                i++;
            }
        }
       return statement + ";";
	}
