package main;

import services.DoctorService;
import utils.Validator;

import java.io.IOException;
import java.util.Scanner;

public class DoctorApp {
    private final Scanner scanner;
    private final DoctorService doctorService = new DoctorService();

    public DoctorApp(String hospitalId, Scanner scanner) throws Exception {
        this.scanner = scanner;
        doctorService.loadDoctorInfo(hospitalId);
        displayDoctorMenu();
    }

    public void displayDoctorMenu() throws IOException {
        boolean loggedOut = false;

        do {
            System.out.println("\nWelcome to Doctor Menu!");
            System.out.println("1 View Patient List");
            System.out.println("2 View Available Schedule");
            System.out.println("3 View Upcoming Appointments");
            System.out.println("4 Logout");

            int choice = Validator.validateIntegerInput(scanner);

            switch (choice) {
                case 1:
                    doctorService.viewPatientList(scanner);
                    break;
                case 2:
                    doctorService.viewSchedule(scanner);
                    break;
                case 3:
                    doctorService.displayUpcomingAppointments(scanner);
                    break;
                case 4:
                    loggedOut = true;
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        } while (!loggedOut);


    }
}
