package com.majd.pomodoro;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseRepository {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public FirebaseUser currentUser() {
        return auth.getCurrentUser();
    }

    public void signIn(String email, String password, OnSuccessListener<?> onSuccess, @NonNull Runnable onError) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(e -> onError.run());
    }

    public void register(String name, String email, String password, OnSuccessListener<?> onSuccess, @NonNull Runnable onError) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user == null) {
                        onError.run();
                        return;
                    }
                    Map<String, Object> profile = new HashMap<>();
                    profile.put("name", name);
                    profile.put("email", email);
                    profile.put("createdAt", System.currentTimeMillis());
                    firestore.collection("users").document(user.getUid())
                            .set(profile)
                            .addOnSuccessListener(onSuccess)
                            .addOnFailureListener(e -> onError.run());
                })
                .addOnFailureListener(e -> onError.run());
    }

    public void saveStudyState(int focusMin, int breakMin, int blocks, int sessionsCompleted, int totalFocusMin, int reminderHour, int reminderMinute) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            return;
        }

        Map<String, Object> study = new HashMap<>();
        study.put("lastFocusMin", focusMin);
        study.put("lastBreakMin", breakMin);
        study.put("lastBlocks", blocks);
        study.put("sessionsCompleted", sessionsCompleted);
        study.put("totalFocusMin", totalFocusMin);
        study.put("averageFocusMin", sessionsCompleted == 0 ? 0 : totalFocusMin / sessionsCompleted);
        study.put("reminderHour", reminderHour);
        study.put("reminderMinute", reminderMinute);
        study.put("updatedAt", System.currentTimeMillis());

        firestore.collection("study_stats").document(user.getUid()).set(study);
    }

    public void signOut() {
        auth.signOut();
    }
}
