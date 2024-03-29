package com.ar.slimsettings.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.util.Log;

public enum ShellProvider {
	INSTANCE;

	/**
	 * 
	 */
	private static final boolean DEBUG = false;
	private static final String EOF_MARK = "s1UZA1BJt9rWWGF1tYFh";

	private class WorkerThread extends Thread {
		@Override
		protected void finalize() throws Throwable {
			process.destroy();
			this.interrupt();
			super.finalize();
		}

		static final String NO_MORE_WORK = "TERM";
		private static final String TAG = "ShellThread";
		BufferedReader output_reader;
		private Process process;
		private BlockingQueue<String> command_queue;
		private ArrayBlockingQueue<String> output_queue;
		private DataOutputStream stdin;
		private DataInputStream stdout;

		WorkerThread(BlockingQueue<String> command_queue,
				ArrayBlockingQueue<String> output_queue) {
			this.command_queue = command_queue;
			this.output_queue = output_queue;
		}

		private void init() {
			try {
				ProcessBuilder pb = new ProcessBuilder("su");
				pb.redirectErrorStream(true);
				process = pb.start();
				// process = Runtime.getRuntime().exec("su");
				stdin = new DataOutputStream(process.getOutputStream());
				stdout = new DataInputStream(process.getInputStream());
				output_reader = new BufferedReader(
						new InputStreamReader(stdout));

			} catch (Exception e) {
				Log.d("ShellProvider", "Couldnt start shell process" + e.toString());
				e.printStackTrace();

			}
		}

		@Override
		public void run() {

			try {
				while (true) {
					String command = command_queue.take();
					if (ensure_process()){
					if (command.equals(NO_MORE_WORK)) {
						command_queue = null;
						output_queue = null;
						stdin.writeBytes("exit \n");
						stdin.flush();
						stdin.close();
						stdout.close();
						output_reader.close();
						process.destroy();
						break;
					}
						stdin.writeBytes(command + "\n" + "echo \"" + EOF_MARK
								+ "\" \n");
						stdin.flush();

						StringBuilder sb = new StringBuilder();
						String line;
						while (((line = output_reader.readLine()) != null)
								&& !line.contains(EOF_MARK)) {
							if (!line.equals(""))
								sb.append(line + "\n");
						}
						/*
						 * try { Thread.sleep(200); } catch
						 * (InterruptedException e) { // TODO Auto-generated
						 * catch block e.printStackTrace(); }
						 */
						output_queue.put(sb.toString());
					
				}
					else output_queue.put("nosu");
				}
			}

			catch (IOException e) {
				Log.d("ShellProvider", "init?");
				e.printStackTrace();
			} catch (InterruptedException e) {
				Log.d("ShellProvider", "waited enough?");
				e.printStackTrace();
			}
		}

		public boolean ensure_process() {
			try {

				if (process == null) {
					if (DEBUG)
						Log.d(TAG,
								"No active shell process, initializing...");
					init();
					
				}
				if (process != null)
					process.exitValue();
					init();
				if (DEBUG)
					Log.w(TAG,
							"No active shell process, initializing...");
				if (process != null)
					process.exitValue();

			} catch (IllegalThreadStateException e) {
				if (DEBUG)
					Log.d(TAG, "Shell process running...");
				return true;
			}
			return false;
		}

	}

	private BlockingQueue<String> command_queue;
	private transient WorkerThread worker;
	private ArrayBlockingQueue<String> output_queue;

	public synchronized void finishWork() {
		try {
			command_queue.put("TERM");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized String getCommandOutput(String command) {
		if (worker == null || !worker.isAlive()) {
			output_queue = new ArrayBlockingQueue<String>(10);
			command_queue = new ArrayBlockingQueue<String>(10);
			worker = new WorkerThread(command_queue, output_queue);
			worker.setName("ShellThread");
			worker.start();
		}
		String output = "";
		output_queue.clear();
		try {
			command_queue.put(command);

		} catch (InterruptedException e) {
			Log.d("ShellProvider", "Interrupted while command_queue.put(command): " + e.toString());
			e.printStackTrace();
			return null;
		}
		try {
			output = output_queue.take();
		} catch (InterruptedException e2) {
			Log.d("ShellProvider",
					"Interrupted while output_queue.take(): " + e2.toString());
		}
		/*if (DEBUG)
			Log.d("ShellProvider", "get output: " + command + " -> \n" + output);*/
		return output;
	}

	public synchronized boolean isSuAvailable() {
		return getCommandOutput("id").contains("uid=0");

	}
}
