package org.rexdy;

import java.sql.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class ProductController {
    private Connection db = null;
    private final String url = "jdbc:sqlite:db/inventry.db";
    @GetMapping
    public List<json> getAllProducts() {
        List<json> firstTenUnits = new java.util.ArrayList<>();
        try {
            db = DriverManager.getConnection(url);
            Statement stmt = db.createStatement();
            String sql = "SELECT * FROM products ORDER BY id DESC LIMIT 10";
            ResultSet rs = stmt.executeQuery(sql);
            for(int i=0; i<10;i++) {
                json temp = new json();
                temp.id = rs.getInt("id");
                temp.unitname = rs.getString("unitname");
                temp.units = rs.getInt("units");
                temp.unitprice = rs.getDouble("unitprice");
                firstTenUnits.add(temp);
                stmt.close();
                db.close();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return firstTenUnits;
    }


    @PostMapping
    public json createProduct(@RequestBody json product) {
        try {
            db = DriverManager.getConnection(url);
            String sql = "INSERT INTO products (unitname, units, unitprice) VALUES (?, ?, ?)";
            PreparedStatement pstmt = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1,product.unitname);
            pstmt.setInt(2, product.units);
            pstmt.setDouble(3,product.unitprice);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows Affected " + rowsAffected);
            pstmt.close();
            db.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return product;
    }


    @PutMapping("/{id}")
    public ResponseEntity<json> updateProduct(@RequestBody json product) {
        try {
            db = DriverManager.getConnection(url);
            String sql = "UPDATE products SET unitname = ?, units = ?, unitprice = ? WHERE id = ?";
            PreparedStatement pstmt = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1,product.unitname);
            pstmt.setInt(2, product.units);
            pstmt.setDouble(3,product.unitprice);
            pstmt.setInt(3,product.id);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows Affected " + rowsAffected);
            pstmt.close();
            db.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
        return ResponseEntity.status(200).body(null);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<json> deleteProduct(@PathVariable int id) {
        try {
            db = DriverManager.getConnection(url);
            String sql = "DELETE FROM products WHERE id = ?";
            PreparedStatement pstmt = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1,id);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows Affected " + rowsAffected);
            pstmt.close();
            db.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
        return ResponseEntity.status(200).body(null);
    }
}