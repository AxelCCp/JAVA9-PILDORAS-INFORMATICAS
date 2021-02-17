package A1_CHAT_ClienteServidor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class A0_192_Cliente {
	
	public static void main(String[]args) {	
		
		MarcoCliente miMarco = new MarcoCliente();
		
		miMarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
	}
}

class MarcoCliente extends JFrame{
	
	public MarcoCliente() {
		
		setTitle("APP CLIENTE");
		
		setBounds(100,50,270,450);
		
		LaminaCliente lamina1 = new LaminaCliente();
		
		add(lamina1);
		
		setVisible(true);
}}

//PONEMOS A LA APP CLIENTE A LA ESCUCHA PERMANENTEMENTE CON RUNNABLE
class LaminaCliente extends JPanel implements Runnable {
	
	public LaminaCliente() {
		
		nick = new JTextField(7);
		add(nick);
		
		JLabel texto = new JLabel("Chat");
		add(texto);
		
		ip = new JTextField(9);
		add(ip);
		
		campoChat = new JTextArea(18,20);
		add(campoChat);
		
		campo1 = new JTextField(20);
		add(campo1);
		
		miBoton = new JButton("Enviar");
		
		EnviaTexto miEvento = new EnviaTexto();
		
		miBoton.addActionListener(miEvento);
		
		add(miBoton);
		
		//PONEMOS EN FUNCIONAMIENTO EL HILO.... PONEMOS "THIS" PQ ES LA PROPIA CLASE DONDE ESTAMOS, 
		//..LA QUE TIENE ESTE HILO Y EL RUN.
		Thread miHilo = new Thread(this);
		miHilo.start();
		
	}
	
	
	private class EnviaTexto implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			
			//PARA QUE SE VEA LO QUE HEMOS ESCRITO NOSOTROS EN EL CHAT
			campoChat.append("\n" + campo1.getText());
			
			try {
				Socket miSocket = new Socket("192.168.0.8",9999);
				
				PaqueteEnvio datos = new PaqueteEnvio();
				
				//OBTENEMOS EL DATO QUE HAY DENTRO DEL JTEXTFIELD Y SE LO PASAMOS A SETTER
				datos.setNick(nick.getText());
				datos.setIp(ip.getText());
				datos.setMensaje(campo1.getText());
				
				//CREAMOS FLUJO DE DATOS DE SALIDA PARA ENVIAR UN OBJETO POR LA RED:
				ObjectOutputStream paqueteDatos = new ObjectOutputStream(miSocket.getOutputStream());
				
				//ESCRIBIR DENTRO DEL FLUJO DE SALIDA, EL PAQUETE:
				paqueteDatos.writeObject(datos);
				miSocket.close();
				
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				System.out.println(e1.getMessage());
			}
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			
			//CREAMOS UN SERVERSOCKET, PONEMOS A LA ESCUCHA A ESTA APP Y ESPECIFICAMOS EL PUERTO 9090.
			ServerSocket servidorCliente = new ServerSocket(9090);
			
			
			//CREAMOS UN SOCKET O CANAL POR DONDE SE VA A RECIBIR EL PAQUETE
			Socket cliente;
			
			//CREAMOS VARIABLE DE TIPO PAQUETEENVÍO QUE ALMACENE EL PAQUETE RECIBIDO
			PaqueteEnvio paqueteRecibido;
			
			//CON UN WHILE INDEFINIDO, PONEMOS A LA APP PERMANENTEMENTE A LA ESCUCHA
			while(true) {
				cliente =servidorCliente.accept();
				
				
				//CREAMOS FLUJO DE DATOS CAPAZ DE TRASPORTAR UN OBJ DE ENTRADA Y LE DECIMOS QUE USE EL SOCKET. 
				ObjectInputStream flujoEntrada = new ObjectInputStream(cliente.getInputStream());
			
				
				//LEEMOS LO QUE LLEGA POR EL FLUJO DE ENTRADA
				paqueteRecibido = (PaqueteEnvio) flujoEntrada.readObject(); 
				
				//ESCRIBIMOS LA INFO DE PAQUETERECIBIDO EN EL CAMPOCHAT DE LA INTERFAZ
				campoChat.append("\n" + paqueteRecibido.getNick() + ": " + paqueteRecibido.getMensaje());
			}
			
			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	
	private JTextField campo1,nick,ip;
	private JButton miBoton;
	private JTextArea campoChat;
	
}


//-------------------------------------------------------------------------------------------------

//SERIALIZAMOS ESTA CLASE PARA CONVERTIR LOS DATOS EN BYTES (BINARIO) PARA TRANSPORTARLOS AL SERVIDOR.
//CON ESTO, TODAS LAS INSTANCIAS PUEDEN CONVERTIRSE EN UNA SERIE DE BYTES QUE SE PUEDEN MANDAR POR LA RED.
class PaqueteEnvio implements Serializable {
	
	private String nick,ip,mensaje;

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	
}




