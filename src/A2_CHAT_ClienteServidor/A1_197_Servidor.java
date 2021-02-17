package A2_CHAT_ClienteServidor;
import java.awt.BorderLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;


public class A1_197_Servidor {

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
			
			
			//DECLARAMOS UN ARRAYLIST PARA ALMACENAR Y VER LAS DIRECCIONES IP EN LA INTERFAZ
			//PONEMOS ESTO AQUÍ, PARA QUE SEA LEIDO SOLO UNA VEZ, PERO LO LLENAMOS MÁS ABAJO EN ..
			//..DETECTA ONLINE
			ArrayList <String> listaIp = new ArrayList <String>();
			
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
			
			
			
			if(!mensaje.equals(" Online")) {
				
			
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
			
			
			}else {
				
				//-----------------DETECTA ONLINE---------------------------------------------------------------
				
				//ALMACENAMOS DENTRO DE LA VARIABLE LOCALIZACIÓN, LA DIRECCION DEL CLIENTE.. 
				//..CON EL QUE ACABAMOS DE CONECTAR 
				InetAddress localizacion = miSocket.getInetAddress();
				
				//ALMACENAMOS A IP DEL CLIENTE QUE ACABA DE CONECTAR
				String ipRemota = localizacion.getHostAddress();
				
				System.out.println("Online" + ipRemota);
				
				
				//RELLENAMOS EL ARRAYLIST CON LAS IP QUE SE VAN ALMACENANDO EN LA VARIABLE ipRemota
				//CADA VEZ QUE EL CODIGO LLEGUE AQUÍ, SE IRÁ RELLENANDO CON UNA OP
				listaIp.add(ipRemota);
				
				
				//CON ESTO METEMOS DENTRO DEL PAQUETERECIBIDO, UN ELEMENTOS MÁS, EL ARRAYLIST.
				//DESPUES HABRÁ QUE MANDAR ESTE PAQUETE A LOS CLIENTES.
				paqueteRecibido.setIps(listaIp);
				
				
				//PRUEBA
				//ESTE BLUCLE RECORRE ELEMENTO A ELEMENTO EL ARRAYLIST DE LAS IP's. 
				//Y SE PUEDE USAR PARA ENVIAR LAS DIRECCIONES IP EXISTENTES A CADA UNA DE LAS.. 
				//..DIRECCIONES IP CONECTADAS A LA APP.
				//A CADA VUELDA DE BUCLE, COGE UNA IP Y LE ENVÍA TODO EL ARRAYLIST. 
				for(String z : listaIp) {
					System.out.println("Array: " + z);
					
					//CREAMOS UN SOCKET O COMUNICACION CON CADA UNA DE LAS IP's, QUE DEBERÁ ENVIAR 
					//..UN FLUJO DE DATOS CON UN PAQUETE EN EL CUAL VA EL ARRAYLIST.
					Socket enviaDestinatario = new Socket(z,9090); 
					
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
				
				
				//----------------------------------------------------------------------------------------------
			}
			
			}
			
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	private JTextArea areaTexto;
	
}
