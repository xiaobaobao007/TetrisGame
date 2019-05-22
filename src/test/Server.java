package test;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends JFrame {

    private ServerSocket serverSocket;//建立服务器连接
    private Socket socket;//连接客户端
    private DataInputStream dis;//数据接收
    private DataOutputStream dos;//数据传送

    Server(){

    }
}