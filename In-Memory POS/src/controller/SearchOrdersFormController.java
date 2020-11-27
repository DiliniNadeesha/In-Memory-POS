/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.sun.javafx.scene.control.skin.LabeledText;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import util.CustomerTM;
import util.Order;
import util.OrderDetail;
import util.OrderTM;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class SearchOrdersFormController {

    public TextField txtSearch;
    public TableView<OrderTM> tblOrders;

    private ArrayList<OrderTM> orders = new ArrayList<>();

    public void initialize() {

        tblOrders.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("orderId"));
        tblOrders.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        tblOrders.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("customerId"));
        tblOrders.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("customerName"));
        tblOrders.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("orderTotal"));

        for (Order order : PlaceOrderFormController.ordersDB) {
            OrderTM o = new OrderTM(order.getId(), order.getDate(), order.getCustomerId(), getCustomerName(order.getCustomerId()),
                    getOrderTotal(order.getOrderDetails()));
            orders.add(o);
            tblOrders.getItems().add(o);
        }

        txtSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ObservableList<OrderTM> searchOrders = tblOrders.getItems();
                searchOrders.clear();
                for (OrderTM order : orders) {
                    if ((order.getOrderId().contains(newValue)||
                            order.getCustomerId().contains(newValue) ||
                            order.getCustomerName().contains(newValue) ||
                            order.getOrderDate().toString().contains(newValue))){
                        searchOrders.add(order);
                    }
                }
            }
        });

    }

    private String getCustomerName(String customerId){
        for (CustomerTM customer : ManageCustomerFormController.customersDB) {
            if (customer.getId().equals(customerId)){
                return customer.getName();
            }
        }
        return null;
    }

    private double getOrderTotal(ArrayList<OrderDetail> orderDetails){
        double total = 0;
        for (OrderDetail orderDetail : orderDetails) {
            total += orderDetail.getQty() * orderDetail.getUnitPrice();
        }
        return total;
    }

    @FXML
    private void navigateToHome(MouseEvent event) throws IOException {
        URL resource = this.getClass().getResource("/view/MainForm.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.txtSearch.getScene().getWindow());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    public void tblOrders_OnMouseClicked(MouseEvent mouseEvent) throws IOException {
        if (tblOrders.getSelectionModel().getSelectedItem() == null){
            return;
        }
        if (mouseEvent.getClickCount() == 2){
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/PlaceOrderForm.fxml"));
            Parent root = fxmlLoader.load();
            PlaceOrderFormController controller = (PlaceOrderFormController) fxmlLoader.getController();
            controller.initializeWithSearchOrderForm(tblOrders.getSelectionModel().getSelectedItem().getOrderId());
            Scene orderScene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(orderScene);
            stage.centerOnScreen();
            stage.show();
        }
    }

}
