import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.stream.Collectors;
import java.util.List;
import java.util.stream.Stream;

public class JarClasses {

    private static List<String> findAllClassesInJar(JarFile jar) {
        Stream<JarEntry> stream = jar.stream();

        return stream
                .filter(entry -> entry.getName().endsWith(".class"))
                .map(entry -> getFQN(entry.getName()))
                .sorted()
                .collect(Collectors.toList());
    }

    private static String getFQN(String resourceName) {
        return resourceName.replaceAll("/", ".").substring(0, resourceName.lastIndexOf('.'));
    }

    private static void loadClass(String className, ClassLoader cl) {
        try {
            Class c = cl.loadClass(className);
            int publicCnt = 0, privateCnt = 0, protectedCnt = 0, staticCnt = 0;

            for (final Method method : c.getDeclaredMethods()) {
                int modifier = method.getModifiers();
                if(Modifier.isPublic(modifier)) {
                    publicCnt++;
                }
                if(Modifier.isPrivate(modifier)) {
                    privateCnt++;
                }
                if(Modifier.isProtected(modifier)) {
                    protectedCnt++;
                }
                if(Modifier.isStatic(modifier)) {
                    staticCnt++;
                }
            }
            Field[] fields = c.getDeclaredFields();

            System.out.println(
                    "----------" + className + "----------\n" +
                    "  Public methods: " + publicCnt + "\n" +
                    "  Private methods: " + privateCnt + "\n" +
                    "  Protected methods: " + protectedCnt + "\n" +
                    "  Static methods: " + staticCnt + "\n" +
                    "  Fields: " + fields.length);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }
    }


    public static void main(String[] args) {
        String jarFile = args[0];
        JarFile jar = null;
        ClassLoader cl = null;
        try {
            jar = new JarFile(jarFile);
            URL[] urls = new URL[]{new File(jarFile).toURI().toURL()};
            cl = new URLClassLoader(urls);

            List<String> classNames = findAllClassesInJar(jar);
            for (String name : classNames) {
                loadClass(name, cl);
            }
        } catch (IOException e) { e.printStackTrace(); }

    }

}