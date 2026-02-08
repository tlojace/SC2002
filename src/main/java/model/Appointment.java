package model;

public class Appointment {
    private int appointmentId;
    private String patientId;
    private String doctorId;
    private String appointmentTime; // d/M/yyyy h:mm:ss a
    private String appointmentStatus;

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getAppointmentTime() { return appointmentTime; }

    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }

    public String getAppointmentStatus() { return appointmentStatus; }

    public void setAppointmentStatus(String appointmentStatus) { this.appointmentStatus = appointmentStatus; }
}
