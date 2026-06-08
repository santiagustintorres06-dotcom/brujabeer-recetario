package com.brujabeer.recetario.ui.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Utilitario para cargar escenas FXML de forma Spring-aware.
 * Usar esto cuando agregues nuevas pantallas (RecipeList, RecipeDetail, etc.)
 */
@Component
public class SceneLoader {

    @Autowired
    private ApplicationContext applicationContext;

    public Parent loadFxml(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(applicationContext::getBean);
        return loader.load();
    }

    public void navigateTo(Stage stage, String fxmlPath, String title) throws IOException {
        Parent root = loadFxml(fxmlPath);
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
