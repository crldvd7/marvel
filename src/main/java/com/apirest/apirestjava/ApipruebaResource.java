/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apirest.apirestjava;

import com.api.bean.Json;
import com.api.bean.Persona;
import com.api.bean.Telefono;
import com.google.gson.Gson;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author Carlos David Zepeda
 */
@Path("apiprueba")
public class ApipruebaResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ApipruebaResource
     */
    public ApipruebaResource() {
    }

    /**
     * Retrieves representation of an instance of com.apirest.apirestjava.ApipruebaResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String consultaPersona(@QueryParam("cedula") String cedula,
            @QueryParam("nombre") String nombre,
            @QueryParam("telefono") String telefono,
            @QueryParam("operador") String operador) {
        
        Persona persona = new Persona();
        Telefono tel = new Telefono();
        
        tel.setNumero(telefono);
        tel.setOperador(operador);
        
        persona.setCedula(cedula);
        persona.setNombre(nombre);
        
        persona.setTelefono(tel);
        
        Json obj = new Json();
        obj.setPersona(persona);
        
        Gson gson = new Gson();
        String jsonString = gson.toJson(obj);
        
        return jsonString;
        //TODO return proper representation object
//        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of ApipruebaResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
}
