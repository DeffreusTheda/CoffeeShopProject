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
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class LoginCtrl {
    private CoffeeShop coffeeShop;
    protected void setCoffeeShop(CoffeeShop cs) {
       coffeeShop = Objects.requireNonNull(cs);
    }
    // LOGIN PAGE
    @FXML @Setter private ImageView ivLogo;
    @FXML private TextField tfUsername, tfPassword;
    @FXML private PasswordField pfPassword;
    @FXML private CheckBox cbToggleShowPassword;
    @FXML private Label lWarning;
    protected void setLWarningText(String message) {
        this.lWarning.setText(message);
    }
    @FXML private void signIn() throws SQLException {
        String username = tfUsername.getText(), password = pfPassword.getText();
        if (username.isEmpty() || password.isEmpty()) {
            lWarning.setText("Please fill your username and password first");
            return;
        }
        lWarning.setText("");
        Connection con = coffeeShop.connectToDB();
        String loginQuery = "SELECT FROM users WHERE username = ? AND password = ?";
        PreparedStatement psLogin = Objects.requireNonNull(con).prepareStatement(loginQuery);
        psLogin.setString(1, username);
        psLogin.setString(2, password);
        ResultSet rsLogin = psLogin.executeQuery();
        if (rsLogin.next()) {
            coffeeShop.loginUser(username, password);
            return;
        }
        lWarning.setText("Invalid login credentials");
    }
    @FXML private void signUp() {
        if (tfUsername.getText().isEmpty() || pfPassword.getText().isEmpty()) {
            lWarning.setText("Please fill your name and password.");
            return;
        }
        lWarning.setText("");
        Connection con = coffeeShop.connectToDB();
        coffeeShop.registerUser(tfUsername.getText(), pfPassword.getText(), con);
    }
    @FXML private void toggleShowPassword() {
        if (cbToggleShowPassword.isSelected()) {
            tfPassword.setText(pfPassword.getText());
            tfPassword.setDisable(false); tfPassword.setVisible(true);
            pfPassword.setVisible(false); pfPassword.setDisable(true);
            return;
        }
        pfPassword.setText(tfPassword.getText());
        tfPassword.setDisable(true); tfPassword.setVisible(false);
        pfPassword.setDisable(false); pfPassword.setVisible(true);
    }
}