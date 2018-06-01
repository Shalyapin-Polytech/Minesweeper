package sample;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class Main extends Application {
    private static final Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
    private static final double WINDOW_WIDTH = bounds.getWidth() * 0.8;
    private static final double WINDOW_HEIGHT = bounds.getHeight() * 0.8;
    static final double ASPECT_RATIO = WINDOW_WIDTH / WINDOW_HEIGHT;
    private String gameMode;

    private void createGameScene(Stage primaryStage) {
        Game game = new Game(gameMode, WINDOW_WIDTH * 0.8);

        HBox gameMenuPane = new HBox();
        gameMenuPane.setAlignment(Pos.CENTER);
        gameMenuPane.setSpacing(50);

        Button restartButton = new Button("Новая игра");
        restartButton.setOnAction(event -> game.restart());
        gameMenuPane.getChildren().add(restartButton);

        Button openAllButton = new Button("Открыть все");
        openAllButton.setOnAction(event -> game.openAll());
        gameMenuPane.getChildren().add(openAllButton);

        Button exitToMenuButton = new Button("Выход в меню");
        exitToMenuButton.setOnAction(event -> createMenuScene(primaryStage));
        gameMenuPane.getChildren().add(exitToMenuButton);

        gameMenuPane.getChildren().add(game.getRemainingNOfMarksLabel());

        GridPane gamePane = new GridPane();
        gamePane.setPadding(new Insets(50));
        gamePane.setHgap(50);
        gamePane.setVgap(50);
        gamePane.setAlignment(Pos.CENTER);
        gamePane.add(gameMenuPane, 0, 0);
        gamePane.add(game.getGroup(), 0, 1);

        Scene gameScene = new Scene(gamePane, WINDOW_WIDTH, WINDOW_HEIGHT);
        gameScene.getStylesheets().add("sample/styles.css");

        primaryStage.setScene(gameScene);
    }

    private void createMenuScene(Stage primaryStage) {
        GridPane root = new GridPane();
        root.setPadding(new Insets(200));
        root.setVgap(50);
        root.setAlignment(Pos.TOP_LEFT);

        Button startButton = new Button("Новая игра");
        startButton.setOnAction(event -> createGameScene(primaryStage));
        root.add(startButton, 0, 0);

        GridPane chooseModeButtons = new GridPane();
        chooseModeButtons.setHgap(50);

        ToggleGroup choiceMode = new ToggleGroup();

        ToggleButton easyModeItem = new ToggleButton("Простой");
        easyModeItem.setOnAction(event -> gameMode = "easy");
        easyModeItem.setToggleGroup(choiceMode);
        chooseModeButtons.add(easyModeItem, 0, 0);

        ToggleButton mediumModeItem = new ToggleButton("Средний");
        mediumModeItem.setOnAction(event -> gameMode = "medium");
        mediumModeItem.setToggleGroup(choiceMode);
        chooseModeButtons.add(mediumModeItem, 1, 0);

        ToggleButton hardModeItem = new ToggleButton("Сложный");
        hardModeItem.setOnAction(event -> gameMode = "hard");
        hardModeItem.setToggleGroup(choiceMode);
        chooseModeButtons.add(hardModeItem, 2, 0);

        root.add(chooseModeButtons, 0, 1);

        Button exitButton = new Button("Выход");
        exitButton.setOnAction(event -> System.exit(0));
        root.add(exitButton, 0, 2);

        Scene menuScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        menuScene.getStylesheets().add("sample/styles.css");

        primaryStage.setScene(menuScene);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Сапёр");
        createMenuScene(primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
