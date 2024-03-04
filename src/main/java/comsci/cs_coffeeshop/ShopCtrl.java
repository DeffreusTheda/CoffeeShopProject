/***************************************************************************************************
 * Copyright (c) 2024 Deffreus Theda
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **************************************************************************************************/

package comsci.cs_coffeeshop;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import lombok.Data;
import lombok.Setter;

public class ShopCtrl {
  @Setter private CoffeeShop coffeeShop;
  private Connection con;
  @Data private class Item {
    private String name;
    private double price, total;
    private int quantity;
    public Item() {
      this.name = "";
      this.price = 0.0f;
      this.total = 0.0f;
      this.quantity = 0;
    }
    public Item(String testname, String testprice, String testQTY, String testtotal) {
      this.name = testname;
      this.price = Double.parseDouble(testprice);
      this.quantity = Integer.parseInt(testQTY);
      this.total = Double.parseDouble(testtotal);
    }
  }

  // CAFE MENU
  @FXML private Label lWelcome;

  /* Editable Start Here */
  // FlowPanes for Item Categories
  // Make sure the FlowPane is injected in shop.fxml
  // Don't forget to add FlowPanes to the 'fps' array in initialize()
  @FXML private FlowPane fpFoods, fpDrinks;
  // Edit the initial capacity accordingly when add/removing category
  private final HashMap<String, FlowPane> hmFlowPanes = new HashMap<>();
  /* Editable End Here */

  // Shopping Cart Table View
  @FXML private TableView<Item> tvCart = new TableView<>();
  @FXML private TableColumn tbItemName, tbItemPrice, tbItemTotal, tbItemQuantity;
  private double sumOfPurchase = 0;
  @FXML private void initialize() {
    // Remove placeholder items
    fpFoods.getChildren().removeAll();

    // Mapping FlowPanes
    hmFlowPanes.put("foods", fpFoods);
    hmFlowPanes.put("drinks", fpDrinks);

    // Shopping Cart Table View
    this.tvCart.setEditable(false);

    // Initializing the TableColumns
    this.tbItemName = new TableColumn<Item, String>("Name");
    this.tbItemPrice = new TableColumn<Item, Double>("Price");
    this.tbItemQuantity = new TableColumn<Item, Integer>("QTY");
    this.tbItemTotal = new TableColumn<Item, Double>("Total");

    // Setting Table Columns Cell Factory
    this.tbItemName.setCellValueFactory(new PropertyValueFactory<Item, String>("name"));
    this.tbItemPrice.setCellValueFactory(new PropertyValueFactory<Item, String>("price"));
    this.tbItemQuantity.setCellValueFactory(new PropertyValueFactory<Item, String>("quantity"));
    this.tbItemTotal.setCellValueFactory(new PropertyValueFactory<Item, String>("total"));
    this.tvCart.setItems(this.tvCart.getItems());
  }
  protected boolean populateItems() throws SQLException {
    // Greet User
    this.lWelcome.setText("Welcome, " + this.coffeeShop.getIdentity()[0]);

    // Connect to Database
    con = this.coffeeShop.connectToDB();
    // Check for connection error
    if (con == null) {
      this.coffeeShop.showAlert(
          Alert.AlertType.ERROR,
          "Error Were Encountered",
          "Failed to connect to product database",
          """
              Please try again later
              Possible causes:
              - Our server is down
              - Internet connection problem"""
      );
      return false;
    }

    // Get Category Count
    PreparedStatement psCategoryCount = con.prepareStatement(
        "SELECT COUNT(DISTINCT category) AS cnt FROM items"
    );
    ResultSet rsCategoryCount = psCategoryCount.executeQuery();
    rsCategoryCount.next();
    short categoryCount = (short) rsCategoryCount.getInt("cnt");
    if (categoryCount == -1) {
      this.coffeeShop.showAlert(
          Alert.AlertType.WARNING,
          "Alert",
          "Unexpected behaviour were encountered",
          "Please immediately contact 'deffreus' on Discord"
      );
      return false;
    }

    // Get Item Rows
    PreparedStatement psGetItems = con.prepareStatement("SELECT * FROM `items`");
    ResultSet rsGetItems = psGetItems.executeQuery();
    while(rsGetItems.next()) { // for each row (item) in database
      // Debug
      System.out.println(rsGetItems.getString("name"));

      // VBox for an item
      VBox vbItem = new VBox();
      vbItem.setPrefWidth(150.0f);
      vbItem.setSpacing(3.0f);

      // Image of the item
      ImageView ivItem = new ImageView(new Image(Objects.requireNonNull(
          getClass().getResourceAsStream(String.format("images/%s/%s.png",
              rsGetItems.getString("category"), rsGetItems.getString("name")))))
      );
      ivItem.setFitHeight(150.0f);
      ivItem.setFitHeight(150.0f);
      ivItem.setPickOnBounds(true);
      ivItem.setPreserveRatio(true);
      vbItem.getChildren().add(ivItem);

      // Name of the item
      Label lName = new Label(this.coffeeShop.toTitleCase(rsGetItems.getString("name")));
      lName.setAlignment(Pos.CENTER);
      lName.setPrefWidth(150.0f);
      lName.setFont(new Font("Serif Bold", 22.0f));
      vbItem.getChildren().add(lName);

      // Price of the item
      String priceString = String.format("Rp.%,.2f", rsGetItems.getDouble("price"));
      Label lPrice = new Label(priceString);
      lPrice.setAlignment(Pos.CENTER);
      lPrice.setPrefWidth(150.0f);
      vbItem.getChildren().add(lPrice);

      // FLowPane for item quantity
      FlowPane fpItem = new FlowPane();
      vbItem.getChildren().add(fpItem);

      // TextField for order quantity
      TextField tfQTY = new TextField();
      tfQTY.setPromptText("0");
      tfQTY.setPrefWidth(95.0f);
      fpItem.getChildren().add(tfQTY);

      // Increment button
      Button bIncrease = new Button("↑");
      bIncrease.setMnemonicParsing(false);
      bIncrease.setOnAction(event -> {
        if (tfQTY.getText().isEmpty())
          tfQTY.setText("0");
        boolean equalToStock = false;
        double dStock = rsGetItems.getDouble("stock");
        if (equalToStock)
          this.coffeeShop.showAlert(
              Alert.AlertType.INFORMATION,
              "Information",
              "",
              "Your order exceed our stock."
          );
        tfQTY.setText(String.valueOf(Integer.parseInt(tfQTY.getText()) + 1));
      });
      fpItem.getChildren().add(bIncrease);

      // Increment button
      Button bDecrease = new Button("↓");
      bDecrease.setMnemonicParsing(false);
      bDecrease.setOnAction(event -> {
        if (tfQTY.getText().isEmpty())
          return;
        tfQTY.setText(String.valueOf(Integer.parseInt(tfQTY.getText()) - 1));
        if (tfQTY.getText().equals("0"))
          tfQTY.setText("");
      });
      fpItem.getChildren().add(bDecrease);

      // Add item according to category
      hmFlowPanes.get(rsGetItems.getString("category")).getChildren().add(vbItem);
    }
    return true;
  }
  @FXML private void reset() {
    for (FlowPane fp : this.hmFlowPanes.values())
      for (Node nodes : fp.getChildren())
        ((Spinner<Integer>) ((VBox) nodes).getChildren().get(3)).getEditor().setText("");
  }
  @FXML private void updateCart() {
    // TODO: code on this
  }
  @FXML private void purchase() {
    String purchases = "";
    for (FlowPane fp : this.hmFlowPanes.values()) for (Node nodes : fp.getChildren()) {
      VBox vbItem = (VBox) nodes;
      int item_count = Integer.parseInt(((TextField) ((FlowPane) vbItem.getChildren().get(3))
          .getChildren().getFirst()).getText());
      if (item_count == 0) continue;
      String item_name = ((Label) vbItem.getChildren().get(1)).getText(),
      item_price = ((Label) vbItem.getChildren().get(2)).getText()
          .replace("Rp", "")
          .replace(",", "");
      double items_price = Double.parseDouble(item_price) * item_count;
      this.sumOfPurchase += items_price;
      purchases = purchases.concat(
          String.format("[%d] %s : Rp%,.2f\n", item_count, item_name, items_price)
      );
    }
    purchases = purchases.concat(String.format("\nTotal : Rp%,.2f\n", this.sumOfPurchase));

    // Purchase Receipt
    Alert receipt = new Alert(Alert.AlertType.INFORMATION);
    receipt.setTitle("Purchase Receipt");
    receipt.setHeaderText("Your Purchase:");
    receipt.setContentText(purchases);
    Image iReceipt = new Image(Objects.requireNonNull(
        getClass().getResourceAsStream("images/logo.png")));
    ImageView ivReceipt = new ImageView(iReceipt);
    ivReceipt.setFitWidth(50.0f);
    ivReceipt.setFitHeight(50.0f);
    receipt.setGraphic(ivReceipt);
    receipt.showAndWait();

    // Purchase again confirmation
    Alert again = new Alert(Alert.AlertType.CONFIRMATION);
    again.setTitle("Confirmation Dialog");
    again.setHeaderText("Do you want to purchase again?");
    again.setContentText(null);
    again.getButtonTypes().clear();
    ButtonType btYes = new ButtonType("Yes");
    ButtonType btNo = new ButtonType("No");
    again.getButtonTypes().addAll(btYes, btNo);
    again.setResizable(false);
    Optional<ButtonType> result = again.showAndWait();
    if (result.get() == btYes)
      reset();
    else
      System.exit(1);
  }
  @FXML private void showUserAccount() {
    Alert aAccountInfo = new Alert(Alert.AlertType.CONFIRMATION);
    aAccountInfo.setTitle("My Account");
    aAccountInfo.setHeaderText("");
    aAccountInfo.setContentText(String.format("Username: %s\nPassword: %s",
        this.coffeeShop.getIdentity()[0], this.coffeeShop.getIdentity()[1]));
    aAccountInfo.getButtonTypes().clear();
    ButtonType btLogout = new ButtonType("Logout");
    ButtonType btOk = new ButtonType("Ok");
    aAccountInfo.getButtonTypes().addAll(btLogout, btOk);
    aAccountInfo.setResizable(false);
    Optional<ButtonType> result = aAccountInfo.showAndWait();
    if (result.get() == btLogout)
      logout();
    aAccountInfo.close();
  }
  @FXML private void about() {
    Dialog<Object> about = new Dialog<>();
    about.setTitle("About VxCoffeeShop");
    about.setHeaderText("This application is an IB ComSci IA project");
    String context = """
        Brought to You by : Deffreus Theda
        
        "May the best get 7"
        
        Special thanks for my amazing teacher, Andri Pramono!""";
    about.setContentText(context);
    Image iLogo = new Image(Objects.requireNonNull(
        getClass().getResourceAsStream("images/logo.png")));
    ImageView ivLogo = new ImageView(iLogo);
    ivLogo.setFitHeight(50.0f);
    ivLogo.setFitWidth(50.0f);
    about.setGraphic(ivLogo);
    ButtonType btOk = new ButtonType("Okay!");
    about.getDialogPane().getButtonTypes().add(btOk);
    about.setResizable(false);
    about.showAndWait();
  }
  @FXML private void logout() {
    this.coffeeShop.logoutUser();
  }
  @FXML private void closeApp() {
    Alert aConfirm_exit = new Alert(Alert.AlertType.CONFIRMATION);
    aConfirm_exit.setHeaderText("Are you sure you want to exit VxCoffeeShop");
    aConfirm_exit.setTitle("Exit Confirmation");
    aConfirm_exit.setContentText("Your progress will not be saved.");
    aConfirm_exit.setResizable(false);
    aConfirm_exit.showAndWait();
    System.exit(1);
  }
}
