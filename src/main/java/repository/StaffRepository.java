package repository;

import constants.FilePath;
import enums.Role;
import model.Doctor;
import model.Staff;
import services.ExcelReaderWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StaffRepository {
    private final String staffInfoFilePath = FilePath.STAFF_INFO_FILE_PATH;
    private List<String> staffFileHeader = new ArrayList<>();

    private final AccountRepository accountRepository = new AccountRepository();

    // Stores all the Staffs, don't need to keep loading, but save after any changes.
    private List<Staff> staffList = new ArrayList<>();


    public StaffRepository() throws IOException {
        this.staffList = loadStaffs();
    }

    private List<Staff> loadStaffRoles(List<Staff> staffList) throws IOException {
        return accountRepository.loadStaffRoles(staffList) ;
    }

    private List<Staff> loadStaffs() throws IOException {
        List<Staff> staffList = new ArrayList<>();
        List<List<String>> staffFile = ExcelReaderWriter.read(staffInfoFilePath);

        for (int i = 0; i < staffFile.size(); i++) {
            if (i == 0) {
                staffFileHeader = staffFile.get(i);
                continue;
            }

            String hospitalId = staffFile.get(i).get(0);
            String gender = staffFile.get(i).get(1);
            int age = (int) Float.parseFloat(staffFile.get(i).get(2));

            if (staffFile.get(i).size() > 3) {
                // Create a Doctor object if doctor-specific data is present
                Doctor doctor = new Doctor();
                doctor.setHospitalId(hospitalId);
                doctor.setGender(gender);
                doctor.setAge(age);
                doctor.setDoctorId(staffFile.get(i).get(3)); // Assuming field (3) is the specialization
                staffList.add(doctor); // Add Doctor to the list
            } else {
                // Create a generic Staff object
                Staff staff = new Staff();
                staff.setHospitalId(hospitalId);
                staff.setGender(gender);
                staff.setAge(age);
                staffList.add(staff); // Add Staff to the list
            }
        }

        staffList = loadStaffRoles(staffList);

        return staffList;
    }

    private void saveStaffs() throws IOException {
        List<List<String>> staffFile = new ArrayList<>();

        staffFile.add(staffFileHeader);
        for (Staff staff : staffList) {
            List<String> staffRow = new ArrayList<>();
            staffRow.add(staff.getHospitalId());
            staffRow.add(staff.getGender());
            staffRow.add(String.valueOf(staff.getAge()));
            if (staff instanceof Doctor) {
                Doctor doctor = (Doctor) staff;
                staffRow.add(doctor.getDoctorId());
            }
            staffFile.add(staffRow);
        }

        ExcelReaderWriter.write(staffFile, staffInfoFilePath);
    }

    // Save Staff Info
    public void saveNewStaff(Staff staffInfo) throws IOException {
        List<String> staffDetailsLine = new ArrayList<>();

        // Format is hospitalId, gender, age, doctorID (If role is Doctor, generate doctorID)
        staffDetailsLine.add(staffInfo.getHospitalId());
        staffDetailsLine.add(staffInfo.getGender().toUpperCase());
        staffDetailsLine.add(String.valueOf(staffInfo.getAge()));
        if (staffInfo.getRole().toUpperCase().equals(Role.DOCTOR.getDisplayValue())) {
            staffDetailsLine.add("D100" + (staffList.size() + 1));
        }

        // Get file details
        List<List<String>> staffInfoFile = ExcelReaderWriter.read(FilePath.STAFF_INFO_FILE_PATH);
        staffInfoFile.add(staffDetailsLine);

        ExcelReaderWriter.write(staffInfoFile, FilePath.STAFF_INFO_FILE_PATH);

        staffList.add(staffInfo);
    }

    public void deleteStaff(String hospitalId) throws IOException {
        for (Staff staff : staffList) {
            if (staff.getHospitalId().equals(hospitalId)) {
                staffList.remove(staff);
                break;
            }
        }

        saveStaffs();
    }

    public List<Staff> getAllStaff() {
        return staffList;
    }

    public void updateStaff(Staff newStaffInfo) throws IOException {
        for (Staff staff : staffList) {
            if (staff.getHospitalId().equals(newStaffInfo.getHospitalId())) {
                staff = newStaffInfo;
                break;
            }
        }
        saveStaffs();
    }

    public Doctor getDoctorByHospitalId(String hospitalId) throws IOException {
        for (Staff staff : staffList) {
            if (staff.getHospitalId().equals(hospitalId)) {
                return (Doctor) staff;
            }
        }

        return null;
    }

    public List<String> getAllDoctors() {

        List<String> doctorList = new ArrayList<>();
        for (Staff staff : staffList) {
            if (staff instanceof Doctor) {
                doctorList.add(((Doctor) staff).getDoctorId());
            }
        }

        return doctorList;
    }
}
