package model;

public class MedicalRecord {
    private String patientId;
    private String diagnoses;
    private String treatment;
    private String prescription;
    private int prescriptionAmount;
    private String status;
    private int id;
    private Appointment pastAppointment;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDiagnoses() {
        return diagnoses;
    }

    public void setDiagnoses(String diagnoses) {
        this.diagnoses = diagnoses;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPrescriptionAmount() {
        return prescriptionAmount;
    }

    public void setPrescriptionAmount(int prescriptionAmount) {
        this.prescriptionAmount = prescriptionAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Appointment getPastAppointment() {
        return pastAppointment;
    }

    public void setPastAppointment(Appointment pastAppointment) {
        this.pastAppointment = pastAppointment;
    }
}
