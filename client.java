import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.event.*;
import java.awt.*;

public class client extends JFrame{
    private JTextArea infoarea;
    private PrintWriter write;
    private JTextArea chatbox;
    private JTextField msgField;
    private JButton toggleConn;
    private Socket socket;
    private boolean isConn;


    public static void main(String args[]){
        new client();
    }
    public client(){
        initGui();
    }
    public void initGui(){
        setTitle("Chat Gram - A Server-Client Chatting program");
        setSize(1000,800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Font font = new Font("Verdana", Font. BOLD, 16);
        Font font2 = new Font("Verdana", Font.BOLD, 13);
        JPanel jpanel = new JPanel(null);

        JLabel chatLabel = new JLabel("Chat Box: ");
        chatLabel.setBounds(15,20,100,30);
        jpanel.add(chatLabel);

        chatbox = new JTextArea();
        chatbox.setEditable(false);
        chatbox.setBorder(new LineBorder(Color.GREEN));
        chatbox.setFont(font);
        chatbox.setForeground(Color.GREEN);
        chatbox.setBackground(Color.BLACK);

        JScrollPane jScrollPane = new JScrollPane(chatbox);
        jScrollPane.setBounds(15,70,625,560);
        jpanel.add(jScrollPane);


        JLabel msgLabel = new JLabel("Enter your message here : ");
        msgLabel.setBounds(15,650,200,20);
        jpanel.add(msgLabel);

        msgField = new JTextField();
        msgField.setBounds(15,680,685,50);
        msgField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                sendMsg(msgField.getText());
                msgField.setText("");     //clear the msgField after sending the message
            }
        });
        jpanel.add(msgField);

        infoarea = new JTextArea();
        infoarea.setEditable(false);
        infoarea.setBackground(Color.BLUE);
        infoarea.setForeground(Color.YELLOW);
        infoarea.setFont(font2);
        infoarea.setLineWrap(true);
        infoarea.setBounds(660,70,250,300);
        jpanel.add(infoarea);

        // JScrollPane j2ScrollPane = new JScrollPane(infoarea);
        // j2ScrollPane.setBounds(660,70,250,300);
        // jpanel.add(j2ScrollPane);

        toggleConn = new JButton("Connect");
        toggleConn.setBounds(660,400,150,50);
        toggleConn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                if(!isConn){
                    connectServer();
                    toggleConn.setText("Disconnect");
                }
                else{
                    disconnectServer();
                    toggleConn.setText("Connect");
                }
            }
        });
        jpanel.add(toggleConn);

        add(jpanel);
        setVisible(true); 
    }
    public void connectServer(){
        try{
            Socket socket =  new Socket("192.168.2.209",50005);
            infoarea.append(" -  Connected to the Server...\n");
            isConn = true;
            
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            write = new PrintWriter(socket.getOutputStream(), true);

            //Starting a thread to listen to message from the server continuously
            new Thread(new Runnable(){
                @Override
                public void run(){
                    String message;
                    try{
                        while((message = in.readLine())!=null){
                            chatbox.append("Server: "+message+"\n");
                        }
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }
    public void disconnectServer(){
        try{
            if(socket != null){
                socket.close();
                isConn = false;
                infoarea.append("-  Disconnected from Server\n");
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void sendMsg(String message){
        write.println(message);    //Send message to the server
        chatbox.append("Client: "+message+"\n");
    }

}