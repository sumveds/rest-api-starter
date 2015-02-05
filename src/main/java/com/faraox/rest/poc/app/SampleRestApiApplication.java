/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.faraox.rest.poc.app;

import com.faraox.rest.poc.resource.PersonResource;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

/**
 *
 * @author sumved.shami
 */
@ApplicationPath("resources")
public class SampleRestApiApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(PersonResource.class);
        resources.add(MultiPartFeature.class);
        return resources;
    }
}
