/** ChatStub.java **/
import java.rmi.*;

public interface HelloIntermediate extends Remote {
	public void addMsg(int codUsuario, String msg) throws RemoteException;
	public String	recMsg(int codUltMsg, int codPrimMsg) throws RemoteException;
	public int addClient(String novoCliente) throws RemoteException;
	public void	clientExit(int codUsuario) throws RemoteException;
	public int returnCodUsuario(String name) throws RemoteException;
	public int qtdMensagens() throws RemoteException;
}
