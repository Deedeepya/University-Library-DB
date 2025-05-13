import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.time.LocalDate;



public class LibraryTransactions {

    private static final String DB_URL = "jdbc:oracle:thin:@az6F72ldbp1.az.uta.edu:1523/pcse1p.data.uta.edu";
    private static final String DB_USER = "dxn0498";
    private static final String DB_PASSWORD = "Dedeepya0081";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd"); 

    public static void main(String[] args) {
        LibraryTransactions libraryTransactions = new LibraryTransactions();
        libraryTransactions.runTransactions();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private void runTransactions() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Library Transactions Menu:");
            System.out.println("1. Add New Member");
            System.out.println("2. Add New Book");
            System.out.println("3. Borrow Book");
            System.out.println("4. Return Book");
            System.out.println("5. Renew Membership");
            System.out.println("0. Exit");

            System.out.println("Enter your choice:");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addNewMember();
                    break;
                case 2:
                    addNewBook();
                    break;
                case 3:
                    borrowBook();
                    break;
                case 4:
                    returnBook();
                    break;
                case 5:
                    renewMembership();
                    break;
                case 0:
                    System.out.println("Exiting Library Transactions. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void addNewMember() {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO Members (Member_ID, Campus_Address, SSN, Home_Address, Phone_num, Books_Borrowed) VALUES (?, ?, ?, ?, ?, 0)"
             )) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter Member ID:");
            int memberID = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.println("Enter Member Campus Address:");
            String campusAddress = scanner.nextLine();

            System.out.println("Enter Member SSN:");
            String ssn = scanner.nextLine();

            System.out.println("Enter Member Home Address:");
            String homeAddress = scanner.nextLine();

            System.out.println("Enter Member Phone Number:");
            String phoneNumber = scanner.nextLine();

            preparedStatement.setInt(1, memberID);
            preparedStatement.setString(2, campusAddress);
            preparedStatement.setString(3, ssn);
            preparedStatement.setString(4, homeAddress);
            preparedStatement.setString(5, phoneNumber);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("New member added successfully.");
            } else {
                System.out.println("Failed to add a new member.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addNewBook() {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO Books (ISBN, Title, Author, Subject_Area, No_of_Books, IsAvailable) " +
                             "VALUES (?, ?, ?, ?, 1, 'Y')"
             );
             PreparedStatement updateVolumesStatement = connection.prepareStatement(
                     "UPDATE Volumes SET Status = 'Available', Location = 'Library' " +
                             "WHERE ISBN = ? AND Volume_ID = (SELECT MAX(Volume_ID) FROM Volumes WHERE ISBN = ?)"
             )) {

            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter Book ISBN:");
            String isbn = scanner.nextLine();

            System.out.println("Enter Book Title:");
            String title = scanner.nextLine();

            System.out.println("Enter Book Author:");
            String author = scanner.nextLine();

            System.out.println("Enter Book Subject Area:");
            String subjectArea = scanner.nextLine();

            preparedStatement.setString(1, isbn);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, author);
            preparedStatement.setString(4, subjectArea);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("New book added successfully.");

                // Increment the count and update Volumes table
                updateVolumesStatement.setString(1, isbn);
                updateVolumesStatement.setString(2, isbn); // Added this line to set the parameter for the subquery
                updateVolumesStatement.executeUpdate();

                System.out.println("Volumes table updated.");
            } else {
                System.out.println("Failed to add a new book.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


   private void borrowBook() {
    try (Connection connection = getConnection();
         PreparedStatement borrowStatement = connection.prepareStatement(
                 "INSERT INTO Loan (Loan_ID, Member_ID, Volume_ID, DueDate, ReturnDate, IssueDate) " +
                         "VALUES (?, ?, ?, ?, NULL, SYSDATE)"
         );
         PreparedStatement volumeStatement = connection.prepareStatement(
                 "SELECT Volumes.Volume_ID " +
                         "FROM Volumes " +
                         "JOIN Books ON Volumes.ISBN = Books.ISBN " +
                         "WHERE Volumes.ISBN = ? AND Books.IsAvailable = 'Y' " 
         )) {

        // User Input
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Member ID:");
        int memberID = scanner.nextInt();

        System.out.println("Enter Book ISBN:");
        String isbn = scanner.next();

        // Check Book Availability
        volumeStatement.setString(1, isbn);
        ResultSet resultSet = volumeStatement.executeQuery();

        if (resultSet.next()) {
            // Book is available, proceed with the borrow transaction
            int volumeID = resultSet.getInt("Volume_ID");

            // Calculate Due Date (Issue Date + 21 days)
            java.sql.Date dueDate = java.sql.Date.valueOf(java.time.LocalDate.now().plusDays(21));

            // Insert Borrow Transaction
            int loanID = getNewLoanID(connection);

            borrowStatement.setInt(1, loanID);
            borrowStatement.setInt(2, memberID);
            borrowStatement.setInt(3, volumeID);
            borrowStatement.setDate(4, dueDate);
            borrowStatement.setDate(5, null); // ReturnDate initially set to NULL
            borrowStatement.setDate(6, java.sql.Date.valueOf(java.time.LocalDate.now())); // IssueDate

            int rowsAffected = borrowStatement.executeUpdate();

            // Transaction Result
            if (rowsAffected > 0) {
                System.out.println("Borrow transaction successful.");
                System.out.println("Due Date: " + dueDate);
            } else {
                System.out.println("Failed to complete the borrow transaction.");
            }
        } else {
            // Book is not available
            System.out.println("The requested book is not available for borrowing.");
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}



    private boolean isBookAvailable(Connection connection, String isbn) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT 1 " +
                        "FROM Volumes " +
                        "JOIN Books ON Volumes.ISBN = Books.ISBN " +
                        "WHERE Volumes.ISBN = ? AND Books.IsAvailable = 'Y' " +
                        "AND Volumes.Status = 'Available' " +
                        "AND NOT EXISTS (SELECT 1 FROM Loan WHERE Volume_ID = Volumes.Volume_ID AND ReturnDate IS NULL)"
        )) {
            statement.setString(1, isbn);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    private int getVolumeID(Connection connection, String isbn) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT Volumes.Volume_ID " +
                        "FROM Volumes " +
                        "JOIN Books ON Volumes.ISBN = Books.ISBN " +
                        "WHERE Volumes.ISBN = ? AND Books.IsAvailable = 'Y' " +
                        "AND Volumes.Status = 'Available' " +
                        "AND NOT EXISTS (SELECT 1 FROM Loan WHERE Volume_ID = Volumes.Volume_ID AND ReturnDate IS NULL)"
        )) {
            statement.setString(1, isbn);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("Volume_ID");
            } else {
                throw new SQLException("Volume ID not found for ISBN: " + isbn);
            }
        }
    }

    public void returnBook() {
        try (Connection connection = getConnection()) {
            Scanner scanner = new Scanner(System.in);

            // Get input from the user (e.g., member ID, book ISBN, return date)
            System.out.println("Enter Member ID:");
            int memberID = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.println("Enter Book ISBN:");
            String bookISBN = scanner.nextLine();

            // Check if the book is on loan to the specified member
            if (isBookOnLoan(connection, memberID, bookISBN)) {
                // Update Loans table to mark the book as returned
                updateLoanTable(connection, memberID, bookISBN);

                // Update Volumes table to increase the available quantity
                updateVolumesTable(connection, bookISBN);

                // Print return receipt with book details and return date
                System.out.println("Book returned successfully.");

            } else {
                System.out.println("The specified book is not on loan to the member.");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately, log or show an error message to the user
        }
    }

    private boolean isBookOnLoan(Connection connection, int memberID, String bookISBN) throws SQLException {
    String sql = "SELECT * FROM Loan WHERE Member_ID = ? AND Volume_ID = ? AND ReturnDate IS NULL";

    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
        preparedStatement.setInt(1, memberID);
        preparedStatement.setString(2, bookISBN);

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            return resultSet.next(); // Return true if the book is on loan, false otherwise
        }
    }
}


    private void updateLoanTable(Connection connection, int memberID, String bookISBN) throws SQLException {
        // Implement logic to update the Loans table to mark the book as returned
        String sql = "UPDATE Loan SET Return_Date = CURRENT_DATE WHERE Member_ID = ? AND Book_ISBN = ? AND Return_Date IS NULL";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, memberID);
            preparedStatement.setString(2, bookISBN);

            preparedStatement.executeUpdate();
        }
    }

    private void updateVolumesTable(Connection connection, String bookISBN) throws SQLException {
        // Implement logic to update the Volumes table to increase the available quantity
        String sql = "UPDATE Volumes SET Available_Quantity = Available_Quantity + 1 WHERE ISBN = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, bookISBN);

            preparedStatement.executeUpdate();
        }
    }


private void renewMembership() {
    try (Connection connection = getConnection();
         PreparedStatement updateStatement = connection.prepareStatement(
                 "UPDATE Members SET Books_Borrowed = 0 WHERE Member_ID = ?"
         );
         PreparedStatement selectStatement = connection.prepareStatement(
                 "SELECT * FROM Members WHERE Member_ID = ?"
         )) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Member ID for Membership Renewal:");
        int memberID = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Update membership
        updateStatement.setInt(1, memberID);
        int rowsAffected = updateStatement.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Membership renewed successfully.");

            // Retrieve and print updated member details
            selectStatement.setInt(1, memberID);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Updated Member Details:");
                System.out.println("Member ID: " + resultSet.getInt("Member_ID"));
                System.out.println("Campus Address: " + resultSet.getString("Campus_Address"));
                // Add similar lines for other columns as needed
            } else {
                System.out.println("Failed to retrieve updated member details.");
            }
        } else {
            System.out.println("Failed to renew membership. Check member ID.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    private int getNewLoanID(Connection connection) throws SQLException {
        int loanID = -1;

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT MAX(Loan_ID) + 1 AS NewLoanID FROM Loan"
        )) {
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                loanID = resultSet.getInt("NewLoanID");
            }
        }

        return loanID;
    }
}
