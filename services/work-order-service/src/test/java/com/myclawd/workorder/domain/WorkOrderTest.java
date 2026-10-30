package com.myclawd.workorder.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class WorkOrderTest {

    private WorkOrder newWorkOrder() {
        return new WorkOrder("WO-001", 1L, 2L, 10L, 20L);
    }

    @Test
    void startsAtStepOne() {
        assertThat(newWorkOrder().getCurrentStep()).isEqualTo(1);
    }

    @Test
    void advanceCompletesCurrentStepAndMovesOn() {
        WorkOrder wo = newWorkOrder();

        int completed = wo.advanceStep();

        assertThat(completed).isEqualTo(1);
        assertThat(wo.getCurrentStep()).isEqualTo(2);
    }

    @Test
    void reachesFinalStepAfterSixAdvances() {
        WorkOrder wo = newWorkOrder();
        for (int i = 0; i < 6; i++) {
            wo.advanceStep();
        }
        assertThat(wo.getCurrentStep()).isEqualTo(WorkOrder.FINAL_STEP);
        assertThat(wo.isComplete()).isTrue();
    }

    @Test
    void cannotAdvancePastFinalStep() {
        WorkOrder wo = newWorkOrder();
        for (int i = 0; i < 6; i++) {
            wo.advanceStep();
        }
        assertThatThrownBy(wo::advanceStep).isInstanceOf(IllegalStateException.class);
    }
}
