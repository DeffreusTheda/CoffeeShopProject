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

import javafx.application.Application;
import javafx.beans.NamedArg;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Getter;
import java.sql.*;
import java.util.Arrays;

public class CoffeeShop extends Application {
  private ShopCtrl shopCtrl;
  private LoginCtrl loginCtrl;

  // Safely Editable Start Here
  private final String[][] foods = {
//    {"Item Name", "Price in Rp", "Path to Image File"},
      {"Toasted Bread", "5000", "images/foods/toasted_bread.jpg"},
      {"Fried Rice", "10000", "images/foods/fried_rice.jpeg"},
      {"Pasta", "12000", "images/foods/pasta.jpeg"},
      {"Indomie", "6000", "images/foods/indomie.jpeg"},
      {"Sandwich", "4000", "images/foods/sandwich.jpeg"},
      {"French Fries", "7000", "images/foods/french_fries.gif"},
      {"Cookies", "5000", "images/foods/cookies.jpg"}
  };
  private final String[][] drinks = {
//    {"Item Name", "Price in Rp", "Path to Image File"},
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

  @Getter private String[] identity = {"", ""};
  private Stage primaryStage;
  private Scene loginScene, shopScene;
  protected void showAlert(@NamedArg("title") String title, @NamedArg("header") String header, @NamedArg("content") String content) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setResizable(false);

    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);

    Image iLogo = new Image(getClass().getResourceAsStream("images/logo.png"));
    ImageView ivLogo = new ImageView(iLogo);
    ivLogo.setFitHeight(50.0f);
    ivLogo.setFitWidth(50.0f);
    alert.setGraphic(ivLogo);

    alert.showAndWait();
  }
  protected Connection connectToDB() {
    Connection con;
    String dbName = "VxCoffeeShop", dbUser = "root", dbPass = "",
        url = "jdbc:mysql://localhost:3306/" + dbName;
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      con = DriverManager.getConnection(url, dbUser, dbPass);
      return con;
    } catch (Exception e) {
      System.out.println("Connecting to Database Error");
    }
    return null;
  }
  protected void logoutUser() {
    this.identity[0] = "";
    this.identity[1] = "";
    this.primaryStage.setScene(this.loginScene);
    this.loginCtrl.setTfUsernameText("");
    this.loginCtrl.setTPfPasswordText("");
    this.loginCtrl.setCbToggleShowPasswordSelected(false);
  }
  protected void loginUser(@NamedArg("name") String name, @NamedArg("pass") String pass) {
    this.identity[0] = name;
    this.identity[1] = pass;
    this.setPrimaryStageScene(this.shopScene);
  }
  protected void registerUser(@NamedArg("name") String name, @NamedArg("pass") String pass, @NamedArg("connection") Connection con) throws SQLException, InterruptedException {
    String loginQuery = "SELECT * FROM users WHERE username = ?;";
    PreparedStatement psLogin = con.prepareStatement(loginQuery);
    psLogin.setString(1, name);
    ResultSet rsLogin = psLogin.executeQuery();
    if (rsLogin.next()) {
      this.loginCtrl.setLWarningText("Username already exist");
      return;
    }
    String connectQuery = String.format("INSERT INTO `users` " +
        "(`username`, `password`, `join_date`) VALUES ('%s', '%s', current_timestamp());",
        name, pass);
    int rowsAffected = 0;
    try {
       Statement statement = con.createStatement();
       rowsAffected = statement.executeUpdate(connectQuery);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (rowsAffected > 0) {
      this.setPrimaryStageScene(this.shopScene);
      return;
    }
    this.loginCtrl.setLWarningText("Connection Error");
  }
  private void setPrimaryStageScene(@NamedArg("scene") Scene scene) {
    this.primaryStage.setScene(scene);
  }
  @Override
  public void start(Stage stage) throws Exception {
    // Primary Stage Scene
    this.primaryStage = stage;
    this.primaryStage.setTitle("Volistic Coffee Shop");
    Image image = new Image(getClass().getResourceAsStream("images/logo.png"));
    this.primaryStage.getIcons().add(image);
    this.primaryStage.setResizable(false);

    // Login Stage Scene
    FXMLLoader fxmlLoaderLoginStage = new FXMLLoader();
    fxmlLoaderLoginStage.setLocation(CoffeeShop.class.getResource("login.fxml"));
    AnchorPane anchorPane = (AnchorPane) fxmlLoaderLoginStage.load();
    this.loginScene = new Scene(anchorPane);

    // Shop Stage Scene
    FXMLLoader fxmlLoaderShopStage = new FXMLLoader();
    fxmlLoaderShopStage.setLocation(CoffeeShop.class.getResource("shop.fxml"));
    anchorPane = (AnchorPane) fxmlLoaderShopStage.load();
    this.shopScene = new Scene(anchorPane);

    // Login Controller
    this.loginCtrl = fxmlLoaderLoginStage.getController();
    this.loginCtrl.setCoffeeShop(this);
    Image iLogo = new Image(getClass().getResourceAsStream("images/logo.png"));
    this.loginCtrl.setIvLogo(new ImageView(iLogo));

    // Shop Controller
    this.shopCtrl = fxmlLoaderShopStage.getController();
    this.shopCtrl.setCoffeeShop(this);
    if (!this.shopCtrl.populateItems()) {
      System.out.println("Populate Item Error");
      Exception e = new Exception();
      throw e;
    }

    // Display
    this.primaryStage.setScene(this.loginScene);
    this.primaryStage.show();
  }
  protected String toTitleCase(@NamedArg("lowercase") String lowercase) {
    char[] tmp = lowercase.toCharArray();
    boolean first = true; // determine if next char is first letter of a word
    for (int i = 0; i < lowercase.length(); ++i) {
      if (!first && (tmp[i] == '_' || tmp[i] == ' ')) {
        tmp[i] = ' ';
        first = true; // first letter after whitespace
        continue;
      }
      if (first) { // first letter in a word
        first = false; // for next char
      }
      if (tmp[i] >= 97 && tmp[i] < 128) { // if lowercase
        tmp[i] -= 32; // set to uppercase
      }
    }
    return Arrays.toString(tmp);
  }
  public static void main(String[] args) {
    launch(args);
  }
}
