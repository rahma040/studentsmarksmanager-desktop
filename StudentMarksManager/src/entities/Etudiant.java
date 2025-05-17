package entities;

import java.util.HashMap;
import java.util.Map;

public class Etudiant {
    private int id;
    private String nom;
    private String prenom;
    private String email;  // Added email field
    private Map<String, Double> notes; // Course name -> Grade

    public Etudiant() {
        notes = new HashMap<>();
    }

    public Etudiant(int id, String nom, String prenom, String email) {
        this();
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Map<String, Double> getNotes() { return notes; }
    public void setNotes(Map<String, Double> notes) { this.notes = notes; }

    public double getMoyenne() {
        return notes.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    public String getGrade() {
        double moy = getMoyenne();
        if (moy >= 16) return "A";
        else if (moy >= 14) return "B";
        else if (moy >= 12) return "C";
        else if (moy >= 10) return "D";
        else return "F";
    }
}