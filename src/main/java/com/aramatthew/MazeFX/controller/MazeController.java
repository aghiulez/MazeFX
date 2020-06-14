package com.aramatthew.MazeFX.controller;
import com.aramatthew.MazeFX.model.*;
import java.util.Stack;

import java.util.Random;


public class MazeController {
    Maze maze;
    Stack<Cell> stack = new Stack<>();
    Random randomGenerator = new Random();

    public MazeController(Maze m){
        this.maze = m;
    }


    Cell planVisit(Cell current){
        return current; // returns current if no possible visit
    }
    void visit(Cell current, Cell next){

    }

    void resetVisits(){
        for ( int i = 0 ; i < maze.board.length ; i++){
            for (int j =0 ; j < maze.board.length ; j++){
                maze.board[i][j].hasVisited = false;
            }
        }
    }
}
