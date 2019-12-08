package ui;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import db.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.scene.canvas.*;
import javafx.scene.text.*;
import javafx.scene.shape.*;



public class InnReservations extends Application{
	Database DB = new Database();
	
	@Override
	public void start(Stage primaryStage) {
		loginScreen(primaryStage);
	};
	
	public void loginScreen(Stage primaryStage) {
		//login screen
		Pane login = new Pane();
		login.setPrefSize(400,500);
		
		Label title = new Label("The Migler Inn");
		title.setLayoutX(80);
		title.setLayoutY(20);
		title.setStyle("-fx-font: 40 serif; -fx-text-fill: darkolivegreen");
		
		Label welcome = new Label("Enter login credentials");
		welcome.setStyle("-fx-font: 16 serif; -fx-text-fill: darkolivegreen");
		welcome.setLayoutX(120);
		welcome.setLayoutY(160);
		
		TextField username = new TextField("Username");
		username.setLayoutX(110);
		username.setLayoutY(200);
		username.setStyle("-fx-background-color: darkolivegreen; -fx-text-fill: white");
		TextField password = new TextField("Password");
		password.setLayoutX(110);
		password.setLayoutY(230);
		password.setStyle("-fx-background-color: darkolivegreen; -fx-text-fill: white");

		Button submit = new Button("Submit");
		submit.setStyle("-fx-background-color: grey; -fx-text-fill: white");

		String submitIdle = "-fx-background-color: grey; -fx-text-fill: white";
		String submitHover = "-fx-background-color: darkgrey; -fx-text-fill: white";
		submit.setOnMouseEntered(e -> submit.setStyle(submitHover));
		submit.setOnMouseExited(e -> submit.setStyle(submitIdle));
		
		
		submit.setLayoutX(160);
		submit.setLayoutY(280);
		submit.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				String user = username.getText();
				String pass = password.getText();
				//have global list of usernames,passwords in DatabaseClass. Use one entered to sign in.
				try {
					DB.getConnection(user,pass);
				} catch (Exception e) {
					System.out.println(e);
				}
				mainMenu(primaryStage);
			}
		});
		
		login.getChildren().addAll(title, welcome, username, password, submit);
		
		primaryStage.setScene(new Scene(login));
		primaryStage.show();
		
	};
	
	
	
	//starting screen after login, main menu
	public void mainMenu(Stage primaryStage){	
		
		GridPane screen = new GridPane();
		
		Pane left = new Pane();
		left.setPrefSize(400,500);
		left.setStyle("-fx-background-color: darkolivegreen");
		
		Pane right = new Pane();
		right.setPrefSize(400, 500);
		
		//display reservations for today on right pane
		Text today = new Text(40,50, "Today's Reservations");
		today.setFill(Color.DARKOLIVEGREEN);
		today.setFont(Font.font(String.valueOf(java.awt.Font.SERIF), 32.0));
		
        LocalDate localDate = LocalDate.now();
        String res = DB.getTodayRes(localDate);
        Label curRes = new Label(res);
        curRes.setFont(Font.font(String.valueOf(java.awt.Font.SERIF)));
        curRes.setLayoutY(70);
		right.getChildren().addAll(curRes,today);
		
		SplitPane split = new SplitPane();
		split.getItems().setAll(left,right);
		split.setStyle("-fx-box-border: transparent;");
		
		Pane layout = new Pane();
		layout.getChildren().setAll(split);
		screen.add(layout, 0, 20);

		Pane root = new Pane();
		Text welcome = new Text(140, 50, "Welcome to the Migler Inn");
		welcome.setFill(Color.DARKOLIVEGREEN);
		welcome.setFont(Font.font(String.valueOf(java.awt.Font.SERIF), 45.0));
		root.getChildren().add(welcome);
		screen.getChildren().add(root);
		
		//logout button
		Button logout = new Button("Logout");
		final String HOVERED_BUTTON_STYLE = "-fx-font: 12 serif; -fx-background-color: darkolivegreen; -fx-text-fill: white;";
		final String IDLE_BUTTON_STYLE = "-fx-font: 11 serif; -fx-background-color: darkolivegreen; -fx-text-fill: black;";
		logout.setStyle(IDLE_BUTTON_STYLE);
		logout.setLayoutX(350);
		logout.setOnMouseEntered(e -> logout.setStyle(HOVERED_BUTTON_STYLE));
		logout.setOnMouseExited(e -> logout.setStyle(IDLE_BUTTON_STYLE));
		left.getChildren().add(logout);
		logout.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				//have global list of usernames,passwords. Use this one to sign in.
				try {
					DB.dbLogout();
				} catch (SQLException e) {
					System.out.println(e);
				}
				loginScreen(primaryStage);
			}
		});
		
		
		//ROOMS: list of rooms and their rate
		//sorted by popularity
		Button rooms = new Button("See Rooms");
		String roomsIdle = "-fx-font: 15 serif; -fx-background-color: white; -fx-text-fill: darkgreen;";
		String roomsHover = "-fx-font: 15 serif; -fx-background-color: lightgrey; -fx-text-fill: black;";
		rooms.setStyle(roomsIdle);
		rooms.setOnMouseEntered(e -> rooms.setStyle(roomsHover));
		rooms.setOnMouseExited(e -> rooms.setStyle(roomsIdle));
		rooms.setLayoutX(140);
		rooms.setLayoutY(60);
		rooms.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				//stay on same page and display rooms on right
				//call to db dislay list of rooms
			}
		});
		
		//NEW RESERVATION: user fills in form for new reservation
		Button newRes = new Button("New Reservation");
		String newResIdle = "-fx-font: 15 serif; -fx-background-color: white; -fx-text-fill: darkgreen;";
		String newResHover = "-fx-font: 15 serif; -fx-background-color: lightgrey; -fx-text-fill: black;";
		newRes.setStyle(newResIdle);
		newRes.setOnMouseEntered(e -> newRes.setStyle(newResHover));
		newRes.setOnMouseExited(e -> newRes.setStyle(newResIdle));
		newRes.setLayoutX(140);
		newRes.setLayoutY(120);
		newRes.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				newReservation(primaryStage);
			}
		});
		
		//CHANGE RES: make changes to existing reservation
		Button changeRes = new Button("Change Reservation");
		String changeResIdle = "-fx-font: 15 serif; -fx-background-color: white; -fx-text-fill: darkgreen;";
		String changeResHover = "-fx-font: 15 serif; -fx-background-color: lightgrey; -fx-text-fill: black;";
		changeRes.setStyle(changeResIdle);
		changeRes.setOnMouseEntered(e -> changeRes.setStyle(changeResHover));
		changeRes.setOnMouseExited(e -> changeRes.setStyle(changeResIdle));
		changeRes.setLayoutX(140);
		changeRes.setLayoutY(180);
		changeRes.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {				
				//display reservations for today on right pane
				Text resCode = new Text(10,200, "Enter Reservation Code: ");
				//today.setFill(Color.DARKOLIVEGREEN);
				resCode.setFont(Font.font(String.valueOf(java.awt.Font.SERIF), 18.0));
				TextField enterResCode = new TextField();
				enterResCode.setPromptText("i.e. 12345");
				enterResCode.setLayoutX(200);
				enterResCode.setLayoutY(181);
				//enterResCode.setStyle("-fx-background-color: darkolivegreen; -fx-text-fill: white");
				
		        right.getChildren().clear();
				// user enters reservation code on right
				
				Button search = new Button("Search");
				String searchIdle = "-fx-font: 15 serif; -fx-background-color: darkgrey; -fx-text-fill: black;";
				String searchHover = "-fx-font: 15 serif; -fx-background-color: lightgrey; -fx-text-fill: black;";
				search.setStyle(searchIdle);
				search.setOnMouseEntered(e -> search.setStyle(searchHover));
				search.setOnMouseExited(e -> search.setStyle(searchIdle));
				search.setLayoutX(140);
				search.setLayoutY(230);
				right.getChildren().addAll(resCode, enterResCode, search);
				search.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
					public void handle(ActionEvent event) {	
						Integer rCode = Integer.parseInt(enterResCode.getText());
						if(!DB.searchRes(rCode)) {
							//stay on page, enter error message
						}
						else {
							updateRes(primaryStage, left, right, rCode);
						}
					}
					
					
				});	
			}
		});
		
		
		
		left.getChildren().addAll(rooms, newRes, changeRes);
		
		primaryStage.setScene(new Scene(screen));
		primaryStage.setTitle("Migler Inn");
		primaryStage.show();
		
	};
	
	
	//Form for new Reservation
	public void newReservation(Stage primaryStage) {
		//form on left
		//available rooms, recommendations on right
		GridPane screen = new GridPane();
		
		Pane left = new Pane();
		left.setPrefSize(400,500);
		left.setStyle("-fx-background-color: darkolivegreen");
		
		Pane right = new Pane();
		right.setPrefSize(400, 500);
		
		SplitPane split = new SplitPane();
		split.getItems().setAll(left,right);
		split.setStyle("-fx-box-border: transparent;");
		
		Pane layout = new Pane();
		layout.getChildren().setAll(split);
		screen.add(layout, 0, 20);

		Pane root = new Pane();
		Text welcome = new Text(140, 50, "Welcome to the Migler Inn");
		welcome.setFill(Color.DARKOLIVEGREEN);
		welcome.setFont(Font.font(String.valueOf(java.awt.Font.SERIF), 40.0));
		root.getChildren().add(welcome);
		screen.getChildren().add(root);
		
		//"New Reservation"
		Text resMsg = new Text(65, 63, "New Reservation");
		resMsg.setFill(Color.WHITE);
		resMsg.setFont(Font.font(String.valueOf(java.awt.Font.SERIF), 35.0));
		
		
		//Firstname
		TextField fName = new TextField();
		fName.setPromptText("First Name");
		fName.setLayoutX(50);
		fName.setLayoutY(100);
		fName.setPrefWidth(130);
		fName.setStyle("-fx-background-color: white; -fx-text-fill: darkgreen");
		
		//Lastname
		TextField lName = new TextField();
		lName.setPromptText("Last Name");
		lName.setLayoutX(200);
		lName.setLayoutY(100);
		lName.setPrefWidth(130);
		lName.setStyle("-fx-background-color: white; -fx-text-fill: darkgreen");
		
		//RoomCode
		TextField rCode = new TextField();
		rCode.setPromptText("Room Code");
		rCode.setLayoutX(50);
		rCode.setLayoutY(140);
		rCode.setPrefWidth(90);
		rCode.setStyle("-fx-background-color: white; -fx-text-fill: darkgreen");
		
		//BedType
		TextField bedType = new TextField();
		bedType.setPromptText("Bed Type");
		bedType.setLayoutX(238);
		bedType.setLayoutY(140);
		bedType.setPrefWidth(90);
		bedType.setStyle("-fx-background-color: white; -fx-text-fill: darkgreen");
		
		//Arrival Date
		DatePicker arrival = new DatePicker();
		arrival.setPromptText("Checkin");
        arrival.setLayoutX(50);
        arrival.setLayoutY(180);
		arrival.setShowWeekNumbers(true);
		arrival.setPrefWidth(130);
        arrival.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) 
            { 
                // get the date picker value 
                LocalDate checkin = arrival.getValue(); 
            } 
        });
	    
        //Departure Date
        DatePicker departure = new DatePicker();
        departure.setShowWeekNumbers(true);
        departure.setPromptText("Checkout");
        departure.setLayoutX(200);
        departure.setLayoutY(180);
        departure.setPrefWidth(130);
        departure.setOnAction(new EventHandler<ActionEvent>() {
	        public void handle(ActionEvent e) 
	        { 
	            // get the date picker value 
	            LocalDate checkout = arrival.getValue(); 
	        } 
	    });
        
        //NumChildren
        TextField numKids = new TextField();
		numKids.setPromptText("Kids");
		numKids.setLayoutX(50);
		numKids.setLayoutY(220);
		numKids.setPrefWidth(60);
		numKids.setStyle("-fx-background-color: white; -fx-text-fill: darkgreen");
	     
		//NumAdults
        TextField numAdults = new TextField();
        numAdults.setPromptText("Adults");
        numAdults.setLayoutX(270);
        numAdults.setLayoutY(220);
        numAdults.setPrefWidth(60);
        numAdults.setStyle("-fx-background-color: white; -fx-text-fill: darkgreen");
		
		//cancel
		Button cancel = new Button("Cancel");
		String cancelIdle = "-fx-font: 15 serif; -fx-background-color: white; -fx-text-fill: darkgreen;";
		cancel.setStyle(cancelIdle);
		String cancelHover = "-fx-font: 15 serif; -fx-background-color: lightgrey; -fx-text-fill: black;";
		cancel.setOnMouseEntered(e -> cancel.setStyle(cancelHover));
		cancel.setOnMouseExited(e -> cancel.setStyle(cancelIdle));
		cancel.setLayoutX(240);
		cancel.setLayoutY(400);
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				mainMenu(primaryStage);
			}
		});
		
		Label AvailRooms = new Label("Available Rooms:");
		AvailRooms.setLayoutX(80);
		AvailRooms.setLayoutY(30);
		AvailRooms.setStyle("-fx-font: 30 serif; -fx-text-fill: darkgrey;");
		Label rooms = new Label("");
		rooms.setLayoutX(60);
		rooms.setLayoutY(80);
		
		//Submit
		Button submit = new Button("Show Available Rooms");
		String submitIdle = "-fx-font: 14 serif; -fx-background-color: white; -fx-text-fill: darkgreen;";
		submit.setStyle(submitIdle);
		String submitHover = "-fx-font: 14 serif; -fx-background-color: lightgrey; -fx-text-fill: black;";
		submit.setOnMouseEntered(e -> submit.setStyle(submitHover));
		submit.setOnMouseExited(e -> submit.setStyle(submitIdle));
		submit.setLayoutX(50);
		submit.setLayoutY(400);
		submit.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				//take to confirmation page displaying data they put in
				String fname = fName.getText();
				String lname = lName.getText();
				String code = rCode.getText();
				String bed = bedType.getText();
				LocalDate checkin = arrival.getValue();
				LocalDate checkout = departure.getValue();
				//checkin and checkout are above
				String adults = numAdults.getText();
				String kids = numKids.getText();
				
				int Y = 90;
				
				ArrayList<String> res = DB.getAvailRooms(code,bed,checkin,checkout,Integer.parseInt(adults+kids));
				for(String r : res) {
					Button option = new Button(r);
					option.setLayoutY(Y);
					option.setMaxWidth(400);
					option.setMinWidth(400);
					String optionIdle = "-fx-background-color: darkolivegreen; -fx-text-fill: white";
					String optionHover = "-fx-background-color: darkgrey; -fx-text-fill: white";
					option.setStyle(optionIdle);
					option.setOnMouseEntered(e -> option.setStyle(optionHover));
					option.setOnMouseExited(e -> option.setStyle(optionIdle));
					Y += 30;
					right.getChildren().add(option);
					
					//set event where if clicked, sends info to confirmation page!
					option.setOnAction(new EventHandler<ActionEvent>() {
						
						@Override
						public void handle(ActionEvent event) {
							//have global list of usernames,passwords. Use this one to sign in.
							String picked = option.getText();
							picked = picked.split("-")[0];
							confirmationPage(primaryStage,fname,lname,code,bed,checkin,checkout,adults,kids,picked);
						}
					});
					
				}
			}
		});
	
		
		left.getChildren().addAll(resMsg,cancel,fName,lName,rCode,bedType,arrival,departure,numKids,numAdults,
				submit);
		right.getChildren().addAll(AvailRooms,rooms);
	
		primaryStage.setScene(new Scene(screen));
		primaryStage.setTitle("Migler Inn");
		primaryStage.show();
		
		
	}
	
	
	//New Reservation Confirmation Page
	public void confirmationPage(Stage primaryStage,String fname,String lname, String code, String bed, 
			LocalDate checkin, LocalDate checkout,
			String adults, String kids, String roomOption) {
		
		
		GridPane screen = new GridPane();
		
		Pane left = new Pane();
		left.setPrefSize(400,500);
		left.setStyle("-fx-background-color: darkolivegreen");
		
		Pane right = new Pane();
		right.setPrefSize(400, 500);
		
		SplitPane split = new SplitPane();
		split.getItems().setAll(left,right);
		split.setStyle("-fx-box-border: transparent;");
		
		Pane layout = new Pane();
		layout.getChildren().setAll(split);
		screen.add(layout, 0, 20);

		Pane root = new Pane();
		Text welcome = new Text(140, 50, "Welcome to the Migler Inn");
		
		welcome.setFill(Color.DARKOLIVEGREEN);
		welcome.setFont(Font.font(String.valueOf(java.awt.Font.SERIF), 40.0));
		root.getChildren().add(welcome);
		screen.getChildren().add(root);
		
		//logout button
		Button logout = new Button("Logout");
		final String HOVERED_BUTTON_STYLE = "-fx-font: 12 cambria; -fx-background-color: darkolivegreen; -fx-text-fill: white;";
		final String IDLE_BUTTON_STYLE = "-fx-font: 11 cambria; -fx-background-color: darkolivegreen; -fx-text-fill: black;";
		logout.setStyle("-fx-font: 11 cambria; -fx-background-color: darkolivegreen; -fx-text-fill: black;");
		logout.setLayoutX(350);
		logout.setOnMouseEntered(e -> logout.setStyle(HOVERED_BUTTON_STYLE));
		logout.setOnMouseExited(e -> logout.setStyle(IDLE_BUTTON_STYLE));
		left.getChildren().add(logout);
		logout.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				//have global list of usernames,passwords. Use this one to sign in.
				loginScreen(primaryStage);
			}
		});

		

		//Check validity of reservation
		Boolean valid = DB.checkNewRes(Integer.parseInt(adults+kids),LocalDate.now(), code);
		Text resMsg = new Text(80, 60, "Confirmation");
		resMsg.setFill(Color.WHITE);
		resMsg.setFont(Font.font(String.valueOf(java.awt.Font.SERIF), 35.0));
		
		
		if(valid) {
			//"Confirmation"
			String details = "\n	First Name: " + fname + "\n\n	Last Name: " + lname 
					+ "\n\n	Room Code: " + code + "\n\n	Bed Type: " + bed 
					+ "\n\n	Checkin Date: " + String.valueOf(checkin) 
					+ "\n\n	Checkout Date: " + String.valueOf(checkout)
					+ "\n\n	Adults: " + adults + "\n\n	Kids: " + kids
					+ "\n\n	Room: " + roomOption;
			Label confirm = new Label(details);
			confirm.setStyle("-fx-font: 12 serif; -fx-text-fill: white;");
			confirm.setLayoutY(100);
			confirm.setLayoutX(80);
			
			Button con = new Button("Book Reservation");
			String conIdle = "-fx-font: 15 serif; -fx-background-color: white; -fx-text-fill: darkgreen;";
			String conHover = "-fx-font: 15 serif; -fx-background-color: lightgrey; -fx-text-fill: black;";
			con.setStyle(conIdle);
			con.setOnMouseEntered(e -> con.setStyle(conHover));
			con.setOnMouseExited(e -> con.setStyle(conIdle));
			con.setLayoutX(70);
			con.setLayoutY(400);
			con.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent event) {
					//have global list of usernames,passwords. Use this one to sign in.
					mainMenu(primaryStage);
				}
			});
			
			
			//back to main menu button
			Button backToMain = new Button("< Back to Main Menu");
			backToMain.setMaxHeight(12);
			final String backHover = "-fx-font: 11 serif; -fx-background-color: darkolivegreen; -fx-text-fill: white;";
			final String backIdle = "-fx-font: 11 serif; -fx-background-color: darkolivegreen; -fx-text-fill: black";
			backToMain.setStyle(backIdle);
			backToMain.setOnMouseEntered(e -> backToMain.setStyle(backHover));
			backToMain.setOnMouseExited(e -> backToMain.setStyle(backIdle));
			backToMain.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent event) {
					mainMenu(primaryStage);
				}
			});
			
			
			//cancel
			Button cancel = new Button("Cancel");
			String cancelIdle = "-fx-font: 15 serif; -fx-background-color: white; -fx-text-fill: darkgreen;";
			cancel.setStyle(cancelIdle);
			String cancelHover = "-fx-font: 15 serif; -fx-background-color: lightgrey; -fx-text-fill: black;";
			cancel.setOnMouseEntered(e -> cancel.setStyle(cancelHover));
			cancel.setOnMouseExited(e -> cancel.setStyle(cancelIdle));
			cancel.setLayoutX(230);
			cancel.setLayoutY(400);
			cancel.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent event) {
					mainMenu(primaryStage);
				}
			});
			
			
			
			left.getChildren().addAll(confirm,con,cancel,backToMain);
		
			//First Name
		} else {
			//"Error: can't make Reservation"
			resMsg = new Text(20, 60, "Error: Unable to Make Reservation");
			resMsg.setFill(Color.WHITE);
			resMsg.setFont(Font.font(String.valueOf(java.awt.Font.SERIF), 22.0));
			resMsg.setLayoutX(20);
			
			Button tryAgain = new Button("Try Again");
			String tryAgainIdle = "-fx-font: 15 serif; -fx-background-color: white; -fx-text-fill: darkgreen;";
			tryAgain.setStyle(tryAgainIdle);
			String tryAgainHover = "-fx-font: 15 serif; -fx-background-color: lightgrey; -fx-text-fill: black;";
			tryAgain.setOnMouseEntered(e -> tryAgain.setStyle(tryAgainHover));
			tryAgain.setOnMouseExited(e -> tryAgain.setStyle(tryAgainIdle));
			tryAgain.setLayoutX(150);
			tryAgain.setLayoutY(200);
			tryAgain.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent event) {
					newReservation(primaryStage);
				}
			});
			left.getChildren().addAll(tryAgain);
		}
		
		left.getChildren().addAll(resMsg);
	
		primaryStage.setScene(new Scene(screen));
		primaryStage.setTitle("Migler Inn");
		primaryStage.show();
	}
		
	public void updateRes(Stage primaryStage, Pane left, Pane right, int code) {
		left.getChildren().clear();
		right.getChildren().clear();
		
		//make call to database to get all information to display here
		
		//Firstname
		TextField fName = new TextField();
		fName.setPromptText("First Name");
		fName.setLayoutX(50);
		fName.setLayoutY(100);
		fName.setPrefWidth(130);
		fName.setStyle("-fx-background-color: white; -fx-text-fill: darkgreen");
		//fName.setText(value);
		//value = result from map('Firstname');
		
		//Lastname
		TextField lName = new TextField();
		lName.setPromptText("Last Name");
		lName.setLayoutX(200);
		lName.setLayoutY(100);
		lName.setPrefWidth(130);
		lName.setStyle("-fx-background-color: white; -fx-text-fill: darkgreen");
		
		//RoomCode
		TextField rCode = new TextField();
		rCode.setPromptText("Room Code");
		rCode.setLayoutX(50);
		rCode.setLayoutY(140);
		rCode.setPrefWidth(90);
		rCode.setStyle("-fx-background-color: white; -fx-text-fill: darkgreen");
		
		//BedType
		TextField bedType = new TextField();
		bedType.setPromptText("Bed Type");
		bedType.setLayoutX(238);
		bedType.setLayoutY(140);
		bedType.setPrefWidth(90);
		bedType.setStyle("-fx-background-color: white; -fx-text-fill: darkgreen");
		
		//Arrival Date
		DatePicker arrival = new DatePicker();
		arrival.setPromptText("Checkin");
        arrival.setLayoutX(50);
        arrival.setLayoutY(180);
		arrival.setShowWeekNumbers(true);
		arrival.setPrefWidth(130);
        arrival.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) 
            { 
                // get the date picker value 
                LocalDate checkin = arrival.getValue(); 
            } 
        });
	    
        //Departure Date
        DatePicker departure = new DatePicker();
        departure.setShowWeekNumbers(true);
        departure.setPromptText("Checkout");
        departure.setLayoutX(200);
        departure.setLayoutY(180);
        departure.setPrefWidth(130);
        departure.setOnAction(new EventHandler<ActionEvent>() {
	        public void handle(ActionEvent e) 
	        { 
	            // get the date picker value 
	            LocalDate checkout = arrival.getValue(); 
	        } 
	    });
        
        //NumChildren
        TextField numKids = new TextField();
		numKids.setPromptText("Kids");
		numKids.setLayoutX(50);
		numKids.setLayoutY(220);
		numKids.setPrefWidth(60);
		numKids.setStyle("-fx-background-color: white; -fx-text-fill: darkgreen");
	     
		//NumAdults
        TextField numAdults = new TextField();
        numAdults.setPromptText("Adults");
        numAdults.setLayoutX(270);
        numAdults.setLayoutY(220);
        numAdults.setPrefWidth(60);
        numAdults.setStyle("-fx-background-color: white; -fx-text-fill: darkgreen");
        
		//cancel
		Button cancel = new Button("Cancel");
		String cancelIdle = "-fx-font: 15 serif; -fx-background-color: white; -fx-text-fill: darkgreen;";
		cancel.setStyle(cancelIdle);
		String cancelHover = "-fx-font: 15 serif; -fx-background-color: lightgrey; -fx-text-fill: black;";
		cancel.setOnMouseEntered(e -> cancel.setStyle(cancelHover));
		cancel.setOnMouseExited(e -> cancel.setStyle(cancelIdle));
		cancel.setLayoutX(240);
		cancel.setLayoutY(400);
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				mainMenu(primaryStage);
			}
		});
		
		Label AvailRooms = new Label("Available Rooms:");
		AvailRooms.setLayoutX(80);
		AvailRooms.setLayoutY(30);
		AvailRooms.setStyle("-fx-font: 30 serif; -fx-text-fill: darkgrey;");
		Label rooms = new Label("");
		rooms.setLayoutX(60);
		rooms.setLayoutY(80);
		
		//Submit
		Button submit = new Button("Show Available Rooms");
		String submitIdle = "-fx-font: 14 serif; -fx-background-color: white; -fx-text-fill: darkgreen;";
		submit.setStyle(submitIdle);
		String submitHover = "-fx-font: 14 serif; -fx-background-color: lightgrey; -fx-text-fill: black;";
		submit.setOnMouseEntered(e -> submit.setStyle(submitHover));
		submit.setOnMouseExited(e -> submit.setStyle(submitIdle));
		submit.setLayoutX(50);
		submit.setLayoutY(400);
		submit.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				//take to confirmation page displaying data they put in
				String fname = fName.getText();
				String lname = lName.getText();
				String code = rCode.getText();
				String bed = bedType.getText();
				LocalDate checkin = arrival.getValue();
				LocalDate checkout = departure.getValue();
				//checkin and checkout are above
				String adults = numAdults.getText();
				String kids = numKids.getText();
				
				int Y = 90;
				
				ArrayList<String> res = DB.getAvailRooms(code,bed,checkin,checkout,Integer.parseInt(adults+kids));
				for(String r : res) {
					Button option = new Button(r);
					option.setLayoutY(Y);
					option.setMaxWidth(400);
					option.setMinWidth(400);
					String optionIdle = "-fx-background-color: darkolivegreen; -fx-text-fill: white";
					String optionHover = "-fx-background-color: darkgrey; -fx-text-fill: white";
					option.setStyle(optionIdle);
					option.setOnMouseEntered(e -> option.setStyle(optionHover));
					option.setOnMouseExited(e -> option.setStyle(optionIdle));
					Y += 30;
					right.getChildren().add(option);
					
					//set event where if clicked, sends info to confirmation page!
					option.setOnAction(new EventHandler<ActionEvent>() {
						
						@Override
						public void handle(ActionEvent event) {
							//have global list of usernames,passwords. Use this one to sign in.
							String picked = option.getText();
							picked = picked.split("-")[0];
							confirmationPage(primaryStage,fname,lname,code,bed,checkin,checkout,adults,kids,picked);
						}
					});
					
				}
			}
		});
	
		
		left.getChildren().addAll(cancel,fName,lName,rCode,bedType,arrival,departure,numKids,numAdults,
				submit);
		right.getChildren().addAll(AvailRooms,rooms);	
	
	}


	
	
	
	
	
public static void main(String[] args) {
	launch(args);
}

}
