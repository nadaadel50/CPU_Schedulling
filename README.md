This project simulates various CPU scheduling algorithms to explore their impact on system performance and resource utilization. Developed in Java, it supports both traditional and innovative approaches to scheduling, providing detailed outputs for analysis.

Features
Non-Preemptive Priority Scheduling:

Schedules processes based on their priority. Higher-priority processes are executed first, with context switching between processes.

Non-Preemptive Shortest Job First (SJF):

Selects the process with the shortest burst time for execution. This method prevents starvation by ensuring that processes with longer burst times are not indefinitely delayed.

Shortest Remaining Time First (SRTF):

Dynamically handles process execution by selecting the process with the shortest remaining burst time. This method effectively prevents starvation by preemptively adjusting to the shortest burst time.

FCAI Scheduling Algorithm:

A dynamic scheduling approach that combines priority, arrival time, and remaining burst time into a composite scheduling factor.

Features dynamic quantum adjustments that optimize efficiency and fairness in process execution.

Supports both preemptive and non-preemptive execution phases, providing more accurate simulation of real-world CPU scheduling scenarios.

