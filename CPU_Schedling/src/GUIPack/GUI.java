package GUIPack;

import java.util.ArrayList;
import java.util.List;
import Main.Process;

public class GUI {
    private Panel panel;

    public GUI(List<ArrayList<Integer>> result, List<Process> processes){
        panel = new Panel(result,processes);
        new Frame(panel);
    }
}

/*
 * expecting a 2d array of integers the first column will have the ids of the processes
 * the rest columns will represent a bit value for the process either executing (1) or idle (0)
 * example:
 * 
 * 
 * 4 process
 * process zero burst time = 3 arrives at time 1
 * process one burst time = 2 arrives at time 3
 * process two burst time = 1 arrives at time 5
 * process three burst time = 1 arrives ar time 8
 * 
 * Id
 * |
 * V
 *    1  2  3  4  5  6  7  8  <= time
 * 0  0  1  1  0  0  0  0  0
 * 1  0  0  0  1  1  0  0  0
 * 2  1  0  0  0  0  1  0  0
 * 3  0  0  0  0  0  0  1  0
 *
 * this example represent first comes first serves process process zero executed 3 units while 1 executed 2, 2 for 1, 3 for 1 unit of time
 * 
 * 
 * to execute this model peroperly the global clock simulation comes to mind same as assignment 2 
 * with each tick the clock will update the arr[processId][currenttime] to be equal to one
 */