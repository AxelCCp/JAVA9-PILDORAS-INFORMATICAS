package A0_Sockets;

import java.awt.BorderLayout;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class A1_190_Servidor {
	public static void main(String[]args) {
	MarcoServidor miMarco = new MarcoServidor();
	miMarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

//HACEMOS QUE ESTA CLASE ESTÉ SIEMPRE A LA ESCUCHA CON RUNNABLE
class MarcoServidor extends JFrame implements Runnable{
	
	public MarcoServidor() {
		setTitle("APP SERVIDOR");
		setBounds(100,100,800,600);
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
			
			//BUCLE INFINITO
			while(true) {
			
			
			//AHORA LE DECIMOS A NUESTRA APP QUE ACEPTE CUALQUIER CONEXIÓN DEL EXTERIOR
			Socket miSocket = servidor.accept();
			
			
			//AHORA CREAMOS EL FLUJO DE ENTRADA
			//INDICAMOS TAMBN POR CUAL SOCKET VIENE LA INFO DE ENTRADA, CON GETINPUTSTREAM()
			DataInputStream flujoEntrada = new DataInputStream(miSocket.getInputStream());
			
			
			//TENEMOS QUE SABER LEER LO QUE VIENE EN EL FLUJO DE ENTRADA.
			//AQUÍ ALMACENAMOS EL TEXTO QUE VIENE DEL CLIENTE;
			String mensajeTexto = flujoEntrada.readUTF();
			
			
			//AGREGAMOS EL TEXTO AL JTEXTAREA AREATEXTO
			areaTexto.append("\n" + mensajeTexto);
			
			
			//CERRAMOS CONEXIÓN
			miSocket.close();
			
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	private JTextArea areaTexto;
	
}