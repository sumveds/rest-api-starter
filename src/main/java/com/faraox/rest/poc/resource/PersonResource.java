/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.faraox.rest.poc.resource;

import com.faraox.rest.poc.bean.Person;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.FormDataParam;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.spi.resource.Singleton;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author sshami
 */
@Singleton
@Path("/person")
public class PersonResource {

    private Map<Long, Person> personMap;

    public PersonResource() {

        personMap = new HashMap<Long, Person>();
        personMap.put(1L, new Person(1L, "Sumved", "Shami"));
        personMap.put(2L, new Person(2L, "Dinesh", "Damodharan"));
        personMap.put(3L, new Person(3L, "Sumit", "Ranjan"));
    }

    /**
     * Upload a File
     */
//    @POST
//    @Path("upload")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @Produces(MediaType.TEXT_PLAIN)
//    public Response uploadFile(@FormDataParam("file") InputStream fileInputStream,
//            @FormDataParam("file") FormDataContentDisposition fileDisposition) {
//        
//        System.out.println("File name: " + fileDisposition.getFileName());
//        return Response.status(200).entity("Success").build();
//    }
    
    @POST
    @Path("upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadFile(FormDataMultiPart multiPart) {
        
        List<BodyPart> bodyParts = multiPart.getBodyParts();
        System.out.println("First part headers: " + bodyParts.get(0).getHeaders());
        System.out.println("File type: " + bodyParts.get(0).getMediaType().getType());
        System.out.println("File name: " + bodyParts.get(0).getContentDisposition().getFileName());
        System.out.println("Content disposition type: " + bodyParts.get(0).getContentDisposition().getType());
        
        return Response.status(200).entity("Success").build();
    }

    @GET
//    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
//    public Response getPersons(@QueryParam("firstname") List<String> firstName) {
    public Response getPersons(@Context UriInfo uriInfo) {

        System.out.println("Query parameters: " + uriInfo.getQueryParameters());
        return Response.status(200).entity(personMap.get(1L)).build();
    }

    @GET
    @Path("/{personId}")
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getPerson(@PathParam("personId") Long personId) {

        return Response.created(URI.create("http://localhost:8084/rest/person/"
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

        return Response.created(URI.create("http://localhost:8084/rest/person/"
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
