package Main;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Process {
    public static int idCounter = 0;
    public int id;
    public String name;
    public int arrivalTime;
    public int burstTime;
    public int priority;
    public int quantum;     //fcai releated only
    public Color color;     //gui releated only

    int remainingTime;
    int completionTime;
    int turnaroundTime;
    int waitingTime;
    int quantumUsed;
    int FCAIFactor;
    List<Integer> quantumUpdates; // List to track quantum history

    public List<Integer> executionTimes;


    public Process(String name, int burstTime, int arrivalTime, int priority, int quantum){
        this.id = idCounter;
        idCounter++;
        this.name = name;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.priority = priority;
        this.quantum = quantum;
        this.remainingTime = burstTime;
        this.quantumUsed = 0;
        this.quantumUpdates = new ArrayList<>();

        this.executionTimes = new ArrayList<>();

        Random random = new Random();
        color = new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255));
    }

    int calculateFCAIFactor(double v1, double v2) {
        FCAIFactor =  (int) ((10 - this.priority) + Math.ceil(this.arrivalTime / v1) + Math.ceil(this.remainingTime / v2));
        return FCAIFactor;
    }

    public int getFCAIFactor() {
        return this.FCAIFactor;
    }

    public void setColor(int r,int g, int b){
        this.color = new Color(r,g,b);
    }

    //deep copy for manipulating
    public Process clone(){
        Process process = new Process(this.name, this.arrivalTime, this.burstTime, this.priority, this.quantum);
        process.color = this.color;
        process.id = this.id;
        return process;
    }

    //test releated
    public static List<Process> generateRandomProcesses(int numProcesses) {
        Random random = new Random();
        List<Process> processes = new ArrayList<>();

        for (int i = 0; i < numProcesses; i++) {
            String name = "Process " + (i + 1);
            int arrivalTime = random.nextInt(10) + 1;
            int burstTime = random.nextInt(30) + 1;
            int priority = random.nextInt(9) + 1;
            int quantum = random.nextInt(5) + 1;
            Process process = new Process(name, arrivalTime, burstTime, priority, quantum);
            processes.add(process);
        }

        return processes;
    }

}

//Comparators section

class ArivalTimeComparator implements Comparator<Process>{
    @Override
    public int compare(Process o1, Process o2) {
        return o1.arrivalTime - o2.arrivalTime;
    }
}

class PriorityComparator implements Comparator<Process>{
    @Override
    public int compare(Process o1, Process o2) {
        return o2.priority - o1.priority;
    }
}

class ShortestJobComparator implements Comparator<Process>{
    @Override
    public int compare(Process o1, Process o2) {
        return o1.burstTime - o2.burstTime;
    }
}