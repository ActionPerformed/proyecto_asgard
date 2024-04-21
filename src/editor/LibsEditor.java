package editor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Clase con los métodos auxiliares usados por el editor
 *
 * @author Sergio
 */
public class LibsEditor {
    
    private editor.Casilla[][] mapaCasillas;
    private boolean mapaInterior;
//    private editor.Casilla[][] casilla;
    
    /**
     * Metodo para generar un String en lenguaje XML que recoge el contenido de
     * la interfaz gráfica 
     * 
     * @param idMapa ID del mapa creado
     * @param casilla Matriz de casillas que conforma el mapa
     * @return un String en lenguaje XML
     */
    public static String generarXML(int idMapa, editor.Casilla[][] casilla){
        StringBuffer dataBuffer = new StringBuffer();
        Object[] infoCasilla;
        dataBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        dataBuffer.append("<mapa id=\"").append(idMapa).append("\">\n");
        for (int i = 0; i < casilla.length; i++) {
            for (int j = 0; j < casilla[i].length; j++) {
                
                infoCasilla = sacarInfoCasilla(casilla[i][j]);
                
                dataBuffer.append("\t<casilla i=\"").append(i).append("\" j=\"").append(j).append("\" ");
                if ((casilla[i][j].getObjeto().isTransitable() && casilla[i][j].getFondo().isTransitable())
                       || casilla[i][j].getObjeto().getPuerta() != null ) {
                    dataBuffer.append("transitable = \"true\">\n");
                }else{
                    dataBuffer.append("transitable = \"false\">\n");
                }
                
                dataBuffer.append("\t\t<fondo>").append(casilla[i][j].getFondo().getCod()).append("</fondo>\n");
                
                if (infoCasilla[1]!=null) {
                    dataBuffer.append("\t\t<objeto>").append(casilla[i][j].getObjeto().getCod()).append("</objeto>\n");
                }
                
                if (!casilla[i][j].getObjeto().getDescripcion().equals("")) {
                    dataBuffer.append("\t\t<descripcion>").append(casilla[i][j].getObjeto().getDescripcion()).append("</descripcion>\n");
                }
                
                if (casilla[i][j].getObjeto().getPuerta()!=null) {
                    dataBuffer.append("\t\t<puerta>\n");
                    dataBuffer.append("\t\t\t<mapa_destino>").append(casilla[i][j].getObjeto().getPuerta().getMapaDestino()).append("</mapa_destino>\n");
                    dataBuffer.append("\t\t\t<i_destino>").append(casilla[i][j].getObjeto().getPuerta().getCoordIDestino()).append("</i_destino>\n");
                    dataBuffer.append("\t\t\t<j_destino>").append(casilla[i][j].getObjeto().getPuerta().getCoordJDestino()).append("</j_destino>\n");
                    dataBuffer.append("\t\t</puerta>\n");
                }
                dataBuffer.append("\t</casilla>\n");
            }
        }
        dataBuffer.append("</mapa>");
        return dataBuffer.toString();
    }
    
    /**
     * El método recoge una Casilla y extrae su Fondo y su Objeto
     * 
     * @param casilla una casilla del editor
     * @return un object[] formado por {Fondo, Objeto}
     */
    public static Object[] sacarInfoCasilla(editor.Casilla casilla){
        Object[] infoCasilla = new Object[2];
        infoCasilla[0] = casilla.getFondo().getIcon().toString();
        if (casilla.getObjeto().getIcon()!=null) {
            infoCasilla[1] = casilla.getObjeto().getIcon().toString();
        }
        return infoCasilla;
    }
    
    /**
     * Devuelve una matriz de casillas a partir de un archivo XML
     * 
     * @param file
     * @param casilla
     * @return
     */
    public Casilla[][] cargaMapa(File file, editor.Casilla[][] casilla){
        try {
            setMapaCasillas(casilla);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(file.getAbsolutePath(), new SaxParserAsgard());
            return getMapaCasillas();
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(LibsEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Devuelve una matriz de casillas a partir de una URL que apunte a un XML
     * 
     * @param url
     * @param casilla
     * @return
     */
    public Casilla[][] cargaMapa(URL url, editor.Casilla[][] casilla){
        try {
            setMapaCasillas(casilla);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(url.getPath(), new SaxParserAsgard());
            return getMapaCasillas();
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(LibsEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
     /**
     * Añade una linea de texto al archivo indicado por parámetro, usando BufferedWriter
     * 
     * @param file Fichero donde se escribirá la cadena de texto
     * @param cadena cadena de texto a guardar
     * @throws IOException
     */
    public static void anadirLinea(String cadena, File file) throws IOException{
        BufferedWriter flujoDatos = new BufferedWriter(new FileWriter(file,false));
        flujoDatos.write(cadena);
        flujoDatos.close(); //IMPORTANTE cerrar o esto no funciona
     }
 
     /**
     * Lee el contenido de un fichero que se pasa por parametro, usando BufferedReader
     *
     * @param fichero
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void verFicheroTexto(String fichero) throws FileNotFoundException, IOException{
        File file = new File(fichero); //Creo un File a partir de la string del parametro
        String lineaDatos = ""; //Creo la variable donde guardaré el resultado final
        int datoLeido; //Declaro la variable que guardará el caracter leido en cada momento
        BufferedReader ficheroLectura = new BufferedReader(new FileReader(file));
        datoLeido=ficheroLectura.read(); //Leo un caracter y lo almaceno en datoLeido
        while (datoLeido!=-1){ //El valor -1 indica fin de archivo
            lineaDatos+=(char)datoLeido; //añado el caracter leido al resultado final, casteado de valor ascii a char
            datoLeido=ficheroLectura.read(); //Leo otro caracter
        }
        ficheroLectura.close(); //Cierro el archivo (IMPORTANTE)
        System.out.println(lineaDatos); //Imprimo el String en el que he guardado el contenido del fichero
    }

    /**
     * @return the mapaCasillas
     */
    public editor.Casilla[][] getMapaCasillas() {
        return mapaCasillas;
    }

    /**
     * @param mapaCasillas the mapaCasillas to set
     */
    public void setMapaCasillas(editor.Casilla[][] mapaCasillas) {
        this.mapaCasillas = mapaCasillas;
    }

    public boolean isMapaInterior() {
        return mapaInterior;
    }

    public void setMapaInterior(boolean mapaInterior) {
        this.mapaInterior = mapaInterior;
    }
    
    
//    /**
//     * @return the casilla
//     */
//    public editor.Casilla[][] getCasilla() {
//        return casilla;
//    }
//
//    /**
//     * @param casilla the casilla to set
//     */
//    public void setCasilla(editor.Casilla[][] casilla) {
//        this.casilla = casilla;
//    }
    
    /**
     * Clase que inicia la lectura del XML
     */
    private class SaxParserAsgard extends DefaultHandler {

        private boolean mapa=false;
        private boolean mapa_id=false;
        private boolean casilla=false;
        private boolean casilla_i=false;
        private boolean casilla_j=false;
        private boolean casilla_transitable=false;
        private boolean fondo=false;
        private boolean objeto=false;
        private boolean descripcion=false;
        private boolean puerta=false;
        private boolean mapa_destino=false;
        private boolean i_destino=false;
        private boolean j_destino=false;
        
        private int indice_i;
        private int indice_j;
 
        /**
         * Gestiona las ordenes a seguir cuando entramos en una etiqueta xml de apertura
         * 
         * @param uri
         * @param localName
         * @param qName nombre de la etiqueta xml
         * @param atrbts atributos de la etiqueta xml
         * @throws SAXException 
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes atrbts) throws SAXException {
            switch (qName){
                case "mapa":
                    //Por ahora no guardamos la id del mapa (TO DO)
                    
                    //atributo 1 --> mapa_interior (true/false)
                    if(atrbts.getValue(1)!=null && atrbts.getValue(1).equals("true")){
                        mapaInterior = true;
                    }else{
                        mapaInterior = false;
                    }
                    setMapa(true);
                    break;
                case "casilla":
                    setCasilla(true);
                    //Guardo las coordenadas de la casilla
                    setIndice_i(Integer.parseInt(atrbts.getValue(0)));
                    setIndice_j(Integer.parseInt(atrbts.getValue(1)));
                    //Configuro la transitabilidad
                    if (atrbts.getValue(2).equals("true")) {
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setTransitable(true);
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setTransitable(true);
                    }else{
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setTransitable(false);
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setTransitable(false);
                    }
                    break;
                case "fondo":
                    setFondo(true);
                    break;
                case "objeto":
                    setObjeto(true);
                    break;
                case "puerta":
                    getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setPuerta(new Objeto.Puerta());
                    setPuerta(true);
                    break;
                case "descripcion":
                    setDescripcion(true);
                    break;
                case "mapa_destino":
                    setMapa_destino(true);
                    break;   
                case "i_destino":
                    setI_destino(true);
                    break; 
                case "j_destino":
                    setJ_destino(true);
                    break;     
            }
        }

         /**
         * Gestiona las ordenes a seguir cuando entramos en una etiqueta xml de cierre
         * 
         * @param uri
         * @param localName
         * @param qName nombre de la etiqueta xml
         * @param atrbts
         * @throws SAXException 
         */
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            switch (qName){
                case "mapa":
                    setMapa(false);
                    break;
                case "casilla":
                    setCasilla(false);
                    break;
                case "fondo":
                    setFondo(false);
                    break;
                case "objeto":
                    setObjeto(false);
                    break;
                case "puerta":
                    setPuerta(false);
                    break;
                case "descripcion":
                    setDescripcion(false);
                    break;
                case "mapa_destino":
                    setMapa_destino(false);
                    break;   
                case "i_destino":
                    setI_destino(false);
                    break; 
                case "j_destino":
                    setJ_destino(false);
                    break; 
            }
        }

        /**
         * Configura los valores de cada elemento
         * 
         * @param chars datos
         * @param start indica el principio de los datos
         * @param length indica la longitud de los datos
         * @throws SAXException 
         */
        @Override
        public void characters(char[] chars, int start, int length) throws SAXException {
            String data = "";
            for (int i = start; i < start + length; i++) {
                data+=chars[i];
            }
            if (isFondo()) {
                switch (data){
                    case "hierba":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setIcon(configuracion.Tileset.getInstance().getHIERBA());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setCod(data);
                        break;
                    case "agua":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setIcon(configuracion.Tileset.getInstance().getAGUA());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setCod(data);
                        break;
                    case "tarima":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setIcon(configuracion.Tileset.getInstance().getTARIMA());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setCod(data);
                        break;
                    case "blank":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setIcon(configuracion.Tileset.getInstance().getBLANK());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setCod(data);
                        break;
                    case "tierra":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setIcon(configuracion.Tileset.getInstance().getTIERRA());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setCod(data);
                        break;
                    //ALFOMBRA    
                    case "alfombra_ab":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setIcon(configuracion.Tileset.getInstance().getALFOMBRA_AB());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setCod(data);
                        break;
                    case "alfombra_ar":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setIcon(configuracion.Tileset.getInstance().getALFOMBRA_AR());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setCod(data);
                        break;
                    case "alfombra_de":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setIcon(configuracion.Tileset.getInstance().getALFOMBRA_DE());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setCod(data);
                        break;
                    case "alfombra_iz":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setIcon(configuracion.Tileset.getInstance().getALFOMBRA_IZ());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setCod(data);
                        break;
                    case "alfombra_ab_de":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setIcon(configuracion.Tileset.getInstance().getALFOMBRA_AB_DE());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setCod(data);
                        break;
                    case "alfombra_ab_iz":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setIcon(configuracion.Tileset.getInstance().getALFOMBRA_AB_IZ());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setCod(data);
                        break;
                    case "alfombra_ar_de":
                        //getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setCod(data);
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setIcon(configuracion.Tileset.getInstance().getALFOMBRA_AR_DE());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setCod(data);
                        break;
                    case "alfombra_ar_iz":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setIcon(configuracion.Tileset.getInstance().getALFOMBRA_AR_IZ());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setCod(data);
                        break;
                    case "alfombra_centro":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setIcon(configuracion.Tileset.getInstance().getALFOMBRA_CENTRO());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getFondo().setCod(data);
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "FONDO NO RECONOCIDO ["+getIndice_i()+"]["+getIndice_j()+"]");
                }
            }else if (isObjeto()) {
                switch (data){
                    //NPCs
                    case "npc_hombre":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getNPC_HOMBRE());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "npc_mujer":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getNPC_MUJER());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    //ELEMENTOS DE CORNISA
                    case "borde_tierra_ab":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_TIERRA_AB());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_tierra_ar":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_TIERRA_AR());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_tierra_de":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_TIERRA_DE());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_tierra_iz":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_TIERRA_IZ());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_tierra_ab_de":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_TIERRA_AB_DE());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_tierra_ab_iz":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_TIERRA_AB_IZ());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_tierra_ar_de":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_TIERRA_AR_DE());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_tierra_ar_iz":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_TIERRA_AR_IZ());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "tierra_ab_de_esquina":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_TIERRA_AB_DE_ESQUINA());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "tierra_ab_iz_esquina":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_TIERRA_AB_IZ_ESQUINA());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "tierra_ar_de_esquina":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_TIERRA_AR_DE_ESQUINA());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "tierra_ar_iz_esquina":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_TIERRA_AR_IZ_ESQUINA());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    //BORDE HIERBA    
                    case "borde_agua_ab":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA_AB());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_agua_ar":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA_AR());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_agua_de":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA_DE());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_agua_iz":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA_IZ());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_agua_ab_de":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA_AB_DE());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_agua_ab_iz":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA_AB_IZ());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_agua_ar_de":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA_AR_DE());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_agua_ar_iz":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA_AR_IZ());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_agua_ab_de_esq":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA_AB_DE_ESQUINA());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_agua_ab_iz_esq":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA_AB_IZ_ESQUINA());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_agua_ar_de_esq":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA_AR_DE_ESQUINA());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_agua_ar_iz_esq":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA_AR_IZ_ESQUINA());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    //BORDE HIERBA    
                    case "borde_agua2_ab":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA2_AB());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_agua2_ar":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA2_AR());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_agua2_de":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA2_DE());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_agua2_iz":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA2_IZ());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_agua2_ab_de":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA2_AB_DE());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_agua2_ab_iz":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA2_AB_IZ());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_agua2_ar_de":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA2_AR_DE());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_agua2_ar_iz":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA2_AR_IZ());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_agua2_ab_de_esq":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA2_AB_DE_ESQUINA());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_agua2_ab_iz_esq":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA2_AB_IZ_ESQUINA());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_agua2_ar_de_esq":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA2_AR_DE_ESQUINA());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "borde_agua2_ar_iz_esq":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getBORDE_AGUA2_AR_IZ_ESQUINA());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    //RAMPAS    
                    case "rampa_ar":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getRAMPA_AR());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "rampa_ab":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getRAMPA_AB());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "rampa_de":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getRAMPA_DE());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "rampa_iz":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getRAMPA_IZ());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;    
                    //SILLAS    
                    case "silla_ar":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getSILLA_AR());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "silla_ab":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getSILLA_AB());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "silla_de":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getSILLA_DE());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "silla_iz":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getSILLA_IZ());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;    
                    //OTROS    
                    case "borde_casa":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        for (int k = getIndice_i(); k < getIndice_i()+3; k++) {
                            for (int l = getIndice_j(); l < getIndice_j()+4; l++) {
                                getMapaCasillas()[k][l].getObjeto().setIcon(configuracion.Tileset.getInstance().getCASA()[k-getIndice_i()][l-getIndice_j()]);  //TODO: NullpointerException
                                getMapaCasillas()[k][l].getObjeto().setTransitable(false);
                            }
                        }
                        break;
                    case "resto_casa":
                        break;
                    case "arbol":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getARBOL());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "libreria":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getLIBRERIA());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    case "mesa":
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setIcon(configuracion.Tileset.getInstance().getMESA());
                        getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setCod(data);
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, data+": OBJETO NO RECONOCIDO ["+getIndice_i()+"]["+getIndice_j()+"]");
                }
            }else if (isDescripcion()) {
                getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().setDescripcion(data);
            }else if (isMapa_destino()) {
                getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().getPuerta().setMapaDestino(Integer.parseInt(data));
            }else if (isI_destino()) {
                getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().getPuerta().setCoordIDestino(Integer.parseInt(data));
            }else if (isJ_destino()) {
                getMapaCasillas()[getIndice_i()][getIndice_j()].getObjeto().getPuerta().setCoordJDestino(Integer.parseInt(data));
            }
        }

        /**
         * @return the mapa
         */
        public boolean isMapa() {
            return mapa;
        }

        /**
         * @param mapa the mapa to set
         */
        public void setMapa(boolean mapa) {
            this.mapa = mapa;
        }

        /**
         * @return the mapa_id
         */
        public boolean isMapa_id() {
            return mapa_id;
        }

        /**
         * @param mapa_id the mapa_id to set
         */
        public void setMapa_id(boolean mapa_id) {
            this.mapa_id = mapa_id;
        }

        /**
         * @return the casilla
         */
        public boolean isCasilla() {
            return casilla;
        }

        /**
         * @param casilla the casilla to set
         */
        public void setCasilla(boolean casilla) {
            this.casilla = casilla;
        }

        /**
         * @return the casilla_i
         */
        public boolean isCasilla_i() {
            return casilla_i;
        }

        /**
         * @param casilla_i the casilla_i to set
         */
        public void setCasilla_i(boolean casilla_i) {
            this.casilla_i = casilla_i;
        }

        /**
         * @return the casilla_j
         */
        public boolean isCasilla_j() {
            return casilla_j;
        }

        /**
         * @param casilla_j the casilla_j to set
         */
        public void setCasilla_j(boolean casilla_j) {
            this.casilla_j = casilla_j;
        }

        /**
         * @return the casilla_transitable
         */
        public boolean isCasilla_transitable() {
            return casilla_transitable;
        }

        /**
         * @param casilla_transitable the casilla_transitable to set
         */
        public void setCasilla_transitable(boolean casilla_transitable) {
            this.casilla_transitable = casilla_transitable;
        }

        /**
         * @return the fondo
         */
        public boolean isFondo() {
            return fondo;
        }

        /**
         * @param fondo the fondo to set
         */
        public void setFondo(boolean fondo) {
            this.fondo = fondo;
        }

        /**
         * @return the objeto
         */
        public boolean isObjeto() {
            return objeto;
        }

        /**
         * @param objeto the objeto to set
         */
        public void setObjeto(boolean objeto) {
            this.objeto = objeto;
        }

        /**
         * @return the descripcion
         */
        public boolean isDescripcion() {
            return descripcion;
        }

        /**
         * @param descripcion the descripcion to set
         */
        public void setDescripcion(boolean descripcion) {
            this.descripcion = descripcion;
        }

        /**
         * @return the puerta
         */
        public boolean isPuerta() {
            return puerta;
        }

        /**
         * @param puerta the puerta to set
         */
        public void setPuerta(boolean puerta) {
            this.puerta = puerta;
        }

        /**
         * @return the mapa_destino
         */
        public boolean isMapa_destino() {
            return mapa_destino;
        }

        /**
         * @param mapa_destino the mapa_destino to set
         */
        public void setMapa_destino(boolean mapa_destino) {
            this.mapa_destino = mapa_destino;
        }

        /**
         * @return the i_destino
         */
        public boolean isI_destino() {
            return i_destino;
        }

        /**
         * @param i_destino the i_destino to set
         */
        public void setI_destino(boolean i_destino) {
            this.i_destino = i_destino;
        }

        /**
         * @return the j_destino
         */
        public boolean isJ_destino() {
            return j_destino;
        }

        /**
         * @param j_destino the j_destino to set
         */
        public void setJ_destino(boolean j_destino) {
            this.j_destino = j_destino;
        }

        /**
         * @return the indice_i
         */
        public int getIndice_i() {
            return indice_i;
        }

        /**
         * @param indice_i the indice_i to set
         */
        public void setIndice_i(int indice_i) {
            this.indice_i = indice_i;
        }

        /**
         * @return the indice_j
         */
        public int getIndice_j() {
            return indice_j;
        }

        /**
         * @param indice_j the indice_j to set
         */
        public void setIndice_j(int indice_j) {
            this.indice_j = indice_j;
        }
    }
}