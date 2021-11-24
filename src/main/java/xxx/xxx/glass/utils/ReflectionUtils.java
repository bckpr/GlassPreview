package xxx.xxx.glass.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to make working with reflection easier.
 */

public class ReflectionUtils {

    private final static Map<String, Class<?>> classCache = new HashMap<>();
    private final static Map<String, Method> methodCache = new HashMap<>();
    private final static Map<String, Field> fieldCache = new HashMap<>();

    static {

        try {
            cacheClass(ClassType.BUKKIT, "SimplePluginManager", "plugin");
            cacheField("SimplePluginManager", "commandMap");
        } catch (final ClassNotFoundException | NoSuchFieldException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Used to access the currently registered commands.
     *
     * @return The found commands in a collection.
     */

    public static Collection<Command> getKnownCommands() {

        Collection<Command> knownCommands = new ArrayList<>();

        try {
            final SimpleCommandMap simpleCommandMap = (SimpleCommandMap) getCachedField("SimplePluginManager", "commandMap").get(Bukkit.getPluginManager());
            knownCommands = simpleCommandMap.getCommands();
        } catch (final IllegalAccessException ex) {
            ex.printStackTrace();
        }

        return knownCommands;

    }

    /**
     * Internal method to cache a resolved class.
     *
     * @param classType The class type for easier package access.
     * @param className The name of the class.
     * @throws ClassNotFoundException Gets thrown if the class couldn't be found.
     */

    private static void cacheClass(final ClassType classType, final String className) throws ClassNotFoundException {

        classCache.put(className, Class.forName(classType.getBasePackage() + className));

    }

    /**
     * Internal method to cache a resolved class.
     *
     * @param classType    The class type for easier package access.
     * @param className    The name of the class.
     * @param classPackage The sub package.
     * @throws ClassNotFoundException Gets thrown if the class couldn't be found.
     */

    private static void cacheClass(final ClassType classType, final String className, final String classPackage) throws ClassNotFoundException {

        classCache.put(className, Class.forName(classType.getBasePackage() + classPackage + "." + className));

    }

    /**
     * Internal method to cache a resolved method.
     *
     * @param className      The name of the class that contains the method.
     * @param methodName     The name of the method.
     * @param parameterTypes The parameter types of the method.
     * @throws NoSuchMethodException Gets thrown if the method couldn't be found.
     */

    private static void cacheMethod(final String className, final String methodName, final Class<?>... parameterTypes) throws NoSuchMethodException {

        final Class<?> cachedClass = getCachedClass(className);
        final Method method = cachedClass.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);

        methodCache.put(className + "#" + methodName, method);

    }

    /**
     * Internal method to cache a resolved field and make it accessible.
     *
     * @param className The name of the class that contains the field.
     * @param fieldName The name of the field.
     * @throws NoSuchFieldException Gets thrown if the field couldn't be found.
     */

    private static void cacheField(final String className, final String fieldName) throws NoSuchFieldException {

        final Class<?> cachedClass = getCachedClass(className);
        final Field field = cachedClass.getDeclaredField(fieldName);
        field.setAccessible(true);

        fieldCache.put(className + "#" + fieldName, field);

    }

    /**
     * Used to get a cached class by its name.
     *
     * @param className The name of the class.
     * @return The found class or null.
     */

    @Nullable
    public static Class<?> getCachedClass(final String className) {

        return classCache.get(className);

    }

    /**
     * Used to get a cached method by its name and class name.
     *
     * @param className  The name of the class that contains the method.
     * @param methodName The name of the method.
     * @return The found method or null.
     */

    @Nullable
    public static Method getCachedMethod(final String className, final String methodName) {

        return methodCache.get(className + "#" + methodName);

    }

    /**
     * Used to get a cached field by its name and class name.
     *
     * @param className The name of the class that contains the field.
     * @param fieldName The name of the field.
     * @return The found field or null.
     */

    @Nullable
    public static Field getCachedField(final String className, final String fieldName) {

        return fieldCache.get(className + "#" + fieldName);

    }

    /**
     * Enum to make server implementation class access easier by providing standard
     * packages in a constant way.
     */

    enum ClassType {

        NMS("net.minecraft.server." + ServerVersion.PACKAGE_VERSION + "."),
        CRAFT("org.bukkit.craftbukkit." + ServerVersion.PACKAGE_VERSION + "."),
        BUKKIT("org.bukkit."),
        MC("net.minecraft.");

        private final String basePackage;

        ClassType(final String basePackage) {

            this.basePackage = basePackage;

        }

        public String getBasePackage() {

            return basePackage;

        }

    }

}
