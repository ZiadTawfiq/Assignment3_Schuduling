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
                System.out.println("Enter Process Name");
                String ProcessName = scanner.next() ;                
                System.out.print("Arrival Time: ");
                int arrivalTime = scanner.nextInt();
                System.out.print("Burst Time: ");
                int BurstTime = scanner.nextInt();
                System.out.print("Priority: ");
                int Priority = scanner.nextInt();
                System.out.print("---------------------------\n");
                Process process = new Process(ProcessName  , arrivalTime ,BurstTime, Priority) ;
                processList.add(process) ;


            }


            CPUScheduler cpuScheduler = new CPUScheduler() ;
            cpuScheduler.SJF(processList);
           cpuScheduler.SRTFScheduler(processList, contextSwitchTime);
            cpuScheduler.FCAIScheduler(processList, contextSwitchTime);
        }
    }
}
