package myComboBox;

import controller.Controller;
import data.RowData;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;

public class MyComboBox {

    private static final int HEIGHT_ITEM = 25;
    private static final String ALL = "All";
    private static final String COLON = ": ";
    private static final String COMMA = ", ";
    private static final String THREE_DOTS = "... ";
    private static final String FILE_TYPES = "Типы файлов";
    private TextField textField;
    private final Popup popup;
    private final CheckBoxTreeItem<RowData> root;
    private final StringBuilder stringBuilder;
    private ObservableList<RowData> observableList;
    private ObservableList<RowData> selectedData;
    private boolean isShowing;

    public MyComboBox(Controller controller) {
        stringBuilder = new StringBuilder();
        textField = controller.getTextField();
        textField.setOnMouseEntered(event -> textField.setTooltip(createTooltip()));
        Button button = controller.getButton();
        this.selectedData = controller.getSelectedData();
        button.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            isShowing = !isShowing;
            showTree();
        });
        root = new CheckBoxTreeItem<>(new RowData(null, FILE_TYPES, null));
        root.setExpanded(true);
        TreeView<RowData> treeView = new TreeView<>(root);
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
        treeView.setPrefWidth(textField.getPrefWidth() + button.getPrefWidth());
        treeView.setPrefHeight(controller.getTable_1().getPrefHeight());        
        popup = new Popup();
        popup.setAutoHide(true);
        popup.setHideOnEscape(false);
        popup.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_TOP_LEFT);
        popup.getContent().add(treeView);
        popup.addEventHandler(WindowEvent.WINDOW_HIDING, event -> {
            isShowing = !isShowing;
            if (!isShowing){
                textField.setText(getChoice());
            }
        });
    }

    private Tooltip createTooltip(){
        if (isShowing) return null;
        Node node = createTree();
        if (node == null) return null;
        Tooltip tooltip = new Tooltip();
        tooltip.setContentDisplay(ContentDisplay.CENTER);
        tooltip.setGraphic(node);
        return tooltip;
    }

    private Node createTree(){
        if (root != null){
            if (root.isSelected() || root.isIndeterminate()){
                TreeView<String> treeView = new TreeView<>();
                treeView.setRoot(new TreeItem<>(root.getValue().getType()));
                treeView.setShowRoot(false);
                TreeItem<String> rootView = treeView.getRoot();
                int count = 0;
                for (int i = 0; i <root.getChildren().size() ; i++) {
                    CheckBoxTreeItem<RowData> item = (CheckBoxTreeItem<RowData>) root.getChildren().get(i);
                    if (!item.isSelected() && !item.isIndeterminate()) continue;
                    TreeItem<String> treeItem = new TreeItem<>(item.getValue().getType());
                    for (int j = 0; j < item.getChildren().size(); j++) {
                        CheckBoxTreeItem<RowData> itemLeaf = (CheckBoxTreeItem<RowData>) item.getChildren().get(j);
                        if (itemLeaf.isSelected()){
                            treeItem.getChildren().add(new TreeItem<>(itemLeaf.getValue().getExtension()));
                            count++;
                        }
                    }
                    treeItem.setExpanded(true);
                    rootView.getChildren().add(treeItem);
                    count++;
                }
                treeView.setEditable(false);
                treeView.setPrefSize(textField.getPrefWidth(), HEIGHT_ITEM * count);
                return treeView;
            }
        }
        return null;
    }

    private String getChoice(){
        if (root.isSelected() && !root.isIndeterminate()) return ALL;
        if (root.isIndeterminate()){
            stringBuilder.delete(0, stringBuilder.length());
            for (int i = 0; i < root.getChildren().size(); i++) {
                CheckBoxTreeItem<RowData> item = (CheckBoxTreeItem<RowData>) root.getChildren().get(i);
                if (!item.isSelected() && !item.isIndeterminate()) continue;
                stringBuilder.append(item.getValue().getType()).append(COLON);
                for (int j = 0; j < item.getChildren().size(); j++) {
                    CheckBoxTreeItem<RowData> itemLeaf = (CheckBoxTreeItem<RowData>) item.getChildren().get(j);
                    if (itemLeaf.isSelected()){
                        stringBuilder.append(itemLeaf.getValue().getExtension()).append(COMMA);
                    }
                }
                stringBuilder.replace(stringBuilder.lastIndexOf(COMMA), stringBuilder.length(), THREE_DOTS);
            }
            stringBuilder.delete(stringBuilder.lastIndexOf(THREE_DOTS), stringBuilder.length());
            return stringBuilder.toString();
        }
        return null;
    }

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
