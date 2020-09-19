import org.sqlite.SQLiteDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class ConnectToSQLiteDB extends AccountsManager{

    static void connectToJDBC() {
        Scanner scanner = new Scanner(System.in);

        String url = "jdbc:sqlite:D:/JAVA_LEARNING/DATABASES/" + dbName;
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        System.out.println("Connect to a database...");
        System.out.println("Please introduce a valid SQLite database name (ex: some_database.s3db):");
        dbName = scanner.nextLine();

        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(5)) {
                System.out.println("Connection is established! \nYour are now connected to \"" + dbName + "\" database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
