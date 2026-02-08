package services;

import constants.FilePath;
import enums.Gender;
import enums.Role;
import enums.Status;
import model.Appointment;
import model.MedicalRecord;
import model.Medication;
import model.Staff;
import repository.*;
import utils.Validator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AdminService {

    private final AccountRepository accountRepository = new AccountRepository();
    private final StaffRepository staffRepository = new StaffRepository();
    private final MedicationRepository medicationRepository = new MedicationRepository();
    private final AppointmentRepository appointmentRepository = new AppointmentRepository();
    private final MedicalRecordRepository medicalRecordRepository = new MedicalRecordRepository();

    private List<Staff> staffList = staffRepository.getAllStaff();

    public AdminService() throws IOException {
    }

    // Staff Management
    public void displayStaffList (Scanner scanner) {
        staffList = staffRepository.getAllStaff();
        List<Staff> filteredStaffList = staffList;

        boolean exit = false;

        do {
            // Show Staff List
            System.out.println("\n--- Staff List ---");
            for (Staff staff : filteredStaffList) {
                System.out.println(staff.getHospitalId() + " | " + staff.getRole() + " | " + staff.getGender() + " | " + staff.getAge());
            }

            System.out.println("\n--- Staff Filter ---");
            System.out.println("1. Filter By Role");
            System.out.println("2. Filter By Gender");
            System.out.println("3. Filter By Age");
            System.out.println("4. Back");
            System.out.print("Enter your choice: ");
            int choice = Validator.validateIntegerInput(scanner);

            switch (choice) {
                case 1:
                    System.out.println("Enter Role (Doctor/Pharmacist): ");
                    String role = Validator.validateStringInput(scanner);
                    filteredStaffList = filterStaffByRole(staffList, role);
                    break;
                case 2:
                    System.out.println("Enter Gender (Male or Female): ");
                    String gender = Validator.validateStringInput(scanner);
                    filteredStaffList = filterStaffByGender(staffList, gender);
                    break;
                case 3:
                    System.out.println("Enter lower age limit (included in range to display staff): ");
                    int lower = Validator.validateIntegerInput(scanner);
                    System.out.println("Enter upper age limit (included in range to display staff): ");
                    int upper = Validator.validateIntegerInput(scanner);
                    filteredStaffList = filterStaffByAge(staffList, upper, lower);
                    break;
                case 4:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        } while (!exit);
    }

    public void displayCreateNewStaffInput (Scanner scanner) throws Exception {
        System.out.print("Enter Hospital ID: ");
        String staffID = Validator.validateStringInput(scanner);

        // check for Doctor or Pharmacist
        String staffRole;
        do {
            System.out.print("Enter Staff Role (Doctor/Pharmacist): ");
            staffRole = scanner.nextLine();

        } while (!staffRole.toUpperCase().equals(Role.DOCTOR.getDisplayValue()) && !staffRole.toUpperCase().equals(Role.PHARMACIST.getDisplayValue()));

        // check for Male or Female
        String staffGender;
        do {
            System.out.print("Enter Staff Gender (Male/Female): ");
            staffGender = scanner.nextLine();

        } while (!staffGender.toUpperCase().equals(Gender.MALE.getDisplayValue()) && !staffGender.toUpperCase().equals(Gender.FEMALE.getDisplayValue()));

        System.out.print("Enter Staff Age: ");
        int staffAge = Validator.validateIntegerInput(scanner);

        Staff newStaff = new Staff();
        newStaff.setHospitalId(staffID);
        newStaff.setRole(staffRole.toUpperCase());
        newStaff.setGender(staffGender.toUpperCase());
        newStaff.setAge(staffAge);

        accountRepository.saveNewAccount(newStaff);
        staffRepository.saveNewStaff(newStaff);
        staffList = staffRepository.getAllStaff();
    }

    // Method to handle input for removing staff
    public void displayRemoveStaffInput (Scanner scanner) throws IOException {
        if (staffList.isEmpty()) {
            System.out.println("No staff found");
            return;
        }

        // Reveal Staff Choices to Remove
        int choices = 0;
        for (int i = 0; i < staffList.size(); i++) {
            System.out.println("[" + (i+1) + "] " + staffList.get(i).getHospitalId());
            choices++;
        }

        System.out.print("Enter (1 - " + choices + ") to remove staff");

        // Remove Staff
        int staffToRemove = Validator.validateIntegerInput(scanner);
        removeStaff(staffList.get(staffToRemove - 1).getHospitalId());

        // reset the staff list since the changes to the staff list
        staffList = staffRepository.getAllStaff();
    }

    public void displayUpdateStaffInput (Scanner scanner) throws IOException {
        if (staffList.isEmpty()) {
            System.out.println("No staff found");
            return;
        }

        // Reveal Staff Choices to Remove
        int choices = 0;
        for (int i = 0; i < staffList.size(); i++) {
            System.out.println("[" + (i+1) + "] " + staffList.get(i).getHospitalId());
            choices++;
        }

        System.out.print("Enter (1 - " + choices + ") to edit staff");
        Staff staffToEdit = staffList.get(Validator.validateIntegerInput(scanner) - 1);

        // check for Doctor or Pharmacist
        String staffRole;

        while (true) {
            System.out.print("Enter Staff Role (Doctor/Pharmacist): ");
            staffRole = Validator.validateStringInput(scanner);

            if (staffRole.toUpperCase().equals(Role.DOCTOR.getDisplayValue()) || staffRole.toUpperCase().equals(Role.PHARMACIST.getDisplayValue())) {
                staffToEdit.setRole(staffRole.toUpperCase());
                break;
            }
        }

        // check for Male or Female
        String staffGender;
        while (true) {
            System.out.print("Enter Staff Gender (Male/Female): ");
            staffGender = scanner.nextLine();

            if (staffGender.toUpperCase().equals(Gender.MALE.getDisplayValue()) || staffGender.toUpperCase().equals(Gender.FEMALE.getDisplayValue())) {
                staffToEdit.setGender(staffGender.toUpperCase());
                break;
            }
        }

        System.out.print("Enter New Staff Age: ");
        staffToEdit.setAge(Validator.validateIntegerInput(scanner));

        staffRepository.updateStaff(staffToEdit);
        staffList = staffRepository.getAllStaff();
    }

    private void removeStaff (String hospitalId) throws IOException {
        accountRepository.deleteAccount(hospitalId);
        staffRepository.deleteStaff(hospitalId);
    }

    public List<Staff> filterStaffByRole (List<Staff> staffList, String role) {
        List<Staff> filteredStaffList = new ArrayList<>();

        for (Staff staff : staffList) {
            if (staff.getRole().equals(role.toUpperCase())) {
                filteredStaffList.add(staff);
            }
        }

        return filteredStaffList;
    }

    public List<Staff> filterStaffByGender (List<Staff> staffList, String gender) {
        List<Staff> filteredStaffList = new ArrayList<>();

        for (Staff staff : staffList) {
            if (staff.getGender().equals(gender.toUpperCase())) {
                filteredStaffList.add(staff);
            }
        }

        return filteredStaffList;
    }

    public List<Staff> filterStaffByAge (List<Staff> staffList, int upper, int lower) {
        List<Staff> filteredStaffList = new ArrayList<>();

        for (Staff staff : staffList) {
            if (staff.getAge() >= lower && staff.getAge() <= upper) {
                filteredStaffList.add(staff);
            }
        }

        return filteredStaffList;
    }

    public void displayMedicalInventory (Scanner scanner) throws IOException {
        List<Medication> medicineList = medicationRepository.getMedicationList();

        System.out.println("\n--- Medicine List ---");
        for (Medication medicalInventory : medicineList) {
            System.out.println(medicalInventory.getMedicineName() + " | " + medicalInventory.getCurrentStock() + " | " + medicalInventory.getLowStockLevel());
        }
    }

    public void displayCreateNewMedicineInput (Scanner scanner) throws Exception {
        System.out.print("Enter Medicine Name: ");
        String medicineName = Validator.validateStringInput(scanner);

        // check for Doctor or Pharmacist
        System.out.print("Enter Current Stock for " + medicineName +  ":");
        int currentStock = Validator.validateIntegerInput(scanner);

        System.out.print("Enter Low Stock Alert Level for " + medicineName + ":");
        int lowStockAlertLevel = Validator.validateIntegerInput(scanner);

        Medication newMedicine = new Medication();
        newMedicine.setMedicineName(medicineName);
        newMedicine.setCurrentStock(currentStock);
        newMedicine.setLowStockLevel(lowStockAlertLevel);
        newMedicine.setReplenishAmount(0);

        medicationRepository.addNewMedication(newMedicine);
    }

    public void displayUpdateMedicineInput (Scanner scanner) throws IOException {
        List<Medication> medicineList = medicationRepository.getMedicationList();

        if (medicineList.isEmpty()) {
            System.out.println("No medicine found");
            return;
        }

        // Reveal Medicine Choices to Update
        int choices = 0;
        for (int i = 0; i < medicineList.size(); i++) {
            System.out.println("[" + (i+1) + "] " + medicineList.get(i).getMedicineName());
            choices++;
        }

        System.out.print("Enter (1 - " + choices + ") to edit medicine:");
        //Medication medicationToEdit = medicineList.get((Integer.parseInt(scanner.nextLine()) - 1));
        Medication medicationToEdit = medicineList.get((Validator.validateIntegerInput(scanner) - 1));

        // check for new value for current stock
        int currentStock;

        while (true) {
            System.out.print("Enter current stock count: ");
            currentStock = Validator.validateIntegerInput(scanner);
            if(currentStock > 0) {
                medicationToEdit.setCurrentStock(currentStock);
                break;
            }

        }

        // check for new value for low stock alert level
        int lowStockAlertLevel;

        while (true) {
            System.out.print("Enter new low stock alert level count: ");
            lowStockAlertLevel = Validator.validateIntegerInput(scanner);
            if(lowStockAlertLevel > 0) {
                medicationToEdit.setLowStockLevel(lowStockAlertLevel);
                break;
            }

        }

        medicationRepository.updateMedication(medicationToEdit);
    }

    public void displayRemoveMedicineInput (Scanner scanner) throws IOException {
        List<Medication> medicineList = medicationRepository.getMedicationList();

        if (medicineList.isEmpty()) {
            System.out.println("No medication found");
            return;
        }

        // Reveal Medicine Choices to Remove
        int choices = 0;
        for (int i = 0; i < medicineList.size(); i++) {
            System.out.println("[" + (i+1) + "] " + medicineList.get(i).getMedicineName());
            choices++;
        }

        System.out.print("Enter (1 - " + choices + ") to remove medicine: ");

        // Remove Staff
        int medicineToRemove = Validator.validateIntegerInput(scanner);
        medicationRepository.removeMedication(medicineList.get(medicineToRemove - 1).getMedicineName());
    }

    public void approveReplenishRequest(Scanner scanner) throws IOException {
        List<Medication> medicationList = medicationRepository.getMedicationList();
        List<Medication> replenishRequestList = new ArrayList<>();

        for (Medication medication : medicationList) {
            if (medication.getReplenishAmount() > 0) {
                replenishRequestList.add(medication);
                System.out.println((replenishRequestList.size()) + " " + medication.getMedicineName() + " | Requested Replenish Amount: " + medication.getReplenishAmount());
            }
        }

        if (replenishRequestList.isEmpty()) {
            System.out.println("No replenish request found.");
        } else {
            System.out.println("Approve Replenish Request for: ");
            int medicationChoice = Validator.validateIntegerInput(scanner);
            if (medicationChoice > 0 && medicationChoice <= replenishRequestList.size()) {
                List<List<String>> medicalInventoryFile = ExcelReaderWriter.read(FilePath.MEDICAL_INVENTORY_FILE_PATH);

                medicationRepository.approveReplenishRequest(replenishRequestList.get((medicationChoice-1)));
            }
        }

    }

    public void displayAppointmentDetails () throws IOException {
        List<Appointment> allAppointments = appointmentRepository.getAppointmentList();

        // Appointment Details Format (Doctor ID, Status, Appointment Holder(PatientID), Status, Outcome records for completed appointments
        System.out.println("\n--- All Appointments ---");
        for (Appointment appointment : allAppointments) {
            System.out.println("\n---- " + appointment.getAppointmentTime() + "------");
            System.out.println("Doctor ID: " + appointment.getDoctorId());
            System.out.println("Status: " + appointment.getAppointmentStatus());
            if (!appointment.getAppointmentStatus().equals(Status.FREE.getDisplayValue())) {
                System.out.println("Patient ID: " + appointment.getPatientId());
            }

            if (appointment.getAppointmentStatus().equals(Status.COMPLETED.getDisplayValue())) {
                System.out.println("\nAppointment Outcome: ");
                MedicalRecord medicalRecord = medicalRecordRepository.getMedicalRecordByAppointmentId(appointment.getAppointmentId());
                System.out.println("Appointment was on " + medicalRecord.getPastAppointment().getAppointmentTime());
                System.out.println("Diagnosis: " + medicalRecord.getDiagnoses());
                System.out.println("Treatment: " + medicalRecord.getTreatment());
                System.out.println("Prescription: " + medicalRecord.getPrescriptionAmount() + "x " + medicalRecord.getPrescription());
                System.out.println("Done By Doctor: " + medicalRecord.getPastAppointment().getDoctorId());
            }

            System.out.println("---------------------------------");
        }
    }
}
