package io.github.jinahya.hectofinancial.firmbanking.fulltext;

final class _Offset {

    static _Offset of(final int value) {
        return new _Offset(value);
    }

    private _Offset(final int value) {
        super();
        if (value < 0) {
            throw new IllegalArgumentException("value(" + value + ") is negative");
        }
        this.value = value;
    }

    final int value;
}
