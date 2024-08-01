import java.sql.*;
import java.util.Scanner;

public class BankY {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/BankY";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root"; // Replace with your MySQL password

    public static void main(String[] args) {
        BankY bank = new BankY();
        bank.run();
    }

    public void run() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("1. Create Account");
                System.out.println("2. Deposit Funds");
                System.out.println("3. Withdraw Funds");
                System.out.println("4. Transfer Funds");
                System.out.println("5. View Account");
                System.out.println("6. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        createAccount(scanner, connection);
                        break;
                    case 2:
                        depositFunds(scanner, connection);
                        break;
                    case 3:
                        withdrawFunds(scanner, connection);
                        break;
                    case 4:
                        transferFunds(scanner, connection);
                        break;
                    case 5:
                        viewAccount(scanner, connection);
                        break;
                    case 6:
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid option. Try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createAccount(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter account ID: ");
        String accountId = scanner.next();
        System.out.print("Enter account holder name: ");
        String name = scanner.next();
        String query = "INSERT INTO accounts (accountId, name, balance) VALUES (?, ?, 0)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, accountId);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
            System.out.println("Account created successfully.");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry error code
                System.out.println("Account already exists.");
            } else {
                throw e;
            }
        }
    }

    private void depositFunds(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter account ID: ");
        String accountId = scanner.next();
        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();
        String query = "UPDATE accounts SET balance = balance + ? WHERE accountId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setDouble(1, amount);
            pstmt.setString(2, accountId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Deposit successful.");
            } else {
                System.out.println("Account not found.");
            }
        }
    }

    private void withdrawFunds(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter account ID: ");
        String accountId = scanner.next();
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();
        String query = "SELECT balance FROM accounts WHERE accountId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                if (balance >= amount) {
                    String updateQuery = "UPDATE accounts SET balance = balance - ? WHERE accountId = ?";
                    try (PreparedStatement updatePstmt = connection.prepareStatement(updateQuery)) {
                        updatePstmt.setDouble(1, amount);
                        updatePstmt.setString(2, accountId);
                        updatePstmt.executeUpdate();
                        System.out.println("Withdrawal successful.");
                    }
                } else {
                    System.out.println("Insufficient funds.");
                }
            } else {
                System.out.println("Account not found.");
            }
        }
    }

    private void transferFunds(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter source account ID: ");
        String sourceId = scanner.next();
        System.out.print("Enter destination account ID: ");
        String destId = scanner.next();
        System.out.print("Enter amount to transfer: ");
        double amount = scanner.nextDouble();

        String query = "SELECT balance FROM accounts WHERE accountId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, sourceId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                if (balance >= amount) {
                    String withdrawQuery = "UPDATE accounts SET balance = balance - ? WHERE accountId = ?";
                    String depositQuery = "UPDATE accounts SET balance = balance + ? WHERE accountId = ?";
                    try (PreparedStatement withdrawPstmt = connection.prepareStatement(withdrawQuery);
                         PreparedStatement depositPstmt = connection.prepareStatement(depositQuery)) {
                        connection.setAutoCommit(false); // Begin transaction

                        withdrawPstmt.setDouble(1, amount);
                        withdrawPstmt.setString(2, sourceId);
                        withdrawPstmt.executeUpdate();

                        depositPstmt.setDouble(1, amount);
                        depositPstmt.setString(2, destId);
                        depositPstmt.executeUpdate();

                        connection.commit(); // Commit transaction
                        System.out.println("Transfer successful.");
                    } catch (SQLException e) {
                        connection.rollback(); // Rollback transaction on error
                        throw e;
                    } finally {
                        connection.setAutoCommit(true); // End transaction
                    }
                } else {
                    System.out.println("Insufficient funds.");
                }
            } else {
                System.out.println("Source account not found.");
            }
        }
    }

    private void viewAccount(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter account ID: ");
        String accountId = scanner.next();
        String query = "SELECT * FROM accounts WHERE accountId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                double balance = rs.getDouble("balance");
                System.out.println("Account ID: " + accountId);
                System.out.println("Name: " + name);
                System.out.println("Balance: " + balance);
            } else {
                System.out.println("Account not found.");
            }
        }
    }
}
