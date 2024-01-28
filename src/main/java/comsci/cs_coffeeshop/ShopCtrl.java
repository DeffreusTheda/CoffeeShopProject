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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;

public class ShopCtrl {
    private class Item {
        @Setter @Getter private String name;
        @Setter @Getter private double price, total;
        @Setter @Getter private int quantity;

        public Item(String testname, String testprice, String testQTY, String testtotal) {
            this.name = testname;
            this.price = Double.parseDouble(testprice);
            this.quantity = Integer.parseInt(testQTY);
            this.total = Double.parseDouble(testtotal);
        }
    }
    @Setter private CoffeeShop coffeeShop;
    // CAFE MENU
    @FXML private Label lWelcome;
    protected void setLWelcomeText(String message) {
        this.lWelcome.setText(message);
    }
    /* Safely Editable Start Here */
    @FXML private FlowPane fpFoods, fpDrinks;
    @FXML private final ArrayList<FlowPane> fps = new ArrayList<>(2);
    /* Safely Editable End Here */
    // METADATA
    @FXML private TableView<Item> tvCart;
    @FXML private TableColumn<Item, String> itemName, itemPrice, itemTotal, itemQuantity;
    private void ShopCtrl() {
        this.itemName.setCellValueFactory(new PropertyValueFactory<Item, String>("name"));
        this.itemPrice.setCellValueFactory(new PropertyValueFactory<Item, String>("price"));
        this.itemQuantity.setCellValueFactory(new PropertyValueFactory<Item, String >("quantity"));
        this.itemTotal.setCellValueFactory(new PropertyValueFactory<Item, String>("total"));
        this.tvCart.setItems(this.tvCart.getItems());
    }
    private double sumOfPurchase = 0;
    @FXML private void initialize(CoffeeShop coffeeShop) {
        this.fps.add(this.fpFoods); this.fps.add(this.fpDrinks);
        for (int i = 0; i < this.fps.size(); ++i) this.populateItems(this.fps.get(i), this.coffeeShop.getCategories()[i]);
    }
    private ObservableList<Item> getItems() {
        ObservableList<Item> items = FXCollections.observableArrayList();
        items.add(new Item("testname", "testprice", "testQTY", "testtotal"));

        return items;
    }
    private void populateItems(FlowPane fp, String[][] items) {
        for (String[] item : items) {
            // vbox for an item
            VBox vbItem = new VBox();
            vbItem.setPrefHeight(200.0);
            vbItem.setPrefWidth(150.0);
            // item image [0]
            ImageView ivItem = new ImageView();
            try { ivItem.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(item[2])))); }
            catch(NullPointerException e) { System.out.println("NullPointerException"); }
            ivItem.setFitHeight(150.0f);
            ivItem.setFitWidth(150.0f);
            ivItem.setPickOnBounds(true);
            ivItem.setPreserveRatio(true);
            Insets iImg = new Insets(0f, 12f, 0f, 12f);
            VBox.setMargin(vbItem, iImg);
            // item name label [1]
            Label lItem_name = new Label(item[0]);
            lItem_name.setAlignment(Pos.CENTER);
            lItem_name.setPrefWidth(150.0f);
            lItem_name.setFont(new Font("Serif Regular", 22.0f));
            lItem_name.setTextFill(new Color(126/255f, 142/255f, 253/255f, 1f));
            // item price label [2]
            Label lItem_price = new Label(String.format("Rp%,.2f", Double.parseDouble(item[1])));
            lItem_price.setAlignment(Pos.CENTER);
            lItem_price.setPrefWidth(150.0f);
            lItem_price.setFont(new Font("Serif Regular", 18.0f));
            // spinner [3]
            Spinner<Integer> sFood = new Spinner<>();
            sFood.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 32767));
            sFood.getEditor().setText("");
            sFood.setPromptText("0");
            sFood.setPrefHeight(30.0f);
            sFood.setEditable(true);
            // finally
            vbItem.getChildren().addAll(ivItem, lItem_name, lItem_price, sFood);
            fp.getChildren().add(vbItem);
        }
    }
    @FXML private void reset() {
        for (FlowPane fp : this.fps) for (Node nodes : fp.getChildren()) {
            ((Spinner<Integer>) ((VBox) nodes).getChildren().get(3)).getEditor().setText("");
        }
    }
    @FXML private void purchase() {
        String purchases = "";
        for (FlowPane fp : this.fps) for (Node nodes : fp.getChildren()) {
            VBox vbItem = (VBox) nodes;
            int item_count = ((Spinner<Integer>) vbItem.getChildren().get(3)).getValue();
            if (item_count == 0) continue;
            String item_name = ((Label) vbItem.getChildren().get(1)).getText(),
            item_price = ((Label) vbItem.getChildren().get(2)).getText().replace("Rp", "").replace(",", "");
            double items_price = Double.parseDouble(item_price) * item_count;
            this.sumOfPurchase += items_price;
            purchases = purchases.concat(String.format("[%d] %s : Rp%,.2f\n", item_count, item_name, items_price));
        }
        purchases = purchases.concat(String.format("\nTotal : Rp%,.2f\n", this.sumOfPurchase));
        // Purchase Receipt
        Alert receipt = new Alert(Alert.AlertType.INFORMATION);
        receipt.setTitle("Purchase Receipt");
        receipt.setHeaderText("Your Purchase:");
        receipt.setContentText(purchases);
        Image iReceipt = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/logo.png")));
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
        if (result.get() == btYes) reset(); else System.exit(1);
    }
    @FXML private void showUserAccount() {
        Alert user_info = new Alert(Alert.AlertType.INFORMATION);
        user_info.setTitle("Your Account");
        user_info.setHeaderText(null);
        user_info.setContentText(String.format("Username: %s\nPassword: %s", this.coffeeShop.identity[0], this.coffeeShop.identity[1]));
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
