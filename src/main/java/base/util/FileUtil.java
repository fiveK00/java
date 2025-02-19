package base.util;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.List;

public class FileUtil {

    public static final String RESOURCES_PATH = FileUtil.class.getClassLoader().getResource("").getPath();

    public static final String TEMP_RESOURCES_PATH = RESOURCES_PATH + "temp";

    public static URL getResource(String resourcePath) {
        return FileUtil.class.getClassLoader().getResource(resourcePath);
    }

    public static File createTempFile(String resourcePath) throws IOException {
        File file = new File(TEMP_RESOURCES_PATH + resourcePath);
        File dir = file.getParentFile();
        if(!dir.exists() && !dir.mkdirs()){
            throw new AccessDeniedException(dir.getPath());
        }

        file = File.createTempFile( file.getName(), "", dir);
        file.deleteOnExit();

        return file;
    }

    public static File createFile(String resourcePath) throws IOException {
        File file = new File(RESOURCES_PATH + resourcePath);
        File dir = file.getParentFile();
        if(!dir.exists() && !dir.mkdirs()){
            throw new AccessDeniedException(dir.getPath());
        }

        if(file.exists() || !file.createNewFile()){
            throw new FileAlreadyExistsException(file.getPath());
        }

        return file;
    }

    public static InputStream inputStream(String resourcePath) throws IOException {
        URL url = getResource(resourcePath);
        if (url == null || url.getFile() == null) {
            throw new FileNotFoundException(resourcePath);
        }

        return url.openStream();
    }

    /**
     * 读取资源路径下的文件
     * @param resourcePath
     * @return
     * @throws IOException
     */
    public static String readContent(String resourcePath) throws IOException {
        URL url = getResource(resourcePath);
        if (url == null || url.getFile() == null) {
            throw new FileNotFoundException(resourcePath);
        }

        try {
            Path path = Path.of(url.toURI());
            List<String> allLines = Files.readAllLines(path);
            return String.join("\n", allLines);
        } catch (URISyntaxException e) {
            throw new FileNotFoundException(url.toString());
        }
    }


    public static void main(String[] args) {
        try {
            File file = FileUtil.createTempFile("/asd/report.txt");
            System.out.println(file.exists());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
