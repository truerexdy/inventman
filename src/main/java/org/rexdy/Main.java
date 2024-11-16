package org.rexdy;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.google.gson.Gson;
import java.sql.*;

public class Main{
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(50505), 0);
        server.createContext("/api/", new restHandler());
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        System.out.println("Server started on port 50505");
    }

    static class restHandler implements HttpHandler {
        private Connection db = null;
        private final String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "/db/inventry.db";
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "";
            int statusCode;
            Gson gson = new Gson();
            try {
                switch (exchange.getRequestMethod()) {
                    case "GET":
                        List<product> firstTenUnits = new ArrayList<>();
                        try{
                            db = DriverManager.getConnection(url);
                            Statement stmt = db.createStatement();
                            String sql = "SELECT * FROM products ORDER BY id DESC LIMIT 10";
                            ResultSet rs = stmt.executeQuery(sql);
                            int i=0;
                            while(rs.next()) {
                                product temp = new product();
                                temp.setId(rs.getInt("id"));
                                temp.setUnitname(rs.getString("unitname"));
                                temp.setUnits(rs.getInt("units"));
                                temp.setUnitprice(rs.getDouble("unitprice"));
                                firstTenUnits.add(temp);
                                i++;
                                if(i>10){
                                    break;
                                }
                            }
                            stmt.close();
                            db.close();
                        }
                        catch(Exception e){
                            statusCode = 500;
                            break;
                        }
                        response = gson.toJson(firstTenUnits);
                        statusCode = 200;
                        break;

                    case "POST":
                        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                        StringBuilder body = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            body.append(line);
                        }
                        product newproduct = gson.fromJson(body.toString(), product.class);
                        db = DriverManager.getConnection(url);
                        String sql = "INSERT INTO products (unitname, units, unitprice) VALUES (?, ?, ?)";
                        PreparedStatement pstmt = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                        pstmt.setString(1,newproduct.getUnitname());
                        pstmt.setInt(2, newproduct.getUnits());
                        pstmt.setDouble(3,newproduct.getUnitprice());
                        int rowsAffected = pstmt.executeUpdate();
                        if (rowsAffected == 0) {
                            response = "Product not found";
                            statusCode = 404;
                        } else {
                            response = gson.toJson("\"message\":\"OK\"");
                            statusCode = 200;
                        }
                        pstmt.close();
                        db.close();
                        response = gson.toJson(newproduct);
                        statusCode = 201;
                        break;
                    case "PUT":
                        reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                        body = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            body.append(line);
                        }
                        product updatedProduct = gson.fromJson(body.toString(), product.class);
                        db = DriverManager.getConnection(url);
                        sql = "UPDATE products SET unitname = ?, units = ?, unitprice = ? WHERE id = ?";
                        pstmt = db.prepareStatement(sql);
                        pstmt.setString(1, updatedProduct.getUnitname());
                        pstmt.setInt(2, updatedProduct.getUnits());
                        pstmt.setDouble(3, updatedProduct.getUnitprice());
                        pstmt.setInt(4, updatedProduct.getId());
                        rowsAffected = pstmt.executeUpdate();
                        if (rowsAffected == 0) {
                            response = "Product not found";
                            statusCode = 404;
                        } else {
                            updatedProduct.setId(updatedProduct.getId());
                            response = gson.toJson(updatedProduct);
                            statusCode = 200;
                        }
                        pstmt.close();
                        db.close();
                        break;
                    case "DELETE":
                        reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                        body = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            body.append(line);
                        }
                        product deleteProduct = gson.fromJson(body.toString(), product.class);
                        db = DriverManager.getConnection(url);
                        sql = "DELETE FROM products WHERE id = ?";
                        pstmt = db.prepareStatement(sql);
                        pstmt.setInt(1, deleteProduct.getId());
                        rowsAffected = pstmt.executeUpdate();
                        if (rowsAffected == 0) {
                            response = "Product not found";
                            statusCode = 404;
                        } else {
                            response = "{\"message\": \"Product deleted successfully\"}";
                            statusCode = 200;
                        }
                        pstmt.close();
                        db.close();
                        break;

                    default:
                        response = "Method not supported";
                        statusCode = 405;
                }
            } catch (Exception e) {
                response = "Internal server error: " + e.getMessage();
                statusCode = 500;
            }
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}