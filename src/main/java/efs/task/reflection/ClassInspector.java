package efs.task.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

public class ClassInspector {

  public static Collection<String> getAnnotatedFields(final Class<?> type,
                                                      final Class<? extends Annotation> annotation) {
    HashSet<String> annotatedFields = new HashSet<>();
    Field[] fields = type.getDeclaredFields();
    for (Field field : fields) {
      if (field.isAnnotationPresent(annotation)) {
        annotatedFields.add(field.getName());
      }
    }
    return annotatedFields;
  }

  public static Collection<String> getAllDeclaredMethods(final Class<?> type) {
    HashSet<String> declaredMethods = new HashSet<>();
    Method[] methods = type.getDeclaredMethods();
    for (Method method : methods) {
      declaredMethods.add(method.getName());
    }
    Class<?>[] interfaces = type.getInterfaces();
    for (Class<?> intf : interfaces) {
      Method[] intfMethods = intf.getMethods();
      for (Method method : intfMethods) {
        declaredMethods.add(method.getName());
      }
    }
    return declaredMethods;
  }

  public static <T> T createInstance(final Class<T> type, final Object... args) throws Exception {
    Constructor<?>[] constructors = type.getDeclaredConstructors();
    for (Constructor<?> constructor : constructors) {
      Class<?>[] parameterTypes = constructor.getParameterTypes();
      if (parameterTypes.length == args.length) {
        boolean match = true;
        for (int i = 0; i < parameterTypes.length; i++) {
          if (!parameterTypes[i].isInstance(args[i])) {
            match = false;
            break;
          }
        }
        if (match) {
          constructor.setAccessible(true);
          return type.cast(constructor.newInstance(args));
        }
      }
    }
    throw new Exception("Matching constructor not found");
  }
}