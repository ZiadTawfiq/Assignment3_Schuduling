import java.util.ArrayList;
import java.util.List;

class FCAIScheduler {
    public Process pickByLessFcai(List<Process> list, Process currentProcess) {
        Process selectedProcess = null;
        double minFCAI = Double.MAX_VALUE;

        for (Process process : list) {
            if (process != null && process.remainingBurstTime > 0 && process.arrivalTime <= currentTime) {
                process.calculateFCAIFactor(V1, V2);
                if (process.FCAIFactor < minFCAI) {
                    minFCAI = process.FCAIFactor;
                    selectedProcess = process;
                }
            }
        }

        if (selectedProcess != null) {
            System.out.println("Selected process for preemption: " + selectedProcess.processName +
                    " (FCAI Factor: " + selectedProcess.FCAIFactor + ")");
        }
        return selectedProcess;
    }

    // Fields
    private List<Process> processes;
    private final List<Process> CPU = new ArrayList<>();
    private List<Process> ready = new ArrayList<>();
    private double V1, V2;
    private int currentTime = 0;
    private int counter = 0;
    private List<String> executionOrder = new ArrayList<>();

    // Constructor
    public FCAIScheduler(List<Process> processes) {
        this.processes = processes;
        calculateV1V2(processes);
    }

    // Helper methods to calculate V1 and V2
    private void calculateV1V2(List<Process> processes) {
        V1 = calculateV1(processes);
        V2 = calculateV2(processes);
        System.out.println(V1);
        System.out.println(V2);
    }

    public double calculateV1(List<Process> processesList) {
        int maxArrivalTime = processesList.stream()
                .mapToInt(p -> p.arrivalTime)
                .max()
                .orElse(0);
        return (double) maxArrivalTime / 10;
    }

    public double calculateV2(List<Process> processesList) {
        int maxBurstTime = processesList.stream()
                .mapToInt(p -> p.BurstTime)
                .max()
                .orElse(0);
        return (double) maxBurstTime / 10;
    }

    // Scheduling logic
    public void schedule() {
        //we loop until all the processes are done
        while (!allProcessDone()) {
            //before we start we check if there is any process at the current time
            checkInput();
            // after checking we update the FCAI Factor for all the processes
            updateFcai();

            //we check if the ready list contain processes to start execution
            if (!ready.isEmpty()) {
                //we use pickProcess() to pick the correct process
                Process selected = pickProcess(ready);
                // then add the selected the process to the cpu list to start the execution
                CPU.add(selected);
                execute();


                System.out.println("-------------------------------");


            }

        }

        printStatistics();
    }

    // Process selection methods
    public Process pickProcess(List<Process> list) {
        Process selectedProcess = null;


        //we check if the cpu already has a process that mean that process preempted another process and we return it as the selected process and then remove it from the cpu
        if (!CPU.isEmpty()) {
            selectedProcess = CPU.get(0);
            CPU.remove(selectedProcess);
        } else {
            //if the cpu is empty then there is no preemption and we select the first process in the ready list
            return ready.get(0);
        }

        if (selectedProcess != null) {
            System.out.println("Selected process: " + selectedProcess.processName +
                    " (FCAI Factor: " + selectedProcess.FCAIFactor + ")");
        }
        return selectedProcess;
    }

    public void execute() {
        //when the execution starts we get the process from the cpu
        Process p = CPU.get(0);
        System.out.println("Currently executing " + p.processName);

        //copy the quantum into a temp variable to calculate without changing the original quantum
        int temp2 = p.Quantum;

        // execution time
        int executionTime = (int) Math.min(Math.ceil(0.4 * p.Quantum), p.remainingBurstTime);
        //update the remaining time
        p.remainingBurstTime -= executionTime;

        //subtract the execution time from the quantum
        temp2 -= executionTime;
        currentTime += executionTime;
        //we check for input every unit of time before we continue execution to check for preemption
        checkInput();
        //the loop continues until the quantum is finished or the process is done
        while (temp2 > 0 && p.remainingBurstTime > 0) {
            checkInput();
            //if there is a preempting process we break the loop, if not we continue and do calculations for time
            if (checkPreemption(p) || temp2 == 0) {
                break;
            }
            executionTime++;
            currentTime++;
            p.remainingBurstTime--;
            temp2--;
        }

        //if the process is not done update the quantum for the current process and add the quantum to quantum history
        if (p.remainingBurstTime != 0) {
            updateQuantum(p, p.Quantum - executionTime, temp2);
            p.quantumHistory.add(p.Quantum);
        }

        //this condition handles preemption
        if (checkPreemption(p)) {
            //we start by picking the process with the least FCAI factor
            Process preemptingProcess = pickByLessFcai(ready, p);
            if (preemptingProcess != null) {
                //we remove the current process from the ready list and add it again to the end of the list to maintain order
                ready.remove(p);
                ready.add(p);
                //then we remove current process from the cpu and add the preempting process
                CPU.remove(p);
                CPU.add(preemptingProcess);
                System.out.println("Preempting process added to CPU: " + preemptingProcess.processName);
            }
        }
        //if no preemption we return the process to the end of the ready list
        ready.remove(p);
        ready.add(p);
        
        //if the process is done we remove the process from the ready list and set the completion time, the counter for the completed processes
        if (p.remainingBurstTime == 0) {
            p.completionTime = currentTime;
            ready.remove(p);
            counter++;

            System.out.println(p.processName + " done");
            executionOrder.add(p.processName);
        }
        //after execution is done we update FCAI factor and remove the current process from the cpu if it was not preempted

        updateFcai();
        CPU.remove(p);

        System.out.println(executionTime);
        System.out.println("Brust Time left : " + p.remainingBurstTime);
        System.out.println("Time: " + currentTime);
    }

    // Update and check methods
    public void updateFcai() {
        for (Process p : processes) {
            p.calculateFCAIFactor(V1, V2);
        }
    }

    //Method to update the quantum value
    public void updateQuantum(Process p, int leftQuantum, int temp) {
        if (temp == 0) {
            p.Quantum += 2;
        } else {
            p.Quantum += leftQuantum;
        }


    }


    public void checkInput() {
        for (Process p : processes) {
            if (!ready.contains(p) && p.arrivalTime <= currentTime && p.remainingBurstTime != 0) {
                ready.add(p);
            }
        }
    }
    
    //we loop on the ready list to get the process with the least FCAI factor
    public boolean checkPreemption(Process currentProcess) {
        for (Process p1 : ready) {
            if (p1 != null && p1.remainingBurstTime > 0 &&
                    p1.FCAIFactor < currentProcess.FCAIFactor &&
                    p1.arrivalTime <= currentTime &&
                    !p1.processName.equals(currentProcess.processName)) {

                return true;
            }
        }
        System.out.println("No preemption for process: " + currentProcess.processName);
        return false;
    }

    public boolean allProcessDone() {
        return counter == processes.size();
    }

    public void printStatistics() {
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

        System.out.println("\nExecution Statistics:");
        System.out.println("---------------------");

        for (String processName : executionOrder) {
            for (Process p : processes) {
                if (p.processName.equals(processName)) {
                    p.waitingTime = p.completionTime - p.arrivalTime - p.BurstTime;
                    p.turnaroundTime = p.completionTime - p.arrivalTime;
                    totalWaitingTime += p.waitingTime;
                    totalTurnaroundTime += p.turnaroundTime;

                    System.out.println("Process: " + p.processName);
                    System.out.println("  Quantum History: " + p.quantumHistory);
                    System.out.println("  Waiting Time: " + p.waitingTime);
                    System.out.println("  Turnaround Time: " + p.turnaroundTime);
                    break;
                }
            }
        }

        // Calculate and print averages
        double avgWaitingTime = (double) totalWaitingTime / processes.size();
        double avgTurnaroundTime = (double) totalTurnaroundTime / processes.size();

        System.out.println("\nSummary:");
        System.out.println("  Execution Order: " + executionOrder);
        System.out.println("  Average Waiting Time: " + avgWaitingTime);
        System.out.println("  Average Turnaround Time: " + avgTurnaroundTime);
    }

}
