package com.gzs.learn.serial.service.imp;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;

import com.gzs.learn.serial.domain.GroupListenerNode;
import com.gzs.learn.serial.domain.pk.SerialGroupPK;
import com.gzs.learn.serial.service.SerialUpdateService;
import com.gzs.learn.serial.service.ZookeeperNotifyService;
import com.gzs.learn.serial.type.NodeType;
import com.gzs.learn.serial.type.ZookeeperFolder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ZookeeperNotifyServiceImp implements ZookeeperNotifyService {
    private final static String PARTITION_FORMAT = ZookeeperFolder.PARTITION + "/%s,%d,%d";

    /**
     * ZooKeeper客户端
     */
    private CuratorFramework client;

    /**
     *
     */
    private PathChildrenCache groupChildrenCache;

    /**
     *
     */
    private PathChildrenCache partitionChildrenCache;

    /**
     *
     */
    private SerialUpdateService serialUpdateService;

    public ZookeeperNotifyServiceImp(String connect, int timeOut, String path) {
        super();
        this.client = CuratorFrameworkFactory.builder().connectString(connect).namespace(path)
                .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000)).connectionTimeoutMs(timeOut)
                .build();
        this.client.start();
        log.info("Init ZooKeeper Client Success!");
    }

    @Override
    public void init(SerialUpdateService serialUpdateService) throws Exception {
        this.serialUpdateService = serialUpdateService;
        this.groupChildrenCache = new PathChildrenCache(client, ZookeeperFolder.GROUP, true);
        groupChildrenCache.getListenable().addListener(serialGroupListener);
        groupChildrenCache.start(StartMode.NORMAL);
        this.partitionChildrenCache =
                new PathChildrenCache(client, ZookeeperFolder.PARTITION, true);
        partitionChildrenCache.getListenable().addListener(serialPartitionListener);
        partitionChildrenCache.start(StartMode.NORMAL);
    }

    @Override
    public boolean tryLock(String name, int version, int partition) {
        boolean ret = false;
        String path = String.format(PARTITION_FORMAT, name, version, partition);
        try {
            if (this.client.create().withMode(CreateMode.EPHEMERAL).forPath(path) != null) {
                ret = true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ret;
    }

    private PathChildrenCacheListener serialGroupListener = (client, event) -> {
        byte[] bytes = client.getData().forPath(event.getData().getPath());
        SerialGroupPK pk = SerializationUtils.deserialize(bytes);
        switch (event.getType()) {
            case CHILD_ADDED:
                serialUpdateService.addQueue(
                        new GroupListenerNode(pk.getName(), pk.getVersion(), 0L, NodeType.ADD));
                break;
            case CHILD_REMOVED:
                serialUpdateService.addQueue(
                        new GroupListenerNode(pk.getName(), pk.getVersion(), 0L, NodeType.REMOVE));
                break;
            default:
                break;
        }

    };

    private PathChildrenCacheListener serialPartitionListener = (client, event) -> {

    };
}
