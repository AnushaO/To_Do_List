package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class CreateNewAccountFormController {
    public PasswordField txtNewPassword;
    public PasswordField txtConfirmPassword;
    public Label lblPasswordNotMatch1;
    public Label lblPasswordNotMatch2;
    public TextField txtUserName;
    public Button btnAddNewUser;
    public Button btnRegister;
    public TextField txtEmail;
    public Label lblid;
    public AnchorPane root;

    // Initialize method to set focus and hide error messages
    public void initialize(){
        setVisibility(false);

     setDisableCommon(true);
    }

    // Action handler for Register button
    public void btnRegisterOnAction(ActionEvent actionEvent) {
        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if(newPassword.equals(confirmPassword)){
            System.out.println("Passwords Match");

            // Hide the error labels and reset border colors
            setVisibility(false);
            setBorderColor("transparent");

            register();

        } else {
            System.out.println("Passwords Do Not Match");

            // Show the error labels and set red border for password fields
            setBorderColor("red");
            setVisibility(true);

            // Refocus to the first password field for correction
            txtNewPassword.requestFocus();
        }


        Connection connection =  DBConnection.getInstance().getConnection();

        System.out.println(connection);
    }

    // Method to change the border color of password fields
    public void setBorderColor(String color){
        txtNewPassword.setStyle("-fx-border-color: " + color);
        txtConfirmPassword.setStyle("-fx-border-color: " + color);
    }

    // Method to set visibility of error labels
    public void setVisibility(boolean isVisible){
        lblPasswordNotMatch1.setVisible(isVisible);
        lblPasswordNotMatch2.setVisible(isVisible);
    }

    public void btnAddNewUserOnAction(ActionEvent actionEvent) {
       setDisableCommon(false);

       txtUserName.requestFocus();

       autoGenerateID();


    }

    public void setDisableCommon(boolean isDisable){
        txtUserName.setDisable(isDisable);
        txtEmail.setDisable(isDisable);
        txtNewPassword.setDisable(isDisable);
        txtConfirmPassword.setDisable(isDisable);
        btnRegister.setDisable(isDisable);
    }

    public void autoGenerateID() {
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select uid from user order by uid desc limit 1");

            boolean isExist = resultSet.next();

            if (isExist) {
                String userID = resultSet.getString(1);

                userID = userID.substring(1,userID.length());

                int intId = Integer.parseInt(userID);

                intId++;

                if(intId < 10) {
                    lblid.setText("U00" + intId);
                }else if(intId < 100){
                    lblid.setText("U0" + intId);
                }else{
                    lblid.setText("U" + intId);
                }




            } else {
                lblid.setText("U001");  // If no user exists, set the first ID
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }



    public void register(){



        String id = lblid.getText();
        String userName = txtUserName.getText();
        String email = txtEmail.getText();
        String password = txtConfirmPassword.getText();

        if (userName.trim().isEmpty()){
            txtUserName.requestFocus();
        }else if(email.trim().isEmpty()){
            txtEmail.requestFocus();
        }else if(txtNewPassword.getText().trim().isEmpty()){
            txtNewPassword.requestFocus();
        }else if(password.trim().isEmpty()){
            txtConfirmPassword.requestFocus();
        }else{
            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("insert into user values(?,?,?,?)");
                preparedStatement.setObject(1,id);
                preparedStatement.setObject(2,userName);
                preparedStatement.setObject(3,email);
                preparedStatement.setObject(4,password);

                preparedStatement.executeUpdate();

                Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));
                Scene scene = new Scene(parent);

                Stage primaryStage = (Stage) root.getScene().getWindow();

                primaryStage.setScene(scene);
                primaryStage.setTitle("Login");
                primaryStage.centerOnScreen();

            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
          }
        }
    }
}
