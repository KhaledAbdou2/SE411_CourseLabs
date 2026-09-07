package edu.psu.se411.lab02;

import java.util.ArrayList;
import java.util.List;

public class Pipeline<T, R> {

    private final List<Transformer<?, ?>> transformers;

    private Pipeline(List<Transformer<?, ?>> transformers) {
        this.transformers = transformers;
    }

    public static <T> Pipeline<T, T> start() {
        return new Pipeline<>(new ArrayList<>());
    }

    public <V> Pipeline<T, V> add(Transformer<R, V> transformer) {
        List<Transformer<?, ?>> updatedTransformers =
                new ArrayList<>(transformers);

        updatedTransformers.add(transformer);

        return new Pipeline<>(updatedTransformers);
    }

    @SuppressWarnings("unchecked")
    public R execute(T input) {
        Object result = input;

        for (Transformer<?, ?> transformer : transformers) {
            Transformer<Object, Object> current =
                    (Transformer<Object, Object>) transformer;

            result = current.transform(result);
        }

        return (R) result;
    }
}