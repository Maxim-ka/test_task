package myComboBox;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.cell.CheckBoxTreeCell;

class MyCheckBoxTreeCell<T> extends CheckBoxTreeCell<T> {

    private static final String SELECT = "select";
    private static final String NOT_SELECTED = "notSelected";
    private static final String VAGUE = "vague";
    private CheckBoxTreeItem<T> treeItem;
    private ChangeListener<Boolean> selected;
    private ChangeListener<Boolean> indeterminate;
    private boolean select;
    private boolean vague;

    MyCheckBoxTreeCell() {
        super();
        selected = (observable, oldValue, newValue) -> {
            select = newValue;
            if (select){
                vague = false;
                setId(SELECT);
                return;
            }
            if (!vague) setId(NOT_SELECTED);
            else setId(VAGUE);
        };
        indeterminate = (observable, oldValue, newValue) -> {
            vague = newValue;
            if (vague){
                select = false;
                setId(VAGUE);
                return;
            }
            if (!select) setId(NOT_SELECTED);
            else setId(SELECT);
        };
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        CheckBoxTreeItem<T> treeItem = (CheckBoxTreeItem<T>) getTreeItem();
        if (treeItem != null){
            set(treeItem);
        }
    }

    private void establish() {
        treeItem.selectedProperty().addListener(selected);
        treeItem.indeterminateProperty().addListener(indeterminate);
    }

    private void remove(){
        if (treeItem != null){
            treeItem.selectedProperty().removeListener(selected);
            treeItem.indeterminateProperty().removeListener(indeterminate);
        }
    }

    void set(CheckBoxTreeItem<T> treeItem) {
        remove();
        this.treeItem = treeItem;
        select = treeItem.isSelected();
        vague = treeItem.isIndeterminate();
        if (select && !vague) setId(SELECT);
        else if (vague)  setId(VAGUE);
        else setId(NOT_SELECTED);
        establish();
    }
}
