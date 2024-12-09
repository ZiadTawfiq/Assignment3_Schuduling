import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter number of processes: ");
            int n = scanner.nextInt();

            System.out.print("Enter context switch time: ");
            int contextSwitchTime = scanner.nextInt();

            List<Process> processList = new ArrayList<>();
            for (int i = 1; i <= n; i++) {
                System.out.println("Enter details for Process " + i + ":");
                System.out.print("Enter Process Name: ");
                String ProcessName = scanner.next() ;
                System.out.print("Arrival Time: ");
                int arrivalTime = scanner.nextInt();
                System.out.print("Burst Time: ");
                int BurstTime = scanner.nextInt();
                System.out.print("Priority: ");
                int Priority = scanner.nextInt();
                System.out.print("Quantum: ");
                int Quantum = scanner.nextInt();
                System.out.print("---------------------------\n");
                Process process = new Process( ProcessName , arrivalTime ,BurstTime, Priority, Quantum) ;
                processList.add(process) ;


            }





                CPUScheduler cpuScheduler = new CPUScheduler() ;

            boolean running = true;
            while (running) {
                System.out.println("\nSelect Scheduling Algorithm:");
                System.out.println("1. Shortest Job First (SJF)");
                System.out.println("2. Priority Scheduling");
                System.out.println("3. Shortest Remaining Time First (SRTF)");
                System.out.println("4. First Come, Arrival Impact (FCAI)");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                List<Process> copyList = copyProcessList(processList);


                switch (choice) {
                    case 1:
                        cpuScheduler.SJF(copyList);
                        break;
                    case 2:
                        cpuScheduler.priorityScheduler(copyList, contextSwitchTime);
                        break;
                    case 3:
                          cpuScheduler.SRTFScheduler(copyList , contextSwitchTime);
                        break;
                    case 4:

                        FCAIScheduler f = new FCAIScheduler(copyList);
                        f.schedule();
                        break;
                    case 5:
                        running = false;
                        System.out.println("Exiting the program.");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }
        }


    }

    public static List<Process> copyProcessList(List<Process> originalList) {
        List<Process> copy = new ArrayList<>();
        for (Process process : originalList) {
            // Create a new Process instance for each process in the list
            copy.add(new Process(process.processName, process.arrivalTime, process.BurstTime, process.PriorityNum, process.Quantum));
        }
        return copy;
    }
}
