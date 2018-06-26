import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class Main extends Application {
    private static final Rectangle2D VISUAL_BOUNDS = Screen.getPrimary().getVisualBounds();
    private static final double WINDOW_WIDTH = VISUAL_BOUNDS.getWidth();
    private static final double WINDOW_HEIGHT = VISUAL_BOUNDS.getHeight();

    private Game game = new Game(WINDOW_WIDTH * 0.8, WINDOW_HEIGHT * 0.8 * 0.9);
    private Stage primaryStage;
    private Scene menuScene = createMenuScene();
    private Scene gameScene = createGameScene();

    private Scene createGameScene() {
        HBox gameMenuPane = new HBox();
        gameMenuPane.setAlignment(Pos.CENTER);
        gameMenuPane.setSpacing(50);
        gameMenuPane.getStyleClass().add("game-menu-pane");

        Button restartButton = new Button("Новая игра");
        restartButton.setOnAction(event -> {
            game.closeGameResultStage();
            game.restart();
        });
        gameMenuPane.getChildren().add(restartButton);

        Button openAllButton = new Button("Открыть все");
        openAllButton.setOnAction(event -> game.openAll());
        gameMenuPane.getChildren().add(openAllButton);

        Button exitToMenuButton = new Button("Выход в меню");
        exitToMenuButton.setOnAction(event -> {
            game.closeGameResultStage();
            primaryStage.setScene(menuScene);
        });
        gameMenuPane.getChildren().add(exitToMenuButton);

        Label remainingNOfMarksLabel = game.getRemainingNOfMarksLabel();
        gameMenuPane.getChildren().add(remainingNOfMarksLabel);

        GridPane gamePane = new GridPane();
        gamePane.setPadding(new Insets(50));
        gamePane.setHgap(50);
        gamePane.setVgap(50);
        gamePane.setAlignment(Pos.CENTER);
        gamePane.add(gameMenuPane, 0, 0);
        gamePane.add(game.getGroup(), 0, 1);

        Scene gameScene = new Scene(gamePane, WINDOW_WIDTH, WINDOW_HEIGHT);
        gameScene.getStylesheets().add("styles.css");

        return gameScene;
    }

    private Scene createMenuScene() {
        GridPane mainMenuPane = new GridPane();
        mainMenuPane.setPadding(new Insets(200));
        mainMenuPane.setVgap(40);
        mainMenuPane.setAlignment(Pos.TOP_LEFT);
        mainMenuPane.getStyleClass().add("main-menu-pane");

        GridPane mainButtonsPane = new GridPane();
        mainButtonsPane.setHgap(40);

        Button startButton = new Button("Новая игра");
        Button continueButton = new Button("Продолжить");
        Button exitButton = new Button("Выход");

        startButton.setOnAction(event -> {
            game.restart();
            continueButton.setDisable(false);
            primaryStage.setScene(gameScene);
        });
        mainButtonsPane.add(startButton, 0, 0);

        continueButton.setDisable(true);
        continueButton.setOnAction(event -> primaryStage.setScene(gameScene));
        mainButtonsPane.add(continueButton, 1, 0);

        exitButton.setOnAction(event -> System.exit(0));
        mainButtonsPane.add(exitButton, 2, 0);

        GridPane choosePane = new GridPane();
        choosePane.setHgap(30);
        choosePane.setVgap(30);
        choosePane.getStyleClass().add("choose-pane");

        Label choiceModeAnnotation = new Label("Уровень сложности:");
        choosePane.add(choiceModeAnnotation, 0, 0);

        ToggleGroup choiceMode = new ToggleGroup();

        ToggleButton easyModeItem = new ToggleButton("Простой");
        ToggleButton mediumModeItem = new ToggleButton("Средний");
        ToggleButton hardModeItem = new ToggleButton("Сложный");

        easyModeItem.setOnAction(event -> {
            continueButton.setDisable(true);
            game.setGameMode("easy");
            if (!easyModeItem.isSelected()) {
                game.setGameMode(null);
            }
        });
        easyModeItem.setToggleGroup(choiceMode);
        choosePane.add(easyModeItem, 1, 0);

        mediumModeItem.setOnAction(event -> {
            continueButton.setDisable(true);
            game.setGameMode("medium");
            if (!easyModeItem.isSelected()) {
                game.setGameMode(null);
            }
        });
        mediumModeItem.setToggleGroup(choiceMode);
        choosePane.add(mediumModeItem, 2, 0);

        hardModeItem.setOnAction(event -> {
            continueButton.setDisable(true);
            game.setGameMode("hard");
            if (!easyModeItem.isSelected()) {
                game.setGameMode(null);
            }
        });
        hardModeItem.setToggleGroup(choiceMode);
        choosePane.add(hardModeItem, 3, 0);

        Label choiceWidthAnnotation = new Label("Ширина поля:");

        Label sliderValueLabel = new Label();

        Slider widthSlider = new Slider();
        widthSlider.setMin(30);
        widthSlider.setMax(40);
        widthSlider.setValue(36);
        widthSlider.setShowTickLabels(true);
        widthSlider.setMinWidth(350);
        game.setWidth((int) widthSlider.getValue());
        sliderValueLabel.setText(String.valueOf((int) widthSlider.getValue()));
        widthSlider.valueProperty().addListener(t -> {
            continueButton.setDisable(true);
            game.setWidth((int) widthSlider.getValue());
            sliderValueLabel.setText(String.valueOf((int) widthSlider.getValue()));
        });

        GridPane sliderPane = new GridPane();
        sliderPane.setHgap(30);
        sliderPane.add(widthSlider, 0, 0);
        sliderPane.add(sliderValueLabel, 1, 0);

        choosePane.add(choiceWidthAnnotation, 0, 1);
        choosePane.add(sliderPane, 1, 1, 3, 1);

        mainMenuPane.add(mainButtonsPane, 0, 0);
        mainMenuPane.add(choosePane, 0, 1);

        Scene menuScene = new Scene(mainMenuPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        menuScene.getStylesheets().add("styles.css");

        return menuScene;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Сапёр");
        primaryStage.setMaximized(true);
        primaryStage.setScene(menuScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
