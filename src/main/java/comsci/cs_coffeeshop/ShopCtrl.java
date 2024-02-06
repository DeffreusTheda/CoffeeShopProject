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

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import lombok.Data;
import lombok.Getter;
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
    private final ArrayList<FlowPane> fps = new ArrayList<>();
    /* Editable End Here */

    // Shopping Cart Table View
    @FXML private TableView tvCart = new TableView<Item>();
    @FXML private TableColumn tbItemName, tbItemPrice, tbItemTotal, tbItemQuantity;
    private double sumOfPurchase = 0;
    @FXML private void initialize() {
        // Adding FLowPanes
        this.fps.add(fpFoods);
        this.fps.add(fpDrinks);

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
        if (con == null) {
            this.coffeeShop.showAlert("Sorry for the inconvenience",
                    "Failed to connect to product database",
                    """
                            Please try again later
                            Possible causes:
                            - Database server is down
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
                    "Alert",
                    "Database Error",
                    "Please immediately contact 'deffreus' on Discord"
            );
            return false;
        }

        // Get Item Rows
        PreparedStatement psGetItems = con.prepareStatement("SELECT * FROM items");
        ResultSet rsGetItems = psGetItems.executeQuery();
        while(rsGetItems.next()) { // for each row (item) in database
            // VBox for an item
            VBox vbItem = new VBox();
            vbItem.setPrefWidth(150.0f);
            vbItem.setSpacing(3.0f);

            // Image of the item
            ImageView ivItem = new ImageView(new Image(getClass().getResourceAsStream(
                    "images/" + rsGetItems.getString("name")))
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
            fpItem.getChildren().add(bIncrease);

            // Increment button
            Button bDecrease = new Button("↓");
            bIncrease.setMnemonicParsing(false);
            fpItem.getChildren().add(bDecrease);

            // Add item according to category

        }

        // Set Up FLow Panes ↑↓
        for (int i = 0; i < categoryCount; ++i) {
            FlowPane fp = new FlowPane();
            fp.setHgap(19.0f);
            fp.setVgap(10.0f);
            fp.setPrefHeight(200.0f);
            // Populate Items
            while (rsGetItems.next()) {
                // VBox for an Item
                VBox vbItem = new VBox();
                vbItem.setPrefHeight(200.0);
                vbItem.setPrefWidth(150.0);
                // Item Image [0]
                ImageView ivItem = new ImageView();
                try {
                    String imagePath = "images/" + rsGetItems.getString("name") + ".png";
                    ivItem.setImage(new Image(getClass().getResourceAsStream(imagePath)));
                } catch(NullPointerException e) {
                    System.out.println("NullPointerException");
                }
                ivItem.setFitHeight(150.0f);
                ivItem.setFitWidth(150.0f);
                ivItem.setPickOnBounds(true);
                ivItem.setPreserveRatio(true);
                Insets iImg = new Insets(0f, 12f, 0f, 12f);
                VBox.setMargin(vbItem, iImg);

                // ItemName Label [1]
                Label lItem_name = new Label(rsGetItems.getString("name"));
                lItem_name.setAlignment(Pos.CENTER);
                lItem_name.setPrefWidth(150.0f);
                lItem_name.setFont(new Font("Serif Regular", 22.0f));
                lItem_name.setTextFill(new Color(126/255f, 142/255f, 253/255f, 1f));

                // ItemPrice Label [2]
                Label lItem_price = new Label(
                        String.format("Rp%,.2f", (double) rsGetItems.getInt("price"))
                );
                lItem_price.setAlignment(Pos.CENTER);
                lItem_price.setPrefWidth(150.0f);
                lItem_price.setFont(new Font("Serif Regular", 18.0f));

                // FLowPane Quantity Objects
                FlowPane fpValue = new FlowPane();

                // Value TextField [3][0]
                TextField tfValue =  new TextField();
                tfValue.setPromptText("0");
                tfValue.setPrefHeight(26.0f);
                tfValue.setPrefWidth(95.0f);
                tfValue.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.isEmpty()) {
                        return;
                    }
                    try {
                        int value = Integer.parseInt(newValue);
                        if (value < 0) {
                          throw new NumberFormatException();
                        }
                        updateCart();
                    } catch (NumberFormatException e) {
                        coffeeShop.showAlert("Exception Found",
                                "", /* Empty for simplicity */
                                "Please input the number correctly"
                        );
                        tfValue.setText("");
                    }
                });
                fpValue.getChildren().add(tfValue);

                // Increment Button [3][1]
                Button bIncrement = new Button("↑");
                bIncrement.setMnemonicParsing(false);
                bIncrement.setPrefHeight(26.0f);
                bIncrement.setPrefWidth(27.5f);
                bIncrement.setTextOverrun(OverrunStyle.CLIP);
                bIncrement.setOnAction(event -> {
                    if (tfValue.getText().isEmpty()) {
                        tfValue.setText("0");
                    }
                    tfValue.setText(String.valueOf(Integer.parseInt(tfValue.getText()) + 1));
                });
                fpValue.getChildren().add(bIncrement);

                // Decrement Button [3][2]
                Button bDecrement = new Button("↓");
                bDecrement.setMnemonicParsing(false);
                bDecrement.setPrefHeight(26.0f);
                bDecrement.setPrefWidth(27.5f);
                bDecrement.setTextOverrun(OverrunStyle.CLIP);
                bDecrement.setOnAction(event -> {
                    if (tfValue.getText().isEmpty()) {
                        return;
                    }
                    if (Integer.parseInt(tfValue.getText()) >= 1) {
                        tfValue.setText(String.valueOf(Integer.parseInt(tfValue.getText()) - 1));
                    }
                    if (Integer.parseInt(tfValue.getText()) == 0) {
                        tfValue.setText("");
                    }
                });
                fpValue.getChildren().add(bDecrement);

                // Finally
                vbItem.getChildren().addAll(ivItem, lItem_name, lItem_price, fpValue);
                fp.getChildren().add(vbItem);
            }
        }
        return true;
    }
    @FXML private void reset() {
        for (FlowPane fp : this.fps) {
            for (Node nodes : fp.getChildren()) {
                ((Spinner<Integer>) ((VBox) nodes).getChildren().get(3)).getEditor().setText("");
            }
        }
    }
    @FXML private void updateCart() {

    }
    @FXML private void purchase() {
        String purchases = "";
        for (FlowPane fp : this.fps) for (Node nodes : fp.getChildren()) {
            VBox vbItem = (VBox) nodes;
            int item_count = ((Spinner<Integer>) vbItem.getChildren().get(3)).getValue();
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
        Image iReceipt = new Image(getClass().getResourceAsStream("images/logo.png"));
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
        if (result.get() == btYes) {
            reset();
        } else {
            System.exit(1);
        }
    }
    @FXML private void showUserAccount() {
        Alert user_info = new Alert(Alert.AlertType.INFORMATION);
        user_info.setTitle("Your Account");
        user_info.setHeaderText(null);
        user_info.setContentText(String.format("Username: %s\nPassword: %s", this.coffeeShop.getIdentity()[0], this.coffeeShop.getIdentity()[1]));
        user_info.getButtonTypes().clear();
        user_info.setResizable(false);
        user_info.showAndWait();
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
        Image iLogo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/logo.png")));
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
