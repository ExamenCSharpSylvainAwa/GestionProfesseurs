package sn.groupeisi.gestionprofesseurs.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "emargements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Emargements {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "statut", nullable = false)
    private String statut;

    @ManyToOne
    @JoinColumn(name = "professeur_id", referencedColumnName = "id", nullable = false)
    private Users professeur;

    @ManyToOne
    @JoinColumn(name = "cours_id", referencedColumnName = "id", nullable = false)
    private Cours cours;

    // Constructeur sans ID (utilisé pour la création d'un nouvel émargement)
    public Emargements(LocalDate date, String statut, Users professeur, Cours cours) {
        this.date = date;
        this.statut = statut;
        this.professeur = professeur;
        this.cours = cours;
    }
}
