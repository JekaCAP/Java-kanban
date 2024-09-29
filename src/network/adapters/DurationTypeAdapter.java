package network.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationTypeAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter out, Duration value) throws IOException {
        if (value != null) {
            out.value(value.toSeconds());
        }
    }

    @Override
    public Duration read(JsonReader in) throws IOException {
        long seconds = in.nextLong();
        return Duration.ofSeconds(seconds);
    }
}