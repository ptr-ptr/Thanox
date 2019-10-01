package github.tornaco.android.thanos.core.util;

import android.util.Log;
import com.google.common.io.Files;
import github.tornaco.java.common.util.CollectionUtils;
import github.tornaco.java.common.util.Consumer;
import github.tornaco.java.common.util.IoUtils;

import java.io.*;
import java.nio.charset.Charset;


/**
 * Created by Nick@NewStand.org on 2017/3/13 10:03
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class FileUtils {

    /**
     * Interface definition for a callback to be invoked regularly as
     * verification proceeds.
     */
    public interface ProgressListener {
        /**
         * Called periodically as the verification progresses.
         *
         * @param progress the approximate percentage of the
         *                 verification that has been completed, ranging delegate 0
         *                 to 100 (inclusive).
         */
        public void onProgress(float progress);
    }


    public static void copy(String spath, String dpath, ProgressListener listener) throws IOException {
        FileInputStream fis = new FileInputStream(spath);
        FileOutputStream fos = new FileOutputStream(dpath);
        int totalByte = fis.available();
        int read = 0;
        int n;
        byte[] buffer = new byte[4096];
        while ((n = fis.read(buffer)) != -1) {
            fos.write(buffer, 0, n);
            fos.flush();
            read += n;
            float per = (float) read / (float) totalByte;
            if (listener != null) {
                listener.onProgress(per * 100);
            }
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {

        }
        IoUtils.closeQuietly(fis);
        IoUtils.closeQuietly(fos);
    }

    public static String formatSize(long fileSize) {
        String wellFormatSize = "";
        if (fileSize >= 0 && fileSize < 1024) {
            wellFormatSize = fileSize + "B";
        } else if (fileSize >= 1024 && fileSize < (1024 * 1024)) {
            wellFormatSize = Long.toString(fileSize / 1024) + "KB";
        } else if (fileSize >= (1024 * 1024) && fileSize < (1024 * 1024 * 1024)) {
            wellFormatSize = Long.toString(fileSize / (1024 * 1024)) + "MB";
        } else if (fileSize >= (1024 * 1024 * 1024)) {
            wellFormatSize = Long.toString(fileSize / (1024 * 1024 * 1024)) + "GB";
        }
        return wellFormatSize;
    }

    public static boolean deleteDir(File dir) {
        final boolean[] res = {true};
        CollectionUtils.consumeRemaining(Files.fileTreeTraverser()
                .postOrderTraversal(dir), new Consumer<File>() {
            @Override
            public void accept(File file) {
                if (!file.delete()) {
                    res[0] = false;
                }
            }
        });
        return res[0];
    }

    public static void deleteDirQuiet(File dir) {
        try {
            DevNull.accept(deleteDir(dir));
            DevNull.accept(dir.delete());
        } catch (Throwable ignored) {

        }
    }

    public static boolean writeString(String str, String path) {
        BufferedWriter bf = null;
        try {
            Files.createParentDirs(new File(path));
            bf = Files.newWriter(new File(path), Charset.defaultCharset());
            bf.write(str, 0, str.length());
            return true;
        } catch (Exception e) {
            Log.e(FileUtils.class.getSimpleName(), "Fail to write file:" + Log.getStackTraceString(e));
        } finally {
            IoUtils.closeQuietly(bf);
        }
        return false;
    }

    public static String readString(String path) {
        BufferedReader reader = null;
        try {
            if (!new File(path).exists()) {
                return null;
            }
            reader = Files.newReader(new File(path), Charset.defaultCharset());
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            Log.e(FileUtils.class.getSimpleName(), "Fail to read file:" + Log.getStackTraceString(e));
        } catch (OutOfMemoryError oom) {
            Log.e(FileUtils.class.getSimpleName(), "OOM while read file:" + Log.getStackTraceString(oom));
        } finally {
            IoUtils.closeQuietly(reader);
        }
        return null;
    }

    public static boolean isEmptyDir(File dir) {
        return dir.exists() && dir.isDirectory() && dir.list().length == 0;
    }

    public static boolean isEmptyDirOrNoExist(File dir) {
        if (!dir.exists()) {
            return true;
        }
        return isEmptyDir(dir);
    }
}
