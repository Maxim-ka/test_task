package view;

import data.Data;
import data.RowData;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;

import java.util.Set;

public class MyComboBox {

    private TextField textField;
    private Button button;
    private final Popup popup;
    private final TreeView<String> treeView;
    private final CheckBoxTreeItem<String> root;
    private boolean isShowing;

    public MyComboBox(TextField textField, Button button) {
        this.textField = textField;
        this.button = button;
        this.button.setOnMouseClicked(event -> {
            isShowing = !isShowing;
            showTree();
        });
        root = new CheckBoxTreeItem<>("Типы файлов");
        root.setExpanded(true);
        treeView = new TreeView<>(root);
        treeView.setCellFactory(CheckBoxTreeCell.forTreeView());
        popup = new Popup();
        popup.setAutoHide(false);
        popup.setHideOnEscape(false);
        popup.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_TOP_LEFT);
        popup.getContent().add(treeView);
    }

    private void showTree(){
        if (isShowing){
            Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
            popup.show(textField.getScene().getWindow(), bounds.getMinX(), bounds.getMaxY());
        } else popup.hide();
    }

    public void setItems(ObservableList<RowData> dataObservableList){
        dataObservableList.addListener(new ListChangeListener<RowData>() {
            @Override
            public void onChanged(Change<? extends RowData> c) {
                if (dataObservableList.isEmpty()){
                    if (!root.getChildren().isEmpty()) root.getChildren().clear();
                    return;
                }
                root.getChildren().clear();
                CheckBoxTreeItem<String> typeFile;
                for (RowData row : dataObservableList) {
                    String type = row.getType();
                    boolean same = !root.getChildren().isEmpty() && root.getChildren().get(root.getChildren().size() - 1).getValue().equals(type);
                    typeFile = (same) ? (CheckBoxTreeItem<String>)root.getChildren().get(root.getChildren().size() - 1) :
                        new CheckBoxTreeItem<>(type);
                    typeFile.getChildren().add(new CheckBoxTreeItem<>(row.getExtension()));
                    if (!same) root.getChildren().add(typeFile);
                }
            }
        });
    }
}
