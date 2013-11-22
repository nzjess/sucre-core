package org.ubercraft.sucre.reflect;

import static org.ubercraft.sucre.reflect.ReflectHelper.accessorMethodNameToFieldName;
import static org.ubercraft.sucre.reflect.ReflectHelper.mutatorMethodNameToFieldName;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * This class reflects a class's accessors and its mutators using Java reflection on its methods.
 */
public class MethodReflectorFactory implements ReflectorFactory {

    public MethodReflectorFactory() {}

    @Override
    public Reflector createReflector(Class<?> type) {
        return new MethodReflectorImpl(type);
    }

    private static class MethodReflectorImpl implements Reflector {

        private Class<?> type;

        private Map<String, Accessor> accessors = null;
        private Map<String, Mutator> mutators = null;

        MethodReflectorImpl(Class<?> type) {
            this.type = type;
        }

        @Override
        public void preCache() throws Exception {
            accessors();
            mutators();
        }

        @Override
        public Accessor getAccessor(String name) {
            return (Accessor)accessors().get(name);
        }

        @Override
        public Iterable<Accessor> getAccessors() {
            return accessors().values();
        }

        @Override
        public Mutator getMutator(String name) {
            return (Mutator)mutators().get(name);
        }

        @Override
        public Iterable<Mutator> getMutators() {
            return mutators().values();
        }

        private Map<String, Accessor> accessors() {
            if (accessors == null) {
                accessors = createAccessorMap();
            }
            return accessors;
        }

        private Map<String, Mutator> mutators() {
            if (mutators == null) {
                mutators = createMutatorMap();
            }
            return mutators;
        }

        private Map<String, Accessor> createAccessorMap() {
            Map<String, Accessor> accessors = new HashMap<String, Accessor>();

            Method[] methods = type.getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];

                // implement some rules to decide if this method is an accessor or not

                // not interested in methods declared by Object
                if (method.getDeclaringClass() == Object.class) continue;

                // method name should start with either 'get' or 'is'
                String fieldName = accessorMethodNameToFieldName(method.getName());
                if (fieldName == null) continue;

                // method should have no parameters
                if (method.getParameterTypes().length != 0) continue;

                // method have non-void return type
                Class<?> returnType = method.getReturnType();
                if (returnType == Void.TYPE) continue;

                // everything checks out, add accessor to the list
                accessors.put(fieldName, new MethodAccessorImpl(method, fieldName, returnType));
            }

            return accessors;
        }

        // create method mutator map
        private Map<String, Mutator> createMutatorMap() {
            Map<String, Mutator> mutators = new HashMap<String, Mutator>();

            Method[] methods = type.getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];

                // implement some rules to decide if this method is a mutator or not

                // not interested in methods declared by Object
                if (method.getDeclaringClass() == Object.class) continue;

                // method name should start with 'get'
                String fieldName = mutatorMethodNameToFieldName(method.getName());
                if (fieldName == null) continue;

                // method should have a single parameter
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) continue;
                Class<?> parameterType = parameterTypes[0];

                // method have void return type
                Class<?> returnType = method.getReturnType();
                if (returnType != Void.TYPE) continue;

                // everything checks out, add mutator to list
                mutators.put(fieldName, new MethodMutatorImpl(method, fieldName, parameterType));
            }

            return mutators;
        }

        private abstract class MethodAccessorMutator extends AbstractAccessorMutatorBase implements AccessorMutator, MethodReflector {

            protected final Method method;

            protected boolean checkedPair = false;

            MethodAccessorMutator(Method method, String name, Class<?> type) {
                super(name, type);
                this.method = method;
            }

            @Override
            public Method getMethod() {
                return method;
            }

            @Override
            public Class<?> getDeclaringType() {
                return method.getDeclaringClass();
            }

            @Override
            public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
                return method.getAnnotation(annotationClass);
            }

            @Override
            public Annotation[] getAnnotations() {
                return method.getAnnotations();
            }
        }

        // used to cache accessor methods for types
        private class MethodAccessorImpl extends MethodAccessorMutator implements Accessor {

            private Mutator pairedMutator;

            MethodAccessorImpl(Method method, String name, Class<?> type) {
                super(method, name, type);
            }

            private void checkPair() {
                if (!checkedPair) {
                    checkedPair = true;
                    pairedMutator = mutators().get(getName());
                }
            }

            @Override
            public Object access(Object object) {
                try {
                    return method.invoke(object, (Object[])null);
                }
                catch (RuntimeException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean isPaired() {
                checkPair();
                return (pairedMutator != null);
            }

            @Override
            public Accessor getPairedAccessor() {
                return this;
            }

            @Override
            public Mutator getPairedMutator() {
                checkPair();
                return pairedMutator;
            }
        }

        // used to cache mutator methods for types
        private class MethodMutatorImpl extends MethodAccessorMutator implements Mutator {

            private Accessor pairedAccessor;

            MethodMutatorImpl(Method method, String name, Class<?> type) {
                super(method, name, type);
            }

            private void checkPair() {
                if (!checkedPair) {
                    checkedPair = true;
                    pairedAccessor = accessors().get(getName());
                }
            }

            @Override
            public void mutate(Object object, Object value) {
                try {
                    method.invoke(object, value);
                }
                catch (RuntimeException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean isPaired() {
                checkPair();
                return (pairedAccessor != null);
            }

            @Override
            public Accessor getPairedAccessor() {
                checkPair();
                return pairedAccessor;
            }

            @Override
            public Mutator getPairedMutator() {
                return this;
            }
        }
    }
}
