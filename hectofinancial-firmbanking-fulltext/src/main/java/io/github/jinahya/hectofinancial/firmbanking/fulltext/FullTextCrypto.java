package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import javax.crypto.Cipher;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Objects;

/**
 * A crypto for encrypting/decrypting instances of {@link FullText}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see FullText#setCrypto(FullTextCrypto)
 */
public class FullTextCrypto {

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    /**
     * Creates new instance with specified arguments.
     *
     * @param cipher a cipher.
     * @param key    a key.
     * @param params a params; may be {@code null}.
     * @return a new instance.
     */
    public static FullTextCrypto newInstance(final Cipher cipher, final Key key, final AlgorithmParameterSpec params) {
        return new FullTextCrypto(cipher, key, params);
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private FullTextCrypto(final Cipher cipher, final Key key, final AlgorithmParameterSpec params) {
        super();
        this.cipher = Objects.requireNonNull(cipher, "cipher is null");
        this.key = Objects.requireNonNull(key, "key is null");
        this.params = params;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private void init(final int opmode) {
        if (params != null) {
            try {
                cipher.init(opmode, key, params);
                return;
            } catch (final Exception e) {
                throw new RuntimeException("failed to initialize the cipher", e);
            }
        }
        try {
            cipher.init(opmode, key);
        } catch (final Exception e) {
            throw new RuntimeException("failed to initialize the cipher", e);
        }
    }

    /**
     * Encrypts specified buffer's remaining bytes, and returns a byte buffer of encrypted bytes.
     *
     * @param input the buffer whose remaining bytes are encrypted.
     * @return a byte buffer of encrypted bytes.
     * @apiNote the result buffer will have no remaining, which means the caller should invoke {@link ByteBuffer#flip()}
     * ont the result.
     */
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

    /**
     * Decrypts specified buffer's remaining bytes, and returns a byte buffer of decrypted bytes.
     *
     * @param input the buffer whose remaining bytes are decrypted.
     * @return a byte buffer of decrypted bytes.
     * @apiNote the result buffer will have no remaining, which means the caller should invoke {@link ByteBuffer#flip()}
     * ont the result.
     */
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
    private final Cipher cipher;

    private final Key key;

    private final AlgorithmParameterSpec params;
}
