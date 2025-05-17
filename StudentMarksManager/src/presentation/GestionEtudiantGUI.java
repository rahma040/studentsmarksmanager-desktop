package presentation;

import entities.Etudiant;
import metier.GestionEtudiant;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestionEtudiantGUI extends JFrame {
    private GestionEtudiant gestion;
    private JTable table;
    private TableModele tableModel;

    public GestionEtudiantGUI() {
        setTitle("Student Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize table with empty model first
        tableModel = new TableModele(new ArrayList<>());
        table = new JTable(tableModel);
        
        try {
            // Initialize the business logic layer
            gestion = new GestionEtudiant();
            
            // Add window listener to close database connection when application closes
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    gestion.closeConnection();
                }
            });
            
            initUI();
            refreshTable(); // Load actual data after UI is set up
        } catch (Exception e) {
            showError("Initialization Error", e);
            System.exit(1);
        }
    }
    
    private void initUI() {
        // Table configuration
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        table.setSelectionBackground(new Color(200, 200, 255));
        table.setAutoCreateRowSorter(true);
        
        // Set up a JScrollPane to contain the table
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel studentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        studentPanel.setBorder(BorderFactory.createTitledBorder("Student Management"));
        
        // Add buttons
        addButton(studentPanel, "Add Student", this::addStudent);
        addButton(studentPanel, "Edit Student", e -> {
            try {
                editStudent(e);
            } catch (SQLException ex) {
                showError("Edit Error", ex);
            }
        });
        addButton(studentPanel, "Delete Student", e -> {
            try {
                deleteStudent(e);
            } catch (SQLException ex) {
                showError("Delete Error", ex);
            }
        });
        addButton(studentPanel, "Search", e -> {
            try {
                searchStudents(e);
            } catch (SQLException ex) {
                showError("Search Error", ex);
            }
        });
        addButton(studentPanel, "Export Data", this::exportStudentData);
        addButton(studentPanel, "Refresh", e -> refreshTable());
        
        // Sorting buttons
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        sortPanel.add(new JLabel("Sort by:"));
        addButton(sortPanel, "Last Name", e -> {
            try {
                sortStudents("last_name");
            } catch (SQLException ex) {
                showError("Sort Error", ex);
            }
        });
        addButton(sortPanel, "First Name", e -> {
            try {
                sortStudents("first_name");
            } catch (SQLException ex) {
                showError("Sort Error", ex);
            }
        });
        addButton(sortPanel, "ID", e -> {
            try {
                sortStudents("student_id");
            } catch (SQLException ex) {
                showError("Sort Error", ex);
            }
        });
        
        studentPanel.add(sortPanel);
        buttonPanel.add(studentPanel);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set column widths after the table is fully initialized
        SwingUtilities.invokeLater(() -> {
            try {
                table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
                table.getColumnModel().getColumn(1).setPreferredWidth(150); // Last Name
                table.getColumnModel().getColumn(2).setPreferredWidth(150); // First Name
                table.getColumnModel().getColumn(3).setPreferredWidth(250); // Email
            } catch (Exception ex) {
                // This might fail if the table is empty and has no columns yet
                System.err.println("Could not set column widths: " + ex.getMessage());
            }
        });
    }

    private void addStudent(ActionEvent e) {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField emailField = new JTextField();

        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Add New Student", JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Etudiant student = new Etudiant(
                    0, // ID will be auto-generated
                    lastNameField.getText().trim(),
                    firstNameField.getText().trim(),
                    emailField.getText().trim()
                );
                gestion.addEtudiant(student);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Student added successfully!");
            } catch (SQLException ex) {
                showError("Error adding student", ex);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, 
                    ex.getMessage(), 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editStudent(ActionEvent e) throws SQLException {
        Etudiant selected = getSelectedStudent();
        if (selected == null) return;

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField firstNameField = new JTextField(selected.getPrenom());
        JTextField lastNameField = new JTextField(selected.getNom());
        JTextField emailField = new JTextField(selected.getEmail());

        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Edit Student", JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Etudiant updated = new Etudiant(
                    selected.getId(),
                    lastNameField.getText().trim(),
                    firstNameField.getText().trim(),
                    emailField.getText().trim()
                );
                gestion.updateEtudiant(updated);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Student updated successfully!");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, 
                    ex.getMessage(), 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteStudent(ActionEvent e) throws SQLException {
        Etudiant selected = getSelectedStudent();
        if (selected == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete " + selected.getPrenom() + " " + selected.getNom() + "?",
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                gestion.deleteEtudiant(selected.getId());
                refreshTable();
                JOptionPane.showMessageDialog(this, "Student deleted successfully!");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, 
                    ex.getMessage(), 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchStudents(ActionEvent e) throws SQLException {
        String searchTerm = JOptionPane.showInputDialog(this, 
            "Enter search term (name or email):");
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            try {
                List<Etudiant> results = gestion.searchEtudiants(searchTerm);
                tableModel.setEtudiants(results);
                
                // Display message if no results found
                if (results.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "No students found matching: " + searchTerm,
                        "Search Results",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, 
                    ex.getMessage(), 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void sortStudents(String criteria) throws SQLException {
        try {
            List<Etudiant> sortedStudents = gestion.sortBy(criteria);
            tableModel.setEtudiants(sortedStudents);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, 
                ex.getMessage(), 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable() {
        try {
            List<Etudiant> etudiants = gestion.getAllEtudiants();
            tableModel.setEtudiants(etudiants);
            
            // Ensure columns are properly sized
            SwingUtilities.invokeLater(() -> {
                try {
                    if (table.getColumnCount() >= 4) {
                        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
                        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Last Name
                        table.getColumnModel().getColumn(2).setPreferredWidth(150); // First Name
                        table.getColumnModel().getColumn(3).setPreferredWidth(250); // Email
                    }
                } catch (Exception ex) {
                    // This might fail if the table is empty and has no columns yet
                    System.err.println("Could not set column widths: " + ex.getMessage());
                }
            });
        } catch (SQLException ex) {
            showError("Error refreshing data", ex);
        }
    }

    private void exportStudentData(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Student Data");
        fileChooser.setSelectedFile(new File("students_export.csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                List<Etudiant> students = gestion.getAllEtudiants();
                
                if (students.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "No data to export.",
                        "Export Notice",
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                try (PrintWriter writer = new PrintWriter(file)) {
                    writer.println("ID,Last Name,First Name,Email");
                    for (Etudiant student : students) {
                        writer.printf("%d,%s,%s,%s%n",
                            student.getId(),
                            student.getNom(),
                            student.getPrenom(),
                            student.getEmail());
                    }
                }
                JOptionPane.showMessageDialog(this,
                    "Data exported successfully to:\n" + file.getAbsolutePath(),
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException | FileNotFoundException ex) {
                showError("Export Error", ex);
            }
        }
    }

    private Etudiant getSelectedStudent() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a student first",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        return tableModel.getEtudiantAt(modelRow);
    }

    private void addButton(JPanel panel, String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        panel.add(button);
    }

    private void showError(String title, Exception e) {
        StringBuilder message = new StringBuilder(title + ":\n" + e.getMessage());
        
        if (e instanceof SQLException) {
            SQLException sqlEx = (SQLException)e;
            message.append("\nSQL State: ").append(sqlEx.getSQLState());
            message.append("\nError Code: ").append(sqlEx.getErrorCode());
        }
        
        JOptionPane.showMessageDialog(this, 
            message.toString(),
            "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set the look and feel to match the system
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new GestionEtudiantGUI().setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, 
                    "Failed to initialize application: " + e.getMessage(),
                    "Startup Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}