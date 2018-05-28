package sample;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {
    private String gameMode;
    static final double ASPECT_RATIO = 16.0 / 9.0;
    private static final double WINDOW_WIDTH = 1500;
    private static final double WINDOW_HEIGHT = WINDOW_WIDTH / ASPECT_RATIO;

    private void createGameScene(Stage primaryStage) {
        Field field = new Field(gameMode, 100, WINDOW_WIDTH * 0.8);

        HBox gameMenuPane = new HBox();
        gameMenuPane.setAlignment(Pos.CENTER);
        gameMenuPane.setSpacing(50);

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(event -> field.restart());
        gameMenuPane.getChildren().add(restartButton);

        Button openAllButton = new Button("Open all");
        openAllButton.setOnAction(event -> field.openAll());
        gameMenuPane.getChildren().add(openAllButton);

        Button exitToMenuButton = new Button("Exit to menu");
        exitToMenuButton.setOnAction(event -> createMenuScene(primaryStage));
        gameMenuPane.getChildren().add(exitToMenuButton);

        Label remainingNOfMinesLabel = field.getRemainingNOfMinesLabel();
        gameMenuPane.getChildren().add(remainingNOfMinesLabel);

        GridPane gamePane = new GridPane();
        gamePane.setPadding(new Insets(50));
        gamePane.setHgap(50);
        gamePane.setVgap(50);
        gamePane.setAlignment(Pos.CENTER);
        gamePane.add(gameMenuPane, 0, 0);
        gamePane.add(field.getGroup(), 0, 1);

        Scene gameScene = new Scene(gamePane, WINDOW_WIDTH, WINDOW_HEIGHT);
        gameScene.getStylesheets().add("sample/styles.css");

        primaryStage.setScene(gameScene);
    }

    private void createMenuScene(Stage primaryStage) {
        GridPane root = new GridPane();
        root.setPadding(new Insets(200));
        root.setHgap(50);
        root.setAlignment(Pos.TOP_CENTER);

        Button startButton = new Button("Start");
        startButton.setOnAction(event -> createGameScene(primaryStage));
        root.add(startButton, 0, 0);

        ToggleGroup choiceMode = new ToggleGroup();

        ToggleButton easyModeItem = new ToggleButton("Easy");
        easyModeItem.setOnAction(event -> gameMode = "easy");
        easyModeItem.setToggleGroup(choiceMode);
        root.add(easyModeItem, 1, 0);

        ToggleButton mediumModeItem = new ToggleButton("Medium");
        mediumModeItem.setOnAction(event -> gameMode = "medium");
        mediumModeItem.setToggleGroup(choiceMode);
        root.add(mediumModeItem, 2, 0);

        ToggleButton hardModeItem = new ToggleButton("Hard");
        hardModeItem.setOnAction(event -> gameMode = "hard");
        hardModeItem.setToggleGroup(choiceMode);
        root.add(hardModeItem, 3, 0);

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> System.exit(0));
        root.add(exitButton, 4, 0);

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
