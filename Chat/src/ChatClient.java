import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

/**
 * ����ͻ���
 * @author 11k
 * 1.�����ͻ��˴��ڲ�����λ�úʹ�С������ӹرմ��ڼ�����(GUI)
 * 2.�ڴ����������������ʾ�򣬲���������ļ�����(GUI)
 * 3.���������ChatServer.java(Socket)
 * 4.��������(Socket)
 * 5.������˴�������(IO)
 * 6.�رմ��ڣ��Ͽ�����
 * 7.���շ���˴����������ݲ���ʾ��ÿһ���ͻ�����ʾ����
 */
@SuppressWarnings("serial")
public class ChatClient extends Frame{

	private TextField textField = new TextField();//�����
	private TextArea textArea = new TextArea();//��ʾ��
	
	private Socket socket = null;
	private DataOutputStream dataOutputStream = null;
	private DataInputStream dataInputStream = null;
	private boolean isConnect = false;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//�����ͻ��˴���
		new ChatClient().launchFrame();
	}

	/**
	 * �����ͻ���
	 */
	public void launchFrame() {
		
		setTitle("ģ������Ⱥ��ϵͳV1.0");
		setLocation(300,300);//���ô���λ�ô�С
		setSize(300,300);
		
		add(textField,BorderLayout.SOUTH);//����
		add(textArea,BorderLayout.NORTH);
		
		pack();//�����˴��ڵĴ�С�����ʺ������������ѡ��С�Ͳ��֡�
		
		//��Ӽ������رմ���(������)
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				disconnect();//�Ͽ�����
				System.exit(0);
			}
		});
		
		//����������
		textField.addActionListener(new TextFiledListener());
		
		setVisible(true);//�ɼ���
		
		connect();//���ӷ����
		
		//�����߳�
		//RecThread recThread = new RecThread();
		//Thread thread = new Thread(recThread);
		//thread.start();
		new Thread(new RecThread()).start();
		
	}
	
	/**
	 * ���ӷ����
	 */
	public void connect() {
		try {
			socket = new Socket("127.0.0.1", 7777);//ͨ���˿ں�ȷ�������ĸ������
			dataOutputStream = new DataOutputStream(socket.getOutputStream());//�������������
			dataInputStream = new DataInputStream(socket.getInputStream());
			isConnect = true;
			
			System.out.println("�Ѿ����ӷ�������");
			
		} catch (ConnectException e) {
			System.out.println("�������ѹرգ�");
		}catch(UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * �Ͽ�����
	 */
	public void disconnect() {
		try {
			if (dataOutputStream != null) dataOutputStream.close();//��ˢ���ر������
			if (dataInputStream != null) dataInputStream.close();
			if (socket != null) socket.close();//�رղ���
			
			System.out.println("�Ͽ����ӣ�");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ����������(�ڲ���)
	 */
	private class TextFiledListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String textString = textField.getText().trim();//��ȡ������е�����
			//textArea.setText(textString);//��������е����ݸ�ֵ����ʾ����ʾ
			textField.setText("");//����������
			
			try {
				dataOutputStream.writeUTF(textString);//д���ֽ�
				dataOutputStream.flush();//��ˢ�����������л�������ǿ�Ʒ��͵�Ŀ�ĵ�
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
	}
	/**
	 * �����߳̽��շ��������͵���Ϣ
	 */
	private class RecThread implements Runnable {

		@Override
		public void run() {
			try {
				while (isConnect) {
					String text = dataInputStream.readUTF();//��ȡ������˴���������
					System.out.println(text);
					textArea.setText(textArea.getText() + text + "\n");//չʾ
				}
			} catch (SocketException e) {
				System.out.println("�˳���");
			}catch (IOException e) {
				e.printStackTrace();
			}
				
		}
		
	}
}
