package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.util.Objects;

final class _Range {

    static _Range of(final _Offset offset, final _Length length) {
        return new _Range(offset, length);
    }

    static _Range from(final int offset, final int length) {
        return of(_Offset.of(offset), _Length.of(length));
    }

    // -----------------------------------------------------------------------------------------------------------------
    private _Range(final _Offset offset, final _Length length) {
        super();
        this.offset = Objects.requireNonNull(offset, "offset is null");
        this.length = Objects.requireNonNull(length, "length is null");
    }

    // ---------------------------------------------------------------------------------------------------------- offset
    int offset() {
        return offset.value;
    }

    // ---------------------------------------------------------------------------------------------------------- length
    int length() {
        return length.value;
    }

    // -----------------------------------------------------------------------------------------------------------------
    final _Offset offset;

    final _Length length;
}
