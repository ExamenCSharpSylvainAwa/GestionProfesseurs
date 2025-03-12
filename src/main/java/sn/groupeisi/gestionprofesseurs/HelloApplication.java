package sn.groupeisi.gestionprofesseurs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("pages/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 650, 440);
        stage.setTitle("Page de Connexion");        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Lancer l'application JavaFX
        launch(args);

        // L'EntityManager peut être utilisé dans les contrôleurs ou services, pas ici
    }
}
