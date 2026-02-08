package services;

import enums.Status;
import model.*;
import repository.*;
import utils.DateUtils;
import utils.Validator;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class DoctorService {
    private Doctor doctorInfo = new Doctor();

    private final StaffRepository staffRepository = new StaffRepository();
    private final PatientRepository patientRepository = new PatientRepository();
    private final MedicalRecordRepository medicalRecordRepository = new MedicalRecordRepository();
    private final AppointmentRepository appointmentRepository = new AppointmentRepository();
    private final MedicationRepository medicationRepository = new MedicationRepository();

    public DoctorService() throws IOException {
    }

    public void loadDoctorInfo (String hospitalId) throws IOException {
        doctorInfo = staffRepository.getDoctorByHospitalId(hospitalId);
    }

    // Medical Record Management

    public void viewPatientList(Scanner scanner) throws IOException {
        List<Patient> patientList = patientRepository.retrievePatientsUnderDoctor(doctorInfo);

        System.out.println("\n -- Patients under your care --");
        for (int i = 0; i < patientList.size(); i++) {
            System.out.println("(" + (i+1) + ") " + patientList.get(i).getPatientName());
        }
        System.out.println("(" + (patientList.size() + 1) + ") Back");

        // Options on what to do after selecting the patient, update etc...
        while (true) {
            int choice = Validator.validateIntegerInput(scanner);
            if (choice == patientList.size() + 1) break;
            if (choice <= patientList.size()) {
                displayUpdatePatientMenu(patientList.get(choice-1), scanner);
                break;
            }
        }
    }

    public void displayUpdatePatientMenu (Patient patient, Scanner scanner) throws IOException {
        System.out.println("\n --- Patient Medical Records ---");
        System.out.println("Patient ID : " + patient.getPatientId());
        System.out.println("Patient Name : " + patient.getPatientName());
        System.out.println("Date of Birth : " + DateUtils.formatDate(patient.getDateOfBirth()));
        System.out.println("Gender : " + patient.getGender());
        System.out.println("Blood Type : " + patient.getBloodType());
        System.out.println("Contact No. : " + patient.getContactNo());
        System.out.println("Email : " + patient.getEmail());

        for (int i = 0; i < patient.getMedicalRecords().size(); i++) {
            System.out.println("----------------------------------------");
            System.out.println("Diagnosis : " + patient.getMedicalRecords().get(i).getDiagnoses());
            System.out.println("Treatment : " + patient.getMedicalRecords().get(i).getTreatment());
            if (patient.getMedicalRecords().get(i).getPrescription()!= null) {
                System.out.println("Prescription : " + patient.getMedicalRecords().get(i).getPrescriptionAmount() + "x " + patient.getMedicalRecords().get(i).getPrescription());
            }
            System.out.println("---------------------------------------");
        }

        System.out.println("1 - Add New Diagnosis");
        System.out.println("2 - Back");

        int choice = Validator.validateIntegerInput(scanner);
        if (choice == 1) {
            addMedicalRecord(patient.getPatientId(), scanner);
        } else if (choice != 2) {
           System.out.println("Invalid Choice");
        }

    }

    public void viewSchedule(Scanner scanner) throws IOException {
        viewSchedule(scanner, null);
    }
    // Appointment Management
    public void viewSchedule(Scanner scanner, LocalDate dateSelected) throws IOException {
        LocalDate date = dateSelected;

        System.out.println("\n-- Schedule ---");

        if (date == null) {
            List<LocalDate> availableDates = new ArrayList<>();
            availableDates.add(LocalDate.now());
            availableDates.add(LocalDate.now().plusDays(1));
            availableDates.add(LocalDate.now().plusDays(2));

            for (int i = 0; i < availableDates.size(); i++) {
                System.out.println((i + 1) + " - " + availableDates.get(i));
            }
            int dateChoice = Validator.validateIntegerInput(scanner);

            date = availableDates.get((dateChoice-1));
        }

        List<Appointment> scheduleList = appointmentRepository.getDoctorSchedule(doctorInfo, date);

        if (scheduleList.isEmpty()) {
            System.out.println("Schedule is free for " + date);
        } else{
            // Sort times in ascending order (earliest to latest)
            scheduleList.sort(Comparator.comparing(appointment -> LocalDateTime.parse(appointment.getAppointmentTime(), DateUtils.getLocalDateTimeFormatter())));

            // Print sorted times
            for (Appointment appointment : scheduleList) {
                System.out.println("\n---------------------------");
                System.out.println("Appointment Time: " + appointment.getAppointmentTime());
                System.out.println("Status: " + appointment.getAppointmentStatus());
                if (appointment.getAppointmentStatus().equals(Status.PENDING.getDisplayValue()) ||
                        appointment.getAppointmentStatus().equals(Status.CONFIRMED.getDisplayValue())) {
                    System.out.println("Patient ID: " + appointment.getPatientId());
                }

                System.out.println("---------------------------");
            }
        }

        displayUpdateScheduleMenu(scheduleList, date, scanner);
    }

    private void displayUpdateScheduleMenu(List<Appointment> schedule, LocalDate selectedDate, Scanner scanner) throws IOException {
        System.out.println("1 Approve/Decline Appointment");
        System.out.println("2 Add Timeslot");
        System.out.println("3 Remove Timeslot");
        System.out.println("4 Back");

        int choice = Validator.validateIntegerInput(scanner);

        if (choice == 1) {
            displayApprovalOfAppointment(schedule, selectedDate, scanner);
        }
        else if (choice == 2) {
            displayAddTimeSlotMenu(schedule, selectedDate, scanner);
        }
        else if (choice == 3) {
            displayRemoveTimeSlot(schedule, selectedDate, scanner);
        }
    }

    private void displayAddTimeSlotMenu(List<Appointment> schedule, LocalDate selectedDate, Scanner scanner) throws IOException {
        System.out.println("\n--- Available Timeslots on " + selectedDate + " ---");
        List<LocalTime> generatedTimeSlots = generateTimeSlots(schedule);

        if(generatedTimeSlots.isEmpty()) {
            System.out.println("There are no available time slots for " + selectedDate);
            return;
        }

        for (int i = 0; i < generatedTimeSlots.size(); i++) {
            System.out.println((i + 1) + " - " + generatedTimeSlots.get(i));
        }

        int timeSlotChoice = Validator.validateIntegerInput(scanner);
        if (timeSlotChoice < 1 || timeSlotChoice > generatedTimeSlots.size()) return;

        // Creation of Time Slot
        Appointment newSchedule = new Appointment();
        newSchedule.setDoctorId(doctorInfo.getDoctorId());
        newSchedule.setPatientId(Status.FREE.getDisplayValue());
        newSchedule.setAppointmentTime(DateUtils.joinDateAsString(selectedDate, generatedTimeSlots.get(timeSlotChoice-1)));
        newSchedule.setAppointmentStatus(Status.FREE.getDisplayValue());
        appointmentRepository.addTimeSlotToSchedule(newSchedule);
        System.out.println("Schedule Updated!: ");
        viewSchedule(scanner, selectedDate);
    }

    private List<LocalTime> generateTimeSlots(List<Appointment> schedule) {
        List<LocalTime> unavailableTimeSlots = new ArrayList<>();
        List<LocalTime> allTimeSlots = new ArrayList<>();

        for (Appointment appointment : schedule) {
            unavailableTimeSlots.add(DateUtils.extractTime(appointment.getAppointmentTime()));
        }

        // Generate all hourly time slots from 8 AM to 5 PM
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(17, 0); // 5 PM

        while (!startTime.isAfter(endTime.minusHours(1))) {
            allTimeSlots.add(startTime);
            startTime = startTime.plusHours(1);
        }

        // Remove unavailable slots from allTimeSlots
        allTimeSlots.removeAll(unavailableTimeSlots);

        return allTimeSlots;
    }

    public void displayRemoveTimeSlot (List<Appointment> schedule, LocalDate selectedDate, Scanner scanner) throws IOException {
        List<Appointment> availableTimeSlotsToRemove = new ArrayList<>();

        for (Appointment appointment : schedule) {
            if (appointment.getAppointmentStatus().equals(Status.FREE.getDisplayValue())) {
                availableTimeSlotsToRemove.add(appointment);
                System.out.println(availableTimeSlotsToRemove.size() + " - " + appointment.getAppointmentTime());
            }
        }

        if (availableTimeSlotsToRemove.isEmpty()) {
            System.out.println("You cannot remove any time slots for " + selectedDate);
            viewSchedule(scanner, selectedDate);
            return;
        }

        int choice = Validator.validateIntegerInput(scanner);
        if (choice > 0 && choice <= availableTimeSlotsToRemove.size()) {
            appointmentRepository.removeTimeSlotFromSchedule(availableTimeSlotsToRemove.get(choice-1));
            System.out.println("Time Slot Removed From Schedule!");
            viewSchedule(scanner, selectedDate);
        }
    }

    public void displayApprovalOfAppointment (List<Appointment> schedule, LocalDate selectedDate, Scanner scanner) throws IOException {
        List<Appointment> pendingAppointments = new ArrayList<>();
        for (Appointment appointment : schedule) {
            if (appointment.getAppointmentStatus().equals(Status.PENDING.getDisplayValue())) {
                pendingAppointments.add(appointment);
                System.out.println(pendingAppointments.size() + " - " + appointment.getAppointmentTime() + " with " + appointment.getPatientId());
            }
        }

        if (pendingAppointments.isEmpty()) {
            System.out.println("No Pending Appointments for " + selectedDate);
            viewSchedule(scanner, selectedDate);
            return;
        }

        int choice = Validator.validateIntegerInput(scanner);

        if (choice < 1 || choice > pendingAppointments.size()) return;

        System.out.println("1 Accept"); // Accept change to Confirmed
        System.out.println("2 Decline"); // Decline straight away free up timeslot
        int approvalChoice = Validator.validateIntegerInput(scanner);

        if (approvalChoice == 1) {
            appointmentRepository.approveAppointment(pendingAppointments.get(choice-1));
            System.out.println("Appointment Approved!");
        } else if (approvalChoice == 2) {
            appointmentRepository.declineAppointment(pendingAppointments.get(choice-1));
            System.out.println("Appointment Declined!");
        } else {
            System.out.println("Invalid choice");
        }

        if (approvalChoice == 1 || approvalChoice == 2) {
            viewSchedule(scanner, selectedDate);
        }
    }

    public void displayUpcomingAppointments(Scanner scanner) throws IOException {
        List<Appointment> upcomingAppointments = appointmentRepository.getUpcomingAppointments(doctorInfo.getDoctorId());
        if (upcomingAppointments.isEmpty()) {
            System.out.println("No Upcoming Appointments for Today");
            return;
        }


        for (int i = 0; i < upcomingAppointments.size(); i++) {
            System.out.println((i+1) + " - " + upcomingAppointments.get(i).getPatientId() + " at " + upcomingAppointments.get(i).getAppointmentTime());
        }
        System.out.println((upcomingAppointments.size() + 1) + " - Back");
        int choice = Validator.validateIntegerInput(scanner);

        if (choice > 0 && choice <= upcomingAppointments.size()) {
            System.out.println("1 - Complete Appointment");
            System.out.println("2 - Back");
            int completeChoice = Validator.validateIntegerInput(scanner);
            if (completeChoice == 1) {
                addMedicalRecord(upcomingAppointments.get((choice-1)), scanner);
                appointmentRepository.completeAppointment(upcomingAppointments.get((choice-1)));
            } else {
                System.out.println("Invalid choice");
            }
        } else if (choice > (upcomingAppointments.size()+1)) {
            System.out.println("Invalid choice");
        }
    }

    public void addMedicalRecord (String patientId, Scanner scanner) throws IOException {
        addMedicalRecord(null, scanner, patientId);
    }

    public void addMedicalRecord (Appointment appointment, Scanner scanner) throws IOException {
        addMedicalRecord(appointment, scanner, appointment.getPatientId());
    }

    public void addMedicalRecord (Appointment appointment, Scanner scanner, String patientId) throws IOException {
        System.out.println("Enter Diagnosis: ");
        String diagnosis = Validator.validateStringInput(scanner);

        System.out.println("Enter Treatment: ");
        String treatment = Validator.validateStringInput(scanner);

        List<Medication> medicationList = medicationRepository.getMedicationList();
        System.out.println("Prescription Choice: ");
        for (int i = 0; i < medicationList.size(); i++) {
            System.out.println((i+1) + " - " + medicationList.get(i).getMedicineName());
        }
        int prescriptionChoice = -111;
        do {
            if (prescriptionChoice != -111) {
                System.out.println("Invalid Input! Enter Again: ");
            }
            prescriptionChoice = Validator.validateIntegerInput(scanner);
        } while (prescriptionChoice <= 0 || prescriptionChoice > medicationList.size());

        System.out.println("Enter Amount to Prescribe: ");
        int prescriptionAmount = -111;
        do {
            if (prescriptionAmount != - 111) {
                System.out.println("Invalid Amount! Enter Again: ");
            }
            prescriptionAmount = Validator.validateIntegerInput(scanner);
        } while (prescriptionAmount <= 0);

        MedicalRecord newMedicalRecord = new MedicalRecord();
        newMedicalRecord.setPatientId(patientId);
        newMedicalRecord.setDiagnoses(diagnosis);
        newMedicalRecord.setTreatment(treatment);
        newMedicalRecord.setPrescription(medicationList.get(prescriptionChoice-1).getMedicineName());
        newMedicalRecord.setPrescriptionAmount(prescriptionAmount);
        newMedicalRecord.setStatus(Status.PENDING.getDisplayValue());
        if(appointment != null) newMedicalRecord.setPastAppointment(appointment);
        medicalRecordRepository.insertNewMedicalRecord(newMedicalRecord);
    }
}
