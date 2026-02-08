package repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import constants.FilePath;
import model.Staff;
import model.User;
import security.Encryptor;
import services.ExcelReaderWriter;

// Read and Write to Accounts.xlsx
public class AccountRepository {
    private final String accountFilePath = FilePath.ACCOUNT_FILE_PATH;

    public User login(String hospitalId, String password) throws Exception {
        List<List<String>> accounts = ExcelReaderWriter.read(accountFilePath);

        // Starts from 1 as row 0 is headers!
        for (int i = 1; i < accounts.size(); i++) {

            // Find Account with Same Hospital ID
            if (accounts.get(i).get(0).equals(hospitalId)) {

                // Encrypt Password and Matching
                if (Encryptor.encrypt(password).equals(accounts.get(i).get(1))) {

                    // Account Found with the Correct Hospital ID and Password
                    User userInfo = new User();
                    userInfo.setHospitalId(accounts.get(i).get(0));
                    userInfo.setRole(accounts.get(i).get(2));

                    if (password.equals("password"))
                    {
                        // Need to change password
                        boolean changedSuccesfully = false;
                        while (!changedSuccesfully) {
                            changedSuccesfully = changePassword(hospitalId);
                        }
                    }

                    // Return User Hospital ID and Role
                    return userInfo;
                }

            }
        }


        // When reached the end of the function, no user is found, return null (Invalid Login Details)
        return null;
    }

    // Add Login Details into Accounts.xlxs
    public void saveNewAccount (User loginDetails) throws Exception {
        List<String> loginDetailsLine = new ArrayList<>();

        // Format is hospitalId, password (hashed), role
        loginDetailsLine.add(loginDetails.getHospitalId());
        loginDetailsLine.add(Encryptor.encrypt("password"));     // Default Password "password"
        loginDetailsLine.add(loginDetails.getRole().toUpperCase());

        // Get file details
        List<List<String>> accountsFile = ExcelReaderWriter.read(accountFilePath);
        accountsFile.add(loginDetailsLine);

        ExcelReaderWriter.write(accountsFile, FilePath.ACCOUNT_FILE_PATH);
    }

    public void deleteAccount(String hospitalId) throws IOException {
        List<List<String>> accountsFile = ExcelReaderWriter.read(accountFilePath);
        for (int i = 1; i < accountsFile.size(); i++) {
            if (accountsFile.get(i).get(0).equals(hospitalId)) {
                accountsFile.remove(i);
                break;
            }
        }
        ExcelReaderWriter.write(accountsFile, FilePath.ACCOUNT_FILE_PATH);
    }

    public List<Staff> loadStaffRoles (List<Staff> staffList) throws IOException {
        List<List<String>> accountDetails = ExcelReaderWriter.read(accountFilePath);
        for (Staff staff : staffList) {
            for (int i = 1; i < accountDetails.size(); i++) {
                if (accountDetails.get(i).get(0).equals(staff.getHospitalId())) {
                    staff.setRole(accountDetails.get(i).get(2));
                }
            }
        }

        return staffList;
    }

    // method to change password
    private boolean changePassword(String hospitalId) throws Exception {
        // Validate Password
        // Two unacceptable passwords: null or password
        System.out.println("Enter your new password:");
        Scanner scanner = new Scanner(System.in);
        String newPassword = scanner.nextLine();
        if (newPassword.isEmpty() || newPassword.equals("password")) {
            // Return Error Message and False to tell them to reenter
            System.out.println("Invalid password.");
            return false;
        } else
            return savePassword(newPassword, hospitalId);
    }
        //
    private boolean savePassword(String newPassword, String hospitalId) throws Exception {
        String encryptedPassword = Encryptor.encrypt(newPassword);
        // Update Database since New Password is OK
        List<List<String>> accounts = ExcelReaderWriter.read(FilePath.ACCOUNT_FILE_PATH);

        // Skip i == 0 because Header
        for (int i = 1; i < accounts.size(); i++) {
            if (accounts.get(i).get(0).equals(hospitalId)) {
                accounts.get(i).set(1, encryptedPassword);

                // Write the updated List into Excel
                ExcelReaderWriter.write(accounts, FilePath.ACCOUNT_FILE_PATH);
                //

                break;
            }
        }

        // Succesfully changed
        return true;
    }
}
