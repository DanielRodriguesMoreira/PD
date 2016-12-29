
package client;

import DataMessaging.Login;
import DataMessaging.DataAddress;
import Exceptions.ClientNotLoggedInException;
import Exceptions.CreateAccountException;
import Exceptions.MakeDirException;
import Exceptions.ServerConnectionException;
import Exceptions.UsernameOrPasswordIncorrectException;
import java.io.File;
import java.util.ArrayList;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public interface FilesInterface {

    public void Login(Login login, DataAddress serverToSend) 
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException;
    public void Logout(Login login, DataAddress serverToSend) 
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException;
    public void CreateAccount(Login login, DataAddress serverToSend) 
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException;
    public ArrayList<File> GetWorkingDirContent(DataAddress serverToSend) 
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException;
    public ArrayList<File> ChangeDirectory(String serverName, String newPath)
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException;
    public String GetWorkingDirPath(String serverName) 
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException;
    public ArrayList<File> MakeDir(String serverName, String newDirName)
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException;
    public boolean GetFilesInDirectory(File directory);
}
