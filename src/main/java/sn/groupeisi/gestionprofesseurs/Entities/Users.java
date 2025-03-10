package sn.groupeisi.gestionprofesseurs.Entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Data
@NoArgsConstructor
@Access(AccessType.FIELD)
@AllArgsConstructor
@Builder
public class Users {

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @Column(name = "nom", nullable = false)
    private String nom;

    @Setter
    @Getter
    @Column(name = "prenom", nullable = false)
    private String prenom;

    @Getter
    @Setter
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Setter
    @Getter
    @Column(name = "role", nullable = false)
    private String role;

    // Constructeur sans ID (utilisé pour la création d'un nouvel utilisateur)
    public Users(String nom, String prenom, String email, String password, String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.role = role;
    }
    public Users(int id, String nom, String prenom, String email, String role) {
        this.id = (long) id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
    }


    // Autres getters et setters pour les autres champs

}
