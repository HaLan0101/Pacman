package com.example.pacman;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.*;


public class HelloApplication extends Application {

    // set value
    static final int WIDTH = 1000;
    static final int HEIGHT = 800;
    static int scoreint = 0;
    int indexActiveSceneGame = 0;
    boolean gamePaused = true;
    boolean pacManVoracious = false;
    String pseudoPlayer;

    //set list
    List<Circle> listGhost;
    List<Circle> listPoint;
    List<Scene> listScenesGame;
    List<Group> listGroupGame;
    List<Circle> listCerises;

    // set pacman circle form
    Circle pacman;

    // set group
    Group groupMenuPage;
    Group groupGamePage;
    Group groupHighScorePage;

    // text score and name player
    Text score;
    Text highscores;
    TextArea inputPseudo;

    //set Button
    Button buttonPauseSceneGame;
    Button buttonGoToHighScorePage;
    Button buttonStartMainMenu;
    Button buttonReturnMainMenu;
    Button buttonReturnToGameFromPause;

    //set Scene
    Scene sceneMenu;
    Scene sceneGameInitial;
    Scene sceneHighScorePage;
    VBox vboxMenu;

    //set Time
    Timeline tl;
    Timer timerCerise = new Timer();

    //set Stage
    Stage primaryStage;

    //Score
    TableView tableHighScore;
    ArrayList<Map<String, Object>> scores;


    @Override
    public void start(Stage stage) throws Exception{
        listScenesGame = new ArrayList<Scene>();
        listGroupGame = new ArrayList<Group>();
        primaryStage = stage;
        primaryStage.setTitle("Pacman");

        //SCENE MENU DEFINITION
        groupMenuPage = new Group();
        buttonStartMainMenu = new Button("Start new GAME!");
        buttonGoToHighScorePage = new Button("Go to highscore!");
        buttonReturnToGameFromPause = new Button("Return to game");
        inputPseudo = new TextArea();
        inputPseudo.setPrefHeight(100);
        inputPseudo.setPrefWidth(100);
        groupMenuPage.setLayoutX(WIDTH/2);
        groupMenuPage.setLayoutY(HEIGHT/2);
        vboxMenu = new VBox(buttonStartMainMenu, buttonGoToHighScorePage, inputPseudo);
        groupMenuPage.getChildren().add(vboxMenu);


        sceneMenu = new Scene(groupMenuPage, WIDTH, HEIGHT, Color.BLACK);
        Image imLaurier = new Image("https://i.pinimg.com/originals/68/43/cc/6843cc365df18febde115bc70eb15290.gif",false);
        sceneMenu.setFill(new ImagePattern(imLaurier));

        primaryStage.setScene(sceneMenu);
        //END SCENE MENU DEFINITION

        //SCENE GAME DEFINITION
        Rectangle rContour = new Rectangle(0,0 ,WIDTH,HEIGHT);
        rContour.setFill(Color.TRANSPARENT);
        rContour.setStroke(Color.PURPLE);
        rContour.setStrokeWidth(10);
        groupGamePage = initializeGroupGame();
        listGroupGame.add(groupGamePage);

        sceneGameInitial = new Scene(groupGamePage, WIDTH, HEIGHT, Color.BLACK);
        listScenesGame.add(sceneGameInitial);

        tl = new Timeline(new KeyFrame(Duration.millis(250), e -> run()));
        tl.setCycleCount(Timeline.INDEFINITE);


        handleGameEvent();

        //HighScorePage
        buttonReturnMainMenu = new Button("Return to menu!");
        highscores = new Text();
        highscores.setX(200);
        highscores.setY(200);
        highscores.setFill(Color.WHITE);
        groupHighScorePage = new Group();
        groupHighScorePage.getChildren().add(buttonReturnMainMenu);
        groupHighScorePage.getChildren().add(highscores);
        tableHighScore = new TableView();
        scores = new ArrayList<Map<String, Object>>();

        TableColumn<Map, String> col1 = new TableColumn<>("userName");
        col1.setCellValueFactory(new MapValueFactory<>("userName"));

        TableColumn<Map, String> col2 = new TableColumn<>("score");
        col2.setCellValueFactory(new MapValueFactory<>("score"));

        for (Map<String, Object> item:scores) {
            tableHighScore.getItems().addAll(item);
        }
        tableHighScore.getColumns().add(col1);
        tableHighScore.getColumns().add(col2);
        tableHighScore.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableHighScore.setLayoutX(WIDTH/4);
        tableHighScore.setLayoutY(HEIGHT/4);
        groupHighScorePage.getChildren().add(tableHighScore);
        sceneHighScorePage = new Scene(groupHighScorePage, WIDTH, HEIGHT, Color.BLACK);
        sceneHighScorePage.setFill(new ImagePattern(imLaurier));
        primaryStage.show();

        buttonStartMainMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //TODO init game scene
                primaryStage.setScene(listScenesGame.get(indexActiveSceneGame));
                gamePaused = false;
                tl.play();
            }
        });

        buttonReturnToGameFromPause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.setScene(listScenesGame.get(indexActiveSceneGame));
                gamePaused = false;
                tl.play();
                vboxMenu.getChildren().remove(buttonReturnToGameFromPause);
            }
        });

        buttonGoToHighScorePage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.setScene(sceneHighScorePage);
                gamePaused = true;
                tl.pause();
            }
        });

        buttonReturnMainMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.setScene(sceneMenu);
                gamePaused = true;
                tl.pause();
            }
        });
    }

    private void handleGameEvent() {
        listScenesGame.get(indexActiveSceneGame).setOnKeyPressed((KeyEvent event) -> {
            if (event.getText().isEmpty())
                return;
            char keyEntered = event.getText().toUpperCase().charAt(0);
            boolean isMouvOk = !gamePaused;
            switch (keyEntered){
                case 'Z' :
                    pacman.setRotate(-90);
                    for (Node node : listGroupGame.get(indexActiveSceneGame).getChildren()) {
                        if( node instanceof Rectangle){
                            Rectangle r = ((Rectangle) node);
                            if((pacman.getCenterX()>= r.getX() && pacman.getCenterX()<=r.getX()+r.getWidth())){
                                if(pacman.getCenterY()-pacman.getRadius() <= r.getY() + r.getHeight() && pacman.getCenterY()>=r.getY()){
                                    isMouvOk = false;
                                }
                            }
                        }
                    }
                    if(isMouvOk){
                        if (pacman.getCenterY() <= 0) {
                            pacman.setCenterY(HEIGHT + pacman.getRadius());
                        }
                        isNextPositionAPoint(listGroupGame.get(indexActiveSceneGame), listPoint, pacman, score);
                        pacman.setCenterY(pacman.getCenterY() - pacman.getRadius());
                    }
                    break;
                case 'S' :
                    pacman.setRotate(90);
                    for (Node node : listGroupGame.get(indexActiveSceneGame).getChildren()) {
                        if( node instanceof Rectangle){
                            Rectangle r = ((Rectangle) node);
                            if((pacman.getCenterX()>= r.getX() && pacman.getCenterX()<=r.getX()+r.getWidth())){
                                if(pacman.getCenterY() <= r.getY() + r.getHeight() &&
                                        pacman.getCenterY()+ pacman.getRadius()>=r.getY()){
                                    System.out.println("Bam");
                                    isMouvOk = false;
                                }
                            }
                        }
                    }
                    if(isMouvOk) {
                        if (pacman.getCenterY() >= HEIGHT) {
                            pacman.setCenterY(0 - pacman.getRadius());
                        }
                        isNextPositionAPoint(listGroupGame.get(indexActiveSceneGame), listPoint, pacman, score);
                        pacman.setCenterY(pacman.getCenterY() + pacman.getRadius());
                    }
                    break;
                case 'Q' :
                    pacman.setRotate(180);
                    for (Node node : listGroupGame.get(indexActiveSceneGame).getChildren()) {
                        if( node instanceof Rectangle){
                            Rectangle r = ((Rectangle) node);
                            if(pacman.getCenterY() <= r.getY() + r.getHeight() &&
                                    pacman.getCenterY()>=r.getY()){
                                if((pacman.getCenterX()>= r.getX() && pacman.getCenterX()-pacman.getRadius()<=r.getX()+r.getWidth())){
                                    isMouvOk = false;
                                }
                            }
                        }
                    }
                    if(isMouvOk) {
                        if (pacman.getCenterX() <= 0) {
                            pacman.setCenterX(WIDTH + pacman.getRadius());
                        }
                        isNextPositionAPoint(listGroupGame.get(indexActiveSceneGame), listPoint, pacman, score);
                        pacman.setCenterX(pacman.getCenterX() - pacman.getRadius());
                    }
                    break;
                case 'D' :
                    pacman.setRotate(0);
                    for (Node node : listGroupGame.get(indexActiveSceneGame).getChildren()) {
                        if( node instanceof Rectangle){
                            Rectangle r = ((Rectangle) node);
                            if(pacman.getCenterY() <= r.getY() + r.getHeight() &&
                                    pacman.getCenterY()>=r.getY()){
                                if((pacman.getCenterX()+pacman.getRadius()>= r.getX() && pacman.getCenterX()-pacman.getRadius()<=r.getX())){
                                    isMouvOk = false;
                                }
                            }
                        }
                    }
                    if(isMouvOk) {
                        if (pacman.getCenterX() >= WIDTH) {
                            pacman.setCenterX(0 - pacman.getRadius());
                        }
                        isNextPositionAPoint(listGroupGame.get(indexActiveSceneGame), listPoint, pacman, score);
                        pacman.setCenterX(pacman.getCenterX() + pacman.getRadius());
                    }
                    break;
                case 'X' :
                    primaryStage.setScene(sceneMenu);
                    break;
                case 'P':
                    if(tl.getStatus() == Animation.Status.RUNNING){
                        tl.pause();
                        gamePaused = true;
                    }
                    else if(tl.getStatus() == Animation.Status.PAUSED){
                        tl.play();
                        gamePaused = false;
                    }
            }
        });

        buttonPauseSceneGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.setScene(sceneMenu);
                gamePaused = true;
                tl.pause();
                vboxMenu.getChildren().add(buttonReturnToGameFromPause);
            }
        });
    }

    private Group initializeGroupGame() {
        Group group = new Group();
        group.getChildren().add(createObstacleOnScene(100,100, 100, 100));
        group.getChildren().add(createObstacleOnScene(800,100, 100, 100));
        group.getChildren().add(createObstacleOnScene(100,600, 100, 100));
        group.getChildren().add(createObstacleOnScene(800,600, 100, 100));
        group.getChildren().add(createObstacleOnScene(400, 300, 200, 200));

        buttonPauseSceneGame = new Button("Pause The GAME!");
        group.getChildren().add(buttonPauseSceneGame);

        score = new Text(WIDTH - 50,25, String.valueOf(scoreint));
        score.setFill(Color.WHITE);
        group.getChildren().add(score);

        pacman = new Circle(WIDTH/2,50, 25);
        Image im = new Image("https://i.gifer.com/origin/64/649852e53b7e4edf15ea1c2f23a61f29_w200.gif",false);
        pacman.setFill(new ImagePattern(im));
        group.getChildren().add(pacman);

        Circle ghost1 = new Circle(700, 700, 25, Color.RED);
        Circle ghost2 = new Circle(700, 700, 25, Color.RED);
        Circle ghost3 = new Circle(700, 700, 25, Color.RED);
        Circle ghost4 = new Circle(700, 700, 25, Color.RED);
        Circle ghost5 = new Circle(700, 700, 25, Color.RED);

        Image im1 = new Image("https://c.tenor.com/LitM8hyiDCkAAAAM/ghost-pacman.gif",false);
        ghost1.setFill(new ImagePattern(im1));
        Image im2 = new Image("https://thumbs.gfycat.com/FalseScentedIzuthrush-max-1mb.gif",false);
        ghost2.setFill(new ImagePattern(im2));
        Image im3 = new Image("https://i.gifer.com/origin/d5/d5b9ae79f5254caaf0fdcf2affcec5b0_w200.gif",false);
        ghost3.setFill(new ImagePattern(im3));
        Image im4 = new Image("https://i.gifer.com/origin/50/5016760dd9f147e7a445529ed8ff40de_w200.gif",false);
        ghost4.setFill(new ImagePattern(im4));
        Image im5 = new Image("https://thumbs.gfycat.com/FalseScentedIzuthrush-max-1mb.gif",false);
        ghost5.setFill(new ImagePattern(im5));



        listGhost = new ArrayList<Circle>();
        listGhost.add(ghost1);
        listGhost.add(ghost2);
        listGhost.add(ghost3);
        listGhost.add(ghost4);
        listGhost.add(ghost5);

        group.getChildren().add(ghost1);
        group.getChildren().add(ghost2);
        group.getChildren().add(ghost3);
        group.getChildren().add(ghost4);
        group.getChildren().add(ghost5);

        Circle cerise = new Circle(250, 250, 10, Color.PINK);
        group.getChildren().add(cerise);
        listCerises = new ArrayList<>();
        listCerises.add(cerise);
        listPoint = new ArrayList<Circle>();
        Circle point = new Circle(50, 50, 10, Color.GAINSBORO);
        listPoint.add(point);
        group.getChildren().add(listPoint.get(0));

        return group;
    }

    private void run() {
        Random r = new Random();
        Circle tempGhostToRemove = null;
        for (Circle ghost:listGhost ) {
            boolean chaseOn = false;
            if((Math.abs(ghost.getCenterX() - pacman.getCenterX()) +
                    Math.abs(ghost.getCenterY() - pacman.getCenterY())) < 500) {
                chaseOn = true;
            }
            if(chaseOn){
                double difX = ghost.getCenterX() - pacman.getCenterX();
                double difY = ghost.getCenterY() - pacman.getCenterY();
                if(Math.abs(difX) < Math.abs(difY)){
                    if(difY>0)  ghost.setCenterY(ghost.getCenterY()-25);
                    else ghost.setCenterY(ghost.getCenterY()+25);
                }
                else {
                    if(difX>0)  ghost.setCenterX(ghost.getCenterX()-25);
                    else ghost.setCenterX(ghost.getCenterX()+25);
                }
            }
            else {
                switch (r.nextInt(4)) {
                    case 0:
                        if (ghost.getCenterX() + 25 < WIDTH) {
                            ghost.setCenterX(ghost.getCenterX() + 25);
                        }
                        break;
                    case 1:
                        if (ghost.getCenterX() - 25 > 0) {
                            ghost.setCenterX(ghost.getCenterX() - 25);
                        }
                        break;
                    case 2:
                        if (ghost.getCenterY() - 25 > 0) {
                            ghost.setCenterY(ghost.getCenterY() - 25);
                        }
                        break;
                    case 3:
                        if (ghost.getCenterY() + 25 < HEIGHT) {
                            ghost.setCenterY(ghost.getCenterY() + 25);
                        }
                        break;
                }
            }
            if(ghost.getCenterX()==pacman.getCenterX() && ghost.getCenterY()==pacman.getCenterY()){
                if(!pacManVoracious) {
                    tl.pause();
                    gamePaused = true;
                    addScoreToHighScore();

                    primaryStage.setScene(sceneHighScorePage);

                    System.out.println("Game Over!");
                }
                else{
                    scoreint = scoreint + 10;
                    score.setText(String.valueOf(scoreint));
                    listGroupGame.get(indexActiveSceneGame).getChildren().remove(ghost);
                    tempGhostToRemove = ghost;
                }
            }
        }
        if(tempGhostToRemove != null){
            listGhost.remove(tempGhostToRemove);
        }
        if(listPoint.size() == 0){
            System.out.println("Bravo!");
            addScoreToHighScore();
            indexActiveSceneGame++;
            listGroupGame.add(initializeGroupGame());
            listScenesGame.add(new Scene(listGroupGame.get(indexActiveSceneGame), WIDTH, HEIGHT, Color.BLACK));
            primaryStage.setScene(listScenesGame.get(indexActiveSceneGame));
            gamePaused = false;
            tl.play();
            handleGameEvent();
        }
    }

    private void addScoreToHighScore() {
        pseudoPlayer = inputPseudo.getText();
        HashMap<String, Object> toAdd = new HashMap<>();
        toAdd.put("userName", pseudoPlayer);
        toAdd.put("score", scoreint);
        scores.add(toAdd);
        tableHighScore.getItems().add(toAdd);
    }

    private void isNextPositionAPoint(Group group, List<Circle> listPoint, Circle pacman, Text score) {
        Circle pointTempToRemove = null;
        for (Circle point : listPoint) {
            if(point.getCenterX() == pacman.getCenterX() && point.getCenterY() == pacman.getCenterY()){
                pointTempToRemove = point;
                group.getChildren().remove(point);
                scoreint++;
                score.setText(String.valueOf(scoreint));
            }
        }
        if(pointTempToRemove!=null) {
            listPoint.remove(pointTempToRemove);
        }
        for (Circle cerise : listCerises) {
            if(cerise.getCenterX() == pacman.getCenterX() && cerise.getCenterY() == pacman.getCenterY()){
                pointTempToRemove = cerise;
                group.getChildren().remove(cerise);
                pacManVoracious = true;
                timerCerise.schedule(task, 10000L);
                System.out.println("Voracious Pacman : " + pacManVoracious);
            }
        }
        if(pointTempToRemove!=null) {
            listCerises.remove(pointTempToRemove);
        }
    }
    TimerTask task = new TimerTask() {
        public void run() {
            pacManVoracious = false;
            System.out.println("Voraciouse Pacman : " + pacManVoracious);
        }
    };
    public Rectangle createObstacleOnScene(int x, int y, int width, int heigth){
        Rectangle r = new Rectangle(x, y, width, heigth);
        r.setFill(Color.GREEN);
        return r;
    }
    public static void main(String[] args) {
        launch(args);
    }
}
