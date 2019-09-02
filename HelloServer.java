/** HelloServer.java **/
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import java.nio.file.*;

public class HelloServer implements HelloIntermediate {
	public HelloServer() {}

	private ArrayList<String> Mensagens = new ArrayList<String>();
	private ArrayList<String> clientes = new ArrayList<String>();
	private int contadorMensagens = 0;

	public static void main(String[] args) {
		try {
			// Instancia o objeto servidor e a sua stub
			HelloServer server = new HelloServer();
			HelloIntermediate stub = (HelloIntermediate) UnicastRemoteObject.exportObject(server, 0);
			// Registra a stub no RMI Registry para que ela seja obtida pelos clientes
			Registry registry = LocateRegistry.createRegistry(2223);
			//Registry registry = LocateRegistry.getRegistry(9999);
			registry.bind("Hello", stub);
			System.out.println("Servidor pronto");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	//Aqui o servidor através das funções tem o controle dos clientes e das mensagens
	//Mensagens ao qual ele reenvia aos clientes.

	public void addMsg(int codUsuario, String msg) throws RemoteException {
		String usuario = clientes.get(codUsuario);
		String array[] = new String[2];
		String texto = ">: ";

		
		FileWriter arq = new FileWriter(usuario+"-0"+codUsuario+".serv");
		PrintWriter gravarArq = new PrintWriter(arq);
		gravarArq.printf(msg);
		arq.close();
					
		Mensagens.add(String.format("< " +usuario +">: " +msg +"\n"));
	}

	public String recMsg(int codUltMsg, int codPrimMsg) throws RemoteException {
		if (codUltMsg == codPrimMsg){
			return null;
		}
		StringBuilder msgReceive = new StringBuilder();
		for (int i = codUltMsg; i < codPrimMsg; i++){
			msgReceive.append(Mensagens.get(i));
		}
		return msgReceive.toString();
	}

	public int addClient(String novoCliente) throws RemoteException {
		if (returnCodUsuario(novoCliente) != -1){
			//Se for != -1 é porque já temos um cliente cadastrado com tal nickname
			return -1;
		}
		clientes.add(novoCliente);
		int codUsuario = returnCodUsuario(novoCliente);
		Mensagens.add(String.format("*** " + novoCliente + " entered the chat room! ***\n"));
		return returnCodUsuario(novoCliente);
	}

	public void clientExit(int codUsuario) throws RemoteException {
		String usuario = clientes.get(codUsuario);
		Mensagens.add(String.format("*** " + usuario + " is leaving the chat room! ***\n"));
	}

	public int returnCodUsuario(String name){
		int codUsuario = -1;
		for (int i = 0; i < clientes.size(); i++){
			if (name.equals(clientes.get(i))){
				codUsuario = i;
			}
		}
		return codUsuario;
		//retorna -1 quando usuario não cadastrado/encontrado.
	}

	public int qtdMensagens() throws RemoteException {
		return Mensagens.size();
	}
}
