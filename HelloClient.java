/** HelloClient.java **/
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.rmi.*;
import java.rmi.registry.*;

public class HelloClient implements Runnable {
	private static Thread playRMI = new Thread(new HelloClient());
	private static BufferedReader is = null;
	private static String usuario = null;
	private static int codUsuario = 0;
	private static int codUltMsg = 0;
	private static int codPrimMsg = 0;
	private static String showMsg;
	private static HelloIntermediate stub = null;
	private static String msg;
	private static String saida;
	private static boolean closed = true;
	private static int contArq = -1;

	public static void main(String[] args) {
		String host = (args.length < 1) ? null : args[0];
		try {
			// Obtém uma referência para o registro do RMI
			Registry registry = LocateRegistry.getRegistry(host, 2223);

			// Obtém a stub do servidor
			stub = (HelloIntermediate) registry.lookup("Hello");
			is = new BufferedReader(new InputStreamReader(System.in));
			codUltMsg = stub.qtdMensagens();
			System.out.println("\nSeja bem-vindo ao chat RMI do grupinho do ppanet!");
			System.out.print("Digite seu nome: ");
			while (true) {
				usuario = is.readLine().trim();
				codUsuario = stub.addClient(usuario);
				if (codUsuario == -1){
					System.err.println("Nome em uso. Digite outro.");
				}
				else{
					break;
				}
			}
			closed = false;
			playRMI.start();
			while (!closed){
				msg = is.readLine().trim();
				if (msg.equals("exit")){
					stub.clientExit(codUsuario);
					closed = true;
					break;
				}
				String conteudoArq = new String(Files.readAllBytes(Paths.get(msg)));
				stub.addMsg(codUsuario, conteudoArq);
			}
			playRMI.join();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// Consulta do servidor de tempos em tempos (polling)
	public void run(){
		try	{
			while (!closed) {
				//inBuf = new byte[256];
				//inPacket = new DatagramPacket(inBuf, inBuf.length);
				codPrimMsg = stub.qtdMensagens();
				if (codPrimMsg > codUltMsg) {
					showMsg = stub.recMsg(codUltMsg, codPrimMsg);
					codUltMsg = codPrimMsg;
					System.out.printf(showMsg);
					if(contArq == -1){
						contArq = 1;
					}

					String array[] = new String[2];
					String texto = ">: ";

					if (showMsg.contains(texto)){
						//String st1 = "Ricardo"; String st2 = st1.substring(0, 3);
						FileWriter arq = new FileWriter(usuario+"-0"+contArq+".client");
						PrintWriter gravarArq = new PrintWriter(arq);
						array = showMsg.split(">: ");
						String st2 = array[1].substring(0,array[1].length()-1);
						gravarArq.printf(st2);
						contArq++;
						arq.close();
					}

				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
