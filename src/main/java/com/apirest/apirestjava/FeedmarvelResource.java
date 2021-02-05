/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apirest.apirestjava;

import com.arnaudpiroelle.marvel.api.MarvelApi;
import com.arnaudpiroelle.marvel.api.MarvelApi.Builder;
import com.arnaudpiroelle.marvel.api.exceptions.AuthorizationException;
import com.arnaudpiroelle.marvel.api.exceptions.EntityNotFoundException;
import com.arnaudpiroelle.marvel.api.exceptions.QueryException;
import com.arnaudpiroelle.marvel.api.exceptions.RateLimitException;
import com.arnaudpiroelle.marvel.api.objects.CharacterList;
import com.arnaudpiroelle.marvel.api.objects.ref.DataContainer;
import com.arnaudpiroelle.marvel.api.objects.ref.DataWrapper;
import com.arnaudpiroelle.marvel.api.params.name.comic.ListComicParamName;
import com.arnaudpiroelle.marvel.api.services.sync.CharactersService;
import com.arnaudpiroelle.marvel.api.services.sync.ComicsService;
import com.squareup.okhttp.OkHttpClient;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import retrofit.client.OkClient;

/**
 * REST Web Service
 *
 * @author Carlos David Zepeda
 */
@Path("feedmarvel")
public class FeedmarvelResource {

    static final String YOUR_PUBLIC_APIKEY = "97633b0dc65e98fe9641dc3bed56fae1";
    static final String YOUR_PRIVATE_APIKEY = "66abf6d009bac876a7655360c817890e6fb0cd8c";
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/marvel_sync?zeroDateTimeBehavior=convertToNull";

    static final String USER = "marvel_u";
    static final String PASS = "PupJaX9vvnRLp5xe";
    
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of FeedmarvelResource
     */
    public FeedmarvelResource() {
    }

    /**
     * Retrieves representation of an instance of com.apirest.apirestjava.FeedmarvelResource
     * @return an instance of java.lang.String
     */
    @GET
    @Path("/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(@PathParam("param") String ch) throws SQLException {
        try {
            //TODO return proper representation object
            //throw new UnsupportedOperationException();
//            ClientConfig configuration = new ClientConfig();
//            configuration.property(ClientProperties.CONNECT_TIMEOUT, 10000);
//            configuration.property(ClientProperties.READ_TIMEOUT, 10000);
//            Client client = ClientBuilder.newClient(configuration);
//            MarvelApi.configure().withApiKeys(YOUR_PUBLIC_APIKEY, YOUR_PRIVATE_APIKEY).withClient((retrofit.client.Client) client).init();
            
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
        
            // create statement
            Statement statement = connection.createStatement();

            // select query
            String query = "UPDATE `time_sync` SET sync_datetime = CURRENT_TIMESTAMP";
            statement.execute(query);
            query = "DELETE FROM `rel_comic_character`";
            statement.execute(query);
            query = "DELETE FROM `rel_comic_creator`";
            statement.execute(query);
            query = "DELETE FROM `comic`";
            statement.execute(query);
            query = "DELETE FROM `character`";
            statement.execute(query);
            query = "DELETE FROM `creator`";
            statement.execute(query);
            query = "ALTER TABLE `rel_comic_character` AUTO_INCREMENT = 1";
            statement.execute(query);
            query = "ALTER TABLE `rel_comic_creator` AUTO_INCREMENT = 1";
            statement.execute(query);
            query = "ALTER TABLE`comic` AUTO_INCREMENT = 1";
            statement.execute(query);
            query = "ALTER TABLE `character` AUTO_INCREMENT = 1";
            statement.execute(query);
            query = "ALTER TABLE `creator` AUTO_INCREMENT = 1";
            statement.execute(query);

            String queryICh = "INSERT IGNORE INTO `character`(id_character, id_character_api, name, name_api) VALUES "
                    + "(1, '1009368', 'ironman', 'Iron Man'), (2, '1009220', 'capamerica', 'Captain America')";
            statement.execute(queryICh);
            queryICh = "INSERT IGNORE INTO `character`(id_character, name_api) VALUES ";
            String queryICo = "INSERT IGNORE INTO `comic`(id_comic, id_comic_api, name_api) VALUES ";
            String queryICoCh = "INSERT IGNORE INTO `rel_comic_character`(id_comic, id_character) VALUES ";
            String queryICr = "INSERT IGNORE INTO `creator`(id_creator, name_api) VALUES ";
            String queryICoCr = "INSERT IGNORE INTO `rel_comic_creator`(id_comic, id_creator, id_role) VALUES ";
            
            final OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setReadTimeout(120, TimeUnit.SECONDS);
            okHttpClient.setConnectTimeout(120, TimeUnit.SECONDS);
            
            Builder builder = MarvelApi.configure().withApiKeys(YOUR_PUBLIC_APIKEY, YOUR_PRIVATE_APIKEY);
            builder.withClient(new OkClient(okHttpClient));
            builder.init();
            

            //Lista de comics
            ComicsService comicsServicio = MarvelApi.getService(ComicsService.class);
            
            Map<ListComicParamName,String> paramCh = new HashMap<ListComicParamName,String>();
            paramCh.put(ListComicParamName.CHARACTERS, "1009368,1009220");
//            paramCh.put(ListComicParamName.CHARACTERS, "1009368");
            paramCh.put(ListComicParamName.LIMIT, "100");
            paramCh.put(ListComicParamName.OFFSET, "0");
            DataWrapper<com.arnaudpiroelle.marvel.api.objects.Comic> listComics = comicsServicio.listComic(paramCh);
            
            int totalComics = listComics.getData().getTotal();
            System.out.println("totalComics: " + totalComics);
            int limite = listComics.getData().getLimit();
            System.out.println("limite: " + limite);
            
            int offset = 0;
            boolean invocacion = true;
            int intentos = 0;
            int idCh = 2;
            int idCr = 0;
            int idCo = 0;
            Map<String, Integer> chIds = new HashMap<String, Integer>();
            chIds.put("Iron Man", 1);
            chIds.put("Captain America", 2);
            
            Map<String, Integer> crIds = new HashMap<String, Integer>();
            
            for (int co = 0; co < totalComics + limite;) {
            //for (int co = 0; co < limite;) {
                System.out.println("Valor de co: " + co);
                if (co >= totalComics) break;
                if (invocacion) {
                    System.out.println("Imprimo resultados...");
                    List<com.arnaudpiroelle.marvel.api.objects.Comic> listComicsFinal = listComics.getData().getResults();
                    for (int i = 0; i < listComicsFinal.size(); i++) {
                    //for (int i = 0; i < 5; i++) {
                        int comicId = listComicsFinal.get(i).getId();
                        idCo++;
                        String comicName = listComicsFinal.get(i).getTitle();
//                        if (idCo == 401) System.out.println("COMIC 401: " + "(" + idCo + ", '" + String.valueOf(comicId) + "', '" + comicName.replace("'","\\'") + "'),");
                        queryICo += "(" + idCo + ", '" + String.valueOf(comicId) + "', '" + comicName.replace("'","\\'") + "'),";
//                        System.out.println("Comic id: " + comicId + ", name: " + comicName);
                        
                        //inserta characteres involucrados
                        List<com.arnaudpiroelle.marvel.api.objects.CharacterSummary> listCh = listComicsFinal.get(i).getCharacters().getItems();
                        for (int c = 0; c < listCh.size(); c++) {
                            String chName = listCh.get(c).getName();
                            int idChReal = 0;
                            if (chIds.containsKey(chName)) {
                                idChReal = chIds.get(chName);
                            }
                            else {
                                idChReal = ++idCh;
                                chIds.put(chName, idChReal);
                                queryICh += "(" + idChReal + ", '" + chName.replace("'","\\'") + "'),";
                            }
                            queryICoCh += "(" + idCo + ", " + idChReal + "),";
                        }
                        
                        //inserta creators involucrados
                        List<com.arnaudpiroelle.marvel.api.objects.CreatorSummary> listCr = listComicsFinal.get(i).getCreators().getItems();
                        for (int cr = 0; cr < listCr.size(); cr++) {
                            String id_role = "-1";
                            switch(listCr.get(cr).getRole()) {
                                case "editor":
                                    id_role = "1";
                                    break;
                                case "writer":
                                    id_role = "2";
                                    break;
                                case "colorist":
                                    id_role = "3";
                                    break;
                                default:
                                    continue;    
                            }
                            String crName = listCr.get(cr).getName();
                            int idCrReal = 0;
                            if (crIds.containsKey(crName)) {
                                idCrReal = crIds.get(crName);
                            }
                            else {
                                idCrReal = ++idCr;
                                crIds.put(crName, idCrReal);
                                queryICr += "(" + idCrReal + ", '" + crName.replace("'","\\'") + "'),";
                            }
                            queryICoCr += "(" + idCo + ", " + idCrReal + ", " + id_role + "),";
                        }
                    }
                }
                offset += 100;
                try {
                    if (intentos <= 100) {
                        System.out.println("(intentos: " + intentos + ") Offset a consultar: " + offset);
                        paramCh.put(ListComicParamName.OFFSET, String.valueOf(offset));
                        listComics = comicsServicio.listComic(paramCh);
                        invocacion = true;
                        intentos = 0;
                        co = co + limite;
                    }
                }
                catch(AuthorizationException ex) {
                    offset -= 100;
                    invocacion = false;
                    intentos++;
                    System.out.println("(intentos: " + intentos + ", offset: " + offset + ") Error de autorización: " + ex.getMessage());
                }
            }
            
            System.out.println("queryICo = " + queryICo);
            statement.execute(queryICo.substring(0,queryICo.length()-1));
            System.out.println("queryICh = " + queryICh);
            statement.execute(queryICh.substring(0,queryICh.length()-1));
            System.out.println("queryICr = " + queryICr);
            statement.execute(queryICr.substring(0,queryICr.length()-1));
            System.out.println("queryICoCr = " + queryICoCr);
            statement.execute(queryICoCr.substring(0,queryICoCr.length()-1));
            System.out.println("queryICoCh = " + queryICoCh);
            statement.execute(queryICoCh.substring(0,queryICoCh.length()-1));
            
            
            
            System.out.println("Offset final: " + offset);
            
//            DataWrapper<com.arnaudpiroelle.marvel.api.objects.Character> listCh = charactersService.listCharacter();
//            DataContainer<com.arnaudpiroelle.marvel.api.objects.Character> chData = listCh.getData();
//            List<com.arnaudpiroelle.marvel.api.objects.Character> listChF = chData.getResults();
//            for (int l = 0; l < listChF.size(); l++) {
//                com.arnaudpiroelle.marvel.api.objects.Character chMarvel = listChF.get(l);
//                System.out.println(chMarvel.getId() + " " + chMarvel.getName());
//            }
        } catch (AuthorizationException ex) {
            Logger.getLogger(FeedmarvelResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (QueryException ex) {
            Logger.getLogger(FeedmarvelResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RateLimitException ex) {
            Logger.getLogger(FeedmarvelResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EntityNotFoundException ex) {
            Logger.getLogger(FeedmarvelResource.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return "{ready: " + ch + "}";
    }

    /**
     * PUT method for updating or creating an instance of FeedmarvelResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
}
