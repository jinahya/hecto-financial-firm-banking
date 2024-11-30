package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.util.Objects;

@SuppressWarnings({
        "java:S119"  // Type parameter names should comply with a naming convention
})
abstract class IFullTextSegmentCodec__Test<CODEC extends IFullTextSegmentCodec<VALUE>, VALUE> {

    IFullTextSegmentCodec__Test(final Class<CODEC> codecClass, final Class<VALUE> valueClass) {
        super();
        this.codecClass = Objects.requireNonNull(codecClass, "codecClass is null");
        this.valueClass = Objects.requireNonNull(valueClass, "valueClass is null");
    }

    // ------------------------------------------------------------------------------------------------------ codecClass
    CODEC newCodecInstance() {
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
    final Class<CODEC> codecClass;

    final Class<VALUE> valueClass;
}