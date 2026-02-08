package services;

import constants.FilePath;
import model.*;
import repository.AppointmentRepository;
import repository.PatientRepository;
import repository.StaffRepository;
import utils.DateUtils;
import utils.Validator;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PatientService {
    private Patient patientInfo = new Patient();

    private final PatientRepository patientRepository = new PatientRepository();
    private final AppointmentRepository appointmentRepository = new AppointmentRepository();
    private final StaffRepository staffRepository = new StaffRepository();

    public PatientService() throws IOException {
    }

    // Information Access
    public void displayMedicalRecords () {
        System.out.println("\n-- Medical Record --");
        System.out.println("Patient ID : " + patientInfo.getPatientId());
        System.out.println("Patient Name : " + patientInfo.getPatientName());
        System.out.println("Date of Birth : " + DateUtils.formatDate(patientInfo.getDateOfBirth()));
        System.out.println("Gender : " + patientInfo.getGender());
        System.out.println("Blood Type : " + patientInfo.getBloodType());
        System.out.println("Contact No. : " + patientInfo.getContactNo());
        System.out.println("Email : " + patientInfo.getEmail());

        for (int i = 0; i < patientInfo.getMedicalRecords().size(); i++) {
            System.out.println("---------------------------------------");
            System.out.println("Diagnosis : " + patientInfo.getMedicalRecords().get(i).getDiagnoses());
            System.out.println("Treatment : " + patientInfo.getMedicalRecords().get(i).getTreatment());
            if (patientInfo.getMedicalRecords().get(i).getPrescription()!= null) {
                System.out.println("Prescription : " + patientInfo.getMedicalRecords().get(i).getPrescriptionAmount() + "x " + patientInfo.getMedicalRecords().get(i).getPrescription());
            }
            System.out.println("---------------------------------------");
        }
    }

    public void loadPatientInfo (String hospitalId) throws IOException {
        patientInfo = patientRepository.loadPatientInfo(hospitalId);
    }

    public void displayUpdateContactInformation (Scanner scanner) throws IOException {
        System.out.println("\n-- Update Contact Information --");
        System.out.println("1 Contact Number");
        System.out.println("2 Email");

        int choice = Validator.validateIntegerInput(scanner);

        while (true) {
            if (choice == 1) {
                System.out.print("Enter New Contact Number: ");
            } else {
                System.out.print("Enter New Email Address: ");
            }

            String input = Validator.validateStringInput(scanner);

            if (choice == 1 && Validator.validateContactNo(input)) {
                updateContactNo(input);
                break;
            } else if (choice == 2 && Validator.validateEmail(input)) {
                updateEmail(input);
                break;
            }
        }
    }

    public void updateContactNo (String contactNo) throws IOException {
        patientInfo.setContactNo(contactNo);
        patientRepository.updatePatientInfo(patientInfo);
    }

    public void updateEmail (String email) throws IOException {
        patientInfo.setEmail(email);
        patientRepository.updatePatientInfo(patientInfo);
    }

    // Appointments

    public void displayUpcomingAppointments (Scanner scanner) throws IOException {
        List<Appointment> upcomingAppointments = appointmentRepository.getUpcomingAppointmentsForPatient(patientInfo);

        if (upcomingAppointments.isEmpty()) {
            System.out.println("No Upcoming Appointments!");
            return;                                             // Empty will skip the rest of the function
        }

        System.out.println("\n--- Upcoming Appointments ---");
        for (Appointment upcomingAppointment : upcomingAppointments) {
            System.out.println(" Appointment with " + upcomingAppointment.getDoctorId()
                    + " at " + upcomingAppointment.getAppointmentTime()
                    + " | Status: " + upcomingAppointment.getAppointmentStatus());
        }

        System.out.println("\n------------------------------");
        System.out.println("1 Reschedule");
        System.out.println("2 Back");

        int input = Validator.validateIntegerInput(scanner);

        if (input == 1) {
            rescheduleAppointment(scanner);
        }
    }

    public boolean displayDoctorsChoiceMenu (Scanner scanner) throws IOException {
        System.out.println("\n---Doctor Choice Menu---");

        List<List<String>> staffList = ExcelReaderWriter.read(FilePath.STAFF_INFO_FILE_PATH);
        List<String> doctorList = staffRepository.getAllDoctors();

        for (int i = 0; i < doctorList.size(); i++) {
            System.out.println((i + 1) + " - " + doctorList.get(i));
        }

        int doctorChoice = Validator.validateIntegerInput(scanner);
        return checkAppointmentSlots(scanner, doctorList.get(doctorChoice-1));
    }

    public boolean checkAppointmentSlots (Scanner scanner, String doctorChoice) throws IOException {
        // Keep it simple, let it choose tomorrow and the day after
        System.out.println("\n-- Available Appointment Slots---");
        List<LocalDate> availableDates = new ArrayList<>();
        availableDates.add(LocalDate.now().plusDays(1));
        availableDates.add(LocalDate.now().plusDays(2));

        for (int i = 0; i < availableDates.size(); i++) {
            System.out.println((i + 1) + " - " + availableDates.get(i));
        }
        int dateChoice = Validator.validateIntegerInput(scanner);

        List<LocalTime> timeSlotsAvailable = appointmentRepository.getAvailableTimeSlots(doctorChoice, availableDates.get(dateChoice - 1));

        // if no time slots, exit method
        if (timeSlotsAvailable.isEmpty()) {
            System.out.println("No Time Slots Available for " + doctorChoice);
            return false;
        }

        // Display Time Slots Available
        System.out.println("\n--- Time Slots Available for " + availableDates.get(dateChoice - 1) + "---");
        for (int i = 0; i < timeSlotsAvailable.size(); i++) {
            System.out.println((i + 1) + " - " + timeSlotsAvailable.get(i));
        }

        // for booking timeslots
        System.out.println("\n--- Book Slot for " + availableDates.get(dateChoice - 1) + "---");
        int selectedTime = Validator.validateIntegerInput(scanner);

        appointmentRepository.bookAppointment(patientInfo.getPatientId(), doctorChoice, availableDates.get(dateChoice - 1), timeSlotsAvailable.get(selectedTime-1));
        return true;
    }



    public void rescheduleAppointment(Scanner scanner) throws IOException{
        List<Appointment> appointmentList = appointmentRepository.getUpcomingAppointmentsForPatient(patientInfo);

        System.out.println("\n--- Upcoming Appointments ---");
        for (int i = 0; i < appointmentList.size(); i++) {
            System.out.println((i + 1) +" Appointment with " + appointmentList.get(i).getDoctorId()
                    + " at " + appointmentList.get(i).getAppointmentTime()
                    + " | Status: " + appointmentList.get(i).getAppointmentStatus());
        }
        System.out.print("Reschedule: ");
        int appointmentToReschedule = Validator.validateIntegerInput(scanner);

        boolean reschedule = displayDoctorsChoiceMenu(scanner);

        if (reschedule) {
            appointmentRepository.freeAppointmentSlot((appointmentList.get(appointmentToReschedule-1)));
        }
    }

    public void cancelAppointment(Scanner scanner) throws IOException{
        List<Appointment> appointmentList = appointmentRepository.getUpcomingAppointmentsForPatient(patientInfo);

        System.out.println("\n--- Upcoming Appointments ---");
        for (int i = 0; i < appointmentList.size(); i++) {
            System.out.println((i + 1) +" Appointment with " + appointmentList.get(i).getDoctorId()
                    + " at " + appointmentList.get(i).getAppointmentTime()
                    + " | Status: " + appointmentList.get(i).getAppointmentStatus());
        }
        System.out.print("Cancel: ");
        int appointmentToCancel = Validator.validateIntegerInput(scanner);

        appointmentRepository.cancelAppointmentSlot((appointmentList.get(appointmentToCancel-1)));
    }


    public void displayPastAppointments(Scanner scanner) throws IOException {
        List<MedicalRecord> pastAppointmentMedicalRecords = new ArrayList<>();

        for (int i = 0; i < patientInfo.getMedicalRecords().size(); i++) {
            if (patientInfo.getMedicalRecords().get(i).getPastAppointment() != null) {
                pastAppointmentMedicalRecords.add(patientInfo.getMedicalRecords().get(i));
            }
        }

        System.out.println("\n---- Past Appointments ----");

        for (MedicalRecord medicalRecord : pastAppointmentMedicalRecords) {
            System.out.println("Appointment was on " + medicalRecord.getPastAppointment().getAppointmentTime());
            System.out.println("Diagnosis: " + medicalRecord.getDiagnoses());
            System.out.println("Treatment: " + medicalRecord.getTreatment());
            System.out.println("Prescription: " + medicalRecord.getPrescriptionAmount() + "x " + medicalRecord.getPrescription());
            System.out.println("Done by: " + medicalRecord.getPastAppointment().getDoctorId());
            System.out.println("---------------------------");
        }
    }
}
