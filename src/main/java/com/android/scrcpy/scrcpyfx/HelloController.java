package com.android.scrcpy.scrcpyfx;

import com.android.scrcpy.scrcpyfx.change.ChangeListener;
import com.android.scrcpy.scrcpyfx.utils.FileUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HelloController implements Initializable {
    @FXML
    private CheckBox auto;
    @FXML
    private TextArea welcomeText;
    @FXML
    private Button reboot;
    @FXML
    private Button getInfo;
    @FXML
    private Button uninstall;
    private boolean isConnect = false;
    private boolean isAuto = false;
    private List<String> devices = new ArrayList<>();
    private String tempFile = "d:/a.txt";
    private String tempFilePath = "D:\\a.txt";
    private String packageName = "com.android.socket.adb";
    private String runMain = packageName + "/.MainActivity";
    private String apkPath = "D://app-release.apk";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        auto.selectedProperty().addListener((observableValue, oldValue, newValue) -> isAuto = newValue);
        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(mRunnable, 0, 3, TimeUnit.SECONDS);
    }

    private void connect() {
        try {
            devices.clear();
            CommandExecution.CommandResult commandResult = CommandExecution.execCommand("adb devices");
//            updateText(commandResult.toString());
            for (String s : commandResult.successMsg.split("\n")) {
                if (s.contains("\tdevice")) {
                    try {
                        devices.add(s.split("\tdevice")[0]);
                    } catch (Exception ignored) {
                    }
                }
            }
            isConnect = devices.size() > 0;
        } catch (Exception e) {
            updateError(e);
            e.printStackTrace();
        }
        reboot.setDisable(!isConnect);
        getInfo.setDisable(!isConnect);
        uninstall.setDisable(!isConnect);
        updateText("有【" + devices.size() + "】个设备连接");
    }

    @FXML
    protected void onRebootButtonClick() {
        alertConfirmation("确定重启设备？", (message) -> CommandExecution.execCommand("adb reboot"));
    }

    @FXML
    protected void onCleanButtonClick() {
        Platform.runLater(() -> welcomeText.setText(""));
    }

    @FXML
    protected void onUninstallButtonClick() {
        alertConfirmation("确定卸载应用？", (message) -> updateText("卸载服务 " + CommandExecution.execCommand("adb uninstall " + packageName)));
    }

    @FXML
    protected void onGetInfoButtonClick() {
        new Thread(() -> {
            CommandExecution.CommandResult commandResult = CommandExecution.execCommand("adb forward tcp:18000 tcp:19000");
            updateText("端口转发" + commandResult);
        }).start();
        new SocketTest(this::updateText);
    }

    boolean isSuccess = false;
    Runnable mRunnable = () -> {
        while (true) {
            if (isAuto) {
                connect();
                if (devices.size() > 0) {
                    if (!isSuccess) {
                        updateText("查看通信服务是否安装成功1");
                        File file = new File(tempFilePath);
                        file.delete();
                        new Thread(() -> CommandExecution.execCommand("adb shell pm list packages>" + tempFile)).start();
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        updateText("查看通信服务是否安装成功2");
                        if (file.exists()) {
                            updateText(file.getAbsolutePath() + " 文件存在");
                            String content = FileUtils.readFile(tempFile);
                            if (content.contains("package:" + packageName)) {
                                updateText("通信服务已安装");
                                isSuccess = true;
                                new Thread(() -> CommandExecution.execCommand("adb shell am start -n " + runMain)).start();
                            } else {
                                updateText("准备安装通信服务");
                                CommandExecution.CommandResult commandResult = CommandExecution.execCommand("adb install " + apkPath);
                                updateText("安装服务 " + commandResult);
                            }
                        } else {
                            updateText(file.getAbsolutePath() + " 文件不存在");
                        }
                    }
                } else isSuccess = false;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    };

    private void updateText(String message) {
        Platform.runLater(() -> welcomeText.appendText(message + "\n"));
    }

    private void updateError(Exception e) {
        Platform.runLater(() -> welcomeText.appendText("发生异常:\n[" + e.getMessage() + "]\n"));
    }

    private void alertConfirmation(String message, ChangeListener change) {
        Alert information = new Alert(Alert.AlertType.CONFIRMATION, "");
        information.setTitle("确定操作");
        information.setHeaderText(message);
        Optional<ButtonType> buttonType = information.showAndWait();
        if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
            try {
                change.change("");
            } catch (Exception e) {
                alertError(e);
                e.printStackTrace();
            }
        }
    }

    private void alertError(Exception e) {
        Alert information = new Alert(Alert.AlertType.ERROR, "发生错误");
        information.setTitle("Error");
        information.setHeaderText(e.getMessage());
        information.showAndWait();
    }
}