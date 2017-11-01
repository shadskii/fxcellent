/*
 * Copyright 2017 Jacob Hassel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package freetimelabs.fxcellent.reactor.schedulers;

import javafx.animation.AnimationTimer;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.scheduler.Scheduler.Worker;
import reactor.core.scheduler.Schedulers;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class FxAnimationWorker extends AnimationTimer implements Worker
{
    private Queue<Runnable> queue = new LinkedList<>();
    private long timeSliceNanos;


    FxAnimationWorker(long timeSliceNanos)
    {
        this.timeSliceNanos = timeSliceNanos;
    }

    private long runElapsed(Runnable runnable)
    {
        long start = System.nanoTime();
        runnable.run();
        long end = System.nanoTime();
        return end - start;
    }

    @Override
    public void handle(long now)
    {
        long sliceEndTime = now + timeSliceNanos;
        while (System.nanoTime() < sliceEndTime)
        {
            queue.poll().run();
        }
    }

    @Override
    public Disposable schedule(Runnable task)
    {
        queue.offer(task);
        return Disposables.disposed();
    }

    @Override
    public Disposable schedule(Runnable task, long delay, TimeUnit unit)
    {
        return null;
    }

    @Override
    public Disposable schedulePeriodically(Runnable task, long initialDelay, long period, TimeUnit unit)
    {
        return null;
    }

    @Override
    public void dispose()
    {
        queue.clear();
    }

    @Override
    public boolean isDisposed()
    {
        return queue.isEmpty();
    }
}
