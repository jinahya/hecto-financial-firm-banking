package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

abstract class FullTextSegmentCodec$Test<C extends FullTextSegmentCodec<V>, V> {

    FullTextSegmentCodec$Test(final Class<C> codecClass, final Class<V> valueClass) {
        super();
        this.codecClass = Objects.requireNonNull(codecClass, "codecClass is null");
        this.valueClass = Objects.requireNonNull(valueClass, "valueClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void __Null() {
        final var instance = newCodecInstance();
        final var length = ThreadLocalRandom.current().nextInt(128) + 1;
        final var encoded = instance.encode(null, length);
        assertThat(encoded).isNotNull().hasSize(length);
        final var decoded = instance.decode(encoded);
        assertThat(decoded).isNull();
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