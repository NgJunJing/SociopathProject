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
            "jdbc:sqlserver://LAPTOP-NPQJUSVA\\SQLEXPRESS:49671;databaseName=master"; 
    static final String DBDRV = // your database connection driver
            "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    static final String DBUSER = "sa"; // your user name
    static final String DBPASSWD = "junjing"; // your password
    static Connection con = null;
    static Statement st = null;
    static ResultSet rs = null;
    
    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName(DBDRV);
        
        eventOne(5);
        eventThree(5);
    }
    
    public static boolean eventOne(int yourID) {
        System.out.println("EVENT 1: Teaching a Stranger to Solve Lab Question");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        Random r = new Random();
        int strangerID = 0; 
        ArrayList<Integer> arrList = new ArrayList<>();
        do {
            strangerID = r.nextInt(getSize()) + 1;
            if (arrList.isEmpty()) {
                arrList.add(strangerID);
            } else if (!arrList.contains(strangerID)) {
                arrList.add(strangerID);
            }
            if (arrList.size() == getSize()) {
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
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        return true;
    }
    
    public static boolean eventThree(int yourID) {
        Scanner sc= new Scanner(System.in); 
        System.out.println("EVENT 3: Your Road to Glory");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        // display all available choices
        
        // store all lunch time, lunch period, and dive rate info except for yourID
        ArrayList<Integer[]> startTimeEndTimeCollection = new ArrayList<>();
        // display all possible choice of person
        try {
            con = DriverManager.getConnection(DBURL, DBUSER, DBPASSWD);
            st = con.createStatement();
            String getInfo = "Select *\n" +
            "from db_owner.Persons Person\n" +
            "where Person.ID !="+yourID+';';
            rs = st.executeQuery(getInfo);
            
            // display the available students for selection
            System.out.println("Available student");
            System.out.printf("%-6s%14s%14s%14s\n", "ID", "diving_rate", "lunch_time", "lunch_period");
            while (rs.next()) {
                int ID = rs.getInt("ID");
                int diveRate = rs.getInt("diving_rate");
                int lunchtime = Integer.parseInt(rs.getString("lunch_time"));
                int lunchperiod = rs.getInt("lunch_period");
                System.out.printf("%-6d%12d%12d%12d\n", ID, diveRate, lunchtime, lunchperiod);
            }
            
            System.out.println("Select the people you are interested to have lunch with, by entering their respective ID");
            System.out.println("[ONE ID PER INPUT][Enter '0' to quit selection]"); // test 2 inputs at once
            int input;
            do {
                System.out.print("Input: ");
                System.out.println("");
                try {
                    String in = sc.next();
                    input = Integer.parseInt(in);
                    sc.nextLine();
                } catch (NumberFormatException e) { //check is the input an integer
                    System.out.println("Invalid input");
                    continue;
                } 
                // check if input is 0
                if (input == 0) {
                    System.out.println("Selection exited.\n");
                    break;
                }
                // check if input equals to the given ID
                if (input == yourID) {
                    System.out.println("This is you.");
                    continue;
                }
                // check the input ID
                String search = "Select *\n" +
                "from db_owner.Persons Person\n" +
                "where Person.ID ="+input+';';
                rs = st.executeQuery(search);
                
                if (rs.next()) { //if ID is found
                    int id = rs.getInt("ID");
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
                    startTimeEndTimeCollection.add(new Integer[] {id, startTime, endTime});
                    System.out.println("ID recorded");
                } else { // if ID not available
                    System.out.println("ID not found");
                }
            } while (true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
        // Exit the program if no one is being selected
        if (startTimeEndTimeCollection.isEmpty()) {
            System.out.println("You have not selected anyone.");
            System.out.println("Event 3 ended.");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            return true;
        }
        
        // display the selected list of students
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Selected students");
        System.out.println("-----------------------------------");
        System.out.printf("| %-6s|%12s|%12s|\n", "ID", "start_time", "end_time");
        System.out.println("-----------------------------------");
        startTimeEndTimeCollection = sortStartTimeEndTime(startTimeEndTimeCollection); // sort the list
        // display the list
        for (int i = 0; i<startTimeEndTimeCollection.size() ; i++) {
            Integer[] arr = startTimeEndTimeCollection.get(i);
            System.out.printf("| %-6d|%12d|%12d|\n", arr[0], arr[1], arr[2]);
        }
        System.out.println("-----------------------------------");
        
        // arrayList to store final ID for lunch
        // stack to store combinations of time slots
        ArrayList<Integer[]> IDForLunch = new ArrayList<>();
        LinkedList<Integer[]> countStack = new LinkedList<>();
        int max = 0;
        for (int i=0; i<startTimeEndTimeCollection.size() ; i++) {
            
            // try each combinations
            for (int j = i; j<startTimeEndTimeCollection.size() ; j++) {
                if (countStack.isEmpty()) {
                    countStack.push(startTimeEndTimeCollection.get(i));
                } else {
                    // if the start time of next person is later than the end time of previous person then can add
                    if (startTimeEndTimeCollection.get(j)[1].compareTo(countStack.peek()[2])>0) { 
                        countStack.push(startTimeEndTimeCollection.get(j));
                    }
                }
            }
            
            int count = countStack.size();
            if (count > max) {
                max=count;
                if (!IDForLunch.isEmpty()) IDForLunch.clear();
                
                // store the new IDs
                while (!countStack.isEmpty()) {
                    IDForLunch.add(countStack.pop());
                }
            } 
            if(!countStack.isEmpty()) countStack.clear(); // clear the stack for next iteration
            
        }
        System.out.println("The maximum number of reputation that you can get is " + max + "\n");
        // display all the students that you will have lunch with
        for (int i=IDForLunch.size()-1 ; i>=0 ; i--) {
            System.out.println("You can have lunch with student, ID="+IDForLunch.get(i)[0]+" from "+IDForLunch.get(i)[1]+
                    " to "+IDForLunch.get(i)[2]+" to increase your reputation relative to that student by 1");
        }
        do {
            System.out.println("\nDo you want to have lunch with them?[yes/no]");
            String in = sc.next();
            sc.nextLine();
            if (in.equals("no")) {
                System.out.println("Event 3 ended.");
                return true;
            }
            else if (in.equals("yes")) {
                System.out.println("Updating relationships");
                break;
            }
            System.out.println("Invalid input");
        } while(true);
        
        // update friend relationship and/or reputation of your ID relative to selected ID
        for (int i=IDForLunch.size()-1 ; i>=0; i--) {
            // check whether the two persons were already connected
            if (hasEdge(yourID, IDForLunch.get(i)[0])) {
                if (!isFriend(yourID, IDForLunch.get(i)[0])) {// check whether the two persons were already friends, if not, add friend
                    addFriend(yourID, IDForLunch.get(i)[0]);
                }
            } else { // no edge
                if(addEdge(yourID, IDForLunch.get(i)[0])) // form edge
                    addFriend(yourID, IDForLunch.get(i)[0]); // add friend
            }
            
            try {
                int currentRep = 0;
                con = DriverManager.getConnection(DBURL, DBUSER, DBPASSWD);
                st = con.createStatement();
                // get current rep
                String getRep = "SELECT Person1.ID, Person2.ID, Person2.diving_rate, reputation \n" +
                "FROM db_owner.Persons AS Person1, \n" +
                "	 db_owner.Persons AS Person2,\n" +
                "	 db_owner.Relationships AS Re\n" +
                "WHERE MATCH(Person2-(Re)->Person1) AND Person1.ID ="+yourID+" AND Person2.ID ="+IDForLunch.get(i)[0]+';';
                rs = st.executeQuery(getRep);
                if (rs.next()) {
                    currentRep = rs.getInt("reputation");
                }
                
                // update rep
                String updateRep = "UPDATE db_owner.Relationships SET reputation = " +(currentRep+1)+" FROM db_owner.Persons AS Person1, \n" +
                "	 db_owner.Persons AS Person2,\n" +
                "	 db_owner.Relationships AS Re\n" + 
                "WHERE MATCH(Person2-(Re)->Person1) AND Person1.ID =" +yourID+" AND Person2.ID = " +IDForLunch.get(i)[0]+';';
                st.execute(updateRep);
                
                // get updated rep
                String selectRep = "SELECT Re.reputation \n" +
                "From db_owner.Persons P1, db_owner.Persons P2, db_owner.Relationships Re\n" +
                "Where match(P2-(Re)->P1) and P1.ID =" +yourID+"and P2.ID = " +IDForLunch.get(i)[0]+';';
                rs = st.executeQuery(selectRep);
                if (rs.next()) {
                    int newRep = rs.getInt("reputation");
                    System.out.println("Your reputation relative to student, ID="+IDForLunch.get(i)[0]+
                        " increased from " + currentRep + " to " + newRep);
                }
                
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        System.out.println("Event 3 ended.");
        System.out.println("");
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
                System.out.println("No such path existed");
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
