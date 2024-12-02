package io.github.jinahya.hectofinancial.firmbanking.fulltext;

final class _Length {

    static _Length of(final int value) {
        return new _Length(value);
    }

    private _Length(final int value) {
        super();
        if (value <= 0) {
            throw new IllegalArgumentException("value(" + value + ") is not positive");
        }
        this.value = value;
    }

    final int value;
}
