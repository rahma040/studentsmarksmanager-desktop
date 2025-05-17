package dao;

import entities.Etudiant;
import java.sql.SQLException;
import java.util.List;

public interface IGestionEtudiant {
    /**
     * Add a new student to the database
     * @param e The student to add
     * @throws SQLException If a database error occurs
     */
    void addEtudiant(Etudiant e) throws SQLException;
    
    /**
     * Delete a student from the database
     * @param id The ID of the student to delete
     * @throws SQLException If a database error occurs
     */
    void deleteEtudiant(int id) throws SQLException;
    
    /**
     * Update an existing student in the database
     * @param e The student with updated information
     * @throws SQLException If a database error occurs
     */
    void updateEtudiant(Etudiant e) throws SQLException;
    
    /**
     * Search for students by keyword
     * @param keyword The search term
     * @return List of students matching the keyword
     * @throws SQLException If a database error occurs
     */
    List<Etudiant> searchEtudiants(String keyword) throws SQLException;
    
    /**
     * Get all students from the database
     * @return List of all students
     * @throws SQLException If a database error occurs
     */
    List<Etudiant> getAllEtudiants() throws SQLException;
    
    /**
     * Sort students by a specific criteria
     * @param criteria The field to sort by
     * @return Sorted list of students
     * @throws SQLException If a database error occurs
     */
    List<Etudiant> sortBy(String criteria) throws SQLException;

	void addOrUpdateNote(int etudiantId, String matiere, double note) throws SQLException;
}