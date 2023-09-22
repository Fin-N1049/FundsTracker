package fundtracker;
import java.sql.*;
import java.util.Scanner;

public class FundTracker {
    public static void main(String[] args) throws Exception{
        Class.forName("com.mysql.cj.jdbc.Driver");
        MoneyManager manager = new MoneyManager();
        manager.run();
    }
}

class MoneyManager {
    private Connection conn;
    private CategoryManager categoryManager;
    private ExpenseManager expenseManager;

    public MoneyManager() {
        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://localhost/money_manager", "your_db_user", "your_db_password");
            this.categoryManager = new CategoryManager(conn);
            this.expenseManager = new ExpenseManager(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nMoney Management System Menu:");
            System.out.println("1. View Balance");
            System.out.println("2. Record Expense");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    viewBalance();
                    break;
                case 2:
                    recordExpense(scanner);
                    break;
                case 3:
                    System.out.println("Exiting the Money Management System. Goodbye!");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private void viewBalance() {
        // View balances for each category and overall balance
        categoryManager.listCategories();
        expenseManager.viewBalance();
    }

    private void recordExpense(Scanner scanner) {
        // Record expenses with categories
        categoryManager.listCategories();
        expenseManager.recordExpense(scanner);
    }
}

class CategoryManager {
    private Connection conn;

    public CategoryManager(Connection conn) {
        this.conn = conn;
    }

    public void listCategories() {   
//        // List categories
          System.out.println("Categories:");
//        try {
//            Statement stmt = conn.createStatement();
//            ResultSet rs = stmt.executeQuery("SELECT * FROM categories");
//            System.out.println("Categories:");
//            while (rs.next()) {
//                int categoryId = rs.getInt("id");
//                String categoryName = rs.getString("name");
//                System.out.println(categoryId + ". " + categoryName);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }
}

class ExpenseManager {
    private Connection conn;

    public ExpenseManager(Connection conn) {
        this.conn = conn;
    }

    public void viewBalance() {
        // View balances
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT category_id, SUM(amount) AS balance FROM expenses GROUP BY category_id");

            System.out.println("Balances by Category:");
            while (rs.next()) {
                int categoryId = rs.getInt("category_id");
                double balance = rs.getDouble("balance");
                System.out.println("Category ID: " + categoryId + ", Balance: $" + balance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void recordExpense(Scanner scanner) {
        // Record expenses
        System.out.print("Enter the category ID: ");
        int categoryId = scanner.nextInt();
        System.out.print("Enter the amount: $");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter a description (optional): ");
        String description = scanner.nextLine();

        String insertExpenseSQL = "INSERT INTO expenses (category_id, amount, description) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertExpenseSQL)) {
            pstmt.setInt(1, categoryId);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, description);
            pstmt.executeUpdate();
            System.out.println("$" + amount + " recorded under category ID " + categoryId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
