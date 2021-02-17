package A0_Sockets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class A0_190_Cliente {
	public static void main(String[]args) {		
		MarcoCliente miMarco = new MarcoCliente();
		miMarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
	}
}


class MarcoCliente extends JFrame{
	public MarcoCliente() {
		setTitle("APP CLIENTE");
		setBounds(100,50,800,600);
		LaminaCliente lamina1 = new LaminaCliente();
		add(lamina1);
		setVisible(true);
}}


class LaminaCliente extends JPanel{
	public LaminaCliente() {
		JLabel texto = new JLabel("Cliente");
		add(texto);
		campo1 = new JTextField(20);
		add(campo1);
		miBoton = new JButton("Enviar");
		
		EnviaTexto miEvento = new EnviaTexto();
		miBoton.addActionListener(miEvento);
		
		add(miBoton);
	}
	
	
	private class EnviaTexto implements ActionListener {
		
		//CONSTRUCCION DEL SOCKET 
		public void actionPerformed(ActionEvent e) {
			//System.out.println(campo1.getText());
			
			try {
				Socket miSocket = new Socket("192.168.0.8",9999);
				
				//CONSTRUIMOS UN FLUJO DE DATOS QUE VA DE SALIDA Y ESTE VA A CIRCULAR POR..
				//MISOCKET. ESTABLECEMOS EL PUENTE CON getOutputStream().
				DataOutputStream flujoSalida = new DataOutputStream(miSocket.getOutputStream());
				
				
				//DEFINIMOS QUÉ ES LO QUE VA A CIRCULAR POR ESTO DATOS DE FLUJO DE SALIDA:
				//DEF: "ESCRIBE EN EL FLUJOSALIDA, LO QUE ESTÁ ESCRITO EN EL CAMPO1".
				flujoSalida.writeUTF(campo1.getText());
				flujoSalida.close();
				
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				//USAMOS GETMESSAGE(), PARA QUE NOS LANCE CUAL HA SIDO EL ERROR. 
				System.out.println(e1.getMessage());
			}
		}
		
	}
	
	
	private JTextField campo1;
	private JButton miBoton;
		
}
