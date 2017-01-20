package ru.glukhov.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/contacts")
public class ContactsService {

    @GET
    @Path("/{param}")
    public Response getContacts(@PathParam("param") String msg) {

        String output = "Contacts outPut : " + msg;

        return Response.status(200).entity(output).build();

    }

 }