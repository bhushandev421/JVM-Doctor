package com.jvm.doctor.rules;

import com.jvm.doctor.model.DiagnosisContext;
import com.jvm.doctor.model.Finding;
import java.util.List;

public interface Rule {
    /**
     * @return Unique rule ID (e.g. THREAD_DEADLOCK)
     */
    String getId();

    /**
     * evaluate the context and return a list of findings (0..*)
     */
    List<Finding> evaluate(DiagnosisContext context);
}
