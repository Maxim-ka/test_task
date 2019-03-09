package myComboBox;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.cell.CheckBoxTreeCell;

class MyCheckBoxTreeCell<T> extends CheckBoxTreeCell<T> {

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
                setId("select");
                return;
            }
            if (!vague) setId("notSelected");
            else setId("vague");
        };
        indeterminate = (observable, oldValue, newValue) -> {
            vague = newValue;
            if (vague){
                select = false;
                setId("vague");
                return;
            }
            if (!select) setId("notSelected");
            else setId("select");
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

    void set(CheckBoxTreeItem<T> treeItem) {
        if (this.treeItem != null){
            this.treeItem.selectedProperty().removeListener(selected);
            this.treeItem.indeterminateProperty().removeListener(indeterminate);
        }
        this.treeItem = treeItem;
        select = treeItem.isSelected();
        vague = treeItem.isIndeterminate();
        if (select && !vague) setId("select");
        else if (vague)  setId("vague");
        else setId("notSelected");
        establish();
    }
}
