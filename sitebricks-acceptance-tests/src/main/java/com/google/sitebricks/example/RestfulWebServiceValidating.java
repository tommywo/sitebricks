package com.google.sitebricks.example;

import java.util.List;

import com.google.sitebricks.client.transport.Json;
import com.google.sitebricks.example.model.Person;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.As;
import com.google.sitebricks.http.Get;
import com.google.sitebricks.http.Post;

@Service
public class RestfulWebServiceValidating {

    @Get
    @As(Json.class)
    Reply<Person> newPerson() {
      return Reply.with(new Person());
    }

    @Post
    @As(Json.class)
    Reply<?> postPerson(@As(Json.class) Person person, Request request) {
      List<String> errors = request.validate(person);
      if (!errors.isEmpty()) {
        return Reply.with(errors).badRequest();
      }
      return Reply.with(person);
    }

}
