package app;

import callback.impl.FileSave2DB;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import task.FileScanner;
import task.FileSearch;
import util.DBInit;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author xinyan
 * @date 2022/07/06 15:07
 **/
public class Controller implements Initializable {

    @FXML
    private GridPane rootPane;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<FileMeta> fileTable;

    @FXML
    private Label srcDirectory;

    private List<FileMeta> fileMetas;

    public void initialize(URL location, ResourceBundle resources) {
        DBInit.init();
        // 添加搜索框监听器，内容改变时执行监听事件
        searchField.textProperty().addListener(new ChangeListener<String>() {

            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                freshTable();
            }
        });
    }

    public void choose(Event event) {
        // 选择文件目录
        DirectoryChooser directoryChooser=new DirectoryChooser();
        Window window = rootPane.getScene().getWindow();
        File file = directoryChooser.showDialog(window);
        if(file == null)
            return;
        // 获取选择的目录路径，并显示
        String path = file.getPath();
        this.srcDirectory.setText(path);
        //获取扫描文件的路径后进行文件扫描.
        System.out.println("开始进行文件扫描任务,根路径为 : " + path);

        long start = System.nanoTime();
        FileScanner fileScanner = new FileScanner(new FileSave2DB());
        fileScanner.scan(file);
        long end = System.nanoTime();

        System.out.println("文件扫描任务结束,共耗时 : " + (end - start) * 1.0 / 1000000 + "ms");
        System.out.println("共扫描到 ： " + fileScanner.getDirNum() + "个文件夹");
        System.out.println("共扫描到 ： " + fileScanner.getFileNum() + "个文件");

        // TODO
        freshTable();
    }

    // 刷新表格数据
    private void freshTable(){
        ObservableList<FileMeta> metas = fileTable.getItems();
        metas.clear();
        // TODO 扫描文件夹之后刷新界面
        String dir = srcDirectory.getText();
        if (dir != null && dir.trim().length() != 0) {
            // 界面中已经选择了文件，此时已经将最新的数据保存到了数据库中，
            // 只需要取出数据库中的内容展示到界面上即可
            // 获取用户在搜索框中输入的内容
            String content = searchField.getText();
            // 根据选择的路径 + 用户的输入(若为空就展示所有内容) 将数据库中的指定内容刷新到界面中
            List<FileMeta> filesFromDB = FileSearch.search(dir,content);
            metas.addAll(filesFromDB);
        }
    }

}