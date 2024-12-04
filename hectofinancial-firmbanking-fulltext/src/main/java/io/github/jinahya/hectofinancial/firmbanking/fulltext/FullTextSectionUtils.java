package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

final class FullTextSectionUtils {

    private static List<FullTextSegment> loadSegments(final String name) {
        assert name != null && !name.isBlank();
        final var segments = new ArrayList<FullTextSegment>();
        try (final var resource = FullTextSectionUtils.class.getResourceAsStream(name)) {
            if (resource == null) {
                throw new RuntimeException("no resource loaded for '" + name + "'");
            }
            try (var s = new Scanner(resource, StandardCharsets.UTF_8)) {
                for (int offset = 0; s.hasNext(); ) {
                    final var type = s.next();
                    final var length = s.nextInt();
                    final var tag = s.nextLine();
                    final FullTextSegment segment;
                    if ("X".equals(type)) {
                        segment = FullTextSegment.newInstanceOfX(offset, length, tag);
                    } else {
                        segment = FullTextSegment.newInstanceOf9(offset, length, tag);
                    }
                    segments.add(segment);
                    offset += segment.getLength();
                }
            }
        } catch (final IOException ioe) {
            throw new RuntimeException("failed to load resource for '" + name + "'", ioe);
        }
        return segments;
    }

    // ------------------------------------------------------------------------------------------------------------ head
    static String getResourceNameForHeadSegments(final FullTextCategory category) {
        return category.name() + ".head.segments";
    }

    static List<FullTextSegment> loadHeadSegments(final FullTextCategory category) {
        Objects.requireNonNull(category, "category is null");
        final var name = getResourceNameForHeadSegments(category);
        return loadSegments(name);
    }

    // ------------------------------------------------------------------------------------------------------------ body
    static String getResourceNameForBodySegments(final FullTextCategory category, final String textCode,
                                                 final String taskCode) {
        return category.name() + textCode + "_" + taskCode + ".body.segments";
    }

    static List<FullTextSegment> loadBodySegments(final FullTextCategory category, final String textCode,
                                                  final String taskCode) {
        Objects.requireNonNull(category, "category is null");
        final var name = getResourceNameForBodySegments(category, textCode, taskCode);
        return loadSegments(name);
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private FullTextSectionUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
