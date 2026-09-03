package com.ptreservation.backend.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SseEmitterRepository {

    private static final long TIMEOUT = 30 * 60 * 1000L;

    private final Map<Long, List<SseEmitter>> classEmitters = new ConcurrentHashMap<>();
    private final Map<String, List<SseEmitter>> memberEmitters = new ConcurrentHashMap<>();

    public SseEmitter subscribeToClass(Long classId) {
        return subscribe(classEmitters, classId);
    }

    public SseEmitter subscribeToMember(String email) {
        return subscribe(memberEmitters, email);
    }

    public void sendToClass(Long classId, String eventName, Object data) {
        send(classEmitters, classId, eventName, data);
    }

    public void sendToMember(String email, String eventName, Object data) {
        send(memberEmitters, email, eventName, data);
    }

    private <K> SseEmitter subscribe(Map<K, List<SseEmitter>> registry, K key) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        List<SseEmitter> emitters = registry.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>());
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        return emitter;
    }

    private <K> void send(Map<K, List<SseEmitter>> registry, K key, String eventName, Object data) {
        List<SseEmitter> emitters = registry.get(key);
        if (emitters == null) return;

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}