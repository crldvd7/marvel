/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apirest.apirestjava;

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
import com.api.bean.Character;
import com.api.bean.Colaborator;
import javax.ws.rs.PathParam;

/**
 * REST Web Service
 *
 * @author Carlos David Zepeda
 */
@Path("colaborators")
public class ColaboratorsResource {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/marvel_sync?zeroDateTimeBehavior=convertToNull";

    static final String USER = "marvel_u";
    static final String PASS = "PupJaX9vvnRLp5xe";
    
    @Context
    private UriInfo context;
    
    /**
     * Creates a new instance of ColaboratorsResource
     */
    public ColaboratorsResource() {
    }

    /**
     * Retrieves representation of an instance of com.apirest.apirestjava.ColaboratorsResource
     * @return an instance of java.lang.String
     */
//    @GET
//    @Path("xml")
//    @Produces(MediaType.APPLICATION_XML)
//    public String getXml() {
//        //TODO return proper representation object
//        //throw new UnsupportedOperationException();
//        return "<xml><met>1</met></xml>";
//    }
    
    @GET
    @Path("/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getColaborators(@PathParam("param") String ch) throws SQLException {
        // build connection
        Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
        
        // create statement
        Statement statement = connection.createStatement();

        // select query
        String characteresQuery = "SELECT  DATE_FORMAT(sync_datetime, '%d/%m/%Y %H:%i:%s') as sync_datetime, "
                + "r_co_cr.id_role, GROUP_CONCAT( DISTINCT (cr.name_api)) as colabs "
                + "FROM `comic` co INNER JOIN `rel_comic_character` r_co_c USING(id_comic) "
                + "INNER JOIN `character` c USING(id_character) INNER JOIN `rel_comic_creator` r_co_cr USING(id_comic) "
                + "INNER JOIN `creator` cr USING(id_creator), `time_sync` t "
                + "WHERE c.name = '" + ch + "' "
                + "GROUP BY r_co_cr.id_role";

        // execute query
        ResultSet rs = statement.executeQuery(characteresQuery);

        // a list of persons
        List<String> editors = new ArrayList();
        List<String> writers = new ArrayList();
        List<String> colorists = new ArrayList();
        String last_sync = "";
        // add persons
        while (rs.next()) {
            last_sync = rs.getString("sync_datetime");
            int id_role = rs.getInt("id_role");
            String colabs = rs.getString("colabs");
            System.out.println("id_role: " + id_role);
            System.out.println("colabs: " + colabs);
            String[] colabs2 = colabs.split(",");
            for (int c=0; c < colabs2.length; c++) {
                switch(id_role) {
                    case 1: 
                        editors.add(colabs2[c]);
                        break;
                    case 2:
                        writers.add(colabs2[c]);
                        break;
                    case 3: 
                        colorists.add(colabs2[c]);
                        break;
                }
            }
        }
        
        Colaborator col = new Colaborator(last_sync, editors, writers, colorists);
        
        // new json string with the persons list
        Gson gson = new Gson();
        String jsonString = gson.toJson(col);

        //TODO return proper representation object
        //throw new UnsupportedOperationException();
        return jsonString;
    }

    /**
     * PUT method for updating or creating an instance of ColaboratorsResource
     * @param content representation for the resource
     */
//    @PUT
//    @Consumes(MediaType.APPLICATION_XML)
//    public void putXml(String content) {
//    }
}
