package sn.groupeisi.gestionprofesseurs.Utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import sn.groupeisi.gestionprofesseurs.Entities.Emargements;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.FileNotFoundException;
import java.util.List;

public class RapportExportateur {

    public static void genererRapportPDF(String fichierSortie) {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();

        try {
            // Récupérer tous les émargements
            TypedQuery<Emargements> query = entityManager.createQuery("FROM Emargements", Emargements.class);
            List<Emargements> emargementsList = query.getResultList();

            if (emargementsList.isEmpty()) {
                System.out.println("⚠️ Aucune donnée disponible pour l'exportation PDF.");
                return;
            }

            // Création du document PDF
            PdfWriter writer = new PdfWriter(fichierSortie);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Titre du rapport
            Paragraph titre = new Paragraph("Rapport d'Émargements")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(16)
                    .setBold();
            document.add(titre);

            // Création du tableau
            float[] columnWidths = {2, 2, 2, 2};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // En-têtes du tableau
            String[] headers = {"Nom", "Prénom", "Date", "Nombre d'Émargements"};
            for (String header : headers) {
                table.addHeaderCell(createHeaderCell(header));
            }

            // Ajout des données
            for (Emargements emargement : emargementsList) {
                table.addCell(createCell(emargement.getProfesseur().getNom()));
                table.addCell(createCell(emargement.getProfesseur().getPrenom()));
                table.addCell(createCell(emargement.getDate().toString()));
                table.addCell(createCell(String.valueOf(calculerNombreEmargements(emargement))));
            }

            // Ajout du tableau au document
            document.add(table);

            // Fermeture du document
            document.close();
            System.out.println("✅ Rapport PDF généré avec succès : " + fichierSortie);

        } catch (FileNotFoundException e) {
            System.err.println("❌ Erreur lors de la création du fichier PDF : " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    // Méthode pour calculer le nombre d'émargements (à adapter selon la logique réelle)
    private static int calculerNombreEmargements(Emargements emargement) {
        return 1; // Remplace ceci par la vraie logique si nécessaire
    }

    // Méthode utilitaire pour créer des cellules d'en-tête
    private static Cell createHeaderCell(String content) {
        return new Cell()
                .add(new Paragraph(content))
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12)
                .setBold();
    }

    // Méthode utilitaire pour créer des cellules standard
    private static Cell createCell(String content) {
        return new Cell()
                .add(new Paragraph(content))
                .setTextAlignment(TextAlignment.CENTER);
    }

    // Méthode surchargée pour être compatible avec votre code JavaFX existant
    public static void genererRapportPDF(List<Emargements> emargements, String cheminFichier) {
        // Ouvrir un EntityManager pour charger les données complètes si nécessaire
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();

        try {
            // Générer le rapport avec le chemin de fichier spécifié
            genererRapportPDF(cheminFichier);
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}