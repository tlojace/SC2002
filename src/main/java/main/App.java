package main;

import java.util.Scanner;

import enums.Role;
import model.User;
import services.AuthenticationService;
import utils.Validator;

public class App {
	static Scanner scanner = new Scanner(System.in);
	static AuthenticationService authenticator = new AuthenticationService();

	public static void main(String[] args) throws Exception {
		boolean systemOn = true;
		
		while (systemOn) {
			System.out.println("\nWelcome to Hospital Management System!");
			System.out.println("[1] Login");
			System.out.println("[2] System Shut Down");
			int choice = Validator.validateIntegerInput(scanner);
			
			switch (choice) {
				case 1: 
					enterLoginDetails(); 
					break;
				case 2:
					System.out.println("System Shutting Down....");
					systemOn = false;
					break;
				default:
					System.out.println("Invalid choice");
					break;
			}
		}
	}
	
	private static void enterLoginDetails() throws Exception {
		scanner = new Scanner(System.in);
		System.out.print("Hospital ID: ");
		String hospitalId = scanner.nextLine();  // Wait for user input
		System.out.print("Password: ");
		String password = scanner.nextLine();  // Wait for user input
		
		User userLoggedIn = authenticator.login(hospitalId, password);
		if(userLoggedIn == null){
			System.out.println("Invalid ID or Password \n");
		}

		if (userLoggedIn != null) {
			if (userLoggedIn.getRole().equals(Role.ADMINISTRATOR.getDisplayValue())) {
				// Start Admin
				new AdminApp(scanner);
			}

			if (userLoggedIn.getRole().equals(Role.PATIENT.getDisplayValue())) {
				// Start Patient
				new PatientApp(userLoggedIn.getHospitalId(), scanner);
			}

			if (userLoggedIn.getRole().equals(Role.DOCTOR.getDisplayValue())) {
				new DoctorApp(userLoggedIn.getHospitalId(), scanner);
			}

			if (userLoggedIn.getRole().equals(Role.PHARMACIST.getDisplayValue())) {
				new PharmacistApp(scanner);
			}
		}
	}
}
