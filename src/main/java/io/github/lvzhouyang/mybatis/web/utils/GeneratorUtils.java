package io.github.lvzhouyang.mybatis.web.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class GeneratorUtils {
    public static final Logger LOGGER = LoggerFactory.getLogger(GeneratorUtils.class);

    public static void zipFolder(String sourceFolderName, String outputFileName) {
        try {
            FileOutputStream fos = new FileOutputStream(outputFileName);
            ZipOutputStream zos = new ZipOutputStream(fos);

            //level - the compression level (0-9)
            zos.setLevel(9);
            LOGGER.info("Begin to compress folder : " + sourceFolderName + " to " + outputFileName);
            addFolder(zos, sourceFolderName, sourceFolderName);
            zos.close();
            LOGGER.info("Program ended successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     * If a deletion fails, the method stops attempting to
     * delete and returns "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children == null) {
                return dir.delete();
            }
            //递归删除目录中的子目录下
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    private static void addFolder(ZipOutputStream zos, String folderName, String baseFolderName) throws Exception {
        File f = new File(folderName);
        if (!f.exists()) {
            LOGGER.info("File or directory not found " + folderName);
        }
        if (f.isDirectory()) {
            //Thank to peter
            //For pointing out missing entry for empty folder
            if (!folderName.equalsIgnoreCase(baseFolderName)) {
                String entryName = folderName.substring(baseFolderName.length() + 1, folderName.length()) + File.separatorChar;
                ZipEntry ze = new ZipEntry(entryName);
                zos.putNextEntry(ze);
            }
            File f2[] = f.listFiles();
            if (f2 == null) {
                return;
            }
            for (File aF2 : f2) {
                addFolder(zos, aF2.getAbsolutePath(), baseFolderName);
            }
        } else {
            //add file
            //extract the relative name for entry purpose
            String entryName = folderName.substring(baseFolderName.length() + 1, folderName.length());
            ZipEntry ze = new ZipEntry(entryName);
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream(folderName);
            int len;
            byte buffer[] = new byte[1024];
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
            in.close();
            zos.closeEntry();
        }

    }


}
