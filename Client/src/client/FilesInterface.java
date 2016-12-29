
package client;

import DataMessaging.Login;
import DataMessaging.DataAddress;
import Exceptions.ClientNotLoggedInException;
import Exceptions.CopyFileException;
import Exceptions.CreateAccountException;
import Exceptions.GetFileContentException;
import Exceptions.MakeDirException;
import Exceptions.RemoveFileOrDirException;
import Exceptions.ServerConnectionException;
import Exceptions.UploadException;
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
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException;
    public void Logout(Login login, DataAddress serverToSend) 
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException;
    public void CreateAccount(Login login, DataAddress serverToSend) 
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException;
    public ArrayList<File> GetWorkingDirContent(DataAddress serverToSend) 
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException;
    public ArrayList<File> ChangeDirectory(String serverName, String newPath)
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException;
    public String GetWorkingDirPath(String serverName) 
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException;
    public ArrayList<File> MakeDir(String serverName, String newDirName)
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException;
    public ArrayList<File> Remove(String serverName, String fileOrDirName)
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException;
    public ArrayList<File> CopyAndPaste(String serverName, String originalFilePath)
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException;
    public void Download(String serverName, String originalFilePath)
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException;
    public ArrayList<File> Upload(String serverName)
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException;
    public void GetFileContent(String serverName, String fileToOpen)
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException;
}
