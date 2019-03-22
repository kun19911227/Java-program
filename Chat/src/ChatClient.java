import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

/**
 * 聊天客户端
 * @author 11k
 * 1.创建客户端窗口并设置位置和大小，并添加关闭窗口监听器(GUI)
 * 2.在窗口中添加输入框和显示框，并添加输入框的监听器(GUI)
 * 3.创建服务端ChatServer.java(Socket)
 * 4.创建连接(Socket)
 * 5.往服务端传输数据(IO)
 * 6.关闭窗口，断开连接
 * 7.接收服务端传过来的数据并显示在每一个客户端显示框中
 */
@SuppressWarnings("serial")
public class ChatClient extends Frame{

	private TextField textField = new TextField();//输入框
	private TextArea textArea = new TextArea();//显示框
	
	private Socket socket = null;
	private DataOutputStream dataOutputStream = null;
	private DataInputStream dataInputStream = null;
	private boolean isConnect = false;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//启动客户端窗口
		new ChatClient().launchFrame();
	}

	/**
	 * 启动客户端
	 */
	public void launchFrame() {
		
		setTitle("模拟在线群聊系统V1.0");
		setLocation(300,300);//设置窗口位置大小
		setSize(300,300);
		
		add(textField,BorderLayout.SOUTH);//布局
		add(textArea,BorderLayout.NORTH);
		
		pack();//调整此窗口的大小，以适合其子组件的首选大小和布局。
		
		//添加监听器关闭窗口(匿名类)
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				disconnect();//断开连接
				System.exit(0);
			}
		});
		
		//输入框监听器
		textField.addActionListener(new TextFiledListener());
		
		setVisible(true);//可见的
		
		connect();//连接服务端
		
		//启动线程
		//RecThread recThread = new RecThread();
		//Thread thread = new Thread(recThread);
		//thread.start();
		new Thread(new RecThread()).start();
		
	}
	
	/**
	 * 连接服务端
	 */
	public void connect() {
		try {
			socket = new Socket("127.0.0.1", 7777);//通过端口号确定连接哪个服务端
			dataOutputStream = new DataOutputStream(socket.getOutputStream());//创建数据输出流
			dataInputStream = new DataInputStream(socket.getInputStream());
			isConnect = true;
			
			System.out.println("已经连接服务器！");
			
		} catch (ConnectException e) {
			System.out.println("服务器已关闭！");
		}catch(UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 断开连接
	 */
	public void disconnect() {
		try {
			if (dataOutputStream != null) dataOutputStream.close();//冲刷并关闭输出流
			if (dataInputStream != null) dataInputStream.close();
			if (socket != null) socket.close();//关闭插座
			
			System.out.println("断开连接！");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 输入框监听器(内部类)
	 */
	private class TextFiledListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String textString = textField.getText().trim();//获取输入框中的内容
			//textArea.setText(textString);//把输入框中的内容赋值给显示框显示
			textField.setText("");//把输入框清空
			
			try {
				dataOutputStream.writeUTF(textString);//写出字节
				dataOutputStream.flush();//冲刷出流，将所有缓存数据强制发送到目的地
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
	}
	/**
	 * 创建线程接收服务器发送的消息
	 */
	private class RecThread implements Runnable {

		@Override
		public void run() {
			try {
				while (isConnect) {
					String text = dataInputStream.readUTF();//读取到服务端传来的数据
					System.out.println(text);
					textArea.setText(textArea.getText() + text + "\n");//展示
				}
			} catch (SocketException e) {
				System.out.println("退出了");
			}catch (IOException e) {
				e.printStackTrace();
			}
				
		}
		
	}
}
