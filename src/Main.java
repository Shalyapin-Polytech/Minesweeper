import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class Main extends Application {
    private Game game = new Game();
    private Stage primaryStage;
    private Scene menuScene = createMenuScene();
    private Scene gameScene = createGameScene();

    private static final Rectangle2D VISUAL_BOUNDS = Screen.getPrimary().getVisualBounds();
    static final double WINDOW_WIDTH = VISUAL_BOUNDS.getWidth();
    private static final double WINDOW_HEIGHT = VISUAL_BOUNDS.getHeight();
    static final double ASPECT_RATIO = WINDOW_WIDTH / WINDOW_HEIGHT;

    private Scene createGameScene() {
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
        exitToMenuButton.setOnAction(event -> primaryStage.setScene(menuScene));
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

        GridPane chooseModePane = new GridPane();
        chooseModePane.setHgap(30);
        chooseModePane.getStyleClass().add("choose-pane");

        Label choiceModeAnnotation = new Label("Уровень сложности:");
        chooseModePane.add(choiceModeAnnotation, 0, 0);

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
        chooseModePane.add(easyModeItem, 1, 0);

        mediumModeItem.setOnAction(event -> {
            continueButton.setDisable(true);
            game.setGameMode("medium");
            if (!easyModeItem.isSelected()) {
                game.setGameMode(null);
            }
        });
        mediumModeItem.setToggleGroup(choiceMode);
        chooseModePane.add(mediumModeItem, 2, 0);

        hardModeItem.setOnAction(event -> {
            continueButton.setDisable(true);
            game.setGameMode("hard");
            if (!easyModeItem.isSelected()) {
                game.setGameMode(null);
            }
        });
        hardModeItem.setToggleGroup(choiceMode);
        chooseModePane.add(hardModeItem, 3, 0);

        GridPane chooseWidthPane = new GridPane();
        chooseWidthPane.setHgap(30);
        chooseWidthPane.getStyleClass().add("choose-pane");

        Label choiceWidthAnnotation = new Label("Ширина поля:");

        Label sliderValueLabel = new Label();

        Slider widthSlider = new Slider(30, 40, 1);
        widthSlider.setBlockIncrement(1);
        widthSlider.setValue(36);
        sliderValueLabel.setText(String.valueOf((int) widthSlider.getValue()));
        widthSlider.setShowTickLabels(true);
        widthSlider.setShowTickMarks(true);
        widthSlider.valueProperty().addListener(t -> {
            game.setWidth((int) widthSlider.getValue());
            sliderValueLabel.setText(String.valueOf((int) widthSlider.getValue()));
        });

        chooseWidthPane.add(choiceWidthAnnotation, 0, 0);
        chooseWidthPane.add(widthSlider, 1, 0);
        chooseWidthPane.add(sliderValueLabel, 2, 0);

        mainMenuPane.add(mainButtonsPane, 0, 0);
        mainMenuPane.add(chooseModePane, 0, 1);
        mainMenuPane.add(chooseWidthPane, 0, 2);

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
