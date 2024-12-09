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
        while (!allProcessDone()) {
            checkInput();
            updateFcai();
            if (!ready.isEmpty()) {
                Process selected = pickProcess(ready);
                CPU.add(selected);
                execute();

                
                System.out.println("-------------------------------");

                for (Process f : CPU) {
                    System.out.println(f);
                }
            } 
             // Increment time if no process is ready

        }

        printStatistics();
    }

    // Process selection methods
    public Process pickProcess(List<Process> list) {
        Process selectedProcess = null;
        

        if (!CPU.isEmpty()) {
            selectedProcess = CPU.get(0);
            CPU.remove(selectedProcess);
        } else {
            return ready.get(0);
        }

        if (selectedProcess != null) {
            System.out.println("Selected process: " + selectedProcess.processName +
                    " (FCAI Factor: " + selectedProcess.FCAIFactor + ")");
        }
        return selectedProcess;
    }

    // Execution logic
    public void execute() {
        
        Process p = CPU.get(0);
        System.out.println("Currently executing " + p.processName);

        int temp2 = p.Quantum;
        int executionTime = (int) Math.min(Math.ceil(0.4 * p.Quantum), p.remainingBurstTime);
        p.remainingBurstTime -= executionTime;
        temp2 -= executionTime;
        currentTime += executionTime;

        checkInput();
        while (temp2 > 0 && p.remainingBurstTime > 0) {
            checkInput();

            if (checkPreemption(p) || temp2 == 0) {
                break;
            }
            executionTime++;
            currentTime++;
            p.remainingBurstTime--;
            temp2--;
        }

        
        if(p.remainingBurstTime != 0){
        updateQuantum(p, p.Quantum - executionTime, temp2);
        p.quantumHistory.add(p.Quantum);
        }
        if (checkPreemption(p)) {
            Process preemptingProcess = pickByLessFcai(ready, p);
            if (preemptingProcess != null) {
                ready.remove(p);
                ready.add(p);
                CPU.remove(p);
                CPU.add(preemptingProcess);
                System.out.println("Preempting process added to CPU: " + preemptingProcess.processName);
            }
        }

        ready.remove(p);
        ready.add(p);
        if (p.remainingBurstTime == 0) {
            p.completionTime = currentTime;
            ready.remove(p);
            counter++; 

            System.out.println(p.processName + " done");
            executionOrder.add(p.processName);  
        }

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