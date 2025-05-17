package presentation;

import entities.Etudiant;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class TableModele extends AbstractTableModel {
    // Only keep basic student information columns
    private final String[] columnNames = {"ID", "Last Name", "First Name", "Email"};
    private List<Etudiant> etudiants;

    public TableModele(List<Etudiant> etudiants) {
        this.etudiants = etudiants;
    }

    @Override
    public int getRowCount() {
        return etudiants.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Etudiant etudiant = etudiants.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> etudiant.getId();
            case 1 -> etudiant.getNom();      // Last Name
            case 2 -> etudiant.getPrenom();   // First Name
            case 3 -> etudiant.getEmail();
            default -> null;
        };
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public void setEtudiants(List<Etudiant> etudiants) {
        this.etudiants = etudiants;
        fireTableDataChanged(); // Notify table that data has changed
    }
    
    public Etudiant getEtudiantAt(int row) {
        if (row >= 0 && row < etudiants.size()) {
            return etudiants.get(row);
        }
        return null;
    }
}