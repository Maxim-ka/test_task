package myComboBox;

import controller.Controller;
import data.RowData;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.util.StringConverter;

public class MyComboBox {

    private TextField textField;
    private final Popup popup;
    private final CheckBoxTreeItem<RowData> root;
    private final TreeView<RowData> treeView;
    private ObservableList<RowData> observableList;
    private ObservableList<RowData> selectedData;
    private boolean isShowing;

    public MyComboBox(Controller controller) {
        textField = controller.getTextField();
        Button button = controller.getButton();
        this.selectedData = controller.getSelectedData();
        button.setOnMouseClicked(event -> {
            isShowing = !isShowing;
            showTree();
        });
        root = new CheckBoxTreeItem<>(new RowData(null, "Типы файлов", null));
        root.setExpanded(true);
        treeView = new TreeView<>(root);
        treeView.setCellFactory(CheckBoxTreeCell.forTreeView());
        treeView.cellFactoryProperty().setValue(param -> {
            MyCheckBoxTreeCell<RowData> cell = new MyCheckBoxTreeCell<>();
            cell.setConverter(new StringConverter<TreeItem<RowData>>() {
                @Override
                public String toString(TreeItem<RowData> object) {
                    if (object == null) return null;
                    CheckBoxTreeItem<RowData> item = (CheckBoxTreeItem<RowData>) object;
                    cell.set(item);
                    if (item.isLeaf()){
                        return item.getValue().getExtension();
                    }
                    return item.getValue().getType();
                }

                @Override
                public TreeItem<RowData> fromString(String string) {
                    return null;
                }
            });
            return cell;
        });
        popup = new Popup();
        popup.setAutoHide(false);
        popup.setHideOnEscape(false);
        popup.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_TOP_LEFT);
        popup.getContent().add(treeView);
    }

    private Tooltip createTooltip(){
        if (!isShowing){
            Tooltip tooltip = new Tooltip();
//            tooltip.setText(getChoice());
            return tooltip;
        }
        return null;
    }

//    private String getChoice(){
//        if (root.isSelected() || root.isIndeterminate()){
//
//        }
//        return null;
//    }

    public void getSelected(){
        selectedData.clear();
        if (!root.isSelected() && !root.isIndeterminate()) return;
        if (root.isSelected()){
            selectedData.addAll(observableList);
            return;
        }
        for (int i = 0; i < root.getChildren().size() ; i++) {
            CheckBoxTreeItem<RowData> item = (CheckBoxTreeItem<RowData>) root.getChildren().get(i);
            if (!item.isSelected() && !item.isIndeterminate()) continue;
            for (int j = 0; j < item.getChildren().size(); j++) {
                CheckBoxTreeItem<RowData> itemLeaf = (CheckBoxTreeItem<RowData>) item.getChildren().get(j);
                if (itemLeaf.isSelected()) selectedData.add(itemLeaf.getValue());
            }
        }
    }

    private void showTree(){
        if (isShowing){
            Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
            popup.show(textField, bounds.getMinX(), bounds.getMaxY());
        } else popup.hide();
    }

    public void setItems(final ObservableList<RowData> dataObservableList){
        observableList = dataObservableList;
        observableList.addListener((ListChangeListener<RowData>) c -> {
            if (observableList.isEmpty()){
                if (!root.getChildren().isEmpty()) root.getChildren().clear();
                return;
            }
            root.getChildren().clear();
            CheckBoxTreeItem<RowData> typeFile;
            for (RowData row : observableList) {
                String type = row.getType();
                boolean same = !root.getChildren().isEmpty() && type.equals(root.getChildren().get(root.getChildren().size() - 1).getValue().getType());
                typeFile = (same) ? (CheckBoxTreeItem<RowData>) root.getChildren().get(root.getChildren().size() - 1) :
                    new CheckBoxTreeItem<>(new RowData(null,row.getType(),null));
                typeFile.getChildren().add(new CheckBoxTreeItem<>(row));
                typeFile.setExpanded(true);
                if (!same) root.getChildren().add(typeFile);
            }
        });
    }
}
