package example.todolist;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TodoListApplication extends Application
{
    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(new ByteArrayInputStream("""
                <?xml version="1.0" encoding="UTF-8"?>
                                
                <?import javafx.geometry.Insets?>
                <?import javafx.scene.control.Button?>
                <?import javafx.scene.control.cell.PropertyValueFactory?>
                <?import javafx.scene.control.ComboBox?>
                <?import javafx.scene.control.DatePicker?>
                <?import javafx.scene.control.Label?>
                <?import javafx.scene.control.TableColumn?>
                <?import javafx.scene.control.TableView?>
                <?import javafx.scene.control.TextField?>
                <?import javafx.scene.layout.HBox?>
                <?import javafx.scene.layout.VBox?>
                <VBox alignment="CENTER" spacing="20.0" xmlns:fx="http://javafx.com/fxml"
                      fx:controller="example.todolist.TodoListController">
                    <padding>
                        <Insets bottom="20.0" left="20.0" right="20.0" top="20.0" />
                    </padding>
                                
                    <HBox id="HBox1" alignment="CENTER_LEFT" spacing="5.0">
                        <Label text="Item: " />
                        <TextField fx:id="todoItem" />
                        <Label text="Category: " />
                        <ComboBox fx:id="todoCategory" />
                        <Label text="Date: " />
                        <DatePicker fx:id="todoDate" />
                    </HBox>
                    <TableView fx:id="todoList">
                        <columns>
                            <TableColumn text="Name" minWidth="75.0" sortable="true">
                                <cellValueFactory>
                                    <PropertyValueFactory property="name" />
                                </cellValueFactory>
                            </TableColumn>
                            <TableColumn text="Category" minWidth="50.0" sortable="true">
                                <cellValueFactory>
                                    <PropertyValueFactory property="category" />
                                </cellValueFactory>
                            </TableColumn>
                            <TableColumn text="Date" minWidth="50.0" sortable="true">
                                <cellValueFactory>
                                    <PropertyValueFactory property="date" />
                                </cellValueFactory>
                            </TableColumn>
                        </columns>
                    </TableView>
                    <HBox id="HBox2" alignment="CENTER" spacing="5.0">
                        <Button text="Add" onAction="#onAddButtonClick" alignment="BOTTOM_LEFT" />
                        <Button text="Remove" onAction="#onRemoveButtonClick" alignment="BOTTOM_RIGHT" />
                    </HBox>
                </VBox>
                """.getBytes(StandardCharsets.UTF_8)));
        TodoListController controller = fxmlLoader.getController();
        Scene scene = new Scene(root, 640, 480);
        stage.setTitle("Todo List");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            controller.writeToDoListToFile();
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args)
    {
        TodoListApplication.launch();
    }
}