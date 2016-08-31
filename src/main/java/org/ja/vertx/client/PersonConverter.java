package org.ja.vertx.client;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.ja.vertx.client.model.Person;

/**
 * Converter for Json
 *
 * Created by andre on 25.08.16.
 */
public class PersonConverter {

    /**
     * convert an pojo to an json object
     *
     * @param person
     */
    private static JsonObject convertPojoToJson(Person person) {
        String jsonString = Json.encode(person);
        JsonObject json = new JsonObject(jsonString);
        return json;
    }

    /**
     * convert a json string to an pojo
     *
     * @param result
     */
    private Person convertJsonToPojo(String result) {
        Person person = Json.decodeValue(result , Person.class);
        return person;

    }

}
