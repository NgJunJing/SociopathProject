
package test.groupproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

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
            System.out.print("Enter your student ID [Integer][Enter '0' to exit the program]: ");
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
            if (checkID(id)) {
                System.out.println("Hello, student, ID=" +id+'.');
            } else {
                continue;
            }
            
            //display main menu
            do {
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                System.out.println("Main Menu\n" +
                        "1. Teaching a Stranger to Solve Lab Questions\n" +
                        "2. Chit-Chat\n" +
                        "3. Your Road to Glory\n" +
                        "4. Bored?\n" +
                        "5. Meet Your Crush\n" +
                        "6. Friendship\n" +
                        "7. Parallel Farming\n" + 
                        "8. Herd Immunity\n" +
                        "9. Exit Program");
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
                if (choice == 9) break;
                boolean error = false;
                switch(choice){
                    case 1:
                        if(!eventOne(id)) error = true;
                        break;
                    case 2:
                        System.out.println("Not available yet");
                        break;
                    case 3:
                        if(!eventThreeV2(id)) error = true;
                        break;
                    case 4:
                        System.out.println("Not available yet");
                        break;
                    case 5:
                        System.out.println("Not available yet");
                        break;
                    case 6:
                        if(!eventSix()) error = true;
                        break;
                    case 7:
                        if(!parallelFarming(id)) error = true;
                        break;
                    case 8:
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
    
    /**
     * Event 1 Teaching a Stranger to Solve Lab Questions
     * @param yourID ID input from user
     * @return if the method run successfully, return true, else, return false.
     */
    public static boolean eventOne(int yourID) {
        System.out.println("EVENT 1: Teaching a Stranger to Solve Lab Question");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        Random r = new Random();
        int strangerID = 0; 
        ArrayList<Integer> arrList = new ArrayList<>();
        do {
            strangerID = r.nextInt(getNumberOfPerson()) + 1;
            if (arrList.isEmpty()) {
                arrList.add(strangerID);
            } else if (!arrList.contains(strangerID)) {
                arrList.add(strangerID);
            }
            if (arrList.size() == getNumberOfPerson()) {
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
    
    /**
     * Event 3 Your Road to Glory
     * @param yourID ID input from user
     * @return If method run successfully, return true, else, return false.
     */
    public static boolean eventThreeV2(int yourID) {
        Scanner sc= new Scanner(System.in); 
        System.out.println("EVENT 3: Your Road to Glory");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        
        // ask user for interested persons
        ArrayList<Integer> IDList = new ArrayList<>();
        System.out.println("Select persons that you are interested to have lunch with [Enter '0' to exit]");
        do {
            System.out.println("Input ID: ");
            try {
                String in = sc.next();
                int currentSelectedID = Integer.parseInt(in);
                if (currentSelectedID == 0) {
                    System.out.println("You have exited selection with code '0'");
                    break;
                }
                if (currentSelectedID == yourID) {
                    System.out.println("This is you");
                    continue;
                }
                if (!checkID(currentSelectedID)){
                    continue;
                }
                if (IDList.isEmpty()) {
                    IDList.add(currentSelectedID);
                }
                else if (IDList.contains(currentSelectedID)) {
                    System.out.println("ID already selected");
                } else {
                    IDList.add(currentSelectedID);
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid input");
            }
        } while (true);
        System.out.println(IDList.toString());
        if (IDList.isEmpty()) {
            System.out.println("You did not choose any person");
            System.out.println("\nEvent 3 ended.");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            return true;
        }
        
        // ask user to input today's lunch time info
        int yourStartTime, yourEndTime;
        System.out.println("Provide your lunch starting time and planned duration for lunch today");
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
        
        // get maximum number of reputation that can get today
        ArrayList<Integer[]> validIntersection = new ArrayList<>();
        // store all intersected lunch time interval
        for (int i = 0; i<IDList.size() ; i++) {
            try {
                con = DriverManager.getConnection(DBURL, DBUSER, DBPASSWD);
                st = con.createStatement();
                String getInfo = "Select *\n" +
                "from db_owner.Persons Person\n" +
                "where Person.ID ="+IDList.get(i)+';';
                rs = st.executeQuery(getInfo);

                if(rs.next()) {
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
        }
        int max = 0; // maximum reputation
        if(!validIntersection.isEmpty()) {
            validIntersection = sortStartTimeEndTime(validIntersection); // sort the list
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
            }
        }
        System.out.println("The maximum number of reputation that you can get is " + max + "\n");
        if (max == 0) {
            System.out.println("Oops.. seems like you do not have the chance to meet them during lunch time.");
            System.out.println("\nEvent 3 ended.");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            return true;
        }
        
        // Randomly select person(s) that you have lunch with
        Random r = new Random();
        int n = r.nextInt(max) + 1;
        if (n == 0 ) {
            System.out.println("Oops.. seems like you were unlucky today. None of them had their lunch during ur lunch time.");
            System.out.println("\nEvent 3 ended.");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            return true;
        }
        System.out.println("You were lucky to have lunch with " + n + " person(s) today.");
        for (int i=0; i<n; i++) {
            int id = validIntersection.remove(r.nextInt(validIntersection.size()))[0];
            // check whether the two persons were already connected
            if (!hasEdge(yourID, id)) {
                addEdge(yourID, id);
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
                "WHERE MATCH(Person2-(Re)->Person1) AND Person1.ID ="+yourID+" AND Person2.ID ="+id+';';
                rs = st.executeQuery(getRep);
                if (rs.next()) {
                    currentRep = rs.getInt("reputation");
                }
                
                // update rep
                String updateRep = "UPDATE db_owner.Relationships SET reputation = " +(currentRep+1)+" FROM db_owner.Persons AS Person1, \n" +
                "	 db_owner.Persons AS Person2,\n" +
                "	 db_owner.Relationships AS Re\n" + 
                "WHERE MATCH(Person2-(Re)->Person1) AND Person1.ID =" +yourID+" AND Person2.ID = " +id+';';
                st.execute(updateRep);
                
                // get updated rep
                String selectRep = "SELECT Re.reputation \n" +
                "From db_owner.Persons P1, db_owner.Persons P2, db_owner.Relationships Re\n" +
                "Where match(P2-(Re)->P1) and P1.ID =" +yourID+"and P2.ID = " +id+';';
                rs = st.executeQuery(selectRep);
                if (rs.next()) {
                    int newRep = rs.getInt("reputation");
                    System.out.println("Your reputation relative to student, ID="+id+
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
    
    /**
     * To check the existence of the ID
     * @param ID ID to be checked
     * @return If ID found return true, else, return false
     */
    public static boolean checkID(int ID) {
        try {
            con = DriverManager.getConnection(DBURL, DBUSER, DBPASSWD);
            st = con.createStatement();
            String checkID = "SELECT * FROM db_owner.Persons P WHERE P.ID =" +ID+';';
            rs = st.executeQuery(checkID);
            if (!rs.next()) {
                System.out.println("ID not found");
                return false;
            }
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Sort the array list according to the average starting lunch time
     * @param timeSlots The array list to be sorted
     * @return sorted array list
     */
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
    
    /**
     * method to add friend for Event 1 to Event 3 if needed
     * @param ID1
     * @param ID2
     * @return return true if add friend is successful, return false otherwise.
     */
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
    
    /**
     * method to add edge between students in the list for event 1, 2, 3, 5
     * @param ID1
     * @param ID2
     * @return return true if edge addition is successful, return false otherwise.
     */
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
    
    /**
     * method to check whether two students are friends
     * @param ID1
     * @param ID2
     * @return return true if they are friends return true, else, return false.
     */
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
    
    /**
     * check whether there is an edge between two students
     * @param ID1
     * @param ID2
     * @return true if edge was found, else, return false.
     */
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
    
    /**
     * check whether there is a path between two student
     * @param fromID
     * @param toID
     * @return true if path was found, else, return false.
     */
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
    
    /**
     * get the size of the graph
     * @return size of the graph
     */
    public static int getNumberOfPerson() {
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
    
    private static int relationNumber;
    
    /**
     * Event 6 Friendship
     * @return if the method run successfully, return true, else, return false.
     */
    public static boolean eventSix() {
        System.out.println("EVENT 6: Friendship");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        relationNumber = 0;
        // create a tables required to store the graph for this event
        try {
            con = DriverManager.getConnection(DBURL, DBUSER, DBPASSWD);
            st = con.createStatement();
            String createTableForNode = "DROP TABLE IF EXISTS db_owner.EventSixPersons\n" +
            "CREATE TABLE db_owner.EventSixPersons (id INTEGER PRIMARY KEY, created_at DATETIME) AS NODE";
            st.execute(createTableForNode);
            
            String createTableForEdge = "DROP TABLE IF EXISTS db_owner.EventSixRelations\n" +
            "CREATE TABLE db_owner.EventSixRelations (created_at DATETIME) AS EDGE";
            st.execute(createTableForEdge);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
        
        Scanner sc = new Scanner(System.in);
        System.out.println("Input");
        ArrayList<Integer> persons = new ArrayList<>();
        int x = 0;
        do {
            try{
            String in = sc.next();
            x = Integer.parseInt(in);
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid input. Only positive integer are allowed.");
                continue;
            } 
            if (x==0) {// stop process
                System.out.println("No line exist.");
                System.out.println("\nEvent 6 ended.");
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
                return true;
            } 
            if (x<0) {
                System.out.println("Invalid input. Only positive integer are allowed.");
                continue;
            }
            break;
        } while (true);
        
        // get list input
        for (int i = 0; i < x; ) { 
            int srt = -1, end = -1;
            try {
                String in = sc.next();
                srt = Integer.parseInt(in);
                in = sc.next();
                end = Integer.parseInt(in);
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid input. Only positive integer are allowed.");
                continue;
            }
            if (srt < 1 || end < 1) {
                System.out.println("Invalid input. Only positive integer are allowed.");
                continue;
            }
            
            // add node
            if(persons.isEmpty()) {
                persons.add(srt);
                addPersonEventSix(srt);
            }
            if(!persons.contains(srt)){
                persons.add(srt);
                addPersonEventSix(srt);
            }
            if(!persons.contains(end)){
                persons.add(end);
                addPersonEventSix(end);
            }
            // add relation
            addEdgeEventSix(srt, end);
            i++;
        }
        System.out.println("You can form the following friendship(s):");
        for (int i = 0; i < persons.size(); i++) {
            for (int j = i+1; j < persons.size(); j++) {
                find_FRIEND(persons.get(i),persons.get(j),new ArrayList<>()); // find relation
            }
        }
        System.out.println("Total friendship(s): "+relationNumber);
        System.out.println("\nEvent 6 ended.");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        return true;
    }
    
    /**
     * Method for event 6 to find path to know people
     * @param srcId source node
     * @param desId destination node
     * @param arr arr containing the nodes
     */
    public static void find_FRIEND(int srcId, int desId, ArrayList<Integer>arr){
        ArrayList<Integer> fid = getFriend(srcId, "EventSixPersons", "EventSixRelations");
        arr.add(srcId);
        for (int i = 0; i < fid.size(); i++) {
            if(fid.get(i).equals(desId)){
                ArrayList<Integer>newArray = new ArrayList<>();
                for (int j = 0; j < arr.size(); j++) {
                    newArray.add(arr.get(j));
                }
                newArray.add(desId);
                relationNumber++;
                show(newArray);
            }else{
                if(!arr.contains(fid.get(i))){
                    ArrayList<Integer>newArray = new ArrayList<>();
                    for (int j = 0; j < arr.size(); j++) {
                        newArray.add(arr.get(j));
                    }
                    find_FRIEND(fid.get(i), desId, newArray);
                }
            }
        }
    }
    
    /**
     * Method to display the path found in event 6
     * @param ar the path
     */
    public static void show(ArrayList<Integer>ar){
        System.out.println(relationNumber + ". " + ar.toString());
    }
    
    /**
     * Method for event 6 to add node
     * @param id
     */
    public static void addPersonEventSix(int id) {
        try {
            con = DriverManager.getConnection(DBURL, DBUSER, DBPASSWD);
            st = con.createStatement();
            String insertNode = "insert into db_owner.EventSixPersons\n" +
                    "values ("+id+", getdate());";
            st.execute(insertNode);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Method for event 6 to add edge
     * @param id1
     * @param id2
     */
    public static void addEdgeEventSix(int id1, int id2) {
        try {
            con = DriverManager.getConnection(DBURL, DBUSER, DBPASSWD);
            st = con.createStatement();
            String insertRelation = "insert into db_owner.EventSixRelations\n" +
            "values ((SELECT $node_id FROM db_owner.EventSixPersons WHERE id ="+id1+"),(SELECT $node_id FROM db_owner.EventSixPersons WHERE id ="+id2+"), getdate()),\n"+
            "((SELECT $node_id FROM db_owner.EventSixPersons WHERE id ="+id2+"),(SELECT $node_id FROM db_owner.EventSixPersons WHERE id ="+id1+"), getdate());";
            st.execute(insertRelation);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Method to get all the friends for event 6
     * @param id
     * @param nodeName
     * @param edgeName
     * @return list of friends
     */
    public static ArrayList<Integer> getFriend(int id, String nodeName, String edgeName) {
        ArrayList<Integer> friend = new ArrayList<>();
        try {
            con = DriverManager.getConnection(DBURL, DBUSER, DBPASSWD);
            st = con.createStatement();
            String getFriend = "select P2.id from db_owner." +nodeName+ " P1, db_owner."+nodeName+" P2,"
                    + " db_owner."+edgeName+" re \n where match (P1-(re)->P2) and P1.id = " +id+';';
            rs = st.executeQuery(getFriend);
            while (rs.next()) {
                friend.add(rs.getInt("id"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return friend;
    }
    
    /**
     * Extra feature: Event 3 upgrade - Parallel Farming
     * @param yourID
     * @return if the method run successfully, return true, else, return false.
     */
    public static boolean parallelFarming(int yourID) {
        Scanner sc= new Scanner(System.in); 
        System.out.println("EVENT 3 UPGRADED: Your Road to Glory with Parallel Farming");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        
        // ask user for interested persons
        ArrayList<Integer> IDList = new ArrayList<>();
        System.out.println("Select persons that you are interested to have lunch with [Enter '0' to exit]");
        do {
            System.out.println("Input ID: ");
            try {
                String in = sc.next();
                int currentSelectedID = Integer.parseInt(in);
                if (currentSelectedID == 0) {
                    System.out.println("You have exited selection with code '0'");
                    break;
                }
                if (currentSelectedID == yourID) {
                    System.out.println("This is you");
                    continue;
                }
                if (!checkID(currentSelectedID)){
                    continue;
                }
                if (IDList.isEmpty()) {
                    IDList.add(currentSelectedID);
                }
                else if (IDList.contains(currentSelectedID)) {
                    System.out.println("ID already selected");
                } else {
                    IDList.add(currentSelectedID);
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid input");
            }
        } while (true);
        System.out.println(IDList.toString());
        if (IDList.isEmpty()) {
            System.out.println("You did not choose any person");
            System.out.println("\nParallel Farming ended.");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            return true;
        }
        
        // ask user to input today's lunch time info
        int yourStartTime, yourEndTime;
        System.out.println("Provide your lunch starting time and planned duration for lunch today");
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
        
        // get maximum number of reputation that can get today
        ArrayList<Integer[]> validIntersection = new ArrayList<>();
        // store all intersected lunch time interval
        for (int i = 0; i<IDList.size() ; i++) {
            try {
                con = DriverManager.getConnection(DBURL, DBUSER, DBPASSWD);
                st = con.createStatement();
                String getInfo = "Select *\n" +
                "from db_owner.Persons Person\n" +
                "where Person.ID ="+IDList.get(i)+';';
                rs = st.executeQuery(getInfo);

                if(rs.next()) {
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
        }
        int max = 0; // maximum reputation
        // a queue-like list will be used to get the maximum reputation
        if(!validIntersection.isEmpty()) {
            validIntersection = sortStartTimeEndTime(validIntersection); // sort the list
            ArrayList<Integer[]> total = new ArrayList<>();
            for (int i=0; i<validIntersection.size() ; i++) {
                if (total.isEmpty()) {
                    total.add(validIntersection.get(i));
                    max++;
                } else if (validIntersection.get(i)[1].compareTo(total.get(getMaxEndTime(total))[2])<0 && total.size()<3) {
                    total.add(validIntersection.get(i));
                    max++;
                } else if (validIntersection.get(i)[1].compareTo(total.get(getMinEndTime(total))[2])>0 && total.size()==3) {
                    total.remove(getMinEndTime(total));
                    total.add(validIntersection.get(i));
                    max++;
                } else {
                    total.remove(getMinEndTime(total));
                }
            }
        }
        System.out.println("The maximum number of reputation that you can get is " + max + "\n");
        if (max == 0) {
            System.out.println("Oops.. seems like you do not have the chance to meet them during lunch time.");
            System.out.println("\nParallel Farming ended.");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            return true;
        }
        
        // Randomly select person(s) that you have lunch with
        Random r = new Random();
        int n = r.nextInt(max) + 1;
        if (n == 0 ) {
            System.out.println("Oops.. seems like you were unlucky today. None of them had their lunch during ur lunch time.");
            System.out.println("\nParallel Farming ended.");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            return true;
        }
        System.out.println("You were lucky to have lunch with " + n + " person(s) today.");
        for (int i=0; i<n; i++) {
            int id = validIntersection.remove(r.nextInt(validIntersection.size()))[0];
            // check whether the two persons were already connected
            if (!hasEdge(yourID, id)) {
                addEdge(yourID, id);
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
                "WHERE MATCH(Person2-(Re)->Person1) AND Person1.ID ="+yourID+" AND Person2.ID ="+id+';';
                rs = st.executeQuery(getRep);
                if (rs.next()) {
                    currentRep = rs.getInt("reputation");
                }
                
                // update rep
                String updateRep = "UPDATE db_owner.Relationships SET reputation = " +(currentRep+1)+" FROM db_owner.Persons AS Person1, \n" +
                "	 db_owner.Persons AS Person2,\n" +
                "	 db_owner.Relationships AS Re\n" + 
                "WHERE MATCH(Person2-(Re)->Person1) AND Person1.ID =" +yourID+" AND Person2.ID = " +id+';';
                st.execute(updateRep);
                
                // get updated rep
                String selectRep = "SELECT Re.reputation \n" +
                "From db_owner.Persons P1, db_owner.Persons P2, db_owner.Relationships Re\n" +
                "Where match(P2-(Re)->P1) and P1.ID =" +yourID+"and P2.ID = " +id+';';
                rs = st.executeQuery(selectRep);
                if (rs.next()) {
                    int newRep = rs.getInt("reputation");
                    System.out.println("Your reputation relative to student, ID="+id+
                        " increased from " + currentRep + " to " + newRep);
                }
                
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Parallel Farming ended.");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        return true;
    }
    
    /**
     * Method for event 6 to get earliest lunch end time
     * @param lunchTime
     * @return the index of the student with the earliest lunch end time
     */
    public static int getMinEndTime(ArrayList<Integer[]> lunchTime) {
        int minIndex = 0;
        int time1 = lunchTime.get(0)[2];
        for (int i =1 ; i< lunchTime.size() ; i++) {
            int temp = lunchTime.get(i)[2];
            if (temp < time1) {
                minIndex = i;
            }
        }
        return minIndex;
    }
    
    /**
     * Method for event 6 to get latest lunch end time
     * @param lunchTime
     * @return the index of the student with the latest lunch end time
     */
    public static int getMaxEndTime(ArrayList<Integer[]> lunchTime) {
        int maxIndex = 0;
        int time1 = lunchTime.get(0)[2];
        for (int i =1 ; i< lunchTime.size() ; i++) {
            int temp = lunchTime.get(i)[2];
            if (temp > time1) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}
