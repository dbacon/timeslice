package com.enokinomi.timeslice.app.stask;

import java.util.UUID;

public class ServerSideTaskSvc
{
    public static class ServerSideTask
    {
        public String generateMessage()
        {
            return "Nothing";
        }

        ClientDescriptor describe()
        {
            return new ClientDescriptor(UUID.randomUUID().toString(), generateMessage());
        }
    }

    public static class ClientDescriptor
    {
        private final String id;
        private final String message;

        public ClientDescriptor(String id, String message)
        {
            this.id = id;
            this.message = message;
        }

        public String getId()
        {
            return id;
        }

        public String getMessage()
        {
            return message;
        }
    }

    public void list()
    {
    }

    public void check()
    {
    }

    public void perform()
    {
    }

}
