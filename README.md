# marvel

El propósito de este proyecto es proporcionar un servicio web mediante el cual se cumplan las siguientes consideraciones:
* Consumir la API que Marvel proporciona a desarrolladores.
* Estructurar la información recibida de la API para poder entregar la información que el cliente requiere:
	* Colaboradores de los comics de los personajes: Iron Man y Captain America
	* Personajes que mediante los comics han interactuado con: Iron Man y Captain America.

A continuación se describe cada recurso que permite llevar a cabo las tareas anteriores:

Recurso: Feedmarvel
URI/Path: http://localhost:8080/marvel/webresources/feedmarvel/now
Verbo: GET
Parámetro: now (String)
Descripción: El cliente realiza esta petición cada que requiere actualizar la información de la API de Marvel. Para conseguirlo se llevo a cabo el registro del servidor test.albo.mx y adicional se ocuparon las claves públicas y privadas para hacer la invocación. A fin de ahorrar tiempo, se utilizó una librería externa que permite consultar la API (com.arnaudpiroelle.marvel-api). La lógica del módulo es como sigue:
1. Limpieza de las tablas a hacer actualizadas y estampa de tiempo de inicio de actualización.
2. Carga inicial de los 2 personajes de los que se requiere información.
3. Definición de la instancia de la librería de conexión al API de Marvel ocupando llaves proporcionadas y modificando el tiempo de ejecución para evitar problemas de timeout en la invocación.
4. No se permite recibir más de 100 resultados por invocación a la API de Marvel, por lo que se tiene que trabajar el parámetro offset para mover el índice de los resultados.
5. Se decide tomar como base la invocación de los comics relacionados a los 2 personajes requeridos, ya que con este mismo resultado podemos obtener los caracteres y creadores involucrados.
6. Se arman las consultas que servirán para insertar catálogos (comics, caracteres, creadores) y relaciones entre ellos.
7. Al terminar de recorrer todos los resultados de la invocación del punto 5, se realiza la inserción masiva de los valores (para evitar una inserción individual por registro que consume tiempo y recurso).

            // build connection
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
                System.out.println("Valor de co: " + co);
                if (co >= totalComics) break;
                if (invocacion) {
                    System.out.println("Imprimo resultados...");
                    List<com.arnaudpiroelle.marvel.api.objects.Comic> listComicsFinal = listComics.getData().getResults();
                    for (int i = 0; i < listComicsFinal.size(); i++) {
                        int comicId = listComicsFinal.get(i).getId();
                        idCo++;
                        String comicName = listComicsFinal.get(i).getTitle();
                        queryICo += "(" + idCo + ", '" + String.valueOf(comicId) + "', '" + comicName.replace("'","\\'") + "'),";
                        
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

Recurso: Characters
URI/Path: http://localhost:8080/marvel/webresources/characters/{param}
Verbo: GET
Parámetro: ch (String). Nombre del personaje del que se buscarán los caracteres involucrados.
Descripción: El cliente realiza esta petición para obtener de acuerdo a la estructura solicitada, los personajes involucrados con el personaje definido en el parámetro a través de los diferentes comics donde este último personaje participa. En una única consulta a la BD de sincronización local, se obtiene el resultado esperado con la estructura JSON deseada.
Respuesta: JSON

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

Ejemplos:
Invocación: http://localhost:8080/marvel/webresources/characters/ironman
Respuesta: ironmanCharacteres.json
Invocación: http://localhost:8080/marvel/webresources/characters/capamerica
Respuesta: capamericaCharacteres.json

Recurso: Colaborators
URI/Path: http://localhost:8080/marvel/webresources/colaborators/{param}
Verbo: GET
Parámetro: ch (String). Nombre del personaje del que se buscarán los colaboradores involucrados.
Descripción: El cliente realiza esta petición para obtener de acuerdo a la estructura solicitada, los creadores del personaje definido en el parámetro a través de los diferentes comics donde este último personaje participa. Cabe mencionar, que se consideran para esta prueba aquellos con un rol de editor, escritor, colorista; los demás roles no se localizan en la BD. En una única consulta a la BD de sincronización local, se obtiene el resultado esperado con la estructura JSON deseada.
Respuesta: JSON
Ejemplos:
Invocación: http://localhost:8080/marvel/webresources/colaborators/ironman
Respuesta: ironmanColaborators.json
Invocación: http://localhost:8080/marvel/webresources/colaborators/capamerica
Respuesta: capamericaColaborators.json

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

En la raiz de este repositorio se localizan 2 scripts sql de base de datos.
Se requiere cargar localmente la estructura básica (sin información de sincronización) mediante el script marvel_sync.sql.

    mysql -u root -p < marvel_sync.sql
    
Se coloca el segundo script a fin de ejemplo del llenado de la base de datos una vez que se invocó el API de Marvel (marvel_sync_synchronized.sql)
