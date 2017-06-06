/**
 * Created by jonathanw on 6/5/2017.
 */
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.io.*;
import java.util.*;

public class DBAPI
{
    private String connectionString = "jdbc:mysql://localhost:3306/lt_checkout_db";
    private String password = ""; // on laptop password is "root"
    private String username = "root";
    private Connection conn;

    public static void main (String[] args)
    { // String ltName, short numChargers, short numCardReaders, short numLTBags,
        // short numWebCams, String description, boolean checkedOut, boolean overdue
        try
        {
            DBAPI db = new DBAPI();
            db.createNewLT("SACTJJKB-LOAN-0");
            db.createNewLT("SACTJJKB-LOAN-1");

            java.util.Date dt = new java.util.Date();

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String currentTime = sdf.format(dt);

            db.checkoutLT("SACTJJKB-LOAN-0", "224005649", "Jonathan Westerfield", 0, 0, 0, 0,
                    "2017-06-05 02:50:24", "2018-04-25 12:20:18", "test lt checkout", false, false);

            db.checkoutLT("SACTJJKB-LOAN-1", "123456789", "John Doe", 0, 0, 0, 0,
                    "2017-06-05 02:50:24", "2018-04-25 12:20:18", "test lt checkout", false, false);

            /*ArrayList<String> laptopHistory = db.seeLaptopHistory("SACTJJKB-LOAN-0");
            System.out.println();
            for(int i = 0; i < laptopHistory.size(); i++)
            {
                System.out.print(laptopHistory.get(i) + " ");
                if(i % 12 == 11)
                {
                    System.out.println();
                }
            }*/

            db.searchByUser("Jonathan Westerfield");
            System.out.println();
            db.searchByUIN("224005649");
        }
        catch(SQLException | ClassNotFoundException | java.io.IOException e)
        {
            e.printStackTrace();
            System.err.println(e);
        }

    }

    // class constructor
    DBAPI() throws ClassNotFoundException, SQLException
    {
        Class.forName("com.mysql.jdbc.Driver");
        this.conn = DriverManager.getConnection(connectionString, username, password);
        System.out.println("Database connection established");
    }


    // creates new laptop name in the file with the laptop names
    public void createNewLT(String ltName) throws FileNotFoundException, java.io.IOException
    {
        File f = new File(".\\" + "resource" + "\\" + "laptop.dat");
        if((!f.exists() && !f.isDirectory())) // if the text file for laptops doesn't exist, create a new one
        {
            System.out.println("File not found\nCreating a new one");
            PrintWriter print = new PrintWriter(new File(".\\" + "resource" + "\\" + "laptop.dat"));
            print.println(ltName);
            System.out.println("Creating new laptop name file and writing " + ltName
                    + "to it");
            print.close();
        }
        else if(f.exists() && !f.isDirectory()) // if file exists, append new LT name to it
        {
            System.out.println("File found\n appending to file");
            File file = new File(".\\" + "resource" + "\\" + "laptop.dat");
            FileWriter writer = new FileWriter(file, true);
            writer.write(ltName + "\n");
            System.out.println("Writing " + ltName + "to laptop name file");
            writer.close();
        }

        return;
    }

    public ArrayList<String> searchByUser(String userName) throws SQLException
    {
        //SQL statement that gets whole table around a certain laptop name
        String query = "SELECT * FROM lt_info WHERE Name = \"" + userName + "\"";
        System.out.println("Inserting query for checking out\n" + query);
        Statement seeHistory = conn.createStatement();//4/11/97
        ResultSet result = seeHistory.executeQuery(query);
        ResultSetMetaData rsmd = result.getMetaData();

        int columnsNumber = rsmd.getColumnCount();

        ArrayList<String> resultList = new ArrayList<String>();

        System.out.println("Search by user function return");

        while(result.next())
        {
            for(int i = 1; i <= columnsNumber; i++)
            {
                //prints it out exactly like the table would look like
                System.out.print(result.getString(i) + " "); // print one element of a row
                resultList.add(result.getString(i));
            }
            System.out.println();
        }
        System.out.println();

        return resultList;
    }

    public ArrayList<String> searchByUIN(String UIN) throws SQLException
    {
        //SQL statement that gets whole table around a certain laptop name
        String query = "SELECT * FROM lt_info WHERE UIN = \"" + UIN + "\"";
        System.out.println("Inserting query for checking out\n" + query);
        Statement seeHistory = conn.createStatement();//4/11/97
        ResultSet result = seeHistory.executeQuery(query);
        ResultSetMetaData rsmd = result.getMetaData();

        int columnsNumber = rsmd.getColumnCount();

        ArrayList<String> resultList = new ArrayList<String>();

        System.out.println("Search by UIN function return");

        while(result.next())
        {
            for(int i = 1; i <= columnsNumber; i++)
            {
                //prints it out exactly like the table would look like
                System.out.print(result.getString(i) + " "); // print one element of a row
                resultList.add(result.getString(i));
            }
            System.out.println();
        }
        System.out.println();

        return resultList;
    }

    //shows the history of a laptop, takes laptop name as arguement
    public ArrayList<String> seeLaptopHistory(String ltName) throws SQLException
    {
        //SQL statement that gets whole table around a certain laptop name
        String query = "SELECT * FROM lt_info WHERE LT_Name = \"" + ltName + "\"";
        System.out.println("Inserting query for checking out\n" + query);
        Statement seeHistory = conn.createStatement();//4/11/97
        ResultSet result = seeHistory.executeQuery(query);
        ResultSetMetaData rsmd = result.getMetaData();

        int columnsNumber = rsmd.getColumnCount();

        // Iterate through the data in the result set and display it

        //puts results into an arraylist and returns it
        ArrayList<String> resultList= new ArrayList<String>();

        while(result.next())
        {
            for(int i = 1; i <= columnsNumber; i++)
            {
                //prints it out exactly like the table would look like
                System.out.print(result.getString(i) + " "); // print one element of a row
                resultList.add(result.getString(i));
            }
            System.out.println();
        }
        return resultList;
    }

    //checkouts a laptop by inserting information about the laptop checkout into the table
    public void checkoutLT(String ltName, String UIN, String name, int numChargers, int numCardReaders, int numLTBags,
                            int numWebCams, String startDate, String endDate, String description, boolean checkedOut, boolean overdue) throws SQLException
    {
        String query = "INSERT INTO lt_info VALUES (\"" + ltName + "\", " + UIN + ",\"" + name + "\", " +
                numChargers + ", " + numCardReaders + ", " + numLTBags + ", " + numWebCams + ", \""
                +  startDate + "\", \"" + endDate + "\", \"" + description + "\", " + checkedOut + ", " + overdue + ")";
        System.out.println("Inserting query for checking out\n" + query);
        PreparedStatement insertStatement = conn.prepareStatement(query);
        insertStatement.execute();
    }

    public void createTable() throws SQLException
    {
        String query = "CREATE TABLE `lt_info` (\n" +
                "\t`LT_Name` VARCHAR(20) NOT NULL,\n" +
                "\t`UIN` VARCHAR(11) NOT NULL,\n" +
                "\t`Name` VARCHAR(20) NOT NULL,\n" +
                "\t`Num_Chargers` INT(10) UNSIGNED NOT NULL,\n" +
                "\t`Num_Card_Reader` INT(10) UNSIGNED NOT NULL,\n" +
                "\t`Num_LT_Bags` INT(10) UNSIGNED NOT NULL,\n" +
                "\t`Num_WebCams` INT(10) UNSIGNED NOT NULL,\n" +
                "\t`Start_Date` DATETIME NOT NULL,\n" +
                "\t`End_Date` DATETIME NOT NULL,\n" +
                "\t`Description` VARCHAR(800) NOT NULL,\n" +
                "\t`Checked_Out` TINYINT(1) NOT NULL,\n" +
                "\t`Overdue` TINYINT(1) NOT NULL\n" +
                ")\n" +
                "COLLATE='latin1_swedish_ci'\n" +
                "ENGINE=InnoDB\n" +
                ";";
        System.out.println("Inserting query for checking out\n" + query);
        PreparedStatement insertStatement = conn.prepareStatement(query);
        insertStatement.execute();
    }

    //closes the database
    public void close() throws SQLException
    {
        conn.close();
    }
}
