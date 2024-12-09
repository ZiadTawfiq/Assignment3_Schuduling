import java.util.ArrayList;
import java.util.List;

public class Process {
    int processID;
    int arrivalTime;
    int BurstTime;
    int PriorityNum;
    int age;
    int waitingTime = 0;
    int turnaroundTime = 0;
    int remainingBurstTime;
    int completionTime;
    int Quantum ;
    double FCAIFactor;

    public List<Integer> quantumHistory = new ArrayList<>();


    public Process(int processID, int arrivalTime, int burstTime, int priorityNum , int Quantum) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.BurstTime = burstTime;
        this.PriorityNum = priorityNum;
        this.remainingBurstTime = burstTime;
        this.Quantum = Quantum;
        quantumHistory.add(Quantum);
    }

    public void calculateFCAIFactor(double V1, double V2) {
        
        this.FCAIFactor = (Math.ceil(10 - this.PriorityNum) + Math.ceil(this.arrivalTime / V1) + Math.ceil(this.remainingBurstTime / V2));
    }
    public void adjustQuantum(boolean isPreempted, int unusedQuantum) {
        if (isPreempted) {
            this.Quantum += unusedQuantum; 
        } else {
            this.Quantum += 2;
        }
    }


    public void logQuantum() {
        quantumHistory.add(this.Quantum);
    }

    @Override
    public String toString() {
        return "{Process Name: " + processID + ", BurstTime: " + BurstTime + "}";
    }
}
