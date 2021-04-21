package zad2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ChatServer extends Thread {

    /*
    you can use netcat as client  (nc 127.0.0.1 12345)
     */

    String host = "127.0.0.1";
    int port=12345;

    public ChatServer() throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(new InetSocketAddress(host,port));
        serverChannel.configureBlocking(false);
        int ops = serverChannel.validOps();

        Selector selector = Selector.open();

        // SelectionKey slcKey = serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        SelectionKey slcKey = serverChannel.register(selector, ops, null);     // alternatywny sposob

        for (;;){
            System.out.println("writing for selectt..");
            int nrOfKeys = selector.select();
            System.out.println("number of selected keys "+nrOfKeys);
            Set selectedKeys = selector.selectedKeys();
            Iterator iterator = selectedKeys.iterator();

            while (iterator.hasNext()){
                SelectionKey key = (SelectionKey) iterator.next();
                if (key.isAcceptable()){
                    SocketChannel client = serverChannel.accept();
                    client.configureBlocking(false);
                    client.register(selector,SelectionKey.OP_READ);
                    System.out.println("accept new connection from "+client);

                }else if(key.isReadable()){
                    SocketChannel client = (SocketChannel)key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(512);
                    client.read(buffer);
                    String out = new String(buffer.array()).trim();
                    System.out.println("message > "+out);
                    if(out.equals("bye.")){
                        client.close();
                        System.out.println("server is closed");

                    }
                }
                iterator.remove();

            }

        }


    }
}
