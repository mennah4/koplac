package koplac.vyskovnice.tools;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetAddress;

/**
 * Library with all functions necessary for work with ftp server
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class FTPConnection {
    public static final String AVATAR_IMAGES_DIRECTORY="/profileimages";

    private final String user;
    private final String password;
    private final String serverAddress;

    private FTPClient connection;

    public FTPConnection(String user, String password, String serverAddress){
        this.user=user;
        this.password=password;
        this.serverAddress=serverAddress;

        this.connection=null;
    }

    /**
     * Creates a new connection
     * @return boolean result with true or false
     */
    public boolean createConnection(){
        boolean result;
        try{
            if(this.connection!=null){
                this.closeConnection();
            }
            this.connection=new FTPClient();
            this.connection.connect(InetAddress.getByName(this.serverAddress));
            if(FTPReply.isPositiveCompletion(connection.getReplyCode())){
                result=this.connection.login(this.user,this.password);
                if(result){
                    this.connection.setFileType(FTP.BINARY_FILE_TYPE);
                    this.connection.enterLocalPassiveMode();
                    return true;
                }
                return false;
            }
            return false;
        }
        catch(Exception exception){
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * Closes the current connection
     */
    public void closeConnection(){
        try{
            if(this.connection!=null) {
                this.connection.logout();
                this.connection.disconnect();
                this.connection=null;
            }
        }
        catch(Exception exception){
            exception.printStackTrace();
        }
    }

    /**
     * Change to current directory at ftp server
     * @param directory Current directory
     * @return boolean result with true or false
     */
    public boolean changeDirectory(String directory){
        boolean result;

        result=false;
        if(this.connection!=null) {
            try {
                result = this.connection.changeWorkingDirectory(directory);
            } catch (Exception exception) {
                exception.printStackTrace();
                result = false;
            }
        }

        return result;
    }

    /**
     * Send a bytes array inside of a data stream to ftp server
     * @param archivo Current bytes array
     * @param ruta Current path at the ftp server
     * @param nombreArchivoNuevo New name for new file at ftp server
     * @return boolean result with true or false
     */
    public boolean sendStream(byte[] archivo, String ruta, String nombreArchivoNuevo) {
        boolean result;
        ByteArrayInputStream bais;

        result = false;
        try {
            bais = new ByteArrayInputStream(archivo);
            if (this.changeDirectory(ruta)) {
                result = this.connection.storeFile(nombreArchivoNuevo,bais);
            }
            bais.close();
            this.closeConnection();
        } catch (Exception excepcion) {
            excepcion.printStackTrace();
            result = false;
        }

        return result;
    }

    /**
     * Retrieves a data stream and convert to bytes array from the ftp server
     * @param serverDirectory Path directory at ftp server
     * @param serverFileName Filename at the ftp server
     * @return Bytes array with data stream file
     */
    public byte[] receiveStream(String serverDirectory,String serverFileName){
        InputStream is;
        byte[] aux;
        ByteArrayOutputStream baos;

        is=null;
        aux=null;
        try{
            this.createConnection();
            if (this.changeDirectory(serverDirectory)) {
                baos=new ByteArrayOutputStream();
                is=connection.retrieveFileStream(serverFileName);
                IOUtils.copy(is,baos);
                aux=baos.toByteArray();
                connection.completePendingCommand();
                is.close();
                is=null;
            }
            this.closeConnection();
            return aux;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * Shortcut function for upload a bytes array to ftp server
     * @param avatar Current bytes array
     * @param path Path for store at ftp server
     * @param newFileName New filename for current bytes array
     * @return boolean result with tru or false
     */
    public boolean uploadAvatar(byte[] avatar,String path,String newFileName){
        boolean result;

        this.createConnection();
        result=this.sendStream(avatar,path,newFileName);
        this.closeConnection();

        return result;
    }

}
