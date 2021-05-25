/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.groupproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author user
 */
public class main {
    
    static final String DBURL = // your database connection url
            "jdbc:sqlserver://LAPTOP-NPQJUSVA\\SQLEXPRESS:49672;databaseName=master"; 
    static final String DBDRV = // your database connection driver
            "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    static final String DBUSER = "sa"; // your user name
    static final String DBPASSWD = "junjing"; // your password
    static Connection con = null;
    static Statement st = null;
    static ResultSet rs = null;
    
    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName(DBDRV);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Welcome to New_2 - The Sociopath");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        Scanner sc = new Scanner(System.in);
        do {
            System.out.print("Enter your student ID [Integer][Enter '0' to exit the program]: "); // to abtain id input
            System.out.println();
            int id = -1;
            try {
                String in = sc.next();
                id = Integer.parseInt(in);
                sc.nextLine();
            } catch (NumberFormatException e) { //check is the input an integer
                System.out.println("Invalid input");
                continue;
            }  
            // check if input is 0
            if (id == 0) break;
            
            // check if the id exists
            try {
                con = DriverManager.getConnection(DBURL, DBUSER, DBPASSWD);
                st = con.createStatement();
                String checkID = "SELECT * FROM db_owner.Persons P WHERE P.ID =" +id+';';
                rs = st.executeQuery(checkID);
                if (rs.next()) {
                    System.out.println("Hello, student, ID=" +id+'.');
                } else {
                    System.out.println("ID not found");
                    continue;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                break;
            }
            
            // main menu section
            do {
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                System.out.println("Main Menu\n" +
                        "1. Teaching a Stranger to Solve Lab Questions\n" +
                        "2. Chit-Chat\n" +
                        "3. Your Road to Glory\n" +
                        "4. Bored?\n" +
                        "5. Meet Your Crush\n" +
                        "6. Friendship\n" +
                        "7. Exit Program");
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                System.out.print("Select [Integer]: ");
                System.out.println();
                
                int choice = -1;    
                try {
                    String in = sc.next();
                    choice = Integer.parseInt(in);
                    sc.nextLine();
                } catch (NumberFormatException e) { //check is the input an integer
                    System.out.println("Invalid input");
                    continue;
                }  
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                if (choice == 7) break;
                boolean error = false;
                switch(choice){
                    case 1:
                        if(!eventOne(id)) error = true;
                        break;
                    case 2:
                        System.out.println("Not available yet");
                        break;
                    case 3:
                        if(!eventThree(id)) error = true;
                        break;
                    case 4:
                        System.out.println("Not available yet");
                        break;
                    case 5:
                        System.out.println("Not available yet");
                        break;
                    case 6:
                        System.out.println("Not available yet");
                        break;
                    default:
                        System.out.println("Invalid choice");
                        continue;
                }
                if (error == true) break;
                else {
                    System.out.println("Back to main menu? [Enter to continue]");
                    sc.nextLine();
                }
            } while(true);
            break;
        } while (true);
        System.out.println("You have exited the program.");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        
    }
    
    public static boolean eventOne(int yourID) {
        System.out.println("EVENT 1: Teaching a Stranger to Solve Lab Question");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        Random r = new Random();
        int strangerID = 0; 
        ArrayList<Integer> arrList = new ArrayList<>(); // to check the number of ID generated
        do {
            strangerID = r.nextInt(getSize()) + 1;
            if (arrList.isEmpty()) {
                arrList.add(strangerID);
            } else if (!arrList.contains(strangerID)) {
                arrList.add(strangerID);
            }
            if (arrList.size() == getSize()) { // if number of ID generated is same as the number of person as nodes
                System.out.println("Looks like you have no more stranger that might know you");
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                System.out.println("Event 1 ended.");
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
                return true;
            }
        } while ((strangerID == yourID)||
                (!hasPath(strangerID, yourID))|| //check whether there is a path connecting two person, if no means they will not know each other existence
                (isFriend(yourID, strangerID))); //check whether the person is already friend with you, if yes means the person is not stranger
        System.out.println("A stranger has came to ask you to teach solving lab question");
        System.out.println("The stranger is student, ID=" + strangerID+'.');
        
        //if there isn't an edge connecting but there is a path between
        if (!hasEdge(yourID, strangerID)) {
            System.out.print("You don't know this person");
            if(addEdge(yourID, strangerID))
                System.out.println(", a new relation is formed between you and student, ID=" + strangerID+'.');
        } else { // there is an edge, meaning you two might already know each other but not friends yet
            System.out.println("You might know this person but you are not friend with this person yet");
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        
        boolean good = r.nextBoolean(); // the teaching experience
        try {
            //Update the reputation and friend status
            con = DriverManager.getConnection(DBURL, DBUSER, DBPASSWD);
            st = con.createStatement();
            //update the relation as friend
            addFriend(yourID, strangerID);
            
            // update reputation
            int reputation = 0;
            if (good) {
                reputation = 10;
                System.out.println("The learning experience with you is nice. Therefore,");
            }
            else {
                reputation = 2;
                System.out.println("The question is hard, learning experience with you is not pleasant. Therefore,");
            }
            
            String updateRep = "UPDATE db_owner.Relationships SET reputation = " +reputation+" FROM db_owner.Persons AS Person1, \n" +
            "	 db_owner.Persons AS Person2,\n" +
            "	 db_owner.Relationships AS Re\n" + 
            "WHERE MATCH(Person2-(Re)->Person1) AND Person1.ID =" +yourID+" AND Person2.ID = " +strangerID+";";;
            st.execute(updateRep);
            
            //display reputation status
            String repStatus = "SELECT Person1.ID AS yourID, Person2.ID AS strangerID, Re1.reputation\n" +
            "FROM db_owner.Persons AS Person1, \n" +
            "	 db_owner.Persons AS Person2,\n" +
            "	 db_owner.Relationships AS Re1,\n" +
            "	 db_owner.Relationships AS Re2\n" +
            "WHERE (MATCH(Person2-(Re1)->Person1) AND MATCH(Person1-(Re2)->Person2))\n" +
            "AND Person1.ID =" +yourID+" AND Person2.ID =" +strangerID+";";
            rs = st.executeQuery(repStatus);
            if (rs.next()) {
                int sID = rs.getInt("strangerID");
                int rep = rs.getInt("reputation");
                System.out.println("your reputation relative to student, ID=" +sID+ " is " +rep);
            }
            System.out.println("Anyway, student, ID=" +strangerID+" and you have became friends.");
            
            // display friend status
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            String friendStatus = "SELECT Person1.ID AS yourID, Person2.ID AS strangerID, Re1.friend AS f1, Re2.friend AS f2,"
                    + "Re1.reputation AS rep1, Re2.reputation AS rep2\n" +
            "FROM db_owner.Persons AS Person1, \n" +
            "	 db_owner.Persons AS Person2,\n" +
            "	 db_owner.Relationships AS Re1,\n" +
            "	 db_owner.Relationships AS Re2\n" +
            "WHERE (MATCH(Person2-(Re1)->Person1) AND MATCH(Person1-(Re2)->Person2))\n" +
            "AND Person1.ID =" +yourID+" AND Person2.ID =" +strangerID+";";
            
            rs = st.executeQuery(friendStatus);
            if(rs.next()) {
                int yID = rs.getInt("yourID");
                int sID = rs.getInt("strangerID");
                int f1 = rs.getInt("f1");
                int f2 = rs.getInt("f2");
                int rep1 = rs.getInt("rep1");
                int rep2= rs.getInt("rep2");
                System.out.printf("%15s%15s%15s    %4s\n", "StudentID1", "StudentID2", "friend", "reputation_relative_to_studentID2");
                System.out.printf("%15d%15d%15d    %4d\n", yID, sID, f2, rep1);
                System.out.printf("%15d%15d%15d    %4d\n", sID, yID, f1, rep2);
            }
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            
        } catch (SQLException ex) {
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            ex.printStackTrace();
            return false;
        }
        System.out.println("Event 1 ended.");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        return true;
    }
    
    public static boolean eventThree(int yourID) {
        Scanner sc= new Scanner(System.in); 
        System.out.println("EVENT 3: Your Road to Glory");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        // ask for user input for today's lunch time and duration
        int yourStartTime, yourEndTime;
        System.out.println("Provide your lunch starting time and planned duration of lunch today");
        do {
            System.out.println("Lunch start time(1100-1400):");
            try {
                String in = sc.next();
                yourStartTime = Integer.parseInt(in);
                sc.nextLine();
            } catch (NumberFormatException e) { //check is the input an integer
                System.out.println("Invalid input");
                continue;
            }
            if (yourStartTime>1400 || yourStartTime<1100) {
                System.out.println("Invalid lunch time. Lunch time should be between 1100 to 1400");
                continue;
            } else if (yourStartTime%100 > 59) {
                System.out.println("Invalid time input");
                continue;
            }
            break;
        } while(true);
        do {
            System.out.println("Lunch duration(mins):");
            int duration;
            try {
                String in = sc.next();
                duration = Integer.parseInt(in);
                sc.nextLine();
            } catch (NumberFormatException e) { //check is the input an integer
                System.out.println("Invalid input");
                continue;
            }
            if (duration>59 || duration<5) {
                System.out.println("Invalid lunch duration. Lunch time should be between 6 to 59");
                continue;
            }
            int hour = yourStartTime/100;
            int mins = yourStartTime%100;
            mins = mins + duration;
            if (mins >= 60) {
                hour += (mins/60);
                mins = (mins%60);
            }
            yourEndTime = hour*100 + mins;
            break;
        } while(true);
        
        // store all lunch time, lunch period, and dive rate info except for yourID
        ArrayList<Integer[]> validIntersection = new ArrayList<>();
        // display all possible choice of person
        try {
            con = DriverManager.getConnection(DBURL, DBUSER, DBPASSWD);
            st = con.createStatement();
            String getInfo = "Select *\n" +
            "from db_owner.Persons Person\n" +
            "where Person.ID !="+yourID+';';
            rs = st.executeQuery(getInfo);
            
            while(rs.next()) {
                int id = rs.getInt("ID");
                int dive = rs.getInt("diving_rate");
                int startTime = Integer.parseInt(rs.getString("lunch_time"));
                int lunchPeriod = rs.getInt("lunch_period");
                
                // calculate lunch end time
                int hour = startTime/100;
                int mins = startTime%100;
                mins = mins + lunchPeriod;
                if (mins >= 60) {
                    hour += (mins/60);
                    mins = (mins%60);
                }
                int endTime = hour*100 + mins;
                
                //store
                if ((endTime>yourStartTime && endTime<yourEndTime) || (startTime<yourEndTime && startTime>yourStartTime)) {
                    validIntersection.add(new Integer[] {id, startTime, endTime, dive});
                }
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
        if(validIntersection.isEmpty()) {
            System.out.println("None of the average lunch time intersects with your lunch time today");
            System.out.println("\nEvent 3 ended.");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            return true;
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("These are the students with their average lunch time \n"
               + "that intersect with your today's lunch time");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("------------------------------------------------");
        System.out.printf("| %-6s|%12s|%12s|%12s|\n", "ID", "start_time", "end_time", "dive_rate");
        System.out.println("------------------------------------------------");
        // display the list
        for (int i = 0; i<validIntersection.size() ; i++) {
            Integer[] arr = validIntersection.get(i);
            System.out.printf("| %-6d|%12d|%12d|%12d|\n", arr[0], arr[1], arr[2], arr[3]);
        }
        System.out.println("------------------------------------------------");
            
        // calculate the maximum reputation that can be gotten in one day
        validIntersection = sortStartTimeEndTime(validIntersection); // sort the list
        int max = 0; // maximum reputation
        ArrayList<ArrayList<Integer[]>> possibleCombination = new ArrayList<>(); // list to store all possible combinations
        for (int i=0; i<validIntersection.size() ; i++) {
            ArrayList<Integer[]> eachCombination = new ArrayList<>(); // to store each combination
            // try each combinations
            for (int j = i, count = 0; j<validIntersection.size() ; j++) {
                if (eachCombination.isEmpty()) {
                    eachCombination.add(validIntersection.get(i));
                    count++;
                } else {
                    // if the start time of next person is later than the end time of previous person then can add
                    if (validIntersection.get(j)[1].compareTo(eachCombination.get(count-1)[2])>0) { 
                        eachCombination.add(validIntersection.get(j));
                        count++;
                    }
                }
            }
            int counts = eachCombination.size();
            if (counts > max) {
                max=counts;
            } 
            possibleCombination.add(eachCombination); // store each combination into possible combination
        }
        System.out.println("The maximum number of reputation that you can get is " + max + "\n");
        // display all the possible combinations of student that you can have lunch with
        System.out.println("All of the possible combinations of student that you can have lunch with");
        for (int i=0 ; i<possibleCombination.size() ; i++) {
            System.out.print((i+1) + " ");
            for (int j=0; j<possibleCombination.get(i).size() ; j++) {
                System.out.print(Arrays.toString(possibleCombination.get(i).get(j)));
            }
            System.out.println("");
        }
        
        // select one possible combination
        System.out.println("Please select the combination that you would like to have lunch with. ['0' to quit selection]");
        int selection = -1;
        do {
            System.out.println("");
            try {
                String in = sc.next();
                selection = Integer.parseInt(in);
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid input");
                continue;
            }
            if (selection == 0) {
                System.out.println("You did not choose any combination");
                System.out.println("\nEvent 3 ended.");
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
                return true;
            }
            if (selection > possibleCombination.size() - 1) {
                System.out.println("Invalid selection");
                continue;
            }
            selection = selection - 1;
            break;
        } while(true);
        
        // update relationship reputation of your ID relative to selected ID
        for (int i=possibleCombination.get(selection).size()-1 ; i>=0; i--) {
            // check whether the two persons were already connected
            if (!hasEdge(yourID, possibleCombination.get(selection).get(i)[0])) {
                addEdge(yourID, possibleCombination.get(selection).get(i)[0]);
            }
            
            try {
                int currentRep = 0;
                con = DriverManager.getConnection(DBURL, DBUSER, DBPASSWD);
                st = con.createStatement();
                // get current rep
                String getRep = "SELECT Person1.ID, Person2.ID, reputation \n" +
                "FROM db_owner.Persons AS Person1, \n" +
                "	 db_owner.Persons AS Person2,\n" +
                "	 db_owner.Relationships AS Re\n" +
                "WHERE MATCH(Person2-(Re)->Person1) AND Person1.ID ="+yourID+" AND Person2.ID ="+possibleCombination.get(selection).get(i)[0]+';';
                rs = st.executeQuery(getRep);
                if (rs.next()) {
                    currentRep = rs.getInt("reputation");
                }
                
                // update rep
                String updateRep = "UPDATE db_owner.Relationships SET reputation = " +(currentRep+1)+" FROM db_owner.Persons AS Person1, \n" +
                "	 db_owner.Persons AS Person2,\n" +
                "	 db_owner.Relationships AS Re\n" + 
                "WHERE MATCH(Person2-(Re)->Person1) AND Person1.ID =" +yourID+" AND Person2.ID = " +possibleCombination.get(selection).get(i)[0]+';';
                st.execute(updateRep);
                
                // get updated rep
                String selectRep = "SELECT Re.reputation \n" +
                "From db_owner.Persons P1, db_owner.Persons P2, db_owner.Relationships Re\n" +
                "Where match(P2-(Re)->P1) and P1.ID =" +yourID+"and P2.ID = " +possibleCombination.get(selection).get(i)[0]+';';
                rs = st.executeQuery(selectRep);
                if (rs.next()) {
                    int newRep = rs.getInt("reputation");
                    System.out.println("Your reputation relative to student, ID="+possibleCombination.get(selection).get(i)[0]+
                        " increased from " + currentRep + " to " + newRep);
                }
                
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Event 3 ended.");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        return true;
    }
    
    public static ArrayList<Integer[]> sortStartTimeEndTime(ArrayList<Integer[]> timeSlots) {
        // convert to 2D array
        Integer[][] time = new Integer[timeSlots.size()][timeSlots.get(0).length];
        for (int i=0; i<time.length ; i++) {
            time[i] = timeSlots.get(i);
        }
        // Using built-in sort function Arrays.sort
        Arrays.sort(time, new Comparator<Integer[]>() {
            
          @Override              
          // Compare values according to columns
          public int compare(final Integer[] time1, final Integer[] time2) {
  
            // Sort in ascending order according to lunch start time
            if (time1[1] > time2[1])
                return 1;
            else
                return -1;
          }
        });
        
        //return the time array
        ArrayList<Integer[]> sorted = new ArrayList<>();
        for(int i=0; i<time.length; i++){
            sorted.add(new Integer[] {time[i][0], time[i][1], time[i][2]});
        }
        return sorted;
    }
    
    public static boolean addFriend(int ID1, int ID2) {
        try {
            //Update the reputation and friend status
            con = DriverManager.getConnection(DBURL, DBUSER, DBPASSWD);
            st = con.createStatement();
            String from = "WHERE MATCH(Person2-(Re)->Person1) AND Person1.ID =" +ID1+" AND Person2.ID = " +ID2+";";
            String to = "WHERE MATCH(Person1-(Re)->Person2) AND Person1.ID = " +ID1+" AND Person2.ID = " +ID2+";";
            String updateFriend = "UPDATE db_owner.Relationships SET friend = 1 FROM db_owner.Persons AS Person1, \n" +
            "	 db_owner.Persons AS Person2,\n" +
            "	 db_owner.Relationships AS Re\n";
            st.execute(updateFriend + to + "\n" + updateFriend + from);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static boolean addEdge(int ID1, int ID2) {
        if (hasEdge(ID1, ID2)) return false;
        try {
            con = DriverManager.getConnection(DBURL, DBUSER, DBPASSWD);
            st = con.createStatement();
            String insertEdge = "INSERT INTO db_owner.Relationships\nVALUES \n" +
                "((SELECT $node_id FROM db_owner.Persons WHERE id=" +ID1+"),(SELECT $node_id FROM db_owner.Persons WHERE id =" +ID2+"),0, 0, getdate()),\n" +
                "((SELECT $node_id FROM db_owner.Persons WHERE id =" +ID2+"),(SELECT $node_id FROM db_owner.Persons WHERE id = " +ID1+"),0, 0, getdate())";
            st.execute(insertEdge);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public static boolean isFriend(int ID1, int ID2) {
        try {
            con = DriverManager.getConnection(DBURL, DBUSER, DBPASSWD);
            st = con.createStatement();
            String checkFriend = "SELECT Person1.ID, Person2.id, Re.friend\n" +
            "FROM db_owner.Persons AS Person1, \n" +
            "	 db_owner.Persons AS Person2,\n" +
            "	 db_owner.Relationships AS Re\n" +
            "WHERE MATCH(Person2-(Re)->Person1) AND Person1.ID="+ID1+" AND Person2.ID=" + ID2 +";";
            rs = st.executeQuery(checkFriend);
                
            
            if (rs.next()) { //connected
                int friend = rs.getInt("friend");
                if (friend == 1) return true;
            } 
            return false;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public static boolean hasEdge(int ID1, int ID2) {
        try {
            con = DriverManager.getConnection(DBURL, DBUSER, DBPASSWD);
            st = con.createStatement();
            String checkEdge = "SELECT *\n" +
            "FROM db_owner.Persons AS Person1, \n" +
            "	 db_owner.Persons AS Person2,\n" +
            "	 db_owner.Relationships AS Re1,\n" +
            "	 db_owner.Relationships AS Re2\n" +
            "WHERE (MATCH(Person2-(Re1)->Person1) AND MATCH(Person1-(Re2)->Person2))\n" 
                    + "AND Person1.ID="+ID1+" AND Person2.ID=" + ID2 +";";
            rs = st.executeQuery(checkEdge);
                
            
            if (rs.next()) return true;
            else return false;
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public static boolean hasPath(int fromID, int toID) {
        try {
            con = DriverManager.getConnection(DBURL, DBUSER, DBPASSWD);
            st = con.createStatement();
            String checkPath = "SELECT ID, Related\n" +
            "FROM (	\n" +
            "	SELECT\n" +
            "		Person1.ID AS ID, \n" +
            "		STRING_AGG(Person2.ID, '->') WITHIN GROUP (GRAPH PATH) AS Related,\n" +
            "		LAST_VALUE(Person2.ID) WITHIN GROUP (GRAPH PATH) AS LastNode\n" +
            "	FROM\n" +
            "		db_owner.Persons AS Person1,\n" +
            "		db_owner.Relationships FOR PATH AS re,\n" +
            "		db_owner.Persons FOR PATH  AS Person2\n" +
            "	WHERE MATCH(SHORTEST_PATH(Person1(-(re)->Person2)+))\n" +
            "	AND Person1.ID=" +fromID+ "\n" +
            ") AS Q\n" +
            "WHERE Q.LastNode=" + toID + ";";
            rs = st.executeQuery(checkPath);
            
            if (rs.next()) { return true;
            } else {
                return false;
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    } 
    
    public static int getSize() {
        try{
            con = DriverManager.getConnection(DBURL, DBUSER, DBPASSWD);
            st = con.createStatement();
            String statement = "SELECT id FROM db_owner.Persons";
            rs = st.executeQuery(statement);
            int size = 0;
            while(rs.next()) {
                size++;
            }
            return size;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return 0;
        }
    }
    
}
