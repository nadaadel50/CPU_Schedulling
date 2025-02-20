package Main;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import GUIPack.GUI;

public class Controller {
    private List<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>(); 
    private List<Process> processes = new ArrayList<Process>();

    public Controller(int countProcess){
        System.out.print(
            "enter input mode\n" +
            "1- manual input\n" +
            "2- file input\n" 
        );
        
        int mode;
        mode = Integer.parseInt(App.scanner.nextLine());
        switch (mode) {
            case 2:
            try {
                BufferedReader reader = new BufferedReader(new FileReader("processes.txt"));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split("\\s+"); // Assuming input is space-separated
                    if (tokens.length != 5) {
                        System.out.println("Invalid input line: " + line);
                        continue;
                    }
                        String name = tokens[0];
                        int arrivalTime = Integer.parseInt(tokens[1]);
                        int burstTime = Integer.parseInt(tokens[2]);
                        int priority = Integer.parseInt(tokens[3]);
                        int quantum = Integer.parseInt(tokens[4]);

                        // Maintain the correct order of arguments when creating a new Process instance.
                        processes.add(new Process(name, burstTime, arrivalTime, priority, quantum));
                    } 
                    reader.close();
                }catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;

            case 3:
                processes = Process.generateRandomProcesses(countProcess);
                break;
        
            default:
                break;
        }
        while(countProcess-- > 0 && mode == 1){
            String name;
            int arrivalTime;
            int burstTime;
            int priority;
            int quantum;

            System.out.println("enter name");
            name = App.scanner.nextLine();
            System.out.println("enter arrival time");
            arrivalTime = Integer.parseInt(App.scanner.nextLine());
            System.out.println("enter burst time");
            burstTime = Integer.parseInt(App.scanner.nextLine());
            System.out.println("enter prio");
            priority = Integer.parseInt(App.scanner.nextLine());
            System.out.println("enter quantum");
            quantum = Integer.parseInt(App.scanner.nextLine());
            processes.add(new Process(name, arrivalTime, burstTime, priority, quantum));
            System.out.println("--process added succefully--\n");

        }
        displayMenu();
    }

    private void displayMenu(){
        boolean flag = true;
        while(flag){
            System.out.print(
                "\n" +
                "1- Priority scehdule \n" +
                "2- SJF \n" +
                "3- SRTF \n" +
                "4- FCAI \n" +
                "5- GUI for last schedule\n" +
                "6- Set Color For Process\n" + 
                "0- Exit\n> "
            );
            int choice = Integer.parseInt(App.scanner.nextLine());
            int contextTime = 0;
            switch (choice) {
                case 1:
                    System.out.println("enter context switching time");
                    contextTime = Integer.parseInt(App.scanner.nextLine());
                    nonPrempSchedule(new PriorityComparator(), contextTime);
                    break;

                case 2:
                    System.out.println("enter context switching time");
                    contextTime = Integer.parseInt(App.scanner.nextLine());
                    nonPrempSchedule(new ShortestJobComparator(), contextTime);
                    break;

                case 3:
                    System.out.println("enter context switching time");
                    SRTF(Integer.parseInt(App.scanner.nextLine()));
                    break;

                case 4:
                    try {
                        // Create the controller and execute the scheduling algorithm
                        FCAI();
                        // Calculate the total execution time
                        int totalTime = processes.stream()
                                .mapToInt(p -> p.completionTime)
                                .max()
                                .orElse(0);

                        // Print the execution timeline
                        printExecutionTimeline(processes, totalTime);
                    } catch (Exception e) {
                        System.err.println("Unexpected error: " + e.getMessage());
                    }
                    break;

                case 5:
                    representInGUI();
                    break;

                case 6:
                    int r,g,b;
                    System.out.println("enter the rgb");
                    r = App.scanner.nextInt();
                    g = App.scanner.nextInt();
                    b = App.scanner.nextInt();

                    System.out.println("enter the id");
                    updateColor(App.scanner.nextInt(),r,g,b);
                    break;

                case 7:
                    System.out.println("enter context switching time");
                    SRTFBackUp(Integer.parseInt(App.scanner.nextLine()));
                    break;

                case 0:
                    flag = false;
                    break;
            
                default:
                    System.out.println("invalid choice try again\n");
                    break;
            }

        }
        App.scanner.close();
    }

    private void updateColor(int id, int r, int g, int b){
        processes.get(id).setColor(r, g, b);
    }

    public void nonPrempSchedule(Comparator<Process> Comparator,int contextSwitchTime){
        int currentTime = 1;
        int waitingTime = 0;
        Process currentProcess = null;
        PriorityQueue<Process> allProcesses = new PriorityQueue<Process>(new ArivalTimeComparator());
        PriorityQueue<Process> queuedProcesses = new PriorityQueue<Process>(Comparator);
        result = new ArrayList<ArrayList<Integer>>(); //reassign to clear previous result

        for (Process process : processes) {
            allProcesses.add(process.clone());
        }

        for(int i = 0 ; processes.size() > i ; ++i){
            result.add(new ArrayList<>());
            result.get(i).add(processes.get(i).id);
        }

        while(queuedProcesses.size() != 0  || allProcesses.size() != 0 || currentProcess != null){
            System.out.println("\n\ncurrent time = " + currentTime);

            for(int i = 0 ; processes.size() > i;++i){
                result.get(i).add(0);
            }

            if(queuedProcesses.size() > 0)
                if(currentProcess == null)
                    currentProcess = queuedProcesses.poll();

            while(!allProcesses.isEmpty()){
                if(allProcesses.peek().arrivalTime == currentTime){
                    if(currentProcess == null)
                        currentProcess = allProcesses.poll();
                    else{
                        queuedProcesses.add(allProcesses.peek());
                        System.out.println(allProcesses.poll().id + " process arrived and is waiting");
                    }
                }
                else
                    break;
            }
            if(waitingTime > 0){
                waitingTime--;
            }
            else if(currentProcess != null){
                currentProcess.burstTime--;
                System.out.println("current process executing is " + currentProcess.id + " time left = " + currentProcess.burstTime);
                result.get(currentProcess.id).set(currentTime, 1);

                if(currentProcess.burstTime == 0){
                    for (Process p : processes) {
                        if(p.id == currentProcess.id){
                            p.completionTime = currentTime;
                            break;
                        }
                    }
                    waitingTime = contextSwitchTime;
                    System.out.println("process " + currentProcess.id + " finished executing");
                    currentProcess = null;
                }
            }
            currentTime++;
        }
        int turnaroundTime = 0;
        int totalWaiting = 0;
        for (Process p : processes) {
            p.turnaroundTime = (p.completionTime+1) - p.arrivalTime;
            turnaroundTime += p.turnaroundTime;
            totalWaiting += p.turnaroundTime - p.burstTime;
        }
        System.out.println("\n\navg waiting time = " + (totalWaiting/processes.size()) + "\n" + "avg turnaround time = " + (turnaroundTime/processes.size()));
    }

    public void SRTFBackUp(int contextSwitchTime){
        int currentTime = 1;
        int waitingTime = 0;
        Process currentProcess=null;
        result = new ArrayList<ArrayList<Integer>>();
        PriorityQueue<Process> allProcesses = new PriorityQueue<Process>(new ArivalTimeComparator());
        PriorityQueue<Process> queue = new PriorityQueue<Process>(new ShortestJobComparator());
        for (Process process: processes) {
            allProcesses.add(process.clone());
        }
        for(int i = 0 ; i<processes.size(); ++i){
            result.add(new ArrayList<>());
            result.get(i).add(processes.get(i).id);
        }
        while (!queue.isEmpty() || !allProcesses.isEmpty()){
            System.out.println("\n\n current time = " + currentTime);

            for(int i = 0 ;i<processes.size();++i){
                result.get(i).add(0);
            }

            if(waitingTime > 0)
                waitingTime--;

            while(!allProcesses.isEmpty()){
                if(allProcesses.peek().arrivalTime == currentTime){
                    queue.add(allProcesses.poll());
                }
                else 
                    break;
            }

            if(!queue.isEmpty()){
                if(queue.peek() != currentProcess && currentProcess != null && waitingTime == 0)
                    waitingTime = contextSwitchTime;

                currentProcess = queue.peek();
            }
            
            if(!queue.isEmpty() && waitingTime == 0){
                if(queue.peek().burstTime==1){
                    System.out.println("process " + currentProcess.name + " finished executing");
                    queue.poll();
                    result.get(currentProcess.id).set(currentTime, 1);
                    currentProcess = null;
                } 
                else {
                    queue.peek().burstTime--;
                    System.out.println("current process executing is " + currentProcess.name + " time left = " + currentProcess.burstTime);
                    result.get(queue.peek().id).set(currentTime, 1);
                }
            }
            currentTime++;
        }
    }

    public void SRTF(int contextTime){

        int currentTime = 1;
        Process currentProcess=null;
        result = new ArrayList<ArrayList<Integer>>();
        PriorityQueue<Process> allProcesses=new PriorityQueue<Process>(new ArivalTimeComparator());
        PriorityQueue<Process> queue=new PriorityQueue<Process>(new ShortestJobComparator());
        for (Process process: processes) {
            allProcesses.add(process.clone());
        }
        for(int i = 0 ; i<processes.size(); ++i){
            result.add(new ArrayList<>());
            result.get(i).add(processes.get(i).id);
        }
        while (queue.size()!=0||allProcesses.size()!=0){
            System.out.println("\n\n current time = " + currentTime);
            for(int i = 0 ;i<processes.size();++i){
                result.get(i).add(0);
            }
            if(!allProcesses.isEmpty()) {
                while (allProcesses.peek().arrivalTime == currentTime) {
                    queue.add(allProcesses.poll());
                    if(allProcesses.peek()==null){
                        break;
                    }
                }
            }
            if(currentProcess!=null){
                if(currentProcess!=queue.peek()){
                    for(int j=0;j<contextTime;j++) {
                        for (int i = 0; i < processes.size(); ++i) {
                            result.get(i).add(0);
                        }
                        if (!allProcesses.isEmpty()) {
                            while (allProcesses.peek().arrivalTime == currentTime) {
                                queue.add(allProcesses.poll());
                                if (allProcesses.peek() == null) {
                                    break;
                                }
                            }
                        }
                        currentTime++;
                        currentProcess = queue.peek();
                    }
                    if(contextTime==0){
                        currentTime++;
                        currentProcess = queue.peek();
                    }
                    continue;
                }
            }
            if(!queue.isEmpty()){
                if(queue.peek().burstTime==1){
                    if(currentProcess!=null) {
                        System.out.println("process " + currentProcess.name + " finished executing");
                    }
                    result.get(queue.peek().id).set(currentTime, 1);
                    currentProcess=queue.poll();
                    currentTime++;

                }
                else {
                    if(currentProcess!=null) {
                        System.out.println("current process executing is " + currentProcess.name + " time left = " + currentProcess.burstTime);
                    }
                    currentProcess=queue.peek();
                    queue.peek().burstTime--;
                    result.get(queue.peek().id).set(currentTime, 1);
                    currentTime++;
                }
            }
            else
                currentTime++;
        }

    }

    //FCAI will be final execution since no cloning take place processes are directly updated
    int[][] timeline;
    public void FCAI() {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0;
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

       // int[][] timeline;


        int lastArrivalTime = processes.stream().mapToInt(p -> p.arrivalTime).max().orElse(1);
        int maxBurstTime = processes.stream().mapToInt(p -> p.burstTime).max().orElse(1);
        System.out.println("Last Arrival Time: " + lastArrivalTime);
        System.out.println("Max Burst Time: " + maxBurstTime);


        timeline = new int[processes.size()][lastArrivalTime + maxBurstTime + 100];

        double v1 = (double) lastArrivalTime / 10;
        double v2 = (double) maxBurstTime / 10;
        System.out.println("V1: " + v1 + ", V2: " + v2);

        Queue<Process> rq = new LinkedList<>();

        // Initial FCAI calculation for all processes
        for (Process p : processes) {
            p.calculateFCAIFactor(v1, v2);
        }
        Process currentProcess = null;
        boolean completedQuantum = false;
        boolean completed = false;
        while (!rq.isEmpty() || processes.stream().anyMatch(p -> p.remainingTime > 0)) {
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && p.remainingTime > 0 && !rq.contains(p) && p != currentProcess) {
                    p.calculateFCAIFactor(v1, v2);
                    rq.add(p);
                }
            }
            if (!rq.isEmpty()) {
                currentProcess = rq.peek();
                if(!completedQuantum && !completed) {
                    for (Process p : rq) {
                        if (p.calculateFCAIFactor(v1, v2) < currentProcess.calculateFCAIFactor(v1, v2)) {
                            currentProcess = p;
                        }
                    }
                }
                rq.remove(currentProcess);
                completedQuantum = false;
                completed = false;

                // Calculate next arrival time
                int finalCurrentTime = currentTime;
                int nextArrivalTime = processes.stream()
                        .filter(p -> p.arrivalTime > finalCurrentTime && p.remainingTime > 0)
                        .mapToInt(p -> p.arrivalTime)
                        .min()
                        .orElse(Integer.MAX_VALUE);


                // Calculate non-preemptive execution time (40% of the quantum)
                int nonPreemptiveTime = (int) Math.ceil(currentProcess.quantum * 0.4);
                int execTime = Math.min(nonPreemptiveTime, currentProcess.remainingTime);

                // Adjust execTime if a new process arrives within this time
                execTime = Math.min(execTime, nextArrivalTime - currentTime);

                for (int time = currentTime; time < currentTime + execTime; time++) {
                    timeline[processes.indexOf(currentProcess)][time] = 1; // Mark timeline for the current process
                }

                result.clear();

                // Store the timeline in the result list
                for (int i = 0; i < processes.size(); i++) {
                    ArrayList<Integer> processTimeline = new ArrayList<>();
                    for (int time = 0; time < timeline[i].length; time++) {
                        processTimeline.add(timeline[i][time]);
                    }
                    result.add(processTimeline);
                }

                // Execute non-preemptively for 40%
                System.out.println("Time " + currentTime + "–" + (currentTime + execTime) + ": " + currentProcess.name
                        + " executes for " + execTime + " units non-preemptively.");
                currentProcess.remainingTime -= execTime;
                currentTime += execTime;
                currentProcess.quantumUsed = 0;
                currentProcess.quantumUsed += execTime;
                //currentProcess.calculateFCAIFactor(v1, v2);

                for (Process p : processes) {
                    if (p.arrivalTime <= currentTime && p.remainingTime > 0 && !rq.contains(p) && p != currentProcess) {
                        p.calculateFCAIFactor(v1, v2);
                        rq.add(p);
                    }
                }

                // After non-preemptive execution, check if a higher-priority process is in the queue
                if (currentProcess.remainingTime > 0) {

                    boolean hasHigherPriority = false;

                    for (Process p : rq) {
                        if (p.calculateFCAIFactor(v1, v2) < currentProcess.getFCAIFactor()) {
                            hasHigherPriority = true;
                            break;
                        }
                    }

                    if (!hasHigherPriority) {
                        // Continue execution if no higher-priority process is ready
                        while(currentProcess.quantumUsed != currentProcess.quantum && !hasHigherPriority && currentProcess.remainingTime > 0) {
                            int finalCurrentTime1 = currentTime;
                            nextArrivalTime = processes.stream()
                                    .filter(p -> p.arrivalTime > finalCurrentTime1 && p.remainingTime > 0)
                                    .mapToInt(p -> p.arrivalTime)
                                    .min()
                                    .orElse(Integer.MAX_VALUE);

                            execTime = Math.min(currentProcess.quantum - currentProcess.quantumUsed, currentProcess.remainingTime);

                            execTime = Math.min(execTime, nextArrivalTime - currentTime);

                            for (int time = currentTime; time < currentTime + execTime; time++) {
                                timeline[processes.indexOf(currentProcess)][time] = 1; // Mark timeline for the current process
                            }

                            result.clear();

                            // Store the timeline in the result list
                            for (int i = 0; i < processes.size(); i++) {
                                ArrayList<Integer> processTimeline = new ArrayList<>();
                                for (int time = 0; time < timeline[i].length; time++) {
                                    processTimeline.add(timeline[i][time]);
                                }
                                result.add(processTimeline);
                            }

                            System.out.println("Time " + currentTime + "-" + (currentTime + execTime) + ": " + currentProcess.name
                                    + " executes for " + execTime + " units.");
                            currentProcess.remainingTime -= execTime;
                            currentTime += execTime;
                            currentProcess.quantumUsed += execTime;

                            for (Process p : processes) {
                                if (p.arrivalTime <= currentTime && p.remainingTime > 0 && !rq.contains(p) && p != currentProcess) {
                                    p.calculateFCAIFactor(v1, v2);
                                    rq.add(p);
                                }
                            }

                            // Check if any process in rq has a smaller FCAI factor than the current process
                            for (Process p : rq) {
                                if (p.calculateFCAIFactor(v1, v2) < currentProcess.calculateFCAIFactor(v1, v2)) {
                                    hasHigherPriority = true;
                                    break;
                                }
                            }
                        }
                        if(currentProcess.quantumUsed == currentProcess.quantum){
                            completedQuantum = true;
                        }

                        if (currentProcess.remainingTime > 0) {
                            // Update quantum and requeue process
                            if(completedQuantum){
                                if(currentProcess.quantumUpdates.isEmpty()){
                                    currentProcess.quantumUpdates.add(currentProcess.quantum);
                                }
                                System.out.print(currentProcess.name + " quantum changed from " + currentProcess.quantum);
                                currentProcess.quantum += 2;
                                System.out.println(" to " + currentProcess.quantum);
                                currentProcess.quantumUpdates.add(currentProcess.quantum);
                            }

                            currentProcess.calculateFCAIFactor(v1, v2);
                            rq.add(currentProcess);
                        }
                        else {
                            System.out.println(currentProcess.name + " completed at time " + currentTime);
                            currentProcess.completionTime = currentTime;
                            currentProcess.turnaroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                            currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
                            totalWaitingTime += currentProcess.waitingTime;
                            totalTurnaroundTime += currentProcess.turnaroundTime;
                            completed = true;
                        }
                    }
                    if(hasHigherPriority) {
                        // Preempt current process
                        System.out.println("Time " + currentTime + ": " + currentProcess.name + " preempted.");
                        if(currentProcess.quantumUpdates.isEmpty()){
                            currentProcess.quantumUpdates.add(currentProcess.quantum);
                        }
                        System.out.print(currentProcess.name + " quantum changed from " + currentProcess.quantum);
                        currentProcess.quantum += (currentProcess.quantum - currentProcess.quantumUsed);
                        System.out.println(" to " + currentProcess.quantum);
                        currentProcess.quantumUpdates.add(currentProcess.quantum);

                        currentProcess.calculateFCAIFactor(v1, v2);
                        if(!rq.contains(currentProcess)) {
                            rq.add(currentProcess);
                        }
                    }
                }
                else {
                    System.out.println(currentProcess.name + " completed at time " + currentTime);
                    currentProcess.completionTime = currentTime;
                    currentProcess.turnaroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                    currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
                    totalWaitingTime += currentProcess.waitingTime;
                    totalTurnaroundTime += currentProcess.turnaroundTime;
                    completed = true;
                }
            } else {
                currentTime++;
            }
        }

        printResults(processes);

        double avgWaitingTime = (double) totalWaitingTime / processes.size();
        double avgTurnaroundTime = (double) totalTurnaroundTime / processes.size();

        System.out.println("\nAverage Waiting Time: " + avgWaitingTime);
        System.out.println("Average Turnaround Time: " + avgTurnaroundTime);

        System.out.println("\nQuantum Update History:");
        for (Process p : processes) {
            System.out.println(p.name + ": " + p.quantumUpdates);
        }
    }

    private void printResults(List<Process> processes) {
        System.out.println("\nFinal Results:");
        System.out.println("P\tCT\tTAT\tWT");
        for (Process p : processes) {
            System.out.println(p.name + "\t" + p.completionTime + "\t" + p.turnaroundTime + "\t" + p.waitingTime);
        }
    }

    private void printExecutionTimeline(List<Process> processes, int totalTime) {
        System.out.println("\nExecution Timeline:");

        // Clear the existing data in result to avoid appending to previous data
        result.clear();

        // Populate the result list with timeline data for each process
        for (int i = 0; i < processes.size(); i++) {
            ArrayList<Integer> processTimeline = new ArrayList<>();
            for (int time = 0; time < totalTime; time++) {
                processTimeline.add(timeline[i][time]);
            }
            result.add(processTimeline);
        }

        // Print the result list
        for (int i = 0; i < result.size(); i++) {
            //System.out.print(processes.get(i).name + ": ");
            for (int time : result.get(i)) {
                System.out.print(time + " ");
            }
            System.out.println();
        }
    }

    private void representInGUI(){
        new GUI(result,processes);
    }

}

// test1
// 1
// 5
// 0
// 0
// test1
// 2
// 3
// 0
// 0
// test1
// 6
// 1
// 5
// 0