/********************************************************************************
 * Copyright (c) 2024 Deffreus Theda
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/

package comsci.cs_coffeeshop;

import com.almasb.fxgl.trade.Shop;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class CoffeeShop extends Application {
    private ShopCtrl shopCtrl;
    private LoginCtrl loginCtrl;
    public CoffeeShop() {
        this.shopCtrl = new ShopCtrl();
        this.loginCtrl = new LoginCtrl();
        this.loginCtrl.setCoffeeShop(this);
    }
    // Safely Editable Start Here
    private final String[][] foods = {
//            {"Item Name", "Price in Rp", "Path to Image File"},
            {"Toasted Bread", "5000", "images/foods/toasted_bread.jpg"},
            {"Fried Rice", "10000", "images/foods/fried_rice.jpeg"},
            {"Pasta", "12000", "images/foods/pasta.jpeg"},
            {"Indomie", "6000", "images/foods/indomie.jpeg"},
            {"Sandwich", "4000", "images/foods/sandwich.jpeg"},
            {"French Fries", "7000", "images/foods/french_fries.gif"},
            {"Cookies", "5000", "images/foods/cookies.jpg"}
    };
    private final String[][] drinks = {
//            {"Item Name", "Price in Rp", "Path to Image File"},
            {"Americano", "7500", "images/drinks/americano.jpeg"},
            {"Black Coffee", "5500", "images/drinks/black_coffee.jpeg"},
            {"Cappucino", "6000", "images/drinks/cappucino.jpg"},
            {"Latte", "5500", "images/drinks/latte.jpg"}
    };
    private final String[][][] categories = {foods, drinks};
    protected String[][][] getCategories() {
        return this.categories;
    }
    // Safely Editable End Here
    protected String[] identity = {"", ""};
    private Stage primaryStage;
    private Scene loginStage, shopScene;
    protected Connection connectToDB() {
        Connection con;
        String dbName = "VxCoffeeShop", dbUser = "root", dbPass = "", url = "jdbc:mysql://localhost/" + dbName;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, dbUser, dbPass);
            return con;
        } catch (Exception e) {
            System.out.println("connectToDB : Connection Error");
        }
        return null;
    }
    protected void logoutUser() {
        this.identity[0] = "";
        this.identity[1] = "";
        this.primaryStage.setScene(this.loginStage);
    }
    protected void loginUser(String name, String pass) {
        this.identity[0] = name;
        this.identity[1] = pass;
        this.primaryStage.setScene(this.shopScene);
    }
    protected void registerUser(String name, String pass, Connection con) {
        String connectQuery = String.format("INSERT INTO `users` (`username`, `password`, `join_date`) VALUES ('%s', '%s', current_timestamp());", name, pass);
        int rowsAffected = 0;
        try {
           Statement statement = con.createStatement();
           rowsAffected = statement.executeUpdate(connectQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (rowsAffected > 0) {
            this.shopCtrl.setLWelcomeText("Welcome, " + name + "!");
            this.setPrimaryStageScene(this.shopScene);
            return;
        }
        this.loginCtrl.setLWarningText("Connection Error");
    }
    private void setPrimaryStageScene(Scene scene) {
        this.primaryStage.setScene(scene);
    }
    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        this.loginStage = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml"))));
        this.shopScene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("shop.fxml"))));
        this.primaryStage.setTitle("Volistic Coffee Shop");
        this.primaryStage.setScene(this.loginStage);
        Image iLogo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/logo.png")));
        this.loginCtrl.setIvLogo(new ImageView(iLogo));
        this.primaryStage.setResizable(false);
        this.primaryStage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}