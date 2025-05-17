package metier;

import dao.GestionEtudiantjdbc;
import dao.IGestionEtudiant;
import entities.Etudiant;
import java.sql.SQLException;
import java.util.List;

public class GestionEtudiant {
    private final IGestionEtudiant dao;
    
    public GestionEtudiant() throws SQLException {
        this.dao = new GestionEtudiantjdbc();
        
        // Verify the connection
        if (((GestionEtudiantjdbc) dao).testConnection()) {
            System.out.println("Database connection established successfully.");
        } else {
            throw new SQLException("Failed to establish database connection.");
        }
    }
    
    public void addEtudiant(Etudiant e) throws SQLException {
        validateStudent(e);
        dao.addEtudiant(e);
    }
    
    public void deleteEtudiant(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid student ID: " + id);
        }
        dao.deleteEtudiant(id);
    }
    
    public void updateEtudiant(Etudiant e) throws SQLException {
        validateStudent(e);
        if (e.getId() <= 0) {
            throw new IllegalArgumentException("Invalid student ID: " + e.getId());
        }
        dao.updateEtudiant(e);
    }
    
    public List<Etudiant> searchEtudiants(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be empty");
        }
        return dao.searchEtudiants(keyword);
    }
    
    public List<Etudiant> getAllEtudiants() throws SQLException {
        return dao.getAllEtudiants();
    }
    
    public List<Etudiant> sortBy(String criteria) throws SQLException {
        if (criteria == null || criteria.trim().isEmpty()) {
            criteria = "student_id"; // Default sort by ID if criteria is empty
        }
        return dao.sortBy(criteria);
    }
    
    private void validateStudent(Etudiant e) {
        if (e == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        
        if (e.getNom() == null || e.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        
        if (e.getPrenom() == null || e.getPrenom().trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        
        if (e.getEmail() == null || e.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        
        // Basic email validation
        if (!e.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
    
    // Close the connection when the application shuts down
    public void closeConnection() {
        if (dao instanceof GestionEtudiantjdbc) {
            ((GestionEtudiantjdbc) dao).closeConnection();
        }
    }
}