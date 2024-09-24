package controller;

import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tm.ToDoTM;


import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class ToDoFormController {


    public Label lblTitle;
    public Label lblUserID;
    public AnchorPane root;
    public TextField txtDescription;
    public Pane subRoot;
    public ListView <ToDoTM>lstToDo;
    public TextField txtSelectedToDo;
    public Button btnDelete;
    public Button btnUpdate;
    private PreparedStatement preparedStatement;

    public void initialize(){
        lblTitle.setText("Hi " + LoginFormController.loginUserName + " Welcome to To-Do List ");
        lblUserID.setText(LoginFormController.loginUserID);

        subRoot.setVisible(false);

        loadList();

        setDisableCommon(true);

        lstToDo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoTM>() {
            @Override
            public void changed(ObservableValue<? extends ToDoTM> observable, ToDoTM oldValue, ToDoTM newValue) {

                if(lstToDo.getSelectionModel().getSelectedItems() == null){
                    return;
                }

                setDisableCommon(false);

               subRoot.setVisible(false);

               txtSelectedToDo.setText(lstToDo.getSelectionModel().getSelectedItem().getDescription());
            }
        });

    }

    public void setDisableCommon(boolean isDisable){
        txtSelectedToDo.setDisable(isDisable);
        btnUpdate.setDisable(isDisable);
        btnDelete.setDisable(isDisable);

        txtSelectedToDo.clear();
    }

    public void btnLogOutOnAction(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do You want to Log Out..?" , ButtonType.YES,ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){
            Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));
            Scene scene = new Scene(parent);

            Stage primaryStage = (Stage) root.getScene().getWindow();

            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");
            primaryStage.centerOnScreen();

        }
    }


    public void btnAddNewToDoOnAction(ActionEvent actionEvent) {


        lstToDo.getSelectionModel().clearSelection();

        setDisableCommon(true);

        subRoot.setVisible(true);

        txtDescription.requestFocus();
    }

    public void btnAddToListOnAction(ActionEvent actionEvent) {

        String id = autoGenerateID();
        String description = txtDescription.getText();
        String userId = lblUserID.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            preparedStatement = connection.prepareStatement("insert into todo values (?,?,?)");
           // preparedStatement.setObject(1);

            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,description);
            preparedStatement.setObject(3,userId);

            preparedStatement.executeUpdate();

            System.out.println("Added");

            txtDescription.clear();
            subRoot.setVisible(false);

            loadList();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }

    public String autoGenerateID() {
        Connection connection = DBConnection.getInstance().getConnection();
        String id = "";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT id FROM todo ORDER BY id DESC LIMIT 1");

            if (resultSet.next()) {
                String lastID = resultSet.getString(1);  // Get the last ID, e.g., "T001"
                int intId = Integer.parseInt(lastID.substring(1));  // Extract the numeric part (e.g., "001" -> 1)
                intId++;  // Increment the numeric part

                // Format the new ID with leading zeros for IDs below 100
                id = String.format("T%03d", intId);  // Ensures format like T001, T010, T100, etc.
            } else {
                id = "T001";  // If no ID exists, start with T001
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return id;
    }


    public void loadList(){
        ObservableList<ToDoTM> todoS = lstToDo.getItems();
        todoS.clear();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            preparedStatement = connection.prepareStatement("select * from todo where user_id = ?");
            preparedStatement.setObject(1,lblUserID.getText());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){

                String id = resultSet.getString(1);
                String description = resultSet.getString(2);
                String user_id = resultSet.getString(3);


                todoS.add(new ToDoTM(id,description,user_id));
            }

            lstToDo.refresh();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }


    public void btnUpdateOnAction(ActionEvent actionEvent) {
        String description = txtSelectedToDo.getText();
        String id = lstToDo.getSelectionModel().getSelectedItem().getId();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update todo set description = ? where id = ?");
            preparedStatement.setObject(1,description);
            preparedStatement.setObject(2,id);

            preparedStatement.executeUpdate();

            loadList();

            setDisableCommon(true);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to delete this todo..?",ButtonType.YES,ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.get().equals(ButtonType.YES)){

            String id = lstToDo.getSelectionModel().getSelectedItem().getId();

            Connection connection = DBConnection.getInstance().getConnection();

            try {
                preparedStatement  = connection.prepareStatement("delete from todo where id = ? ");
                this.preparedStatement.setObject(1,id);

                this.preparedStatement.executeUpdate();

                loadList();

                setDisableCommon(true);

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }


    }
}
