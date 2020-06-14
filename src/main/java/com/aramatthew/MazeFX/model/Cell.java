package com.aramatthew.MazeFX.model;

public class Cell {
    public boolean hasVisited;
    public boolean NorthWall, WestWall, SouthWall, EastWall;
    public int x,y;
    Cell(int x, int y){
        this.x = x;
        this.y = y;
        this. hasVisited = false;
        this.NorthWall = true;
        this.WestWall  = true;
        this.SouthWall = true;
        this.EastWall  = true;
    }
}
