package repository;

import constants.FilePath;
import enums.Status;
import model.Appointment;
import model.MedicalRecord;
import services.ExcelReaderWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordRepository {
    private final String medicalRecordFilePath = FilePath.MEDICAL_RECORDS_FILE_PATH;
    private List<String> medicalRecordFileHeader = new ArrayList<>();

    // Stores all the Medical Records, don't need to keep loading, but save after any changes.
    private List<MedicalRecord> medicalRecordList = new ArrayList<>();

    private final AppointmentRepository appointmentRepository = new AppointmentRepository();

    public MedicalRecordRepository() throws IOException {
        this.medicalRecordList = loadMedicalRecords();
    }

    private List<MedicalRecord> loadMedicalRecords() throws IOException {
        List<MedicalRecord> medicalRecordList = new ArrayList<>();
        List<List<String>> medicalRecordFile = ExcelReaderWriter.read(medicalRecordFilePath);

        for (int i = 0; i < medicalRecordFile.size(); i++) {
            if (i == 0) {
                medicalRecordFileHeader = medicalRecordFile.get(i);
                continue;
            }

            MedicalRecord medicalRecord = new MedicalRecord();
            medicalRecord.setPatientId(medicalRecordFile.get(i).get(0));
            medicalRecord.setDiagnoses(medicalRecordFile.get(i).get(1));
            medicalRecord.setTreatment(medicalRecordFile.get(i).get(2));
            medicalRecord.setPrescription(medicalRecordFile.get(i).get(3));
            medicalRecord.setPrescriptionAmount((int) Float.parseFloat(medicalRecordFile.get(i).get(4)));
            medicalRecord.setStatus(medicalRecordFile.get(i).get(5));
            medicalRecord.setId((int) Float.parseFloat(medicalRecordFile.get(i).get(6)));
            if (medicalRecordFile.get(i).size() > 7) medicalRecord.setPastAppointment(appointmentRepository.getAppointmentById((int) Float.parseFloat(medicalRecordFile.get(i).get(7))));
            medicalRecordList.add(medicalRecord);
        }

        return medicalRecordList;
    }

    private void saveMedicalRecords() throws IOException {
        List<List<String>> medicalRecordFile = new ArrayList<>();

        medicalRecordFile.add(medicalRecordFileHeader);
        for (MedicalRecord medicalRecord : medicalRecordList) {
            List<String> medicalRecordRow = new ArrayList<>();
            medicalRecordRow.add(medicalRecord.getPatientId());
            medicalRecordRow.add(medicalRecord.getDiagnoses());
            medicalRecordRow.add(medicalRecord.getTreatment());
            medicalRecordRow.add(medicalRecord.getPrescription());
            medicalRecordRow.add(String.valueOf(medicalRecord.getPrescriptionAmount()));
            medicalRecordRow.add(medicalRecord.getStatus());
            medicalRecordRow.add(String.valueOf(medicalRecord.getId()));
            if (medicalRecord.getPastAppointment() != null) {
                medicalRecordRow.add(String.valueOf(medicalRecord.getPastAppointment().getAppointmentId()));
            }
            medicalRecordFile.add(medicalRecordRow);
        }

        ExcelReaderWriter.write(medicalRecordFile, medicalRecordFilePath);
    }

    public List<MedicalRecord> getMedicalRecordsByPatientId(String patientId) throws IOException {
        medicalRecordList = loadMedicalRecords();
        List<MedicalRecord> patientMedicalRecords = new ArrayList<>();

        for (MedicalRecord medicalRecord : medicalRecordList) {
            if (medicalRecord.getPatientId().equals(patientId)) {
                patientMedicalRecords.add(medicalRecord);
            }
        }

        return patientMedicalRecords;
    }

    public List<MedicalRecord> getAllAppointmentOutcomes() {

        List<MedicalRecord> appointmentOutcomes = new ArrayList<>();

        for (MedicalRecord medicalRecord : medicalRecordList) {
            if (medicalRecord.getStatus().equals(Status.PENDING.getDisplayValue())) {
                appointmentOutcomes.add(medicalRecord);
            }
        }

        return appointmentOutcomes;
    }

    public void dispenseMedication (MedicalRecord mr) throws IOException {
        for (MedicalRecord medicalRecord : medicalRecordList) {
            if (medicalRecord.getId() == mr.getId()) {
                medicalRecord.setStatus(Status.DISPENSED.getDisplayValue());
                break;
            }
        }

        saveMedicalRecords();
    }

    public void insertNewMedicalRecord (MedicalRecord newMedicalRecord) throws IOException {
        newMedicalRecord.setId((medicalRecordList.get(medicalRecordList.size() - 1).getId()+1)); // Auto ID
        medicalRecordList.add(newMedicalRecord);
        saveMedicalRecords();
    }

    public MedicalRecord getMedicalRecordByAppointmentId (int appointmentId) throws IOException {
        for (MedicalRecord medicalRecord : medicalRecordList) {
            if (medicalRecord.getPastAppointment() != null && appointmentId == medicalRecord.getPastAppointment().getAppointmentId()) {
                return medicalRecord;
            }
        }

        return null;
    }
}
