package com.arda.flightplanner.service.locker;

import com.arda.flightplanner.rest.FailedToAcquireLockException;
import com.arda.flightplanner.rest.MaxNumberReachedException;
import com.arda.flightplanner.rest.PlaneIsAlreadyOnAFlightException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Service
public class DistributedLocker {
    private static final long RETRY_TIME = 100L;
    private final ValueOperations<String, String> valueOperations;

    public DistributedLocker(RedisTemplate<String, String> redisTemplate) {
        this.valueOperations = redisTemplate.opsForValue();
    }

    public <T> LockExecutionResult<T> lock(String key, int howLongShouldLockBeAcquiredSeconds, int lockTimeoutSeconds, Callable<T> task) {
        try {
            return tryToGetLock(() -> {
                Boolean lockAcquired = valueOperations.setIfAbsent(key, key, lockTimeoutSeconds, TimeUnit.SECONDS);
                if (lockAcquired == Boolean.FALSE) {
                    log.error("Failed to acquire lock for key '{}', because it is already acquired", key);
                    return null;
                }
                log.info("Successfully acquired lock for key '{}'", key);
                try {
                    T taskResult = task.call();
                    return LockExecutionResult.buildLockAcquiredResult(taskResult);
                } catch (MaxNumberReachedException e) {
                    log.error(e.getMessage(), e);
                    throw new MaxNumberReachedException(e.getMessage());
                } catch (PlaneIsAlreadyOnAFlightException e) {
                    log.error(e.getMessage(), e);
                    throw new PlaneIsAlreadyOnAFlightException(e.getMessage());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return LockExecutionResult.buildLockAcquiredWithException(e);
                } finally {
                    releaseLock(key);
                }
            }, key, howLongShouldLockBeAcquiredSeconds);
        } catch (MaxNumberReachedException e) {
            throw new MaxNumberReachedException(e.getMessage());
        } catch (PlaneIsAlreadyOnAFlightException e) {
            log.error(e.getMessage(), e);
            throw new PlaneIsAlreadyOnAFlightException(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return LockExecutionResult.buildLockAcquiredWithException(e);
        }
    }

    private void releaseLock(String key) {
        valueOperations.getOperations().delete(key);
    }

    private static <T> T tryToGetLock(Supplier<T> task, String lockKey, int howLongShouldLockBeAcquiredSeconds) throws Exception {
        long tryToGetLockTimeout = TimeUnit.SECONDS.toMillis(howLongShouldLockBeAcquiredSeconds);
        long startTimestamp = System.currentTimeMillis();
        while (true) {
            log.info("Trying to get the lock with key '{}'", lockKey);
            T response = task.get();
            if (response != null) {
                return response;
            }
            sleep(RETRY_TIME);
            if (System.currentTimeMillis() - startTimestamp > tryToGetLockTimeout) {
                throw new FailedToAcquireLockException("Failed to acquire lock in " + tryToGetLockTimeout + " milliseconds");
            }
        }
    }

    private static void sleep(long sleepTimeMillis) {
        try {
            Thread.sleep(sleepTimeMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(e.getMessage(), e);
        }
    }
}
