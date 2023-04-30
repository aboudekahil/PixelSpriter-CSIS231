package com.abdelkader.app.scenehandle;

import com.abdelkader.annotations.Component;
import com.abdelkader.annotations.View;
import com.abdelkader.app.PixelSpriter;
import com.abdelkader.interfaces.isController;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class SceneSwitcher {
    private static final Stage main = PixelSpriter.getStage();

    public static void goTo(Class<? extends Scene> clazz) {
        goTo(clazz, null, null);
    }

    public static void goTo(Class<? extends Scene> clazz, Object[] args, Class<?>[] argTypes) {
        try {
            Scene instance;
            Constructor<?> constructor = null;
            if (args == null || args.length == 0) {
                constructor = clazz.getDeclaredConstructor();
                instance    = (Scene) constructor.newInstance();
            }
            else {
                for (Constructor<?> c : clazz.getConstructors()) {
                    if (c.getParameterCount() == args.length) {
                        boolean match = true;
                        Class<?>[] types = c.getParameterTypes();
                        for (int i = 0; i < args.length; i++) {
                            if (!types[i].isAssignableFrom(argTypes[i])) {
                                match = false;
                                break;
                            }
                        }
                        if (match) {
                            constructor = c;
                            break;
                        }
                    }
                }
                if (constructor == null) {
                    throw new IllegalArgumentException("No matching constructor found for " + clazz.getName());
                }
                instance = (Scene) constructor.newInstance(args);
            }

            initController(instance);
            main.setScene(instance);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void initController(Scene instance)
    throws
    InstantiationException,
    IllegalAccessException,
    NoSuchMethodException,
    InvocationTargetException,
    NoSuchFieldException {

        if (instance.getClass().getAnnotation(View.class) == null) {
            throw new IllegalStateException("Scene class has to be annotated with " + View.class.getName());
        }

        if (instance.getClass().getAnnotation(View.class).controller() == null)
            return;

        isController
                controller =
                (isController) instance.getClass().getAnnotation(View.class).controller()
                                       .getDeclaredConstructor()
                                       .newInstance();

        for (Field declaredField : controller.getClass().getDeclaredFields()) {
            if (!declaredField.isAnnotationPresent(Component.class))
                continue;

            String fieldName;

            if (declaredField.getDeclaredAnnotation(Component.class).name().equals("")) {
                fieldName = declaredField.getName();
            }
            else {
                fieldName = declaredField.getDeclaredAnnotation(Component.class).name();
            }

            Field viewField = instance.getClass().getDeclaredField(fieldName);

            if (!viewField.getType().equals(declaredField.getType()))
                throw new IllegalAccessException("Field "
                                                 + fieldName
                                                 + " does not exist of type: "
                                                 + declaredField.getType().getName()
                                                 + " in class "
                                                 + instance.getClass().getName());

            viewField.setAccessible(true);

            Object fieldValue = viewField.get(instance);

            declaredField.setAccessible(true);
            declaredField.set(controller, fieldValue);

            declaredField.setAccessible(false);
            instance.getClass().getDeclaredField(fieldName).setAccessible(false);
        }


        controller.init();

    }
}