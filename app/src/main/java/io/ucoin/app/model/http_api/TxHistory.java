package io.ucoin.app.model.http_api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.nio.charset.Charset;

import io.ucoin.app.enums.SourceType;


public class TxHistory implements Serializable {
    public String currency;
    public String pubkey;
    public History history;



    public static TxHistory fromJson(InputStream json) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Input.class, new InputAdapter())
                .registerTypeAdapter(Output.class, new OutputAdapter())
                .create();

        Reader reader = new InputStreamReader(json, Charset.forName("UTF-8"));
        return gson.fromJson(reader, TxHistory.class);
    }

    public class History implements Serializable {
        public Tx[] sent;
        public Tx[] received;
    }

    public class Tx implements Serializable {
        public String[] issuers;
        public Input[] inputs;
        public Output[] outputs;
        public String comment;
        public String[] signatures;
        public String hash;
        public Long block_number;
        public Long time;
    }

    public static class Input implements Serializable {
        public Integer index;
        public SourceType type;
        public Long number;
        public String fingerprint;
        public Long amount;
    }

    public static class Output implements Serializable {
        public String publickKey;
        public Long amount;
    }

    public static class InputAdapter extends TypeAdapter<Input> {

        @Override
        public Input read(JsonReader reader) throws IOException {
            if (reader.peek() == com.google.gson.stream.JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            String ipt = reader.nextString();
            String[] parts = ipt.split(":");
            Input input = new Input();
            input.index = Integer.parseInt(parts[0]);
            input.type = SourceType.valueOf(parts[1]);
            input.number = Long.parseLong(parts[2]);
            input.fingerprint = parts[3];
            input.amount = Long.parseLong(parts[4]);

            return input;
        }

        public void write(JsonWriter writer, Input input) throws IOException {
            if (input == null) {
                writer.nullValue();
                return;
            }
            writer.value(input.index + ":" +
                    input.type.name() + ":" +
                    input.number + ":" +
                    input.fingerprint + ":" +
                    input.amount);
        }
    }

    public static class OutputAdapter extends TypeAdapter<Output> {

        @Override
        public Output read(JsonReader reader) throws IOException {
            if (reader.peek() == com.google.gson.stream.JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            String upt = reader.nextString();
            String[] parts = upt.split(":");
            Output output = new Output();
            output.publickKey = parts[0];
            output.amount = Long.parseLong(parts[1]);

            return output;
        }

        public void write(JsonWriter writer, Output output) throws IOException {
            if (output == null) {
                writer.nullValue();
                return;
            }
            writer.value(output.publickKey + ":" +
                    output.amount);
        }
    }
}
