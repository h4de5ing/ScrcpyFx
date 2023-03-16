module com.android.scrcpy.scrcpyfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.android.scrcpy.scrcpyfx to javafx.fxml;
    exports com.android.scrcpy.scrcpyfx;
}