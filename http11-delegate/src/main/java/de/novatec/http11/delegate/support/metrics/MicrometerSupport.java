package de.novatec.http11.delegate.support.metrics;

import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.Tag;

import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public final class MicrometerSupport {

    public static Set<Tag> toMicrometerTags(Map<String, String> tags) {
        return tags.entrySet().stream()
                .map(kv -> new ImmutableTag(kv.getKey(), kv.getValue()))
                .collect(toSet());
    }

    private MicrometerSupport() {
    }
}
