package controller;

import data.RowData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellUtil;
import myComboBox.MyComboBox;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Button apply;
    @FXML
    private TextField textField;
    @FXML
    private Button button;
    @FXML
    private TableView<RowData> table_1;
    @FXML
    private TableColumn<RowData, String> tab_1_extension;
    @FXML
    private TableColumn<RowData, String> tab_1_type;
    @FXML
    private TableColumn<RowData, String> tab_1_example;
    @FXML
    private TableView<RowData> table_2;
    @FXML
    private TableColumn<RowData, String> tab_2_extension;
    @FXML
    private TableColumn<RowData, String> tab_2_type;
    @FXML
    private TableColumn<RowData, String> tab_2_example;

    private MyComboBox myComboBox;
    private final ObservableList<RowData> dataObservableList = FXCollections.observableArrayList();
    private final ObservableList<RowData> selectedData = FXCollections.observableArrayList();

    public TextField getTextField() {
        return textField;
    }

    public Button getButton() {
        return button;
    }

    public ObservableList<RowData> getSelectedData() {
        return selectedData;
    }

    public TableView<RowData> getTable_1() {
        return table_1;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        apply.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> myComboBox.getSelected());
        myComboBox = new MyComboBox(this);
        table_1.setItems(dataObservableList);
        table_2.setItems(selectedData);
        myComboBox.setItems(dataObservableList);
        tab_1_extension.setCellValueFactory(new PropertyValueFactory <> ("extension"));
        tab_1_type.setCellValueFactory (new PropertyValueFactory <> ("type"));
        tab_1_example.setCellValueFactory(new PropertyValueFactory <> ("example"));
        tab_2_extension.setCellValueFactory(new PropertyValueFactory <> ("extension"));
        tab_2_type.setCellValueFactory (new PropertyValueFactory <> ("type"));
        tab_2_example.setCellValueFactory(new PropertyValueFactory <> ("example"));

        readFile();
    }

    private void readFile(){
        String extension = null, type = null, example = null;
        try (XSSFWorkbook workbook = new XSSFWorkbook (getClass().getResourceAsStream("/table_1.xlsx"))){
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                for (Cell cell : row) {
                    if(cell == null || cell.getCellTypeEnum() == CellType.BLANK)
                        cell = getFirstCellOfMergedCells(sheet, cell);
                    String string = cell.getStringCellValue();
                    switch (cell.getColumnIndex()){
                        case 0:
                            extension = string;
                            break;
                        case 1:
                            type = string;
                            break;
                        case 2:
                            example = string;
                    }
                }
                dataObservableList.add(new RowData(extension, type, example));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Cell getFirstCellOfMergedCells(XSSFSheet sheet, Cell cell){
        for (int i = 0; i < sheet.getNumMergedRegions(); i++){
            CellRangeAddress region = sheet.getMergedRegion(i);
            if (region.isInRange(cell)){
                return CellUtil.getCell(sheet.getRow(region.getFirstRow()), region.getFirstColumn());
            }
        }
        return cell;
    }
}
