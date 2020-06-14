package com.aramatthew.MazeFX.view;

import com.aramatthew.MazeFX.controller.*;
import com.aramatthew.MazeFX.model.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;


public class MazeFX extends Application implements Runnable {
    private final Object monitor = this;
    private volatile boolean paused = false;

    int speed = 1;
    int size = 500;
    int dimensions = 10;
    Maze myMaze;
    Generator generator;
    Solver solver;
    GridPane maze;
    
//    private AtomicBoolean running = new AtomicBoolean(true);
    Thread ActiveThread;
    
    public void delay(){
        synchronized (monitor) {
            try {

                if (paused) {
                    synchronized (monitor) {
                        try {
                            monitor.wait();
                        }
                        catch (InterruptedException e) {
                        }
                    }
                }
                else{
                    synchronized (monitor) {
                        monitor.wait(speed);
                    }
                }
            }
            catch (InterruptedException e) { }
        }
    }
    public BorderPane CellPane(){
        int thickness = (int) Math.ceil((float)(size/dimensions)/10); //


        BorderPane cell = new BorderPane();
        //cell.getStyleClass().add("cell");
        //in-line
        cell.setStyle("-fx-background-color: black,white");

        Pane topwall = new Pane();
        topwall.setPrefHeight(thickness);
        Pane rightwall = new Pane();
        rightwall.setPrefWidth(thickness);
        Pane bottomwall = new Pane();
        bottomwall.setPrefHeight(thickness);
        Pane leftwall = new Pane();

        leftwall.setPrefWidth(thickness);

        topwall.setStyle("-fx-background-color: black");
        rightwall.setStyle("-fx-background-color: black");
        bottomwall.setStyle("-fx-background-color: black");
        leftwall.setStyle("-fx-background-color: black");

        cell.setTop(topwall);
        cell.setRight(rightwall);
        cell.setBottom(bottomwall);
        cell.setLeft(leftwall);

        return cell;
    }
    public GridPane MazePane(){
        GridPane mymaze = new GridPane();


        mymaze.setStyle("-fx-background-color: black");
        ColumnConstraints column = new ColumnConstraints((int) Math.ceil((float)(size/dimensions)));
        RowConstraints row       = new RowConstraints((int) Math.ceil((float)(size/dimensions)));
        for (int i = 0; i < myMaze.board.length; i++){
            mymaze.getColumnConstraints().add(column);
            mymaze.getRowConstraints().add(row);
        }


        for(int i = 0; i < myMaze.board.length; i++){
            for(int j = 0; j < myMaze.board.length; j++){
                BorderPane cell = CellPane();

                mymaze.add(cell,i,j);
            }
        }
        return mymaze;
    }
    public void removeWall(Cell cell){
        BorderPane cellPane = getCellPane(cell);
        Circle pointer = new Circle((int) Math.ceil((float)(size/dimensions)/5));
        pointer.setFill(Color.RED);
        cellPane.setCenter(pointer);

        if (cellPane != null){
            if (!cell.NorthWall){
                cellPane.setTop(null);
            }
            if (!cell.WestWall){
                cellPane.setRight(null);
            }
            if (!cell.SouthWall){
                cellPane.setBottom(null);
            }
            if (!cell.EastWall){
                cellPane.setLeft(null);
            }

        }
    }
    public void drawPath(Cell from, Cell to){
        BorderPane cellPaneFrom = getCellPane(from);
        BorderPane cellPaneTo   = getCellPane(to);

        if(cellPaneTo.getStyle() == "-fx-background-color: rgba(32,200,59,0.3);" || cellPaneTo.getStyle() == "-fx-background-color: rgba(200,10,0,0.3);"){
            cellPaneTo.setStyle("-fx-background-color: rgba(200,10,0,0.3);");
            if(cellPaneFrom.getStyle() == "-fx-background-color: rgba(32,200,59,0.3);"){
                cellPaneFrom.setStyle("-fx-background-color: rgba(200,10,0,0.3);");
            }
        }
        else{
            cellPaneTo.setStyle("-fx-background-color: rgba(32,200,59,0.3);");
            if(cellPaneFrom.getStyle() == "-fx-background-color: rgba(200,10,0,0.3);"){
                cellPaneFrom.setStyle("-fx-background-color: rgba(32,200,59,0.3);");
            }
        }

    }
    public BorderPane getCellPane(Cell curr){
        for(Node n: maze.getChildren()){
            Integer r = GridPane.getRowIndex(n);
            Integer c = GridPane.getColumnIndex(n);
            int row = r == null ? 0 : r;
            int column = c == null? 0 : c;

            if (row == curr.y && column == curr.x && (n instanceof BorderPane)) {

                BorderPane cellpane = (BorderPane) n;
                //((BorderPane) n).setBottom(null);
//                return cellpane;
                return (BorderPane) n;
            }
        }
        return null;
    }
    public void GenerateMaze()  {
        myMaze.CurrLocationProperty().addListener(new ChangeListener(){
                @Override public void changed(ObservableValue o,Object oldVal,
                                              Object newVal){

                    Cell from = (Cell) oldVal;
                    Cell to = (Cell) newVal;

                    Platform.runLater( () -> {
                        if(from != null){

                            removeWall(from);
                            getCellPane(from).setCenter(null);
                        }
                        removeWall(to);
                    });
//                    synchronized (this) {
//                        try {
//                            wait(speed);
//                        }
//                        catch (InterruptedException e) { }
//                    }
                    delay();

                }
            });
        generator.DFSIterativeBacktracker();
    }
    public void SolveMaze(){
        myMaze.CurrLocationProperty().addListener(new ChangeListener(){
            @Override public void changed(ObservableValue o,Object oldVal,
                                          Object newVal){
                Cell from = (Cell) oldVal;
                Cell to = (Cell) newVal;

                Platform.runLater( () -> {

                    drawPath(from,to);
                });
                delay();

            }
        });
        solver.DFSIterativeBacktracker();
    };

    public void createThread(){
        ActiveThread = new Thread(this);
        ActiveThread.setDaemon(true);
        ActiveThread.start();
    }

    //menu items
    public VBox menuSpinners(){
        Label Title = new Label("MazeFX");


        // Dimensions chooser
        final Spinner<Integer> dimensionsSpinner = new Spinner<>();
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(5,20,5,1);
        dimensionsSpinner.setValueFactory(valueFactory);
        dimensionsSpinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);



        //speed chooser
        final Spinner<Integer> speedSpinner = new Spinner<>();
        SpinnerValueFactory<Integer> speedValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(50,500,50,50);
        speedSpinner.setValueFactory(speedValueFactory);
        speedSpinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);

        return new VBox(Title,dimensionsSpinner,speedSpinner);


    }





    // make a middle menu
    public HBox bottomMenu(Button backbtn){
        Button pause = new Button("pause");
        pause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                paused = paused ? false : true;
                pause.setText(paused ? "play" : "pause");
                synchronized (monitor) {
                    monitor.notifyAll();
                }
            }
        });


        Button slower = new Button("slower");
        Button faster = new Button("faster");

        faster.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                speed = speed - 10;
            }
        });
        slower.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                speed = speed + 50;
            }
        });

        return new HBox (backbtn,pause,slower,faster);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("MazeFX");

        Button startbtn = new Button("start"); // add stop button too?
        Button backbtn = new Button("back"); // add stop button too?



        //com.maze.mazeFX.View
        BorderPane view = new BorderPane();

        //center start menu
        VBox menu = menuSpinners();
        view.setCenter(menu);
        view.setAlignment(menu, Pos.CENTER);
        menu.setAlignment(Pos.CENTER);



        // Start btn props
        view.setBottom(startbtn);
        startbtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                Node spinnernode = menu.getChildren().get(1);
                Node speednode = menu.getChildren().get(2);
                if (spinnernode instanceof Spinner && speednode instanceof  Spinner){
                    dimensions = (int) ((Spinner) spinnernode).getValue();
                    speed      = (int) ((Spinner) speednode).getValue();
                }

                myMaze = new Maze(dimensions); /// --> add dimension choosing functionality
                generator = new Generator(myMaze); // --> add speed of generator
                solver    = new Solver(myMaze);
                maze = MazePane();
                //ADD

                view.setCenter(maze);
                view.setAlignment(maze,Pos.CENTER);
                maze.setAlignment(Pos.CENTER);
                view.setBottom(bottomMenu(backbtn));
                createThread();

            }
        });
        // back btn props
        backbtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ActiveThread.stop();
                System.gc();
                view.setCenter(menu);
                view.setBottom(startbtn);
                speed = 1000;
            }
        });


        // bottom hbox




        //Misc
        view.setAlignment(startbtn,Pos.CENTER);
        view.setAlignment(backbtn,Pos.CENTER);

        //ADD
        Scene scene = new Scene(view, size, size  + 25);



        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();


    }
    public void run(){
        GenerateMaze();
        SolveMaze();

    };
    @Override
    public void stop(){
        System.out.println("Stage is closing");
        // memory stats
        int mb = 1024 * 1024;
        // get Runtime instance
        Runtime instance = Runtime.getRuntime();

        System.out.println("***** Heap utilization statistics [MB] *****\n");

        // available memory
        System.out.println("Total Memory: " + instance.totalMemory() / mb);

        // free memory
        System.out.println("Free Memory: " + instance.freeMemory() / mb);

        // used memory
        System.out.println("Used Memory: "
                + (instance.totalMemory() - instance.freeMemory()) / mb);

        // Maximum available memory
        System.out.println("Max Memory: " + instance.maxMemory() / mb);
        // garbage collect
        System.gc();
    }

    public static void main(String[] args) {
        System.out.println(System.getenv("PORT"));
        launch(args);
    }
}