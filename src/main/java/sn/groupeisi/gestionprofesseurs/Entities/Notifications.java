package sn.groupeisi.gestionprofesseurs.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message", nullable = false)
    private String message;

    @ManyToOne
    @JoinColumn(name = "destinataire_id", referencedColumnName = "id", nullable = false)
    private Users destinataire;

    @Column(name = "date_envoi", nullable = false)
    private LocalDateTime dateEnvoi;

    // Constructeur sans ID (utilisé pour la création d'une nouvelle notification)
    public Notifications(String message, Users destinataire, LocalDateTime dateEnvoi) {
        this.message = message;
        this.destinataire = destinataire;
        this.dateEnvoi = dateEnvoi;
    }
}
