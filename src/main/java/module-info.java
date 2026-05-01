module com.champlain.soft.game {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.champlain.soft.game to javafx.fxml, javafx.graphics;
    exports com.champlain.soft.game;
}