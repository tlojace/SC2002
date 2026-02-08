package repository;

import constants.FilePath;
import model.Doctor;
import model.Patient;
import services.ExcelReaderWriter;
import utils.DateUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PatientRepository {

    private final String patientInfoFilePath = FilePath.PATIENT_INFO_FILE_PATH;
    private List<String> patientInfoHeader = new ArrayList<>();

    private final MedicalRecordRepository medicalRecordRepository = new MedicalRecordRepository();
    private final AppointmentRepository appointmentRepository = new AppointmentRepository();

    private List<Patient> patientList = new ArrayList<>();

    public PatientRepository() throws IOException {
        patientList = loadPatientList();
    }

    public List<Patient> loadPatientList () throws IOException {
        List<List<String>> patientInfoFile = ExcelReaderWriter.read(patientInfoFilePath);
        List<Patient> patientList = new ArrayList<>();

        for (int i = 0; i < patientInfoFile.size(); i++) {
            if (i == 0) {
                patientInfoHeader = patientInfoFile.get(i);
                continue;
            }
            Patient patientInfo = new Patient();
            patientInfo.setHospitalId(patientInfoFile.get(i).get(0));
            patientInfo.setPatientId(patientInfoFile.get(i).get(1));
            patientInfo.setPatientName(patientInfoFile.get(i).get(2));
            patientInfo.setGender(patientInfoFile.get(i).get(3));

            // Define a DateTimeFormatter for the "dd/MM/yyyy" format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yyyy");

            // Parse the date string to LocalDate
            LocalDate dateOfBirth = LocalDate.parse(patientInfoFile.get(i).get(4), DateUtils.getDateFormatter());
            patientInfo.setDateOfBirth(dateOfBirth);
            patientInfo.setBloodType(patientInfoFile.get(i).get(5));
            patientInfo.setEmail(patientInfoFile.get(i).get(6));
            patientInfo.setContactNo(patientInfoFile.get(i).get(7));

            patientInfo.setMedicalRecords(medicalRecordRepository.getMedicalRecordsByPatientId(patientInfo.getPatientId()));
            patientList.add(patientInfo);
        }
        return patientList;
    }

    private void savePatients() throws IOException {
        List<List<String>> patientFile = new ArrayList<>();

        patientFile.add(patientInfoHeader);
        for (Patient patient : patientList) {
            List<String> patientRow = new ArrayList<>();
            patientRow.add(patient.getHospitalId());
            patientRow.add(patient.getPatientId());
            patientRow.add(patient.getPatientName());
            patientRow.add(patient.getGender());
            patientRow.add(patient.getDateOfBirth().format(DateUtils.getDateFormatter()));
            patientRow.add(patient.getBloodType());
            patientRow.add(patient.getEmail());
            patientRow.add(patient.getContactNo());
            patientFile.add(patientRow);
        }

        ExcelReaderWriter.write(patientFile, patientInfoFilePath);
    }

    // Only Class to use another repository to get MedicalRecords of Patient
    public Patient loadPatientInfo (String hospitalId) throws IOException {
        for (Patient patient : patientList) {
            if (patient.getHospitalId().equals(hospitalId)) {
                return patient;
            }
        }
        return null;
    }

    public List<Patient> retrievePatientsUnderDoctor (Doctor doctor) throws IOException {
        patientList = loadPatientList();

        List<Patient> patientUnderDoctor = new ArrayList<>();
        List<String> patientIdsUnderDoctor = appointmentRepository.getPatientListOfDoctor(doctor.getDoctorId());

        for (Patient patient : patientList) {
            if (patientIdsUnderDoctor.contains(patient.getPatientId())) {
                patientUnderDoctor.add(patient);
            }
        }
        return patientUnderDoctor;
    }

    public void updatePatientInfo (Patient patient) throws IOException {
        for (Patient patientInfo : patientList) {
            if (patientInfo.getPatientId().equals(patient.getPatientId())) {
                patientInfo = patient;
                break;
            }
        }

        savePatients();
    }
}
