package A2_CHAT_ClienteServidor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class A0_196_Cliente {
	
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
		
		//CON ESTO HACEMOS QUE AL ABRIRSE LA VENTANA, SE EJECUTE EL MÉTODO WINDOWOPENED()
		addWindowListener(new EnvioOnline());
		
		
		
		
}}



//---------------------------ENVIO DE SEÑAL ONLINE-----------------------------------------------


//CLASE QUE GESTIONA EL EVENTO DE VENTANA, AL ABRIR LA APP, SE CONECTA
class EnvioOnline extends WindowAdapter{
	
	//EL MÉTODO ENVIARÁ UNA SEÑAL AL ABRIR LA APP 
	public void windowOpened(WindowEvent e) {
		
		try {
										//IP DEL SERVIDOR
			Socket miSocket = new Socket("192.168.0.8",9999);
			
			//CREAMOS UN PAQUETE QUE PUEDA RECIBIR EL SERVIDOR
			PaqueteEnvio datos = new PaqueteEnvio();
			
			datos.setMensaje(" Online");
			
			//CREAMOS EL FLUJO DE DATOS
			ObjectOutputStream paqueteDatos = new ObjectOutputStream(miSocket.getOutputStream());
			
			//ESCRIBIMOS EL OBJETO DATOS 
			paqueteDatos.writeObject(datos);
			
			miSocket.close();
			
		}catch(Exception e2) {
			
		}
		
	}
}


//------------------------------------------------------------------------------------------------------





//PONEMOS A LA APP CLIENTE A LA ESCUCHA PERMANENTEMENTE CON RUNNABLE
class LaminaCliente extends JPanel implements Runnable {
	
	public LaminaCliente() {
		
		String nickUsuario = JOptionPane.showInputDialog("Cual es tu nick???: ");
		
		JLabel nNick = new JLabel("Nick: ");
		
		add(nNick);
		
		nick = new JLabel();
		
		nick.setText(nickUsuario);
		
		add(nick);
		
		JLabel texto = new JLabel("| Online: ");
		add(texto);
		
		ip = new JComboBox();
		
		//IP DE CLIENTES... MODIFICARLAS PARA USAR LA APP
		//ip.addItem("Usuario1");
		//ip.addItem("Usuario2");
		//ip.addItem("Usuario3");
		//ESTAS DOS ÚLTIMAS YA NO LAS NECESITAMOS, PQ LA APP AL FINAL DE LA PROGRAMACIÓN,..
		//..LO HACE AUTOMÁTICAMENTE.
		//ip.addItem("192.168.0.197");
		//ip.addItem("192.168.0.198");
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
											 //IP DE MI PC
				Socket miSocket = new Socket("192.168.0.8",9999);
				
				PaqueteEnvio datos = new PaqueteEnvio();
				
				//OBTENEMOS EL DATO QUE HAY DENTRO DEL JTEXTFIELD Y SE LO PASAMOS A SETTER
				datos.setNick(nick.getText());
				datos.setIp(ip.getSelectedItem().toString());
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
				
				
				//EL "ONLINE" DE LA LÍNEA 74.
				//ONLINE ES LA SEÑAL QUE MANDA LA APPCLIENTE A LA  APPSERVIDOR, AL ABRIR LA APPCLIENTE
				//"EN EL CASO DE QUE NO HAYAS RECIBIDO EL MENSAJE 'ONLINE' "...O SEA QUE YA ESTAMOS CHATEANDO.
				if(!paqueteRecibido.getMensaje().equals(" Online")) {
				
					
					//ESCRIBIMOS LA INFO DE PAQUETERECIBIDO EN EL CAMPOCHAT DE LA INTERFAZ
					campoChat.append("\n" + paqueteRecibido.getNick() + ": " + paqueteRecibido.getMensaje());
					
					
				}else {
				
				//CREAMOS UN ARRAYLIST DE STRING PARA QUE APAREZCAN LAS IP EN EL MENU DE LA INTERFAZ.
				//CONVERTIREMOS LAS IP A STRING
				ArrayList <String> IpsMenu = new ArrayList <String>();	
				
				//LE DECIMOS QUE ALMACENE EL ARRAYLIST QUE EL CLIENTE A RECIBIDO DEL SERVIDOR.
				IpsMenu=paqueteRecibido.getIps();
				
				
				//AQUÍ, ANTES DEL FOR, PONEMOS UN METODO PARA REMOVER LOS ITEMS DEL MENÚ IP DE LA APLICACION,..
				//..PARA EVITAR QUE SE DUPLIQUE LA INFORMACIÓN DE LOS ARRAYLIST QUE SE VAN CARGANGO..
				//CADA VEZ QUE SE CONECTA UN CLIENTE.	
				ip.removeAllItems();
			
				
				//RECORREMOS ESTE ARRAY
				for(String z : IpsMenu) {
					//AGREGAMOS LAS IP A CADA VUELTA DE BUCLE
					ip.addItem(z);
				}
				
				
					
				
				//PREPARAMOS AL CLIENTE PARA QUE RECIBA EL PAQUETE CON EL ARRAYLIST.
				//getIps() NOS DEVUELVE EL IP's.
				//campoChat.append("\n" + paqueteRecibido.getIps());
				
				}
				
			}
			
			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	
	private JTextField campo1;
	private JComboBox ip;
	private JLabel nick;
	private JButton miBoton;
	private JTextArea campoChat;
	
}


//-------------------------------------------------------------------------------------------------

//SERIALIZAMOS ESTA CLASE PARA CONVERTIR LOS DATOS EN BYTES (BINARIO) PARA TRANSPORTARLOS AL SERVIDOR.
//CON ESTO, TODAS LAS INSTANCIAS PUEDEN CONVERTIRSE EN UNA SERIE DE BYTES QUE SE PUEDEN MANDAR POR LA RED.
class PaqueteEnvio implements Serializable {
	
	private String nick,ip,mensaje;
	
	private ArrayList <String> Ips; 

	public ArrayList<String> getIps() {
		return Ips;
	}

	public void setIps(ArrayList<String> ips) {
		Ips = ips;
	}

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







