package dao;

import entities.Etudiant;
import entities.Matiere;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class GestionEtudiantjdbc implements IGestionEtudiant {
    private Connection connection;
    
    public GestionEtudiantjdbc() throws SQLException {
        // Use the SingletonConnection class to get the database connection
        try {
            connection = SingletonConnection.getInstance();
            // Make sure autoCommit is set to false for transaction management
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
        } catch (RuntimeException e) {
            throw new SQLException("Failed to get database connection: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void addEtudiant(Etudiant e) throws SQLException {
        String sql = "INSERT INTO students (first_name, last_name, email) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getPrenom());
            ps.setString(2, e.getNom());
            ps.setString(3, e.getEmail());
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Creating student failed, no rows affected.");
            }
            
            // Get the auto-generated ID
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    e.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating student failed, no ID obtained.");
                }
            }
            
            connection.commit(); // Commit the transaction
        } catch (SQLException ex) {
            connection.rollback(); // Rollback on error
            throw ex; // Re-throw the exception
        }
    }
    
    @Override
    public void deleteEtudiant(int id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM students WHERE student_id = ?")) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected == 0) {
                // Instead of throwing an exception, just print a warning
                System.out.println("Warning: No student found with ID: " + id);
            }
            
            connection.commit(); // Commit the transaction
        } catch (SQLException e) {
            connection.rollback(); // Rollback on error
            throw e; // Re-throw the exception
        }
    }
    
    @Override
    public void updateEtudiant(Etudiant e) throws SQLException {
        String sql = "UPDATE students SET first_name = ?, last_name = ?, email = ? WHERE student_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, e.getPrenom());
            ps.setString(2, e.getNom());
            ps.setString(3, e.getEmail());
            ps.setInt(4, e.getId());
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                // Print warning instead of throwing exception
                System.out.println("Warning: No student found with ID: " + e.getId());
            }
            
            connection.commit(); // Commit the transaction
        } catch (SQLException ex) {
            connection.rollback(); // Rollback on error
            throw ex; // Re-throw the exception
        }
    }
    
    @Override
    public List<Etudiant> searchEtudiants(String keyword) throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE first_name LIKE ? OR last_name LIKE ? OR email LIKE ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String searchTerm = "%" + keyword + "%";
            ps.setString(1, searchTerm);
            ps.setString(2, searchTerm);
            ps.setString(3, searchTerm);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    etudiants.add(new Etudiant(
                        rs.getInt("student_id"),
                        rs.getString("last_name"),
                        rs.getString("first_name"),
                        rs.getString("email")
                    ));
                }
            }
        }
        // No need to commit for SELECT operations
        
        return etudiants;
    }
    
    @Override
    public List<Etudiant> getAllEtudiants() throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT * FROM students";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                etudiants.add(new Etudiant(
                    rs.getInt("student_id"),
                    rs.getString("last_name"),
                    rs.getString("first_name"),
                    rs.getString("email")
                ));
            }
        }
        // No need to commit for SELECT operations
        
        return etudiants;
    }
    
    @Override
    public List<Etudiant> sortBy(String criteria) throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();
        
        // Validate the criteria to prevent SQL injection
        if (!criteria.equals("student_id") && !criteria.equals("first_name") && 
            !criteria.equals("last_name") && !criteria.equals("email")) {
            criteria = "student_id"; // Default sort by ID if invalid criteria
        }
        
        String sql = "SELECT * FROM students ORDER BY " + criteria;
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                etudiants.add(new Etudiant(
                    rs.getInt("student_id"),
                    rs.getString("last_name"),
                    rs.getString("first_name"),
                    rs.getString("email")
                ));
            }
        }
        // No need to commit for SELECT operations
        
        return etudiants;
    }
    
    // Close the connection when the application shuts down
    public void closeConnection() {
        // Using the SingletonConnection class to close the connection
        SingletonConnection.closeConnection();
    }
    
    // Method to verify the database connection
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public Etudiant getEtudiantById(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Etudiant(
                        rs.getInt("student_id"),
                        rs.getString("last_name"),
                        rs.getString("first_name"),
                        rs.getString("email")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public void addOrUpdateNote(int etudiantId, String matiere, double note) throws SQLException {
        // Implementation could be added in the future
        throw new UnsupportedOperationException("Not implemented yet");
    }

    
    public Map<String, Double> getNotesForEtudiant(int etudiantId) throws SQLException {
        // Implementation could be added in the future
        return new HashMap<>();
    }

    
    public void addMatiere(Matiere m) throws SQLException {
        // Implementation could be added in the future
        throw new UnsupportedOperationException("Not implemented yet");
    }

    
    public List<Matiere> getAllMatieres() throws SQLException {
        // Implementation could be added in the future
        return new ArrayList<>();
    }

    
    public void addOrUpdateNote(int studentId, int courseId, double note) throws SQLException {
        // Implementation could be added in the future
        throw new UnsupportedOperationException("Not implemented yet");
    }
}