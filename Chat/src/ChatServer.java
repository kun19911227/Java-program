import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天服务端
 * @author 11k
 * 1.设置服务端端口号，启动服务器
 * 2.接收客户端传输的数据
 * 3.创建Client类并实现Runnable接口
 * 4.发送服务器接收到的数据到各个客户端
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
	 * 启动服务器
	 */
	public void start() {
		try {
			serverSocket = new ServerSocket(7777);//设置端口号
			isStart = true;
		} catch (SocketException e) {
			System.out.println("服务器已经启动！");
			System.out.println("请关闭相关应用后重新启动！");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			while (isStart) {
				Socket socket = serverSocket.accept();//接收客户端连接
				System.out.println("一个客户端连入！");
				/*isConnect = true;
				//接收客户端传输的数据
				dataInputStream = new DataInputStream(socket.getInputStream());
				while (isConnect) {
					String textString = dataInputStream.readUTF();
					System.out.println(textString);
				}*/
				Client client = new Client(socket);
				new Thread(client).start();
				
				clients.add(client);//将所有打开的客户端全部装入List容器
				
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
	 * 内部类
	 * 多线程实现多个客户端连接服务器
	 */
	private class Client implements Runnable {

		private Socket s;
		private boolean isConnect = false;
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
		
		//构造方法	:new Client的同时创建字节输入流
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
					String text = dis.readUTF();//读取客户端传输过来的数据
					System.out.println(text);
					
					//把消息发送给客户端
					for (int i = 0; i < clients.size(); i++) {
						Client client = clients.get(i);
						
						client.send(text);
					}
					//System.out.println(clients.size());
					
				}
			} catch (EOFException e) {
				System.out.println("一个客户端已关闭！");
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
		 * 发送消息
		 * @param text
		 */
		private void send(String text) {
			try {
				dos.writeUTF(text);//输出服务端接收到的数据
			} catch (SocketException e) {
				clients.remove(this);
				System.out.println("对方退出了，无法接收消息！");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
