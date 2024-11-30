package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.util.Objects;

abstract class FullTextSegmentCodec__Test<C extends FullTextSegmentCodec<V>, V> {

    FullTextSegmentCodec__Test(final Class<C> codecClass, final Class<V> valueClass) {
        super();
        this.codecClass = Objects.requireNonNull(codecClass, "codecClass is null");
        this.valueClass = Objects.requireNonNull(valueClass, "valueClass is null");
    }

    // ------------------------------------------------------------------------------------------------------ codecClass
    C newCodecInstance() {
        try {
            final var constructor = codecClass.getDeclaredConstructor();
            if (!constructor.canAccess(null)) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance();
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException(roe);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    final Class<C> codecClass;

    final Class<V> valueClass;
}