package org.ja.vertx.client.model;

/**
 *
 * @author Andre Jacobs
 * Created by andre on 25.08.16.
 */
public class Repository {

    public Person getPerson() {
        return createPerson();
    }

    private Person createPerson() {
        Person person = new Person();
        person.setName("Josh");
        person.setLastName("Meyer");
        return person;
    }
}
