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
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        List<SseEmitter> emitters = classEmitters.computeIfAbsent(classId, id -> new CopyOnWriteArrayList<>());
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        return emitter;
    }

    public SseEmitter subscribeToMember(String email) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        List<SseEmitter> emitters = memberEmitters.computeIfAbsent(email, e -> new CopyOnWriteArrayList<>());
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        return emitter;
    }

    public void sendToClass(Long classId, String eventName, Object data) {
        List<SseEmitter> emitters = classEmitters.get(classId);
        if (emitters == null) return;

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }

    public void sendToMember(String email, String eventName, Object data) {
        List<SseEmitter> emitters = memberEmitters.get(email);
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