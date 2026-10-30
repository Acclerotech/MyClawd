package com.myclawd.scanning.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.myclawd.scanning.domain.SubStepTimer.Status;
import com.myclawd.scanning.domain.SubStepTimer.SubStep;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubStepTimerTest {

    /** A clock the test moves forward by hand. */
    static final class TestClock extends Clock {
        private Instant now = Instant.parse("2026-10-30T09:00:00Z");

        void advance(Duration d) {
            now = now.plus(d);
        }

        @Override
        public Instant instant() {
            return now;
        }

        @Override
        public ZoneOffset getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(java.time.ZoneId zone) {
            return this;
        }
    }

    private TestClock clock;
    private SubStepTimer timer;

    @BeforeEach
    void setUp() {
        clock = new TestClock();
        timer = new SubStepTimer(SubStep.SCAN, clock);
    }

    @Test
    void cannotStartWithoutAnAssignedEmployee() {
        assertThatThrownBy(timer::start).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void eachPauseAndResumeIsItsOwnSession() {
        timer.assign("SCNW2002");
        timer.start();
        clock.advance(Duration.ofMinutes(10));
        timer.pause();
        clock.advance(Duration.ofMinutes(30)); // break: not counted
        timer.resume();
        clock.advance(Duration.ofMinutes(5));
        timer.complete();

        assertThat(timer.status()).isEqualTo(Status.COMPLETE);
        assertThat(timer.sessions()).hasSize(2);
        assertThat(timer.sessions().get(0).closedBy()).isEqualTo(Status.PAUSED);
        assertThat(timer.sessions().get(1).closedBy()).isEqualTo(Status.COMPLETE);
        assertThat(timer.sessions().get(1).start()).isEqualTo(Instant.parse("2026-10-30T09:40:00Z"));
        assertThat(timer.elapsed()).isEqualTo(Duration.ofMinutes(15));
    }

    @Test
    void openSessionCountsTowardsElapsedTime() {
        timer.assign("SCNW2002");
        timer.start();
        clock.advance(Duration.ofMinutes(7));

        assertThat(timer.elapsed()).isEqualTo(Duration.ofMinutes(7));
        assertThat(timer.sessions().get(0).end()).isNull();
    }

    @Test
    void cannotPauseWhenNotRunning() {
        assertThatThrownBy(timer::pause).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void completeIsFinalUntilReset() {
        timer.assign("SCNW2002");
        timer.start();
        timer.complete();

        assertThatThrownBy(timer::resume).isInstanceOf(IllegalStateException.class);

        timer.reset();
        assertThat(timer.status()).isEqualTo(Status.NOT_STARTED);
        assertThat(timer.sessions()).isEmpty();
        assertThat(timer.employeeCode()).isNull();
    }
}
