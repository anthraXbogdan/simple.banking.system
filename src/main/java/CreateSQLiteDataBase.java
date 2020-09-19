import javax.swing.*;
import java.sql.*;
import java.util.Scanner;

public class CreateSQLiteDataBase extends AccountsManager{

    static void createNewDatabase() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Starting to create a new SQLite Database...");
        System.out.println("Please introduce a valid SQLite database name (ex: some_database.s3db):");
        while (scanner.hasNext()) {
            dbName = scanner.nextLine();
            break;
        }
        String url = "jdbc:sqlite:D:/JAVA_LEARNING/DATABASES/" + dbName;

        try {
            Connection conn = DriverManager.getConnection(url);
            if (conn != null) {
                //DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The new database \"" + dbName + "\"" + " has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createNewTable() {
        // SQLite connection string
        String url = "jdbc:sqlite:D:/JAVA_LEARNING/DATABASES/" + dbName;

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS card (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " number TEXT,\n"
                + " pin TEXT,\n"
                + " balance INTEGER DEFAULT 0\n"
                + ");";

        try{
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("The table \"card\" has been created!");
    }
}
