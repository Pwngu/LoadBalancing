package at.tgm.ablkreim.common.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Connection {

    private static final Logger LOG = LogManager.getLogger("at.tgm.ablkreim.common.Connection");


    private final Object READING = new Object();


    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Socket socket;

    private String name;

    private volatile boolean isClosed;

    public Connection(Socket socket, String name) {

        try {

            this.socket = socket;

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            this.name = name;
        } catch(Exception ex) {

            throw new RuntimeException("Couldn't instantiate Connection", ex);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T receive() {

        LOG.debug(this.toString() + " started receiving");

        try {

            synchronized (READING) {

                Object obj = in.readObject();
                LOG.debug("{} received \"{}\"", this, obj);

                try {

                    return (T) obj;
                } catch(ClassCastException ex) {

                    LOG.debug("{} received wrong object: {}", this, obj.getClass());
                    return null;
                }
            }
        } catch(EOFException ex) {

            LOG.debug("{} receiving ended with EOF-Ex", this);
            return null;
        } catch(SocketException ex) {

            LOG.debug("{} receiving ended with Socket-Ex", this);
            return null;
        } catch(Exception ex) {

            LOG.error("Exception in {} whilst receiving", this, ex);
            return null;
        }
    }

    public boolean send(Serializable obj) {

        LOG.debug("{} sending \"{}\"", this, obj);

        try {

            out.writeObject(obj);
            out.flush();
            return true;
        } catch(Exception ex) {

            return false;
        }
    }

    public boolean send(byte data) {

        LOG.debug("{} sending Byte \"{}\"", data);

        try {

            out.writeByte(data);
            out.flush();
            return true;
        } catch(Exception ex) {

            LOG.debug("{} while sending byte");
            return false;
        }
    }

    public void close() {

        if(isClosed) return;

        LOG.info("{} closing...", this);

        isClosed = true;

        try {

            out.close();
            in.close();
            socket.close();
        } catch(Exception ignored) {}
    }

    public <T> Future<T> getAcknowledge() {

        return Executors.newCachedThreadPool().submit(new Callable<T>() {

            @Override
            @SuppressWarnings("unchecked")
            public T call() throws Exception {

                Object obj = receive();
                try {

                    return (T) obj;
                } catch(ClassCastException ex) {

                    throw new Exception(ex);
                }
            }
        });
    }

    public String toString() {

        return "Connection" + (name == null ? "" : " " + name) + ": " +
                socket.getLocalAddress().toString() + "<>" + socket.getInetAddress().toString();
    }

    public boolean isClosed() {

        return isClosed;
    }

    public Socket getSocket() {

        return socket;
    }

    public InetAddress getLocalAddress() {

        return socket.getLocalAddress();
    }

    public InetAddress getRemoteAddress() {

        return socket.getInetAddress();
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }
}