package com.champlain.soft.game;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.Random;

public class Gameboard extends Application {

    private static final int SCENE_WIDTH  = 800;
    private static final int SCENE_HEIGHT = 800;

    // Grid
    private static final int ROWS      = 10;
    private static final int COLS      = 10;
    private static final int CELL_SIZE = SCENE_WIDTH / COLS; // 80px
    private static final int NUM_BOMBS = 5;

    enum CellType {
        GRASS, PLAYER, PRINCESS, BOMB, WALL
    }

    private CellType[][] matrix = new CellType[ROWS][COLS];

    // Player starts at [1,1]
    private int playerRow = 1;
    private int playerCol = 1;

    // Images
    private Image imgGrass;
    private Image imgWall;
    private Image imgPlayer;
    private Image imgPrincess;
    private Image imgBomb;

    private GridPane grid;

    @Override
    public void start(Stage stage) {

        loadImages();
        initMatrix();

        grid = new GridPane();
        grid.setHgap(0);
        grid.setVgap(0);
        drawBoard(grid);

        BorderPane root = new BorderPane();
        root.setCenter(grid);
        
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

        stage.setTitle("Rescue the Princess");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }




    // Load images
    private void loadImages() {
        imgGrass    = new Image(getClass().getResourceAsStream("/images/grass.png"));
        imgWall     = new Image(getClass().getResourceAsStream("/images/wall.png"));
        imgPlayer   = new Image(getClass().getResourceAsStream("/images/player.png"));
        imgPrincess = new Image(getClass().getResourceAsStream("/images/princess.png"));
        imgBomb     = new Image(getClass().getResourceAsStream("/images/bomb.png"));
    }


    // Walls on perimeter, player at [1,1], random princess + bombs
    private void initMatrix() {

        // Fill grass
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                matrix[r][c] = CellType.GRASS;
            }
        }

        // Walls
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (r == 0 || r == ROWS - 1 || c == 0 || c == COLS - 1) {
                    matrix[r][c] = CellType.WALL;
                }
            }
        }

        // Player at [1,1]
        matrix[playerRow][playerCol] = CellType.PLAYER;

        // Princess,bombs randomly
        Random rnd = new Random();
        placeRandom(CellType.PRINCESS, rnd);
        for (int i = 0; i < NUM_BOMBS; i++) {
            placeRandom(CellType.BOMB, rnd);
        }
    }

    private void placeRandom(CellType type, Random rnd) {
        int r, c;
        do {
            r = 1 + rnd.nextInt(ROWS - 2);
            c = 1 + rnd.nextInt(COLS - 2);
        } while (matrix[r][c] != CellType.GRASS);
        matrix[r][c] = type;
    }

    //board using images
    private void drawBoard(GridPane grid) {
        grid.getChildren().clear();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                StackPane cell = new StackPane();
                cell.setPrefSize(CELL_SIZE, CELL_SIZE);
                cell.setAlignment(Pos.CENTER);

                CellType type = matrix[row][col];

                Image baseImg = (type == CellType.WALL) ? imgWall : imgGrass;
                cell.getChildren().add(makeImageView(baseImg));

                // Player, princess, bomb on top of grass
                if (type == CellType.PLAYER)   cell.getChildren().add(makeImageView(imgPlayer));
                if (type == CellType.PRINCESS) cell.getChildren().add(makeImageView(imgPrincess));
                if (type == CellType.BOMB)     cell.getChildren().add(makeImageView(imgBomb));

                grid.add(cell, col, row);
            }
        }
    }

    private ImageView makeImageView(Image img) {
        ImageView iv = new ImageView(img);
        iv.setFitWidth(CELL_SIZE);
        iv.setFitHeight(CELL_SIZE);
        iv.setPreserveRatio(true);
        return iv;
    }

   private void handleKey(KeyEvent e){
        int newRow = playerRow;
        int newCol = playerCol;

        switch (e.getCode()){
            case UP -> newRow--;
            case DOWN -> newRow++;
            case LEFT -> newCol--;
            case RIGHT -> newCol++;
            default -> {return;}
        }

        if (matrix[newRow][newCol] == CellType.WALL) return;

        CellType destination = matrix[newRow][newCol];

        matrix[playerRow][playerCol] = CellType.GRASS;
        playerRow = newRow;
        playerCol = newCol;
        matrix[playerRow][playerCol] = CellType.PLAYER;

        drawBoard(grid);

        if (destination == CellType.PRINCESS){
            showAlert("You rescued the princess! You win.");
        } else if (destination == CellType.BOMB) {
            showAlert("You hit the bomb. Game over.");
        }
   }

   private void showAlert(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Rescue the Princess");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        resetGame();
   }

   private void resetGame(){
        playerRow = 1;
        playerCol = 1;
        initMatrix();
        drawBoard(grid);
   }

    public static void main(String[] args) {
        launch(args);
    }
}
