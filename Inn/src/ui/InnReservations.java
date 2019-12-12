package ui;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import db.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.scene.text.*;
import javafx.scene.shape.*;
import javafx.scene.input.*;


public class InnReservations extends Application{
	Database DB = new Database();
	
	
	@Override
	public void start(Stage primaryStage) throws ClassNotFoundException, SQLException {
		DB.getConnection();
		mainMenu(primaryStage);
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
			    System.exit(0);
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
                ArrayList<String> rooms = DB.seeRooms();
                showRooms(primaryStage, right, rooms);
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
						try {
							if(!DB.searchRes(rCode)) {
								Text error = new Text(60, 140, "Error: Reservation doesn't exist.");
								error.setFill(Color.DARKOLIVEGREEN);
								error.setFont(Font.font(String.valueOf(java.awt.Font.SERIF), 20.0));
								right.getChildren().add(error);
							}
							else {
								updateRes(primaryStage, left, right, rCode);
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}					
				});	
			}
		});
		
		
		//CANCEL RESERVATION
		Button cancel = new Button("Cancel Reservation");
		String cancelIdle = "-fx-font: 15 serif; -fx-background-color: white; -fx-text-fill: darkgreen;";
		String cancelHover = "-fx-font: 15 serif; -fx-background-color: lightgrey; -fx-text-fill: black;";
		cancel.setStyle(cancelIdle);
		cancel.setOnMouseEntered(e -> cancel.setStyle(cancelHover));
		cancel.setOnMouseExited(e -> cancel.setStyle(cancelIdle));
		cancel.setLayoutX(140);
		cancel.setLayoutY(240);
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				Text resCode = new Text(10,200, "Enter Reservation Code: ");
				resCode.setFont(Font.font(String.valueOf(java.awt.Font.SERIF), 18.0));
				TextField enterResCode = new TextField();
				enterResCode.setPromptText("i.e. 12345");
				enterResCode.setLayoutX(200);
				enterResCode.setLayoutY(181);
				
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
						try {
							if(!DB.searchRes(rCode)) {
								Text error = new Text(60, 140, "Error: Reservation doesn't exist.");
								error.setFill(Color.DARKOLIVEGREEN);
								error.setFont(Font.font(String.valueOf(java.awt.Font.SERIF), 20.0));
								right.getChildren().add(error);
							}
							else {
								DB.deleteRes(rCode);
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}					
				});		
			}
		});
		
		
		//RESERVATION LOOKUP
        Button search = new Button("Reservation Lookup");
		String searchIdle = "-fx-font: 15 serif; -fx-background-color: white; -fx-text-fill: darkgreen;";
		String searchHover = "-fx-font: 15 serif; -fx-background-color: lightgrey; -fx-text-fill: black;";
		search.setStyle(searchIdle);
		search.setOnMouseEntered(e -> search.setStyle(searchHover));
		search.setOnMouseExited(e -> search.setStyle(searchIdle));
		search.setLayoutX(140);
		search.setLayoutY(300);
		search.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				reservationLookup(primaryStage, left, right);
			}
		});

        // REVENUE
        Button rev = new Button("Revenue");
		String revIdle = "-fx-font: 15 serif; -fx-background-color: white; -fx-text-fill: darkgreen;";
		String revHover = "-fx-font: 15 serif; -fx-background-color: lightgrey; -fx-text-fill: black;";
		rev.setStyle(revIdle);
		rev.setOnMouseEntered(e -> rev.setStyle(revHover));
		rev.setOnMouseExited(e -> rev.setStyle(revIdle));
		rev.setLayoutX(140);
		rev.setLayoutY(360);
		rev.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
                ArrayList<String> rev = DB.getRev();
                showRev(primaryStage, right, rev);
				//stay on same page and display rooms on right
				//call to db dislay list of rooms
			}
		});
		
		left.getChildren().addAll(rooms, newRes, changeRes, cancel, search, rev);
		
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
		ChoiceBox<String> bedType = new ChoiceBox<>();
		bedType.setValue("Any");
		bedType.getItems().addAll("King", "Queen", "Any");
		bedType.setLayoutX(220);
		bedType.setLayoutY(140);
		bedType.setPrefWidth(110);
		bedType.setStyle("-fx-font: 15 serif; -fx-background-color: white; -fx-text-fill: lightgrey");

		//Arrival Date
		DatePicker arrival = new DatePicker();
		arrival.setPromptText("Checkin");
        arrival.setLayoutX(50);
        arrival.setLayoutY(180);
		arrival.setPrefWidth(130);
	    
        //Departure Date
        DatePicker departure = new DatePicker();
        departure.setPromptText("Checkout");
        departure.setLayoutX(200);
        departure.setLayoutY(180);
        departure.setPrefWidth(130);
        
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
				right.getChildren().clear();
				//take to confirmation page displaying data 
				String fname = fName.getText();
				String lname = lName.getText();
				String bed = bedType.getValue();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				String checkinFormat = (arrival.getValue()).format(formatter);
				String checkoutFormat = (departure.getValue()).format(formatter);
				String adults = numAdults.getText();
				String kids = numKids.getText();
				String code = rCode.getText();
				boolean valid = true;
				if(!code.equalsIgnoreCase("any")) {
					//valid = DB.checkDateValid(Integer.parseInt(code), checkin.toString(), checkout.toString(), 00000);
					//get results from getAvailRooms with code entered
				}
				int Y = 90;
				System.out.println(DB.getMaxOcc());
				if(Integer.parseInt(adults)+Integer.parseInt(kids) > DB.getMaxOcc()) {
					valid = false;
				}
				if(!valid) {
					//"Error: can't make Reservation"
					Text resMsg = new Text(20, 60, "Error: Unable to Make Reservation");
					resMsg.setFill(Color.WHITE);
					resMsg.setFont(Font.font(String.valueOf(java.awt.Font.SERIF), 22.0));
					resMsg.setLayoutX(20);
					resMsg.setLayoutY(270);
					
					Text none = new Text("None");
					none.setFill(Color.BLACK);
					none.setFont(Font.font(String.valueOf(java.awt.Font.SERIF), 20.0));
					none.setLayoutX(165);
					none.setLayoutY(100);
					right.getChildren().add(none);
					left.getChildren().add(resMsg);
				} else {
					ArrayList<String> res = DB.getAvailRooms(code, bed, checkinFormat, checkoutFormat, Integer.parseInt(adults+kids));
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
								String picked = option.getText();
								String resCode = DB.getRoomCode(picked);
								String bedType = DB.getBedType(picked);
								//DB.getTotalCost(Integer.parseInt(code), checkin,checkout);
								confirmationPage(primaryStage,fname,lname,resCode,bedType,checkinFormat,checkoutFormat,adults,kids,picked);
							}
						});
					}
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
			String checkin, String checkout,
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
				try {
					DB.dbLogout();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					DB.dbLogout();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					DB.dbLogout();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			    System.exit(0);
			    
			}
		});
		Text resMsg = new Text(80, 60, "Confirmation");
		resMsg.setFill(Color.WHITE);
		resMsg.setFont(Font.font(String.valueOf(java.awt.Font.SERIF), 35.0));
	
		//"Confirmation"
		String details = "\n	First Name: " + fname + "\n\n	Last Name: " + lname 
				+ "\n\n	Room Code: " + code + "\n\n	Bed Type: " + bed 
				+ "\n\n	Checkin Date: " + checkin 
				+ "\n\n	Checkout Date: " + checkout
				+ "\n\n	Adults: " + adults + "\n\n	Kids: " + kids
				+ "\n\n	Room: " + roomOption;
		Label confirm = new Label(details);
		confirm.setStyle("-fx-font: 12 serif; -fx-text-fill: white;");
		confirm.setLayoutY(80);
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
				DB.addNewReservation(fname, lname, code, checkin,
						checkout, Integer.parseInt(adults), Integer.parseInt(kids));
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

		left.getChildren().addAll(confirm,con,cancel,backToMain,resMsg);
		
		primaryStage.setScene(new Scene(screen));
		primaryStage.setTitle("Migler Inn");
		primaryStage.show();
	}
	
    // displays room information
    public void showRooms(Stage primaryStage, Pane right, ArrayList<String> roomData) {
        right.getChildren().clear();
        ScrollPane scrollpane = new ScrollPane();

        scrollpane.setPrefViewportWidth(right.getWidth());
        scrollpane.setPrefViewportHeight(200);
        scrollpane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        
        VBox group = new VBox();
        for(int i = 0; i < roomData.size(); i++){
            HBox row = new HBox();
            row.setSpacing(10);
            String[] temp;
            temp = roomData.get(i).split("<>");
            Label room = new Label(temp[0]);
            room.setPrefWidth(75);
            room.setMinWidth(75);
            room.setMaxWidth(75);

            Label name = new Label(temp[1]);
            name.setPrefWidth(175);
            name.setMinWidth(175);
            name.setMaxWidth(175);

            Label beds = new Label(temp[2]);
            beds.setPrefWidth(50);
            beds.setMinWidth(50);
            beds.setMaxWidth(50);

            Label bedType = new Label(temp[3]);
            bedType.setPrefWidth(75);
            bedType.setMinWidth(75);
            bedType.setMaxWidth(75);

            Label maxOcc = new Label(temp[4]);
            maxOcc.setPrefWidth(100);
            maxOcc.setMinWidth(100);
            maxOcc.setMaxWidth(100);

            Label price = new Label(temp[5]);
            price.setPrefWidth(75);
            price.setMaxWidth(75);
            price.setMinWidth(75);

            Label decor = new Label(temp[6]);
            decor.setPrefWidth(75);
            decor.setMaxWidth(75);
            decor.setMinWidth(75);

            Label pop = new Label(temp[7]);
            pop.setPrefWidth(1110);
            pop.setMinWidth(110);
            pop.setMaxWidth(110);

            Label date = new Label(temp[8]);
            date.setPrefWidth(130);
            date.setMinWidth(130);
            date.setMaxWidth(130);

            Label nights = new Label(temp[9]);
            nights.setPrefWidth(120);
            nights.setMinWidth(120);
            nights.setMaxWidth(120);

            Label checkout = new Label(temp[10]);
            checkout.setPrefWidth(150);
            checkout.setMinWidth(150);
            checkout.setMaxWidth(150);

            row.getChildren().addAll(room, name, beds, bedType, maxOcc, price, decor, pop, date, nights, checkout);
            group.getChildren().add(row);
        }
        scrollpane.setContent(group);
        right.getChildren().addAll(new VBox(30), scrollpane);
    
    }

    public void showRev(Stage primaryStage, Pane right, ArrayList<String> rev){
       right.getChildren().clear();
       ScrollPane scrollpane = new ScrollPane();
       scrollpane.setPrefViewportWidth(right.getWidth());
       scrollpane.setPrefViewportHeight(300);
       scrollpane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
       scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

       VBox group = new VBox();
       for (int i = 0; i < rev.size(); i++) {
            HBox row = new HBox();
            String[] temp = rev.get(i).split("<>");
            int width = 75;
            for (int j = 0; j < 14; j++) {
                Label l = new Label(temp[j]);
                l.setPrefWidth(width);
                l.setMinWidth(width);
                l.setMaxWidth(width);
                row.getChildren().add(l);
            }

            //Label text = new Label(rev.get(i));
            //text.setFont(Font.font(String.valueOf(java.awt.Font.SERIF), 18));
            group.getChildren().add(row);
       }

       scrollpane.setContent(group);
       right.getChildren().add(scrollpane);
    }
	
	//Update existing reservation	
	public void updateRes(Stage primaryStage, Pane left, Pane right, int resCode) {
		left.getChildren().clear();
		right.getChildren().clear();
		
		//make call to database to get all information to display here
		LinkedHashMap <String, String> resInfo = DB.getReservation(resCode);
		System.out.println(resInfo.toString());
		//Change Res
		Text change = new Text(70, 50, "Reservation : " + resCode);
		change.setFill(Color.WHITE);
		change.setFont(Font.font(String.valueOf(java.awt.Font.SERIF), 30.0));
		
		//Room
		Text room = new Text(80, 100, resInfo.get("Room"));
		room.setFill(Color.WHITE);
		room.setFont(Font.font(String.valueOf(java.awt.Font.SERIF), 20.0));
		
		//Firstname
		TextField fName = new TextField();
		fName.setPromptText(resInfo.get("FirstName"));
		fName.setLayoutX(50);
		fName.setLayoutY(140);
		fName.setPrefWidth(130);
		fName.setStyle("-fx-background-color: white; -fx-text-fill: darkgreen");
		
		//Lastname
		TextField lName = new TextField();
		lName.setPromptText(resInfo.get("LastName"));
		lName.setLayoutX(200);
		lName.setLayoutY(140);
		lName.setPrefWidth(130);
		lName.setStyle("-fx-background-color: white; -fx-text-fill: darkgreen");
		
		//Arrival Date
		DatePicker arrival = new DatePicker();
		arrival.setPromptText(resInfo.get("CheckIn"));
        arrival.setLayoutX(50);
        arrival.setLayoutY(180);
		arrival.setShowWeekNumbers(true);
		arrival.setPrefWidth(130);
	    
        //Departure Date
        DatePicker departure = new DatePicker();
        departure.setShowWeekNumbers(true);
        departure.setPromptText(resInfo.get("CheckOut"));
        departure.setLayoutX(200);
        departure.setLayoutY(180);
        departure.setPrefWidth(130);
        
        //NumChildren
        TextField numKids = new TextField();
		numKids.setPromptText(resInfo.get("Kids"));
		numKids.setLayoutX(50);
		numKids.setLayoutY(220);
		numKids.setPrefWidth(60);
		numKids.setStyle("-fx-background-color: white; -fx-text-fill: darkgreen");
	     
		//NumAdults
        TextField numAdults = new TextField();
        numAdults.setPromptText(resInfo.get("Adults"));
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
		
		//Submit
		Button submit = new Button("Submit");
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
				//DB.checkDateValid(resInfo.get("Room"), arrival.getValue().toString(), departure.getValue().toString(), resCode);
				//check if different reservation exists in room on dates
				//check if new occupancy is valid
				if(Integer.parseInt(numAdults.getText()+numKids.getText()) > DB.getMaxOcc()) {
					
				}
				//then confirmation page
				confirmationPage(primaryStage,fName.getText(),lName.getText(),
						String.valueOf(resCode),resInfo.get("BedType"),arrival.getValue().toString(),departure.getValue().toString(),
						numAdults.getText(),numKids.getText(),resInfo.get("RoomName"));

			}
		});
		left.getChildren().addAll(cancel,fName,lName,arrival,departure,numKids,numAdults,
				submit,change,room);
	}
	
	
	//reservation lookup
	public void reservationLookup(Stage primaryStage, Pane left, Pane right) {
		left.getChildren().clear();
		right.getChildren().clear();
		
		Text title = new Text(50, 70, "Enter Search Criteria");
		title.setFill(Color.WHITE);
		title.setFont(Font.font(String.valueOf(java.awt.Font.SERIF), 35.0));
		
		//Firstname
		TextField fName = new TextField();
		fName.setPromptText("First Name");
		fName.setLayoutX(50);
		fName.setLayoutY(140);
		fName.setPrefWidth(130);
		fName.setStyle("-fx-background-color: white; -fx-text-fill: darkgreen");
		
		//Lastname
		TextField lName = new TextField();
		lName.setPromptText("Last Name");
		lName.setLayoutX(200);
		lName.setLayoutY(140);
		lName.setPrefWidth(130);
		lName.setStyle("-fx-background-color: white; -fx-text-fill: darkgreen");
		
		//Arrival Date
		TextField dates = new TextField();
		dates.setPromptText("Possible Dates");
        dates.setLayoutX(50);
        dates.setLayoutY(200);
		dates.setPrefWidth(280);
		dates.setStyle("-fx-background-color: white; -fx-text-fill: darkgreen");


        
        //Roomcode
        TextField roomCode = new TextField();
        roomCode.setPromptText("Room Code");
        roomCode.setLayoutX(50);
        roomCode.setLayoutY(260);
        roomCode.setPrefWidth(130);
		roomCode.setStyle("-fx-background-color: white; -fx-text-fill: darkgreen");

        
        //Reservationcode
        TextField resCode = new TextField();
        resCode.setPromptText("Reservation Code");
        resCode.setLayoutX(200);
        resCode.setLayoutY(260);
        resCode.setPrefWidth(130);
		resCode.setStyle("-fx-background-color: white; -fx-text-fill: darkgreen");

        
      
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
		
		//Submit
		Button submit = new Button("Search");
		String submitIdle = "-fx-font: 15 serif; -fx-background-color: white; -fx-text-fill: darkgreen;";
		submit.setStyle(submitIdle);
		String submitHover = "-fx-font: 15 serif; -fx-background-color: lightgrey; -fx-text-fill: black;";
		submit.setOnMouseEntered(e -> submit.setStyle(submitHover));
		submit.setOnMouseExited(e -> submit.setStyle(submitIdle));
		submit.setLayoutX(90);
		submit.setLayoutY(400);
		submit.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				DB.resLookup(fName.getText(), lName.getText(), dates.getText(), roomCode.getText(), resCode.getText());
			}
		});
		left.getChildren().addAll(cancel,fName,lName,dates,roomCode,resCode,
				title,submit);
		
	}


	
	
	
	
	
public static void main(String[] args) {
	launch(args);
}

}
