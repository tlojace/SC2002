package repository;

import constants.FilePath;
import model.Medication;
import services.ExcelReaderWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MedicationRepository {
    private final String medicationFilePath = FilePath.MEDICAL_INVENTORY_FILE_PATH;
    private List<String> medicationFileHeader = new ArrayList<>();

    // Stores all the Medications, don't need to keep loading, but save after any changes.
    private List<Medication> medicationList = new ArrayList<>();


    public MedicationRepository() throws IOException {
        this.medicationList = loadMedications();
    }

    private List<Medication> loadMedications() throws IOException {
        List<Medication> medicationList = new ArrayList<>();
        List<List<String>> medicationFile = ExcelReaderWriter.read(medicationFilePath);

        for (int i = 0; i < medicationFile.size(); i++) {
            if (i == 0) {
                medicationFileHeader = medicationFile.get(i);
                continue;
            }

            Medication medication = new Medication();
            medication.setMedicineName(medicationFile.get(i).get(0));
            medication.setCurrentStock((int) Float.parseFloat(medicationFile.get(i).get(1)));
            medication.setLowStockLevel((int) Float.parseFloat(medicationFile.get(i).get(2)));
            medication.setReplenishAmount((int) Float.parseFloat(medicationFile.get(i).get(3)));
            medicationList.add(medication);
        }

        return medicationList;
    }

    private void saveMedications() throws IOException {
        List<List<String>> medicationFile = new ArrayList<>();

        medicationFile.add(medicationFileHeader);
        for (Medication medication : medicationList) {
            List<String> medicationRow = new ArrayList<>();
            medicationRow.add(medication.getMedicineName());
            medicationRow.add(String.valueOf(medication.getCurrentStock()));
            medicationRow.add(String.valueOf(medication.getLowStockLevel()));
            medicationRow.add(String.valueOf(medication.getReplenishAmount()));
            medicationFile.add(medicationRow);
        }

        ExcelReaderWriter.write(medicationFile, medicationFilePath);
    }

    public List<Medication> getMedicationList() {
        return medicationList;
    }

    public void addNewMedication (Medication medication) throws IOException {
        medicationList.add(medication);
        saveMedications();
    }

    public void updateMedication(Medication medicationToUpdate) throws IOException {
        for (Medication medication : medicationList) {
            if (medication.getMedicineName().equals(medicationToUpdate.getMedicineName())) {
                medication = medicationToUpdate;
                break;
            }
        }
        saveMedications();
    }

    public void removeMedication(String medicationName) throws IOException {
        for (Medication medication : medicationList) {
            if (medication.getMedicineName().equals(medicationName)) {
                medicationList.remove(medication);
                break;
            }
        }

        saveMedications();
    }

    public void approveReplenishRequest(Medication m) throws IOException {
        for (Medication medication : medicationList) {
            if (medication.getMedicineName().equals(m.getMedicineName())) {
                medication.setCurrentStock(medication.getCurrentStock() + medication.getReplenishAmount());
                medication.setReplenishAmount(0);
                break;
            }
        }

        saveMedications();
    }

    public void submitReplenishRequest(Medication m, int amountToReplenish) throws IOException {
        for (Medication medication : medicationList) {
            if (medication.getMedicineName().equals(m.getMedicineName())) {
                medication.setReplenishAmount(amountToReplenish);
                break;
            }
        }

        saveMedications();
    }

    public boolean dispenseMedication(String medicationName, int amountToDispense) throws IOException {
        for (Medication medication : medicationList) {
            if (medication.getMedicineName().equals(medicationName)) {
                if (medication.getCurrentStock() < amountToDispense) {
                    return false;
                }

                medication.setCurrentStock(medication.getCurrentStock() - amountToDispense);
                break;
            }
        }

        saveMedications();
        return true;
    }
}
