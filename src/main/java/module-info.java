module com.example.project4_yuvrajchandra {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.project4_yuvrajchandra to javafx.fxml;
    exports com.example.project4_yuvrajchandra;
}