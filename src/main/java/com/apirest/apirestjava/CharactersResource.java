/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apirest.apirestjava;

import com.api.bean.CharacterRel;
import com.api.bean.Character;
import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.PathParam;

/**
 * REST Web Service
 *
 * @author Carlos David Zepeda
 */
@Path("characters")
public class CharactersResource {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/marvel_sync?zeroDateTimeBehavior=convertToNull";

    static final String USER = "marvel_u";
    static final String PASS = "PupJaX9vvnRLp5xe";
    
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of CharactersResource
     */
    public CharactersResource() {
    }

    /**
     * Retrieves representation of an instance of com.apirest.apirestjava.CharactersResource
     * @return an instance of java.lang.String
     */
    @GET
    @Path("/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCharacters(@PathParam("param") String ch) throws SQLException {
        // build connection
        Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
        
        // create statement
        Statement statement = connection.createStatement();

        // select query
        String characteresQuery = "SELECT DATE_FORMAT(sync_datetime, '%d/%m/%Y %H:%i:%s') as sync_datetime, "
                + "c2.name_api as ch, GROUP_CONCAT(DISTINCT (co2.name_api)) as comics "
                + "FROM `comic` co INNER JOIN `rel_comic_character` r_co_c ON(co.id_comic = r_co_c.id_comic) " 
                + "INNER JOIN `character` c ON(r_co_c.id_character = c.id_character) "
                + "INNER JOIN `rel_comic_character` r_co_c_2 ON(r_co_c.id_comic = r_co_c_2.id_comic) "
                + "INNER JOIN `character` c2 ON(r_co_c_2.id_character = c2.id_character) "
                + "INNER JOIN `comic` co2 ON(r_co_c_2.id_comic = co2.id_comic), `time_sync` t " 
                + "WHERE c.name = '" + ch + "' AND r_co_c_2.id_character != c.id_character " 
                + "GROUP BY c2.name_api";
        System.out.println("Consulta: " + characteresQuery);
        // execute query
        ResultSet rs = statement.executeQuery(characteresQuery);

        // a list of persons
        List<com.api.bean.Character> characters = new ArrayList();
        String last_sync = "";
        // add persons
        while (rs.next()) {
            last_sync = rs.getString("sync_datetime");
            String characterRel = rs.getString("ch");
            String comics = rs.getString("comics");
            List<String> listComics = new ArrayList();
            System.out.println("chRel: " + characterRel);
            System.out.println("comics: " + comics);
            String[] comics2 = comics.split(",");
            System.out.println("long comics2: " + comics2.length);
            for (int c=0; c < comics2.length; c++) {
                listComics.add(comics2[c]);
            }
            com.api.bean.Character chNuevo = new Character(characterRel, listComics);
            characters.add(chNuevo);
        }
        
        CharacterRel chRelNuevo = new CharacterRel(last_sync, characters);
        
        // new json string with the persons list
        Gson gson = new Gson();
        String jsonString = gson.toJson(chRelNuevo);

        //TODO return proper representation object
        //throw new UnsupportedOperationException();
        return jsonString;
//        return "{met3: " + ch + "}";
    }

    /**
     * PUT method for updating or creating an instance of CharactersResource
     * @param content representation for the resource
     */
//    @PUT
//    @Consumes(MediaType.APPLICATION_JSON)
//    public void putJson(String content) {
//    }
}
