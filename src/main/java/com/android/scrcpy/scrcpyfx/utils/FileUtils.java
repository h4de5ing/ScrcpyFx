package com.android.scrcpy.scrcpyfx.utils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Create by h4de5ing 2016/5/7 007
 */
public class FileUtils {

    public static void closeIO(Closeable... closeables) {
        if (null == closeables) return;
        for (Closeable cb : closeables) {
            try {
                if (null == cb) continue;
                cb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean deleteFile(String filename) {
        return new File(filename).delete();
    }

    public static void deleteFileByDirectory(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            for (File file : Objects.requireNonNull(directory.listFiles())) file.delete();
        }
    }

    public static boolean isFileExist(String filePath) {
        return new File(filePath).exists();
    }

    public static boolean writeFile(String filename, String content, boolean append) {
        boolean isSuccess = false;
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(filename, append));
            bufferedWriter.write(content);
            isSuccess = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(bufferedWriter);
        }
        return isSuccess;
    }

    public static String readFileJava8(String filename) {
        StringBuilder sb = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
            for (String line : lines) sb.append(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String readFile(String filename) {
        File file = new File(filename);
        BufferedReader bufferedReader = null;
        StringBuilder sb = new StringBuilder();
        try {
            if (file.exists()) {
                bufferedReader = new BufferedReader(new FileReader(filename));
                String line;
                while ((line = bufferedReader.readLine()) != null) sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(bufferedReader);
        }
        return sb.toString();
    }

    public static StringBuilder readFile(File file, String charsetName) {
        StringBuilder fileContent = new StringBuilder();
        if (file == null || !file.isFile()) return null;
        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) fileContent.append("\r\n");
                fileContent.append(line);
            }
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            closeIO(reader);
        }
    }

    public static void copyFile(InputStream in, OutputStream out) {
        try {
            byte[] b = new byte[2 * 1024 * 1024]; //2M memory
            int len;
            while ((len = in.read(b)) > 0) {
                out.write(b, 0, len);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(in, out);
        }
    }

    public static void copyFileFast(File in, File out) {
        FileChannel fileIn = null;
        FileChannel fileOut = null;
        try {
            fileIn = new FileInputStream(in).getChannel();
            fileOut = new FileOutputStream(out).getChannel();
            fileIn.transferTo(0, fileIn.size(), fileOut);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeIO(fileIn, fileOut);
        }
    }


    public static void zip(InputStream is, OutputStream os) {
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(os);
            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) != -1) {
                gzip.write(buf, 0, len);
                gzip.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(is, gzip);
        }
    }

    public static void unzip(InputStream is, OutputStream os) {
        GZIPInputStream gzip = null;
        try {
            gzip = new GZIPInputStream(is);
            byte[] buf = new byte[1024];
            int len;
            while ((len = gzip.read(buf)) != -1) os.write(buf, 0, len);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(gzip, os);
        }
    }


    public static void Stream2File(InputStream is, File file) {
        byte[] b = new byte[1024];
        int len;
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            while ((len = is.read(b)) != -1) {
                os.write(b, 0, len);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(is, os);
        }
    }

    public static boolean createFolder(String filePath) {
        return createFolder(filePath, false);
    }

    public static boolean createFolder(String filePath, boolean recreate) {
        String folderName = getFolderName(filePath);
        if (folderName == null || folderName.length() == 0 || folderName.trim().length() == 0) return false;
        File folder = new File(folderName);
        if (folder.exists()) {
            if (recreate) {
                deleteFile(folderName);
                return folder.mkdirs();
            } else return true;
        } else return folder.mkdirs();
    }


    public static boolean rename(String filepath, String newName) {
        File file = new File(filepath);
        return file.exists() && file.renameTo(new File(newName));
    }

    public static String getFolderName(String filePath) {
        if (filePath == null || filePath.length() == 0 || filePath.trim().length() == 0) return filePath;
        int filePos = filePath.lastIndexOf(File.separator);
        return (filePos == -1) ? "" : filePath.substring(0, filePos);
    }

    public static ArrayList<File> getFilesArray(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        ArrayList<File> listFile = new ArrayList<>();
        if (files != null) {
            for (File value : files) {
                if (value.isFile()) listFile.add(value);
                if (value.isDirectory()) listFile.addAll(getFilesArray(value.toString()));
            }
        }
        return listFile;
    }

    public static boolean deleteFiles(String folder) {
        if (folder == null || folder.length() == 0 || folder.trim().length() == 0) return true;
        File file = new File(folder);
        if (!file.exists()) return true;
        if (file.isFile()) return file.delete();
        if (!file.isDirectory()) return false;
        for (File f : Objects.requireNonNull(file.listFiles())) {
            if (f.isFile()) f.delete();
            else if (f.isDirectory()) deleteFile(f.getAbsolutePath());
        }
        return file.delete();
    }

    public static boolean isNumber(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
