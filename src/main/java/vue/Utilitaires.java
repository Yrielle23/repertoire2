package vue;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Utilitaires {

    /**
     * Parcourt le package donné dans le classpath
     * et retourne les noms des classes annotées @Controller
     */
    public static List<String> getControllerClasses(String packageName)
            throws Exception {

        List<String> result = new ArrayList<>();
        String path = packageName.replace('.', '/');
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = cl.getResource(path);

        if (url == null) {
            System.out.println("Package introuvable : " + packageName);
            return result;
        }

        File dir = new File(url.getFile());
        File[] files = dir.listFiles();
        if (files == null) return result;

        for (File file : files) {
            if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                Class<?> clazz = cl.loadClass(className);
                if (clazz.isAnnotationPresent(Controller.class)) {
                    result.add(className);
                }
            }
        }
        return result;
    }
}
