package com.gzs.learn.serial.domain;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.gzs.learn.serial.type.NodeType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SerialNode {
    /**
     * 当前节点主键
     */
    private String key;

    /**
     * 当前节点版本
     */
    private int version;

    /**
     * 当前更新时间戳
     */
    private long tsup;

    /**
     * 当前使用位置
     */
    private long position;

    /**
     * 当前最大使用值
     */
    private long max;

    /**
     * 步长
     */
    private int step;

    /**
     * 增容阈值，当为本partition最后一个节点时与max相等
     */
    private long limit;

    /**
     * 锁
     */
    private Lock lock = new ReentrantLock();

    /**
     * 填充通知开关
     */
    private boolean notifySwitch;

    /**
     * 特殊通知开关
     */
    private boolean specialSwitch;

    /**
     * 释放统计
     */
    private int release;

    /**
     * 填充通知
     */
    private LinkedBlockingQueue<GroupListenerNode> notify;

    /**
     * @param key
     * @param version
     * @param tsup
     * @param position
     * @param max
     * @param limit
     * @param notify
     */
    public SerialNode(String key, int version, long tsup, long position, long max, int step,
            long limit, LinkedBlockingQueue<GroupListenerNode> notify) {
        super();
        this.key = key;
        this.version = version;
        this.tsup = tsup;
        this.position = position;
        this.max = max;
        this.step = step;
        this.limit = limit;
        this.notify = notify;
        this.notifySwitch = true;
        this.specialSwitch = false;
        this.release = 0;
    }

    /**
     * @param length
     * @return
     */
    public long getSerial(int length) {
        long ret = 0L;
        if (length < 0 || length > this.step) {
            throw new IllegalArgumentException(
                    String.format("Serial length support [1~%d].", this.step));
        }

        try {
            if (this.lock.tryLock() || this.lock.tryLock(10, TimeUnit.MILLISECONDS)) {
                if (position + length <= max) {
                    ret = position;
                    position += length;
                } else if (this.notifySwitch) {
                    this.specialSwitch = true;
                    this.notify.offer(new GroupListenerNode(this.key, this.version, this.tsup,
                            NodeType.RECHARGE));
                } else {
                    this.release++;
                }
                if (position >= limit && this.notifySwitch) {
                    this.notify.offer(new GroupListenerNode(this.key, this.version, this.tsup,
                            NodeType.RECHARGE));
                }
                if (position == max || this.release > 99) {
                    this.notify.offer(new GroupListenerNode(this.key, this.version, this.tsup,
                            NodeType.REMOVE));
                }
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        } finally {
            this.lock.unlock();
        }
        return ret;
    }

    /**
     * @return
     */
    public boolean checkRemainder() {
        return this.limit - this.position <= 0 || this.specialSwitch;
    }

    /**
     * @return
     */
    public boolean checkEnd() {
        return this.limit == this.max;
    }

    /**
     * @param max
     * @param limit
     */
    public void addSerial(long max, long limit) {
        try {
            this.lock.lock();
            this.max = max;
            this.limit = limit;
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * @param key
     * @param version
     * @return
     */
    public boolean equal(String key, int version) {
        if (this.key.equals(key) && this.version == version) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param key
     * @param version
     * @param tsup
     * @return
     */
    public boolean equal(String key, int version, long tsup) {
        if (this.key.equals(key) && this.version == version && this.tsup == tsup) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return the max
     */
    public long getMax() {
        return max;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * @return the tsup
     */
    public long getTsup() {
        return tsup;
    }

    public void finishRecharge() {
        this.notifySwitch = false;
    }
}
