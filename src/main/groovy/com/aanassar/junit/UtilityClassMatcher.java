package com.aanassar.junit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;

public class UtilityClassMatcher extends TypeSafeMatcher<Class<?>> {

    public static TypeSafeMatcher<Class<?>> isUtilityClass() {
        return new UtilityClassMatcher();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("is a utility class");
    }

    private Constructor<?> getConstructor(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor();
        } catch (Exception e1) {
            throw new AssertionError("Could not invoke constructor");
        }
    }

    @Override
    protected boolean matchesSafely(Class<?> clazz) {
        Assert.assertTrue("Class " + clazz + " must be final", Modifier.isFinal(clazz.getModifiers()));
        Assert.assertEquals("There must be only one constructor", 1, clazz.getDeclaredConstructors().length);
        Constructor<?> constructor = getConstructor(clazz);
        Assert.assertFalse("Constructor is accessible", constructor.isAccessible());
        Assert.assertTrue("Constructor is not private", Modifier.isPrivate(constructor.getModifiers()));
        invokeConstructor(constructor);
        for (final Method method : clazz.getMethods()) {
            // If there's a non-static method...
            if (!Modifier.isStatic(method.getModifiers())) {
                // ...it might belong to java.lang.Object.
                if (!method.getDeclaringClass().equals(clazz)) {
                    continue;
                }
                Assert.fail("There exists a non-static method: " + method);
            }

            if (method.getParameterTypes().length == 0) {
                try {
                    method.invoke(null, new Object[0]);
                } catch (Exception ex) {
                    Assert.fail("Could not invoke parameterless method " + method);
                }
            }
        }

        return true;
    }

    private void invokeConstructor(Constructor<?> constructor) {
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
        } catch (Exception ex) {
            throw new AssertionError("Could not invoke default constructor", ex);
        }
        constructor.setAccessible(false);
    }
}
