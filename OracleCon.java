import java.io.FileReader;
import java.io.StringReader; // Add this import statement
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.sql.Clob;

import com.opencsv.CSVReader;

public class OracleCon {
    private static final String DB_URL = "jdbc:oracle:thin:@az6F72ldbp1.az.uta.edu:1523/pcse1p.data.uta.edu";
    private static final String DB_USER = "dxn0498";
    private static final String DB_PASSWORD = "Dedeepya0081";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd"); // Change date format if needed

    public static void main(String[] args) {
        OracleCon obj = new OracleCon();
        Connection con = obj.getConnection();
        obj.insertMembersData();  // Insert data into Members table
        obj.insertLibrarianData();
        obj.insertBooksData();
        obj.insertProfessorsData();
        obj.insertOtherMembersData();
        obj.insertInquiriesData();
        obj.insertCatalogData();
        obj.insertManagesData();
        obj.insertVolumesData();
        obj.loadLoanData();

    }

    private Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void insertMembersData() {
    String tableName = "Members";
    try (Connection con = getConnection();
         PreparedStatement pstmt = con.prepareStatement(
                 "INSERT INTO " + tableName + " (Member_ID, Campus_Address, SSN, Home_Address, Phone_num, Books_Borrowed) VALUES (?, ?, ?, ?, ?, ?)"
         );
         FileReader fileReader = new FileReader("Members.csv");
         CSVReader reader = new CSVReader(fileReader, ',', '\'')) {
        System.out.println("Connected to the database ");
        String[] line;
        int lineNumber = 1;  // Add this line
        while ((line = reader.readNext()) != null) {
            try {
                int memberID = Integer.parseInt(line[0].trim());
                String campusAddress = line[1];
                String ssn = line[2].replace("-", "");
                String homeAddress = line[3];
                String phoneNum = line[4];
                int booksBorrowed = Integer.parseInt(line[5].trim());
            
                pstmt.setInt(1, memberID);
                pstmt.setString(2, campusAddress);
                pstmt.setString(3, ssn);
                pstmt.setString(4, homeAddress);
                pstmt.setString(5, phoneNum);
                pstmt.setInt(6, booksBorrowed);
                pstmt.executeUpdate();
            } catch (NumberFormatException e) {
                System.err.println("Invalid value in the Books_Borrowed column at line " + lineNumber + ": " + line[5]);
            }
            lineNumber++;  // Add this line
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
private void insertLibrarianData() {
        String tableName = "Librarian";
        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(
                     "INSERT INTO " + tableName + " (ID, Name, Role) VALUES (?, ?, ?)"
             );
             FileReader fileReader = new FileReader("Staff.csv");
             CSVReader reader = new CSVReader(fileReader, ',')) {
            System.out.println("Connected to the database ");
            String[] line;
            int lineNumber = 1;
            while ((line = reader.readNext()) != null) {
                try {
                    int librarianID = Integer.parseInt(line[0].trim());
                    String librarianName = line[1];
                    String librarianRole = line[2];

                    pstmt.setInt(1, librarianID);
                    pstmt.setString(2, librarianName);
                    pstmt.setString(3, librarianRole);
                    pstmt.executeUpdate();
                } catch (NumberFormatException e) {
                    System.err.println("Invalid value in the ID column at line " + lineNumber + ": " + line[0]);
                }
                lineNumber++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void insertBooksData() {
        String tableName = "Books";
        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(
                     "INSERT INTO " + tableName + " (ISBN, Title, Author, Subject_Area, No_of_Books, IsAvailable) VALUES (?, ?, ?, ?, ?, ?)"
             );
             FileReader fileReader = new FileReader("Title.csv");
             CSVReader reader = new CSVReader(fileReader, ',')) {
            System.out.println("Connected to the database ");
            String[] line;
            int lineNumber = 1;
            while ((line = reader.readNext()) != null) {
                try {
                    String isbn = line[0];
                    String title = line[1];
                    String author = line[2];
                    String subjectArea = line[3];
                    int noOfBooks = Integer.parseInt(line[4].trim());
                    String isAvailable = line[5];

                    pstmt.setString(1, isbn);
                    pstmt.setString(2, title);
                    pstmt.setString(3, author);
                    pstmt.setString(4, subjectArea);
                    pstmt.setInt(5, noOfBooks);
                    pstmt.setString(6, isAvailable.substring(0, 1));
                    pstmt.executeUpdate();
                } catch (NumberFormatException e) {
                    System.err.println("Invalid value in the No_of_Books column at line " + lineNumber + ": " + line[4]);
                }
                lineNumber++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void insertProfessorsData() {
    String tableName = "Professor";
    try (Connection con = getConnection();
         PreparedStatement pstmt = con.prepareStatement(
                 "INSERT INTO " + tableName + " (Member_ID, Issued_ID, Expired_ID) VALUES (?, ?, ?)"
         );
         FileReader fileReader = new FileReader("Professors.csv");
         CSVReader reader = new CSVReader(fileReader, ',')) {
        System.out.println("Connected to the database ");
        String[] line;
        int lineNumber = 1;
        while ((line = reader.readNext()) != null) {
            try {
                int memberID = Integer.parseInt(line[0].trim());

                // Parse dates using SimpleDateFormat
                Date issuedDate = new java.sql.Date(dateFormat.parse(line[1]).getTime());
                Date expiredDate = new java.sql.Date(dateFormat.parse(line[2]).getTime());

                pstmt.setInt(1, memberID);
                pstmt.setDate(2, issuedDate);
                pstmt.setDate(3, expiredDate);
                pstmt.executeUpdate();
            } catch (NumberFormatException e) {
                System.err.println("Invalid value in the Member_ID column at line " + lineNumber + ": " + line[0]);
            }
            lineNumber++;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
private void insertOtherMembersData() {
        String tableName = "Other_Members";
        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(
                     "INSERT INTO " + tableName + " (Member_ID, Card_expired, Card_issued) VALUES (?, ?, ?)"
             );
             FileReader fileReader = new FileReader("Other_Members.csv");
             CSVReader reader = new CSVReader(fileReader, ',')) {
            System.out.println("Connected to the database ");
            String[] line;
            int lineNumber = 1;
            while ((line = reader.readNext()) != null) {
                try {
                    int memberID = Integer.parseInt(line[0].trim());
                    String cardExpired = line[1].substring(0, 1);  // Assuming "Y" or "N" in CSV
                    Date cardIssued = new java.sql.Date(dateFormat.parse(line[2]).getTime());

                    pstmt.setInt(1, memberID);
                    pstmt.setString(2, cardExpired);
                    pstmt.setDate(3, cardIssued);
                    pstmt.executeUpdate();
                } catch (NumberFormatException e) {
                    System.err.println("Invalid value in the Member_ID column at line " + lineNumber + ": " + line[0]);
                }
                lineNumber++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void insertInquiriesData() {
        String tableName = "Inquiries";
        try (Connection con = getConnection();
                PreparedStatement pstmt = con.prepareStatement(
                        "INSERT INTO " + tableName + " (Member_ID, Librarian_ID) VALUES (?, ?)"
                );
                FileReader fileReader = new FileReader("Inquiries.csv");
                CSVReader reader = new CSVReader(fileReader, ',')) {
            System.out.println("Connected to the database ");
            String[] line;
            int lineNumber = 1;
            while ((line = reader.readNext()) != null) {
                try {
                    int memberID = Integer.parseInt(line[0].trim());
                    int librarianID = Integer.parseInt(line[1].trim());

                    pstmt.setInt(1, memberID);
                    pstmt.setInt(2, librarianID);
                    pstmt.executeUpdate();
                } catch (NumberFormatException e) {
                    System.err.println("Invalid value in the Member_ID or Librarian_ID column at line " + lineNumber
                            + ": " + line[0] + ", " + line[1]);
                }
                lineNumber++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void insertCatalogData() {
    String tableName = "Catalog";
    try (Connection con = getConnection();
         PreparedStatement pstmt = con.prepareStatement(
                 "INSERT INTO " + tableName + " (Catalog_ID, ISBN, Description) VALUES (?, ?, ?)"
         );
         FileReader fileReader = new FileReader("Catalog.csv");
         CSVReader reader = new CSVReader(fileReader, ',')) {
        System.out.println("Connected to the database ");
        String[] line;
        int lineNumber = 1;
        while ((line = reader.readNext()) != null) {
            try {
                int catalogID = Integer.parseInt(line[0].trim());
                String isbn = line[1];
                String description = line[2];

                pstmt.setInt(1, catalogID);
                pstmt.setString(2, isbn);
                pstmt.setCharacterStream(3, new StringReader(description), description.length());
                pstmt.executeUpdate();
            } catch (NumberFormatException e) {
                System.err.println("Invalid value in the Catalog_ID column at line " + lineNumber + ": " + line[0]);
            }
            lineNumber++;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
private void insertManagesData() {
        String tableName = "Manages";
        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(
                     "INSERT INTO " + tableName + " (Librarian_ID, ISBN) VALUES (?, ?)"
             );
             FileReader fileReader = new FileReader("Manages.csv");
             CSVReader reader = new CSVReader(fileReader, ',')) {
            System.out.println("Connected to the database ");
            String[] line;
            int lineNumber = 1;
            while ((line = reader.readNext()) != null) {
                try {
                    int librarianID = Integer.parseInt(line[0].trim());
                    String isbn = line[1];

                    pstmt.setInt(1, librarianID);
                    pstmt.setString(2, isbn);
                    pstmt.executeUpdate();
                } catch (NumberFormatException e) {
                    System.err.println("Invalid value in the Librarian_ID or ISBN column at line " + lineNumber
                            + ": " + line[0] + ", " + line[1]);
                }
                lineNumber++;
            }
        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }
    private void insertVolumesData() {
        String tableName = "Volumes";
        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(
                     "INSERT INTO " + tableName + " (Volume_ID, ISBN, Status, Location) VALUES (?, ?, ?, ?)"
             );
             FileReader fileReader = new FileReader("Volumes.csv");
             CSVReader reader = new CSVReader(fileReader, ',')) {
            System.out.println("Connected to the database ");
            String[] line;
            int lineNumber = 1;
            while ((line = reader.readNext()) != null) {
                try {
                    int volumeID = Integer.parseInt(line[0].trim());
                    String isbn = line[1];
                    String status = line[2];
                    String location = line[3];

                    pstmt.setInt(1, volumeID);
                    pstmt.setString(2, isbn);
                    pstmt.setString(3, status);
                    pstmt.setString(4, location);
                    pstmt.executeUpdate();
                } catch (NumberFormatException e) {
                    System.err.println("Invalid value in the Volume_ID column at line " + lineNumber + ": " + line[0]);
                }
                lineNumber++;
            }
        } catch (Exception e) {
            // Handle other exceptions
            e.printStackTrace();
        }
    }
    private void loadLoanData() {
        String tableName = "Loan";
        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(
                     "INSERT INTO " + tableName + " (Loan_ID, Member_ID, Volume_ID, DueDate, ReturnDate, IssueDate) VALUES (?, ?, ?, ?, ?, ?)"
             );
             FileReader fileReader = new FileReader("Loan.csv");
             CSVReader reader = new CSVReader(fileReader, ',')) {

            System.out.println("Connected to the database ");
            String[] line;
            int lineNumber = 1;

            while ((line = reader.readNext()) != null) {
                try {
                    int loanID = Integer.parseInt(line[0].trim());
                    int memberID = Integer.parseInt(line[1].trim());
                    int volumeID = Integer.parseInt(line[2].trim());

                    // Parse dates if available
                    Date dueDate = new java.sql.Date(dateFormat.parse(line[3]).getTime());
                    Date returnDate = new java.sql.Date(dateFormat.parse(line[4]).getTime());
                    Date issueDate = new java.sql.Date(dateFormat.parse(line[5]).getTime());

                    pstmt.setInt(1, loanID);
                    pstmt.setInt(2, memberID);
                    pstmt.setInt(3, volumeID);
                    pstmt.setDate(4, dueDate);
                    pstmt.setDate(5, returnDate);
                    pstmt.setDate(6, issueDate);

                    pstmt.executeUpdate();
                } catch (NumberFormatException e) {
                    System.err.println("Invalid value in the CSV at line " + lineNumber + ": " + String.join(",", line));
                }
                lineNumber++;
            }
            System.out.println("Data loaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
