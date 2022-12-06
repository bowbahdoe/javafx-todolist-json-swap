module example.todolist {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.eclipse.collections.api;
    requires org.eclipse.collections.impl;

    requires dev.mccue.json;
    requires dev.mccue.json.decode.alpha;
    opens example.todolist;
}