package main;

import services.PatientService;
import utils.Validator;

import java.io.IOException;
import java.util.Scanner;

public class PatientApp {
    private final Scanner scanner;
    private final PatientService patientService = new PatientService();

    public PatientApp(String hospitalId, Scanner scanner) throws Exception {
        this.scanner = scanner;
        patientService.loadPatientInfo(hospitalId);
        displayPatientMenu();
    }

    public void displayPatientMenu() throws IOException {
        boolean loggedOut = false;

        do {
            System.out.println("\nWelcome to Patient Menu!");
            System.out.println("1 View Medical Records");
            System.out.println("2 Update Contact Information");
            System.out.println("3 View Upcoming Appointments");
            System.out.println("4 Book Appointment");
            System.out.println("5 Reschedule Appointment");
            System.out.println("6 Cancel Appointment");
            System.out.println("7 View Appointment Outcome Records");
            System.out.println("8 Logout");

            int choice = Validator.validateIntegerInput(scanner);

            switch (choice) {
                case 1:
                    patientService.displayMedicalRecords();
                    break;
                case 2:
                    patientService.displayUpdateContactInformation(scanner);
                    break;
                case 3:
                    patientService.displayUpcomingAppointments(scanner);
                    break;
                case 4:
                    patientService.displayDoctorsChoiceMenu(scanner);
                    break;
                case 5:
                    patientService.rescheduleAppointment(scanner);
                    break;
                case 6:
                    patientService.cancelAppointment(scanner);
                    break;
                case 7:
                    patientService.displayPastAppointments(scanner);
                    break;
                case 8:
                    loggedOut = true;
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        } while (!loggedOut);
    }
}
