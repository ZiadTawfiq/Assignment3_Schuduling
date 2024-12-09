import java.util.*;

public class CPUScheduler {

    public void SJF(List<Process>ProcessList){

        int currentTime = 0;
        int totalTurnaroundTime = 0;
        int totalWaitingTime = 0 ;
        int agingThreshold = 10;
        int maxAge = 20;

        ProcessList.sort(Comparator.comparingInt(p -> p.BurstTime));

        System.out.println("Schedule:");

        for (Process process : ProcessList) {
            if (currentTime < process.arrivalTime) {
                currentTime = process.arrivalTime;
            }
            if (process.age >= agingThreshold) {
                process.BurstTime = process.BurstTime - 1;
                process.age = 0;
            } else {
                process.age += process.BurstTime;
            }

            if (process.age > maxAge) {
                process.age = maxAge;
            }

            process.waitingTime = currentTime - process.arrivalTime;
            totalWaitingTime += process.waitingTime;

            currentTime += process.BurstTime;
            process.turnaroundTime = currentTime - process.arrivalTime;
            totalTurnaroundTime += process.turnaroundTime;

             System.out.println("Process "+process.processName + "\t" + "Arrival Time = "+ process.arrivalTime + "\t" +"Brust Time = "+ process.BurstTime + "\t" +
                    "Priority = "+ process.PriorityNum + "\t" +"Waiting Time = "+ process.waitingTime + "\t" + "TurnAround Time = " +process.turnaroundTime);

        }

        int numProcesses = ProcessList.size();
        double avgWaitingTime = (double) totalWaitingTime / numProcesses;
        double avgTurnaroundTime = (double) totalTurnaroundTime / numProcesses;

        System.out.println("Avg Waiting Time: " + avgWaitingTime);
        System.out.println("Avg Turnaround Time: " + avgTurnaroundTime);
    }



    public void priorityScheduler(List<Process> processesList, int contextSwitchTime) {

        processesList.sort(Comparator.comparingInt((Process p) -> p.arrivalTime)
                .thenComparingInt(p -> p.PriorityNum));

        int currentTime = 0;
        List<Process> executedProcesses = new ArrayList<>();

        while (!processesList.isEmpty()) {
            Process selectedProcess = null;

            for (Process p : processesList) {
                if (p.arrivalTime <= currentTime) {// if the process arrived
                    if (selectedProcess == null || p.PriorityNum < selectedProcess.PriorityNum) {
                        selectedProcess = p;
                    }
                }
            }

            if (selectedProcess != null) {
                selectedProcess.waitingTime = currentTime - selectedProcess.arrivalTime;
                currentTime += selectedProcess.BurstTime;
                selectedProcess.turnaroundTime = currentTime - selectedProcess.arrivalTime;

                executedProcesses.add(selectedProcess);
                processesList.remove(selectedProcess);

                currentTime += contextSwitchTime;
            } else {
                currentTime++;
            }
        }


        double totalWaitingTime = 0, totalTurnAroundTime = 0;
        for (Process p : executedProcesses) {
            System.out.println("Process "+p.processName + "\t" + "Arrival Time = "+ p.arrivalTime + "\t" +"Brust Time = "+ p.BurstTime + "\t" +
                    "Priority = "+ p.PriorityNum + "\t" +"Waiting Time = "+ p.waitingTime + "\t" + "TurnAround Time = " +p.turnaroundTime);
            totalWaitingTime += p.waitingTime;
            totalTurnAroundTime += p.turnaroundTime;
        }

        double averagWaitingTime = totalWaitingTime / executedProcesses.size();
        double averageTurnAroundTime = totalTurnAroundTime / executedProcesses.size();

        System.out.println("\nAverage Waiting Time: " + averagWaitingTime);
        System.out.println("Average Turnaround Time: " + averageTurnAroundTime);


    }
    public double calculateV1(List<Process> processesList) {
        int maxArrivalTime = processesList.stream()
                .mapToInt(p -> p.arrivalTime)
                .max()
                .orElse(0);
        return (double) maxArrivalTime / 10;
    }

    // Calculate V2
    public double calculateV2(List<Process> processesList) {
        int maxBurstTime = processesList.stream()
                .mapToInt(p -> p.BurstTime)
                .max()
                .orElse(0);
        return (double) maxBurstTime / 10;
    }


    public void SRTFScheduler(List<Process> ProcessList, int contextSwitchTime) {
        int size = ProcessList.size();
        System.out.println("--------------------------------");
        System.out.println("SRTF Scheduling: ");
        int currentTime = 0;
        int completedProcesses = 0;
        boolean[] isCompleted = new boolean[size];

        for (Process process : ProcessList) {
            process.remainingBurstTime = process.BurstTime;
            process.waitingTime = 0;
        }

        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;
        Process prevShortestProcess = null;
        Process shortestProcess = null;

        // starvation handle
        final int MAX_WT = 100;
        final int AGE_INC = 2;

        while (completedProcesses < size) {
            prevShortestProcess = shortestProcess;
            shortestProcess = null;

            // shortest remaining time
            for (Process p : ProcessList) {
                if (!isCompleted[ProcessList.indexOf(p)] && p.arrivalTime <= currentTime) {
                    if (shortestProcess == null || p.remainingBurstTime < shortestProcess.remainingBurstTime) {
                        shortestProcess = p;
                    }
                }
            }

            // for starvation handle
            for (Process p : ProcessList) {
                if (!isCompleted[ProcessList.indexOf(p)] && p != shortestProcess && p.arrivalTime <= currentTime) {
                    p.waitingTime++;

                    if (p.waitingTime > MAX_WT) {
                        currentTime += contextSwitchTime;
                        p.remainingBurstTime = Math.max(1, p.remainingBurstTime - AGE_INC);
                        p.waitingTime = 0;
                    }
                }
            }

            if (prevShortestProcess != null && completedProcesses < size && prevShortestProcess != shortestProcess) {
                currentTime += contextSwitchTime;
            }

            if (shortestProcess != null) {

                shortestProcess.remainingBurstTime--;
                currentTime++;

                // process is completed
                if (shortestProcess.remainingBurstTime == 0) {
                    isCompleted[ProcessList.indexOf(shortestProcess)] = true;
                    completedProcesses++;

                    shortestProcess.turnaroundTime = currentTime - shortestProcess.arrivalTime;
                    shortestProcess.waitingTime = shortestProcess.turnaroundTime - shortestProcess.BurstTime;

                    totalWaitingTime += shortestProcess.waitingTime;
                    totalTurnaroundTime += shortestProcess.turnaroundTime;
                }
            } else {
                currentTime++;
                System.out.println("No process in ready qeue at time " + currentTime);
            }
        }

        System.out.println("Final Results:");
        for (Process p : ProcessList) {
           System.out.println("Process "+p.processName + "\t" + "Arrival Time = "+ p.arrivalTime + "\t" +"Brust Time = "+ p.BurstTime + "\t" +
                    "Priority = "+ p.PriorityNum + "\t" +"Waiting Time = "+ p.waitingTime + "\t" + "TurnAround Time = " +p.turnaroundTime);
        }

        double avgWaitingTime = (double) totalWaitingTime / size;
        double avgTurnaroundTime = (double) totalTurnaroundTime / size;
        System.out.println("Average Waiting Time: " + avgWaitingTime);
        System.out.println("Average Turnaround Time: " + avgTurnaroundTime);
    }




}

