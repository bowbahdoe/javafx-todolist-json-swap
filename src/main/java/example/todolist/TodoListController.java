package example.todolist;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import dev.mccue.json.Json;
import dev.mccue.json.JsonReadException;
import dev.mccue.json.ToJson;
import dev.mccue.json.decode.alpha.Decoder;
import dev.mccue.json.decode.alpha.JsonDecodingException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.collector.Collectors2;

public class TodoListController
{
    @FXML
    private TextField todoItem;

    @FXML
    public ComboBox<ToDoCategory> todoCategory;

    @FXML
    private DatePicker todoDate;

    @FXML
    private TableView<ToDoItem> todoList;

    @FXML
    protected void initialize()
    {
        MutableList<ToDoItem> items = this.readToDoListFromFile();
        ObservableList<ToDoItem> list = FXCollections.observableList(items);

        this.todoList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.todoList.setItems(list);

        ObservableList<ToDoCategory> categories =
                FXCollections.observableList(
                        Lists.mutable.with(ToDoCategory.values()));

        this.todoCategory.setItems(categories);
    }

    @FXML
    protected void onAddButtonClick()
    {
        String text = this.todoItem.getText();
        ToDoCategory category = this.todoCategory.getValue();
        LocalDate date = this.todoDate.getValue();
        if (text == null || category == null || date == null)
        {
            this.displayInvalidInputMessage();
        }
        else
        {
            this.createAndAddToDoItem(text, category, date);
        }
    }

    private void displayInvalidInputMessage()
    {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("Invalid input");
        errorAlert.setContentText("Text, category and date must all be specified.");
        errorAlert.showAndWait();
    }

    private void createAndAddToDoItem(String text, ToDoCategory category, LocalDate date)
    {
        ToDoItem item = new ToDoItem(text, category, date);
        this.todoList.getItems().add(item);
    }

    @FXML
    protected void onRemoveButtonClick()
    {
        int indexToRemove = this.todoList.getSelectionModel().getSelectedIndex();
        this.todoList.getItems().remove(indexToRemove);
    }

    private MutableList<ToDoItem> readToDoListFromFile()
    {
        try (var reader = new FileReader(Paths.get("todolist.json").toFile()))
        {
            var json = Json.read(reader);
            return Decoder.array(json, ToDoItem::fromJson)
                    .stream()
                    .collect(Collectors2.toList());
        }
        catch (IOException |  JsonReadException | JsonDecodingException e)
        {
            System.out.println(e);
        }
        return Lists.mutable.empty();
    }

    public void writeToDoListToFile()
    {
        Json list = Json.of(this.todoList.getItems());
        try (var writer = new FileWriter(Paths.get("todolist.json").toFile()))
        {
            Json.write(list, writer, new Json.WriteOptions().withIndentation(4));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public record ToDoItem(
            String name,
            ToDoCategory category,
            LocalDate date)
        implements ToJson
    {
        public String getCategory()
        {
            return this.category.getEmoji();
        }

        public String getName()
        {
            return this.name;
        }

        public String getDate()
        {
            return this.date.toString();
        }

        @Override
        public Json toJson() {
            return Json.objectBuilder()
                    .put("name", name)
                    .put("category", category)
                    .put("date", DateTimeFormatter.ISO_DATE.format(date))
                    .build();
        }

        public static ToDoItem fromJson(Json json) {
            return new ToDoItem(
                    Decoder.field(json, "name", Decoder::string),
                    Decoder.field(json, "category", ToDoCategory::fromJson),
                    Decoder.field(json, "date", date ->
                            LocalDate.parse(Decoder.string(date), DateTimeFormatter.ISO_DATE)
                    )
            );
        }
    }

    public enum ToDoCategory implements ToJson
    {
        EXERCISE("🚴"),
        WORK("📊"),
        RELAX("🧘"),
        TV("📺"),
        READ("📚"),
        EVENT("🎭"),
        CODE("💻"),
        COFFEE("☕️"),
        EAT("🍽"),
        SHOP("🛒"),
        SLEEP("😴");

        private String emoji;

        ToDoCategory(String emoji)
        {
            this.emoji = emoji;
        }

        public String getEmoji()
        {
            return this.emoji;
        }

        @Override
        public Json toJson() {
            return Json.of(this.toString());
        }

        public static ToDoCategory fromJson(Json json) {
            return ToDoCategory.valueOf(Decoder.string(json));
        }
    }
}