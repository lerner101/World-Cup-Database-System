import java.sql.* ;
import java.sql.Date;
import java.util.*;

public class Soccer {

    public static void main (String[] args) throws SQLException {
        // Unique table names.  Either the user supplies a unique identifier as a command line argument, or the program makes one up.
        String tableName = "";
        int sqlCode = 0;      // Variable to hold SQLCODE
        String sqlState = "00000";  // Variable to hold SQLSTATE

        if (args.length > 0)
            tableName += args[0];
        else
            tableName += "exampletbl";

        // Register the driver.  You must register the driver before you can use it.
        try {
            DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
        } catch (Exception cnfe) {
            System.out.println("Class not found");
        }

        // This is the url you must use for DB2.
        //Note: This url may not valid now! Check for the correct year and semester and server name.
        String url = "jdbc:db2://winter2023-comp421.cs.mcgill.ca:50000/cs421";

        //REMEMBER to remove your user id and password before submitting your code!!
        String your_userid = null;
        String your_password = null;

        //AS AN ALTERNATIVE, you can just set your password in the shell environment in the Unix (as shown below) and read it from there.
        //$  export SOCSPASSWD=yoursocspasswd
        if (your_userid == null && (your_userid = System.getenv("SOCSUSER")) == null) {
            System.err.println("Error!! do not have a password to connect to the database!");
            System.exit(1);
        }
        if (your_password == null && (your_password = System.getenv("SOCSPASSWD")) == null) {
            System.err.println("Error!! do not have a password to connect to the database!");
            System.exit(1);
        }

        Connection con = DriverManager.getConnection(url, your_userid, your_password);
        Statement statement = con.createStatement( );

        // Main menu options
        int choice = 0;
        boolean x = true;

        while (choice != 4) {
            System.out.println("Soccer Main Menu");
            System.out.println("1. List information of matches of a country");
            System.out.println("2. Insert initial player information for a match");
            System.out.println("3. Insert a Goal");
            System.out.println("4. Exit application");

            Scanner scanner = new Scanner(System.in);
            System.out.print("Please Enter Your Option: ");
            choice = scanner.nextInt();


            switch (choice) {
                case 1:
                    // TODO: Implement list matches of a country functionality
                    while(x) {
                        try {
                            Scanner Country = new Scanner(System.in);
                            System.out.print("Enter the name of the country: ");
                            String country = Country.nextLine();
                            System.out.println("\n");
                            String querySQL = "SELECT Matches.Hteam, Matches.Ateam, Matches.Date_Time, Matches.Round, \n " +
                                    "COUNT(DISTINCT(CASE WHEN Matches.MId = Playing_in.Mid = Goal.MId AND Playing_in.PId = Players.PId AND Players.Team = Matches.Hteam AND Players.PId = Goal.Pid THEN Goal.Occurrence END)) AS Home_Goals, \n" +
                                    "COUNT(DISTINCT(CASE WHEN Matches.MId = Playing_in.Mid = Goal.MId AND Playing_in.PId = Players.PId AND Players.Team = Matches.Ateam AND Players.PId = Goal.Pid THEN Goal.Occurrence END)) AS Away_Goals, \n" +
                                    "    COUNT(DISTINCT Ticket.TId) AS Seats_Sold\n" +
                                    "FROM \n" +
                                    "    Matches \n" +
                                    "    LEFT JOIN Playing_in ON Matches.MId = Playing_in.MId\n" +
                                    "    LEFT JOIN Goal ON Playing_in.PId = Goal.PId AND Playing_in.MId = Goal.MId\n" +
                                    "    LEFT JOIN Ticket ON Matches.MId = Ticket.Mid\n" +
                                    "    LEFT JOIN Players ON Playing_in.PId = Players.PId " +
                                    "WHERE \n" +
                                    "    Matches.Hteam = '" + country + "' OR Matches.Ateam = '" + country + "'\n" +
                                    "GROUP BY \n" +
                                    "    Matches.MId, \n" +
                                    "    Matches.Hteam, \n" +
                                    "    Matches.Ateam, \n" +
                                    "    Matches.Date_Time, \n" +
                                    "    Matches.Round;\n";


                            java.sql.ResultSet rs = statement.executeQuery(querySQL);
                            int counter = 0;
                            while (rs.next()) {
                                counter++;
                                String c1 = rs.getString(1);
                                String c2 = rs.getString(2);
                                Date date = rs.getDate(3);
                                String round = rs.getString(4);
                                int g1 = rs.getInt(5);
                                int g2 = rs.getInt(6);
                                int seats = rs.getInt(7);
                                System.out.printf("%s   %s  %s  %s  %d  %d  %d\n", c1, c2, date, round, g1, g2, seats);

                            }
                            if (counter == 0){
                                System.out.println("No records with given team");
                                System.out.println("\n");
                            }
                            System.out.println("\n");
                            Scanner returnLine = new Scanner(System.in);
                            System.out.print("Enter [A] to find matches of another country, [P] to go to the previous menu: ");
                            String goBack = returnLine.nextLine();

                            if (goBack.equals("A")) {
                                continue;
                            } else if (goBack.equals("P")) {
                                break;
                            }
                        } catch (SQLException e) {
                            sqlCode = e.getErrorCode(); // Get SQLCODE
                            sqlState = e.getSQLState(); // Get SQLSTATE

                            //DO SOMETHING
                            // Your code to handle errors comes here;
                            // something more meaningful than a print would be good
                            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
                            System.out.println(e);

                        }
                        break;
                    }break;


                case 2:
                    // TODO: Implement insert player information for a match functionality
                    while(true) {
                        try {
                            System.out.println("");
                            System.out.println("Matches: ");
                            String querySQL = "SELECT MId, Hteam, Ateam, Date_Time, Round \n" +
                                    "FROM Matches \n" +
                                    "WHERE Date_Time BETWEEN CURRENT_DATE AND CURRENT_DATE + 3 DAY";
                            java.sql.ResultSet rs = statement.executeQuery(querySQL);
                            int counter = 0;
                            while (rs.next()) {
                                counter++;
                                int Mid = rs.getInt(1);
                                String c1 = rs.getString(2);
                                String c2 = rs.getString(3);
                                Date date = rs.getDate(4);
                                String round = rs.getString(5);
                                System.out.printf("%d   %s  %s  %s  %s\n", Mid, c1, c2, date, round);
                            }
                            if (counter == 0) {
                                System.out.println("");
                                System.out.println("NO games in the next 3 days");
                                System.out.println("");
                            }
                            System.out.println("\n");
                            Scanner returnLine = new Scanner(System.in);
                            System.out.print("Enter [P] to go to the previous menu or anything else to continue: ");
                            String goBack = returnLine.nextLine();


                            if (goBack.equals("P")) {
                                break;
                            }
                            while(true) {
                                try {
                                    Scanner MID = new Scanner(System.in);
                                    System.out.print("Enter the Match ID: ");
                                    String MatchID = MID.nextLine();
                                    Scanner country = new Scanner(System.in);
                                    System.out.print("Enter the Country: ");
                                    String country1 = country.nextLine();
                                    System.out.println("\n");
                                    System.out.println("The following players from " + country1 + " are already entered for match " + MatchID + ":");
                                    System.out.println("\n");

                                    String querySQL1 = "SELECT Players.Name, Players.Shirt_Num, Players.Position, Playing_in.Entered AS Start_Time, Playing_in.Left AS End_Time, Playing_in.Yellow, Playing_in.Red\n" +
                                            "        FROM Playing_in\n" +
                                            "        INNER JOIN Players ON Playing_in.PId = Players.PId\n" +
                                            "        INNER JOIN Matches ON Playing_in.MId = Matches.MId\n" +
                                            "        WHERE Matches.MId = " + MatchID + " AND Playing_in.Entered >= 0 AND Players.Team = '" + country1 + "'";

                                    java.sql.ResultSet rs1 = statement.executeQuery(querySQL1);
                                    int counter1 = 0;
                                    while (rs1.next()) {
                                        counter1++;
                                        String player = rs1.getString(1);
                                        int num = rs1.getInt(2);
                                        String pos = rs1.getString(3);
                                        int enter = rs1.getInt(4);
                                        int left = rs1.getInt(5);
                                        int yellow = rs1.getInt(6);
                                        int red = rs1.getInt(7);

                                        System.out.printf("%s      %d      %s    from minute %d     to minute NULL  yellow: %d  red: %d\n", player, num, pos, enter, left, yellow, red);
                                        System.out.println("");
                                    }
                                    if (counter1 == 0) {
                                        System.out.println("No players in the match");
                                    }
                                    else if (counter1 == 11){
                                        System.out.println("Already 11 players in the game, cannot add any more");
                                        System.out.println("\n");
                                        Scanner returnLine1 = new Scanner(System.in);
                                        System.out.print("Enter [P] to go to the previous menu: ");
                                        String goBack1 = returnLine1.nextLine();


                                        if (goBack1.equals("P")) {
                                            break;
                                        }
                                    }


                                    String querySQL2 = "SELECT Players.Name, Players.Shirt_Num, Players.Position, Players.pid\n" +
                                            "        FROM Playing_in\n" +
                                            "        INNER JOIN Players ON Playing_in.PId = Players.PId\n" +
                                            "        INNER JOIN Matches ON Playing_in.MId = Matches.MId\n" +
                                            "        WHERE Matches.MId = " + MatchID + " AND Players.Team = '" + country1 + "' AND Playing_in.Entered IS NULL";


                                    java.sql.ResultSet rs2 = statement.executeQuery(querySQL2);
                                    int counter2 = 0;
                                    Map<Integer,Integer> pidRetrieve = new HashMap<>();
                                    System.out.println("Bench Players: ");
                                    while (rs2.next()) {
                                        counter2++;
                                        String player = rs2.getString(1);
                                        int num = rs2.getInt(2);
                                        String pos = rs2.getString(3);
                                        int pid = rs2.getInt(4);
                                        pidRetrieve.put(num,pid);

                                        System.out.printf(counter2 + ". %s     %d    %s    \n", player, num, pos);
                                    }
                                    if (counter2 == 0) {
                                        System.out.println("No players remaining on the bench");
                                    }


                                    System.out.println("\n");
                                    Scanner returnLine1 = new Scanner(System.in);
                                    System.out.print("Enter the number of the player you want to insert or [P]\n" +
                                            "to go to the previous menu: ");
                                    String checker = returnLine1.nextLine();


                                    if (checker.equals("P")) {
                                        break;
                                    } else {
                                        if (pidRetrieve.containsKey(Integer.valueOf(checker))){

                                            Scanner position1 = new Scanner(System.in);
                                            System.out.print("Enter the position of the player: ");
                                            String position = position1.nextLine();

                                            int pid = pidRetrieve.get(Integer.valueOf(checker));
                                            String updateSQL = "UPDATE Playing_in\n" +
                                                    "SET Entered = 0\n" +
                                                    "WHERE pid = " + pid;
                                            statement.executeUpdate(updateSQL);
                                            //System.out.println("Player with number " + checker + "is set to play" );
                                        }
                                        else{
                                            System.out.println("");
                                            System.out.println("that player is not on the bench");
                                            System.out.println("");
                                            break;
                                        }
                                        String querySQL4 = "SELECT Players.Name, Players.Shirt_Num, Players.Position, Playing_in.Entered AS Start_Time, Playing_in.Left AS End_Time, Playing_in.Yellow, Playing_in.Red\n" +
                                                "        FROM Playing_in\n" +
                                                "        INNER JOIN Players ON Playing_in.PId = Players.PId\n" +
                                                "        INNER JOIN Matches ON Playing_in.MId = Matches.MId\n" +
                                                "        WHERE Matches.MId = " + MatchID + " AND Playing_in.Entered >= 0 AND Players.Team = '" + country1 + "'";

                                        java.sql.ResultSet rs4 = statement.executeQuery(querySQL4);
                                        System.out.println("");
                                        while (rs4.next()) {

                                            String player = rs4.getString(1);
                                            int num = rs4.getInt(2);
                                            String pos = rs4.getString(3);
                                            int enter = rs4.getInt(4);
                                            int left = rs4.getInt(5);
                                            int yellow = rs4.getInt(6);
                                            int red = rs4.getInt(7);

                                            System.out.printf("%s      %d      %s    from minute %d     to minute NULL  yellow: %d  red: %d\n", player, num, pos, enter, left, yellow, red);
                                            System.out.println("");
                                        }

                                        String querySQL5 = "SELECT Players.Name, Players.Shirt_Num, Players.Position, Players.pid\n" +
                                                "        FROM Playing_in\n" +
                                                "        INNER JOIN Players ON Playing_in.PId = Players.PId\n" +
                                                "        INNER JOIN Matches ON Playing_in.MId = Matches.MId\n" +
                                                "        WHERE Matches.MId = " + MatchID + " AND Players.Team = '" + country1 + "' AND Playing_in.Entered IS NULL";


                                        java.sql.ResultSet rs5 = statement.executeQuery(querySQL5);
                                        int counter5 = 0;

                                        System.out.println("Bench Players: ");
                                        while (rs5.next()) {
                                            counter5++;
                                            String player = rs5.getString(1);
                                            int num = rs5.getInt(2);
                                            String pos = rs5.getString(3);
                                            int pid = rs5.getInt(4);


                                            System.out.printf(counter5 + ". %s     %d    %s    \n", player, num, pos);
                                        }
                                        if (counter5 == 0) {
                                            System.out.println("No players remaining on the bench");
                                        }
                                        Scanner position3 = new Scanner(System.in);
                                        System.out.print("Enter [P] to go to main menu or anything else to select another Match ID ");
                                        String finalOption = position3.nextLine();

                                        if (finalOption.equals(finalOption)){
                                            break;
                                        }



                                    }



                                } catch (SQLException e) {
                                    sqlCode = e.getErrorCode(); // Get SQLCODE
                                    sqlState = e.getSQLState(); // Get SQLSTATE

                                    // Your code to handle errors comes here;
                                    // something more meaningful than a print would be good
                                    System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
                                    System.out.println(e);
                                }
                                //break;
                            }break;

                        } catch (SQLException e) {
                            sqlCode = e.getErrorCode(); // Get SQLCODE
                            sqlState = e.getSQLState(); // Get SQLSTATE

                            // Your code to handle errors comes here;
                            // something more meaningful than a print would be good
                            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
                            System.out.println(e);

                        }break;
                    }break;
                case 3:
                    // TODO: Implement "for you to design" functionality
                    while(true) {
                        try {
                            HashMap<String, List<String>> check12 = new HashMap<>();
                            System.out.println("");
                            System.out.println("Matches: ");
                            String querySQL = "SELECT MId, Hteam, Ateam, Date_Time, Round \n" +
                                    "FROM Matches \n" +
                                    "WHERE Date_Time BETWEEN CURRENT_DATE - 1 DAY AND CURRENT_DATE";
                            java.sql.ResultSet rs = statement.executeQuery(querySQL);
                            int counter = 0;
                            while (rs.next()) {
                                counter++;
                                int Mid = rs.getInt(1);
                                String c1 = rs.getString(2);
                                String c2 = rs.getString(3);
                                Date date = rs.getDate(4);
                                String round = rs.getString(5);
                                System.out.printf("%d   %s  %s  %s  %s\n", Mid, c1, c2, date, round);
                                check12.put(String.valueOf(Mid), Arrays.asList(c1,c2));

                            }
                            if (counter == 0) {
                                System.out.println("No games in the past day");
                                break;
                            }
                            System.out.println("");
                            Scanner returnLine = new Scanner(System.in);
                            System.out.print("Enter [P] to go to the previous menu or anything else to continue: ");
                            String goBack = returnLine.nextLine();


                            if (goBack.equals("P")) {
                                break;
                            }
                            while(true) {
                                try {
                                    Scanner MID = new Scanner(System.in);
                                    System.out.print("Enter the Match ID: ");
                                    String MatchID = MID.nextLine();
                                    if (!check12.containsKey(MatchID)){
                                        System.out.println("");
                                        System.out.println("Match Id not in list of games played in the last day");
                                        System.out.println("");
                                        break;
                                    }


                                    System.out.println("");
                                    System.out.println("The following are goals from that have already been entered for match " + MatchID + ":");
                                    System.out.println("");

                                    String querySQL1 = "select p.Name, p.shirt_num, g.occurrence, g.minute_scored, g.penalty_kick, p.pid from Goal g, players p, matches m where p.pid = g.pid and m.mid = g.mid and g.mid = " + MatchID + "";

                                    java.sql.ResultSet rs1 = statement.executeQuery(querySQL1);
                                    int counter1 = 0;
                                    HashMap<Integer, String> pid = new HashMap<>();
                                    while (rs1.next()) {
                                        counter1++;
                                        String player = rs1.getString(1);
                                        int shirtnum = rs1.getInt(2);
                                        int num = rs1.getInt(3);
                                        int minute = rs1.getInt(4);
                                        int Penalty = rs1.getInt(5);
                                        int pid1 = rs1.getInt(6);

                                        pid.put(shirtnum, String.valueOf(pid1));



                                        System.out.printf("%s       %d    %d      %d    %d\n", player, shirtnum, num, minute, Penalty);
                                        //System.out.println("");
                                    }
                                    if (counter1 == 0) {
                                        System.out.println("No goals in the game yet");
                                    }

                                    //DISPLAY PLAYERS
                                    System.out.println("");
                                    System.out.println("The following are are the names and shirt number of the player that played and were active in the game with MatchID:  " + MatchID + ":");
                                    System.out.println("");


                                    String querySQL2 = "select p.Name, p.shirt_num, p.pid from players p, playing_in i, Matches m where p.pid = i.pid and i.mid = m.mid and i.Entered >= 0 and m.MID = " + MatchID + "";
                                    java.sql.ResultSet rs2 = statement.executeQuery(querySQL2);
                                    while (rs2.next()) {

                                        String player = rs2.getString(1);
                                        int shirtnum = rs2.getInt(2);
                                        int pid1 = rs2.getInt(3);
                                        pid.put(shirtnum, String.valueOf(pid1));

                                        System.out.printf("%s       %d\n", player, shirtnum);
                                        //System.out.println("");
                                    }
                                    System.out.println("");
                                    Scanner returnLine1 = new Scanner(System.in);
                                    System.out.print("Enter the number of the player who scored the goal or [P] to go to the previous menu: ");
                                    String shirt = returnLine1.nextLine();


                                    if (shirt.equals("P")) {
                                        break;
                                    } else {
                                        int shirtnew = Integer.valueOf(shirt);

                                        //CHECKER//
                                        //DiSPLAY PLAYERS//

                                        System.out.println("");
                                        Scanner returnLine2 = new Scanner(System.in);
                                        System.out.print("Enter the minute scored: ");
                                        String minutescored = returnLine2.nextLine();

                                        if (Integer.valueOf(minutescored) > 100){
                                            System.out.println("");
                                            System.out.println("Games do not go that long");
                                            break;
                                        }



                                        System.out.println("");
                                        Scanner returnLine3 = new Scanner(System.in);
                                        System.out.print("was this a penalty (1 for yes, 0 for no): ");
                                        String penalty = returnLine3.nextLine();

                                        counter1++;

                                        int pidget = Integer.parseInt(pid.get(shirtnew));

                                        String updateSQL = "Insert into Goal\n values (" + MatchID + ", " + counter1 + ", " + minutescored + ", " + penalty + ", " + pidget + ")";
                                        statement.executeUpdate(updateSQL);


                                    }
                                    System.out.println("");
                                    String querySQL3 = "select p.Name, p.shirt_num, g.occurrence, g.minute_scored, g.penalty_kick, p.pid from Goal g, players p, matches m where p.pid = g.pid and m.mid = g.mid and g.mid = " + MatchID + "";

                                    java.sql.ResultSet rs3 = statement.executeQuery(querySQL3);


                                    while (rs3.next()) {
                                        String player = rs3.getString(1);
                                        int shirtnum = rs3.getInt(2);
                                        int num = rs3.getInt(3);
                                        int minute = rs3.getInt(4);
                                        int Penalty = rs3.getInt(5);
                                        int pid1 = rs3.getInt(6);

                                        System.out.printf("%s       %d    %d      %d    %d\n", player, shirtnum, num, minute, Penalty);
                                        //System.out.println("");
                                    }
				    System.out.println("");
                                    System.out.println("The following are are the names and shirt number of the player that played and were active in the game with MatchID:  " + MatchID + ":");
                                    System.out.println("");


                                    String querySQL4 = "select p.Name, p.shirt_num, p.pid from players p, playing_in i where p.pid = i.pid and i.Entered >= 0 and i.MID = " + MatchID + "";
                                    java.sql.ResultSet rs4 = statement.executeQuery(querySQL4);
                                    while (rs4.next()) {

                                        String player = rs4.getString(1);
                                        int shirtnum = rs4.getInt(2);
                                        int pid1 = rs4.getInt(3);


                                        System.out.printf("%s       %d\n", player, shirtnum);
                                        //System.out.println("");
                                    }
                                    System.out.println("");
                                    Scanner returnLine4 = new Scanner(System.in);
                                    System.out.print("Select [P] to go to main menu or anything else to pick a new Match ID: ");
                                    String final1 = returnLine4.nextLine();

                                    if (final1.equals("P")){
                                        break;
                                    }




                                } catch (SQLException e) {
                                    sqlCode = e.getErrorCode(); // Get SQLCODE
                                    sqlState = e.getSQLState(); // Get SQLSTATE

                                    // Your code to handle errors comes here;
                                    // something more meaningful than a print would be good
                                    System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
                                    System.out.println(e);
                                }

                            }

                        } catch (SQLException e) {
                            sqlCode = e.getErrorCode(); // Get SQLCODE
                            sqlState = e.getSQLState(); // Get SQLSTATE

                            // Your code to handle errors comes here;
                            // something more meaningful than a print would be good
                            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
                            System.out.println(e);

                        }break;
                    }break;
                    
                    
                    //break;
                case 4:
                    System.out.println("Exiting application...");
                    // Close the database connection before exiting
                    statement.close();
                    con.close();
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option (1-4).");
                    break;
            }
        }
    }
}
