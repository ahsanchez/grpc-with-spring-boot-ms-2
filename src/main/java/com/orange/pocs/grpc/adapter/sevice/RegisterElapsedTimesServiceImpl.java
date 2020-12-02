package com.orange.pocs.grpc.adapter.sevice;

import com.orange.pocs.grpc.adapter.enums.Method;
import com.orange.pocs.grpc.adapter.enums.Protocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class RegisterElapsedTimesServiceImpl {

    @Value("${adapter.elapsedTimes.file}")
    private String fileName;

    public synchronized void writeEntry(Protocol protocol, Method method, double elapsedTime) throws IOException {
        String entry = protocol.name() + ";" + method.name() + ";" + elapsedTime;
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
        writer.append('\n');
        writer.append(entry);
        writer.close();
    }
}
