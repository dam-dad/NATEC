package dad.game.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import dad.App;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;

public class MenuPrincipalController implements Initializable {

	@FXML
	private VBox root;

	Scene scene;

	public MenuPrincipalController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuPrincipalView.fxml"));
		loader.setController(this);
		loader.load();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		root.setBackground(
				new Background(new BackgroundImage(new Image("/imagenes/niveles/menuSeleccionarNivelOscuro.jpg"),
						BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
						new BackgroundSize(100, 100, true, true, true, true))));
	}

	private void pulsarTecla(String key) {
		if (key.equals("UP") || key.equals("RIGHT")) {
			
		} else if (key.equals("DOWN") || key.equals("LEFT")) {
			
		}
	}

	@FXML
	void onAjustesAction(ActionEvent event) {

	}

	@FXML
	void onIniciarPartidaAction(ActionEvent event) throws IOException {
		App.startMenuNivelController();
	}

	@FXML
	void onSalirJuegoAction(ActionEvent event) {
		System.exit(0);
	}

	public Scene getScene() {
		return new Scene(root);
	}

}
