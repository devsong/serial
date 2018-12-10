package com.gzs.learn.serial.service.imp;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

import com.gzs.learn.serial.domain.pk.SerialGroupPK;
import com.gzs.learn.serial.service.ZookeeperNotifyService;
import com.gzs.learn.serial.type.ZookeeperFolder;

public class ZookeeperNotifyServiceImp implements ZookeeperNotifyService {
    private final static Logger logger = LoggerFactory.getLogger(ZookeeperNotifyServiceImp.class);
    private final static String PATH_FORMAT = ZookeeperFolder.GROUP + "/%s_%d";
    /**
     * ZooKeeper客户端
     */
    private CuratorFramework client;

    public ZookeeperNotifyServiceImp(String connect, int timeOut, String path) {
        super();
        this.client = CuratorFrameworkFactory.builder().connectString(connect).namespace(path)
                .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000)).connectionTimeoutMs(timeOut)
                .build();
        this.client.start();
        logger.info("Init ZooKeeper Client Success!");
    }

    @Override
    public boolean ceateNode(SerialGroupPK primaryKey) {
        try {
            String path = String.format(PATH_FORMAT, primaryKey.getName(), primaryKey.getVersion());
            if (this.client.checkExists().forPath(path) == null) {
                this.client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                        .forPath(path, SerializationUtils.serialize(primaryKey));
            }
            return true;
        } catch (Exception e) {
            logger.error("create node error", e);
            return false;
        }
    }

    @Override
    public boolean deleteNode(SerialGroupPK primaryKey) {
        try {
            String path = String.format(PATH_FORMAT, primaryKey.getName(), primaryKey.getVersion());
            if (this.client.checkExists().forPath(path) != null) {
                this.client.delete().deletingChildrenIfNeeded().forPath(path);
            }
            return true;
        } catch (Exception e) {
            logger.error("delete node error", e);
            return false;
        }
    }
}
