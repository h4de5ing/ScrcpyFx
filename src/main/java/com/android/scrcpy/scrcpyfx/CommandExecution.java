package com.android.scrcpy.scrcpyfx;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandExecution {
    public final static String COMMAND_SH = "sh";
    public final static String COMMAND_EXIT = "exit\n";
    public final static String COMMAND_LINE_END = "\n";

    /**
     * Command执行结果
     *
     * @author Mountain
     */
    public static class CommandResult {
        public int result = -1;
        public String errorMsg;
        public String successMsg;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (result == 0) sb.append(successMsg);
            else sb.append(errorMsg);
            if (result == 0) System.out.println("{\n" + successMsg + "\n}");
            else System.err.println("{\n" + errorMsg + "\n}");
            return sb.toString();
        }
    }

    /**
     * 执行命令—单条
     *
     * @param command 一条命令
     * @return 返回结果
     */
    public static CommandResult execCommand(String command) {
        String[] commands = {command};
        return execCommand(commands);
    }

    /**
     * 执行命令-多条
     *
     * @param commands 多条命令
     * @return 执行结果
     */
    public static CommandResult execCommand(String[] commands) {
        CommandResult commandResult = new CommandResult();
        if (commands == null || commands.length == 0) return commandResult;
        Process process = null;
        DataOutputStream os = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg;
        StringBuilder errorMsg;
        try {
            process = Runtime.getRuntime().exec(COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command != null) {
                    os.write(command.getBytes());
                    os.writeBytes(COMMAND_LINE_END);
                    os.flush();
                }
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();
            commandResult.result = process.waitFor();
            successMsg = new StringBuilder();
            errorMsg = new StringBuilder();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) successMsg.append(s).append("\n");
            while ((s = errorResult.readLine()) != null) errorMsg.append(s).append("\n");
            commandResult.successMsg = successMsg.toString();
            commandResult.errorMsg = errorMsg.toString();
        } catch (Exception e) {
            String errMsg = e.getMessage();
            if (errMsg != null) System.err.println(errMsg);
            else e.printStackTrace();
        } finally {
            try {
                if (os != null) os.close();
                if (successResult != null) successResult.close();
                if (errorResult != null) errorResult.close();
            } catch (IOException e) {
                String errMsg = e.getMessage();
                if (errMsg != null) System.err.println(errMsg);
                else e.printStackTrace();
            }
            if (process != null) process.destroy();
        }
        return commandResult;
    }
}
