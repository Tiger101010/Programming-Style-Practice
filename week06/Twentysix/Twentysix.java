import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class Twentysix {

    public static Connection createNewDatabase(String fileName) {

        String url = "jdbc:sqlite:./" + fileName;

        try {
            Connection conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
            return conn;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static void creatSchema(Connection conn){
        try{
            if(conn != null) {
                Statement stmt = conn.createStatement();
                String[] sql = new String[]{
                            "CREATE TABLE documents (id INTEGER PRIMARY KEY AUTOINCREMENT, name);",
                            "CREATE TABLE words (id, doc_id, value);",
                            "CREATE TABLE characters (id, word_id, value);"};
                for (String s : sql) {
                    stmt.execute(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadFileToDB(Connection conn, String filePath) {
        List<String> stop_words;
        List<String> words = null;
        try {
            stop_words = asList(new String(Files.readAllBytes(Paths.get("../../stop_words.txt"))).split(","));
            words = asList(new String(Files.readAllBytes(Paths.get(filePath)))
                    .split("[^a-zA-Z]+"))
                    .stream()
                    .filter(s -> !(stop_words.contains(s.toLowerCase()) || (s.length() < 2))).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(conn != null) {
            try {
                String sql1 = "INSERT INTO documents (name) VALUES (?)";
                PreparedStatement stmt = conn.prepareStatement(sql1);
                stmt.setString(1, filePath);
                stmt.executeUpdate();

                String sql2 = "SELECT id from documents WHERE name=? ;";
                PreparedStatement stmt2 = conn.prepareStatement(sql2);
                stmt2.setString(1, filePath);
                ResultSet rs = stmt2.executeQuery();
                int docId = rs.getInt("id");

                Statement stmt3 = conn.createStatement();
                String sql3 = "SELECT MAX(id) FROM words";
                rs = stmt3.executeQuery(sql3);

                int wordId = 0;
                try {
                    wordId = rs.getInt(1);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO words VALUES (?, ?, ?)");
                PreparedStatement pstmt2 = conn.prepareStatement("INSERT INTO characters VALUES (?, ?, ?)");
                if(words != null) {
                    for (String w: words){
                        try {
                            w = w.toLowerCase();
                            pstmt.setInt(1, wordId);
                            pstmt.setInt(2, docId);
                            pstmt.setString(3, w);
                            pstmt.executeUpdate();
                            for(int i = 0; i < w.length(); i++) {
                                pstmt2.setInt(1, i);;
                                pstmt2.setInt(2, wordId);
                                pstmt2.setString(3, String.valueOf(w.charAt(i)));
                                pstmt2.executeUpdate();
                            }
                            wordId++;
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }


    public static void main(String[] args) {
        Connection conn = null;
        if(!Files.exists(Paths.get("./tf.db"))) {
            conn = createNewDatabase("tf.db");
            creatSchema(conn);
            loadFileToDB(conn, args[0]);
        } else {
            try {
                conn = DriverManager.getConnection("jdbc:sqlite:./tf.db");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // query
        String sql = "SELECT value, COUNT(*) as C FROM words GROUP BY value ORDER BY C DESC";
        String sql2 = "SELECT COUNT(*) as C FROM (SELECT DISTINCT(value) as D FROM words WHERE value LIKE '%z%');";
        if(conn != null) {
            try {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                int i = 0;

                System.out.println("----------------");
                System.out.println("Top 25 Words");
                System.out.println("----------------");

                while(rs.next()) {
                    if(i > 24) break;
                    System.out.println(rs.getString(1) + " - " + rs.getInt(2));
                    i++;
                }
                System.out.println("----------------");


                rs = stmt.executeQuery(sql2);
                System.out.println("--------------------------------");
                System.out.println("Number of Unique Words with 'z'");
                System.out.println("--------------------------------");
                System.out.println(rs.getInt(1) + "\n\n\n\n\n");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }
}  