
public class Process {
    String processName;
    int arrivalTime;
    int BurstTime;
    int PriorityNum;
    int waitingTime = 0;
    int turnaroundTime = 0;
    int age ; 
    int remainingBurstTime;

    public Process(String processName, int arrivalTime, int burstTime, int priorityNum) {
        this.processName = processName;
        this.arrivalTime = arrivalTime;
        this.BurstTime = burstTime;
        this.PriorityNum = priorityNum;
        this.age = 0 ;
        this.remainingBurstTime = burstTime; // Initialize remainingBurstTime
    }

    public double calculateFCAIFactor(double V1, double V2) {
        return (10 - this.PriorityNum) + (this.arrivalTime / V1) + (this.remainingBurstTime / V2);
    }

    @Override
    public String toString() {
        return "{Process Name: " + processName + ", BurstTime: " + BurstTime + "}";
    }
}
