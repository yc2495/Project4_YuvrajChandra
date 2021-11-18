package com.example.project4_yuvrajchandra;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.controlsfx.control.PropertySheet;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

   @FXML
    private Button SaveButton;
   @FXML
    private Button NewButton;
   @FXML
    private ChoiceBox<ItemType> Type;
   @FXML
    private TextField Name;
   @FXML
    private TextField Price;
   @FXML
    private ChoiceBox<MerchandiseItem> ItemList;

   private MerchandiseItem CurrentItem;
   Store StockList = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            StockList = new Store();
        }
        catch (IOException error) {
            error.printStackTrace();
        }
        ItemList.getItems().addAll(Store.getStock());
        ItemList.setOnAction(this::SelectItem);
        Type.getItems().addAll(ItemType.values());
    }

    public MerchandiseItem SelectItem(ActionEvent click);{
        var selectedItem = ItemList.getValue();
        return selectedItem;
    }


}