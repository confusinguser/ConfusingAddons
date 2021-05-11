package com.confusinguser.confusingaddons.core;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.confusinguser.confusingaddons.core.feature.Feature;
import com.confusinguser.confusingaddons.utils.Multithreading;
import com.confusinguser.confusingaddons.utils.Utils;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class LiveGcConnectionManager {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final ConfusingAddons main = ConfusingAddons.getInstance();
    public final BlockingQueue<ClientChatReceivedEvent> sendQueue = new ArrayBlockingQueue<>(1024, true);
    private final BlockingQueue<String> sendToChatQueue = new ArrayBlockingQueue<>(1024, true);
    public DataInputStream dataInputStream;
    public Socket liveGCSocket;
    public AtomicBoolean terminateThread = new AtomicBoolean(false);

    public LiveGcConnectionManager() {
//        connectToServer();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void connectToServer() {
        if (!Utils.isOnHypixel()) return;
        int attempt = 0;
        while (Utils.isOnHypixel()) {
            try {
                liveGCSocket = new Socket(main.getRuntimeInfo().getLiveGCIP(), 35746);
                dataInputStream = new DataInputStream(liveGCSocket.getInputStream());
                break;
            } catch (IOException exception) {
                try {
                    //noinspection BusyWait
                    Thread.sleep((int) Math.min(60, Math.pow(2, attempt++)));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }


        // Receiver
        Multithreading.runAsync(() -> {
            while (!Thread.currentThread().isInterrupted() && !terminateThread.get() && Utils.isOnHypixel()) {
                String data;
                JsonObject parsedJson = null;
                try {
                    byte[] dataBytes = new byte[4096];
                    dataInputStream.read(dataBytes);
                    data = new DataInputStream(new ByteArrayInputStream(dataBytes)).readUTF();
                    parsedJson = new JsonParser().parse(data).getAsJsonObject();
                } catch (IOException | JsonIOException e) { // Stream closed on server side
                    try {
                        liveGCSocket.close();
                        dataInputStream.close();
                        if (Utils.isOnHypixel()) {
                            liveGCSocket = new Socket(main.getRuntimeInfo().getLiveGCIP(), 35746);
                            dataInputStream = new DataInputStream(liveGCSocket.getInputStream());
                        }
                    } catch (IOException ex) {
                        if (!terminateThread.get()) {
                            if (Utils.isOnHypixel())
                                main.resetLiveGcConnectionManager();

                            terminateThread.set(true);
                        }
                        return;
                    }
                } catch (JsonParseException | IllegalStateException ignored) {
                }

                if (parsedJson != null && parsedJson.has("type")) {
                    if (parsedJson.get("type").getAsString().equals("chat")) {
                        if (parsedJson.has("message")) {
                            if (Feature.isEnabled("SHOW_MESSAGES_FROM_DISCORD") || (parsedJson.has("override") && parsedJson.get("override").getAsBoolean())) {
                                IChatComponent message = Utils.fixChatComponentColors(new ChatComponentText(parsedJson.get("message").getAsString()));
                                Utils.sendMessageToPlayer(message);
                            }
                        }
                        if (parsedJson.has("send_to_chat")) {
                            sendToChatQueue.offer(parsedJson.get("send_to_chat").getAsString());
                        }
                    }
                }
            }
        });

        // Send to chat mechanism
        Multithreading.runAsync(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    mc.thePlayer.sendChatMessage(sendToChatQueue.take());
                    //noinspection BusyWait
                    Thread.sleep(1000); // This is simply a cooldown
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        // Sender
        Multithreading.runAsync(() -> {
            DataOutputStream dataOutputStream;
            try {
                dataOutputStream = new DataOutputStream(liveGCSocket.getOutputStream());
            } catch (IOException e) {
                return;
            }

            // Send UUID for guild identifiaction
            JsonObject json = new JsonObject();
            json.addProperty("senderUUID", mc.thePlayer.getUniqueID().toString());
            try {
                dataOutputStream.writeUTF(json.toString());
                dataOutputStream.flush();
            } catch (IOException ignored) {
            }


            while (!Thread.currentThread().isInterrupted() && !terminateThread.get() && Utils.isOnHypixel()) {
                ClientChatReceivedEvent event;
                try {
                    event = sendQueue.take();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                json = new JsonObject();
                json.addProperty("message", event.message.getFormattedText());
                json.addProperty("senderUUID", mc.thePlayer.getUniqueID().toString());
                json.addProperty("modVersion", ConfusingAddons.VERSION);
                try {
                    dataOutputStream.writeUTF(json.toString());
                    dataOutputStream.flush();
                } catch (IOException | JsonIOException e) { // Stream closed on server side
                    try {
                        liveGCSocket.close();
                        dataOutputStream.close();
                        if (Utils.isOnHypixel()) {
                            liveGCSocket = new Socket(main.getRuntimeInfo().getLiveGCIP(), 35746);
                            dataOutputStream = new DataOutputStream(liveGCSocket.getOutputStream());
                        }
                        main.getChatMessageListener().onChatMessageLowPrio(event); // Retry sending message
                    } catch (IOException ex) {
                        if (!terminateThread.get()) {
                            if (Utils.isOnHypixel())
                                main.resetLiveGcConnectionManager();

                            terminateThread.set(true);
                        }
                        return;
                    }
                }
            }
        });
    }




}
