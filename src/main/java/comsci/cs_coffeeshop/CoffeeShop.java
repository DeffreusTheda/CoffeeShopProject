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
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CoffeeShop extends Application {
    protected static Stage primaryStage;
    protected static Scene loginStage, shopScene;
    static String[][] account =
            {{"admin"},   // Username
                    {"4|)|\\/|!|\\|"}}; // Password
    static boolean authenticationSuccessful = false;
    static String[] identity = {"", ""};

    protected static void loginUser(String name, String pass) {
        authenticationSuccessful = true;
        identity[0] = name;
        identity[1] = pass;
        CoffeeShop.primaryStage.setScene(CoffeeShop.shopScene);
    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        loginStage = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml"))));
        shopScene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("shop.fxml"))));
        primaryStage.setTitle("Volistic Coffee Shop");
        primaryStage.setScene(loginStage);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}