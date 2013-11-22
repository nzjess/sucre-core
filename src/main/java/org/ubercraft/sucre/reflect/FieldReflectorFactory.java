package org.ubercraft.sucre.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class reflects a class's accessors and its mutators using Java reflection on its fields.
 */
public class FieldReflectorFactory implements ReflectorFactory {

    public FieldReflectorFactory() {}

    @Override
    public Reflector createReflector(Class<?> type) {
        return new FieldReflectorImpl(type);
    }

    private static class FieldReflectorImpl implements Reflector {

        private Class<?> type;

        private Map<String, ? super FieldAccessorMutator> accessorsMutators = null;

        FieldReflectorImpl(Class<?> type) {
            this.type = type;
        }

        @Override
        public void preCache() throws Exception {
            accessorsMutators();
        }

        @Override
        public Accessor getAccessor(String name) {
            return (Accessor)getAccessorMutator(name);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Iterable<Accessor> getAccessors() {
            return (Iterable<Accessor>)iterateAccessorsMutators();
        }

        @Override
        public Mutator getMutator(String name) {
            return (Mutator)getAccessorMutator(name);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Iterable<Mutator> getMutators() {
            return (Iterable<Mutator>)iterateAccessorsMutators();
        }

        private Object getAccessorMutator(String name) {
            return accessorsMutators().get(name);
        }

        private Iterable<? super FieldAccessorMutator> iterateAccessorsMutators() {
            return accessorsMutators().values();
        }

        private Map<String, ? super FieldAccessorMutator> accessorsMutators() {
            if (accessorsMutators == null) {
                accessorsMutators = createAccessorMutatorMap();
            }
            return accessorsMutators;
        }

        private Map<String, ? super FieldAccessorMutator> createAccessorMutatorMap() {

            Map<String, ? super FieldAccessorMutator> accessorsMutators = new HashMap<String, FieldAccessorMutator>();

            // get all declared fields from class hierarchy
            List<Field> fieldList = findAllDeclaredFields(type);
            for (Iterator<Field> iterator = fieldList.iterator(); iterator.hasNext();) {
                Field field = iterator.next();

                // implement some rules to decide if this field is an accessor/mutator or not

                if (Modifier.isTransient(field.getModifiers()) || // don't include transient fields
                        Modifier.isStatic(field.getModifiers())) // or static fields
                {
                    iterator.remove();
                    continue;
                }

                Class<?> fieldType = field.getType();
                String fieldName = field.getName();

                // create field accessor/mutator
                accessorsMutators.put(fieldName, new FieldAccessorMutator(field, fieldName, fieldType));
            }

            // ensure fields are accessible
            Field[] fields = (Field[])fieldList.toArray(new Field[fieldList.size()]);
            Field.setAccessible(fields, true);

            return accessorsMutators;
        }

        private static List<Field> findAllDeclaredFields(Class<?> type) {
            List<Field> fieldList = new ArrayList<Field>();
            findAllDeclaredFields(type, fieldList);
            return fieldList;
        }

        private static void findAllDeclaredFields(Class<?> type, List<Field> fieldList) {
            Field[] declaredFields = type.getDeclaredFields();
            for (int i = 0; i < declaredFields.length; i++) {
                fieldList.add(declaredFields[i]);
            }

            type = type.getSuperclass();
            if (type == Object.class) return;

            findAllDeclaredFields(type, fieldList);
        }

        // used to cache accessor/mutator fields for types
        private static class FieldAccessorMutator extends AbstractAccessorMutatorBase implements Accessor, Mutator, FieldReflector {

            private final Field field;

            FieldAccessorMutator(Field field, String name, Class<?> type) {
                super(name, type);
                this.field = field;
            }

            @Override
            public Field getField() {
                return field;
            }

            @Override
            public Class<?> getDeclaringType() {
                return field.getDeclaringClass();
            }

            @Override
            public Object access(Object object) {
                try {
                    return field.get(object);
                }
                catch (RuntimeException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new ReflectException("access on field: '" + this + "' failed", e);
                }
            }

            @Override
            public void mutate(Object object, Object value) {
                try {
                    field.set(object, value);
                }
                catch (RuntimeException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new ReflectException("mutate on field: '" + this + "' failed", e);
                }
            }

            @Override
            public boolean isPaired() {
                return true;
            }

            @Override
            public Accessor getPairedAccessor() {
                return this;
            }

            @Override
            public Mutator getPairedMutator() {
                return this;
            }

            @Override
            public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
                return field.getAnnotation(annotationClass);
            }

            @Override
            public Annotation[] getAnnotations() {
                return field.getAnnotations();
            }
        }
    }
}
