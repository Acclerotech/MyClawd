package com.myclawd.scanning.domain;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Box-level time tracking for one sub-step (Prep, Scan, QC, Coding, OCR, Export), as in the
 * prototype's Box Time Tracker. Every Start/Resume opens a session and every Pause/Complete
 * closes it, so the Timesheet export gets one row per real work session with real timestamps.
 *
 * <p>Pure domain logic; persistence (box_substep / substep_session tables) is the next slice.
 */
public class SubStepTimer {

    public enum SubStep { PREP, SCAN, QC, CODING, OCR, EXPORT }

    public enum Status { NOT_STARTED, IN_PROGRESS, PAUSED, COMPLETE }

    public record Session(Instant start, Instant end, String employeeCode, Status closedBy) {
        public Duration duration(Instant now) {
            return Duration.between(start, end == null ? now : end);
        }
    }

    private final SubStep subStep;
    private final Clock clock;
    private final List<Session> sessions = new ArrayList<>();
    private Status status = Status.NOT_STARTED;
    private String employeeCode;

    public SubStepTimer(SubStep subStep, Clock clock) {
        this.subStep = subStep;
        this.clock = clock;
    }

    public void assign(String employeeCode) {
        if (status == Status.COMPLETE) {
            throw new IllegalStateException(subStep + " is complete; reset it before reassigning");
        }
        this.employeeCode = employeeCode;
    }

    public void start() {
        require(status == Status.NOT_STARTED, "start");
        if (employeeCode == null || employeeCode.isBlank()) {
            throw new IllegalStateException("Assign an employee to " + subStep + " before starting the timer");
        }
        openSession();
    }

    public void pause() {
        require(status == Status.IN_PROGRESS, "pause");
        closeSession(Status.PAUSED);
    }

    public void resume() {
        require(status == Status.PAUSED, "resume");
        openSession();
    }

    public void complete() {
        require(status == Status.IN_PROGRESS || status == Status.PAUSED, "complete");
        if (status == Status.IN_PROGRESS) {
            closeSession(Status.COMPLETE);
        }
        status = Status.COMPLETE;
    }

    /** Clears status, employee and history so a sub-step can be re-run from scratch. */
    public void reset() {
        sessions.clear();
        employeeCode = null;
        status = Status.NOT_STARTED;
    }

    public Duration elapsed() {
        Instant now = clock.instant();
        return sessions.stream().map(s -> s.duration(now)).reduce(Duration.ZERO, Duration::plus);
    }

    public SubStep subStep() {
        return subStep;
    }

    public Status status() {
        return status;
    }

    public String employeeCode() {
        return employeeCode;
    }

    public List<Session> sessions() {
        return Collections.unmodifiableList(sessions);
    }

    private void openSession() {
        sessions.add(new Session(clock.instant(), null, employeeCode, null));
        status = Status.IN_PROGRESS;
    }

    private void closeSession(Status closedBy) {
        int last = sessions.size() - 1;
        Session open = sessions.get(last);
        sessions.set(last, new Session(open.start(), clock.instant(), open.employeeCode(), closedBy));
        status = closedBy;
    }

    private void require(boolean ok, String action) {
        if (!ok) {
            throw new IllegalStateException("Cannot " + action + " " + subStep + " while " + status);
        }
    }
}
