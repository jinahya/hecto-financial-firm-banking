package io.github.jinahya.hectofinancial.firmbanking.fulltext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

final class FullTextSectionUtils {

    private static List<FullTextSegment> loadSegments(final String name) {
        Objects.requireNonNull(name, "name is null");
        final var segments = new ArrayList<FullTextSegment>();
        try (final var resource = FullTextSectionUtils.class.getResourceAsStream(name)) {
            Objects.requireNonNull(resource, "no resource loaded for '" + name + "'");
            try (var s = new Scanner(resource, StandardCharsets.UTF_8)) {
                while (s.hasNext()) {
                    final var type = s.next();
                    final var length = s.nextInt();
                    final var tag = s.nextLine();
                    if ("X".equals(type)) {
                        final var segment = FullTextSegment.newInstanceOfX(length);
                        segment.setTag(tag);
                        segments.add(segment);
                    } else {
                        final var segment = FullTextSegment.newInstanceOf9(length);
                        segment.setTag(tag);
                        segments.add(segment);
                    }
                }
            }
        } catch (final IOException ioe) {
            throw new RuntimeException("failed to load resource for '" + name + "'", ioe);
        }
        return segments;
    }

    static String getHeadSegmentsResourceName(final FullTextCategory category) {
        return category.name() + ".head.segments";
    }

    static List<FullTextSegment> loadHeadSegments(final FullTextCategory category) {
        Objects.requireNonNull(category, "category is null");
        final var name = getHeadSegmentsResourceName(category);
        return loadSegments(name);
    }

    static String getBodySegmentsResourceName(final FullTextCategory category, final String textCode,
                                              final String taskCode) {
        return category.name() + textCode + "_" + taskCode + ".body.segments";
    }

    static List<FullTextSegment> loadBodySegments(final FullTextCategory category, final String textCode,
                                                  final String taskCode) {
        Objects.requireNonNull(category, "category is null");
        final var name = getBodySegmentsResourceName(category, textCode, taskCode);
        return loadSegments(name);
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private FullTextSectionUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
