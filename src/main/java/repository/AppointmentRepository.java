package repository;

import constants.FilePath;
import enums.Status;
import model.Appointment;
import model.Doctor;
import model.Patient;
import services.ExcelReaderWriter;
import utils.DateUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AppointmentRepository {

    private final String appointmentsFilePath = FilePath.APPOINTMENTS_FILE_PATH;
    private List<String> appointmentsFileHeader = new ArrayList<>();

    // Stores all the appointments, don't need to keep loading, but save after any changes.
    private List<Appointment> appointmentList = new ArrayList<>();

    public AppointmentRepository() throws IOException {
        this.appointmentList = loadAppointments();
    }

    private List<Appointment> loadAppointments() throws IOException {
        List<Appointment> appointmentList = new ArrayList<>();
        List<List<String>> appointmentFile = ExcelReaderWriter.read(appointmentsFilePath);

        for (int i = 0; i < appointmentFile.size(); i++) {
            if (i == 0) {
                appointmentsFileHeader = appointmentFile.get(i);
                continue;
            }

            Appointment appointment = new Appointment();
            appointment.setAppointmentId((int) Float.parseFloat(appointmentFile.get(i).get(0)));
            appointment.setDoctorId(appointmentFile.get(i).get(1));
            appointment.setPatientId(appointmentFile.get(i).get(2));
            appointment.setAppointmentTime(appointmentFile.get(i).get(3));
            appointment.setAppointmentStatus(appointmentFile.get(i).get(4));
            appointmentList.add(appointment);
        }

        return appointmentList;
    }

    public List<Appointment> getAppointmentList() {
        return appointmentList;
    }

    private void saveAppointments() throws IOException {
        List<List<String>> appointmentFile = new ArrayList<>();

        appointmentFile.add(appointmentsFileHeader);
        for (Appointment appointment : appointmentList) {
            List<String> appointmentRow = new ArrayList<>();
            appointmentRow.add(String.valueOf(appointment.getAppointmentId()));
            appointmentRow.add(appointment.getDoctorId());
            appointmentRow.add(appointment.getPatientId());
            appointmentRow.add(appointment.getAppointmentTime());
            appointmentRow.add(appointment.getAppointmentStatus());
            appointmentFile.add(appointmentRow);
        }

        ExcelReaderWriter.write(appointmentFile, appointmentsFilePath);
    }

    public List<String> getPatientListOfDoctor(String doctorId) {
        List<String> patients = new ArrayList<>();
        for (Appointment appointment : appointmentList) {
            if (appointment.getDoctorId().equals(doctorId)) {
                patients.add(appointment.getPatientId());
            }
        }

        return patients;
    }

    public List<Appointment> getDoctorSchedule(Doctor doctor, LocalDate dateChoice) {
        List<Appointment> doctorSchedule = new ArrayList<>();

        for (Appointment appointment : appointmentList) {
            if (appointment.getDoctorId().equals(doctor.getDoctorId())) {
                if (appointment.getAppointmentStatus().equals(Status.FREE.getDisplayValue()) ||
                appointment.getAppointmentStatus().equals(Status.PENDING.getDisplayValue()) ||
                appointment.getAppointmentStatus().equals(Status.CONFIRMED.getDisplayValue())) {

                    LocalDate appointmentDate = DateUtils.extractDate(appointment.getAppointmentTime());
                    LocalTime appointmentTime = DateUtils.extractTime(appointment.getAppointmentTime());

                    assert appointmentDate != null;
                    assert appointmentTime != null;

                    if (appointmentDate.equals(dateChoice) && appointmentTime.isAfter(LocalTime.now())) {
                        doctorSchedule.add(appointment);
                    }
                }
            }
        }

        return doctorSchedule;
    }

    public List<Appointment> getUpcomingAppointmentsForPatient(Patient patient) {
        List<Appointment> upcomingAppointments = new ArrayList<>();

        for (Appointment appointment : appointmentList) {
            if (appointment.getPatientId().equals(patient.getPatientId())) {
                if (DateUtils.isUpcoming(appointment.getAppointmentTime())) {
                    upcomingAppointments.add(appointment);
                }
            }
        }

        return upcomingAppointments;
    }

    public List<LocalTime> getAvailableTimeSlots (String doctorId, LocalDate date) {
        List<LocalTime> availableTimeSlots = new ArrayList<>();

        for (Appointment appointment : appointmentList) {
            if (appointment.getDoctorId().equals(doctorId)) {
                if (Objects.equals(DateUtils.extractDate(appointment.getAppointmentTime()), date)) {
                    if (appointment.getAppointmentStatus().equals(Status.FREE.getDisplayValue()) ||
                            appointment.getAppointmentStatus().equals(Status.CANCELLED.getDisplayValue())) {
                        availableTimeSlots.add(DateUtils.extractTime(appointment.getAppointmentTime()));
                    }
                }
            }
        }

        return availableTimeSlots;
    }

    public void bookAppointment (String patientId, String doctorId, LocalDate date, LocalTime time) throws IOException {
        for (Appointment appointment : appointmentList) {
            if (appointment.getDoctorId().equals(doctorId)) {
                if (Objects.equals(DateUtils.extractDate(appointment.getAppointmentTime()), date) &&
                Objects.equals(DateUtils.extractTime(appointment.getAppointmentTime()), time)) {
                    appointment.setAppointmentStatus(Status.PENDING.getDisplayValue());
                    appointment.setPatientId(patientId);
                    break;
                }
            }
        }

        saveAppointments();
    }

    public void freeAppointmentSlot (Appointment appointmentToFree) throws IOException {
        for (Appointment appointment: appointmentList) {
            if (appointment.getAppointmentId() == appointmentToFree.getAppointmentId()) {
                appointment.setAppointmentStatus(Status.FREE.getDisplayValue());
                appointment.setPatientId(Status.FREE.getDisplayValue());
                break;
            }
        }
        saveAppointments();
    }

    public void cancelAppointmentSlot (Appointment appointmentToCancel) throws IOException {
        for (Appointment appointment: appointmentList) {
            if (appointment.getAppointmentId() == appointmentToCancel.getAppointmentId()) {
                appointment.setAppointmentStatus(Status.CANCELLED.getDisplayValue());
                appointment.setPatientId(Status.FREE.getDisplayValue());
                break;
            }
        }
        saveAppointments();
    }

    public void addTimeSlotToSchedule (Appointment appointment) throws IOException {
        appointment.setAppointmentId((appointmentList.get(appointmentList.size()-1).getAppointmentId() + 1)); // Auto Generated ID
        appointmentList.add(appointment);
        saveAppointments();
    }

    public void removeTimeSlotFromSchedule (Appointment appointment) throws IOException {
        for (Appointment appointmentToRemove : appointmentList) {
            if (appointmentToRemove.getAppointmentId() == appointment.getAppointmentId()) {
                appointmentList.remove(appointmentToRemove);
                break;
            }
        }

        saveAppointments();
    }

    public void approveAppointment(Appointment appointmentToApprove) throws IOException {
        for (Appointment appointment: appointmentList) {
            if (appointment.getAppointmentId() == appointmentToApprove.getAppointmentId()) {
                appointment.setAppointmentStatus(Status.CONFIRMED.getDisplayValue());
                break;
            }
        }

        saveAppointments();
    }

    public void declineAppointment(Appointment appointmentToDecline) throws IOException {
        for (Appointment appointment: appointmentList) {
            if (appointment.getAppointmentId() == appointmentToDecline.getAppointmentId()) {
                appointment.setPatientId(Status.FREE.getDisplayValue());
                appointment.setAppointmentStatus(Status.FREE.getDisplayValue());
                break;
            }
        }

        saveAppointments();
    }

    public List<Appointment> getUpcomingAppointments (String doctorId) {
        List<Appointment> upcomingAppointments = new ArrayList<>();

        for (Appointment appointment : appointmentList) {
            if (appointment.getDoctorId().equals(doctorId)) {
                if (Objects.equals(DateUtils.extractDate(appointment.getAppointmentTime()), LocalDate.now())) {
                    if (appointment.getAppointmentStatus().equals(Status.CONFIRMED.getDisplayValue())) {
                        upcomingAppointments.add(appointment);
                    }
                }
            }
        }

        return upcomingAppointments;
    }

    public void completeAppointment(Appointment appointmentToComplete) throws IOException {
        for (Appointment appointment: appointmentList) {
            if (appointment.getAppointmentId() == appointmentToComplete.getAppointmentId()) {
                appointment.setAppointmentStatus(Status.COMPLETED.getDisplayValue());
                break;
            }
        }

        saveAppointments();
    }

    public Appointment getAppointmentById (int appointmentId) throws IOException {
        for (Appointment appointment: appointmentList) {
            if (appointment.getAppointmentId() == appointmentId) {
                return appointment;
            }
        }

        return null;
    }
}
