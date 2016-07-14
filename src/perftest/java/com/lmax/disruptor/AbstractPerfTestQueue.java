/*
 * Copyright 2011 LMAX Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lmax.disruptor;


import java.io.PrintStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class AbstractPerfTestQueue
{
    public static final int RUNS = 7;

    protected void testImplementations()
    {
        try {
            String testName = this.getClass().getSimpleName();

            final int availableProcessors = Runtime.getRuntime().availableProcessors();
            if (getRequiredProcessorCount() > availableProcessors) {
                System.out.print(
                        "*** Warning ***: your system has insufficient processors to execute the test efficiently. ");
                System.out.println(
                        "Processors required = " + getRequiredProcessorCount() + " available = " + availableProcessors);
            }

            long[] queueOps = new long[RUNS];

            System.out.println("Starting Queue tests");
            long totalOpsPerSecond = 0;
            for (int i = 0; i < RUNS; i++) {
                System.gc();
                queueOps[i] = runQueuePass();
                totalOpsPerSecond += queueOps[i];
                System.out.format("Run %d, BlockingQueue=%,d ops/sec%n", i, Long.valueOf(queueOps[i]));
            }

            Path totalsPath = AbstractPerfTestDisruptor.getTotalsPath();
            PrintStream totals = AbstractPerfTestDisruptor.getAppendStream(totalsPath);
            totals.format("%s,%s,%d\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), testName, totalOpsPerSecond / RUNS);
            totals.close();
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    public static void printResults(final String className, final long[] disruptorOps, final long[] queueOps)
    {
        for (int i = 0; i < RUNS; i++)
        {
            System.out.format(
                "%s run %d: BlockingQueue=%,d Disruptor=%,d ops/sec\n",
                className, Integer.valueOf(i), Long.valueOf(queueOps[i]), Long.valueOf(disruptorOps[i]));
        }
    }

    protected abstract int getRequiredProcessorCount();

    protected abstract long runQueuePass() throws Exception;
}
