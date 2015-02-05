/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.faraox.rest.poc.resource;

import com.faraox.rest.poc.bean.Person;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 *
 * @author sshami
 */
@Singleton
@Path("/persons")
public class PersonResource {

    private final Map<Long, Person> personMap;

    public PersonResource() {
        System.out.println("Invoking person resource constructor...");
        personMap = new HashMap<>();
        personMap.put(1L, new Person(1L, "Sumved", "Shami"));
        personMap.put(2L, new Person(2L, "Dinesh", "Damodharan"));
        personMap.put(3L, new Person(3L, "Sumit", "Ranjan"));
    }
    
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadPerson(@FormDataParam("file") InputStream fileInputStream, 
            @FormDataParam("file") FormDataContentDisposition contentDisposition) 
            throws FileNotFoundException, IOException {
        
        String fileName = contentDisposition.getFileName();
        System.out.println("File name: " + fileName);
        System.out.println("Content disposition parameters: " + 
                contentDisposition.getParameters());
        System.out.println("Content disposition type: " + contentDisposition.getType());
        System.out.println("Content disposition name: " + contentDisposition.getName());
        
        String filePath = "C://Users//sumved.shami//Desktop//" + fileName;
        try (OutputStream outputStream = new FileOutputStream(filePath)) {
            int read = 0;
            final byte[] bytes = new byte[1024];
            while ((read = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
        
        return Response.status(Response.Status.OK).entity("File upload success").build();
    }

    @GET
    public Response getPersons(@Context UriInfo uriInfo) {

        System.out.println("Query parameters: " + uriInfo.getQueryParameters().size());
        return Response.status(200).entity(personMap.values()).build();
    }

    @GET
    @Path("/{personId}")
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getPerson(@PathParam("personId") Long personId) {

        return Response.created(URI.create("/persons/"
                + personId)).entity(personMap.get(personId)).
                status(Response.Status.OK).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response createPerson(@Context HttpHeaders headers, Person person) {

        System.out.println("Headers: " + headers.getRequestHeaders());
        Long personId = person.getPersonId();
        if (!personMap.containsKey(personId)) {
            personMap.put(personId, person);
        } else {
            // throw some exception
        }

        return Response.created(URI.create("/person/"
                + personId)).entity(person).
                status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{personId}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updatePerson(@PathParam("personId") Long personId, Person person) {

        Person personFromMap = personMap.get(personId);
        personFromMap.setFirstName(person.getFirstName());
        personFromMap.setLastName(person.getLastName());

        return Response.created(null).entity(personFromMap).
                status(Response.Status.OK).build();
    }

    @DELETE
    @Path("/{personId}")
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deletePerson(@PathParam("personId") Long personId) {

        Person person = new Person();
        if (personMap.containsKey(personId)) {
            personMap.remove(personId);
            person.setFlag("deleted");
        } else {
            person.setFlag("not_found");
        }

        return Response.created(null).entity(person).
                status(Response.Status.OK).build();
    }
}
