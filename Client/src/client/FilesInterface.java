
package client;

import DataMessaging.Login;
import DataMessaging.DataAddress;
import java.io.File;

/**
 *
 * @author Daniel Moreira
 */

public interface FilesInterface {

    public boolean Login(Login login, DataAddress serverToSend);
    public boolean Logout(Login login);     //Ainda falta discutir se podem haver 2 usernames iguais
    public boolean CreateAccount(Login login, File rootDirectory);
    public boolean GetFilesInDirectory(File directory);
}
