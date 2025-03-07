package sn.groupeisi.gestionprofesseurs.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "salle")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Salle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "libelle", nullable = false)
    private String libelle;

    // Constructeur sans ID (utilisé pour la création d'une nouvelle salle)
    public Salle(String libelle) {
        this.libelle = libelle;
    }
}
