package A1_CHAT_ClienteServidor;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class A1_193_Servidor {
	public static void main(String[]args) {
	MarcoServidor miMarco = new MarcoServidor();
	miMarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

//HACEMOS QUE ESTA CLASE ESTÉ SIEMPRE A LA ESCUCHA CON RUNNABLE
class MarcoServidor extends JFrame implements Runnable{
	
	public MarcoServidor() {
		setTitle("APP SERVIDOR");
		setBounds(100,100,270,450);
		JPanel lamina1 = new JPanel();
		
		lamina1.setLayout(new BorderLayout());
		areaTexto = new JTextArea();
		lamina1.add(areaTexto,BorderLayout.CENTER);
		add(lamina1);
		setVisible(true);	
		
		//CREAMOS UN HILO Y LO EJECUTAMOS
		Thread miHilo = new Thread(this);
		miHilo.start();
	}
	
	@Override
	public void run() {
		//CREA UN SOCKET DE SERVIDOR, ABIERTO EN EL PUERTO ESPECIFICADO EN EL PARAMETRO DEL CONSTRUCTOR.
		//CON ESTO PONEMOS A LA APP A LA ESCUCHA.
		//VA DENTRO DE UN TRY/CATCH.
		try {
			ServerSocket servidor = new ServerSocket(9999);
			
			//almacenamos la info que llega por la red
			String nick,ip,mensaje;
			
			PaqueteEnvio paqueteRecibido;
			
			
			//BUCLE INFINITO
			while(true) {
			
			//AHORA LE DECIMOS A NUESTRA APP QUE ACEPTE CUALQUIER CONEXIÓN DEL EXTERIOR
			Socket miSocket = servidor.accept();
			
			
			//CREAMOS UN OBJECTOUTPUTSTREAM, PARA RECIBIR OBJETOS CON UN "FLUJO DE ENTRADA"
			ObjectInputStream paqueteDatos = new ObjectInputStream(miSocket.getInputStream()); 
			
			
			//METER DENTRO DE PAQUETERECIBIDO, LO QUE LLEGA POR LA RED
			paqueteRecibido = (PaqueteEnvio) paqueteDatos.readObject();
			
			
			//ALMACENAMOS LA INFORMACIÓN DEL PAQUETE EN LAS VARIABLES
			nick=paqueteRecibido.getNick();
			ip=paqueteRecibido.getIp();
			mensaje=paqueteRecibido.getMensaje();
			
			//AGREGAMOS LA INFO A LA INTERFAZ
			areaTexto.append("\n" + nick + ":" + "\n" + mensaje + "\npara IP: " + ip);
			
			
			//CREAMOS SOCKET "PUENTE" POR DONDE LLEGUE LA INFORMACIÓN DESDE EL SERVIDOR:
			//     SERVIDOR----->>------DESTINATARIO
			Socket enviaDestinatario = new Socket(ip,9090); 
				
			
			//ENVIAMOS EL PAQUETE AL DESTINATARIO  //USAMOS EL SOCKET PARA ENVIAR AL DESTINATARIO.
			ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream()); 
			
			
			//CARGAMOS LA INFORMACIÓN EN PAQUETEREENVIO. 
			//..ESCRIBIMOS EN PAQUETEREENVIO LO QUE HAY EN PAQUETERECIBIDO
			paqueteReenvio.writeObject(paqueteRecibido);
			paqueteReenvio.close();
			
			//CERRAMOS EL SOCKET
			enviaDestinatario.close();
			//CERRAMOS CONEXIÓN
			miSocket.close();
			
			}
			
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	private JTextArea areaTexto;
	
}
