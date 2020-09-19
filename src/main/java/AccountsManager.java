import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Scanner;

class InsertCardData extends AccountsManager {
    public void insert(String number, String pin, Integer balance) {
        String sql = "INSERT INTO card(number, pin, balance) VALUES(?,?,?)";
        String url = "jdbc:sqlite:D:/JAVA_LEARNING/DATABASES/" + dbName;

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, number);
            pstmt.setString(2, pin);
            pstmt.setInt(3, balance);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}

public class AccountsManager {
    static ArrayList<String> creditCardsNumbersStorage = new ArrayList<>();
    static ArrayList<String> creditCardPinNumbersStorage = new ArrayList<>();
    static LinkedHashMap<String, String> creditCardsList = new LinkedHashMap<>();
    static String dbName;
    static String cardNo;
    static String cardToTransfer;
    static String pinNo;
    static ResultSet rs;
    static int transferSum;
    static int accountBalance;

    static void mainMenu() {
        System.out.println("Please choose action:");
        System.out.println("1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");
    }

    static void accountMenu() {
        System.out.println("1. Balance");
        System.out.println("2. Add income");
        System.out.println("3. Do transfer");
        System.out.println("4. Close account");
        System.out.println("5. Log out");
        System.out.println("0. Exit");
    }

    static void cardGenerator() {
        Random random = new Random();

        //**Generate and validate card No using Luhn Algorithm**
        StringBuilder bin = new StringBuilder();
        bin.append("400000");//this is BIN(Bank Identification Number)

        //generate a unique Account Identifier
        for (int i = 0; i < 9; i++) {
            int x = random.nextInt(10);
            bin.append(x);
        }

        //validation process
        String uncheckedCardNo = bin.toString();

        ArrayList<Integer> unchecked = new ArrayList<>();
        unchecked.add(0, 0);
        for (char ch : uncheckedCardNo.toCharArray()) {
            unchecked.add(Integer.parseInt(String.valueOf(ch)));
        }

        ArrayList<Integer> multiply = new ArrayList<>();
        for (int i = 1; i < unchecked.size(); ++i) {
            if (i % 2 != 0) {
                multiply.add(unchecked.get(i) * 2);
            } else {
                multiply.add(unchecked.get(i));
            }
        }
        multiply.set(0, unchecked.get(1) * 2);

        ArrayList<Integer> subtract = new ArrayList<>();
        for (Integer integer : multiply) {
            if (integer > 9) {
                subtract.add(integer - 9);
            } else {
                subtract.add(integer);
            }
        }

        int sum = 0;
        for (Integer integer : subtract) {
            sum += integer;
        }

        int checksum = 0;
        for (int i = 0; i < 10; i++) {
            if ((sum + i) % 10 == 0) {
                checksum += i;
            }
        }
        unchecked.remove(0);
        unchecked.add(checksum);
        StringBuilder checkedCard = new StringBuilder();
        for (int i : unchecked) {
            checkedCard.append(i);
        }

        String cardNo = checkedCard.toString();
        creditCardsNumbersStorage.add(cardNo);

        //**generate PIN Number**
        StringBuilder b = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            int x = random.nextInt(10);
            b.append(x);
        }

        String pinNo = b.toString();
        creditCardPinNumbersStorage.add(pinNo);

        creditCardsList.put(cardNo, pinNo);

        InsertCardData app = new InsertCardData();
        app.insert(cardNo, pinNo, 0);

        System.out.println();
        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(cardNo);
        System.out.println("Your card PIN:");
        System.out.println(pinNo);

    }

    public static void selectBalance() {
        //retrieve balance query
        String url = "jdbc:sqlite:D:/JAVA_LEARNING/DATABASES/" + dbName;

        String sql = "SELECT balance\n" +
                "FROM card\n" +
                "WHERE\n" +
                "number = " + cardNo;
        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            accountBalance = rs.getInt("balance");
            System.out.println("Balance: " + rs.getInt("balance"));
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public static void addIncome() {
        Scanner scanner = new Scanner(System.in);
        String url = "jdbc:sqlite:D:/JAVA_LEARNING/DATABASES/" + dbName;

        System.out.println("Enter income:");
        int income = scanner.nextInt();

        String sql = "UPDATE card\n" +
                "SET balance = balance + " + income +
                " WHERE\n" +
                " number = " + cardNo;

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt  = conn.createStatement();
            stmt.execute(sql);
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Income was added!");
    }

    public static void closeAccount() {
        String url = "jdbc:sqlite:D:/JAVA_LEARNING/DATABASES/" + dbName;

        String sql = "DELETE FROM card\n" +
                " WHERE number = " + cardNo;

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt  = conn.createStatement();
            stmt.execute(sql);
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        creditCardsNumbersStorage.remove(cardNo);
        creditCardsList.remove(cardNo);
        System.out.println("The account has been closed!");
    }

    public static void doTransfer() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Transfer\nEnter card number:");
        while (scanner.hasNext()) {
            cardToTransfer = scanner.nextLine();
            if (!creditCardsNumbersStorage.contains(cardToTransfer)) {
                System.out.println("Such a card does not exist.");
                break;
            } else {
                if (cardToTransfer.equals(cardNo)) {
                    System.out.println("You can't transfer money to the same account!");
                    break;
                } else {
                    System.out.println("Enter how much money you want to transfer:");
                    transferSum = scanner.nextInt();
                    if (transferSum > accountBalance) {
                        System.out.println("Not enough money!");
                    } else {
                        String url = "jdbc:sqlite:D:/JAVA_LEARNING/DATABASES/" + dbName;

                        String sql1 = "UPDATE card\n" +
                                "SET balance = balance + " + transferSum +
                                " WHERE\n" +
                                " number = " + cardToTransfer;

                        String sql2 = "UPDATE card\n" +
                                "SET balance = balance - " + transferSum +
                                " WHERE\n" +
                                " number = " + cardNo;

                        try {
                            Connection conn = DriverManager.getConnection(url);
                            Statement stmt  = conn.createStatement();
                            stmt.execute(sql1);
                            stmt.execute(sql2);
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }
                        System.out.println("Success!");
                    }
                }
                break;
            }
        }

    }

    static void accountLogin(){
        String url = "jdbc:sqlite:D:/JAVA_LEARNING/DATABASES/" + dbName;
        String sql = "SELECT number, pin\n" +
                "FROM card;";
        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                creditCardsNumbersStorage.add(rs.getString("number"));
                creditCardPinNumbersStorage.add(rs.getString("pin"));
                creditCardsList.put(rs.getString("number"), rs.getString("pin"));
            }
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println();
        System.out.println("Enter your card number:");
        while (scanner.hasNext()) {
            cardNo = scanner.nextLine();
            System.out.println("Enter your PIN:");
            pinNo = scanner.nextLine();

            if (creditCardsNumbersStorage.contains(cardNo) && creditCardPinNumbersStorage.contains(pinNo)) {
                System.out.println();
                System.out.println("You have successfully logged in!");
                System.out.println();
                accountMenu();

                while (scanner.hasNext()) {
                    String action = scanner.nextLine();
                    if (action.equals("5")) {
                        System.out.println("You have successfully logged out!");
                        break;
                    }
                    else if (action.equals("4")) {
                        System.out.println();
                        closeAccount();
                        System.out.println();
                        break;
                    } else{
                        switch (action) {
                            case "1":
                                System.out.println();
                                selectBalance();
                                System.out.println();
                                accountMenu();
                                break;
                            case "2":
                                System.out.println();
                                addIncome();
                                System.out.println();
                                accountMenu();
                                break;
                            case "3":
                                System.out.println();
                                doTransfer();
                                System.out.println();
                                accountMenu();
                                break;
                            case "0":
                                System.out.println();
                                System.out.println("Bye!");
                                System.exit(0);
                                break;
                            default:
                                break;
                        }
                    }
                }
                break;
            } else {
                System.out.println();
                System.out.println("Wrong card number or PIN!");
                System.out.println();
                break;
            }
        }

    }

    static void createAccounts(){
        Scanner scanner = new Scanner(System.in);
        System.out.println();
        mainMenu();

        while (scanner.hasNext()) {
            String action = scanner.nextLine();
            if (action.equals("0")) {
                break;
            } else {
                switch(action) {
                    case "1":
                        cardGenerator();
                        System.out.println();
                        System.out.println(creditCardsList);
                        break;
                    case "2":
                        accountLogin();
                        break;
                }
                System.out.println();
                mainMenu();
            }
        }
    }

    public static void main(String[] args) {

        ConnectToSQLiteDB.connectToJDBC();
        CreateSQLiteDataBase.createNewTable();
        createAccounts();

    }
}
