package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import javax.crypto.Cipher;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Objects;

/**
 * Parameters for encrypting/decrypting instance of {@link FullText}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see FullText#setSecurity(FullTextSecurity)
 */
public final class FullTextSecurity {

    /**
     * Creates new instance with specified arguments.
     *
     * @param cipher a cipher.
     * @param key    a key.
     * @param params a params.
     * @return a new instance.
     */
    public static FullTextSecurity newInstance(final Cipher cipher, final Key key,
                                               final AlgorithmParameterSpec params) {
        return new FullTextSecurity(cipher, key, params);
    }

    private FullTextSecurity(final Cipher cipher, final Key key, final AlgorithmParameterSpec params) {
        super();
        this.cipher = Objects.requireNonNull(cipher, "cipher is null");
        this.key = Objects.requireNonNull(key, "key is null");
        this.params = params;
    }

    void init(final int opmode) {
        if (params != null) {
            try {
                cipher.init(opmode, key, params);
            } catch (final Exception e) {
                throw new RuntimeException("failed to initialize the cipher", e);
            }
        } else {
            try {
                cipher.init(opmode, key);
            } catch (final Exception e) {
                throw new RuntimeException("failed to initialize the cipher", e);
            }
        }
    }

    ByteBuffer encrypt(final ByteBuffer input) {
        assert input != null;
        init(Cipher.ENCRYPT_MODE);
        final var output = ByteBuffer.allocate(cipher.getOutputSize(input.remaining()));
        try {
            final var bytes = cipher.doFinal(input, output);
            assert bytes <= output.capacity();
        } catch (final Exception e) {
            throw new RuntimeException("failed to encrypt", e);
        }
        return output;
    }

    ByteBuffer decrypt(final ByteBuffer input) {
        assert input != null;
        init(Cipher.DECRYPT_MODE);
        final var output = ByteBuffer.allocate(cipher.getOutputSize(input.remaining()));
        try {
            final var bytes = cipher.doFinal(input, output);
            assert bytes <= output.capacity();
        } catch (final Exception e) {
            throw new RuntimeException("failed to decrypt", e);
        }
        return output;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private Cipher cipher;

    private Key key;

    private AlgorithmParameterSpec params;
}
