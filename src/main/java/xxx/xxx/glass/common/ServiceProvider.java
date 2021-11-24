package xxx.xxx.glass.common;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Simple ServiceProvider that can be used for simple dependency injection.
 */

public class ServiceProvider {

    private final Map<Class<?>, Supplier<?>> suppliers = new HashMap<>();

    /**
     * Used to add a new service to the provider.
     *
     * @param clazz    The class of the new service.
     * @param instance The instance of the new service.
     * @param <T>      The type of the new service.
     */

    public <T> void addService(final Class<?> clazz, final T instance) {

        suppliers.put(clazz, () -> instance);

    }

    /**
     * Used to add a new service supplier to the provider.
     *
     * @param clazz    The class of the new service supplier.
     * @param supplier The supplier.
     * @param <T>      The type of the new service supplier.
     */

    public <T> void addServiceSupplier(final Class<?> clazz, final Supplier<T> supplier) {

        suppliers.put(clazz, supplier);

    }

    /**
     * Used to get a service from the provider based on the
     * provided class. Might return null.
     *
     * @param clazz The class.
     * @param <T>   The type of the service.
     * @return The found service or <code>null</code>.
     */

    @SuppressWarnings("unchecked")
    public <T> T getService(final Class<T> clazz) {

        if (!suppliers.containsKey(clazz)) return null;
        return (T) suppliers.get(clazz).get();

    }

    public static class Builder {

        private final ServiceProvider serviceProvider = new ServiceProvider();

        public <T> Builder addService(final Class<?> clazz, final T instance) {

            serviceProvider.addService(clazz, instance);
            return this;

        }

        public <T> Builder addServiceSupplier(final Class<?> clazz, final Supplier<T> supplier) {

            serviceProvider.addServiceSupplier(clazz, supplier);
            return this;

        }

        public ServiceProvider build() {

            return serviceProvider;

        }

    }

}
