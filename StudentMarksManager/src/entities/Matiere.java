package entities;


public class Matiere {
    private int id;
    private String nom;
    private int coefficient;

    // Fixed constructors
    public Matiere() {}

    public Matiere(int id, String nom, int coefficient) {
        this.id = id;
        this.nom = nom;
        this.coefficient = coefficient;
    }

    public Matiere(String nom, int coefficient) {
        this(0, nom, coefficient); // Default ID 0 for new matieres
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public int getCoefficient() { return coefficient; }
    public void setCoefficient(int coefficient) { this.coefficient = coefficient; }
}