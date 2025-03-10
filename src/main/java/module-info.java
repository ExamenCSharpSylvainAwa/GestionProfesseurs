module sn.groupeisi.gestionprofesseurs {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    requires static lombok;
    requires javafx.graphics;
    requires java.base;
    requires java.naming;
    requires java.sql;
    requires java.persistence;
    requires org.hibernate.orm.core;
    requires spring.security.crypto;

    opens sn.groupeisi.gestionprofesseurs to javafx.fxml;
    exports sn.groupeisi.gestionprofesseurs;
    exports sn.groupeisi.gestionprofesseurs.Controllers;
    opens sn.groupeisi.gestionprofesseurs.Entities to org.hibernate.orm.core, javafx.base;
    opens sn.groupeisi.gestionprofesseurs.Controllers to javafx.fxml;
}