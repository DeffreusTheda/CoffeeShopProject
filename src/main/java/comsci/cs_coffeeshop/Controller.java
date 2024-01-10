package comsci.cs_coffeeshop;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Arrays;

public class Controller {
    @FXML
    private TextField c1, c2, c3, c4, c5, c6;
    @FXML
    private Label warning;
    @FXML
    private int[] orders = {0, 0, 0, 0, 0, 0};
    private final String[] menus = {"Espresso", "Americano", "Macciato", "Latte", "Cappucino", "Mochaccino"};
    private final double[] prices = {35.0, 35.0, 40.0, 40.0, 35.0, 35.0};
    private Alert struck = new Alert(Alert.AlertType.INFORMATION);

    @FXML
    protected void purchase() {
        String message = "Your purchase:\n";
        boolean isEmpty = true, o3dc = false, o4dc = false;
        double sum = 0;
        try {
            orders[0] = Integer.parseInt(c1.getText());
            orders[1] = Integer.parseInt(c2.getText());
            orders[2] = Integer.parseInt(c3.getText());
            orders[3] = Integer.parseInt(c4.getText());
            orders[4] = Integer.parseInt(c5.getText());
            orders[5] = Integer.parseInt(c6.getText());
        } catch (Exception e) {
            warning.setText("Warning: at least one of the menu portion value is invalid!");
            return;
        }
        warning.setText("");
        for (int i = 0; i < orders.length; i++) {
            if (orders[i] > 0) {
                double cur = prices[i] * orders[i];
                if (orders[3] >= 10 && i == 3) {
                    cur = prices[i] * 0.95 * orders[i];
                    o3dc = true;
                }
                if (orders[4] >= 10 && i == 4) {
                    cur = prices[i] * 0.9 * orders[i];
                    o4dc = true;
                }
                message = message.concat(String.format("\n%d %s = Rp.%,.2f", orders[i], menus[i], cur * 1000));
                sum += cur * 1000;
                isEmpty = false;
            }
        }
        struck.setTitle("Struck");
        struck.setHeaderText(null); // No header text for simplicity
        if (isEmpty) {
            struck.setContentText("You haven't purchased anything.");
            return;
        }
        message = message.concat("\n");
        if (o3dc)
            message = message.concat(String.format("\nDiscount on %s: %s", menus[3], "5%"));
        if (o4dc)
            message = message.concat(String.format("\nDiscount on %s: %s", menus[4], "10%"));
        message = message.concat(String.format("\nTotal: Rp.%,.2f", sum));
        struck.setContentText(message);
        struck.showAndWait();
    }

    @FXML
    protected void reset() {
        c1.setText("0");
        c2.setText("0");
        c3.setText("0");
        c4.setText("0");
        c5.setText("0");
        c6.setText("0");
        Arrays.fill(orders, 0);
        warning.setText("");
    }

    @FXML
    protected void add1() {
        orders[0] = Math.max(Integer.parseInt(c1.getText()) + 1, 0);
        c1.setText(String.valueOf(orders[0]));
    }

    @FXML
    protected void rm1() {
        orders[0] = Math.max(Integer.parseInt(c1.getText()) - 1, 0);
        c1.setText(String.valueOf(orders[0]));
    }

    @FXML
    protected void update1() {
        orders[0] = Integer.parseInt(c1.getText());
    }

    @FXML
    protected void add2() {
        orders[1] = Math.max(Integer.parseInt(c2.getText()) + 1, 0);
        c2.setText(String.valueOf(orders[1]));
    }

    @FXML
    protected void rm2() {
        orders[1] = Integer.parseInt(c2.getText()) > 0 ? Integer.parseInt(c2.getText()) - 1 : 0;
        c2.setText(String.valueOf(orders[1]));
    }

    @FXML
    protected void update2() {
        orders[1] = Integer.parseInt(c2.getText());
    }

    @FXML
    protected void add3() {
        orders[2] = Math.max(Integer.parseInt(c3.getText()) + 1, 0);
        c3.setText(String.valueOf(orders[2]));
    }

    @FXML
    protected void rm3() {
        orders[2] = Integer.parseInt(c3.getText()) > 0 ? Integer.parseInt(c3.getText()) - 1 : 0;
        c3.setText(String.valueOf(orders[2]));
    }

    @FXML
    protected void update3() {
        orders[2] = Integer.parseInt(c3.getText());
    }

    @FXML
    protected void add4() {
        orders[3] = Math.max(Integer.parseInt(c4.getText()) + 1, 0);
        c4.setText(String.valueOf(orders[3]));
    }

    @FXML
    protected void rm4() {
        orders[3] = Integer.parseInt(c4.getText()) > 0 ? Integer.parseInt(c4.getText()) - 1 : 0;
        c4.setText(String.valueOf(orders[3]));
    }

    @FXML
    protected void update4() {
        orders[3] = Integer.parseInt(c4.getText());
    }

    @FXML
    protected void add5() {
        orders[4] = Math.max(Integer.parseInt(c5.getText()) + 1, 0);
        c5.setText(String.valueOf(orders[4]));
    }

    @FXML
    protected void rm5() {
        orders[4] = Integer.parseInt(c5.getText()) > 0 ? Integer.parseInt(c5.getText()) - 1 : 0;
        c5.setText(String.valueOf(orders[4]));
    }

    @FXML
    protected void update5() {
        orders[4] = Integer.parseInt(c5.getText());
    }

    @FXML
    protected void add6() {
        orders[5] = Math.max(Integer.parseInt(c6.getText()) + 1, 0);
        c6.setText(String.valueOf(orders[5]));
    }

    @FXML
    protected void rm6() {
        orders[5] = Integer.parseInt(c6.getText()) > 0 ? Integer.parseInt(c6.getText()) - 1 : 0;
        c6.setText(String.valueOf(orders[5]));
    }

    @FXML
    protected void update6() {
        orders[5] = Integer.parseInt(c6.getText());
    }
}