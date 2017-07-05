package sc.learn.common.thrift;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.learn.common.util.DateTimeUtil;

public class ThriftServiceStatus {

    private static Logger serviceStatusLoger = LoggerFactory.getLogger("serviceStatusLoger");
    private static Logger apiSwitchMonitor = LoggerFactory.getLogger("apiSwitchMonitorLog");

    private static final int INTERFACE_TOTAL_COUNT = 30;// 服务访问次数达到该值进行切服计算
    private static final long INTERFACE_RECORD_COUNT_RESET_TIME = 1 * 60 * 1000;// 服务统计量重置时间间隔
    private static final long INTERFACE_AUTO_NORMAL_TIME = 5 * 60 * 1000;// 接口自动恢复时间5分钟

    /**
     * 第三方服务异常次数
     */
    private int count;

    /**
     * 第三方服务开始计数时间
     */
    private long recordStartTime;

    /**
     * 第三方服务最近一次关闭时间
     */
    private long closeTime;

    /**
     * 第三方服务名称
     */
    private String serviceName;

    private Lock lock;

    public ThriftServiceStatus(String serviceName) {
        this.recordStartTime = System.currentTimeMillis();
        this.count = 0;
        this.closeTime = 0;
        this.lock = new ReentrantLock();
        this.serviceName = serviceName;
    }

    /**
     * 在切服时间内，服务处于不可用状态
     * @return
     */
    public boolean ifServiceUsable() {
        return (System.currentTimeMillis() - this.closeTime) > INTERFACE_AUTO_NORMAL_TIME;
    }

    public void checkThriftServiceStatus() {
        this.lock.lock();
        try {
            this.count++;
            serviceStatusLoger.info("[this service " + this.serviceName + "]" + "  has exceptions. count:["
                    + this.count + "] recordStartTime:[" + DateTimeUtil.formatCommon(recordStartTime) + "] nowTime:["
                    + DateTimeUtil.formatCommon(System.currentTimeMillis()) + "]");
            // 服务异常次数 超过统计阀值时 进行 切服
            if (this.count >= INTERFACE_TOTAL_COUNT) {
                this.closeTime = System.currentTimeMillis();// 更新关闭时间
                apiSwitchMonitor.info("[" + this.serviceName + "]" + " close time:["
                        + DateTimeUtil.formatCommon(this.closeTime) + "]" + "average response time:[10000]");
            }
            // 1分钟后 重置计数
            if ((System.currentTimeMillis() - recordStartTime) > INTERFACE_RECORD_COUNT_RESET_TIME) {
                this.count = 0;
                this.recordStartTime = System.currentTimeMillis();
            }
        } finally {
            this.lock.unlock();
        }
    }

}