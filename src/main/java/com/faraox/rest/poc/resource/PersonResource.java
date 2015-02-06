/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.faraox.rest.poc.resource;

import com.faraox.rest.poc.bean.Person;
import com.faraox.rest.poc.exception.PersonNotFoundException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
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
        personMap.put(1L, new Person(1L, "Lionel", "Messi"));
        personMap.put(2L, new Person(2L, "Christian", "Ronaldo"));
        personMap.put(3L, new Person(3L, "Pete", "Sampras"));
        personMap.put(4L, new Person(4L, "Roger", "Federer"));
        personMap.put(5L, new Person(5L, "Sachin", "Tendulkar"));
        personMap.put(6L, new Person(6L, "George", "Bush"));
        personMap.put(7L, new Person(7L, "Jackie", "Chan"));
        personMap.put(8L, new Person(8L, "Hu", "Jintao"));
        personMap.put(9L, new Person(9L, "Andre", "Agassi"));
        personMap.put(10L, new Person(10L, "Michael", "Schumacher"));
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
        System.out.println("Content disposition parameters: "
                + contentDisposition.getParameters());
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
    @Path("/{personId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getPerson(/*@CookieParam("JSESSIONID") String sessionId,
            @MatrixParam("sport") String sport,
            @Context HttpHeaders headers,*/
            @Context UriInfo uriInfo,
            @PathParam("personId") Long personId)
            throws PersonNotFoundException {

        /*MultivaluedMap<String, String> headersMap = headers.getRequestHeaders();
        System.out.println("*******************Headers*******************");
        for (String key : headersMap.keySet()) {
            System.out.println("[" + key + ": " + headersMap.getFirst(key) + "]");
        }
        System.out.println("*********************************************");
        System.out.println("Cookie session id: " + sessionId);
        System.out.println("*********************************************");
        System.out.println("Favourite sport: " + sport);
        System.out.println("*********************************************");*/
        
        System.out.println("Absolute URI: " + uriInfo.getAbsolutePath().toString());
        System.out.println("Base URI: " + uriInfo.getBaseUri().toString());
        
        Person person = personMap.get(personId);
        if (person != null) {
            return Response.ok(person).build();
        } else {
            throw new PersonNotFoundException("Person with id "
                    + personId + " is unavailable.");
        }
    }
    
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getPersons(@Context UriInfo uriInfo, 
            @QueryParam("page") @DefaultValue("0") Integer page, 
            @QueryParam("rpp") @DefaultValue("2") Integer recordsPerPage) {
        
        int first = (page * recordsPerPage) + 1;
        int last = first + recordsPerPage;
        
        Set<Person> persons = new HashSet<>();
        
        for(int i = first; i < last; i++) {
            persons.add(personMap.get((long) i));
        }
        
        Response.ResponseBuilder builder = Response.ok(persons);
        if(page > 0) {
            URI prev = uriInfo.getAbsolutePathBuilder()
                .queryParam("page", page - 1)
                .queryParam("rpp", recordsPerPage).build();
            builder = builder.link(prev, "prev");
        }
        URI next = uriInfo.getAbsolutePathBuilder()
                .queryParam("page", page + 1)
                .queryParam("rpp", recordsPerPage).build();
        builder = builder.link(next, "next");
        
        return builder.build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response createPerson(@Context UriInfo uriInfo, Person person) {
        
        Long personId = person.getPersonId();
        if (!personMap.containsKey(personId)) {
            personMap.put(personId, person);
        } else {
            // throw some exception
        }
        
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(personId.toString()).build();
        
        return Response.created(location).entity(person)
                .status(Response.Status.CREATED)
                .link(location, "update")
                .link(location, "get")
                .build();
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
        
        if (personMap.containsKey(personId)) {
            personMap.remove(personId);
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
