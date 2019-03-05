import data.RowData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellUtil;
import view.MyComboBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private TextField textField;
    @FXML
    private Button button;
    @FXML
    private Button apply;
    @FXML
    private TableView<RowData> table_1;
    @FXML
    private TableColumn<RowData, String> tab_1_extension;
    @FXML
    private TableColumn<RowData, String> tab_1_type;
    @FXML
    private TableColumn<RowData, String> tab_1_example;
    @FXML
    private TableView table_2;
    @FXML
    private TableColumn tab_2_extension;
    @FXML
    private TableColumn tab_2_type;
    @FXML
    private TableColumn tab_2_example;

    private final ObservableList<RowData> dataObservableList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MyComboBox myComboBox = new MyComboBox(textField, button);
        table_1.setItems(dataObservableList);
        myComboBox.setItems(dataObservableList);
        tab_1_extension.setCellValueFactory(new PropertyValueFactory <> ("extension"));
        tab_1_type.setCellValueFactory (new PropertyValueFactory <> ("type"));
        tab_1_example.setCellValueFactory(new PropertyValueFactory <> ("example"));

        readFile();

    }

    private void readFile(){
        String extension = null, type = null, example = null;
        try (FileInputStream fileInputStream = new FileInputStream(new File("table_1.xlsx"))){
            XSSFWorkbook workbook = new XSSFWorkbook (fileInputStream);
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
        }catch (IOException e){
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
