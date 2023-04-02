package xyz.terrific.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class FileUtils {
    public static String readFile(File file) {
        if (!file.exists()) {
            return null;
        }

        InputStream stream;
        try {
            stream = Files.newInputStream(file.toPath());

            StringBuilder builder = new StringBuilder();
            int i;
            while ((i = stream.read()) != -1) {
                builder.append((char) i);
            }
            stream.close();

            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
