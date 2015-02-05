/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.faraox.rest.poc.exception.mapper;

import com.faraox.rest.poc.exception.PersonNotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author sumved.shami
 */
@Provider
public class PersonNotFoundExceptionMapper implements
        ExceptionMapper<PersonNotFoundException> {

    @Override
    public Response toResponse(PersonNotFoundException ex) {
        String entity = "{\"message\": " + ex.getMessage() + "}";
        System.out.println("Entity message: " + entity);
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON).entity(entity).build();
    }
}
