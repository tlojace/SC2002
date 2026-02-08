package main;

import services.PharmacistService;
import utils.Validator;

import java.io.IOException;
import java.util.Scanner;

public class PharmacistApp {
    private final Scanner scanner;
    private final PharmacistService pharmacistService = new PharmacistService();

    public PharmacistApp(Scanner scanner) throws Exception {
        this.scanner = scanner;
        displayPharmacistMenu();
    }

    public void displayPharmacistMenu() throws IOException {
        boolean loggedOut = false;

        do {
            System.out.println("\nWelcome to Pharmacist Menu!");
            System.out.println("1 View Completed Appointments");
            System.out.println("2 View Medicine Inventory");
            System.out.println("3 Submit Medicine Replenish Request"); // display all medication that are low on stock and key the amount of medication to replenish
            System.out.println("4 Logout");

            int choice = Validator.validateIntegerInput(scanner);

            switch (choice) {
                case 1:
                    pharmacistService.displayAllAppointmentOutcomes(scanner);
                    break;
                case 2:
                    pharmacistService.displayMedicalInventory(scanner);
                    break;
                case 3:
                    pharmacistService.submitReplenishRequest(scanner);
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
