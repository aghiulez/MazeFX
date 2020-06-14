package com.aramatthew.MazeFX.controller;

import com.aramatthew.MazeFX.model.*;

import java.util.ArrayList;
import java.util.List;


public class Solver extends MazeController{

    public Solver(Maze m) {
        super(m);
        //super.resetVisits();
    }
    public void DFSIterativeBacktracker() {
        resetVisits();
        maze.setCurrentLocation(maze.board[0][0]);

        this.stack.push(maze.getCurrLocation());


//        while (!this.stack.empty()){
        while (maze.getCurrLocation() != maze.board[maze.board.length -1][maze.board.length -1]){
            maze.setCurrentLocation(this.stack.pop());

            Cell next = planVisit(maze.getCurrLocation());

            if (next != maze.getCurrLocation()){
                this.stack.push(maze.getCurrLocation());
                this.stack.push(next);
            }



        }

        System.gc();
    }

    @Override
    Cell planVisit(Cell current){
        List<Cell> possible = new ArrayList<Cell>();

        if(current.NorthWall == false && (!this.maze.board[current.y-1][current.x].hasVisited)){
            possible.add(this.maze.board[current.y-1][current.x]);
        }
        if(current.WestWall == false && (!this.maze.board[current.y][current.x+1].hasVisited)){
            possible.add(this.maze.board[current.y][current.x+1]);
        }
        if(current.SouthWall == false && (!this.maze.board[current.y+1][current.x].hasVisited)){
            possible.add(this.maze.board[current.y+1][current.x]);
        }
        if(current.EastWall == false && (!this.maze.board[current.y][current.x-1].hasVisited)){
            possible.add(this.maze.board[current.y][current.x-1]);
        }

        if (possible.size() > 0){
            int randomIndex = super.randomGenerator.nextInt(possible.size());
            return possible.get(randomIndex);
        }

        return current; // returns current if no possible visit
    }
//    @Override
//    void visit(Cell current, Cell next){
//    }
}
