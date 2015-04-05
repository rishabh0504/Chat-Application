package com.avalia.server;

import java.io.*;
import java.net.*;
import java.awt.*;

import javax.swing.*;

import java.awt.event.*;

public class Server extends JFrame {
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;

	public Server() {
		super("Backend");
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				sendMessage(event.getActionCommand());
				userText.setText("");
			}
		});
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(300, 150);
		setVisible(true);
	}

	// set up and run the server.
	public void startRunning() {

		try {
			server = new ServerSocket(8000, 100);
			while (true) {
				try {
					waitForConnection();
					setupStream();
					whileChatting();
				} catch (EOFException eofException) {
					showMessage("\n Server ended connection.!");
				} finally {
					closeCrap();
				}
				;
			}

		} catch (IOException ioException) {
			ioException.printStackTrace();

		}

	}

	// wait for connection.

	private void waitForConnection() throws IOException {
		showMessage("Waiting for someone to connect...\n");
		connection = server.accept();
		showMessage("Connected to "
				+ connection.getInetAddress().getHostAddress());

	}

	// go and setup output stream or messages.
	private void setupStream() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n\nStream are now Connected...!");
	}

	// during chatting
	private void whileChatting() throws IOException {
		String message = "You are now connected..!";
		sendMessage(message);
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n " + message);

			} catch (ClassNotFoundException classNotFoundException) {
				showMessage("\nUser send");
			}
		} while (!message.equals("Client-End"));
	}

	private void closeCrap() {
		showMessage("\n Closing connections.....\n");
		ableToType(false);

		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}

	}

	// send message to client...
	private void sendMessage(String message) {
		try {
			output.writeObject("Serevr : " + message);
			output.flush();
			showMessage("\n Server : " + message);
		} catch (IOException ioException) {
			chatWindow.append("\n Error : Sorry cant send messages.");

		}

	}

	private void showMessage(final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				chatWindow.append(message);
			}
		});

	}

	private void ableToType(final boolean b) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				userText.setEditable(b);
			}
		});

	}
}
