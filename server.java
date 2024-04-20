import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.event.*;
import java.awt.*;

public class server extends JFrame{
    private JTextArea infoarea;
    private PrintWriter write;
    private JTextArea chatbox;
    private JTextField msgField;
    private JButton toggleServer;
    private boolean isServerRunning;
    private ServerSocket serverSocket;
    private Socket clientSocket;


    public static void main(String args[]){
        new server();
    }
    public server(){
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
                msgField.setText("");
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

        toggleServer = new JButton("Start Server");
        toggleServer.setBounds(660,400,150,50);
        toggleServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                if(!isServerRunning){
                    startServer();
                    toggleServer.setText("Stop Server");
                }
                else{
                    stopServer();
                    toggleServer.setText("Start Server");
                }
            }
        });
        jpanel.add(toggleServer);
        // JScrollPane j2ScrollPane = new JScrollPane(infoarea);
        // j2ScrollPane.setBounds(660,70,250,300);
        // jpanel.add(j2ScrollPane);

        add(jpanel);
        setVisible(true);
    }
    public void startServer(){
        try{
            //Starting the server at port 50005
            serverSocket = new ServerSocket(50005);
            infoarea.append("-  A Server has been started...\n");
            infoarea.append("-  Waiting for client to join...\n");
            isServerRunning = true;     //making the flag value true to help set the text and implementation of toggleServer Button


            //Starting a thread for accepting connections and continuously listen to messages from client
            new Thread(new Runnable(){
                @Override
                public void run(){
                    try{
                    clientSocket  =  serverSocket.accept();
                    infoarea.append("-  A Client has joined the chat...\n");

                    //setting up connection between client and server to allow the flow of data
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    write = new PrintWriter(clientSocket.getOutputStream(),true);

                    //Listening to message from client
                    String message;
                    while((message = in.readLine())!=null){     // checking if the input is not null
                        chatbox.append("Client: "+message+ "\n");  //display the message from client in server chatbox
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
                }
            }).start();
        }
        catch(IOException e){
            e.printStackTrace();    //prints the error along with line number,classes and other information
        }

    }
    public void stopServer(){
        try{
            if(serverSocket != null){
                serverSocket.close();
                isServerRunning = false;
                infoarea.append("-  Server has been stopped...\n");
            }
            if(clientSocket != null){
                clientSocket.close();
                infoarea.append("- A Client has disconnected...\n"); // Notify disconnection in the info area
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void sendMsg(String message){
        write.println(message);      //Sending message to client
        chatbox.append("Server: "+message+"\n");    //displaying the message sent by server in its own chatbox
    }
}
