import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ��������
 * @author 11k
 * 1.���÷���˶˿ںţ�����������
 * 2.���տͻ��˴��������
 * 3.����Client�ಢʵ��Runnable�ӿ�
 * 4.���ͷ��������յ������ݵ������ͻ���
 */
public class ChatServer {

	boolean isStart = false;
	ServerSocket serverSocket = null;
	
	List<Client> clients = new ArrayList<ChatServer.Client>();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ChatServer().start();
	}
	/**
	 * ����������
	 */
	public void start() {
		try {
			serverSocket = new ServerSocket(7777);//���ö˿ں�
			isStart = true;
		} catch (SocketException e) {
			System.out.println("�������Ѿ�������");
			System.out.println("��ر����Ӧ�ú�����������");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			while (isStart) {
				Socket socket = serverSocket.accept();//���տͻ�������
				System.out.println("һ���ͻ������룡");
				/*isConnect = true;
				//���տͻ��˴��������
				dataInputStream = new DataInputStream(socket.getInputStream());
				while (isConnect) {
					String textString = dataInputStream.readUTF();
					System.out.println(textString);
				}*/
				Client client = new Client(socket);
				new Thread(client).start();
				
				clients.add(client);//�����д򿪵Ŀͻ���ȫ��װ��List����
				
				//dataInputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * �ڲ���
	 * ���߳�ʵ�ֶ���ͻ������ӷ�����
	 */
	private class Client implements Runnable {

		private Socket s;
		private boolean isConnect = false;
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
		
		//���췽��	:new Client��ͬʱ�����ֽ�������
		public Client(Socket s) {
			this.s = s;
			try {
				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());
				isConnect = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			try {
				while (isConnect) {
					String text = dis.readUTF();//��ȡ�ͻ��˴������������
					System.out.println(text);
					
					//����Ϣ���͸��ͻ���
					for (int i = 0; i < clients.size(); i++) {
						Client client = clients.get(i);
						
						client.send(text);
					}
					//System.out.println(clients.size());
					
				}
			} catch (EOFException e) {
				System.out.println("һ���ͻ����ѹرգ�");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (dis != null) dis.close();
					if (dos != null) dos.close();
					if (s != null) s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		
		/**
		 * ������Ϣ
		 * @param text
		 */
		private void send(String text) {
			try {
				dos.writeUTF(text);//�������˽��յ�������
			} catch (SocketException e) {
				clients.remove(this);
				System.out.println("�Է��˳��ˣ��޷�������Ϣ��");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
