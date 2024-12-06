package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
final class FullTextCrypto_TestUtils {

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

    static <R> R applyFullTextCrypto(final Function<? super FullTextCrypto, ? extends R> function) {
        return applyCipherKeyAndParams(c -> k -> p -> function.apply(FullTextCrypto.newInstance(c, k, p)));
    }

    static void acceptFullTextCrypto(final Consumer<? super FullTextCrypto> consumer) {
        applyFullTextCrypto(s -> {
            consumer.accept(s);
            return null;
        });
    }

    private FullTextCrypto_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}