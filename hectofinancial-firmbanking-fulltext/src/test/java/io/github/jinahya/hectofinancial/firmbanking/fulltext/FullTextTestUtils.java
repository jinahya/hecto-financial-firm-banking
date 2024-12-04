package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.provider.Arguments;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
final class FullTextTestUtils {

    static Stream<Arguments> getCategoryTextCodeAndTaskCodeArgumentsStream() {
        return Stream.of(
                Arguments.of(FullTextCategory.D, "1000", "500")
        );
    }

    static String getResourceNameForFullText(final FullTextCategory category, final String textCode,
                                             final String taskCode) {
        return category.name() + textCode + '_' + taskCode + ".fulltext";
    }

    static byte[] loadFullTextData(final FullTextCategory category, final String textCode, final String taskCode) {
        final var name = getResourceNameForFullText(category, textCode, taskCode);
        try (var resource = FullTextTestUtils.class.getResourceAsStream(name)) {
            assertThat(resource)
                    .as("resource for '%1$s'", name)
                    .isNotNull();
            final var instance = FullText.newInstance(category, textCode, taskCode);
            final var src = resource.readNBytes(instance.getLength());
            return src;
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    static FullText loadFullText(final FullTextCategory category, final String textCode, final String taskCode) {
        final var instance = FullText.newInstance(category, textCode, taskCode);
        final var data = loadFullTextData(category, textCode, taskCode);
        instance.setRawData(ByteBuffer.wrap(data));
        assertThat(instance.getTextCode()).isEqualTo(textCode);
        assertThat(instance.getTaskCode()).isEqualTo(taskCode);
        return instance;
    }

    static <R> R applyCipherKeyAndParams(
            final Function<? super Cipher,
                    ? extends Function<? super Key,
                            ? extends Function<? super AlgorithmParameterSpec,
                                    ? extends R>>> function) {
        try {
            final var cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final Key key;
            {
                final var bytes = new byte[16];
                ThreadLocalRandom.current().nextBytes(bytes);
                key = new SecretKeySpec(bytes, "AES");
            }
            final AlgorithmParameterSpec params;
            {
                final var bytes = new byte[16];
                ThreadLocalRandom.current().nextBytes(bytes);
                params = new IvParameterSpec(bytes);
            }
            return function.apply(cipher).apply(key).apply(params);
        } catch (final Exception e) {
            throw new RuntimeException("failed to create a cipher, a key, and a params", e);
        }
    }

    static void acceptCipherKeyAndParams(
            final Function<? super Cipher,
                    ? extends Function<? super Key,
                            ? extends Consumer<? super AlgorithmParameterSpec>>> consumer) {
        applyCipherKeyAndParams(c -> k -> p -> {
            consumer.apply(c).apply(k).accept(p);
            return null;
        });
    }

    private FullTextTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}