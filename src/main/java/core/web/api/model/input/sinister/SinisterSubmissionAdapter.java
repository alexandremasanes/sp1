package core.web.model.databinding.command.sinister;


import com.google.gson.*;

import java.lang.reflect.Type;

public class SinisterSubmissionAdapter implements JsonDeserializer<SinisterSubmission> {

    private final static int ACCIDENT_SUBMISSION_FIELDS_COUNT;

    private final static int PLAIN_SINISTER_SUBMISSION_FIELDS_COUNT;

    static {
        ACCIDENT_SUBMISSION_FIELDS_COUNT       = AccidentSubmission.class.getFields().length;
        PLAIN_SINISTER_SUBMISSION_FIELDS_COUNT = PlainSinisterSubmission.class.getFields().length;
    }

    @Override
    public SinisterSubmission deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        if(json.getAsJsonArray().size() == ACCIDENT_SUBMISSION_FIELDS_COUNT)
            return context.deserialize(json, AccidentSubmission.class);
        else
            return context.deserialize(json, PlainSinisterSubmission.class);

    }
}