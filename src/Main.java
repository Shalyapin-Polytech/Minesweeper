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
    static final double ASPECT_RATIO = WINDOW_WIDTH / WINDOW_HEIGHT;
    private String gameMode;

    private Scene createGameScene(Stage primaryStage) {
        Game game = new Game(gameMode, WINDOW_WIDTH * 0.8);

        HBox gameMenuPane = new HBox();
        gameMenuPane.setAlignment(Pos.CENTER);
        gameMenuPane.setSpacing(50);
        gameMenuPane.getStyleClass().add("game-menu-pane");

        Button restartButton = new Button("Новая игра");
        restartButton.setOnAction(event -> game.restart());
        gameMenuPane.getChildren().add(restartButton);

        Button openAllButton = new Button("Открыть все");
        openAllButton.setOnAction(event -> game.openAll());
        gameMenuPane.getChildren().add(openAllButton);

        Button exitToMenuButton = new Button("Выход в меню");
        exitToMenuButton.setOnAction(event -> primaryStage.setScene(createMenuScene(primaryStage)));
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
        gameScene.getStylesheets().add("styles.css");

        return gameScene;
    }

    private Scene createMenuScene(Stage primaryStage) {
        GridPane mainMenuPane = new GridPane();
        mainMenuPane.setPadding(new Insets(200));
        mainMenuPane.setVgap(50);
        mainMenuPane.setAlignment(Pos.TOP_LEFT);
        mainMenuPane.getStyleClass().add("main-menu-pane");

        Button startButton = new Button("Новая игра");
        startButton.setOnAction(event -> primaryStage.setScene(createGameScene(primaryStage)));
        mainMenuPane.add(startButton, 0, 0);

        GridPane chooseModePane = new GridPane();
        chooseModePane.setHgap(50);

        ToggleGroup choiceMode = new ToggleGroup();

        ToggleButton easyModeItem = new ToggleButton("Простой");
        easyModeItem.setOnAction(event -> gameMode = "easy");
        easyModeItem.setToggleGroup(choiceMode);
        chooseModePane.add(easyModeItem, 0, 0);

        ToggleButton mediumModeItem = new ToggleButton("Средний");
        mediumModeItem.setOnAction(event -> gameMode = "medium");
        mediumModeItem.setToggleGroup(choiceMode);
        chooseModePane.add(mediumModeItem, 1, 0);

        ToggleButton hardModeItem = new ToggleButton("Сложный");
        hardModeItem.setOnAction(event -> gameMode = "hard");
        hardModeItem.setToggleGroup(choiceMode);
        chooseModePane.add(hardModeItem, 2, 0);

        mainMenuPane.add(chooseModePane, 0, 1);

        Button exitButton = new Button("Выход");
        exitButton.setOnAction(event -> System.exit(0));
        mainMenuPane.add(exitButton, 0, 2);

        Scene menuScene = new Scene(mainMenuPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        menuScene.getStylesheets().add("styles.css");

        return menuScene;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Сапёр");
        primaryStage.setMaximized(true);
        primaryStage.setScene(createMenuScene(primaryStage));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
