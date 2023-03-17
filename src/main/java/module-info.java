module com.android.scrcpy.scrcpyfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.desktop;
    requires org.json;


    opens com.android.scrcpy.scrcpyfx to javafx.fxml;
    exports com.android.scrcpy.scrcpyfx;
    exports com.android.scrcpy.scrcpyfx.change;
    exports com.android.scrcpy.scrcpyfx.utils;
    opens com.android.scrcpy.scrcpyfx.utils to javafx.fxml;
}