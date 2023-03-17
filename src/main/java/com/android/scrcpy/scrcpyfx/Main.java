package com.android.scrcpy.scrcpyfx;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("参数个数:" + args.length);
            for (int i = 0; i < args.length; i++) {
                System.out.println("参数 " + i + " :" + args[i]);
            }
            if (args.length > 0) APKPath.apkPath = args[0];
        } catch (Exception ignored) {
        }
        HelloApplication.main(args);
    }
}